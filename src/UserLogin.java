import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet used to process the user's login
 * checks if the username is already in the database and logs them in if the username x pwd x role combination is correct
 * keeps count of failed login attempts and disables the form after 3 failed attempts
 * After successful login, takes the admin or public users to their respective pages
 */
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {

    private Connection conn;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "passcode";

        // URLs to connect to database depending on your development approach
        // (NOTE: please change to option 1 when submitting)

        // 1. use this when running everything in Docker using docker-compose
        //String DB_URL = "jdbc:mysql://db:3306/lottery";

        // 2. use this when running tomcat server locally on your machine and mysql database server in Docker
        String DB_URL = "jdbc:mysql://localhost:33333/lottery";

        // 3. use this when running tomcat and mysql database servers on your machine
        //String DB_URL = "jdbc:mysql://localhost:3306/lottery";

        // get parameters from the log In form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // get the current session from Servlet
        HttpSession session = request.getSession();

        /// crate a login failed boolean indicator
        boolean loginFailed = true;

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //get salts from all users with the same username
            PreparedStatement getsalt = conn.prepareStatement("SELECT Salt FROM userAccounts WHERE Username = ?");
            getsalt.setString(1, username);
            ResultSet salts = getsalt.executeQuery();

            // hash the password of the current user (attempting login) with each of the salts from the query above
            // store all the hashed password variations in a List
            List<String> hashes = new ArrayList<>();

            // for each salt (so in case this username exists in the database)
            while(salts.next()){
                //add it to the hashed password and store the complete hashed password in the hashes array
                hashes.add(CreateAccount.hash_pwd(password, salts.getBytes("Salt")));
            }

            //this will get omitted if the username, thus salts, don't exist in the database yet - go down for failed login handling

            // for every variation of the hashed password
            for (String hash : hashes) {

                //prepare a check whether this combination of username x hashed password x role is already in the database
                PreparedStatement check = conn.prepareStatement("SELECT * FROM userAccounts WHERE Username = ? AND Pwd = ? AND Userrole = ?");
                check.setString(1, username);
                check.setString(2, hash);
                check.setString(3, role);
                ResultSet rscheck = check.executeQuery();

                if (rscheck.next()) { //if this account is already in the database

                        // set the user data as attributes of the session
                        session.setAttribute("first name", rscheck.getString("Firstname"));
                        session.setAttribute("last name", rscheck.getString("Lastname"));
                        session.setAttribute("email", rscheck.getString("Email"));
                        session.setAttribute("phone number", rscheck.getString("Phone"));
                        session.setAttribute("username", rscheck.getString("Username"));
                        session.setAttribute("role", rscheck.getString("Userrole"));
                        session.setAttribute("hashed password", rscheck.getString("Pwd"));

                        // set the login count for this session as null cuz the user logged correctly
                        session.setAttribute("loginsLeft", null);

                    // display account.jsp page with given message if successful and the user is public
                    RequestDispatcher dispatcher;
                    if (session.getAttribute("role").equals("public")){
                        dispatcher = request.getRequestDispatcher("/account.jsp");
                        request.setAttribute("message", "Login successful!");
                        dispatcher.forward(request, response);
                    }
                    //display the admin page if successful and user is admin
                    else if (session.getAttribute("role").equals("admin")){
                        dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
                        request.setAttribute("message", "Login successful!");
                        dispatcher.forward(request, response);
                    }

                    //close the connection
                    conn.close();

                    //set the failed login indicator to false cuz the login was successful
                    loginFailed = false;


                } else {
                    //this username x password x role doesn't exist so break out to access the failed login handling
                    break; }

                break;
            }

            if (loginFailed){
                // joint functionality to handle unsuccessful user logins when
                // either the username doesn't exist or the username x password x role combination doesn't exist
                // and with a limit of 3 failed login attempts

                int loginsLeft;

                // find out how many login attempts are left

                // if this is the user's FIRST login attempt
                if (session.getAttribute("loginsLeft") == null){
                    session.setAttribute("loginsLeft", 3); // create a loginsleft session attribute with value 3
                    loginsLeft = 3; //still 3 login chances left

                }
                else{ // if this user HAS already attempted logins

                    //find out how many attempts they have left
                    loginsLeft = (Integer) session.getAttribute("loginsLeft");
                }

                // the action happens
                if (loginsLeft <= 1){ //if the user has no more attempts

                    RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
                    // to inform the index.jsp page to disable the form
                    request.setAttribute("loginsattempted", 3);
                    dispatcher.forward(request, response);

                    // reset the loginsleft count to 3 so the user can try again after they refresh the page and enable the form again
                    loginsLeft = 3;
                }
                else{
                    //if they failed the login but they still have more attempts
                    loginsLeft --;
                    // display error.jsp page with given message if unsuccessful
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                    request.setAttribute("message", "Login unsuccessful! You have " + loginsLeft + " attempts left!");
                    request.setAttribute("message2", "To try again, click <a href=\"index.jsp\">here</a>! :)");
                    dispatcher.forward(request, response);
                }

                // update the loginsleft tracker
                session.setAttribute("loginsLeft", loginsLeft);

                // close connection
                conn.close();

            }

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
