package planner;

import java.util.*;

//特殊な操作ができるリストのようなもの
public class Formula implements Expression {
	List<Predicate> preds;

	public Formula(List<Predicate> preds) {
		this.preds = new ArrayList<Predicate>(preds);
	}

	public String toString() {
		return this.preds.toString();
	}

	//predsの各要素で具体化されていないものがなければtrue
	public boolean isGround() {
		return this.preds.stream().filter(p -> !p.isGround()).findFirst().isEmpty();
	}

	//predsの各要素を具体化
	public Formula instantiated(Bind b) {
		return new Formula(this.preds.stream().map(p -> p.instantiated(b)).toList());
	}

	//現在のpredsの後ろにgのpredsを加える
	public Formula added(Formula g) {
		return new Formula(Utils.concat(this.preds, g.preds));
	}

	//現在のpredsからgのpredsを取り除く
	public Formula removed(Formula g) {
		return new Formula(Utils.subtract(this.preds, g.preds));
	}
	
	//ハッカソン用
	public String nowSpotName() {
		for(var pred:preds) {
			if(pred.terms.isEmpty()) continue;
			if(pred.terms.get(0).equals("at")) {
				return pred.terms.get(1);
			}
		}
		return null;
	}

	//ハッシュ値を返す(順列を考慮せずpredsで決定)
	public int hashCode() {
		return Objects.hash(new HashSet<>(this.preds));
	}

	//順列を考慮せずpredsで同値判定
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Formula) == false)
			return false;
		var other = (Formula) obj;
		if (this.preds.size() != other.preds.size())
			return false;
		return Objects.equals(new HashSet<>(this.preds), new HashSet<>(other.preds));
	}
}