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
        Map params = request.getParameterMap(); //k(names):v(values) map of input sent from the Sign Up Form

        /*
        Inside the While Loop, each Key and Value of each parameter (a Key-Value pair) is cast to a String type
        and assigned to separate variables, key and values respectively. Note, key is used as the parameter in the call
        to get the parameter Value assigned to values.  Note also that for each Key-Value pair,
        the Key may map to a Value set hence why values is an array of Strings.
        An If statement is then used to check each String in the values array to see if it is valid.
        If not valid, then the boolean invalid is assigned true and the If statement exits using the break keyword.
        The validity of each String in values is checked by the checkChars method we will define below.
        The last line here forces the While Loop to exit, again using break, because we only need to find one invalid
        String for the entire user input to be invalid.
         */
        if(params != null){
            for (Object o : params.keySet()) {
                String key = (String) o;
                String[] values = (String[]) params.get(key);

                for (String value : values) {
                    if (checkChars(value)) {
                        invalid = true;
                        break;
                    }
                }
                if (invalid) {
                    break;
                }
            }
        }

        if(invalid){
            try{
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                request.setAttribute("message", "error");
                dispatcher.forward(request, response);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            chain.doFilter(request, response);
        }

    }

    /**
     * Essentially, the method defines an array of Strings containing the unwanted Strings or Characters given above,
     * loops through that array and checks if any of the unwanted Strings are present in the value String parameter.
     * This is done using the String indexOf() method which returns the position of the first occurrence of specified
     * character(s) in a string. If a position, or index, is returned by indexOf(), an unwanted String must exist in
     * value. Subsequently, the boolean invalid is assigned true and returned by the method.
     *
     * @param value - user's input to a text field in a form
     * @return - boolean value saying whether the input is valid or not
     */
    //TODO change syntax so it's not John XD
    private static boolean checkChars(String value) {
        boolean invalid = false;
        String[] badChars = { "<", ">", "!", "{", "}", "insert", "into", "where", "script", "delete", "input" };

        for (String badChar : badChars) {
            if (value.contains(badChar)) {
                invalid = true;
                break;
            }
        }
        return invalid;
    }
}
