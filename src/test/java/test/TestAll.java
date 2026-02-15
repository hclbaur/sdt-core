package test;

public final class TestAll {

	public static void main(String[] args) throws Exception {

		System.out.print("\nSDAXPath  : ");
		test.TestSDAXPath.main(args);
		System.out.print("\nSDTXPath  : ");
		test.TestSDTXPath.main(args);
		System.out.print("\nSDTParser : ");
		test.TestSDTParser.main(args);
	}
}
