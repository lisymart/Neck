<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${pageContext.request.contextPath}/resources/styles.css" rel="stylesheet" type="text/css"/>
<script src="${pageContext.request.contextPath}/resources/scripts.js" type="text/javascript"></script> 
<title> Neck </title>
</head>
<body>
<div id="center">
<h1> Neck </h1>
<form action="/Neck/index" method="POST" onsubmit="ShowLoading()"> 
<a> Welcome to Neck web application. </a>
<br>
<a> Through this app you can upload pcap and log files to Elasticsearch. </a>
<br>
<a> Applications Logstash and Bro are necessary to be installed to use this app. </a>
<br>
<a> First run may last longer due to several installations check. </a>
<br>
<a> If any error occurs, please fix it and restart the application (and terminal too). </a>
<br>
<br>
<h4> Enter your ElasticSearch location: </h4>
<input type="text" name="EShost" value="localhost:9200" autofocus = "autofocus" required = "required">
<br>
<br>
<div id="spinner" style="display: none"><img src="/Neck/resources/spinner.gif"/><br> Processing, please wait.</div>
<div id="overlay" style="display: none"> </div>
<br>
<input class="btn" type="submit" value="Begin">
<br>
<br>
<div class="errorMessage">${message}</div> 
</form>
</div>
</body>
</html>

