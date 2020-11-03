<%--
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

<!-- Redirect to log in page all users who tried accessing this public account page without logging in or from an admin account -->
<% if ((request.getSession() == null) || (!(session.getAttribute("role").equals("public")))) {
    response.sendRedirect("index.jsp"); }  %>

<h3><%= request.getAttribute("message") %></h3>

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
<% String[] arr = (String[]) request.getAttribute("draws");
    if (!(arr == null)){ %>
        <h3><%="Your draws:" %></h3>
       <% for (int i = 0; i < arr.length; i++){ %>
            <p><%= i+1 + ") " + arr[i] %></p>
      <%  }
      session.setAttribute("draws", arr);
    } %>

<form action="AddUserNumbers" method="post">
    <label for="usernumber">Your Number:</label>
    <input type="text" id="usernumber" name="usernumber" pattern="(60|[0-5]?[0-9]){6}" title="Enter 6 integers between 0 and 60 inclusive.">
    <input type="button" onclick="secureluckyDip()" value="Get me a random number!" required>
    <input type="submit" value="Submit Your Number">
</form>

<form action="GetUserNumbers" method="post">
    <input type="submit" value="Get Draws">
</form>

<form action="CheckForWinners" method="post">
    <input type="submit" value="Are you a winner?">
</form>

<% String winner = (String) request.getAttribute("winningdraw");
String[] draws = (String[]) session.getAttribute("draws");
    if ((draws != null) && (winner != null)){
        System.out.println("halo sprawdzam array");
        Boolean win = false;
        for (String s : draws) {
            if (s.equals(winner)) {
                win = true;
                System.out.println("mamy to");
                break;
            }
            else{
                win = false;
                System.out.println("nie mamy tego");
            }
        }
        if (win){
            System.out.println("wygrana");%>
        <h4><%= "You win!!!" %></h4>
       <% } else {
           System.out.println("przegrana");%>
             <h4><%= "This is not your lucky day :(" %></h4>
      <% }
} %>

<script>
    //TODO check here what happens when the if statement is deleted
    // generate 6 cryptographically secure random integers between 0 and 60 (inclusive)
    function secureluckyDip(){
        max = 60;
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
        document.getElementById("usernumber").value = no;
    }
</script>


<a href="index.jsp">Log Out</a>

</body>
</html>