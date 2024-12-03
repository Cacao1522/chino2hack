package planner;

import java.util.*;

//オペレータ
public class Operator implements Expression {
	static int lastIndex = 0;
	Predicate name;
	Formula ifList;
	Formula addList;
	Formula delList;

	public Operator(Predicate name, Formula ifList, Formula addList, Formula delList) {
		this.name = name.clone();
		this.ifList = ifList;
		this.addList = addList;
		this.delList = delList;
	}

	//オペレータの表示
	public String toString() {
		return "NAME: " + this.name + "\n" + " IF : " + this.ifList + "\n" + " ADD: " + this.addList + "\n" + " DEL: "
				+ this.delList;
	}

	//すべてのFormulaが具体化されているとtrue
	public boolean isGround() {
		return this.ifList.isGround() && this.addList.isGround() && this.delList.isGround();
	}

	//全Formulaの具体化、変数改名
	public Operator renamed() {
		Map<String, String> vars = new HashMap<>();
		long index = ++Operator.lastIndex;
		return new Operator(rename(this.name, vars, index), rename(this.ifList, vars, index),
				rename(this.addList, vars, index), rename(this.delList, vars, index));
	}

	//Formulaの具体化、変数改名
	private Formula rename(Formula f, Map<String, String> vars, long index) {
		return new Formula(f.preds.stream().map(p -> rename(p, vars, index)).toList());
	}

	//Predicateのリストの具体化、変数改名
	private Predicate rename(Predicate p, Map<String, String> vars, long index) {
		var renamedTerms = p.terms.stream().map(term -> rename(term, vars, index)).toList();
		return new Predicate(renamedTerms);
	}

	//項を具体化する。できなければ新たな変数を作成し、これも新たな具体化として登録し、新しい変数名を返す。
	private String rename(String term, Map<String, String> vars, long index) {
		if (Predicate.isVar(term) == false)
			return term;
		var newName = vars.get(term);
		if (newName == null) {
			newName = String.format("%s_%d", term, index);
			vars.put(term, newName);
		}
		return newName;
	}

	//すべてのFormulaを具体化
	public Operator instantiated(Bind b) {
		return new Operator(this.name.instantiated(b), this.ifList.instantiated(b), this.addList.instantiated(b),
				this.delList.instantiated(b));
	}

	//状態集合からdelListを取り除き、addListを加える。状態遷移
	public Formula appliedForward(Formula state) {
		state = state.removed(this.delList).added(this.addList);
		state.preds = state.preds.stream().distinct().toList();
		return state;
	}

	//状態遷移の逆をたどる
	public Formula appliedBackward(Formula goal) {
		goal = goal.removed(this.addList).added(this.ifList);
		goal.preds = goal.preds.stream().distinct().toList();
		return goal;
	}
}