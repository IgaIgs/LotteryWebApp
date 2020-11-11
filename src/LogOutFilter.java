import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Enumeration;

@WebFilter(filterName = "LogOutFilter")
public class LogOutFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        //get session
        HttpSession session = request.getSession();

        //get the encryption keypair from the current session to prevent it from being deleted
        KeyPair pair = (KeyPair) session.getAttribute("keypair");

        //get the log in attempts from the current session to keep the count in check
        Integer loginTries = (Integer) session.getAttribute("loginsLeft");
        System.out.println("how many login attempts left: " + loginTries);

        //remove all pre-existing session attributes
        Enumeration<String> attributes =  session.getAttributeNames();
        while (attributes.hasMoreElements()){
            session.removeAttribute(attributes.nextElement());
        }
        System.out.println(session.getAttribute("loginsLeft") == null);
        //invalidate the current session
        session.invalidate();
        System.out.println("cleared the session in filter");

        // get the session again
        session = request.getSession();

        //assign the keypair again
        session.setAttribute("keypair", pair);

        //assign the logins again
        session.setAttribute("loginsLeft", loginTries);
        System.out.println("ile mam loginow halo: " + loginTries);

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        config.getServletContext().log("Filter Started");
    }

}
