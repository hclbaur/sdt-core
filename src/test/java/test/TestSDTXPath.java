package test;
import java.time.ZoneId;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sdt.xpath.DocumentNavigator;
import be.baur.sdt.xpath.SDAXPath;

public class TestSDTXPath {

	public static void main(String[] args) throws Exception {

		DocumentNavigator nav = (DocumentNavigator) DocumentNavigator.getInstance();
		
		Test t = new Test( (str,obj) -> {
			try {
				XPath xpath = SDAXPath.withSDTSupport(str);
				return xpath.evaluate(obj).toString();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		String file = TestSDTXPath.class.getResource("/addressbook.sda").getFile();
		Node doc = DocumentNavigator.newDocumentNode((DataNode) nav.getDocument(file));
		
		Node addressbook = doc.nodes().get(0);
		//List<Node> contacts = addressbook.nodes();
		//Node alice = contacts.get(0); 
		//Node bob = contacts.get(1);
		
		t.so("S1", "fn:string-join(/addressbook/contact/phonenumber)", doc, "06-1111111106-2222222206-3333333306-44444444");
		t.so("S2", "fn:string-join(contact | contact/firstname,':')", addressbook, "1:Alice:2:Bob");
		
		t.so("S3", "sdt:left(/addressbook/contact[1]/firstname,0)", doc, "");
		t.so("S4", "sdt:left(/addressbook/contact[1]/firstname,2)", doc, "Al");
		t.so("S5", "sdt:left(/addressbook/contact[1]/firstname,6)", doc, "Alice");
		
		t.so("S6", "sdt:right(/addressbook/contact[2]/firstname,0)", doc, "");
		t.so("S7", "sdt:right(/addressbook/contact[2]/firstname,2)", doc, "ob");
		t.so("S8", "sdt:right(/addressbook/contact[2]/firstname,4)", doc, "Bob");
		
		t.so("S11", "sdt:compare-number(1,3)", doc, "-1.0");
		t.so("S12", "sdt:compare-number(3,'3')", doc, "0.0");
		t.so("S13", "sdt:compare-number('6','4')", doc, "1.0");
		t.so("S14", "sdt:compare-number('a',1)", doc, "1.0");
		t.so("S15", "sdt:compare-number('a','b')", doc, "0.0");
		t.so("S16", "sdt:compare-number('a',1,true())", doc, "-1.0");
		t.so("S17", "sdt:compare-number('a',1,false())", doc, "1.0");
		t.so("S18", "sdt:compare-number('a','b',true())", doc, "0.0");
		t.so("S19", "sdt:compare-number('a','b',false())", doc, "0.0");
		
		t.so("S21", "sdt:compare-string('a','A')", doc, "-1.0");
		t.so("S22", "sdt:compare-string(3,'3')", doc, "0.0");
		t.so("S23", "sdt:compare-string('b','A')", doc, "1.0");
		t.so("S24", "sdt:compare-string('Ångström','Zulu','en')", doc, "-1.0");
		t.so("S25", "sdt:compare-string('Ångström','Zulu','sv')", doc, "1.0");

		t.so("S31", "sdt:tokenize('')", doc, "[]");
		t.so("S32", "sdt:tokenize('abc')", doc, "abc");
		t.so("S33", "sdt:tokenize('abc','')", doc, "[a, b, c]");
		t.so("S34", "sdt:tokenize(' a  b   c    ')", doc, "[a, b, c]");
		t.so("S35", "sdt:tokenize('127.0.0.1:80','[\\.:]')", doc, "[127, 0, 0, 1, 80]");
		t.so("S36", "sdt:tokenize('1;2;;3;',';')", doc, "[1, 2, 3]");
		t.so("S37", "sdt:tokenize('1; 2; ; 3; ','; ',true())", doc, "[1, 2, , 3, ]");
		
		t.so("S41", "sdt:render-sda('')", doc, "");
		t.so("S42", "sdt:render-sda(unknown)", doc, "");
		t.so("S43", "sdt:render-sda(/addressbook/contact/firstname)", doc, "firstname \"Alice\"");
		t.so("S44", "sdt:render-sda(/addressbook/contact/phonenumber)", doc, "phonenumber \"06-11111111\"");
		t.so("S45", "sdt:render-sda(/addressbook/contact[2])", doc, "contact \"2\" { firstname \"Bob\" phonenumber \"06-33333333\" phonenumber \"06-44444444\" }");
		t.so("F46", "sdt:parse-sda('')", doc, "unexpected end of input");
		t.so("F47", "sdt:parse-sda('greeting message \"hello\" }')", doc, "unexpected character 'm'");
		t.so("S48", "sdt:parse-sda('greeting { message \"hello\" }')", doc, "[greeting { message \"hello\" }]");
		
		System.out.print("\n	    ");
		
		t.so("S51", "sdt:dateTime('1968-02-28T12:00')", doc, "1968-02-28T12:00:00");
		t.so("S52", "sdt:dateTime('1968-02-28T12:00+01:00')", doc, "1968-02-28T12:00:00+01:00");
		t.so("S53", "sdt:dateTime('1968-02-28T12:00:00.000Z')", doc, "1968-02-28T12:00:00Z");
		t.so("F54", "sdt:dateTime('a')", doc, "dateTime() argument 'a' is invalid.");
		t.so("F55", "sdt:dateTime()", doc, "dateTime() requires exactly one argument.");
		t.so("S56", "sdt:format-dateTime('1968-02-28T12:00','yyyy/MM/dd HH:mm')", doc, "1968/02/28 12:00");
		t.so("S57", "sdt:format-dateTime(sdt:millis-to-dateTime(0),'yyyyMMddHHmmss')", doc, "19700101000000");
		t.so("F58", "sdt:format-dateTime('a','yyyyMMddHHmmss')", doc, "format-dateTime() argument 'a' is invalid.");
		t.so("F59", "sdt:format-dateTime()", doc, "format-dateTime() requires two arguments.");
		
		t.so("S61", "sdt:millis-to-dateTime(0)", doc, "1970-01-01T00:00:00Z");
		t.so("S62", "sdt:millis-to-dateTime(-3600000)", doc, "1969-12-31T23:00:00Z");
		t.so("S63", "sdt:millis-to-dateTime(3600000)", doc, "1970-01-01T01:00:00Z");
		t.so("F64", "sdt:millis-to-dateTime()", doc, "millis-to-dateTime() requires exactly one argument.");
		t.so("F65", "sdt:millis-to-dateTime('a')", doc, "millis-to-dateTime() requires a number.");

		t.so("S66", "sdt:dateTime-to-millis('1970-01-01T00:00:00Z')", doc, "0.0");
		t.so("S67", "sdt:dateTime-to-millis('1969-12-31T23:00:00Z')", doc, "-3600000.0");
		t.so("S68", "sdt:dateTime-to-millis('1970-01-01T01:00:00Z')", doc, "3600000.0");
		t.so("F69", "sdt:dateTime-to-millis(0)", doc, "dateTime-to-millis() argument '0.0' is invalid.");
		t.so("F70", "sdt:dateTime-to-millis()", doc, "dateTime-to-millis() requires exactly one argument.");
		
		t.so("S71", "sdt:parse-dateTime('1968/02/28 12:00','yyyy/MM/dd HH:mm')", doc, "1968-02-28T12:00:00");
		t.so("S72", "sdt:parse-dateTime('19700101000000+00:00','yyyyMMddHHmmssz')", doc, "1970-01-01T00:00:00Z");
		t.so("F73", "sdt:parse-dateTime('a','yyyyMMddHHmmss')", doc, "parse-dateTime() failed to parse 'a'.");
		t.so("F74", "sdt:parse-dateTime()", doc, "parse-dateTime() requires two arguments.");
		
		t.so("S75", "sdt:dateTime-to-timezone('2025-03-30T01:00:00Z', 'Europe/Amsterdam')", doc, "2025-03-30T03:00:00+02:00");
		t.so("S76", "sdt:dateTime-to-timezone('2025-10-26T00:00:00Z', 'Europe/Amsterdam')", doc, "2025-10-26T02:00:00+02:00");
		t.so("S77", "sdt:dateTime-to-timezone('2025-10-26T01:00:00Z', 'Europe/Amsterdam')", doc, "2025-10-26T02:00:00+01:00");
		t.so("S78", "sdt:dateTime-to-timezone('2025-03-30T02:00:00', 'Europe/Amsterdam')", doc, "2025-03-30T03:00:00+02:00");
		t.so("S79", "sdt:dateTime-to-timezone('2025-10-26T02:00:00', 'Europe/Amsterdam')", doc, "2025-10-26T02:00:00+02:00");
		t.so("S80", "sdt:dateTime-to-timezone('2025-10-26T03:00:00', 'Europe/Amsterdam')", doc, "2025-10-26T03:00:00+01:00");
		t.so("F81", "sdt:dateTime-to-timezone('a', 'Europe/Amsterdam')", doc, "dateTime-to-timezone() argument 'a' is invalid.");
		t.so("F82", "sdt:dateTime-to-timezone('2025-03-30T01:00:00Z', 'a')", doc, "dateTime-to-timezone() time zone 'a' is invalid.");
		
		System.out.print("\n	    ");
				
		t.so("S84", "sdt:dateTime-to-local('1970-01-01T00:00:00')", doc, "1970-01-01T00:00:00");
		t.so("S84", "sdt:dateTime-to-local('1970-01-01T00:00:00Z')", doc, "1970-01-01T00:00:00");
		t.so("F85", "sdt:dateTime-to-local()", doc, "dateTime-to-local() requires exactly one argument.");
		t.so("F86", "sdt:dateTime-to-local('a')", doc, "dateTime-to-local() argument 'a' is invalid.");
		
		t.so("S87", "sdt:implicit-timezone()", doc, ZoneId.systemDefault().toString());
		t.so("F88", "sdt:implicit-timezone('a')", doc, "implicit-timezone() requires no arguments.");
		t.so("S87", "sdt:system-timezone()", doc, ZoneId.systemDefault().toString());
		t.so("F88", "sdt:system-timezone('a')", doc, "system-timezone() requires no arguments.");
		
		t.so("S89", "sdt:timezone-from-dateTime('1970-01-01T00:00:00')", doc, "");
		t.so("S90", "sdt:timezone-from-dateTime('1970-01-01T00:00:00Z')", doc, "Z");
		t.so("S91", "sdt:timezone-from-dateTime('1968-02-28T12:00+01:00')", doc, "+01:00");
		t.so("F92", "sdt:timezone-from-dateTime()", doc, "timezone-from-dateTime() requires exactly one argument.");
	}

}
