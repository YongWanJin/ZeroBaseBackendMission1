package mainPackage;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class APICall {
	
	/** result : 현재 url 호출 순서에서, startIndex에서 endIndex까지 뽑아온 데이터를 저장하는 json객체
	 * */
	public JSONObject result;
	
	/** allResult : result들을 모아둔 배열 객체
	 * */
	public ArrayList<JSONObject> allResult = new ArrayList<>();
	
	/** wifiNum : 전체 와이파이 개수
	 * */
	public static int wifiNum;
	
	public APICall(){}
	
	public void getData(String authKey, String startIndex, String endIndex) throws IOException {
		
    	// # 요청 url 작성
    	StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
    	// * 인증키
    	urlBuilder.append("/" + URLEncoder.encode(authKey, "UTF-8"));
    	// * 요청 파일 타입
    	urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8"));
    	// * api 서비스명
    	urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8"));
    	// * 요청한 페이지의 시작
    	urlBuilder.append("/" + URLEncoder.encode(startIndex, "UTF-8"));
    	// * 요청한 페이지의 끝
    	urlBuilder.append("/" + URLEncoder.encode(endIndex, "UTF-8"));
    	
    	
    	// # GET 메서드로 데이터 가져오기
    	URL url = new URL(urlBuilder.toString());
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setRequestMethod("GET");
    	conn.setRequestProperty("Content-type", "appplication/json");

    	
    	// # 데이터 성공적으로 받아왔는지 확인
    	// * 콘솔창 Response code가
    	// 200대이면 데이터를 성공적으로 받아왔다는 뜻
    	// 300대이면 추가적인 작업이 필요하다는 뜻
    	// 400대이면 클라이언트 쪽에서 잘못되어서 요청 처리 불가
    	// 500대이면 서버 쪽에서 문제가 있어서 요청 처리 불가
    	// https://www.whatap.io/ko/blog/40/
    	
    	// * Response code 출력
//    	System.out.println("Response code : " + conn.getResponseCode());
    	
    	// * 받아온 데이터를 String으로 조합
    	BufferedReader rd;
    	if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
    		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	} else {
    		rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
    	}
    	StringBuilder sb = new StringBuilder();
    	String line;
    	while( (line = rd.readLine()) != null ) {
    		sb.append(line);
    	}
    	rd.close();
    	conn.disconnect();
//    	System.out.println(sb.toString());
    	
    	
    	// # 받아온 데이터를 json 확장자로 변환
    	// 객체 result에 저장 후, 해당 경로에 파일로도 저장.
    	result = null;
    	try {
    		result = (JSONObject) new JSONParser().parse(sb.toString());
    		allResult.add(result);
//    		System.out.println("allResult 입력완료" + allResult.size());
    	} catch(ParseException e){
    		e.printStackTrace();
    	}
    	
    	
    	// # 전체 와이파이 개수 구하기
    	JSONObject temp = (JSONObject) allResult.get(0);
    	JSONObject temp2 = (JSONObject) temp.get("TbPublicWifiInfo");
    	if (temp2 != null) {
    		String longToStr = Long.toString((long)temp2.get("list_total_count"));
    		wifiNum = Integer.parseInt(longToStr);
    	}
    
    	// * json 파일 저장
//    	JSONObject TbPublicWifiInfo = (JSONObject) result.get("TbPublicWifiInfo");
//         	if(TbPublicWifiInfo != null) {
//        	FileWriter file = new FileWriter("C:\\Users\\PC\\Desktop\\Backend\\_88_1_Mission1\\ShowMeTheWIFI\\result" + startIndex + ".json");
//        	file.write(result.toJSONString());
//        	file.close();
//    	}
	}
	
}
