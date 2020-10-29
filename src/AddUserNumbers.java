import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.*;
import java.util.Arrays;

@WebServlet("/AddUserNumbers")
public class AddUserNumbers extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            // get the session from Servlet
            HttpSession session = request.getSession();

            //get the hashed password
            String pwd = (String) session.getAttribute("hashed password");

            //create a single String from the number submitted by the user
            String usernumber = request.getParameter("usernumber");

            // create the KeyPair for encryption
            KeyPair pair;

            // get an instance of the KeyPairGenerator
            KeyPairGenerator keyPairGen = null;
            try {
                keyPairGen = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            //generate a new keypair and assign it to our pair variable
            assert keyPairGen != null;
            pair = keyPairGen.generateKeyPair();

            // set the KeyPair as attributes of the session
            session.setAttribute("keypair", pair);

            // get the encrypted string
            String enString = encryptData(usernumber, pair);
            System.out.println(enString);

            //write the String to file
            //create file name from first 20 characters of the hashed password
            String filename = pwd.substring(0, 20);
            System.out.println(filename);
            writeToFile(filename, enString);
            System.out.println("plik poszedl");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            request.setAttribute("message", "File name: " + filename);
            dispatcher.forward(request, response);
        }
        catch(Exception se){
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "no i znowu sie zjebalo");
            dispatcher.forward(request, response);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    // create a function to decrypt data
    public String encryptData(String usernumber, KeyPair pair) {
        try{
            //use a public key to encrypt the string with numbers
            PublicKey publicKey = pair.getPublic();

            // create a cipher object
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            //set cipher to encyption mode and pass it the publickey defined earlier
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            //convert the input String to a byte[] to be able to pass it to cipher
            cipher.update(usernumber.getBytes());

            //finally: encrypt the String using cipher
            return Arrays.toString(cipher.doFinal());

        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex){
            ex.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeToFile(String filename, String encrypted){
        try{
            // add true if append, not overwrite
            FileWriter plswrite = new FileWriter("D:\\Users\\Kirai\\CSC2031 Coursework\\LotteryWebApp\\Created Files\\" + filename);
            plswrite.write(encrypted);
            plswrite.flush();
            plswrite.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
