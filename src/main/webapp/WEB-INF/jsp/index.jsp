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
<a> Welcome to Neck web application. </a>
<br>
<a> Through this app you can upload log files to Elasticsearch. </a>
<br>
<a> Applications Logstash and Bro network monitor are necessary to be installed to use this app. </a>
<br>
<br>
<br>
<input class="btn" type="submit" value="Begin">
<div class="errorMessage">${message}</div> 
</form>
</center>
</body>
</html>

