package test;

import junit.framework.TestCase;

public final class RunTest extends TestCase {


    public void testAll() throws Exception  {
        RunTest.main(new String[]{});
    }
 
	public static void main(String[] args) throws Exception {

		System.out.print("\nSDAXPath  : ");
		test.TestSDAXPath.main(args);
		System.out.print("\nSDTXPath  : ");
		test.TestSDTXPath.main(args);
		System.out.print("\nSDTParser : ");
		test.TestSDTParser.main(args);
	}
}
