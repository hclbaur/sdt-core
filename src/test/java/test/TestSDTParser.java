package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sda.serialization.Parser;
import be.baur.sdt.Transform;
import be.baur.sdt.serialization.SDTParseException;
import be.baur.sdt.serialization.SDTParser;
import be.baur.sdt.statements.PrintStatement;
import be.baur.sdt.xpath.SDAXPath;

public final class TestSDTParser {

	private static final Parser<DataNode> sdaparser = SDA.parser();
	
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			try {
				return SDTParser.parse( sdaparser.parse(new StringReader(s)) ).toString();
			} catch (SDTParseException e) {
				return e.getErrorNode().path() + ": " + e.getMessage();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		Transform t1 = new Transform();
		SDAXPath x = new SDAXPath("'Hello World!'");
		t1.add(new PrintStatement(x, false));
		System.out.print(t1);
		t1.verify();
		
		// test exception
//		SDTParser sdtparser = new SDTParser();
//		sdtparser.parse(new StringReader("transform { print { value \"'a''b'\" } }"));
		
		System.out.print("\n            "); /* test valid SDT */
		t.s("S01", "transform { }", null);
		t.s("S02", "transform { print { value \"'a'\" } }", null);
		t.s("S03", "transform { println { value \"'a'\" } }", null);
		t.s("S04", "transform { param \"par\" { select \"'a'\" } }", null);
		t.s("S05", "transform { variable \"var\" { select \"'a'\" } }", null);
		t.s("S06", "transform { param \"par\" { select \"'a'\" } print { value \"$par\" } }", null);
		t.s("S07", "transform { foreach \"/item\" { println { value \".\" } } }", null);
		t.s("S08", "transform { if \"true()\" { print { value \"'1'\" } } }", null);
		t.s("S09", "transform { choose { when \"1\" { print { value \"1\" } } } }", null);
		t.s("S10", "transform { choose { when \"1\" { print { value \"1\" } } when \"0\" { print { value \"0\" } } } }", null);
		t.s("S11", "transform { choose { when \"1\" { print { value \"1\" } } otherwise { print { value \"0\" } } } }", null);
		t.s("S12", "transform { node \"a\" { value \"'b'\" } }", null);
		t.s("S13", "transform { node \"a\" { node \"b\" { value \"'c'\" } } }", null);
		t.s("S14", "transform { node \"a\" { value \"'b'\" } node \"c\" { value \"'d'\" } }", null);
		t.s("S15", "transform { copy { select \"/item\" } }", null);
		
		System.out.print("\n            "); /* test invalid SDT */
		t.s("F01", "transfrom \"\"", "/transfrom: 'transform' statement expected");
		t.s("F02", "transform \"\"", "/transform: statement 'transform' is incomplete");
		t.s("F03", "transform { test \"\" }", "/transform/test: attribute 'test' is unknown");
		t.s("F04", "transform { select \"\" }", "/transform/select: attribute 'select' is not allowed here");
		t.s("F05", "transform { test { } }", "/transform/test: statement 'test' is unknown");
		t.s("F06", "transform { when { } }", "/transform/when: statement 'when' is not allowed here");
		t.s("F07", "transform { otherwise { } }", "/transform/otherwise: statement 'otherwise' is not allowed here");

		t.s("F08", "transform { print \"\" }", "/transform/print: attribute 'print' is unknown");
		t.s("F09", "transform { print { } }", "/transform/print: statement 'print' is incomplete");
		t.s("F10", "transform { print \"\" { } }", "/transform/print: statement 'print' is incomplete");
		t.s("F11", "transform { print \"\" { select \"\" } }", "/transform/print/select: attribute 'select' is not allowed here");
		t.s("F12", "transform { print \"\" { value \"\" } }", "/transform/print/value: attribute 'value' requires a value");
		t.s("F13", "transform { print \"a\" { value \"''\" } }", "/transform/print: statement 'print' requires no value");

		t.s("F14", "transform { println \"\" }", "/transform/println: attribute 'println' is unknown");
		t.s("F15", "transform { println { } }", "/transform/println: statement 'println' is incomplete");
		t.s("F16", "transform { println \"\" { } }", "/transform/println: statement 'println' is incomplete");
		t.s("F17", "transform { println \"\" { select \"\" } }", "/transform/println/select: attribute 'select' is not allowed here");
		t.s("F18", "transform { println \"\" { value \"\" } }", "/transform/println/value: attribute 'value' requires a value");
		t.s("F19", "transform { println \"a\" { value \"''\" } }", "/transform/println: statement 'println' requires no value");

		t.s("F20", "transform { param \"\" }", "/transform/param: attribute 'param' is unknown");
		t.s("F21", "transform { param { } }", "/transform/param: statement 'param' is incomplete");
		t.s("F22", "transform { param \"\" { } }", "/transform/param: statement 'param' is incomplete");
		t.s("F23", "transform { param \"\" { value \"\" } }", "/transform/param: statement 'param' requires a variable name");
		t.s("F24", "transform { param \"a\" { value \"\" } }", "/transform/param/value: attribute 'value' is not allowed here");
		t.s("F25", "transform { param \"\" { select \"\" } }", "/transform/param: statement 'param' requires a variable name");
		t.s("F26", "transform { param \"a\" { select \"\" } }", "/transform/param/select: attribute 'select' requires a value");
		t.s("F27", "transform { param \"a\" { select \"''\" print { } } }", "/transform/param/print: statement 'print' is not allowed here");
		t.s("F28", "transform { param \":a\" { select \"''\" } }", "/transform/param: variable name ':a' is invalid");
		t.s("F29", "transform { param \"p\" { select \"''\" } param \"p\" { select \"''\" } }", "/transform/param[1]: parameter 'p' cannot be redeclared");
		t.s("F30", "transform { if \"1\" { param \"p\" { select \"''\" } } }", "/transform/if/param: statement 'param' is not allowed here");
		
		System.out.print("\n            ");
		t.s("F31", "transform { variable \"\" }", "/transform/variable: attribute 'variable' is unknown");
		t.s("F32", "transform { variable { } }", "/transform/variable: statement 'variable' is incomplete");
		t.s("F33", "transform { variable \"\" { } }", "/transform/variable: statement 'variable' is incomplete");
		t.s("F34", "transform { variable \"\" { value \"\" } }", "/transform/variable: statement 'variable' requires a variable name");
		t.s("F35", "transform { variable \"a\" { value \"\" } }", "/transform/variable/value: attribute 'value' is not allowed here");
		t.s("F36", "transform { variable \"\" { select \"\" } }", "/transform/variable: statement 'variable' requires a variable name");
		t.s("F37", "transform { variable \"a\" { select \"\" } }", "/transform/variable/select: attribute 'select' requires a value");
		t.s("F38", "transform { variable \"a\" { select \"''\" print { } } }", "/transform/variable/print: statement 'print' is not allowed here");		
		t.s("F39", "transform { variable \":a\" { select \"''\" } }", "/transform/variable: variable name ':a' is invalid");

		t.s("F40", "transform { foreach \"\" }", "/transform/foreach: attribute 'foreach' is unknown");
		t.s("F41", "transform { foreach { } }", "/transform/foreach: statement 'foreach' is incomplete");
		t.s("F42", "transform { foreach \"\" { } }", "/transform/foreach: statement 'foreach' is incomplete");
		t.s("F43", "transform { foreach \"\" { value \"\" } }", "/transform/foreach: statement 'foreach' requires an XPath expression");
		t.s("F44", "transform { foreach \"a\" { value \"\" } }", "/transform/foreach/value: attribute 'value' is not allowed here");
		t.s("F45", "transform { foreach \"a\" { choose { } } }", "/transform/foreach/choose: statement 'choose' is incomplete");
		t.s("F46", "transform { foreach \"a\" { when { } }}", "/transform/foreach/when: statement 'when' is not allowed here");
		
		t.s("F47", "transform { if \"\" }", "/transform/if: attribute 'if' is unknown");
		t.s("F48", "transform { if { } }", "/transform/if: statement 'if' is incomplete");
		t.s("F49", "transform { if \"\" { } }", "/transform/if: statement 'if' is incomplete");
		t.s("F50", "transform { if \"\" { value \"\" } }", "/transform/if: statement 'if' requires an XPath expression");
		t.s("F51", "transform { if \"true()\" { value \"\" } }", "/transform/if/value: attribute 'value' is not allowed here");
		t.s("F52", "transform { if \"true()\" { choose { } } }", "/transform/if/choose: statement 'choose' is incomplete");
		t.s("F53", "transform { if \"true()\" { otherwise { } }}", "/transform/if/otherwise: statement 'otherwise' is not allowed here");
		
		System.out.print("\n            ");
		t.s("F54", "transform { choose \"\" }", "/transform/choose: attribute 'choose' is unknown");
		t.s("F55", "transform { choose { } }", "/transform/choose: statement 'choose' is incomplete");
		t.s("F56", "transform { choose \"\" { } }", "/transform/choose: statement 'choose' is incomplete");
		t.s("F57", "transform { choose { value \"\" } }", "/transform/choose/value: attribute 'value' is not allowed here");
		t.s("F58", "transform { choose { if { } } }", "/transform/choose/if: statement 'if' is not allowed here");
		t.s("F59", "transform { choose { otherwise \"''\" } }", "/transform/choose/otherwise: attribute 'otherwise' is unknown");
		t.s("F60", "transform { choose { otherwise { } } }", "/transform/choose/otherwise: statement 'otherwise' is incomplete");
		t.s("F61", "transform { choose { otherwise \"\" { } } }", "/transform/choose/otherwise: statement 'otherwise' is incomplete");
		t.s("F62", "transform { choose { otherwise { print { value \"''\" } } } }", "/transform/choose/otherwise: 'when' statement expected");
		t.s("F63", "transform { choose { otherwise \"a\" { print { value \"''\" } } } }", "/transform/choose/otherwise: statement 'otherwise' requires no value");
		t.s("F64", "transform { choose { when \"''\" } }", "/transform/choose/when: attribute 'when' is unknown");
		t.s("F65", "transform { choose { when { } } }", "/transform/choose/when: statement 'when' is incomplete");
		t.s("F66", "transform { choose { when \"\" { } } }", "/transform/choose/when: statement 'when' is incomplete");
		t.s("F67", "transform { choose { when { print { value \"''\" } } } }", "/transform/choose/when: statement 'when' requires an XPath expression");
		t.s("F68", "transform { choose { when \"true()\" { print { value \"''\" } } otherwise { print { value \"''\" } }  when { } } }", "/transform/choose/otherwise: statement 'otherwise' is misplaced");
		
		t.s("F69", "transform { node \"\" }", "/transform/node: attribute 'node' is unknown");
		t.s("F70", "transform { node { } }", "/transform/node: statement 'node' is incomplete");
		t.s("F71", "transform { node \"\" { } }", "/transform/node: statement 'node' is incomplete");
		t.s("F72", "transform { node \"\" { select \"\" } }", "/transform/node: statement 'node' requires a node name");
		t.s("F73", "transform { node \"a\" { select \"\" } }", "/transform/node/select: attribute 'select' is not allowed here");
		t.s("F74", "transform { node \"a\" { value \"\" } }", "/transform/node/value: attribute 'value' requires a value");
		t.s("F75", "transform { node \"2\" { value \"''\" } }", "/transform/node: node name '2' is invalid");

		t.s("F76", "transform { copy \"\" }", "/transform/copy: attribute 'copy' is unknown");
		t.s("F77", "transform { copy { } }", "/transform/copy: statement 'copy' is incomplete");
		t.s("F78", "transform { copy \"\" { } }", "/transform/copy: statement 'copy' is incomplete");
		t.s("F79", "transform { copy \"\" { value \"\" } }", "/transform/copy/value: attribute 'value' is not allowed here");
		t.s("F80", "transform { copy \"\" { select \"\" } }", "/transform/copy/select: attribute 'select' requires a value");
		t.s("F81", "transform { copy \"a\" { value \"''\" } }", "/transform/copy: statement 'copy' requires no value");
		
		/* odd */ t.s("F82", "transform { transform { } }", "/transform/transform: statement 'transform' is unknown");
	
		
		// test performance
		InputStream input = TestSDTParser.class.getResourceAsStream("/addressbook.sdt");
		DataNode sdt = sdaparser.parse(new InputStreamReader(input,"UTF-8"));
		
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
