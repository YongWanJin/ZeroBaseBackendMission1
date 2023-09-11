<%@ page import = "mainPackage.DatabaseControl"%>
<%@ page import = "mainPackage.APICall"%>
<%@ page import = "org.json.simple.*" %>
<%@ page import = "org.json.simple.parser.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.net.*" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.sql.Date" %>
<%@ page import = "java.util.regex.*" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>와이파이 정보 구하기</title>
	<style>
		table{
			width: 100%;
			border-collapse: collapse;
		}
		tr, td, th {
			border: solid 1px #b4b4b4;
			padding: 8px;
  			vertical-align: middle;
		}
		tr:nth-child(even){
			background-color: #f2f2f2;
		}
		th{
			background-color: #46BD7B;
		}
		#delete25{
			text-align: middle;
		}
	</style>
</head>


<body>
	<h1>위치 히스토리 목록</h1>
	<%
		String referer = (String)request.getHeader("REFERER"); // 이전페이지의 url 주소
		Pattern urlPat = Pattern.compile(".*\\/Home\\.jsp");
		Matcher mat = urlPat.matcher(referer);
		
		if(mat.matches()){
			out.write("<a href ='./Home.jsp'> 홈 </a> |");
		} else {
			out.write("<a href ='./Result.jsp'> 홈 </a> |");
		}
	%>
	<a href = './History.jsp'> 위치 히스토리 목록 </a> |
	<a id = "getAPI" href = "./getAPIData.jsp"> Open API 와이파이 정보 가져오기</a>
	<script>
		document.getElementById("getAPI").addEventListener('click', fun);
		function fun(){
			alert("서울시의 와이파이 정보를 로딩합니다. 정보 갱신시 수 분 정도 시간이 소요됩니다. \n(데이터 출처 : 서울 열린데이터 광장 Open API)");
		}
	</script>
	<br>
	<br>
	
	<table>
		<thead>
			<tr>
				<th> ID </th>
				<th> X좌표 </th>
				<th> Y좌표 </th>
				<th> 조회일자 </th>
				<th> 비고 </th>
			</tr>
		</thead>
		
		<!--  DB 불러오기 -->
		<%
			DatabaseControl dbControl = new DatabaseControl();
			dbControl.dbSelectLog();
		%>
		
		<!-- 불러온 데이터 출력 -->
		<tbody>
			<% for(int i = dbControl.history.size()-1; i >= 0; i--){ %>
				<tr>
					<% for(String field : dbControl.history.get(i)) {
						out.write("<td>" + field + "</td>");
					}%>
					
					<td>
						<form action = './deleteHistory.jsp' method = 'post' accept-charset = 'utf-8'>
							<input type = 'hidden' name = 'deleteId' value = '<%out.write(Integer.toString(i+1));%>'>
							<input type = 'submit' value = '삭제'>
						</form>
					</td>
				</tr>
			<% } %>
			<tr>
			</tr>
		</tbody>
	</table>




</body>
</html>