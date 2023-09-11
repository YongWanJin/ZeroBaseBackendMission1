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
		#isNullText{
			text-align: center;
		}
	</style>

</head>

<body>
	<h1> 와이파이 정보 구하기 </h1>
	
	<a href ="./Home.jsp"> 홈 </a> |
	<a href = "./History.jsp"> 위치 히스토리 목록 </a> |
	<a id = "getAPI" href = "./getAPIDataWorking.jsp"> Open API 와이파이 정보 가져오기</a>
	<script>
		document.getElementById("getAPI").addEventListener('click', fun);
		function fun(){
			alert("서울시의 와이파이 정보를 로딩합니다. 정보 갱신시 수 분 정도 시간이 소요됩니다. \n(데이터 출처 : 서울 열린데이터 광장 Open API)");
		}
	</script>
	<br>
	<br>
	
	<form action = "./Result.jsp" method = "post" accept-charset = "utf-8" onsubmit = "return isNull();" >
		LAT : <input type = "number" step = "any" id = "lat" name = "lat" value = "">,
		LNT : <input type = "number" step = "any" id = "lnt" name = "lnt" value = "">  
		<button type="button" id = "getUserLocation"> 내 위치 가져오기 </button>
		<input type = "submit" id = "submitInput" value = "근처 WIFI 정보 보기">
	</form>
	
	<script>
		function isNull(){
			alert('검색을 원하시면 먼저 Open API 와이파이 정보를 가져오십시오.');
			return false;
		}
	</script>
	
	<script>
		function success({ coords, timestamp }) {
		    const latitude = coords.latitude;   // 위도
		    const longitude = coords.longitude; // 경도
		    
		    document.getElementById("lat").setAttribute('value', latitude);
		    document.getElementById("lnt").setAttribute('value', longitude);
		}
		
		function getUserLocation() {
		    if (!navigator.geolocation) {
		        throw "위치 정보가 지원되지 않습니다.";
		    }
		    navigator.geolocation.getCurrentPosition(success);
		}
		
		document.getElementById("getUserLocation").addEventListener('click', getUserLocation);
	</script>
	
	
	<br>

	
	<table>
	
		<!-- 테이블 column (Table Head) -->
		<thead>
			<tr>
				<th> 거리 (km) </th>
				<th> 관리번호 </th>
				<th> 자치구 </th>
				<th> 와이파이명</th>
				<th> 도로명주소</th>
				<th> 상세주소</th>
				<th> 설치위치(층)</th>
				<th> 설치유형</th>
				<th> 설치기관</th>
				<th> 서비스구분</th>
				<th> 망종류 </th>
				<th> 설치년도 </th>
				<th> 실내외구분 </th>
				<th> WIFI접속환경</th>
				<th> X좌표</th>
				<th> Y좌표</th>
				<th> 정보갱신일자 </th>
			</tr>
		</thead>
		
		<!-- 테이블 record (Table Body) -->
		<tbody>
			<tr>
				<td colspan = "17" id = "isNullText"> 위치 정보를 입력한 후에 조회해 주세요. </td>
			</tr>
		</tbody>
	
	</table>
	
	
	<%

	%>

</body>
</html>