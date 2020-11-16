import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

/**
 * This servlet is used to:
 * retrieve the data entered by the user to the registration form
 * compare it against the data in the database - if this username doesn't yet exist -->
 * allow the user to create an account and save their data in the database
 * assign the user's information to session attributes
 */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {

    private Connection conn;
    private PreparedStatement stmt;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // MySql database connection info
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

        // get parameter data that was submitted in HTML form
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String role = request.getParameter("role");
        String password = request.getParameter("password");

        // get the session from Servlet
        HttpSession session = request.getSession();

        try{
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //check if the username entered is already in the database
            PreparedStatement checkusers = conn.prepareStatement("SELECT Username FROM userAccounts WHERE Username = ?");
            checkusers.setString(1, username);
            ResultSet rschecked = checkusers.executeQuery();

            //if this user is NOT yet in the database, allow to create an account
            if (!(rschecked.next())) {

                // Create sql query to insert the user's data to the database
                String query = "INSERT INTO userAccounts (Firstname, Lastname, Email, Phone, Username, Userrole, Pwd, Salt)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                byte[] salt = getSalt(); //generate salt for this user (for password hashing)
                String hashedpwd = hash_pwd(password, salt); //hash this user's password

                // set values into SQL query statement
                stmt = conn.prepareStatement(query);
                stmt.setString(1, firstname);
                stmt.setString(2, lastname);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, username);
                stmt.setString(6, role);
                stmt.setString(7, hashedpwd);
                stmt.setBytes(8, salt);

                // execute query and close connection
                stmt.execute();
                conn.close();

                // set the user data as attributes of the session
                session.setAttribute("first name", firstname);
                session.setAttribute("last name", lastname);
                session.setAttribute("email", email);
                session.setAttribute("phone number", phone);
                session.setAttribute("username", username);
                session.setAttribute("hashed password", hashedpwd);
                session.setAttribute("salt", salt);

                // stay on index.jsp with instruction to log in
                RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
                request.setAttribute("message", "You have successfully created an account! Please log in :)");
                dispatcher.forward(request, response);

            }
            else{
                // display error.jsp page with correct message for when the username entered by the user is already in the database
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                request.setAttribute("message", firstname+", this username already exists. Please try again");
                dispatcher.forward(request, response);
            }


        } catch(Exception se){
            se.printStackTrace();
            // display error.jsp page with correct message for when entered username x password combination already exists
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", firstname+", this username/password combination already exists. Please try again");
            dispatcher.forward(request, response);
        }
        finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }
            catch(SQLException se2){
                se2.printStackTrace();
            }
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * method to hash the password with advanced hashing algorithm called PBKDF2WithHmacSHA1
     * has to be public so that the it can be also accessed within the UserLogin.java servlet
     * @param pwd - password String to be hashed
     * @return - hashed password String
     * @throws NoSuchAlgorithmException - in case the requested algo is not available in the environment
     * @throws InvalidKeySpecException - when the key specification is invalid
     */
    public static String hash_pwd(String pwd, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // if the password is empty
        if (pwd == null || pwd.length() == 0){
            throw new IllegalArgumentException("Empty passwords are not supported.");}

        // instantiate the SecretKeyFactory using the above algo
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        // generate the hash with the use of the salt generated in the method below
        SecretKey key = f.generateSecret(new PBEKeySpec(pwd.toCharArray(), salt, 20000, 64*8));

        // encode the hash
        byte[] hash = key.getEncoded();

        //return the hashed password in a form of a hex string
        return hex(hash);
    }

    /**
     * create salt to be used in the password hashing process
     * @return - salt (byte[])
     * @throws NoSuchAlgorithmException - in case the requested algo is not available in the environment
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException{
        return SecureRandom.getInstance("SHA1PRNG").generateSeed(32);
    }

    /**
     * simple hex function to be used on the hashed password
     * @param b - byte array to be converted to a hex string
     * @return - hex string
     */
    public static String hex(byte[] b) {
        return String.format("%040x", new BigInteger(1, b));
    }
}
