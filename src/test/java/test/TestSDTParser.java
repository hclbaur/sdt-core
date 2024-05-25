package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.function.Function;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.serialization.SDTParseException;
import be.baur.sdt.serialization.SDTParser;
import be.baur.sdt.statements.PrintStatement;
import be.baur.sdt.statements.Transform;
import be.baur.sdt.xpath.SDAXPath;

public final class TestSDTParser {
	
	public static void main(String[] args) throws Exception {

		Function<String, String> pf = str -> {
			try {
				return SDTParser.parse( SDA.parse(new StringReader(str)) ).toString();
			} catch (SDTParseException e) {
				return e.getMessage();
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
		s.s("S06", "transform { param \"par\" { select \"'a'\" } print \"$par\" }", null);
		//s.s("S07", "transform { foreach \"/item\" { } }", null);
		s.s("S07", "transform { foreach \"/item\" { println \".\" } }", null);
		s.s("S08", "transform { if \"true()\" { print \"'1'\" } }", null);
		s.s("S09", "transform { choose { when \"1\" { print \"1\" } } }", null);
		s.s("S10", "transform { choose { when \"1\" { print \"1\" } when \"0\" { print \"0\" } } }", null);
		s.s("S11", "transform { choose { when \"1\" { print \"1\" } otherwise { print \"0\" } } }", null);
		s.s("S12", "transform { node \"a\" { value \"'b'\" } }", null);
		s.s("S13", "transform { node \"a\" { node \"b\" { value \"'c'\" } } }", null);
		s.s("S14", "transform { node \"a\" { value \"'b'\" } node \"c\" { value \"'d'\" } }", null);
		s.s("S15", "transform { copy { select \"/item\" } }", null);
		s.s("S16", "transform { foreach \"/i\" { sort \".\" print \".\" } }", null);
		s.s("S17", "transform { foreach \"/i\" { sort \".\" { } print \".\" } }", "transform { foreach \"/i\" { sort \".\" print \".\" } }");
		s.s("S18", "transform { foreach \"/i\" { sort \".\" { reverse \"1\" } print \".\" } }", null);
		
		System.out.print("\n            "); /* test invalid SDT */
		f.s("F01", "transfrom \"\"", "/transfrom: 'transform' statement expected");
		f.s("F02", "transform \"\"", "/transform: statement 'transform' requires a compound statement");
		f.s("F03", "transform { test \"\" }", "/transform/test: statement 'test' is unknown");
		f.s("F04", "transform { select \"\" }", "/transform/select: statement 'select' is not allowed here");
		f.s("F05", "transform { test { } }", "/transform/test: statement 'test' is unknown");
		f.s("F06", "transform { when { } }", "/transform/when: statement 'when' is not allowed here");
		f.s("F07", "transform { otherwise { } }", "/transform/otherwise: statement 'otherwise' is not allowed here");

		f.s("F08", "transform { print \"\" }", "/transform/print: statement 'print' requires an XPath expression");
		f.s("F09", "transform { print { } }", "/transform/print: statement 'print' expects no compound statement");
		f.s("F10", "transform { print \"\" { } }", "/transform/print: statement 'print' expects no compound statement");
		f.s("F11", "transform { print \"\" { select \"\" } }", "/transform/print: statement 'print' expects no compound statement");

		f.s("F12", "transform { println \"\" }", "/transform/println: statement 'println' requires an XPath expression");
		f.s("F13", "transform { println { } }", "/transform/println: statement 'println' expects no compound statement");
		f.s("F14", "transform { println \"\" { } }", "/transform/println: statement 'println' expects no compound statement");
		f.s("F15", "transform { println \"\" { select \"\" } }", "/transform/println: statement 'println' expects no compound statement");

		f.s("F20", "transform { param \"\" }", "/transform/param: statement 'param' requires a compound statement");
		f.s("F21", "transform { param { } }", "/transform/param: statement 'param' requires a variable name");
		f.s("F22", "transform { param \"\" { } }", "/transform/param: statement 'param' requires a variable name");
		f.s("F23", "transform { param \"\" { value \"\" } }", "/transform/param: statement 'param' requires a variable name");
		f.s("F24", "transform { param \"a\" { value \"\" } }", "/transform/param/value: statement 'value' is not allowed here");
		f.s("F25", "transform { param \"\" { select \"\" } }", "/transform/param: statement 'param' requires a variable name");
		f.s("F26", "transform { param \"a\" { select \"\" } }", "/transform/param/select: statement 'select' requires an XPath expression");
		f.s("F27", "transform { param \"a\" { select \"''\" print { } } }", "/transform/param/print: statement 'print' is not allowed here");
		f.s("F28", "transform { param \":a\" { select \"''\" } }", "/transform/param: variable name ':a' is invalid");
		f.s("F29", "transform { param \"p\" { select \"''\" } param \"p\" { select \"''\" } }", "/transform/param[1]: parameter 'p' cannot be redeclared");
		f.s("F30", "transform { if \"1\" { param \"p\" { select \"''\" } } }", "/transform/if/param: statement 'param' is not allowed here");
		
		System.out.print("\n            ");
		f.s("F31", "transform { variable \"\" }", "/transform/variable: statement 'variable' requires a compound statement");
		f.s("F32", "transform { variable { } }", "/transform/variable: statement 'variable' requires a variable name");
		f.s("F33", "transform { variable \"\" { } }", "/transform/variable: statement 'variable' requires a variable name");
		f.s("F34", "transform { variable \"\" { value \"\" } }", "/transform/variable: statement 'variable' requires a variable name");
		f.s("F35", "transform { variable \"a\" { value \"\" } }", "/transform/variable/value: statement 'value' is not allowed here");
		f.s("F36", "transform { variable \"\" { select \"\" } }", "/transform/variable: statement 'variable' requires a variable name");
		f.s("F37", "transform { variable \"a\" { select \"\" } }", "/transform/variable/select: statement 'select' requires an XPath expression");
		f.s("F38", "transform { variable \"a\" { select \"''\" print { } } }", "/transform/variable/print: statement 'print' is not allowed here");		
		f.s("F39", "transform { variable \":a\" { select \"''\" } }", "/transform/variable: variable name ':a' is invalid");

		f.s("F40", "transform { foreach \"\" }", "/transform/foreach: statement 'foreach' requires a compound statement");
		f.s("F41", "transform { foreach { } }", "/transform/foreach: statement 'foreach' requires an XPath expression");
		f.s("F42", "transform { foreach \"\" { } }", "/transform/foreach: statement 'foreach' requires an XPath expression");
		f.s("F43", "transform { foreach \"\" { value \"\" } }", "/transform/foreach: statement 'foreach' requires an XPath expression");
		f.s("F44", "transform { foreach \"a\" { value \"\" } }", "/transform/foreach/value: statement 'value' is not allowed here");
		f.s("F45", "transform { foreach \"a\" { choose { } } }", "/transform/foreach/choose: 'when' statement expected in 'choose'");
		f.s("F46", "transform { foreach \"a\" { when { } }}", "/transform/foreach/when: statement 'when' is not allowed here");
		
		f.s("F47", "transform { if \"\" }", "/transform/if: statement 'if' requires a compound statement");
		f.s("F48", "transform { if { } }", "/transform/if: statement 'if' requires an XPath expression");
		f.s("F49", "transform { if \"\" { } }", "/transform/if: statement 'if' requires an XPath expression");
		f.s("F50", "transform { if \"\" { value \"\" } }", "/transform/if: statement 'if' requires an XPath expression");
		f.s("F51", "transform { if \"true()\" { value \"\" } }", "/transform/if/value: statement 'value' is not allowed here");
		f.s("F52", "transform { if \"true()\" { choose { } } }", "/transform/if/choose: 'when' statement expected in 'choose'");
		f.s("F53", "transform { if \"true()\" { otherwise { } }}", "/transform/if/otherwise: statement 'otherwise' is not allowed here");
		
		System.out.print("\n            ");
		f.s("F54", "transform { choose \"\" }", "/transform/choose: statement 'choose' requires a compound statement");
		f.s("F55", "transform { choose { } }", "/transform/choose: 'when' statement expected in 'choose'");
		f.s("F56", "transform { choose \"a\" { } }", "/transform/choose: statement 'choose' requires no value");
		f.s("F57", "transform { choose { value \"\" } }", "/transform/choose/value: statement 'value' is not allowed here");
		f.s("F58", "transform { choose { if { } } }", "/transform/choose/if: statement 'if' is not allowed here");
		f.s("F59", "transform { choose { otherwise \"''\" } }", "/transform/choose/otherwise: statement 'otherwise' requires a compound statement");
		f.s("F60", "transform { choose { otherwise { } } }", "/transform/choose/otherwise: 'when' statement expected");
		f.s("F61", "transform { choose { otherwise \"\" { } } }", "/transform/choose/otherwise: 'when' statement expected");
		f.s("F62", "transform { choose { otherwise { print \"''\" } } }", "/transform/choose/otherwise: 'when' statement expected");
		f.s("F63", "transform { choose { otherwise \"a\" { print \"''\" } } }", "/transform/choose/otherwise: statement 'otherwise' requires no value");
		f.s("F64", "transform { choose { when \"''\" } }", "/transform/choose/when: statement 'when' requires a compound statement");
		f.s("F65", "transform { choose { when { } } }", "/transform/choose/when: statement 'when' requires an XPath expression");
		f.s("F66", "transform { choose { when \"\" { } } }", "/transform/choose/when: statement 'when' requires an XPath expression");
		f.s("F67", "transform { choose { when { print \"''\" } } }", "/transform/choose/when: statement 'when' requires an XPath expression");
		f.s("F68", "transform { choose { when \"true()\" { print \"''\" } otherwise { print \"''\" }  when { } } }", "/transform/choose/otherwise: statement 'otherwise' is misplaced");
		
		f.s("F69", "transform { node \"\" }", "/transform/node: statement 'node' requires a compound statement");
		f.s("F70", "transform { node { } }", "/transform/node: statement 'node' requires a node name");
		f.s("F71", "transform { node \"\" { } }", "/transform/node: statement 'node' requires a node name");
		f.s("F72", "transform { node \"\" { select \"\" } }", "/transform/node: statement 'node' requires a node name");
		f.s("F73", "transform { node \"a\" { select \"\" } }", "/transform/node/select: statement 'select' is not allowed here");
		f.s("F74", "transform { node \"a\" { value \"\" } }", "/transform/node/value: statement 'value' requires an XPath expression");
		f.s("F75", "transform { node \"2\" { value \"''\" } }", "/transform/node: node name '2' is invalid");

		f.s("F76", "transform { copy \"\" }", "/transform/copy: statement 'copy' requires a compound statement");
		f.s("F77", "transform { copy { } }", "/transform/copy: 'select' statement expected in 'copy'");
		f.s("F78", "transform { copy \"\" { } }", "/transform/copy: 'select' statement expected in 'copy'");
		f.s("F79", "transform { copy \"\" { value \"\" } }", "/transform/copy/value: statement 'value' is not allowed here");
		f.s("F80", "transform { copy \"\" { select \"\" } }", "/transform/copy/select: statement 'select' requires an XPath expression");
		f.s("F81", "transform { copy \"a\" { value \"''\" } }", "/transform/copy: statement 'copy' requires no value");
		
		/* odd */ f.s("F82", "transform { transform { } }", "/transform/transform: statement 'transform' is unknown");
	
		
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
	
		p.test("\nPerformance: P01", sdt, 12500, 30);
	}
}
