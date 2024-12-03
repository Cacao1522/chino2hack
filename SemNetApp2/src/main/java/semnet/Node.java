package semnet;

import java.util.ArrayList;
import java.util.HashMap;

class Node {
	String name;

	// 自分から出ていくリンク
	ArrayList<Link> departFromMeLinks;
	// 自分に入ってくるリンク
	ArrayList<Link> arriveAtMeLinks;

	// 数値プロパティ（滞在時間、料金など）
    HashMap<String, Double> numericProperties;
	public Node(String theName) {
		name = theName;
		departFromMeLinks = new ArrayList<Link>();
		arriveAtMeLinks = new ArrayList<Link>();
		numericProperties = new HashMap<>();
	}

	public ArrayList<Node> getISATails() {
		ArrayList<Node> isaTails = new ArrayList<Node>();
		for (int i = 0; i < arriveAtMeLinks.size(); i++) {
			Link theLink = (Link) arriveAtMeLinks.get(i);
			if ("is-a".equals(theLink.getLabel())) {
				isaTails.add(theLink.getTail());
			}
		}
		return isaTails;
	}

	public void addDepartFromMeLinks(Link theLink) {
		departFromMeLinks.add(theLink);
	}

	public ArrayList<Link> getDepartFromMeLinks() {
		return departFromMeLinks;
	}

	public void addArriveAtMeLinks(Link theLink) {
		arriveAtMeLinks.add(theLink);
	}

	public ArrayList<Link> getArriveAtMeLinks() {
		return arriveAtMeLinks;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}
}
