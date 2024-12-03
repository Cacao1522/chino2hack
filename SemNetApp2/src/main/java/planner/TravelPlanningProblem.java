package planner;

import static planner.Utils.*;

import java.util.*;

public class TravelPlanningProblem implements Problem{
	List<String> inputInitialState;
	List<String> inputGoalsate;
	String goalSpot;
	
	TravelPlanningProblem(List<String> inputInitialState, List<String> inputGoalState, String goalSpot){
		this.inputInitialState = inputInitialState;
		this.inputGoalsate = inputGoalState;
		this.goalSpot = goalSpot;
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
        		        add("at ?y", "visited ?y", "do ?c"),
        		        del("at ?x", "must do ?c", "must visit ?x")),
        		 
        		//OPERATOR 2 (移動し、移動先で滞在する)
        		operator("#2: go to ?y from ?x to stay",
        		        _if("at ?x", "spot ?y", "must visit ?y"),
        		        add("at ?y", "visited ?y"),
        		        del("at ?x", "must visit ?x")),
        		
        		//OPERATOR 3 (すべて完遂しゴールする。ゴール地点は「行きたい場所、したいことがある場所」でなくても行けるようにする必要がある)
        		operator("#3: goal in " + goalSpot + " from ?x",
        		        goalState().removed(formula("at "+goalSpot)).added(formula("goal "+goalSpot,"at ?x")),
        		        add("at "+goalSpot),
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
    	
        return List.of(
				"spot 国宝犬山城",
				"spot 名古屋城",
				"spot 岡崎城",
				"spot 清州城",
				"spot 小牧山城",
				"spot トヨタ産業技術記念館",
				"spot ノリタケの森",
				"spot トヨタ博物館",
				"spot 招き猫ミュージアム",
				"spot 愛知県陶磁美術館",
				"spot 鶴舞公園",
				"spot 名城公園",
				"spot 名古屋市科学館",
				"spot 愛知県美術館",
				"spot 東山動植物園",
				"spot 名古屋港水族館",
				"spot 豊橋総合植物公園",
				"spot ラグーナテンボス",
				"spot オアシス21",
				"spot 名古屋工業大学"
				);
    }

    public List<String> activityList(){
        return List.of(
				"城を見る at 国宝犬山城",
				"城を見る at 名古屋城",
				"城を見る at 岡崎城",
				"城を見る at 清州城",
				"城を見る at 小牧山城",
				"製品を見る at トヨタ産業技術記念館",
				"製品を見る at ノリタケの森",
				"製品を見る at トヨタ博物館",
				"伝統工芸品を見る at 招き猫ミュージアム",
				"伝統工芸品を見る at 愛知県陶磁美術館",
				"自然を見る at 鶴舞公園",
				"自然を見る at 名城公園",
				"科学を体験する at 名古屋市科学館",
				"美術品を見る at 愛知県美術館",
				"動物を見る at 東山動植物園",
				"動物を見る at 名古屋港水族館",
				"動物を見る at 豊橋総合植物公園",
				"遊園地で遊ぶ at ラグーナテンボス",
				"建造物を見る at オアシス21",
				"大学に訪れる at 名古屋工業大学"
				);
    }
}
