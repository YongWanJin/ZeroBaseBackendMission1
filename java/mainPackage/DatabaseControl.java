package mainPackage;

import java.sql.*;
import java.sql.Timestamp;
import java.sql.Date;
import org.json.simple.*;
import java.util.*;

public class DatabaseControl {
	
	/**# distMap : key값은 와이파이의 일련번호, 
	 * value값은 해당 와이파이와 검색한 위치 사이의 거리(km)를 담은 해시테이블. 
	 * 매서드 dbSelect에서 선별된 와이파이들을 저장한다. 
	 * (key : manageCode, value : dist)
	 * */
	public HashMap<String, Double> distMap = new HashMap<>();
	
	
	/**# results : 현 위치와 가장 가까운 와이파이 20개를 담은 2차원 리스트. 
	 * 매서드 dbSelectTop20에서 선별된 와이파이를 저장하며, 
	 * 서비스 이용자에게 가장 중요하게 보여질 정보들이 담겨있다. 
	 * */
	public ArrayList<LinkedList<String>> results = new ArrayList<>();
	

	/**# history : 어떤 위치에서 검색을 했는지에 대한 이력을 담은 2차원 리스트.
	 * 매서드 dbSelectLog에서 select한 결과를 저장한다.
	 * */
	public ArrayList<LinkedList<String>> history = new ArrayList<>();
	
	/**# isNumCorrect : 테이블 WIFILIST에 현재 저장된 데이터의 개수와 
	 * API로부터 호출된 데이터의 개수가 일치하는지 여부를 boolean 자료형으로 나타낸 변수. 
	 * 매서드 dbNumCheck의 리턴값을 담고있다. 
	 * */
	public static boolean isNumCorrect = false;
	
	
	public DatabaseControl(){}
	
	// ================================================================== // 
	
	
	/** dbNumCheck : 현재 테이블 WIFILIST의 데이터 개수가 정상적인지 확인하는 메서드. 
	 * 개수가 정상이면 true를, 그렇지 않으면 false를 리턴한다. false 리턴시 초기화 요망.
	 * */
	public boolean dbNumCheck() {
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        try {
        	connection = DriverManager.getConnection(url, dbUserId, dbUserPw);
        	int cnt = 0;
            String sql = "select count(*) from WIFILIST";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while(rs.next()) {
            	cnt = rs.getInt("count(*)");
            }
            if (cnt == APICall.wifiNum) {
            	return true;
            } else {
            	return false;
            }
        	
        } catch (SQLException e) {
            throw new RuntimeException(e);

        // 5. DB 관련 객체 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
        }
	}
	
	// ================================================================== // 
	
	
	//  [ 데이터 입력 (테이블 WIFI LIST) ]

