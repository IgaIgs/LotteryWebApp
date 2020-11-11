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

@WebServlet("/GetUserNumbers")
public class GetUserNumbers extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            // get the session from Servlet
            HttpSession session = request.getSession();

                //get the hashed password
                String pwd = (String) session.getAttribute("hashed password");

                //get file name to be read from
                String filename = pwd.substring(0, 20) + ".txt";

            if (Files.exists(Path.of("./Created Files/" + filename))) {
                //if the user has already submitted draws but hasn't yet checked for winners so their file exists

                //get all the encrypted strings from their file
                String encrypted = readFile(filename);
                System.out.println("Encrypted String: " + encrypted);

                //get the encryption keypair which was saved as an attribute of the session when encrypting
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

                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("draws", decryptedStrings);
                request.setAttribute("message", "To find out whether you won, press the 'Are you a winner?' button!");
                dispatcher.forward(request, response);
            }
            else{
                // when the this user's file doesn't exist
                // (cuz they didn't add any draws yet or they have already checked for winners and it got deleted etc)
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("message", "No lottery draws found. Please add some first.");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private static String readFile(String filename) throws IOException {
        /*Path path = Paths.get("D:\\Users\\Kirai\\CSC2031 Coursework\\LotteryWebApp\\Created Files\\");
        path = path.resolve(filename);
        FileInputStream plsread = new FileInputStream(path.toAbsolutePath().toString());*/
        String decrypted = "";
        try{
        FileInputStream plsread = new FileInputStream("./Created Files/" + filename);
        decrypted = new String(plsread.readAllBytes());
        plsread.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    private static String decryptData(String encrypted, KeyPair pair){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());

            //convert the encrypted string to a byte array to be able to pass it to the cipher
            cipher.update(hexStringtoByte(encrypted));

            byte[] decrbyte = cipher.doFinal(); //decrypt the string
            return new String(decrbyte); //return decrypted string

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] hexStringtoByte(String hexs){
        int len = hexs.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            b[i / 2] = (byte) ((Character.digit(hexs.charAt(i), 16) << 4)
                    + Character.digit(hexs.charAt(i+1), 16));
        }
        return b;
    }
}
