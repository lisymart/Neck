<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="styles.css">
<title>Neck web app</title>
</head>
<body>
<center>
<h1> Neck web application </h1>
<form action="ServletLoadFile" method="post"> 
<br>
<table class="table1">
<tr>
<td><a> Enter your file location (../file.pcap) </a></td>
<td><input type="text" name="processFilePath" /></td>
</tr>
<tr>
<td></td>
<td> <input class="btn" type="submit" value="Next"> </td>
</tr>
</table>
<div class="errorMessage">${message}</div> 
</form>
</center>
</body>
</html>
