<%@ page import="entity.Role" %><%--
  Created by IntelliJ IDEA.
  User: h'p
  Date: 2019/9/2
  Time: 8:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>session Attribute</title>
</head>
<body>
<center>
    <%
        Role role = (Role) session.getAttribute("role");
        out.println("id = " + role.getId() + "<p/>");
    %>
</center>
</body>
</html>
