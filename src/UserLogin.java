import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {

    private Connection conn;
    private Statement stmt;

    //get the CreateAccount class to use its hashing methods
    CreateAccount acc = new CreateAccount();

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

        //if this user has already used the website and files were already created for him
        if ((session.getAttribute("username") != null) && (session.getAttribute("username").equals(username))){
            //get the hashed password
            String pwd = (String) session.getAttribute("hashed password");

            //get this users filename
            String filename = pwd.substring(0, 20) + ".txt";

            // make the file blank so that the next time the user logs in and picks his lottery numbers,
            // there are no problems with encryption and decryption
            FileWriter plswrite = new FileWriter("D:\\Users\\Kirai\\CSC2031 Coursework\\LotteryWebApp\\Created Files\\" + filename, StandardCharsets.UTF_8);
            plswrite.write("");
            plswrite.close();
        }

        //get the log in attempts from the current session
        Integer loginsSoFar = (Integer) session.getAttribute("loginsLeft");

        //remove all pre-existing session attributes
        Enumeration<String> attributes =  session.getAttributeNames();
        while (attributes.hasMoreElements()){
            session.removeAttribute(attributes.nextElement());
        }
        //invalidate the current session
        session.invalidate();

        // get the session again
        session = request.getSession();

        //assign the logins again
        session.setAttribute("loginsLeft", loginsSoFar);
        System.out.println("ile mam loginow halo: " + loginsSoFar);

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //get salts from all users with the same username
            PreparedStatement getsalt = conn.prepareStatement("SELECT Salt FROM userAccounts WHERE Username = ?");
            getsalt.setString(1, username);
            ResultSet salts = getsalt.executeQuery(); //my salts here

            //hash the password of the current user (attempting login) with each of the salts from the query above
            //list to store variations of the hashed password
            List<String> hashes = new ArrayList<>();
            // for each salt,
            while(salts.next()){
                //add it to the hashed password and store the complete hashed password in the hashes array
                hashes.add(acc.hash_pwd(password, salts.getBytes("Salt")));
            }
            System.out.println("zhashowalam passwordy");

            System.out.println(hashes.size());

            // for every variation of the hashed password
            for (String hash : hashes) {

                //prepare a check whether this combination of username and hashed password and role is already in the database
                PreparedStatement check = conn.prepareStatement("SELECT * FROM userAccounts WHERE Username = ? AND Pwd = ? AND Userrole = ?");
                check.setString(1, username);
                check.setString(2, hash);
                check.setString(3, role);
                ResultSet rscheck = check.executeQuery();
                System.out.println("sprawdzilam czy password jest w bazie");

                if (rscheck.next()) { //if this account is already in the database

                    System.out.println("log in successful");

                        // set the user data as attributes of the session
                        session.setAttribute("first name", rscheck.getString("Firstname"));
                        System.out.println(rscheck.getString("Firstname"));
                        session.setAttribute("last name", rscheck.getString("Lastname"));
                        session.setAttribute("email", rscheck.getString("Email"));
                        session.setAttribute("phone number", rscheck.getString("Phone"));
                        session.setAttribute("username", rscheck.getString("Username")); //from the log in form
                        session.setAttribute("role", rscheck.getString("Userrole")); //from the log in form
                        session.setAttribute("hashed password", rscheck.getString("Pwd"));

                        // set the login count for this session as null if another user logs in correctly
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
                    conn.close();

                } else {// functionality to limit unsuccessful user logins to 3

                    System.out.println("login unsuccessful so work on the attempts");
                    int loginsLeft;

                    // find out how many login attempts are left
                    if (session.getAttribute("loginsLeft") == null){ // if this is the user's FIRST login attempt

                        session.setAttribute("loginsLeft", 3); // create an attempt session attribute with value 3
                        loginsLeft = 3; //still 3 login chances left
                        System.out.println("no attempts yet");

                    }
                    else{ // if this user HAS already attempted logins
                        loginsLeft = (Integer) session.getAttribute("loginsLeft"); //find out how many attempts they have left
                        System.out.println("some attempts already");
                    }

                    // the action happens
                    if (loginsLeft <= 1){ //if the user has no more attempts
                        System.out.println("action for zero attempts");
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
                        request.setAttribute("logins", 3); // to inform the index.jsp page to disable the form
                        dispatcher.forward(request, response);
                    }
                    else{
                        System.out.println("action when attempts left");
                        //if they failed the login but they still have more attempts
                        loginsLeft --;
                        // display error.jsp page with given message if unsuccessful
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                        request.setAttribute("message", "Login unsuccessful! You have " + loginsLeft + " attempts left!");
                        request.setAttribute("message2", "To try again, click <a href=\"index.jsp\">here</a>! :)");
                        dispatcher.forward(request, response);
                    }

                    session.setAttribute("loginsLeft", loginsLeft);

                    System.out.println("assigned logins to session");

                    // close connection
                    conn.close();
                }

            }

            /*stmt = conn.createStatement();

            // query database and get results
            ResultSet rs = stmt.executeQuery("SELECT * FROM userAccounts ");

            // create HTML table text
            String content = "<table border='1' cellspacing='2' cellpadding='2' width='100%' align='left'>" +
                    "<tr><th>First name</th><th>Last name</th><th>Email</th><th>Phone number</th><th>Username</th><th>Password</th></tr>";

            // add HTML table data using data from database
            while (rs.next()) {
                content += "<tr><td>"+ rs.getString("Firstname") + "</td>" +
                        "<td>" + rs.getString("Lastname") + "</td>" +
                        "<td>" + rs.getString("Email") + "</td>" +
                        "<td>" + rs.getString("Phone") + "</td>" +
                        "<td>" + rs.getString("Username") + "</td>" +
                        "<td>" + rs.getString("Pwd") + "</td></tr>";
            }
            // finish HTML table text
            content += "</table>";

            // close connection
            conn.close();

            // display output.jsp page with given content above if successful
                RequestDispatcher dispatcher = request.getRequestDispatcher("/output.jsp");
                request.setAttribute("data", content);
                dispatcher.forward(request, response);*/

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
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
