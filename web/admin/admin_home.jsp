<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 02/11/2020
  Time: 18:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin homepage</title>
</head>
<body>
<h1> Welcome to your admin account, <%=session.getAttribute("first name")%>! </h1>

<!-- Redirect to log in page all users who tried accessing this admin page without logging in or from a public account -->
<% if ((request.getSession() == null) || (!(session.getAttribute("role").equals("admin")))) {
    response.sendRedirect("../index.jsp"); }  %>

<h3><%= request.getAttribute("message") %></h3>

<% if (request.getAttribute("data") != null) { %>
<p><%= request.getAttribute("data") %></p><br>
<% } %>

<form action="GetUserData" method="post" style ='position: relative; top: 10px;'>
    <input type="submit" value="Get User Data">
</form>

<a href="${pageContext.request.contextPath}/index.jsp">Log Out</a>
</body>
</html>
