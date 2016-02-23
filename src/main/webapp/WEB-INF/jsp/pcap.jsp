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
		<div> Here are selected attributes from the first 1000 lines of each log. You can modify them. </div>
		<div class="errorMessage">${message}</div> 
		<a> File(s): </a> 
		<c:forEach var="name" items="${fileNames}">
			<a>- ${name} -</a>
			<input type="hidden" name="fileNames" value="${name}">
		</c:forEach>
		<br>
			<div> Choose attributes and action: </div>
			<br>
			<div>
			<input class="btn" name="rnm" type="submit" value="Rename">
			<input class="btn" name="dlt" type="submit" value="Delete">
			<input class="btn" name="ts" type="submit" value="TimeStamp">
			<input class="btn" name="uc" type="submit" value="UpperCase">
			<input class="btn" name="lc" type="submit" value="LowerCase">
			<ul>	
				<c:forEach var="item" items="${attributesList}">
					<li> <input type="checkbox" name="checked" value="${item}"> ${item} </li>
					<input type="hidden" name="params" value="${item}">
				</c:forEach>
			</ul>
			<input class="btn" name="rnm" type="submit" value="Rename">
			<input class="btn" name="dlt" type="submit" value="Delete">
			<input class="btn" name="ts" type="submit" value="TimeStamp">
			<input class="btn" name="uc" type="submit" value="UpperCase">
			<input class="btn" name="lc" type="submit" value="LowerCase">
			</div>
			<c:if test="${not empty timeStamp}">
			<input type="hidden" name="timeStamp" value="${timeStamp}">
			<h4> Time Stamp is now --<b>  ${timeStamp}  </b>-- field. Choose its format :
				<select name="timeStampFormat">
					<option value="ISO8601"> ISO8601 </option>
					<!-- <option value="YYYY/MM/dd HH:mm:ss"> 2015/04/14 09:32:01 </option>
					<option value="dd.MM.YYYY HH:mm:ss"> 14.04.2015 09:32:01</option>
					<option value="YYYY MMM dd HH:mm:ss"> 2015 Apr 17 09:32:01 </option> -->
				</select>
			</h4>
			</c:if>
			<c:if test="${not empty renameList}">
			<h4> Renaming: </h4>
			<table class="table1">
			<tr><td><b> Original name </b></td><td><b> Your name </b></td></tr>	
				<c:forEach var="item" items="${renameList}">
					<tr> <td>${item}</td> <td> <input type="text" name="${item}"></td> </tr>
					<input type="hidden" name="rename" value="${item}">
				</c:forEach>
			</table>
			</c:if>
			<c:if test="${not empty deleteList}">
			<h4> Removing: </h4>
			<ul>
				<c:forEach var="item" items="${deleteList}">
					<li>	${item} <input type="hidden" name="delete" value="${item}"> </li>
				</c:forEach>
			</ul>
			</c:if>
			<c:if test="${not empty uppercaseList}">
			<h4> UpperCase: </h4>
			<ul>
				<c:forEach var="item" items="${uppercaseList}">
					<li>	${item} <input type="hidden" name="uppercase" value="${item}"> </li>
				</c:forEach>
			</ul>
			</c:if>
			<c:if test="${not empty lowercaseList}">
			<h4> LowerCase: </h4>
			<ul>
				<c:forEach var="item" items="${lowercaseList}">
					<li>	${item} <input type="hidden" name="lowercase" value="${item}"> </li>
				</c:forEach>
			</ul>
			</c:if>
			<a> And here you can add any other logstash configuration. <br> Please use the whole filter configuration e.g.:  </a>
			<br>
			<i> mutate{rename => {"fieldName" => "newName"}} </i>
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