	/** 메서드 dbInsert : API로 받아온 데이터를 데이터베이스의 테이블 WIFILIST에 저장
	 * */
	public void dbInsert(ArrayList<JSONObject> allJson) {
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        // # 쿼리 적용
        try {
            connection = DriverManager.getConnection(url, dbUserId, dbUserPw);
            
            // # 테이블 초기화
            isNumCorrect = dbNumCheck();
        	if (isNumCorrect) {
        		connectionClose(connection);
        		return; // 데이터 개수가 맞으면 굳이 이 메서드를 톨릴 필요가 없음. 종료.
        	} else {
            	String sql = "delete from WIFILIST";
            	statement = connection.prepareStatement(sql);
            	int affected = statement.executeUpdate();
            	if(affected == 0) {
            		System.out.println("초기화 실패");
            	}
            	statClose(statement);
        	}
            
            // # 테이블에 데이터 추가
            for(JSONObject jsonObj : allJson) {
            	JSONObject TbPublicWifiInfo = (JSONObject) jsonObj.get("TbPublicWifiInfo");
            	if (TbPublicWifiInfo == null) continue;
            	ArrayList<JSONObject> rows = (ArrayList) TbPublicWifiInfo.get("row");
            	
            	for(JSONObject row : rows) {
//            		statement = null;
            		String manageCode = (String) row.get("X_SWIFI_MGR_NO");
            		String region = (String) row.get("X_SWIFI_WRDOFC");
            		String name = (String) row.get("X_SWIFI_MAIN_NM");
            		String address = (String) row.get("X_SWIFI_ADRES1");
            		String addressDetail = (String) row.get("X_SWIFI_ADRES2");
            		String floor = (String) row.get("X_SWIFI_INSTL_FLOOR");
            		String constructType  = (String) row.get("X_SWIFI_INSTL_TY");
            		String constructInst = (String) row.get("X_SWIFI_INSTL_MBY");
            		String serviceType = (String) row.get("X_SWIFI_SVC_SE");
            		String network = (String) row.get("X_SWIFI_CMCWR");
            		int sinceYear = Integer.parseInt((String) row.get("X_SWIFI_CNSTC_YEAR"));
            		String inOutDoor = (String) row.get("X_SWIFI_INOUT_DOOR");
            		String env = (String) row.get("X_SWIFI_REMARS3");
            		double xPos = Double.parseDouble((String) row.get("LNT"));
            		double yPos = Double.parseDouble((String) row.get("LAT"));
            		String searchDate = (String) row.get("WORK_DTTM"); // 보류
            		
                    String sql = "insert into WIFILIST"
                    		+ " (ManageCode, Region, Name, Address, AddressDetail, Floor"
                    		+ ", ConstructType, ConstructInst, ServiceType, Network, SinceYear"
                    		+ ", InOutDoor, Env, Xpos, Ypos, SearchDate)"
                    		+ " values"
                    		+ " (?, ?, ?, ?, ?, ?" // 1~6
                    		+ ", ?, ?, ?, ?, ?" // 7 ~ 11
                    		+ ", ?, ?, ?, ?, ?);"; // 12 ~ 16
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, manageCode);
                    statement.setString(2, region);
                    statement.setString(3, name);
                    statement.setString(4, address);
                    statement.setString(5, addressDetail);
                    statement.setString(6, floor);
                    statement.setString(7, constructType);
                    statement.setString(8, constructInst);
                    statement.setString(9, serviceType);
                    statement.setString(10, network);
                    statement.setInt(11, sinceYear);
                    statement.setString(12, inOutDoor);
                    if(env.length() <= 30) {
                    	statement.setString(13, env);
                    } else { // Env column에서 자꾸만 길이 초과 에러가 발생함
                    	statement.setString(13, ""); 
                    }
                    statement.setDouble(14, xPos);
                    statement.setDouble(15, yPos);
                    statement.setString(16, searchDate);
                    
                    // # 쿼리 적용 잘 되었는지 확인
                    int affected = statement.executeUpdate();
                    if(affected <= 0){
                       System.out.println("테이블에 데이터 저장 실패");
                    }
            	}
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        // 5. DB 관련 객체 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
        }
	}
	
	// ================================================================== // 
	
	
	//  [ 데이터 입력 (테이블 SearchLog) ]
	//  어느 위치에서 검색을 했는지 로그를 남김
	
	public void dbInsertSearchLog(double xPos, double yPos) {
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        
        // # 쿼리 적용
        try {
        	connection = DriverManager.getConnection(url, dbUserId, dbUserPw);
        	
        	Date date = new Date(System.currentTimeMillis());
        	Timestamp now = new Timestamp(date.getTime());
        	
        	String sql = "insert into SearchLog"
        			+ " (Xpos, Ypos, SearchDate)"
        			+ " values"
        			+ " (?, ?, ?)";
        	statement = connection.prepareStatement(sql);
        	statement.setDouble(1, xPos);
        	statement.setDouble(2, yPos);
        	statement.setTimestamp(3, now);
        	
            // # 쿼리 적용 잘 되었는지 확인
            int affected = statement.executeUpdate();
            if(affected <= 0){
               System.out.println("데이터 입력 실패");
            }
        	
        } catch (SQLException e) {
        	throw new RuntimeException(e);
        
        
        // # DB 관련 객체 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
        }
	}
	
