<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<br>
<h4> You can upload multiple files to process here.<br> But please, upload files of the same type.<br> Supported are .pcap .txt .json and .log  </h4>
<form action="/Neck/loadFile" method="POST" enctype="multipart/form-data" onsubmit="ShowLoading()">
<input type="hidden" name="ES" value="${ES}">
<img src="${pageContext.request.contextPath}/resources/spinner.gif" style="display: none;" id="loading_image">
<br>
<a><label>(store on server <input type="checkbox" name="store" value="store">)</label></a>
<input type="file" name="filesToUpload" multiple="multiple">
<br>
<br>
<a>(or)</a>
<br>
<br>
<input class="btn" type="submit" name="chooseFile" value="Choose stored file">
<br>
<br>
<div class="gray">
		<c:if test="${not empty fileList}">
		<h4> Previously uploaded files: </h4>
		<ul class="params">	
			<c:forEach var="item" items="${fileList}">
				<li> <label><input type="checkbox" name="checked" value="${item}"> ${item} </label></li>
				<input type="hidden" name="files" value="${item}">
			</c:forEach>
		</ul>
		<input class="btn" type="submit" name="delete" value="Delete selected">
		</c:if>
		</div>
		<br>
<div id="spinner" style="display: none"><img src="/Neck/resources/spinner.gif"/><br> Processing, please wait.</div>
<div id="overlay" style="display: none"> </div>
<br>
<input id="button-upload" class="btn" name="continue" type="submit" value="Next">
<br>
<br>
<div class="errorMessage">${message}</div> 
</form>
</div>
</body>
</html>
