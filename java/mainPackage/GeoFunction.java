package mainPackage;

public class GeoFunction {
	
	// # 두 위도와 경도 사이의 거리 계산 (km로 환산된 값)
	public static double getDistance(double x1, double y1, double x2, double y2) {
	    double distance;
	    double radius = 6371; // 지구 반지름(km)
	    double toRadian = Math.PI / 180;

	    double deltaLatitude = Math.abs(x1 - x2) * toRadian;
	    double deltaLongitude = Math.abs(y1 - y2) * toRadian;

	    double sinDeltaLat = Math.sin(deltaLatitude / 2);
	    double sinDeltaLng = Math.sin(deltaLongitude / 2);
	    double squareRoot = Math.sqrt(
	        sinDeltaLat * sinDeltaLat +
	        Math.cos(x1 * toRadian) * Math.cos(x2 * toRadian) * sinDeltaLng * sinDeltaLng);

	    distance = 2 * radius * Math.asin(squareRoot);

	    return distance;
	}
	
	public static void main(String[] args) {
		double lat = 37.5544069;  // startX
		double lnt = 126.8998666; // startY
		double xPos = 37.552788;  // endX
		double yPos = 126.89939;  // endY
		double dist = getDistance(lat, lnt, xPos, yPos);
		System.out.println(dist);
	}
}