	// ================================================================== // 
	
	
	//  [ 데이터 선별 (테이블 WIFI LIST) ]
	//  - 입력받은 위도 경도 값(테이블 SearchLog에 저장되어있는 Xpos, Ypos)을 기준으로
	//  반경 1km안에 있는 와이파이들을 선별
	//  - DB의 모든 데이터의 거리를 구하지 않음으로써 속도 향상
	
	public void dbSelect(double lat, double lnt) {
		
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        // # select된 record들을 담을 해시테이블 생성
        // key : manageCode, value : dist
        this.distMap = new HashMap<>();
        
        // # 쿼리 적용
        try {
            connection = DriverManager.getConnection(url, dbUserId, dbUserPw);
            String sql = "select ManageCode, Name, Xpos, Ypos from WIFILIST"
            		+ " where ABS( ? - Xpos) <= 0.012 and ABS( ? - Ypos) <= 0.01";
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, lat);
            statement.setDouble(2, lnt);

            // # select된 record들을 해시테이블 distMap에 저장
            rs = statement.executeQuery();
            while (rs.next()) {
            	String manageCode = rs.getString("ManageCode");
            	double xPos = rs.getDouble("Xpos");
            	double yPos = rs.getDouble("Ypos");
            	System.out.print("xPos : " + xPos + "  yPos : " + yPos + "  ");
            	// 현 위치와 와이파이 위치 사이의 거리(km) 계산
            	double dist = GeoFunction.getDistance(lat, lnt, xPos, yPos);
            	this.distMap.put(manageCode, dist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        // # DB 관련 객체 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
        }
	}
	
	// ================================================================== // 
	
	
	//  [ Top20 선별 (테이블 WIFI LIST) ]
	//  - 가장 가까운 와이파이 Top20 선별
	
	public void dbSelectTop20(HashMap<String, Double> distMap) {
		
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        // # select 결과를 담을 2차원 배열 생성
        results = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
        	LinkedList<String> result = new LinkedList<>();
        	results.add(result);
        }
        
        // # 거리가 짧은 순으로 오름차순 정렬
        List<Map.Entry<String, Double>> entryList = new LinkedList<>(distMap.entrySet());
        entryList.sort(
        		new Comparator<Map.Entry<String, Double>>() {
        			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
        				return Double.compare(o1.getValue(), o2.getValue());
        			}
        		}
        		);
        
        // # 거리가 가장 짧은 20개의 pk값(ManageCode)을 추출 후
        // 리스트 객체 top20IDs에 저장
        ArrayList<String> top20IDs = new ArrayList<>();
        int rank = 1;
        for(Map.Entry<String, Double> entry : entryList) {
        	if(rank > 20) break;
        	top20IDs.add(entry.getKey());
        	rank++;
        }

        // # 쿼리 적용
        try {
        	String sql = " ";
            connection = DriverManager.getConnection(url, dbUserId, dbUserPw);

            // # select된 record를 2차원 리스트 results에 저장
            int idx = 0;
            for(String top20ID : top20IDs) {
                sql = "select * from WIFILIST"
                		+ " where ManageCode = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, top20ID);
                rs = statement.executeQuery();
                
                while(rs.next()) {
                	results.get(idx).add(rs.getString("ManageCode"));
                	results.get(idx).add(rs.getString("Region"));
                	results.get(idx).add(rs.getString("Name"));
                	results.get(idx).add(rs.getString("Address"));
                	results.get(idx).add(rs.getString("AddressDetail"));
                	results.get(idx).add(rs.getString("Floor"));
                	results.get(idx).add(rs.getString("ConstructType"));
                	results.get(idx).add(rs.getString("ConstructInst"));
                	results.get(idx).add(rs.getString("ServiceType"));
                	results.get(idx).add(rs.getString("Network"));
                	results.get(idx).add(Integer.toString(rs.getInt("SinceYear")));
                	results.get(idx).add(rs.getString("InOutDoor"));
                	results.get(idx).add(rs.getString("Env"));
                	results.get(idx).add(Double.toString(rs.getDouble("Xpos")));
                	results.get(idx).add(Double.toString(rs.getDouble("Ypos")));
                	results.get(idx).add(rs.getString("SearchDate"));
                }
                // # 현재 위치와 와이파이 사이의 거리를 2차원리스트 results에 저장
                Double curDist = entryList.get(idx).getValue();
                Double curDistRound = Math.round(curDist*10000)/10000.0;
                results.get(idx).addFirst(Double.toString(curDistRound));
                idx ++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        // # DB 관련 객체 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
        }
	}
	
