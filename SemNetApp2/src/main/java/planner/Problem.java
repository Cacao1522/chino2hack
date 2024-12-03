package planner;

import java.util.*;

//問題の条件を定義するインターフェース。初期状態、目標状態、オペレータ集合をもつ。
public interface Problem {
	Formula initialState();

	Formula goalState();

	List<Operator> operators();
}