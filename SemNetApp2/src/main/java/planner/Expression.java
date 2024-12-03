package planner;

//表現。少なくともすべて具体化されているかの判定、bindによる具体化をする
public interface Expression {
	boolean isGround();

	Expression instantiated(Bind b);
}