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
<br>
<h4> You can upload multiple files to process here.<br> But please, upload files of the same type.<br> Supported are .pcap and .csv </h4>
<form action="/Neck/loadFile" method="POST" enctype="multipart/form-data"> 
<br>
<table class="table1">
<tr>
<td><a> Browse your files to process: </a></td>
<td><input type="file" name="filesToUpload" multiple="multiple"></td>
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
