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
import java.util.Arrays;


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

        // get parameter data that was submitted in HTML form (use form attributes 'name')
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // get the session from Servlet
        HttpSession session = request.getSession();


        try{
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);


            // Create sql query
            String query = "INSERT INTO userAccounts (Firstname, Lastname, Email, Phone, Username, Pwd, Salt)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?)";

            byte[] salt = getSalt(); //get salt
            System.out.println("nowe salt: " + Arrays.toString(salt));
            String hashedpwd = hash_pwd(password, salt); //get a hashed password

            // set values into SQL query statement
            stmt = conn.prepareStatement(query);
            stmt.setString(1,firstname);
            stmt.setString(2,lastname);
            stmt.setString(3,email);
            stmt.setString(4,phone);
            stmt.setString(5,username);
            stmt.setString(6,hashedpwd);
            stmt.setBytes(7, salt);


            // execute query and close connection
            stmt.execute();
            conn.close();

            // set the user data as attributes of the session
            session.setAttribute("first name", firstname);
            System.out.println(firstname);
            session.setAttribute("last name", lastname);
            session.setAttribute("username", username);
            session.setAttribute("email", email);
            session.setAttribute("phone number", phone);
            session.setAttribute("hashed password", hashedpwd);
            session.setAttribute("salt", salt);

            // display account.jsp page with given message if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            request.setAttribute("message", firstname+", you have successfully created an account");
            dispatcher.forward(request, response);


            // TODO check his error handling and improve yours

        } catch(Exception se){
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", firstname+", this username/password combination already exists. Please try again");
            dispatcher.forward(request, response);
        }
        finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }
            catch(SQLException se2){}
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
     * hash the password with advanced hashing algorithm called PBKDF2WithHmacSHA1
     * @param pwd - password to be hashed
     * @return - hashed password
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public String hash_pwd(String pwd, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (pwd == null || pwd.length() == 0){
            throw new IllegalArgumentException("Empty passwords are not supported.");}

        // instantiate the SecretKeyFactory using the above algo
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // generate our hash
        SecretKey key = f.generateSecret(new PBEKeySpec(pwd.toCharArray(), salt, 20000, 64*8));
        // encode the hash
        byte[] hash = key.getEncoded();
        //get the pwd
        System.out.println(hex(hash));
        return hex(hash);
    }

    /**
     * create salt to hash the password
     * @return - salt
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getSalt() throws NoSuchAlgorithmException{
        return SecureRandom.getInstance("SHA1PRNG").generateSeed(32);
    }

    public static String hex(byte[] b) {
        return String.format("%040x", new BigInteger(1, b));
    }
}
