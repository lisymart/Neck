<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<link href="${pageContext.request.contextPath}/resources/styles.css" rel="stylesheet" type="text/css"/>
	<title> Neck </title>
</head> 
<body>
	<div id="center">
		<form action="/Neck/pcap" method="POST"> 
		<h1> Neck </h1>
		<h4> Here are selected attributes from the first 1000 lines of each log. You can modify them. </h4>
		<c:forEach var="name" items="${fileNames}">
			<a>- ${name} -</a>
			<input type="hidden" name="${name}">
		</c:forEach>
		<br><br>
			<input class="btn" name="rename" type="submit" value="Rename">
			<br><br>
			<div> Choose attributes to process: </div>
			<ul>	
				<c:forEach var="item" items="${attributesList}">
					<li> <input type="checkbox" name="checked" value="${item}"> ${item} </li>
					<input type="hidden" name="attributes" value="${item}">
				</c:forEach>
			</ul>
			<br>
			<h4> Renaming: </h4>
			<table class="table1">
			<tr><td> Original name </td><td> Your name </td></tr>	
				<c:forEach var="item" items="${renameList}">
					<tr> <td>${item}</td> <td> <input type="text" name="${item}"></td> </tr>
				</c:forEach>
			</table>
			<h5> And here you can add any other logstash configuration. Please use the whole filter configuration e.g.:  </h5>
			<i> mutate{rename => {"fieldName" => "newName"}} </i>
			<br>
			<br>
			<textarea class="addition" name="addition" rows=10>
			</textarea>
			<br>
			<br>
			<input class="btn" name="uploadToES" type="submit" value="Upload to ES">
		</form>
	</div>
</body>
</html>

