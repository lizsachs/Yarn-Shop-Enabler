<%--
  Created by IntelliJ IDEA.
  User: Liz
  Date: 7/20/12
  Time: 9:13 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Test Token</title>
</head>
<body>
    <% projects.each { project -> %>
        <%= project.name + " " + project.started  %> <br>
    <%}%>
</body>
</html>