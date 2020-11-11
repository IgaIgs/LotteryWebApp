<%--
  Created by IntelliJ IDEA.
  User: johnmace
  Date: 21/10/2020
  Time: 15:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Home</title>
  </head>
  <body>

  <h1> Home Page </h1>

  <% if (request.getAttribute("message") != null) { %>
  <h4><%= request.getAttribute("message") %></h4>
  <% } %>

  <!--  create and position title of form1 -->
  <h2 style ='position: absolute; left: 10px; top: 70px; padding: 10px;'> Sign Up </h2>

  <!-- create Sign Up form --> <!--onsubmit="return validateRegister()" -->
  <form action="CreateAccount" method="post" onsubmit="return validateRegister()" style ='position: absolute; left: 10px; top: 120px; padding: 10px;'>
      <!-- first name field -->
      <label for="firstname">First name:</label><br>
      <input type="text" id="firstname" name="firstname" placeholder="John" required><br>
      <!-- last name field -->
      <label for="lastname">Last name:</label><br>
      <input type="text" id="lastname" name="lastname" placeholder="Doe" required><br>
      <!-- username field -->
      <label for="username">Username:</label><br>
      <input type="text" id="newusername" name="username" placeholder="JohnDoe938" required><br>
      <!-- phone field -->
      <label for="phone">Phone Number:</label><br>
      <input type="tel" id="phone" name="phone" placeholder="00-0000-0000000"
             title="Must follow this pattern: 12-1234-1234567" required><br> <!-- pattern="[0-9]{2}-[0-9]{4}-[0-9]{7}" -->
      <!-- email field -->
      <label for="email">Email:</label><br>
      <input type="text" id="email" name="email" maxlength='50' placeholder="johndoe@hello.com" required><br>
      <!-- pass field -->
      <label for="password">Password:</label><br>
      <input type="password" id="newpassword" name="password"
             title="Must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15
             characters." required><br><br> <!-- pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}" -->
      <!-- Drop down menu to choose the role for the account -->
      <label>Select Account Type:</label><br>
      <select name="role" required>
          <option value="" selected="selected"> - select role - </option>
          <option value="admin">Admin</option>
          <option value="public">Public</option>
      </select>
      <input type="submit" value="Submit">
  </form>

  <!-- create and position title of form 2 -->
  <h2 style ='position: absolute; left: 230px; top: 70px; padding: 10px;'> Log In </h2>

  <!-- create and position form for Logging In for already registered users --> <!-- onsubmit="return validateLogIn()"-->
  <form action="UserLogin" method="post" onsubmit="return validateLogIn()" style ='position: absolute; left: 230px; top: 120px; padding: 10px;'>
      <label for="Username">Username:</label><br>
      <input type="text" id="username" name="username" required><br>
      <label for="Password">Password:</label><br>
      <input type="password" id="password" name="password"
             title="Must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15
             characters." required><br> <!-- pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}" -->
      <!-- Drop down menu to choose the role for the account -->
      <label>Select Account Type:</label><br>
      <select name="role" id="role" required>
          <option value="" selected="selected"> - select role - </option>
          <option value="admin">Admin</option>
          <option value="public">Public</option>
      </select>
      <input type="submit" id="submitb" value="Submit">
  </form>

  <!-- if the user hit maximum failed logins - disable the login form -->
  <% if ((request.getAttribute("loginsattempted") != null) && ((Integer) request.getAttribute("loginsattempted") == 3)) {%>
  <script>
      document.getElementById("username").disabled=true;
      document.getElementById("password").disabled=true;
      document.getElementById("role").disabled=true;
      document.getElementById("submitb").disabled=true;

      alert("Unfortunately, you have exceeded the maximum possible unsuccessful login attempts.\n Refresh the page to try again!");

  </script>
  <%} %>

  <script>
      // reactivate the website after refresh
      if ( window.history.replaceState ) {
          window.history.replaceState( null, null, "index.jsp");
      }
  </script>


    <script>
      function validateRegister(){
          tel = document.getElementById("phone").value;
          mail = document.getElementById("email").value;
          pass = document.getElementById("newpassword").value;
          var telRe = /^\(?([0-9]{2})\)?[-]?([0-9]{4})[-]?([0-9]{7})$/ // regex for checking phone numbers for the XX{2}-XXXX{4}-XXXXXXX{7} pattern
          var passRe = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}$/ // regex for password validity
          var emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/ //regex for email validation, pattern: sth@sth.sth, doesn't allow two @
          if (telRe.test(tel)){
              if (emailRe.test(mail)){
                  if (passRe.test(pass)){
                      return true;
                  }
                  else{
                      alert("A valid password must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15 characters.")
                      document.getElementById("newpassword").value = null;
                      return false;
                  }
              }
              else {
                  alert("Enter a valid email of this structure: sometext@webdomain.extention");
                  //document.getElementById("email").value = null;
                  return false;}
          }
          else{
              alert("A valid telephone number must follow this pattern: 12-1234-1234567");
              //document.getElementById("phone").value = null;
              return false;
          }
      }

      function validateLogIn(){
          pass = document.getElementById("password").value;
          var passRe = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}$/ // regex for password validity
          if (passRe.test(pass)){
              return true;
          }
          else{
              alert("A valid password must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15 characters.")
              document.getElementById("password").value = null;
              return false;
          }
      }
  </script>

  </body>
</html>
