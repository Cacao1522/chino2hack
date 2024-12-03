package planner;

import java.util.ArrayList;

public class EX6 {

	public static void main(String argv[]) {
		Problem p = new MonkeyBananaAppleProblem();
		new ForwardPlanner().solve(p,new ArrayList<String>(), new ArrayList<String>());
	}
}