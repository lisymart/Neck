<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${pageContext.request.contextPath}/resources/styles.css" rel="stylesheet" type="text/css"/>
<title> Neck </title>
</head>
<body>
<div id="center">
<h1> Neck </h1>
<h3> Here are selected attributes from the first 1000 lines of each log. </h3>
<h3> You can change the name of attributes here: </h3>
<br>
<form action="/Neck/pcap" method="POST"> 
<table class="table1">
<tr>
<td><b> Attribute orig name </b></td>
<td><b> Your change </b></td>
</tr>
<tr></tr>
<c:forEach var="item" items="${attributesList}">
<tr>
<td> <c:out value="${item}"/> </td>
<td> <input type="text" name="${item}"><br> </td>
</tr> 
</c:forEach>
</table>
<br>
<input class="btn" type="submit" value="Upload to ES">
</form>
</div>
</body>
</html>

