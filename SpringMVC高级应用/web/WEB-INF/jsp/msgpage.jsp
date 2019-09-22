<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: h'p
  Date: 2019/9/18
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>msgpage</title>
</head>
<body>
<center>
    <h2>
        <spring:message code="welcome" />
    </h2>
    Locale:${pageContext.response.locale}
</center>
</body>
</html>
