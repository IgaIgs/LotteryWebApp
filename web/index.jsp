<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <title>Home</title>

      <!-- Get all the references and libraries -->
      <link rel="stylesheet" href="style.css"/>
      <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
      <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.19.1/jquery.validate.min.js"></script>
      <script src="http://jqueryvalidation.org/files/dist/additional-methods.min.js"></script>
  </head>
  <body>

  <h1> Home Page </h1>

  <!-- display the message if it's defined -->
  <% if (request.getAttribute("message") != null) { %>
  <h4><%= request.getAttribute("message") %></h4>
  <% } %>

  <!--  create and position title of registration form -->
  <h2 style ='position: absolute; left: 10px; top: 70px; padding: 10px;'> Sign Up </h2>

  <!-- create Sign Up form -->
  <form action="CreateAccount" name="register" method="post" style ='position: absolute; left: 10px; top: 120px; padding: 10px;'>
      <!-- first name field -->
      <label for="firstname">First name:</label><br>
      <input type="text" id="firstname" name="firstname" placeholder="John"><br>
      <!-- last name field -->
      <label for="lastname">Last name:</label><br>
      <input type="text" id="lastname" name="lastname" placeholder="Doe"><br>
      <!-- username field -->
      <label for="newusername">Username:</label><br>
      <input type="text" id="newusername" name="username" placeholder="JohnDoe938"><br>
      <!-- phone field -->
      <label for="phone">Phone Number:</label><br>
      <input type="tel" id="phone" name="phone" placeholder="00-0000-0000000"
             title="Must follow this pattern: 12-1234-1234567"><br>
      <!-- email field -->
      <label for="email">Email:</label><br>
      <input type="email" id="email" name="email" title="Must be a valid email of this structure: sometext@webdomain.extention"
             placeholder="johndoe@hello.com"><br>
      <!-- pwd field -->
      <label for="newpassword">Password:</label><br>
      <input type="password" id="newpassword" name="password"
             title="Must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15
             characters." ><br>
      <!-- Drop down menu to choose the role for the account -->
      <label for="role" >Select Account Type:</label><br>
      <select name="role" id="role">
          <option value="" selected="selected"> - select role - </option>
          <option value="admin">Admin</option>
          <option value="public">Public</option>
      </select><br><br>
      <input type="submit" value="Submit">
  </form>

  <!-- create and position title of log in form -->
  <h2 style ='position: absolute; left: 230px; top: 70px; padding: 10px;'> Log In </h2>

  <!-- create and position Log In form for already registered users -->
  <form action="UserLogin" name="login" method="post" style ='position: absolute; left: 230px; top: 120px; padding: 10px;'>
      <label for="username">Username:</label><br>
      <input type="text" id="username" name="username"><br>
      <label for="password">Password:</label><br>
      <input type="password" id="password" name="password"
             title="Must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15
             characters."><br>
      <!-- Drop down menu to choose the role for the account -->
      <label for="role1">Select Account Type:</label><br>
      <select name="role" id="role1">
          <option value="" selected="selected"> - select role - </option>
          <option value="admin">Admin</option>
          <option value="public">Public</option>
      </select><br><br>
      <input type="submit" id="submitb" value="Submit">
  </form>

  <!-- if the user hit maximum failed logins - disable the login form. Will be re-enabled after refresh-->
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
      // reactivate the website after refresh - to re-enable the log in form
      if ( window.history.replaceState ) {
          window.history.replaceState( null, null, "index.jsp");
      }
  </script>

  <script>
      // script containing validation functionality for both forms, done using JQuery library
      $(function() {

          // validation for the Sign Up form
          $("form[name='register']").validate({
              rules: {
                  firstname: "required",
                  lastname: "required",
                  username: "required",
                  phone: {
                      required: true,
                      pattern: /^\(?([0-9]{2})\)?[-]?([0-9]{4})[-]?([0-9]{7})$/,
                  },
                  email: {
                      required: true,
                      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                      email: true,
                      maxlength: "254"
                  },
                  password: {
                      required: true,
                      pattern: /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}$/
                  },
                  role: "required",
              },
              messages: {
                  firstname: "Please enter your firstname",
                  lastname: "Please enter your lastname",
                  username: "Please enter your username",
                  phone: {
                      required: "Please provide a phone number",
                      pattern: "A valid telephone number must follow this pattern: 12-1234-1234567"
                  },
                  email: {
                      required: "Please provide an email address",
                      email: "Enter a valid email of this structure: sometext@webdomain.extention",
                      pattern: "Enter a valid email of this structure: sometext@webdomain.extention",
                      maxlength: "You exceeded the character limit for this field."
                  },
                  password: {
                      required: "Please enter your password",
                      pattern: "A valid password must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15 characters."
                  },
                  role: "Please choose a role"
              },
          })

          // validation for the Log In form
          $("form[name='login']").validate({
              rules: {
                  username: "required",
                  password: {
                      required: true,
                      pattern: /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}$/
                  },
                  role: "required",
              },
              messages: {
                  username: "Please enter your username",
                  password: {
                      required: "Please enter your password",
                      pattern: "A valid password must contain at least one digit, one uppercase, one lowercase letter, and between 8 and 15 characters."
                  },
                  role: "Please choose a role"
              },
          })
      })
  </script>

  </body>
</html>
