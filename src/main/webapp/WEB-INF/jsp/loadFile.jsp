<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
<form action="/Neck/loadFile" method="POST" enctype="multipart/form-data"> 
<br>
<table class="table1">
<tr>
<td><a> Browse your file to process: </a></td>
<td> Rename your file if you want to: </td>
</tr>
<tr>
<td><input type="file" name="fileToUpload"></td>
<td><input type="text" name="nameOfFile"></td>
</tr>
</table>
<br>
<br>
<input class="btn" type="submit" value="Next">
<br>
<br>
<div class="errorMessage">${message}</div> 
</form>
</div>
</body>
</html>
