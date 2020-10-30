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

        // get the current session from Servlet
        HttpSession session = request.getSession();

        //remove all pre-existing session attributes
        Enumeration<String> attributes =  session.getAttributeNames();
        while (attributes.hasMoreElements()){
            session.removeAttribute(attributes.nextElement());
        }
        //invalidate the current session
        session.invalidate();

        // get the session again
        session = request.getSession();


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

                //prepare a check whether this combination of username and hashed password is already in the database
                PreparedStatement check = conn.prepareStatement("SELECT * FROM userAccounts WHERE Username = ? AND Pwd = ?");
                check.setString(1, username);
                check.setString(2, hash);
                ResultSet rscheck = check.executeQuery();
                System.out.println("sprawdzilam czy password jest w bazie");

                if (rscheck.next()) { //if this account is already in the database

                        // set the user data as attributes of the session
                        session.setAttribute("first name", rscheck.getString("Firstname"));
                        System.out.println(rscheck.getString("Firstname"));
                        session.setAttribute("last name", rscheck.getString("Lastname"));
                        session.setAttribute("username", username); //from the log in form
                        session.setAttribute("email", rscheck.getString("Email"));
                        session.setAttribute("phone number", rscheck.getString("Phone"));
                        session.setAttribute("hashed password", rscheck.getString("Pwd"));

                    // display account.jsp page with given message if successful
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                    request.setAttribute("message", "Login successful!");
                    dispatcher.forward(request, response);

                    conn.close();
                } else {
                    // display error.jsp page with given message if unsuccessful
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                    request.setAttribute("message", "Login unsuccessful!");
                    dispatcher.forward(request, response);

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
