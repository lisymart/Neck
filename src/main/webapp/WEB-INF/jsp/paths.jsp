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
<form action="/Neck/paths" method="POST"> 
<table class="table1">
    <tr>
        <td><a>Enter your Bro installation path (../bro/bin/bro)</a></td>
        <td><input type="text" name="broPath"></td>
    </tr>
    <tr>
        <td><a> Enter your Logstash installation path (../logstash/bin/logstash) </a></td>
        <td><input type="text" name="logstashPath"></td>
    </tr>
</table>
<br>
<br>
<input class="btn" type="submit" value="Save">
<div class="errorMessage">${message}</div> 
</form>
</div>
</body>
</html>

