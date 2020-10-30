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

            //get encrypted string
            String encrypted = readFromFile(filename);

            //get the encryption keypair which was saved as an attribute of the session when encrypting
            KeyPair pair = (KeyPair) session.getAttribute("keypair");

            //decrypt the string
            String decrypted = decryptData(encrypted, pair);
            System.out.println("decrypted: " + decrypted);

            //add the decrypted String to an Array
            String[] decryptedStrings = new String[4];
            decryptedStrings[0]= decrypted;
            for (int i = 1; i < decryptedStrings.length-1; i++){
                decryptedStrings[i] = "Whatever";
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            request.setAttribute("draws", decryptedStrings);
            request.setAttribute("message", "Find your draws below your account info:");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public String readFromFile(String filename){
        try{
            return Files.readString(Path.of("D:\\Users\\Kirai\\CSC2031 Coursework\\LotteryWebApp\\Created Files\\" + filename));
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /*public String readFile(String filename) throws IOException {
        Path path = Paths.get("D:\\Users\\Kirai\\CSC2031 Coursework\\LotteryWebApp\\Created Files\\");
        path = path.resolve(filename + ".txt");
        FileInputStream plsread = new FileInputStream(path.toAbsolutePath().toString());
        String decrypted = new String(plsread.readAllBytes());
        plsread.close();
        return decrypted;
    }*/

    //TODO solve decryption error
    public String decryptData(String encrypted, KeyPair pair){
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

    public byte[] hexStringtoByte(String hexs){
        int len = hexs.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            b[i / 2] = (byte) ((Character.digit(hexs.charAt(i), 16) << 4)
                    + Character.digit(hexs.charAt(i+1), 16));
        }
        return b;
    }
}
