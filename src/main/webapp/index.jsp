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
<form action="/Neck/index" method="POST"> 
<table class="table1">
<tr>
<td>
    <a> Welcome to Neck web application. Through this app you can upload log files to Elasticsearch. </a>
    <br>
    <a> Applications Logstash and Bro network monitor are necessary to be installed to use this app. </a>
</td>
</tr>
<tr></tr>
<tr>
<td> <input class="btn" type="submit" value="Begin"> </td>
</tr>
</table>
<div class="errorMessage">${message}</div> 
</form>
</center>
</body>
</html>

