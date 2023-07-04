package test;


import java.util.function.BiFunction;
import java.util.function.Function;

/** A convenience class with testing methods that accept Lambda expressions */
public class Test {

	Function<String, String> function;
	BiFunction<String, Object, String> bifunc;
	
	public Test(Function<String, String> function) {
		this.function = function;
	}
	
	public Test(BiFunction<String, Object, String> bifunction) {
		this.bifunc = bifunction;
	}
	
	
	public void s(String scenario, String str, String expected) {
		
		String result = function.apply(str);

		if (expected == null) expected = str;
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
		}
	}
	
	
	public void so(String scenario, String str, Object obj, String expected) {
		
		String result = bifunc.apply(str, obj);

		if (expected == null) expected = str;
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
		}
	}
	
}
