package planner;

import java.util.ArrayList;
import java.util.Arrays;

public class test {

	public static void main(String argv[]) {
		ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5));
		ArrayList<Integer> b = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5));
		ArrayList<Integer> c = new ArrayList<Integer>(Arrays.asList(5,4,3,2,1,0));
		System.out.println(a.equals(b));
		System.out.println(a.equals(c));
	}
}