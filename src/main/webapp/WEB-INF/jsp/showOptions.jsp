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
		<form name="frm" action="/Neck/showOptions" method="POST" enctype="multipart/form-data" >
		<input type="hidden" name="store" value="${store}">
		<input type="hidden" name="stored" value="${stored}">
		<input type="hidden" name="ES" value="${ES}">
		<h1> Neck </h1>
		<div> Here are selected attributes from the first 1000 lines of each log. You can modify them. </div>
		<div class="gray">
		<a> File(s): </a> 
		<c:forEach var="name" items="${fileNames}">
			<a>- ${name} -</a>
			<input type="hidden" name="fileNames" value="${name}">
		</c:forEach>
		</div>
		<input type="file" name="importConfig" onchange="this.form.submit()" class="importCfg" id="importCfg">
		<label class="btn" for="importCfg">Import config file</label>
		<div class="errorMessage">${message}</div> 
		<br>
		<div class="gray">
		<div> Choose attributes and action: </div>
		<br>
		<div>
		<input class="btn" name="restore" type="submit" value="Restore" onClick="ShowLoading()">
		<input class="btn" name="addn" type="submit" value="Add new field" onClick="ShowLoading()">
		<br>
		<input class="btn" name="annm" type="submit" value="Anonymize" onClick="ShowLoading()">
		<input class="btn" name="dlt" type="submit" value="Delete" onClick="ShowLoading()">
		<input class="btn" name="rnm" type="submit" value="Rename" onClick="ShowLoading()">
		<input class="btn" name="rng" type="submit" value="Range" onClick="ShowLoading()">
		<input class="btn" name="ts" type="submit" value="TimeStamp" onClick="ShowLoading()">
		<input class="btn" name="lc" type="submit" value="LowerCase" onClick="ShowLoading()">
		<input class="btn" name="uc" type="submit" value="UpperCase" onClick="ShowLoading()">
		<br>
		<label><input type="checkbox" name="allCheck" onClick="selectallMe()">  select all </label>
		<ul class="params">
			<c:forEach var="item" items="${attributesList}">
				<li> <label><input type="checkbox" name="checked" value="${item}" onClick="selectall()"> ${item} </label></li>
				<input type="hidden" name="params" value="${item}">
			</c:forEach>
			<li> <label><input type="checkbox" name="checked" value="host" onClick="selectall()"> host </label>
			<input type="hidden" name="params" value="host"> </li>
			<li> <label><input type="checkbox" name="checked" value="message" onClick="selectall()"> message </label>
			<input type="hidden" name="params" value="message"> </li>
			<li> <label><input type="checkbox" name="checked" value="path" onClick="selectall()"> path </label>
			<input type="hidden" name="params" value="path"> </li>
			<li> <label><input type="checkbox" name="checked" value="@version" onClick="selectall()"> @version </label>
			<input type="hidden" name="params" value="@version"> </li>
		</ul>
		<a> Last 4 values are automatically added by ES. You can modify them too. </a>
		<br>
		<input class="btn" name="annm" type="submit" value="Anonymize" onClick="ShowLoading()">
		<input class="btn" name="dlt" type="submit" value="Delete" onClick="ShowLoading()" >
		<input class="btn" name="rnm" type="submit" value="Rename" onClick="ShowLoading()">
		<input class="btn" name="rng" type="submit" value="Range" onClick="ShowLoading()">
		<input class="btn" name="ts" type="submit" value="TimeStamp" onClick="ShowLoading()">
		<input class="btn" name="lc" type="submit" value="LowerCase" onClick="ShowLoading()">
		<input class="btn" name="uc" type="submit" value="UpperCase" onClick="ShowLoading()">
		</div>
		<br>
		</div>
		<br>
		<div class="gray">
		<c:if test="${not empty timeStamp}">
		<input type="hidden" name="timeStamp" value="${timeStamp}">
		<p> new Time Stamp is    <b><a style="color:green"><u>${timeStamp}</u></a></b>    field.<br> (Choose its format e.g.: "UNIX", "ISO8601", "dd/MMM/yyyy:HH:mm:ss":
			<input type="text" name="timeStampFormat" value="${tsFormat}" autofocus = "autofocus" required = "required">
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
				<tr> <td>${item.key}</td> <td> <input type="text" name="rnm${item.key}" value="${item.value}"></td> </tr>
				<input type="hidden" name="rename" value="${item.key}">
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
		<tr><td></td><td>from (empty = -inf)</td><td>to (empty = +inf)</td></tr>
			<c:forEach var="item" items="${rangeList}">
				<input type="hidden" name="range" value="${item.key}">
				<tr> <td>${item.key}</td>
				<c:if test="${not empty item.value}">
					<c:forEach var="i" items="${item.value}">
						<td> <input type="text" name="rng${item.key}" value = "${i}"> </td>
					</c:forEach>
				</c:if>
				<c:if test="${empty item.value}">
					<td> <input type="text" name="rng${item.key}"> </td>
					<td> <input type="text" name="rng${item.key}"> </td>
				</c:if>			 	
				</tr>
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
		and hashing key
		<input type="text" name="hashingKey" value = "${hashingKey}">
		)</h4>
		<ul>
			<c:forEach var="item" items="${anonymList}">
				<li>	${item} <input type="hidden" name="anonym" value="${item}" required = "required"> </li>
			</c:forEach>
		</ul>
		</c:if>
		</div>
		<div class="gray">
		<c:if test="${not empty addFieldList}">
		<h4>Add new field:</h4>
		<table class="table1">
		<tr>
			<td>field name</td><td>field value</td>
		</tr>
			<c:forEach var="item" items="${addFieldList}">
				<tr>
					<td><input type="text" name="fn${item.key}" value="${item.key}"></td>
					<td><input type="text" name="fv${item.key}" value="${item.value}"></td>
				</tr>
				<input type="hidden" name="fn" value="${item.key}">
			</c:forEach>
		</table>
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
		<input type="submit" class="btn" name="exportCfg" value="Download created config file">
		<input id="button-upload" class="btn" name="uploadToES" type="submit" value="Upload to ES" onClick="ShowLoading()">
		<br>
		<br>
		</form>
	</div>
</body>
</html>

