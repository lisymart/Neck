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
<div id="center">
<h1> Neck </h1>
<form action="/Neck/success" method="POST"> 
<h2> Uploading was successful. </h2>
<br>
<br>
<input class="btn" type="submit" value="Upload another file">
</form>
</div>
</body>
</html>
