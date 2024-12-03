package planner;

import static planner.Utils.*;

import java.util.*;

public class TravelPlanningProblem2 implements Problem{
	List<String> inputInitialState;
	List<String> inputGoalsate;
	Spot initialSpot;
	Spot goalSpot;
	String goalSpotName;

	public TravelPlanningProblem2(List<String> inputInitialState, List<String> inputGoalState, String goalSpot){
		this.inputInitialState = inputInitialState;
		this.inputGoalsate = inputGoalState;
		this.goalSpotName = goalSpot;
	}

	public TravelPlanningProblem2(String startSpot, String goalSpot, List<String> visitList, List<String> doList){
		this.inputInitialState = makeInitialState(startSpot, goalSpot, visitList, doList);
		this.inputGoalsate = makeGoalState(goalSpot, visitList, doList);
		this.goalSpotName = goalSpot;
	}

	public TravelPlanningProblem2(double sLat, double sLon, double gLat, double gLon, List<String> visitList, List<String> doList){
		this.initialSpot = new Spot("startPoint",0,0,sLat,sLon,new HashMap<String,Activity>());
		this.goalSpot = new Spot("goalPoint",0,0,sLat,sLon,new HashMap<String,Activity>());
		this.inputInitialState = makeInitialState(initialSpot.getName(), goalSpot.getName(), visitList, doList);
		this.inputGoalsate = makeGoalState(goalSpot.getName(), visitList, doList);
		this.goalSpotName = goalSpot.getName();
	}

    public Formula initialState(){ //初期状態を返す
        int numDefaultState = 1; // "do something" で1個
        // デフォルトで与えるアサーション、ユーザからの入力、市町村・活動についてのアサーションを1つの配列にまとめる
        int numAllState = numDefaultState + inputInitialState.size() + spotList().size() + activityList().size();
        String[] allState = new String[numAllState];
        allState[0] = "do something";
        for(int i = 0; i < inputInitialState.size(); i++){
            allState[i + numDefaultState] = inputInitialState.get(i);
        }
        for (int i = 0; i < spotList().size(); i++){
            allState[i + numDefaultState + inputInitialState.size()] = spotList().get(i);
        }
        for (int i = 0; i < activityList().size(); i++){
            allState[i + numDefaultState + inputInitialState.size() + spotList().size()] = activityList().get(i);
        }

        return formula(allState);
        /* ユーザからの入力 -> アサーション
        * ・スタート地点a -> "at a", "visited a" */
    }

    public Formula goalState() { //目標状態を返す
        int numState = inputGoalsate.size();
        String[] allState = new String[numState];
        for(int i = 0; i < numState; i++){
            allState[i] = inputGoalsate.get(i);
        }

        return formula(allState);
        /* ユーザからの入力 -> アサーション
         * ・ゴール地点a -> "at a"
         * ・行きたい場所b -> "visited b"
         * ・やりたいことc -> "do c" */
    }

    public List<Operator> operators() { //動作のリストを返す
        return List.of(
        		//OPERATOR 1 （移動し、移動先で?cをする）
        		operator("#1: go to ?y from ?x to do ?c",
        		        _if("at ?x", "spot ?y", "?c at ?y", "must do ?c"),
        		        add("at ?y", "visited ?y", "did ?c"),
        		        del("at ?x", "must do ?c", "must visit ?x", "must visit ?y")),

        		//OPERATOR 2 (移動し、移動先で滞在する)
        		operator("#2: go to ?y from ?x to stay",
        		        _if("at ?x", "spot ?y", "must visit ?y"),
        		        add("at ?y", "visited ?y"),
        		        del("at ?x", "must visit ?x", "must visit ?y")),

        		//OPERATOR 3 (すべて完遂しゴールする。ゴール地点は「行きたい場所、したいことがある場所」でなくても行けるようにする必要がある)
        		operator("#3: goal in " + goalSpotName + " from ?x",
        		        goalState().removed(formula("at "+goalSpotName)).added(formula("goal "+goalSpotName,"at ?x")),
        		        add("at "+goalSpotName),
        		        del("at ?x"))

        		/*
                //OPERATOR 1 (市町村間の移動)
                operator("#1: go to ?y from ?x",
                        _if("at ?x", "spot ?y"),
                        add("at ?y", "visited ?y"),
                        del("at ?x", "spot ?y")),

                //OPERATOR 2 (市町村で何かする)
                operator("#2: do activity ?x at ?y",
                        _if("at ?y", "?x at ?y"),
                        add("do ?x"),
                        del("?x at ?y"))
                /*
        		  operator("#3: goal in ?x",
        				   _if("goal ?x"),
        				   add("at ?x"),
        				   del("do something"))
        				   */
        		  );
    }

    public List<String> spotList(){
    	var spots = DataBaseDAO.getSpots();
    	List<String> spotlist = new ArrayList<>();
    	for(var s : spots.values()) {
    		spotlist.add(s.printSpot());
    	}
    	return spotlist;
    	/*
        return List.of(
				"spot spot1",
				"spot spot2",
				"spot spot3",
				"spot spot4",
				"spot spot5",
				"spot spot6",
				"spot spot7",
				"spot spot8",
				"spot spot9",
				"spot spot10"
				);
		*/
    }

    public List<String> activityList(){
    	var spots = DataBaseDAO.getSpots();
    	List<String> activityList = new ArrayList<>();
    	for(var s : spots.values()) {
    		for(var a : s.activityMap.values()) activityList.add(a.getName() + " at " + s.getName());
    	}
    	return activityList;
    	/*
        return List.of(
        		"spot1に行く at spot1",
        		"spot2に行く at spot2",
        		"spot3に行く at spot3",
        		"spot4に行く at spot4",
        		"spot5に行く at spot5",
        		"spot6に行く at spot6",
        		"spot7に行く at spot7",
        		"spot8に行く at spot8",
        		"spot9に行く at spot9",
        		"spot10に行く at spot10",
        		"製品を見る at spot1",
        		"動物を見る at spot2",
        		"自然を見る at spot3",
        		"伝統工芸品を見る at spot4",
        		"城を見る at spot5",
        		"自然を見る at spot6",
        		"動物を見る at spot7",
        		"伝統工芸品を見る at spot8",
        		"製品を見る at spot9",
        		"城を見る at spot10"
				);
		*/
    }

    public List<String> makeInitialState(String startSpot, String goalSpot, List<String> visitList, List<String> doList){
    	List<String> initialState = new ArrayList<>();
    	initialState.add("at "+startSpot);
    	initialState.add("goal "+goalSpot);
    	for(var s : visitList) initialState.add("must visit "+ s);
    	for(var s : doList) initialState.add("must do "+ s);
    	return initialState;
    }

    public List<String> makeGoalState(String goalSpot, List<String> visitList, List<String> doList){
    	List<String> goalState = new ArrayList<>();
    	goalState.add("at "+goalSpot);
    	for(var s : visitList) goalState.add("visited "+ s);
    	for(var s : doList) goalState.add("did "+ s);
    	return goalState;
    }
}
