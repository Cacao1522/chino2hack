package semnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.IOException;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.h2.util.json.JSONObject;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;

public class Search {
     // APIキーを設定
        String apiKey = "AIzaSyBQjOdvRm0QnKiDotdnkl7lf0_4pQEfRt4";
     // GeoApiContextを作成
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey) // APIキーを設定
                .build();
        // 観光地リスト（例としてfirst, second, third）
        // 満足度が高い順にソートされている想定
        public List<TouristSpot> touristSpots = Arrays.asList(
                new TouristSpot(
                        "名古屋城",
                        35.18493421439098,
                        136.8997204840597,
                        4.5,
                        2.5,
                        Arrays.asList("名古屋", "城", "歴史")),
                new TouristSpot(
                        "犬山城",
                        35.388535305191596,
                        136.93961647981007,
                        4.0,
                        2,
                        Arrays.asList("犬山", "城", "歴史")),
                new TouristSpot(
                        "岡崎城",
                        34.956510044113635,
                        137.15921212212925,
                        3.5,
                        2,
                        Arrays.asList("岡崎", "城", "歴史")),
                new TouristSpot(
                        "熱田神宮",
                        35.127463170875295,
                        136.90923123932276,
                        4.0,
                        2,
                        Arrays.asList("名古屋", "神社", "歴史")),
                new TouristSpot(
                        "豊川稲荷",
                        34.83702822191114,
                        137.39428437915595,
                        4.5,
                        2,
                        Arrays.asList("豊川", "神社", "歴史")),
                new TouristSpot(
                        "六所神社",
                        34.955203444827546,
                        137.17838625006277,
                        3.0,
                        1,
                        Arrays.asList("岡崎", "神社", "歴史")),
                new TouristSpot(
                        "岡崎公園",
                        34.956805750308725,
                        137.16013077795222,
                        4.0,
                        2,
                        Arrays.asList("岡崎", "公園")),
                new TouristSpot(
                        "熱田神宮公園",
                        35.13144950884303,
                        136.90286960872663,
                        3.5,
                        1.5,
                        Arrays.asList("名古屋", "公園")),
                new TouristSpot(
                        "モンキーパーク",
                        35.38955324224789,
                        136.95783222426653,
                        4.5,
                        2.5,
                        Arrays.asList("犬山", "公園", "動植物")));

        // 観光地リストを一つのリストにまとめて再帰で処理
        List<List<TouristSpot>> allSpots = new ArrayList<>();
     // 出発地と到着地
        String origin = "名古屋工業大学";
        String destination = "名古屋工業大学";
        // 出発地の緯度経度
        double leaveLat = 35.157203090619916;
        double leaveLon = 136.92551086446304;
        // 到着地の緯度経度
        double arriveLat = 35.157203090619916;
        double arriveLon = 136.92551086446304;
     // 許容時間
        double allowTime = 8;
     // 現時点の最大値を保存
        List<TouristSpot> result = new ArrayList<>();
        double maxdist = -1;
        double maxtotal = -1;
        double maxss = -1;
        List<Integer> optimalTravelTimes = null; // 各観光地間の移動時間を記録


        Search (String leave, String arrive, double time, List<List<String>> keywordsLists) {
//        	this.origin = leave;
//        	this.destination = arrive;
        	this.allowTime = time;

        	for (List<String> inputList : keywordsLists) {
     		   for (TouristSpot spot : touristSpots) {
                spot.setAdjustedSatisfactionScore();
                for (String keyword : inputList) {
                    if (spot.getName().equals(keyword)) {
                        spot.increaseSatisfaction(2);
                    	}
                    if (spot.getKeywords().contains(keyword)) {
                        spot.increaseSatisfaction(1);
                    	}
                	}
     		   }

            // 満足度で降順ソートし、上位5つを取得
            List<TouristSpot> topSpots = touristSpots.stream()
            		.sorted(Comparator.comparingDouble(TouristSpot::getAdjustedSatisfactionScore).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
            allSpots.add(topSpots);
        }
        System.out.println(allSpots);

     }
     Search() {}
   public void search() {


   try {
	   System.out.println("出発地"+origin);
		GeocodingResult[] results = GeocodingApi.geocode(context, origin).await();
		leaveLat = results[0].geometry.location.lat;
		leaveLon = results[0].geometry.location.lng;
		System.out.println("出発地の緯度"+leaveLat);
		System.out.println("出発地の経度"+leaveLon);
   } catch (ApiException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	} catch (IOException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}

   System.out.println("制限時間：" + allowTime);

   int average_speed = 60; // km/h
   double K = 2.0; // 補正係数

   while (!allSpots.isEmpty()) {
	    List<List<TouristSpot>> allPlans = new ArrayList<>();
	    generateAllPlans(allSpots, allPlans, 0, new ArrayList<>());

	    boolean foundPlan = false;


	    for (List<TouristSpot> order : allPlans) {
	        double distance = 0;
	        double totalTime = 0;
	        double ss = 0;
	        double lat1 = leaveLat;
	        double lon1 = leaveLon;

	        List<Double> travelTimes = new ArrayList<>(); // 各区間の移動時間を記録

	        for (TouristSpot spot : order) {
	            double lat2 = spot.getLatitude();
	            double lon2 = spot.getLongitude();
	            double segmentDistance = haversine(lat1, lon1, lat2, lon2);
	            distance += segmentDistance;
	            lat1 = lat2;
	            lon1 = lon2;

	            // 各区間の移動時間を計算して記録

	            double segmentTravelTime = segmentDistance * K / average_speed;
	            travelTimes.add(segmentTravelTime);

	            totalTime += spot.getStay();
	            ss += spot.getAdjustedSatisfactionScore();
	        }

	        // 最後の区間（ゴール地点への移動時間）
	        double segmentDistance = haversine(lat1, lon1, arriveLat, arriveLon);
	        distance += segmentDistance;
	        double finalTravelTime = segmentDistance * K / 60; // ゴール地点への移動時間
	        travelTimes.add(finalTravelTime);

	        totalTime += travelTimes.stream().mapToDouble(Double::doubleValue).sum(); // 合計移動時間
	        //System.out.println(totalTime);
	        if (totalTime <= allowTime) {
	            foundPlan = true;
	            if (ss > maxss || (ss == maxss && maxtotal > totalTime)) {
	                maxdist = distance;
	                maxtotal = totalTime;
	                maxss = ss;
	                result = order;
	                optimalTravelTimes = travelTimes.stream() // 最適プランの移動時間を記録
	                        .map(time -> (int) (time * 60)) // 時間を分に変換し、Integer に変換
	                        .collect(Collectors.toList());
	            }
	        }
	    }

	    if (foundPlan) {
	        break; // プランが見つかった場合、探索終了
	    } else {
	        System.out.println("制限時間内に終わるプランが見つかりませんでした。スポットリストを縮小します。");
	        allSpots.remove(allSpots.size() - 1); // 末尾のリストを削除
	    }
	}

	if (result == null) {
	    System.out.println("全てのプランで制限時間を満たすものが見つかりませんでした。");
	} else {
	    System.out.println("最適なプランが見つかりました: " + result);
	    System.out.println("総距離: " + maxdist);
	    System.out.println("総時間: " + maxtotal);
	    System.out.println("満足度: " + maxss);
	    System.out.println("各区間の移動時間: " + optimalTravelTimes);
	}

   }
	// 組み合わせと順列を一括生成する再帰関数
	public static void generateAllPlans(
	        List<List<TouristSpot>> allSpots,
	        List<List<TouristSpot>> allPlans,
	        int index,
	        List<TouristSpot> current) {
	    if (index == allSpots.size()) {
	        allPlans.add(new ArrayList<>(current));
	        return;
	    }

	    for (TouristSpot spot : allSpots.get(index)) {
	        if (!current.contains(spot)) { // 重複を防ぐ
	            current.add(spot);
	            generateAllPlans(allSpots, allPlans, index + 1, current);
	            current.remove(current.size() - 1); // 戻す
	        }
	    }
	}



    // 地球の半径 (km)
    private static final double EARTH_RADIUS = 6371.0;

    /*
     * 2地点間の距離をハーヴァーサインの公式で計算するメソッド
     *
     * @param lat1 地点1の緯度（度）
     *
     * @param lon1 地点1の経度（度）
     *
     * @param lat2 地点2の緯度（度）
     *
     * @param lon2 地点2の経度（度）
     *
     * @return 2地点間の距離（km）
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // 緯度・経度をラジアンに変換
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 緯度・経度の差分
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // ハーヴァーサインの公式
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        // 距離計算
        return EARTH_RADIUS * c;
    }
}

class TouristSpot {
    private String name; // 観光地の名前
    private double latitude; // 緯度public
    private double longitude; // 経度
    private double satisfactionScore; // 満足度 (1.0 - 5.0)
    private double adjustedSatisfactionScore; // 調整後の満足度
    private double stay;
    // int cost; // 費用（例: 入館料 + 移動費）
    private List<String> keywords; // キーワード（例: "遊園地", "歴史"）
    // private Map<String, String> features; // 特徴とその詳細情報 (例: "アクセス" ->
    // "公共交通機関でアクセス可能")

    // コンストラクタ
    public TouristSpot(String name, double latitude, double longitude, double satisfactionScore, double stay,
            List<String> keywords) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.satisfactionScore = satisfactionScore;
        this.adjustedSatisfactionScore = satisfactionScore;
        this.stay = stay;
        this.keywords = keywords;
        // this.cost = cost;
    }

    public void increaseSatisfaction(double value) {
        this.adjustedSatisfactionScore += value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSatisfactionScore() {
        return satisfactionScore;
    }

    public void setSatisfactionScore(double satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }

    public double getAdjustedSatisfactionScore() {
        return adjustedSatisfactionScore;
    }

    public void setAdjustedSatisfactionScore() {
        this.adjustedSatisfactionScore = this.satisfactionScore;
    }

    public double getStay() {
        return stay;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "TouristSpot{" +
                "name='" + name + '\'' +
                // ", latitude=" + latitude +
                // ", longitude=" + longitude +
                // ", satisfactionScore=" + satisfactionScore +
                // ", keywords=" + keywords +
                '}';
    }

}
