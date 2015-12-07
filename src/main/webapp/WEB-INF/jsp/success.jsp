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
<form action="/Neck/success" method="POST"> 
<table class="table1">
<tr>
<td><a> Uploading was successful. </a></td>
<td>  </td>
</tr>
<tr>
<td></td>
<td> <input class="btn" type="submit" value="Upload another file"> </td>
</tr>
</table>
</form>
</center>
</body>
</html>