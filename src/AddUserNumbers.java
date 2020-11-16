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

/**
 * This servlet is used to retrieve the lottery draws submitted by the user, encrypt them and save them to a text file
 * with filename from first 20 characters of the user's hashed password.
 * The text file is created inside a 'Created Files' directory inside the default tomcat bin folder.
 * Once that's done, appropriate messages are shown to the user on the account.jsp page
 */
@WebServlet("/AddUserNumbers")
public class AddUserNumbers extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            // get the current session from Servlet
            HttpSession session = request.getSession();

            //get the user's hashed password
            String pwd = (String) session.getAttribute("hashed password");

            //create a single String from the 6 numbers submitted by the user, separated by commas
            StringBuilder usernumber = new StringBuilder();

            for (int i =1; i <=6; i++){
                if (i == 1){
                    usernumber.append(request.getParameter("userno1")); //add the integer from the first text box
                }
                else{
                    // add integers from subsequent text boxes with commas in front of each
                    usernumber.append(",").append(request.getParameter("userno" + i));
                }
            }

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
            else{
                //if there is already a keypair in use in this session
                pair = (KeyPair) session.getAttribute("keypair");
            }

            // set the KeyPair as an attribute of the session
            session.setAttribute("keypair", pair);

            // encrypt the string containing user's draws
            String enString = encryptData(usernumber.toString(), pair);

            //create file name from first 20 characters of the hashed password
            String filename = pwd.substring(0, 20) + ".txt";

            //write the encrypted string to the file
            writeToFile(filename, enString);

            // redirect to the account page with info about the draws being added
            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            request.setAttribute("message", "Draws added successfully!<br>" + "Press the 'Get Draws' button to display you draws!");
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

    /**
     * Method for encrypting Strings using the RSA/ECB/PKCS1Padding algorithm
     * @param usernumber - String containing user's lottery draws
     * @param pair - encryption keypair
     * @return - encrypted hex string
     */
    public String encryptData(String usernumber, KeyPair pair) {
        try{
            //use a public key to encrypt the string with draws
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

    /**
     * method for writing strings to a file
     * @param filename - the name of the file that's going to be edited
     * @param encrypted - encrypted string to be written to the file
     */
    private static void writeToFile(String filename, String encrypted){
        try{
            // create the directory to store the created files
            File dir = new File("Created Files");
            dir.mkdir();
            // create a filewriter for the chosen file
            FileWriter plswrite = new FileWriter(dir +"\\" + filename, StandardCharsets.UTF_8, true);
            //write the encrypted string with a new line after it so it's easier to split them later
            plswrite.write(encrypted + System.lineSeparator());
            //close the writer
            plswrite.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * method to convert a byte array to a hex string
     * @param bytes - the byte array to be converted
     * @return - the hex string after conversion
     */
    private static String bytesToHex(byte[] bytes) {

        //specify the hex characters
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

        // create a char array of 2ce the length of the given byte array (hex take up twice the space that bytes do)
        char[] hexChars = new char[bytes.length * 2];

        // for every byte in the byte array convert the byte to a hex char and add it to the hexChars array
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        // cast the hex char array to string and return
        return new String(hexChars);
    }
}
