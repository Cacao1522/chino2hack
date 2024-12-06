package planner;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DatabaseDAOクラス
 */
public class DataBaseDAO {
    private static Connection connection;

    //データベースの初期化
    public static void initializeDatabase() {
		try {
		    // データベースへの接続を作成
		    connection = DriverManager.getConnection("jdbc:h2:./data/database");

		    //テーブル初期化
		    dropTable("spots");
		    dropTable("spot_info");

           //観光地を表すspotsの作成
		    createTable("CREATE TABLE IF NOT EXISTS spots (\n"
	           		//+ "  spot_id INT AUTO_INCREMENT PRIMARY KEY,\n"
	           		+ "  spot_name VARCHAR(255),\n"
	           		+ "  spot_cost INT,\n"
	           		+ "  spot_time INT,\n"
	           		+ "  location VARCHAR(255),\n"
	           		//+ "  reputation REAL,\n"
	           		+ "  check(spot_cost >= 0 and spot_time >= 0)\n"
	           		+ ");");

		    //観光地の情報を表すspot_infoの作成
           createTable("CREATE TABLE IF NOT EXISTS spot_info (\n"
           		+ "  spot_name VARCHAR(255),\n"
           		+ "  do_name VARCHAR(255),\n"
           		+ "  do_cost INT,\n"
           		+ "  do_time INT,\n"
           		+ "  check(do_cost >= 0 and do_time >= 0)\n"
           		+ ");");


           //////////Webからのデータ入力/////////
           //spotsに初期値入力

           updateTable("INSERT INTO spots (spot_name, spot_cost, spot_time, location)\n"
           	+ "VALUES ('名古屋城', 550, 60, '35.18485521703068, 136.8997104927299'),\n"
           		+ "('国宝犬山城', 550, 60, '35.388500346034, 136.93968621971064'),\n"
           		+ "('岡崎城', 300, 60, '34.95657159671378, 137.15901900254596'),\n"
           		+ "('清州城', 400, 60, '35.2167403771669, 136.8439355197518'),\n"
           		+ "('田峯城', 220, 60, '35.05495260859201, 137.53517905043378'),\n"
           		+ "('トヨタ産業技術記念館', 1000, 120, '35.18269550618594, 136.87629573324236'),\n"
           		+ "('ノリタケの森', 500, 120, '35.179273119522755, 136.88185059832276'),\n"
           		+ "('愛知県陶磁美術館', 400, 90, '35.18593834367427, 137.09823633694668'),\n"
           		+ "('ミツカンミュージアム', 500, 120, '34.89326887298398, 136.93469639460793'),\n"
           	    + "('有松絞会館', 300, 90, '35.066586182308804, 136.97166635413842'),\n"
           		+ "('招き猫ミュージアム', 300, 90, '35.22647322195961, 137.1039520964724'),\n"
           		+ "('半田赤レンガ建物', 200, 90, '34.90158595700351, 136.9291823811162'),\n"
           		+ "('四谷千牧田', 0, 90, '35.048318373456254, 137.5671172480543'),\n"
           		+ "('名城公園', 500, 120, '35.18886746442391, 136.9032812332426'),\n"
           		+ "('鶴舞公園', 1500, 40, '35.155235966194894, 136.92031773138916'),\n"
    			+ "('徳川美術館', 1600, 120, '35.18347487796634, 136.9335886197506'),\n"
           		+ "('名古屋市科学館', 400, 40, '35.16528727135357, 136.90017466578175'),\n"
           		+ "('愛知県美術館', 500, 120, '35.17108374588295, 136.9116476657822'),\n"
           		+ "('東山動植物園', 500, 120, '35.15671330672965, 136.98234633879753'),\n"
           		+ "('名古屋港水族館', 2030, 120, '35.09071946420501, 136.87924235969555'),\n"
           		+ "('豊橋総合動植物公園のんほいパーク', 600, 120, '34.72051339788123, 137.43263729460162'),\n"
           		+ "('ラグーナテンボス', 2300, 240, '34.808746072108775, 137.2720279588878'),\n"
           		+ "('刈谷ハイウェイオアシス', 0, 120, '35.04131722059449, 137.04927827371313'),\n"
           	+ "('LEGOLAND', 4500, 240, '35.05069430395393, 136.84449448482604'),\n"
           		+ "('名古屋工業大学', 0, 60, '35.1572030907294, 136.9254679485854');");


         //spot_infoに初期値入力
           updateTable("INSERT INTO spot_info (spot_name, do_name, do_cost, do_time)\n"
        		   + "VALUES ('名古屋城', '城を見る', 550, 60),\n"
        		   + "('名古屋城', '庭園を見る', 0, 30),\n"
        		   + "('国宝犬山城', '城を見る', 500, 50),\n"
        		   + "('国宝犬山城', '城下町を見る', 0, 90),\n"
        		   + "('岡崎城', '城を見る', 300, 60),\n"
        		   + "('清州城', '城を見る', 500, 60),\n"
        		   + "('清州城', '甲冑試着をする', 0, 30),\n"
        		   + "('田峯城', '山城を見る', 220, 60),\n"
        		   + "('トヨタ産業技術記念館', '産業を知る', 500, 60),\n"
        		   + "('トヨタ産業技術記念館', '技術体験をする', 500, 30),\n"
        		   + "('ノリタケの森', '産業を知る', 500, 50),\n"
        		   + "('ノリタケの森', '陶芸体験をする', 1000, 60),\n"
        		   + "('愛知県陶磁美術館', '産業を知る', 400, 60),\n"
        		   + "('愛知県陶磁美術館', '陶磁器を知る', 400, 60),\n"
        		   + "('ミツカンミュージアム', '産業を知る', 500, 120),\n"
        		   + "('ミツカンミュージアム', '酢づくりを知る', 500, 120),\n"
        		   + "('有松絞会館', '伝統を知る', 300, 90),\n"
        		   + "('有松絞会館', '有松絞を知る', 300, 90),\n"
        		   + "('招き猫ミュージアム', '伝統をしる', 550, 90),\n"
        		   + "('招き猫ミュージアム', '招き猫を見る', 550, 90),\n"
        		   + "('半田赤レンガ建物', '伝統を知る', 200, 90),\n"
        		   + "('半田赤レンガ建物', 'ビール製造を知る', 200, 90),\n"
        		   + "('四谷千牧田', '自然を見る', 0, 90),\n"
        		   + "('四谷千牧田', '棚田を見る', 0, 90),\n"
        		   + "('名城公園', '自然を見る', 0, 90),\n"
        		   + "('名城公園', '花を見る', 0, 90),\n"
        		   + "('鶴舞公園', '自然を見る', 0, 90),\n"
        		   + "('鶴舞公園', '花を見る', 0, 90),\n"
        		   + "('徳川美術館', '歴史を知る', 1400, 120),\n"
        		   + "('徳川美術館', '絵画を見る', 1400, 120),\n"
        		   + "('名古屋市科学館', '科学を知る', 400, 120),\n"
        		   + "('名古屋市科学館', 'プラネタリウムを見る', 400, 30),\n"
        		   + "('愛知県美術館', '美術品を見る', 500, 120),\n"
        		   + "('東山動植物園', '動物を見る', 500, 120),\n"
        		   + "('東山動植物園', '植物を見る', 500, 90),\n"
        		   + "('東山動植物園', 'スカイタワーに登る', 300, 30),\n"
        		   + "('名古屋港水族館', '魚を見る', 2030, 120),\n"
        		   + "('名古屋港水族館', '動物を見る', 2030, 120),\n"
        		   + "('名古屋港水族館', 'イルカショーを見る', 2030, 30),\n"
        		   + "('豊橋総合動植物公園のんほいパーク', '動物を見る', 600, 120),\n"
        		   + "('ラグーナテンボス', 'アトラクションに乗る', 1300, 240),\n"
        		   + "('刈谷ハイウェイオアシス', '観覧車に乗る', 600, 15),\n"
        		   + "('刈谷ハイウェイオアシス', '温泉にはいる', 900, 60),\n"
        		   + "('LEGOLAND', 'アトラクションに乗る', 4500, 240),\n"
        		   + "('名古屋工業大学', '構内を見る', 0, 60),\n"
        		   + "('名古屋工業大学', '研究室に訪れる', 0, 60);"
        		   );

		} catch (SQLException e) {
		    e.printStackTrace();
		}
    }


