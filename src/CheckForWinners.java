import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

/**
 * This servlet is used to:
 * create a table to store winning draws if this table doesn't exist yet
 * add the custom winning draw to the table
 * retrieve the winning draw from the database and passing it to the account.jsp page
 * finding and deleting the current user's lottery file once they checked for a win
 */
@WebServlet("/CheckForWinners")
public class CheckForWinners extends HttpServlet {

    private Connection conn;
    private Statement stmt, stmt2;

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
            stmt2 = conn.createStatement();

            DatabaseMetaData meta = conn.getMetaData();
            // check if "winningDraws" table exists
            ResultSet table = meta.getTables(null, null, "winningDraws", null);

            // if the table doesn't exist yet
            if (!(table.next())) {
                // create it
                String sql = "CREATE TABLE winningDraws (" +
                        "Winningdraw VARCHAR(50) NOT NULL," +
                        "PRIMARY KEY (WinningDraw))";

                stmt2.executeUpdate(sql);
            }

            //empty the database with the winning draws before adding a new one (for when the table existed before)
            PreparedStatement trnct = conn.prepareStatement("TRUNCATE TABLE winningDraws");
            trnct.execute();

            // manually add a winning draw to the database to check against user's draws
            String query = "INSERT INTO winningDraws"
                    + " VALUES (?)";
            PreparedStatement adddraw = conn.prepareStatement(query);
            adddraw.setString(1, "00,11,22,33,44,55");
            adddraw.execute();

            // query database and get the winning lottery draw
            ResultSet rs = stmt.executeQuery("SELECT Winningdraw FROM winningDraws");

            if (rs.next()){
                // display account.jsp page with the winner draw added to request object and a message with instruction for user
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("winningdraw", rs.getString("Winningdraw"));
                dispatcher.forward(request, response);
            }

            //get the session
            HttpSession session = request.getSession();

            //get the hashed password
            String pwd = (String) session.getAttribute("hashed password");

            //get this user's filename
            String filename = pwd.substring(0, 20) + ".txt";

            //delete this user's file after they checked for wins
            try{
                Files.deleteIfExists(Path.of("./Created Files/" + filename));
            } catch(IOException e)
            {
                e.printStackTrace();
            }

            // close the connection
            conn.close();


        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (stmt2 != null)
                    stmt2.close();
            } catch (SQLException se3) {
                se3.printStackTrace();
            }
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
