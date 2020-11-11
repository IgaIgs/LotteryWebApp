import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Objects;

public class LotteryAppContextListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        File dir = new File("Created Files");

        if(dir.exists()) {
            for (File file : Objects.requireNonNull(dir.listFiles()))
                if (!file.isDirectory())
                    file.delete();

            dir.delete();
            System.out.println("usunelam directory");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        File dir = new File("Created Files");

        if(dir.exists()) {
            for (File file : Objects.requireNonNull(dir.listFiles()))
                if (!file.isDirectory())
                    file.delete();
            System.out.println("usunelam directory");
            dir.delete();
        }
    }
}
