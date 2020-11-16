<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
   <h1>Error Page</h1>

   <!-- display messages to the user provided they exist -->
   <% if (request.getAttribute("message") != null) { %>
   <h3><%= request.getAttribute("message") %></h3>
   <% } %>
   <% if (request.getAttribute("message2") != null) { %>
   <h4><%= request.getAttribute("message2") %></h4>
    <% } %>


   <!-- Take the user back to home page -->
   <a href="index.jsp">Home Page</a>

</body>
</html>
