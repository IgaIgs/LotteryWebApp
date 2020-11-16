import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * This servlet is used to:
 * get the lottery draw file for the current user
 * retrieve and decrypt the draws from inside this file
 * add the decrypted draws to  array and passing it as one of the request attributes to the account page
 */
@WebServlet("/GetUserNumbers")
public class GetUserNumbers extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            // get the session from Servlet
            HttpSession session = request.getSession();

            //get the current user's hashed password
            String pwd = (String) session.getAttribute("hashed password");

            //get file name to be read from
            String filename = pwd.substring(0, 20) + ".txt";

            //if the user's file exists (already submitted draws but not yet checked for winners)
            if (Files.exists(Path.of("./Created Files/" + filename))) {

                //get all the encrypted strings from their file
                String encrypted = readFile(filename);

                //get the encryption keypair which was saved as an attribute of the session during encryption
                KeyPair pair = (KeyPair) session.getAttribute("keypair");

                // split the strings from the file back to single encrypted strings with 6 integers each
                // and store them in an array
                String[] lines = encrypted.split(System.lineSeparator());

                //create an array to store decrypted strings
                String[] decryptedStrings = new String[lines.length];

                //for each string inside the lines array
                for (int i = 0; i < lines.length; i++) {
                    //decrypt the string
                    String decrypted = decryptData(lines[i], pair);
                    // and add it to the decrypted array
                    decryptedStrings[i] = decrypted;
                }


                // forward the user to the account page with the decrypted draws saved to request attribute
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("draws", decryptedStrings);
                request.setAttribute("message", "To find out whether you won, press the 'Are you a winner?' button!");
                dispatcher.forward(request, response);
            }
            else{
                // when the this user's file doesn't exist
                // (cuz they didn't add any draws yet or they have already checked for winners and it got deleted etc.)
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("message", "No lottery draws found. Please add some first.");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     Method for reading strings from a given file
     * @param filename - the name of the file to be read from
     * @return - string read from the file
     */
    private static String readFile(String filename){
        // create an empty String variable
        String decrypted = "";
        // try to read the file
        try{
        FileInputStream plsread = new FileInputStream("./Created Files/" + filename);
        decrypted = new String(plsread.readAllBytes());
        plsread.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    /**
     * Method for decrypting Strings
     * @param encrypted - encrypted string to be decrypted
     * @param pair - encryption keypair used for decryption
     * @return - decrypted string
     */
    private static String decryptData(String encrypted, KeyPair pair){
        try {
            // instantiate the cipher object, set it to decryption mode and give it the key pair
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());

            //convert the encrypted string to a byte array to be able to pass it to the cipher
            cipher.update(hexStringtoByte(encrypted));

            //decrypt the string
            byte[] decrbyte = cipher.doFinal();
            //return decrypted string
            return new String(decrbyte);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method for converting a hex string to a byte array
     * @param hexs - hex string to be converted
     * @return - byte array
     */
    private static byte[] hexStringtoByte(String hexs){
        // get the length of the hex string
        int len = hexs.length();
        // create a byte array of half the length (bytes hav half the size of hex chars)
        byte[] b = new byte[len / 2];
        // for every character from the hex string, convert it to byte and save to the byte array
        for (int i = 0; i < len; i += 2) {
            b[i / 2] = (byte) ((Character.digit(hexs.charAt(i), 16) << 4)
                    + Character.digit(hexs.charAt(i+1), 16));
        }
        // return the byte array
        return b;
    }
}
