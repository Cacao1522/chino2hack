package planner;

import java.util.*;

class Node {
	static int lastId = 0;
	final int id = lastId++;

	Node parent; //親ノード
	Operator operator;
	Bind bind;
	Formula goal;
	Formula state;
	int allCost;
	int allTime;
	double allMovement;
	Spot nowSpot;

	Node() {
	}

	Node(Formula state, Formula goal, Spot nowSpot) {
		this.state = state;
		this.goal = goal;
		this.nowSpot = nowSpot;
		this.bind = new Bind();
		this.allMovement = 0;
		setResource(0,0);
	}

	Node(Formula state, Formula goal, Spot nowSpot,double allMovement, int allCost, int allTime) {
		this.state = state;
		this.goal = goal;
		this.nowSpot = nowSpot;
		this.bind = new Bind();
		this.allMovement = allMovement;
		setResource(allTime, allTime);
	}

	public String toString() {
		return String.format("(%d) %s", this.id, this.operator.name);
	}
	
	public String printMove() {
		switch(operator.name.terms.get(0)) {
		case "#1:":
			String activity = operator.name.terms.get(8);
			return nowSpot.getName()+"で"+activity;
		case "#2:":
			return nowSpot.getName()+"を観光する。";
		case "#3:":
			return "終着地点へ向かう。";
		}
		return null;
	}

	int depth() {
		return this.parent == null ? 0 : this.parent.depth() + 1;
	}

	//親をたどり、どんな行動をすれば目標にたどり着くかを記録する。
	List<Operator> toPlan() {
		var plan = new ArrayList<Operator>();
		Node node = this;

		while (node != null && node.operator != null) {
			plan.add(node.operator.instantiated(this.bind));
			node = node.parent;
		}

		Collections.reverse(plan);

		return plan;
	}
	
	//目標から親をたどり、親から同状態が遷移したかを返す。
	List<Formula> toPlanState() {
		var planState = new ArrayList<Formula>();
		Node node = this;

		while (node != null && node.state != null) {
			planState.add(node.state);
			node = node.parent;
		}

		Collections.reverse(planState);

		return planState;
	}

	//ハッカソン用
	List<Node> toPlanNode(){
		var planNodee = new ArrayList<Node>();
		Node node = this;

		while (node != null) {
			planNodee.add(node);
			node = node.parent;
		}

		Collections.reverse(planNodee);

		return planNodee;	
	}
	
	public void setResource(int cost, int time){
		this.allCost = cost;
		this.allTime = time;	
	}
	
	public void addResource(int cost, int time){
		this.allCost += cost;
		this.allTime += time;	
	}

	public void setMovement(double distance) {
		this.allMovement = distance;
	}
	
	public void addMovement(double distance) {
		this.allMovement += distance;
	}
	
	public int[] getResource() {
		switch(operator.name.terms.get(0)) {
		case "#1:":
			String activity = operator.name.terms.get(8);
			return nowSpot.activityMap.get(activity).getResource();
		case "#2:":
			return nowSpot.getResource();
		}
		return new int[]{0,0};
	}

	public double getAllMovement() {
		return allMovement;
	}
	
	public double getAllCost() {
		return allCost;
	}
	
	public int hashCode() {
		return Objects.hash(state, allCost, allTime, allMovement);
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Node) == false)
			return false;
		var other = (Node) obj;
		if (this.allCost != other.allCost) return false;
		if (this.allTime != other.allTime) return false;
		if (this.allMovement != other.allMovement) return false;
		return Objects.equals(this.state, this.state);
	}
}