package planner;

import static planner.Utils.*;

import java.util.*;

public class MonkeyBananaAppleProblem implements Problem {
	//初期状態
	public Formula initialState() {
		return formula("Monkey moving", "Monkey at A", "Monkey in Low", "Monkey likes Banana a little", "Banana at B", "Banana in High","Monkey likes Apple", "Apple at B", "Apple in Low", "Box at C");
	}

	//目標状態
	public Formula goalState() {
		return formula("Monkey has Banana", "Monkey has Apple");
	}

	//オペレータ
	public List<Operator> operators() {
		return List.of(
				// OPERATOR 1
				operator("#1: go to ?y for ?b", _if("Monkey moving", "Monkey at ?x", "Monkey in Low", "?b at ?y"),
						add("Monkey at ?y"),
						del("Monkey at ?x")),

				// OPERATOR 2
				operator("#2: push Box to ?y", _if("Monkey moving", "Monkey at ?x", "Monkey in Low", "Box at ?x", "?b at ?y"),
						add("Monkey at ?y", "Box at ?y"),
						del("Monkey at ?x", "Box at ?x")),

				// OPERATOR 3
				operator("#3: climb on Box", _if("Monkey moving", "Monkey at ?x", "Monkey in Low", "Box at ?x"),
						add("Monkey in High"),
						del("Monkey in Low")),

				// OPERATOR 4
				operator("#4: grasp ?b", _if("Monkey moving", "Monkey at ?x", "Monkey in ?y", "Monkey likes ?b a little", "?b at ?x", "?b in ?y"),
						add("Monkey has ?b"),
						del()),
				
				// OPERATOR 5
				operator("#5: grasp favorite ?b", _if("Monkey moving", "Monkey at ?x", "Monkey in ?y", "Monkey likes ?b", "?b at ?x", "?b in ?y"),
						add("Monkey has ?b", "Monkey stopping"),
						del("Monkey moving")),
				
				// OPERATOR 6
				operator("#6: eat ?b", _if("Monkey stopping", "Monkey likes ?b", "Monkey has ?b"),
						add("Monkey moving"),
						del("Monkey has ?b", "Monkey stopping")),
				
				// OPERATOR 7
				operator("#7: Get off Box", _if("Monkey moving", "Monkey at ?x", "Monkey in High", "Box at ?x"),
						add("Monkey in Low"),
						del("Monkey in High"))
				);
	}
}