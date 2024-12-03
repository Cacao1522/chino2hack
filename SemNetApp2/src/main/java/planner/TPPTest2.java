package planner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TPPTest2 {

	public static void main(String args[]) {
		/*
		List<String> initialState = List.of(
				"at spot5","visited spot5","goal spot5",
				"must visit spot1","must visit spot2",
				"must do 製品を見る","must do 伝統工芸品を見る"
				);
		List<String> goalState = List.of(
				"at spot5",
				"visited spot1",
				"visited spot2",
				"did 製品を見る",
				"did 伝統工芸品を見る"
				);
		*/
		String startSpot = "spot1";
		String goalSpot = "spot9";
		List<String> visitList = List.of(
				"spot1", "spot5"
				);
		List<String> doList = List.of(
				"製品を見る",
				"伝統工芸品を見る"
				);
		//Problem p = new TravelPlanningProblem2(startSpot, goalSpot, visitList, doList);
		//new TravelPlanner(p,(new DBsubstitute()).getSpots()).solve();
		

		Problem p2 = new TravelPlanningProblem2(34, 132, 36, 133, visitList, doList);
		//new TravelPlanner(p2,(new DBsubstitute()).getSpots()).solve(23000, 1500);
		List<String> answer = new TravelPlanner(p2,(new DBsubstitute()).getSpots()).solve();
		for(var s : answer) System.out.println(s);
	}
}