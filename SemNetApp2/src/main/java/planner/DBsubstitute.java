package planner;

import java.util.*;

public class DBsubstitute {
	Map<String,Spot> spots;

	public DBsubstitute(){
		Map<String,Spot> spots = new HashMap<>();
		//各数値はランダム
		spots.put("spot1", new Spot("spot1", 4300, 40, 34.9283, 137.2417, new HashMap<String, Activity>()));
		spots.put("spot2", new Spot("spot2", 8500, 60, 34.8827, 136.9734, new HashMap<String, Activity>()));
		spots.put("spot3", new Spot("spot3", 7900, 110, 35.1520, 137.3318, new HashMap<String, Activity>()));
		spots.put("spot4", new Spot("spot4", 5100, 140, 34.9312, 137.0149, new HashMap<String, Activity>()));
		spots.put("spot5", new Spot("spot5", 3700, 70, 34.8563, 136.7402, new HashMap<String, Activity>()));
		spots.put("spot6", new Spot("spot6", 6600, 120, 34.9687, 137.2381, new HashMap<String, Activity>()));
		spots.put("spot7", new Spot("spot7", 9200, 170, 34.9876, 137.4021, new HashMap<String, Activity>()));
		spots.put("spot8", new Spot("spot8", 2100, 80, 35.0034, 136.8537, new HashMap<String, Activity>()));
		spots.put("spot9", new Spot("spot9", 3000, 100, 34.8478, 137.1265, new HashMap<String, Activity>()));
		spots.put("spot10", new Spot("spot10", 7400, 50, 35.1147, 137.2768, new HashMap<String, Activity>()));

		for(var spot:spots.values()) {
			spot.activityMap.put(spot.name+"に行く", new Activity(spot.name+"に行く",spot,spot.cost,spot.time));
		}
		spots.get("spot1").activityMap.put("製品を見る", new Activity("製品を見る", spots.get("spot1"), 3300, 30));
		spots.get("spot2").activityMap.put("動物を見る", new Activity("動物を見る", spots.get("spot2"), 7200, 50));
		spots.get("spot3").activityMap.put("自然を見る", new Activity("自然を見る", spots.get("spot3"), 6500, 90));
		spots.get("spot4").activityMap.put("伝統工芸品を見る", new Activity("伝統工芸品を見る", spots.get("spot4"), 4600, 120));
		spots.get("spot5").activityMap.put("城を見る", new Activity("城を見る", spots.get("spot5"), 3000, 60));
		spots.get("spot6").activityMap.put("自然を見る", new Activity("自然を見る", spots.get("spot6"), 6000, 100));
		spots.get("spot7").activityMap.put("動物を見る", new Activity("動物を見る", spots.get("spot7"), 8100, 150));
		spots.get("spot8").activityMap.put("伝統工芸品を見る", new Activity("伝統工芸品を見る", spots.get("spot8"), 1900, 70));
		spots.get("spot9").activityMap.put("製品を見る", new Activity("製品を見る", spots.get("spot9"), 2500, 90));
		spots.get("spot10").activityMap.put("城を見る", new Activity("城を見る", spots.get("spot10"), 6600, 40));

		this.spots = spots;
	}

	public Map<String,Spot> getSpots(){
		return spots;
	}

	//spotをmapではなくリストで返す
	public List<Spot> getSpotList(){
		return new ArrayList<Spot>(spots.values());
	}
	//spotの名前とそこにあるactivityの名前を配列に保存したリストを返す
	public List<String[]> getSpotActivity(){
		List<String[]> spotActivity = new ArrayList<>();
		for(var spot:spots.values()) {
			for(var activity:spot.getActivityList())
				spotActivity.add(new String[]{spot.getName(), activity.getName()});
		}
		return spotActivity;
	}
	//すべてのactivityを返す
	public List<String> allActyivity(){
		Set<String> allActivity = new HashSet<>();
		for(var spot:spots.values()) {
			for(var activity:spot.getActivityList())
				allActivity.add(activity.getName());
		}
		return new ArrayList<String>(allActivity);
	}
}

/*class Spot{
	String name;
	int cost;
	int time;
	double latitude;
	double longitude;
	HashMap<String, Activity> activityMap;
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
	//Spot spot;
	int cost;
	int time;

	Activity(String name, Spot spot, int cost, int time){
		this.name = name;
		//this.spot = spot;
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

	/*
	public String toStringAll() {
		return "["+name+"]　費用+"+cost+"円　予想滞在時間"+time+"分 できる場所("+spot.getName()+")";
	}

	public String printActivity() {
		return name + " at " + spot.getName();
	}

}*/