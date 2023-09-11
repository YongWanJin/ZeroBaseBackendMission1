<%@ page import = "mainPackage.DatabaseControl"%>
<%@ page import = "mainPackage.APICall"%>
<%@ page import = "org.json.simple.*" %>
<%@ page import = "org.json.simple.parser.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.net.*" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.sql.Date" %>


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>


<body>
	<%
		int deleteId = Integer.parseInt(request.getParameter("deleteId"));
		System.out.println(Integer.toString(deleteId));
		DatabaseControl dbControl = new DatabaseControl();
		dbControl.deleteSearchLog(deleteId);
	%>
	
	<%
		pageContext.forward("./History.jsp");
	%>

</body>

</html>