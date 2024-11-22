package semnet;

import java.util.ArrayList;

/***
 * Semantic Net の使用例
 */
/*public class Example {
	public static void main(String args[]) {
		SemanticNet sn = new SemanticNet();

		// 野球はスポーツである．
		sn.addLink(new Link("is-a", "baseball", "sports", sn));

		// 太郎は名古屋工業大学の学生である．
		sn.addLink(new Link("is-a", "Taro", "NIT-student", sn));

		// 太郎の専門は人工知能である．
		sn.addLink(new Link("speciality", "Taro", "AI", sn));

		// フェラーリは車である．
		sn.addLink(new Link("is-a", "Ferrari", "car", sn));

		// 車はエンジンを持つ．
		sn.addLink(new Link("has-a", "car", "engine", sn));

		// 太郎の趣味は野球である．
		sn.addLink(new Link("hobby", "Taro", "baseball", sn));

		// 太郎はフェラーリを所有する．
		sn.addLink(new Link("own", "Taro", "Ferrari", sn));

		// 名古屋工業大学の学生は，学生である．
		sn.addLink(new Link("is-a", "NIT-student", "student", sn));

		// 学生は勉強しない．
		sn.addLink(new Link("donot", "student", "study", sn));

		sn.printLinks();
		sn.printNodes();
		//System.out.println("ok");
		ArrayList<Link> query = new ArrayList<Link>();
		query.add(new Link("own", "?y", "Ferrari"));
		query.add(new Link("is-a", "?y", "student"));
		query.add(new Link("hobby", "?y", "baseball"));
		sn.query(query);
	}
}*/
