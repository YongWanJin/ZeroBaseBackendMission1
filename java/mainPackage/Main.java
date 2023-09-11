package mainPackage;

// 서울시 공공와이파이 서비스 위치 정보
// https://data.seoul.go.kr/dataList/OA-20883/S/1/datasetView.do

import java.io.*;
import org.json.simple.*;

public class Main {

    public static void main(String[] args) throws IOException{
    
    	// # API 호출 준비
    	APICall apiCall = new APICall();
    	int startIndex = 20;
    	int pageNum = 10;
    	String authKey = "6a6b544f5877687539366777495463";
    	
    	// #  API 호출 시작
    	while(true) {
    		apiCall.getData(authKey, Integer.toString(startIndex), Integer.toString(startIndex + pageNum - 1));
    		
        	// # 데이터 호출 종료 조건
        	JSONObject TbPublicWifiInfo = (JSONObject) apiCall.result.get("TbPublicWifiInfo");
        	if(TbPublicWifiInfo == null) {
        		System.out.println("더이상 데이터가 존재하지 않습니다. 호출 및 csv 저장을 종료합니다.");
        		break;
        	}
        	
//        	if(startIndex == 50) { // test
//        		break;
//        	}
//        	
        	System.out.println(startIndex + " ~ " + Integer.toString(startIndex + pageNum - 1) + " 데이터 호출 성공");
    		startIndex += pageNum;
    	}
    	
    	// # DB에 저장
    	DatabaseControl dbControl = new DatabaseControl();
    	dbControl.dbInsert(apiCall.allResult);

    	
    	
//    	// DB 연동 테스트
//    	DatabaseControl dbControl = new DatabaseControl();
//    	dbControl.dbSelect();
    	
    	
    }
}

