import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * This servlet is used to retrieve all users' data from the database and display it in a form of a table
 * on the admin page
 */
@WebServlet("/GetUserData")
public class GetUserData extends HttpServlet {
    private Connection conn;
    private Statement stmt;

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

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            // query database for all users' data and get resultset
            ResultSet rs = stmt.executeQuery("SELECT Firstname, Lastname, Email, Phone, Username, Userrole FROM userAccounts");

            // create HTML table text
            StringBuilder content = new StringBuilder("<table border='1' tablepadding='2' cellspacing='2' cellpadding='2' width='100%' align='left'>" +
                    "<tr><th>First name</th><th>Last name</th><th>Email</th><th>Phone number</th><th>Username</th><th>User role</th></tr>");

            // add HTML table data using data from database
            while (rs.next()) {
                content.append("<tr><td>").append(rs.getString("Firstname")).append("</td>").append("<td>")
                        .append(rs.getString("Lastname")).append("</td>").append("<td>")
                        .append(rs.getString("Email")).append("</td>").append("<td>")
                        .append(rs.getString("Phone")).append("</td>").append("<td>")
                        .append(rs.getString("Username")).append("</td>").append("<td>")
                        .append(rs.getString("Userrole")).append("</td></tr>");
            }
            // finish HTML table text
            content.append("</table>");

            // close connection
            conn.close();

            // display admin_home.jsp page with given content above if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
            request.setAttribute("message", "User data:");
            request.setAttribute("data", content.toString());
            dispatcher.forward(request, response);


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
                se2.printStackTrace();
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
