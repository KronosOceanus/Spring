<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: h'p
  Date: 2019/9/4
  Time: 16:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Role</title>
</head>
<body>
<table border="1">
    <tr>
        <td>标签</td>
        <td>值</td>
    </tr>
    <tr>
        <td>角色编号</td>
        <td><c:out value="${role.id}" /></td>
    </tr>
    <tr>
        <td>角色名称</td>
        <td><c:out value="${role.roleName}" /></td>
    </tr>
    <tr>
        <td>角色备注</td>
        <td><c:out value="${role.note}" /></td>
    </tr>
</table>
</body>
</html>
