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
    <title>Output</title>
</head>
<body>

<h1>Database Contents</h1>

<div>
    <%= request.getAttribute("data") %>
    <%= request.getAttribute("msg") %>
</div>

<div>
    <a href="index.jsp">Home Page</a>
</div>

</body>
</html>