	// ================================================================== // 
	
	
	//  [ 히스토리 불러오기 (테이블 SearchLog) ]
	//  - 검색한 위치 이력을 불러오는 매서드
	//  - 불러온 결과를 2차원 리스트 
	
	public void dbSelectLog() {
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        
        // # DB 연결
        try {
            connection = DriverManager.getConnection(url, dbUserId, dbUserPw);
            String sql = " ";
            
            // # 쿼리 적용
            // : 불러올 데이터의 총 개수를 구한 뒤 cnt에 저장
            int cnt = 0;
            sql = "select count(*) from SearchLog";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while(rs.next()) {
            	cnt = rs.getInt("count(*)");
            }
            // # 연결 해제
            statClose(statement);
            rsClose(rs);
            
            
            // # 결과를 담을 2차원 리스트를 개수에 맞게 생성
            history = new ArrayList<>();
            for(int i = 0; i < cnt; i++) {
            	LinkedList<String> result = new LinkedList<>();
            	history.add(result);
            }
            
            
            // # 쿼리 적용
            // : 테이블 SearchLog의 모든 record를 불러온 뒤,
            // 2차원 리스트 history에 저장
            sql = "select * from SearchLog";
            statement = connection.prepareStatement(sql);
            
            // # select된 record를 2차원 리스트 history에 저장
            rs = statement.executeQuery();
            int i = 0;
            while(rs.next()) {
            	history.get(i).add(Integer.toString(rs.getInt("ID")));
            	history.get(i).add(Double.toString(rs.getDouble("Xpos")));
            	history.get(i).add(Double.toString(rs.getDouble("Ypos")));
            	history.get(i).add(rs.getTimestamp("SearchDate").toString());
            	i++;
            }
            

        } catch (SQLException e) {
            throw new RuntimeException(e);

        // # DB 관련 객체 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
        }
        
	}
	

	// ================================================================== //
	
	//  [ history 삭제 (테이블 SearchLog) ]
	//  - '삭제' 버튼을 누른 곳에 해당하는 ID를 가진 row를
	//  SearchLog 테이블에서 삭제
	
	public void deleteSearchLog(int id) {
		// # DB 접속에 필요한 정보 입력
		String url = "jdbc:mysql://43.202.116.172:3306/wifiDB";
        String dbUserId = "testDB_admin";
        String dbUserPw = "1234";
        
        // # 드라이버 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // # DB 관련 객체 생성
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        try {
        	connection = DriverManager.getConnection(url, dbUserId, dbUserPw);
        	
        	// # 삭제
            String sql = "delete from SearchLog"
            		+ " where ID = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            statClose(statement);
            
            // # ID 순서 재정렬
            sql = "set @count = 0";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statClose(statement);

            sql = "update SearchLog"
            		+ " set ID = @count := @count+1";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statClose(statement);
            
            sql = "alter table SearchLog auto_increment = 0";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statClose(statement);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
            
        // DB 연결 해제
        } finally {
        	rsClose(rs);
        	statClose(statement);
        	connectionClose(connection);
    	}
    }
	
	// ================================================================== // 
	
	
	//  [ 각종 커넥션 연결 해제하는 메서드들 ]
	
	private void rsClose(ResultSet rs) {
        try {
        	if (rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
        	}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
	
	private void statClose(PreparedStatement statement) {
        try {
        	if (statement != null) {
                if (!statement.isClosed()) {
                    statement.close();
                }
        	}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
	
	private void connectionClose(Connection connection) {
        try {
        	if (connection != null) {
                if (!connection.isClosed()) {
                    connection.isClosed();
                }
        	}
        } catch (SQLException e) {
            throw new RuntimeException(e);
    	}
	}
        
}
	
	
	
	
	// ================================================================== // 
	
	
	

