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
		#bt{
			text-align: center;
		}
		#isNullText{
			text-align: center;
		}
	</style>
</head>


<body>

	<h1> 와이파이 정보 구하기 </h1>
	
	<a href ="./Result.jsp"> 홈 </a> |
	<a href = "./History.jsp"> 위치 히스토리 목록 </a> |
	<a id = "getAPI" href = "./getAPIDataWorking.jsp"> Open API 와이파이 정보 가져오기</a>
	<script>
		document.getElementById("getAPI").addEventListener('click', fun);
		function fun(){
			alert("서울시의 와이파이 정보를 로딩합니다. 정보 갱신시 수 분 정도 시간이 소요됩니다. \n(데이터 출처 : 서울 열린데이터 광장 Open API)")
		}
	</script>
	<br>
	<br>
	
	<form action = "./Result.jsp" method = "post" accept-charset = "utf-8" onsubmit = "return isNull();">
		LAT : <input type = "number" step = "any" id = "lat" name = "lat" value = "">,
		LNT : <input type = "number" step = "any" id = "lnt" name = "lnt" value = "">  
		<button type="button" id = "getUserLocation"> 내 위치 가져오기 </button>
		<input type = "submit" id = "submitInput" value = "근처 WIFI 정보 보기">
	</form>
	
	<script>
		function isNull(){
			let lat = document.getElementById("lat"); // 입력한 lat값 가져오기
			let lnt = document.getElementById("lnt"); // 입력한 lnt값 가져오기
			if(lat.value.length == 0 || lnt.value.length == 0){
				alert('현재 위치의 위도와 경도를 입력하세요!');
				return false; // form태그의 onsubmit 옵션에 false 전달 -> action 옵션 발동 안됨
			}
			return true; // form태그의 onsubmit 옵션에 true 전달 -> action 옵션 발동 안됨
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
	
	<!-- 결과 출력 테이블 -->
	<table>
		<!-- column (Table Head) -->
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
				<th> 검색일자 </th>
			</tr>
		</thead>
		
		<!-- 테이블 record (Table Body) -->
		<%
			// # 입력받은 위도, 경도값을 테이블 SearchLog에 Insert
			DatabaseControl dbControl = new DatabaseControl();
			double lat = -1;
			double lnt = -1;
					
			if(request.getParameter("lat") != null && request.getParameter("lnt") != null){
				lat = Double.parseDouble(request.getParameter("lat"));
				lnt = Double.parseDouble(request.getParameter("lnt"));
				dbControl.dbInsertSearchLog(lat, lnt);
			
				// # 반경 1km안에 있는 와이파이들을 선별
				dbControl.dbSelect(lat, lnt);
				
				// # 가장 가까운 와이파이 Top20 선별
				// (선별 결과를 dbControl 객체의 2차원 리스트 변수 results에 저장)
			    dbControl.dbSelectTop20(dbControl.distMap);
			}

		%>
		<tbody>
			<%
				if(lat == -1 && lnt == -1){
					String message = "<tr><td colspan = '17' id = 'isNullText'>"
							+ " 위치 정보를 입력한 후에 조회해 주세요."
							+ " </td></tr>";
					out.write(message);
				}
				else if(dbControl.results.get(0).size() == 0){
					String message = "<tr><td colspan = '17' id = 'isNullText'>"
							+ " 반경 1km내에 와이파이가 존재하지 않습니다."
							+ " 장소를 옮기거나, 서울시에 해당하는 위치 정보를 입력해 주십시오."
							+ " </td></tr>";
				    out.write(message);
				} else {
					for(int i = 0; i < dbControl.results.size(); i++){ %>
					<tr>
						<% for(String field : dbControl.results.get(i)){
							out.write("<td>" + field + "</td>");  
						}%>
					</tr>
						
				<%	}
				} %>
		</tbody>
	
	</table>
	

</body>

</html>