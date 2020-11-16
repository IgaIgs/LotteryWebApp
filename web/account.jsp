<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Account</title>
</head>
<body>
<h1> Welcome to your account, <%=session.getAttribute("first name")%>! </h1>

<!-- Redirect to log in page all users who tried accessing this public account page without logging in or creating an account or from an admin account -->
<% if ((request.getSession() == null) || (session.getAttribute("role") == null) || (!(session.getAttribute("role").equals("public")))) {
    response.sendRedirect("index.jsp"); }  %>

<!-- Show the message to the user if the message exists -->
<% if (request.getAttribute("message") != null) { %>
    <h3><%= request.getAttribute("message") %></h3>
<% } %>

<!-- Display the user's account data -->
<p> ---------------- </p>
<h3><%= "Your account data: "%></h3>
<p><%= "First name: " + session.getAttribute("first name") %></p>
<p><%= "Last name: " + session.getAttribute("last name") %></p>
<p><%= "Username: " + session.getAttribute("username") %></p>
<p><%= "Role: " + session.getAttribute("role") %></p>
<p><%= "Email: " + session.getAttribute("email") %></p>
<p><%= "Phone number: " + session.getAttribute("phone number") %></p>
<p></p>

<!-- Section of the page dedicated to the lottery -->
<p> ---------------- </p>
<h3><%= "Lottery panel: "%></h3>
<!-- check whether the draws have been passed already and display them if yes -->
<% String[] arr = (String[]) request.getAttribute("draws");
    if (!(arr == null)){ %>
        <h4><%="Your draws:" %></h4>
       <% for (int i = 0; i < arr.length; i++){ %>
            <p><%= i+1 + ") " + arr[i] %></p>
      <%  }
      session.setAttribute("draws", arr);
    } %>

<!-- Create a form for the users to add their lottery draws -->
<form action="AddUserNumbers" method="post" >
    <label for="userno1">No. 1</label>
    <input type="text" id="userno1" name="userno1" pattern="(60|[0-5]?[0-9]){1}" size="1" title="Enter an integer between 0 and 60 inclusive." required>
    <label for="userno2">No. 2</label>
    <input type="text" id="userno2" name="userno2" pattern="(60|[0-5]?[0-9]){1}" size="1" title="Enter an integer between 0 and 60 inclusive." required>
    <label for="userno3">No. 3</label>
    <input type="text" id="userno3" name="userno3" pattern="(60|[0-5]?[0-9]){1}" size="1" title="Enter an integer between 0 and 60 inclusive." required>
    <label for="userno4">No. 4</label>
    <input type="text" id="userno4" name="userno4" pattern="(60|[0-5]?[0-9]){1}" size="1" title="Enter an integer between 0 and 60 inclusive." required>
    <label for="userno5">No. 5</label>
    <input type="text" id="userno5" name="userno5" pattern="(60|[0-5]?[0-9]){1}" size="1" title="Enter an integer between 0 and 60 inclusive." required>
    <label for="userno6">No. 6</label>
    <input type="text" id="userno6" name="userno6" pattern="(60|[0-5]?[0-9]){1}" size="1" title="Enter an integer between 0 and 60 inclusive." required>
    <input type="button" onclick="secureluckyDip()" value="Get me a random draw number!" required>
    <input type="submit" value="Submit Your Draw" >
</form>

<!-- Form and button for retrieving the user's draws -->
<form action="GetUserNumbers" method="post" >
    <input type="submit" id="getDraws" value="Get Draws" >
</form>

<!-- Form with a button for checking against the winning draw -->
<form action="CheckForWinners" method="post">
    <input type="submit" id="getWinners" value="Are you a winner?" >
</form>

<%   // get the winning draw from the request attribute
    String winner = (String) request.getAttribute("winningdraw");
     // get the current user's draws
    String[] draws = (String[]) session.getAttribute("draws");

    // if both variables have values
    if ((draws != null) && (winner != null)){

        // create a boolean tracker for the win
        boolean wins = false;
        // create an empty string to pass he winning draw to it if the user wins
        String winnerdraw;
        // for each of user's draws compare them with he winning draw
        for (String s : draws) {
            if (s.equals(winner)) { // if the user wins
                // update the boolean tracker
                wins = true;
                // set the winning draw to the one that matched
                winnerdraw = s;
                // if the user won display a message with information on which of their draws was the winning one %>
                <h3><%= String.format("You win!!! Your draw '%s' was a match!!", winnerdraw) %></h3>
               <% break;
            }
        }
        if (!wins) { %>
<h3><%= "This is not your lucky day :(" %></h3>
         <% }
        // remove that user's draws from the session
        session.removeAttribute("draws"); %>
        <h5><%= "Submit new numbers to try again!" %></h5>
   <% } %>

<script>
    /**
     *  Generate 6 cryptographically secure random integers between 0 and 60 (inclusive)
     *  And display each of them in the correct text box in the lottery numbers form
     */
    function secureluckyDip(){
        // set limits
        const max = 60;
        const min = 0;

        // for each of the 6 lottery integers create a secure random number
        for (let j = 1; j<=6; j++){
            // Create byte array and fill with 1 random number
            const arr = new Uint8Array(1);
            window.crypto.getRandomValues(arr);

            const range = max - min + 1;
            const max_range = 256;
            let no = "";
            for (let i = 0; i< arr.length; i++){ //for each of the elements of the array
                if (arr[i] >= Math.floor(max_range / range) * range) //check if its out of the range
                    return secureluckyDip(min, max);
                no = no + (min + (arr[i] % range)); //if not, generate random numbers and add them to the output
            }
            // assign each of the generated numbers to their respective fields
            document.getElementById("userno" + j).value = no;
        }
    }
</script>

<!--Log the user out -->
<a href="index.jsp" >Log Out</a>

</body>
</html>