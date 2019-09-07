<%--
  Created by IntelliJ IDEA.
  User: h'p
  Date: 2019/9/2
  Time: 8:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RequestAttribute</title>
</head>
<body>
<%-- 转发请求到控制器 --%>
<%
    request.setAttribute("id", 1);
    request.getRequestDispatcher("../attribute/requestAttribute.form").forward(request,response);
%>
</body>
</html>
