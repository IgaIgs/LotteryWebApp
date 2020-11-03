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
import java.nio.charset.StandardCharsets;
import java.security.*;

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

            // if there isn't any keypair already generated in this session:
            if (session.getAttribute("keypair") == null){

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
            }
            else{ //if there is already a keypair in use in this session
                pair = (KeyPair) session.getAttribute("keypair");
            }

            // set the KeyPair as attributes of the session
            session.setAttribute("keypair", pair);

            // get the encrypted string
            String enString = encryptData(usernumber, pair);

            //create file name from first 20 characters of the hashed password
            String filename = pwd.substring(0, 20) + ".txt";
            //write the String to file
            writeToFile(filename, enString);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            request.setAttribute("message", "Press the 'Get Draws' button to display you draws!");
            dispatcher.forward(request, response);
        }
        catch(Exception se){
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Something went wrong");
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

            //encrypt and convert bytes to hex string
            return bytesToHex(cipher.doFinal());


        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void writeToFile(String filename, String encrypted){
        try{
            // add true if append, not overwrite
            FileWriter plswrite = new FileWriter("D:\\Users\\Kirai\\CSC2031 Coursework\\LotteryWebApp\\Created Files\\" + filename, StandardCharsets.UTF_8, true);
            plswrite.write(encrypted + System.lineSeparator()); //write the encrypted string with a new line after it so it's easier to split them later
            plswrite.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
