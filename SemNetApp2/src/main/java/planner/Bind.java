package planner;

import java.util.*;

//マッチングを試し、具体化のマップを持つ
public class Bind implements Cloneable {
	private static Bind UNSATISFIED = new Bind(null); //マッチングできなかった場合に返される
	Map<String, String> vars = new TreeMap<>(); //具体化のマップ

	public Bind() {
	}

	private Bind(Map<String, String> vars) {
		this.vars = vars;
	}

	//varsの存在判定
	public boolean isConsistent() {
		return this.vars != null;
	}

	//クローン作成
	public Bind clone() {
		var b = new Bind();
		b.vars.putAll(this.vars);
		return b;
	}

	//ハッシュ値を返す(varsで決定)
	public int hashCode() {
		return Objects.hash(this.vars);
	}

	//varsで同値判定
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Bind) == false)
			return false;
		Bind other = (Bind) obj;
		return Objects.equals(this.vars, other.vars);
	}

	//varsを表示、varsがなければfailed
	public String toString() {
		if (isConsistent())
			return this.vars.toString();
		return "failed";
	}

	//thisのvarsあとにptherのvarsを加えたものを返す
	public Bind merged(Bind other) {
		var b = new Bind();
		b.vars.putAll(this.vars);
		b.vars.putAll(other.vars);
		return b.resolved();
	}

	//lhsとrhsのマッチング判定
	public boolean bind(String lhs, String rhs) {
		return unify(lhs, rhs, this);
	}

	//lhsとrhsのマッチング判定。(varsを変数に代入した後?)マッチングできなければUNSATISFIEDを、できればマッチングの結果を返す
	public Bind unified(Predicate lhs, Predicate rhs) {
		if (lhs.size() != rhs.size())
			return UNSATISFIED;
		lhs = lhs.instantiated(this);
		rhs = rhs.instantiated(this);
		if (lhs.equals(rhs))
			return clone();
		var b = clone();
		for (int i = 0; i < lhs.size(); i++) {
			if (unify(lhs.terms.get(i), rhs.terms.get(i), b) == false)
				return UNSATISFIED;
		}
		return b.resolved();
	}

	//マッチングの判定。1項ずつ。また、具体化の例をbのvarsに入れる
	static boolean unify(String lhs, String rhs, Bind b) {
		//すでに具体化があれば先に具体化
		lhs = b.instantiate(lhs);
		rhs = b.instantiate(rhs);
		if (lhs.equals(rhs))
			return true;
		var isVar1 = Predicate.isVar(lhs);
		var isVar2 = Predicate.isVar(rhs);
		if (isVar1 == false && isVar2 == false)
			return lhs.equals(rhs);
		if (isVar1 == false && isVar2 || isVar1 && isVar2 && lhs.compareTo(rhs) < 0) {
			b.vars.put(rhs, lhs);
		} else {
			b.vars.put(lhs, rhs);
		}
		return true;
	}

	//具体化
	String instantiate(String term) {
		//変数から変数への具体化を考慮した処理
		while (Predicate.isVar(term)) {
			var value = this.vars.get(term);
			if (value == null)
				break;
			term = value;
		}
		return term;
	}

	//具体化のmapを作成
	Bind resolved() {
		var b = new Bind();
		this.vars.keySet().forEach(term -> b.bind(term, instantiate(term)));
		return b;
	}
}