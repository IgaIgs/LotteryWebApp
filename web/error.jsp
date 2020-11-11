<%--
  Created by IntelliJ IDEA.
  User: johnmace
  Date: 21/10/2020
  Time: 16:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
   <h1>Error Page</h1>

   <% if (request.getAttribute("message") != null) { %>
   <h3><%= request.getAttribute("message") %></h3>
   <% } %>
   <% if (request.getAttribute("message2") != null) { %>
   <h4><%= request.getAttribute("message2") %></h4>
    <% } %>


   <a href="index.jsp">Home Page</a>

</body>
</html>
