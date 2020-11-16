import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Enumeration;

/**
 * This is a filter for the log out functionality
 * When the user logs out, all his session attributes are removed, besides the login count and the key pair.
 */
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

        //remove all other existing session attributes
        Enumeration<String> attributes =  session.getAttributeNames();
        while (attributes.hasMoreElements()){
            session.removeAttribute(attributes.nextElement());
        }
        //invalidate the current session
        session.invalidate();

        // get a new session
        session = request.getSession();

        //assign the keypair again
        session.setAttribute("keypair", pair);

        //assign the logins again
        session.setAttribute("loginsLeft", loginTries);

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) {
        config.getServletContext().log("Filter Started");
    }

}
