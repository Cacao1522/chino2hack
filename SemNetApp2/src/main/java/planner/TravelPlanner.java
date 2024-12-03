package planner;

import static planner.Utils.*;

import java.util.*;

//旅行のプランニングをするクラス
public class TravelPlanner {
	Problem problem;
	List<Operator> operators;
	Map<String,Spot> spots;
	Formula init;
	Formula goal;
	
	public TravelPlanner(Problem problem, Map<String,Spot> spots){
		this.problem = problem;
		this.operators = problem.operators();
		this.init = problem.initialState();
		this.goal = problem.goalState();
		this.spots = spots;
		if(problem instanceof TravelPlanningProblem2) {
			TravelPlanningProblem2 tp = (TravelPlanningProblem2) problem;
			var initialSpot = tp.initialSpot;
			var goalSpot = tp.goalSpot;
			if(initialSpot != null && goalSpot != null) {
				spots.put(initialSpot.getName(), initialSpot);
				spots.put(goalSpot.getName(), goalSpot);
			}
		}
	}

	//問題を解く
	public List<String> solve(int costLimit, int timeLimit) {
		var root = new Node(this.init, this.goal, spots.get(this.init.nowSpotName()));

		println("***** start forward search *****");
		println("init: " + this.init);
		println("goal: " + this.goal);
		println("spots: " + this.spots);

		//探索実行
		int maxLimit = 20;
		Result result = search(root,maxLimit, costLimit, timeLimit);

		if (result == null) {
			println("**** failed ****");
			return List.of("経路が作成できませんでした。");
		}

		//結果表示
		println("***** This is a plan! *****");
		List<String> answer = new ArrayList<>();
		var plan = result.goal.toPlanNode();
		
		int i = 1;
		for (var node : plan) {
			if(node.parent != null) {
				double distance = haversine(node.parent.nowSpot, node.nowSpot);
				int[] resource = node.getResource();
				int cost = resource[0] + (int)(distance/13*170);
				int time = resource[1] + (int)(distance/50*60);
				println(node.operator.name+" [距離 "+distance+"km, 費用 "+cost+"円, 時間"+time+"分]");
				answer.add((i++)+" : "+node.printMove()+" [距離 "+distance+"km, 費用 "+cost+"円, 時間"+time+"分]");
			}
			println(node.state);
		}
		String out = "合計費用 "+result.cost+"円, 合計時間　"+result.time+"分, 走行距離 "+result.movement+"km";
		println(out);
		answer.add(out);

		return answer;
	}
	public List<String> solve() {
		return solve(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	//横型探索を実行
	Result search(Node root, int depthLimit, int costLimit, int timeLimit) {
		PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparing(Node::getAllMovement,Comparator.naturalOrder())
					.thenComparing(Node::getAllCost,Comparator.naturalOrder()));
		HashSet<Node> closedList = new HashSet<>();
		openList.add(root);

		while (openList.size() > 0) {
			Node s = openList.poll();
			closedList.add(s);
			println("visit (%d) %s", s.id, s.state);

			if (isGoal(s))
				return new Result(s,s.allCost,s.allTime, s.allMovement);

			if (s.depth() < depthLimit) {
				var children = expand(s, costLimit, timeLimit, closedList);
				openList.addAll(children);
			}
		}

		return null;
	}

	//ゴール判定
	boolean isGoal(Node node) {
		var unifiers = Matcher.match(this.goal, node.state, node.bind);

		if (unifiers == null)
			return false;

		for (var b : unifiers) {
			var g = this.goal.instantiated(b);

			if (g.isGround())
				return true;
		}

		return false;
	}

	//ノードから何かしらのオペレータを適用した後の子ノードリストを返す
	List<Node> expand(Node node, int costLimit, int timeLimit, HashSet<Node> closedList) {
		var children = new ArrayList<Node>();

		for (var op : this.operators) {
			op = op.renamed();
			var unifiers = Matcher.match(op.ifList, node.state, node.bind);
			expand(node, op, unifiers, children, costLimit, timeLimit, closedList);
		}

		return children;
	}

	//現在のノードの具体化リストに、childへ移る際のオペレータの具体化を追加して子を作成、childrenに追加
	void expand(Node node, Operator operator, List<Bind> unifiers, List<Node> children, int costLimit, int timeLimit, HashSet<Node> closedList) {
		for (var b : unifiers) {
			var child = new Node();
			b = node.bind.merged(b);
			child.bind = b;
			child.operator = operator.instantiated(b);
			child.state = child.operator.appliedForward(node.state.instantiated(b));
			child.parent = node;
			child.nowSpot = spots.get(child.state.nowSpotName());
			double distance = haversine(node.nowSpot, child.nowSpot);
			//燃費 13 km/L ガソリン代 170 円/Lと仮定
			//平均時速 50kmと仮定
			int[] resource = child.getResource();
			int cost = resource[0] + (int)(distance/13*170);
			int time = resource[1] + (int)(distance/50*60);
			child.setResource(node.allCost+cost, node.allTime+time);
			child.setMovement(node.allMovement+distance);
			
			if(child.allCost <= costLimit && child.allTime <= timeLimit && !closedList.contains(child)) {
				children.add(child);
				println(" expand " + child +" [距離 "+distance+"km, 費用 "+cost+"円, 時間"+time+"分]");
			}
		}
	}
}

class Result{
	Node goal;
	int cost;
	int time;
	double movement;
	
	Result(Node goal, int cost, int time, double movement){
		this.goal = goal;
		this.cost = cost;
		this.time = time;
		this.movement = movement;
	}
}