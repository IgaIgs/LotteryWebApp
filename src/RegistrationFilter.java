import javax.servlet.RequestDispatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Map;

//filter to prevent SQL injection hacks
@WebFilter(filterName = "RegistrationFilter")
public class RegistrationFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
        config.getServletContext().log("Filter Started");
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        boolean invalid = false;
        Map params = request.getParameterMap(); //k(input names):v(input values) map of input sent from the Sign Up Form

        if(params != null){
            for (Object o : params.keySet()) { // for each key (input name) from params
                String key = (String) o; //set its value to a local variable called key
                String[] values = (String[]) params.get(key); // get values mapped to each of the keys and add them to a String array

                for (String value : values) { //for each value in the array
                    if (checkChars(value)) { //if the value matched with any bad characters, this will be true
                        invalid = true; // so the input is invalid
                        break;
                    }
                }
                if (invalid) {
                    break;
                }
            }
        }

        if(invalid){ // if the input is invalid
            try{ //take the user to an error page with info on what happened
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                request.setAttribute("message", "Error <br> Invalid characters entered. Please try again!");
                dispatcher.forward(request, response);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{ // if the input was valid, proceed with regular chain of activities
            chain.doFilter(request, response);
        }

    }

    /**
     * Method checking whether given String parameter matches any of the forbidden characters (Strings), which are
     * stored in a form of a String array. If it matches, the method returns true, if not, false.
     *
     * @param value - user's input to a text field in a form
     * @return - boolean value saying whether the input is valid or not
     */
    //TODO change syntax so it's not John XD
    private static boolean checkChars(String value) {
        boolean invalid = false;
        String[] badChars = { "<", ">", "!", "{", "}", "insert", "into", "where", "script", "delete", "input" };

        for (String badChar : badChars) { // check every forbidden character against the argument value
            if (value.contains(badChar)) {
                invalid = true; //if the value matches with any of the bad chars - set invalid to true
                break;
            }
        }
        return invalid;
    }
}
