<%@ page contentType="text/html" pageEncoding="UTF-8"%>
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
		<form name="frm" action="/Neck/showOptions" method="POST" onsubmit="ShowLoading()">
		<input type="hidden" name="store" value="${store}">
		<input type="hidden" name="stored" value="${stored}">
		<input type="hidden" name="ES" value="${ES}">
		<h1> Neck </h1>
		<div> Here are selected attributes from the first 1000 lines of each log. You can modify them. </div>
		<div class="errorMessage">${message}</div> 
		<div class="gray">
		<a> File(s): </a> 
		<c:forEach var="name" items="${fileNames}">
			<a>- ${name} -</a>
			<input type="hidden" name="fileNames" value="${name}">
		</c:forEach>
		</div>
		<br>
		<div class="gray">
		<div> Choose attributes and action: </div>
		<br>
		<div>
		<input class="btn" name="restore" type="submit" value="Restore">
		<br>
		<input class="btn" name="annm" type="submit" value="Anonymize">
		<input class="btn" name="dlt" type="submit" value="Delete">
		<input class="btn" name="rnm" type="submit" value="Rename">
		<input class="btn" name="rng" type="submit" value="Range">
		<input class="btn" name="ts" type="submit" value="TimeStamp">
		<input class="btn" name="lc" type="submit" value="LowerCase">
		<input class="btn" name="uc" type="submit" value="UpperCase">
		<br>
		<input type="checkbox" name="allCheck" onClick="selectallMe()">  select all
		<ul class="params">
			<c:forEach var="item" items="${attributesList}">
				<li> <input type="checkbox" name="checked" value="${item}" onClick="selectall()"> ${item} </li>
				<input type="hidden" name="params" value="${item}">
			</c:forEach>
		</ul>
		<input class="btn" name="annm" type="submit" value="Anonymize">
		<input class="btn" name="dlt" type="submit" value="Delete">
		<input class="btn" name="rnm" type="submit" value="Rename">
		<input class="btn" name="rng" type="submit" value="Range">
		<input class="btn" name="ts" type="submit" value="TimeStamp">
		<input class="btn" name="lc" type="submit" value="LowerCase">
		<input class="btn" name="uc" type="submit" value="UpperCase">
		</div>
		<br>
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty timeStamp}">
		<input type="hidden" name="timeStamp" value="${timeStamp}">
		<p> new Time Stamp is    <b><a style="color:green"><u>${timeStamp}</u></a></b>    field.<br> (Choose its format e.g.: "UNIX", "ISO8601", "dd/MMM/yyyy:HH:mm:ss":
			<input type="text" name="timeStampFormat" value="ISO8601" autofocus = "autofocus" required = "required">
			)
		</p>
		</c:if>
		</div>
		<br>
		<div class="gray">
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
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty rangeList}">
		<h4> Ranges set: </h4>
		<table class="table1">
		<tr><td><b> Attribute </b></td><td colspan="2"><b> Ranges </b></td></tr>	
			<c:forEach var="item" items="${rangeList}">
				<tr> <td>${item}</td> <td> from: <input type="text" name="${item}from"> </td> <td> to: <input type="text" name="${item}to"></td> </tr>
				<input type="hidden" name="range" value="${item}">
			</c:forEach>
		</table>
		</c:if>
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty deleteList}">
		<h4> Removing: </h4>
		<ul>
			<c:forEach var="item" items="${deleteList}">
				<li>	${item} <input type="hidden" name="delete" value="${item}"> </li>
			</c:forEach>
		</ul>
		</c:if>
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty uppercaseList}">
		<h4> UpperCase: </h4>
		<ul>
			<c:forEach var="item" items="${uppercaseList}">
				<li>	${item} <input type="hidden" name="uppercase" value="${item}"> </li>
			</c:forEach>
		</ul>
		</c:if>
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty lowercaseList}">
		<h4> LowerCase: </h4>
		<ul>
			<c:forEach var="item" items="${lowercaseList}">
				<li>	${item} <input type="hidden" name="lowercase" value="${item}"> </li>
			</c:forEach>
		</ul>
		</c:if>
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty anonymList}">
		<h4> Anonymize: (Choose algorithm: 
		<select name="annmAlgo"> 
				<option value="SHA1"> SHA1 </option>
				<option value="SHA256"> SHA256 </option>
				<option value="SHA384"> SHA384 </option>
				<option value="SHA512"> SHA512 </option>
				<option value="MD5"> MD5 </option>
		</select>
		)</h4>
		<ul>
			<c:forEach var="item" items="${anonymList}">
				<li>	${item} <input type="hidden" name="anonym" value="${item}"> </li>
			</c:forEach>
		</ul>
		</c:if>
		</div>
		<br>
		<div class="gray">
		<a> And here you can add any other logstash configuration. <br> Please use the whole filter configuration e.g.:  </a>
		<br>
		<i> mutate{rename => {"fieldName" => "newName"}} </i>
		<br>
		<textarea class="addition" name="addition" rows=10>
		</textarea>
		<br>
		<br>
		</div>
		<br>
		<div id="spinner" style="display: none"><img src="/Neck/resources/spinner.gif"/><br> Processing, please wait.</div>
		<div id="overlay" style="display: none"> </div>
		<br>
		<input id="button-upload" class="btn" name="uploadToES" type="submit" value="Upload to ES">
		<br>
		<br>
		
		</form>
	</div>
</body>
</html>

