<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
<form action="/Neck/loadFile" method="POST"> 
<br>
<table class="table1">
<tr>
<td><a> Enter your file location (../file.pcap) </a></td>
<td><input type="text" name="processFilePath" /></td>
</tr>
</table>
<br>
<br>
<input class="btn" type="submit" value="Next">
<div class="errorMessage">${message}</div> 
</form>
</center>
</body>
</html>
