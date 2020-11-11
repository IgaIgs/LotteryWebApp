<%@ page import="java.util.Arrays" %><%--
  Created by IntelliJ IDEA.
  User: johnmace
  Date: 21/10/2020
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
</head>
<body>
<h1> Welcome to your account, <%=session.getAttribute("first name")%>! </h1>
<% System.out.println("Check 3: " + (session.getAttribute("role") == null));%>
<!-- Redirect to log in page all users who tried accessing this public account page without logging in or creating an account or from an admin account -->
<% if ((request.getSession() == null) || (session.getAttribute("role") == null) || (!(session.getAttribute("role").equals("public")))) {
    response.sendRedirect("index.jsp"); }  %>

<% if (request.getAttribute("message") != null) { %>
    <h3><%= request.getAttribute("message") %></h3>
<% } %>


<p> ---------------- </p>
<h3><%= "Your account data: "%></h3>
<p><%= "First name: " + session.getAttribute("first name") %></p>
<p><%= "Last name: " + session.getAttribute("last name") %></p>
<p><%= "Username: " + session.getAttribute("username") %></p>
<p><%= "Role: " + session.getAttribute("role") %></p>
<p><%= "Email: " + session.getAttribute("email") %></p>
<p><%= "Phone number: " + session.getAttribute("phone number") %></p>
<p></p>
<p> ---------------- </p>
<h3><%= "Enter your draw numbers for a chance to win in the lottery: "%></h3>
<% String[] arr = (String[]) request.getAttribute("draws");
    System.out.println("biore draws od get user numbers request");
    if (!(arr == null)){ %>
        <h3><%="Your draws:" %></h3>
       <% for (int i = 0; i < arr.length; i++){ %>
            <p><%= i+1 + ") " + arr[i] %></p>
      <%  }
      session.setAttribute("draws", arr);
    } %>

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

<form action="GetUserNumbers" method="post" >
    <input type="submit" id="getDraws" value="Get Draws" >
</form>

<form action="CheckForWinners" method="post">
    <input type="submit" id="getWinners" value="Are you a winner?" >
</form>

<% String winner = (String) request.getAttribute("winningdraw");
String[] draws = (String[]) session.getAttribute("draws");
    if ((draws != null) && (winner != null)){
        System.out.println("halo sprawdzam array");
        Boolean wins = false;
        String winnerdraw = "";
        for (String s : draws) {
            if (s.equals(winner)) {
                wins = true;
                winnerdraw = s;
                System.out.println("mamy to");
                break;
            }
            else{
                wins = false;
                System.out.println("nie mamy tego");
            }
        }
        if (wins){
            System.out.println("wygrana");%>
        <h3><%= String.format("You win!!! Your draw %s was a match!!", winnerdraw) %></h3>
       <% } else {
           System.out.println("przegrana");%>
             <h3><%= "This is not your lucky day :(" %></h3>
      <% }
        session.removeAttribute("draws"); %>
        <h5><%= "Submit new numbers to try again!" %></h5>
   <% } %>

<script>
    //TODO check here what happens when the if statement is deleted
    // generate 6 cryptographically secure random integers between 0 and 60 (inclusive)
    function secureluckyDip(){
        max = 60;
        min = 0;

        for (var j = 1; j<=6; j++){
            // Create byte array and fill with 1 random number
            var arr = new Uint8Array(1);
            window.crypto.getRandomValues(arr);

            var range = max - min + 1;
            var max_range = 256;
            var no = "";
            for (var i = 0; i< arr.length; i++){ //for each of the elements of the array
                if (arr[i] >= Math.floor(max_range / range) * range) //check if its out of the range
                    return secureluckyDip(min, max);
                no = no + (min + (arr[i] % range)); //if not, generate random numbers and add them to the output
            }
            document.getElementById("userno" + j).value = no;
        }
/*        max = 60;
        min = 0;


        // Create byte array and fill with 1 random number
        var arr = new Uint8Array(6);
        window.crypto.getRandomValues(arr);

        var range = max - min + 1;
        var max_range = 256;
        var no = "";
        for (var i = 0; i< arr.length; i++){ //for each of the elements of the array
            if (arr[i] >= Math.floor(max_range / range) * range) //check if its out of the range
                return secureluckyDip(min, max);
            no = no + (min + (arr[i] % range)); //if not, generate random numbers and add them to the output
        }
        document.getElementById("usernumber").value = no;*/
    }
</script>



<a href="index.jsp" >Log Out</a> <!-- onclick="<%//session.setAttribute("role", null);%>" -->

</body>
</html>