    //データベースへの接続確立
    public static Connection getConnection() {
		return connection;
    }


    //////////データベースに対する操作//////////
    //与えられたSQL文でテーブルの作成
    public static void createTable(String sql) {
    	try {
    		Statement stmt = connection.createStatement();
    		stmt.execute(sql);
    	} catch (SQLException e) {
		    e.printStackTrace();
		}
    }

    //与えられたSQL文でテーブルの更新
    public static void updateTable(String sql) {
    	try {
    		Statement stmt = connection.createStatement();
    		stmt.executeUpdate(sql);
    	} catch (SQLException e) {
		    e.printStackTrace();
		}
    }

    //与えられたSQL文でテーブルの検索
    public static ResultSet queryTable(String sql) {
    	try {
    		Statement stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(sql);
    		return rs;
    	} catch (SQLException e) {
		    e.printStackTrace();
		}
    	return null;
    }

    //与えられたテーブル名のテーブル削除
    public static void dropTable(String table_name) {
    	try {
		    // すべてのデータ削除
		    Statement stmt = connection.createStatement();
		    stmt.execute("DROP TABLE IF EXISTS " + table_name);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
    }

    //データベース内のすべての要素表示
    public static void showAllData() {
    	try {
    		ResultSet rs = queryTable("select * from spots natural join spot_info");

        	//結果に対する処理
    		for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
    			System.out.print(rs.getMetaData().getColumnLabel(i) + " | ");
    		}
    		System.out.println();
    		while(rs.next()) {
    			//System.out.println(rs.toString());

    			for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
    				System.out.print(rs.getString(i) + " ");
    			}
    			System.out.println();
    		}
    		System.out.println("---------");
    	} catch (SQLException e) {
    		 e.printStackTrace();
    	}
}

    /*データベース内の要素をプランニングで用いるマップ形式に変換*/
    public static Map<String, Spot> dataToMap(){
    	Map<String,Spot> spots = new HashMap<>();		//データベースの内容を格納したmap

    	try {
    		//各観光地の場所に訪れる際のデータ
    		ResultSet rs = queryTable("select * from spots");

    		while(rs.next()) {
    			String location = rs.getString("location");
    			String[] location_part = location.split(",");
    			String latitude = location_part[0];
    			String longitude = location_part[1];

    			spots.put(rs.getString("spot_name"), new Spot(rs.getString("spot_name"), rs.getInt("spot_cost"), rs.getInt("spot_time"), Double.parseDouble(latitude) ,Double.parseDouble(longitude), new HashMap<String, Activity>()));
    		}

    		//各観光地で何かをする際のデータ
    		ResultSet rs_info = queryTable("select * from spot_info");

    		while(rs_info.next()) {
    			spots.get(rs_info.getString("spot_name")).activityMap.put(rs_info.getString("do_name"), new Activity(rs_info.getString("do_name"), rs_info.getInt("do_cost"), rs_info.getInt("do_time")));
    		}

    	} catch (SQLException e) {
    		 e.printStackTrace();
    	}

    	return spots;
    }


    public static Map<String,Spot> getSpots(){
		return dataToMap();
	}

  //spotをmapではなくリストで返す
  	public static List<Spot> getSpotList(){
  		return new ArrayList<Spot>(getSpots().values());
  	}
  	//spotの名前とそこにあるactivityの名前を配列に保存したリストを返す
  	public static List<String[]> getSpotActivity(){
  		List<String[]> spotActivity = new ArrayList<>();
  		for(var spot:getSpots().values()) {
  			for(var activity:spot.getActivityList())
  				spotActivity.add(new String[]{spot.getName(), activity.getName()});
  		}
  		return spotActivity;
  	}
  	//すべてのactivityを返す
  	public static List<String> allActivity(){
  		Set<String> allActivity = new HashSet<>();
  		for(var spot:getSpots().values()) {
  			for(var activity:spot.getActivityList())
  				allActivity.add(activity.getName());
  		}
  		return new ArrayList<String>(allActivity);
  	}
    /*-------検証用--------*/
    public static void showData(String table_name) {
    	try {
    		ResultSet rs = queryTable("select * from " + table_name);

        	//結果に対する処理
    		for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
    			System.out.print(rs.getMetaData().getColumnLabel(i) + " | ");
    		}
    		System.out.println();
    		while(rs.next()) {
    			//System.out.println(rs.toString());

    			for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
    				System.out.print(rs.getString(i) + " ");
    			}
    			System.out.println();
    		}
    		System.out.println("---------");
    	} catch (SQLException e) {
    		 e.printStackTrace();
    	}
    	queryTable("select * from spots");
    	queryTable("select * from spot_info");
    }
}

