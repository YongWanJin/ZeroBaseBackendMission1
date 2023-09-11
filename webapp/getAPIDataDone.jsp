<%@ page import = "mainPackage.DatabaseControl"%>
<%@ page import = "mainPackage.APICall"%>
<%@ page import = "org.json.simple.*" %>
<%@ page import = "org.json.simple.parser.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.net.*" %>


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>와이파이 정보 구하기</title>
	
	<style>
		div{
			text-align : center;
		}
	</style>
</head>



<body>

	<div> <h2> <% out.write(Integer.toString((int)request.getAttribute("wifiNum"))); %>개의 WIFI 정보를 정상적으로 저장하였습니다. </h2> </div>
	<br>
	
	<div> <a href = "./Result.jsp" > 홈으로 가기  </a> </div>

</body>
</html>