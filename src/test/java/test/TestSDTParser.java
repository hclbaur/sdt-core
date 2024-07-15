package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.function.Function;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.parser.SDTParseException;
import be.baur.sdt.parser.SDTParser;
import be.baur.sdt.transform.PrintStatement;
import be.baur.sdt.transform.Transform;
import be.baur.sdt.xpath.SDAXPath;

public final class TestSDTParser {
	
	public static void main(String[] args) throws Exception {

		Function<String, String> pf = str -> {
			try {
				return SDTParser.parse( SDA.parse(new StringReader(str)) ).toString();
			} catch (SDTParseException e) {
				return e.getLocalizedMessage();
			} catch (Exception e) {
				return e.getMessage();
			}
		};
		
		Test s = new Test(pf, "");
		Test f = new Test(pf, "error at ");
		
		Transform t1 = new Transform();
		SDAXPath x = new SDAXPath("'Hello World!'");
		t1.add(new PrintStatement(x, false));
		System.out.print(t1);
		t1.verify();
		
		// test exception
//		SDTParser sdtparser = new SDTParser();
//		sdtparser.parse(new StringReader("transform { print \"'a''b'\" }"));
		
		System.out.print("\n            "); /* test valid SDT */
		s.s("S01", "transform { }", null);
		s.s("S02", "transform { print \"'a'\" }", null);
		s.s("S03", "transform { println \"'a'\" }", null);
		s.s("S04", "transform { param \"par\" { select \"'a'\" } }", null);
		s.s("S05", "transform { variable \"var\" { select \"'a'\" } }", null);
		s.s("S06", "transform { foreach \"/item\" { } }", null);
		s.s("S07", "transform { foreach \"/item\" { println \".\" } }", null);
		s.s("S08", "transform { if \"true()\" { } }", null);
		s.s("S09", "transform { if \"true()\" { print \"'1'\" } }", null);
		s.s("S10", "transform { choose { when \"1\" { print \"1\" } } }", null);
		s.s("S11", "transform { choose { when \"1\" { } when \"0\" { print \"0\" } } }", null);
		s.s("S12", "transform { choose { when \"1\" { print \"1\" } otherwise { } } }", null);
		s.s("S13", "transform { choose { when \"1\" { print \"1\" } otherwise { print \"0\" } } }", null);
		s.s("S14", "transform { node \"a\" { } }", null);
		s.s("S15", "transform { node \"a\" { value \"'b'\" } }", null);
		s.s("S16", "transform { node \"a\" { println \"'b'\" } }", null);
		s.s("S17", "transform { node \"a\" { value \"'b'\" node \"c\" { } copy \"'d'\" } }", null);
		s.s("S18", "transform { copy \"/item\" }", null);
		s.s("S19", "transform { foreach \"/i\" { sort \".\" } }", null);
		s.s("S20", "transform { foreach \"/i\" { sort \".\" sort \".\" } }", null);
		s.s("S21", "transform { foreach \"/i\" { sort \".\" print \".\" } }", null);
		s.s("S22", "transform { foreach \"/i\" { sort \".\" sort \".\" print \".\" } }", null);
		s.s("S23", "transform { foreach \"/i\" { sort \".\" { } print \".\" } }", "transform { foreach \"/i\" { sort \".\" print \".\" } }");
		s.s("S24", "transform { foreach \"/i\" { sort \".\" { reverse \"1\" } } }", null);
		s.s("S25", "transform { foreach \"/i\" { sort \".\" { comparator \"f(?,?)\" } } }", null);
		s.s("S26", "transform { foreach \"/i\" { sort \".\" { comparator \"f(?,?)\" reverse \"0\" } } }", "transform { foreach \"/i\" { sort \".\" { reverse \"0\" comparator \"f(?,?)\" } } }");
		s.s("S27", "transform { foreach \"/i\" { group \".\" } }", null);
		s.s("S28", "transform { foreach \"/i\" { sort \".\" group \".\" } }", "transform { foreach \"/i\" { group \".\" sort \".\" } }");
		s.s("S29", "transform { foreach \"/i\" { group \".\" println \".\" } }", null);
		s.s("S30", "transform { foreach \"/i\" { group \".\" foreach \"$sdt:current-group\" { } } }", null);
		
		System.out.print("\n            "); /* test invalid SDT */
		f.s("F01", "transfrom \"\"", "/transfrom: 'transform' statement expected");
		f.s("F02", "transform \"\"", "/transform: statement 'transform' requires a compound statement");
		f.s("F03", "transform \"a\"", "/transform: statement 'transform' requires no expression");
		f.s("F04", "transform { test \"\" }", "/transform/test: keyword 'test' is unknown");
		f.s("F05", "transform { select \"\" }", "/transform/select: attribute 'select' is not allowed here");
		f.s("F06", "transform { test { } }", "/transform/test: keyword 'test' is unknown");
		f.s("F07", "transform { when { } }", "/transform/when: statement 'when' is not allowed here");
		f.s("F08", "transform { otherwise { } }", "/transform/otherwise: statement 'otherwise' is not allowed here");
		f.s("F09", "transform { sort \"\" }", "/transform/sort: statement 'sort' is not allowed here");
		f.s("F10", "transform { group \"\" }", "/transform/group: attribute 'group' is not allowed here");
		f.s("F11", "transform { print \"\" }", "/transform/print: statement 'print' requires an expression");
		f.s("F12", "transform { println { } }", "/transform/println: statement 'println' expects no compound statement");
		f.s("F13", "transform { print \"\" { } }", "/transform/print: statement 'print' expects no compound statement");
		f.s("F14", "transform { println \"\" { select \"\" } }", "/transform/println: statement 'println' expects no compound statement");

		f.s("F15", "transform { param \"\" }", "/transform/param: statement 'param' requires a compound statement");
		f.s("F16", "transform { variable { } }", "/transform/variable: statement 'variable' requires a variable name");
		f.s("F17", "transform { param \"v\" { } }", "/transform/param: 'select' attribute expected in 'param'");
		f.s("F18", "transform { variable \"v\" { value \"\" } }", "/transform/variable/value: attribute 'value' is not allowed here");
		f.s("F19", "transform { param \"\" { select \"\" } }", "/transform/param: statement 'param' requires a variable name");
		f.s("F20", "transform { variable \"v\" { select \"\" } }", "/transform/variable/select: attribute 'select' requires an expression");
		f.s("F21", "transform { param \"p\" { select \"''\" print \"0\" } }", "/transform/param/print: statement 'print' is not allowed here");
		f.s("F22", "transform { variable \":v\" { select \"''\" } }", "/transform/variable: variable name ':v' is invalid");
		f.s("F23", "transform { param \"p\" { select \"0\" } param \"p\" { select \"1\" } }", "/transform/param[2]: parameter 'p' cannot be redeclared");
		f.s("F24", "transform { if \"1\" { param \"p\" { select \"1\" } } }", "/transform/if/param: statement 'param' is not allowed here");
		f.s("F25", "transform { param \"p\" { select \"0\" } variable \"p\" { select \"1\" } }", "/transform/variable: variable 'p' cannot overwrite parameter");
		f.s("F26", "transform { variable \"v\" { select \"0\" } param \"v\" { select \"1\" } }", "/transform/param: parameter 'v' cannot overwrite variable");
		f.s("F27", "transform { param \"p\" { select \"0\" select \"1\" } }", "/transform/param/select[1]: attribute 'select' can occur only once");
		
		System.out.print("\n            ");

		f.s("F30", "transform { foreach \"\" }", "/transform/foreach: statement 'foreach' requires a compound statement");
		f.s("F31", "transform { foreach { } }", "/transform/foreach: statement 'foreach' requires an expression");
		f.s("F32", "transform { foreach \"\" { } }", "/transform/foreach: statement 'foreach' requires an expression");
		f.s("F33", "transform { foreach \"\" { test { } } }", "/transform/foreach/test: keyword 'test' is unknown");
		f.s("F34", "transform { foreach \"\" { value \"\" } }", "/transform/foreach/value: attribute 'value' is not allowed here");
		f.s("F35", "transform { foreach \"a\" { choose { } } }", "/transform/foreach/choose: 'when' statement expected in 'choose'");
		f.s("F36", "transform { foreach \"a\" { when { } } }", "/transform/foreach/when: statement 'when' is not allowed here");
		s.s("F37", "transform { foreach \"/i\" { sort \".\" { value \"\" } } }", "error at /transform/foreach/sort/value: attribute 'value' is not allowed here");
		s.s("F38", "transform { foreach \"/i\" { print \".\" sort \".\" } }", "error at /transform/foreach/sort: statement 'sort' is misplaced");
		s.s("F39", "transform { foreach \"/i\" { sort \".\" print \".\" sort \".\" } }", "error at /transform/foreach/sort[2]: statement 'sort' is misplaced");
		s.s("F40", "transform { foreach \"/i\" { group \".\" { print \".\" } } }", "error at /transform/foreach/group: attribute 'group' expects no compound statement");
		
		f.s("F41", "transform { if \"\" }", "/transform/if: statement 'if' requires a compound statement");
		f.s("F42", "transform { if { } }", "/transform/if: statement 'if' requires an expression");
		f.s("F43", "transform { if \"\" { } }", "/transform/if: statement 'if' requires an expression");
		f.s("F44", "transform { if \"\" { test { } } }", "/transform/if/test: keyword 'test' is unknown");
		f.s("F45", "transform { if \"\" { value \"\" } }", "/transform/if/value: attribute 'value' is not allowed here");
		f.s("F46", "transform { if \"true()\" { choose { } } }", "/transform/if/choose: 'when' statement expected in 'choose'");
		f.s("F47", "transform { if \"true()\" { otherwise { } }}", "/transform/if/otherwise: statement 'otherwise' is not allowed here");
		
		System.out.print("\n            ");
		f.s("F50", "transform { choose \"\" }", "/transform/choose: statement 'choose' requires a compound statement");
		f.s("F51", "transform { choose { } }", "/transform/choose: 'when' statement expected in 'choose'");
		f.s("F52", "transform { choose \"a\" { } }", "/transform/choose: statement 'choose' requires no expression");
		f.s("F53", "transform { choose { test \"\" } }", "/transform/choose/test: keyword 'test' is unknown");
		f.s("F54", "transform { choose { value \"\" } }", "/transform/choose/value: attribute 'value' is not allowed here");
		f.s("F55", "transform { choose { if { } } }", "/transform/choose/if: statement 'if' is not allowed here");
		f.s("F56", "transform { choose { otherwise \"\" } }", "/transform/choose/otherwise: statement 'otherwise' requires a compound statement");
		f.s("F57", "transform { choose { otherwise { } } }", "/transform/choose/otherwise: 'when' statement expected");
		f.s("F58", "transform { choose { otherwise \"\" { } } }", "/transform/choose/otherwise: 'when' statement expected");
		f.s("F59", "transform { choose { otherwise { value \"\" } } }", "/transform/choose/otherwise/value: attribute 'value' is not allowed here");
		f.s("F60", "transform { choose { otherwise \"a\" { print \"''\" } } }", "/transform/choose/otherwise: statement 'otherwise' requires no expression");
		f.s("F61", "transform { choose { when \"1\" } }", "/transform/choose/when: statement 'when' requires a compound statement");
		f.s("F62", "transform { choose { when { } } }", "/transform/choose/when: statement 'when' requires an expression");
		f.s("F63", "transform { choose { when \"\" { } } }", "/transform/choose/when: statement 'when' requires an expression");
		f.s("F64", "transform { choose { when { print \"''\" } } }", "/transform/choose/when: statement 'when' requires an expression");
		f.s("F65", "transform { choose { when \"1\" { select \"\" } } }", "/transform/choose/when/select: attribute 'select' is not allowed here");
		f.s("F66", "transform { choose { when \"1\" { print \"''\" } otherwise { print \"''\" }  when { } } }", "/transform/choose/otherwise: statement 'otherwise' is misplaced");
		
		f.s("F70", "transform { node \"\" }", "/transform/node: statement 'node' requires a compound statement");
		f.s("F71", "transform { node { } }", "/transform/node: statement 'node' requires a node name");
		f.s("F72", "transform { node \"a\" { select \"\" } }", "/transform/node/select: attribute 'select' is not allowed here");
		f.s("F73", "transform { node \"a\" { value \"\" } }", "/transform/node/value: attribute 'value' requires an expression");
		f.s("F74", "transform { node \"2\" { value \"''\" } }", "/transform/node: node name '2' is invalid");
		
		f.s("F80", "transform { copy \"\" }", "/transform/copy: statement 'copy' requires an expression");
		f.s("F81", "transform { copy { } }", "/transform/copy: statement 'copy' expects no compound statement");
		f.s("F82", "transform { copy \"/item\" { } }", "/transform/copy: statement 'copy' expects no compound statement");
		f.s("F83", "transform { copy \"/item\" { value \"\" } }", "/transform/copy: statement 'copy' expects no compound statement");
		
		f.s("F84", "transform { transform { } }", "/transform/transform: statement 'transform' is not allowed here");
	
		
		// test performance
		InputStream input = TestSDTParser.class.getResourceAsStream("/addressbook.sdt");
		DataNode sdt = SDA.parse(new InputStreamReader(input,"UTF-8"));
		
		PerfTest p = new PerfTest(sdtnode -> {
			try {
				SDTParser.parse(sdtnode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	
		p.test("\nPerformance: P01", sdt, 12500, 1);
	}
}