class Spot{
	String name;
	int cost; // 料金
	int time; // 滞在時間
	double latitude;
	double longitude;
	HashMap<String, Activity> activityMap;
	double satisfactionScore; // 満足度 (0.0 - 5.0)
	double adjustedSatisfactionScore; // 調整後の満足度
	List<String> keywords; // キーワード（例: "遊園地", "歴史"）
	String link; // 画像へのリンク
	String detail; // 説明文
	String site; // 公式サイトへのリンク
	Spot(String name, int cost, int time, double lat, double lon, HashMap<String, Activity> activityMap){
		this.name = name;
		this.cost = cost;
		this.time = time;
		latitude = lat;
		longitude = lon;
		this.activityMap = activityMap;
	}
	public String getName() {
		return name;
	}
	public int getCsot() {
		return cost;
	}
	public int getTime() {
		return time;
	}
	public HashMap<String, Activity> getActivityMap() {
		return activityMap;
	}
	public List<Activity> getActivityList(){
		return new ArrayList<Activity>(activityMap.values());
	}
	public int[] getResource() {
		return new int[]{cost, time};
	}

	public String toString() {
		return "["+name+"]　できること["+activityMap+"]";
	}

	public String toStringResource() {
		return "["+name+"]　費用+"+cost+"円　予想滞在時間"+time+"分";
	}

	public String toStringAll() {
		return "["+name+"]　費用+"+cost+"円　予想滞在時間"+time+"分 (経度,緯度)=("+latitude+","+longitude+") できること["+activityMap+"]";
	}

	public String printSpot() {
		return "spot "+name;
	}
}

class Activity{
	String name;
	int cost;
	int time;

	Activity(String name, int cost, int time){
		this.name = name;
		this.cost = cost;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public int[] getResource() {
		return new int[]{cost, time};
	}

	public String toString() {
		return name;
	}

	public String toStringResource() {
		return "["+name+"]　費用+"+cost+"円　予想滞在時間"+time+"分";
	}

	public String toStringAll(String spot_name) {
		return "["+name+"]　費用+"+cost+"円　予想滞在時間"+time+"分 できる場所("+spot_name+")";
	}

	//変更
	public String printActivity(String spot_name) {
		return name + " at " + spot_name;
	}
}
