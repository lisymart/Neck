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
<center>
<h1> Neck </h1>
<h3> Here you can change the name of attributes: </h3>
<br>
<form action="/Neck/pcap" method="POST"> 
<table class="table1">
    <tr><td><b>log file name</b></td><td><b>attribute orig name</b></td><td><b>your name</b></td></tr>
<tr></tr>
<c:forEach var="item" items="${attributesList}">
    <tr>
       <td> <c:out value="${item}" /> </td>
       <td></td>
       <td> <input type="text" name="${item}"><br> </td> 
    </tr>
</c:forEach>
</table>
<br>
<input class="btn" type="submit" value="Upload to ES">
</form>
</center>
</body>
</html>

