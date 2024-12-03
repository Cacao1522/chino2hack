package planner;

import java.util.*;

public class DBtest {
	public List<Data> getDatas(){
		List<Data> datas = new ArrayList<>();
		datas.add(new Data("名古屋","城を見る"));
		datas.add(new Data("名工大","研究室見学"));
		return datas;
	}
}
class Data{
	String spot;
	String _do;
	Data(String spot, String _do){
		this.spot = spot;
		this._do = _do;
	}
	public String getSpot() {
		return spot;
	}
	public String getDo() {
		return _do;
	}
}