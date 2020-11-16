import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Objects;

/**
 * This is a context listener used to delete all existing users' files when the tomcat session is created/closed
 */
public class LotteryAppContextListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        File dir = new File("Created Files");

        if(dir.exists()) {
            // delete all files inside the directory
            for (File file : Objects.requireNonNull(dir.listFiles()))
                if (!file.isDirectory())
                    file.delete();

            // and delete the directory itself
            dir.delete();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        File dir = new File("Created Files");

        if(dir.exists()) {
            for (File file : Objects.requireNonNull(dir.listFiles()))
                if (!file.isDirectory())
                    file.delete();
            dir.delete();
        }
    }
}
