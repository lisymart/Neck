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
<h5> Here are selected attributes from the first 1000 lines of each log. You can change the name of attributes here: </h5>
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
<h5> And here you can add any other logstash configuration. Please use the whole filter configuration e.g.:  </h5>
<i> mutate{rename => {"fieldName" => "newName"}} </i>
<br><br>
<textarea class="addition" name="addition" rows=10>
</textarea>
<br>
<br>
<input class="btn" type="submit" value="Upload to ES">
</form>
</div>
</body>
</html>

