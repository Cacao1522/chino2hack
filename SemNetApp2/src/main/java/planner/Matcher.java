package planner;

import java.util.*;

public class Matcher {

	public static List<Bind> match(Formula f1, Formula f2, Bind b) {
		return match(f1.preds, f2.preds, b);
	}

	//psをすべて満たす具体化の例をqsから見つける。
	public static List<Bind> match(List<Predicate> ps, List<Predicate> qs, Bind b) {
		if (ps.size() == 0)
			return List.of(b);

		var head = ps.get(0);
		//subList(a,b) -> インデックス=aからインデックス=b-1の要素を取り出す。
		var tail = ps.subList(1, ps.size());
		var bs = new ArrayList<Bind>();

		for (var q : qs) {
			var b1 = b.unified(head, q);
			if (b1.isConsistent()) {
				var bs1 = match(tail, qs, b1).stream().filter(p -> p.isConsistent()).toList();
				bs.addAll(bs1);
			}
		}

		return bs;
	}
}