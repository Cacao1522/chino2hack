package planner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TPPTest {

	public static void main(String args[]) {
		List<String> initialState = List.of(
				"at トヨタ博物館","visited 名古屋工業大学","goal トヨタ博物館",
				"must visit 名古屋城","must visit 名城公園","must visit ラグーナテンボス",
				"must do 城を見る","must do 伝統工芸品を見る","must do 科学を体験する"
				);
		List<String> goalState = List.of(
				"at トヨタ博物館",
				"visited 名古屋城",
				"visited 名城公園",
				"visited ラグーナテンボス",
				"do 城を見る",
				"do 伝統工芸品を見る",
				"do 科学を体験する"
				);
		Problem p = new TravelPlanningProblem(initialState, goalState, "トヨタ博物館");
		new ForwardPlanner().solve(p,initialState,goalState);
	}
}