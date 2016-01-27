<%-- 
    Document   : progress
    Created on : Dec 8, 2015, 3:09:02 AM
    Author     : a
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="refresh" content="5" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="${pageContext.request.contextPath}/resources/styles.css" rel="stylesheet" type="text/css"/>
        <title> Neck </title>
    </head>
    <body>
<center>
<h1> Neck </h1>
<form action="/Neck/progress" method="POST"> 
<table class="table1">
<tr>
<td><a> Check the progress of uploading: </a></td>
<td>  </td>
</tr>
<tr>
<td> <div class="progress">${progress}</div> </td>
<td> </td>
</tr>
</table>
<br>
<br>
<input class="btn" type="submit" value="Upload another file">
</form>
</center>
</body>
</html>
