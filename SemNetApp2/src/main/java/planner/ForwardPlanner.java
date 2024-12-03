package planner;

import static planner.Utils.*;

import java.util.*;

//問題を解くためのクラス
public class ForwardPlanner {
	List<Operator> operators;
	Formula init;
	Formula goal;

	//問題を解く
	public List<Operator> solve(Problem problem, List<String> initialState, List<String> goalState) {
		//環境構築
		this.operators = problem.operators();
		this.init = problem.initialState();
		this.goal = problem.goalState();
			
		var root = new Node(this.init, this.goal, this.init.nowSpot());

		println("***** start forward search *****");
		println("init: " + this.init);
		println("goal: " + this.goal);

		//探索実行
		Node goal = plan(root);

		if (goal == null) {
			println("**** failed ****");
			return null;
		}

		//結果表示
		println("***** This is a plan! *****");
		var plan = goal.toPlan();

		var planState = goal.toPlanState();//追加　初期から目標までのStateを記録
		
		int i=0; //追加
		for (var action : plan) {
			println(planState.get(i++)); //追加
			println(action.name);
		}
		println(planState.get(i)); //追加

		return plan;
	}

	//課題の都合上、最大12まで
	Node plan(Node root) {
		final int maxDepthLimit = 16; //13に変更
		int depthLimit = 4;

		while (depthLimit < maxDepthLimit) {
			println("******************");
			println("depth limit: %d", depthLimit);
			var goal = search(root, depthLimit);

			if (goal != null)
				return goal;

			depthLimit += 1;
		}

		return null;
	}

	//横型探索を実行
	Node search(Node root, int depthLimit) {
		List<Node> openList = new ArrayList<>();
		openList.add(root);

		while (openList.size() > 0) {
			Node s = openList.remove(0);
			println("visit (%d) %s", s.id, s.state);

			if (isGoal(s))
				return s;

			if (s.depth() < depthLimit) {
				var children = expand(s);
				openList = concat(openList, children);
				//openList = concat(children, openList);
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
	List<Node> expand(Node node) {
		var children = new ArrayList<Node>();

		for (var op : this.operators) {
			op = op.renamed();
			var unifiers = Matcher.match(op.ifList, node.state, node.bind);
			expand(node, op, unifiers, children);
		}

		return children;
	}

	//現在のノードの具体化リストに、childへ移る際のオペレータの具体化を追加して子を作成、childrenに追加
	void expand(Node node, Operator operator, List<Bind> unifiers, List<Node> children) {
		for (var b : unifiers) {
			var child = new Node();
			b = node.bind.merged(b);
			child.bind = b;
			child.operator = operator.instantiated(b);
			child.state = child.operator.appliedForward(node.state.instantiated(b));
			child.parent = node;
			child.nowSpot = child.state.nowSpot();
			children.add(child);
			println(" expand " + child);
		}
	}
}