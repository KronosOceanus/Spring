<%--
  Created by IntelliJ IDEA.
  User: h'p
  Date: 2019/9/2
  Time: 8:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SessionAttribute</title>
</head>
<body>
<%
    //在 session 中设置属性，并跳转到控制器
    session.setAttribute("id", 1);
    response.sendRedirect("../attribute/sessionAttribute.form");
%>
</body>
</html>
