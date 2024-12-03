package planner;

import java.util.*;

//基本的な操作を補助する
public class Utils {
	static int count = 0;

	//表示形式を揃えるためのもの
	public static void println(Object o) {
		System.out.printf("%6d| %s\n", ++count, o.toString());
	}

	//表示形式を揃えるためのもの
	public static void println(String format, Object... args) {
		var s = String.format(format, args);
		System.out.printf("%6d| %s\n", ++count, s);
	}

	//xsの後ろにysをつなげる
	public static <T> List<T> concat(List<T> xs, List<T> ys) {
		var zs = new ArrayList<T>(xs);
		zs.addAll(ys);
		return zs;
	}

	//xsからysを取り除く
	public static <T> List<T> subtract(List<T> xs, List<T> ys) {
		var zs = new ArrayList<>(xs);
		zs.removeAll(ys);
		return zs;
	}

// syntactic sugar
	public static Predicate predicate(String x) {
		return new Predicate(x);
	}

	//String...は可変長引数。(A,B,C)という感じに、これをリストに直すメソッド
	public static Formula formula(String... texts) {
		var ps = Arrays.stream(texts).map(Predicate::new).toList();
		return new Formula(ps);
	}

	public static Operator operator(String name, Formula ifList, Formula addList, Formula delList) {
		return new Operator(new Predicate(name), ifList, addList, delList);
	}

	public static Formula _if(String... args) {
		return formula(args);
	}

	public static Formula add(String... args) {
		return formula(args);
	}

	public static Formula del(String... args) {
		return formula(args);
	}
	
//hackathon用
	public static double haversine(Spot spot1, Spot spot2) {
		return haversine(spot1.latitude, spot1.longitude, spot2.latitude, spot2.longitude);
	}
	
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
    	double EARTH_RADIUS = 6371.0;
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