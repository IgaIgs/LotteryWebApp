import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import java.util.Arrays;

@WebServlet("/GetUserNumbers")
public class GetUserNumbers extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            // get the session from Servlet
            HttpSession session = request.getSession();

            System.out.println("otwieram sesje");
            //get the hashed password
            String pwd = (String) session.getAttribute("hashed password");
            System.out.println("mam hashed password");

            //get file name to be read from
            String filename = pwd.substring(0, 20);
            System.out.println("mam nazwe pliku");

            //get encrypted string
            String encrypted = readFromFile(filename);
            System.out.println("encrypted string: " + encrypted);

            //get the encryption keypair which was saved as an attribute of the session when encrypting
            KeyPair pair = (KeyPair) session.getAttribute("keypair");
            System.out.println("mam key pair");

            //decrypt the string
            String decrypted = decryptData(filename, pair);
            System.out.println("decrypted: " + decrypted);

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

    //TODO solve decryption error
    public String decryptData(String encrypted, KeyPair pair){
        try {
            System.out.println("weszlam do decrypt funkcji");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
            System.out.println("zainicjowalam ciphera");
            //convert the encrypted string to a byte array to be able to pass it to the cipher
            cipher.update(encrypted.getBytes());
            System.out.println("String do bytearray");

            byte[] decrbyte = cipher.doFinal(); //decrypt the string
            System.out.println("String decrypted");
            return Arrays.toString(decrbyte); //return decrypted string

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
