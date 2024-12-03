package planner;

import static java.util.stream.Collectors.*;
import java.util.*;

//条件、アサーション。一行のパターンを項ごとに保存したもの
public class Predicate implements Expression {
	List<String> terms = new ArrayList<>(); //項集合

	//textを引数に空白で分割したlistを返す
	public Predicate(String text) {
		var terms = text.split(" ");
		this.terms = Arrays.asList(terms);
	}

	//termsのlistを加える
	public Predicate(List<String> terms) {
		this.terms.addAll(terms);
	}

	//クローン作成
	public Predicate clone() {
		return new Predicate(this.terms);
	}

	//ハッシュ値を返す(順列を考慮しないtermsで決定)
	public int hashCode() {
		return Objects.hash(new HashSet<>(this.terms));
	}

	//順列を考慮しないtermsで同値判定
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Predicate) == false)
			return false;
		var other = (Predicate) obj;
		if (this.terms.size() != other.size())
			return false;
		return Objects.equals(new HashSet<>(this.terms), new HashSet<>(other.terms));
	}

	//termsを空白で結合したものを表示
	public String toString() {
		return this.terms.stream().collect(joining(" "));
	}

	//termsのサイズ表示
	public int size() {
		return this.terms.size();
	}

	//?で始まる項は変数
	public static boolean isVar(String term) {
		return term.startsWith("?");
	}

	//変数でフィルターし、その中のはじめの一つを抜き出す。それが空ならtrue→変数が含まれるかの判定
	public boolean isGround() {
		return this.terms.stream().filter(t -> isVar(t)).findFirst().isEmpty();
	}

	//bを元に具体化する？
	public Predicate instantiated(Bind b) {
		var instantiatedTerms = this.terms.stream().map(b::instantiate).toList();
		return new Predicate(instantiatedTerms);
	}
}