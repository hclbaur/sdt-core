package test;
import java.time.ZoneId;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sdt.xpath.DocumentNavigator;
import be.baur.sdt.xpath.DocumentNode;
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
		
		String f = TestSDTXPath.class.getResource("/addressbook.sda").getFile();
		DocumentNode d = DocumentNavigator.newDocumentNode((DataNode) nav.getDocument(f));
		
		var addressbook = d.nodes().get(0);
		//var contacts = addressbook.nodes();
		//var alice = contacts.get(0); 
		//var bob = contacts.get(1);
		
		t.so("S1", "fn:string-join(/addressbook/contact/phonenumber)", d, "06-1111111106-2222222206-3333333306-44444444");
		t.so("S2", "fn:string-join(contact | contact/firstname,':')", addressbook, "1:Alice:2:Bob");
		
		t.so("S3", "sdt:left(/addressbook/contact[1]/firstname,0)", d, "");
		t.so("S4", "sdt:left(/addressbook/contact[1]/firstname,2)", d, "Al");
		t.so("S5", "sdt:left(/addressbook/contact[1]/firstname,6)", d, "Alice");
		
		t.so("S6", "sdt:right(/addressbook/contact[2]/firstname,0)", d, "");
		t.so("S7", "sdt:right(/addressbook/contact[2]/firstname,2)", d, "ob");
		t.so("S8", "sdt:right(/addressbook/contact[2]/firstname,4)", d, "Bob");
		
		t.so("S11", "sdt:compare-number(1,3)", d, "-1.0");
		t.so("S12", "sdt:compare-number(3,'3')", d, "0.0");
		t.so("S13", "sdt:compare-number('6','4')", d, "1.0");
		t.so("S14", "sdt:compare-number('a',1)", d, "1.0");
		t.so("S15", "sdt:compare-number('a','b')", d, "0.0");
		t.so("S16", "sdt:compare-number('a',1,true())", d, "-1.0");
		t.so("S17", "sdt:compare-number('a',1,false())", d, "1.0");
		t.so("S18", "sdt:compare-number('a','b',true())", d, "0.0");
		t.so("S19", "sdt:compare-number('a','b',false())", d, "0.0");
		
		t.so("S21", "sdt:compare-string('a','b')", d, "-1.0");
		t.so("S22", "sdt:compare-string('a','A')", d, "-1.0");
		t.so("S23", "sdt:compare-string(3,'3')", d, "0.0");
		t.so("S24", "sdt:compare-string('b','A')", d, "1.0");
		t.so("S25", "sdt:compare-string('Ångström','Zulu','en')", d, "-1.0");
		t.so("S26", "sdt:compare-string('Ångström','Zulu','sv')", d, "1.0");

		t.so("S31", "sdt:tokenize('')", d, "[]");
		t.so("S32", "sdt:tokenize('abc')", d, "abc");
		t.so("S33", "sdt:tokenize('abc','')", d, "[a, b, c]");
		t.so("S34", "sdt:tokenize(' a  b   c    ')", d, "[a, b, c]");
		t.so("S35", "sdt:tokenize('127.0.0.1:80','[\\.:]')", d, "[127, 0, 0, 1, 80]");
		t.so("S36", "sdt:tokenize('1;2;;3;',';')", d, "[1, 2, 3]");
		t.so("S37", "sdt:tokenize('1; 2; ; 3; ','; ',true())", d, "[1, 2, , 3, ]");
		
		t.so("S41", "sdt:render-sda('')", d, "");
		t.so("S42", "sdt:render-sda(unknown)", d, "");
		t.so("S43", "sdt:render-sda(/addressbook/contact/firstname)", d, "firstname \"Alice\"");
		t.so("S44", "sdt:render-sda(/addressbook/contact/phonenumber)", d, "phonenumber \"06-11111111\"");
		t.so("S45", "sdt:render-sda(/addressbook/contact[2])", d, "contact \"2\" { firstname \"Bob\" phonenumber \"06-33333333\" phonenumber \"06-44444444\" }");
		t.so("F46", "sdt:parse-sda('')", d, "unexpected end of input");
		t.so("F47", "sdt:parse-sda('greeting message \"hello\" }')", d, "unexpected character 'm'");
		t.so("S48", "sdt:parse-sda('greeting { message \"hello\" }')", d, "[greeting { message \"hello\" }]");
		
		System.out.print("\n	    ");
		
		t.so("S51", "sdt:dateTime('1968-02-28T12:00')", d, "1968-02-28T12:00:00");
		t.so("S52", "sdt:dateTime('1968-02-28T12:00+01:00')", d, "1968-02-28T12:00:00+01:00");
		t.so("S53", "sdt:dateTime('1968-02-28T12:00:00.500+01:00[Europe/Amsterdam]')", d, "1968-02-28T12:00:00.5+01:00[Europe/Amsterdam]");
		t.so("S54", "sdt:dateTime('1968-02-28T12:00:00.000000001Z')", d, "1968-02-28T12:00:00.000000001Z");
		t.so("F55", "sdt:dateTime('a')", d, "dateTime() argument 'a' is not a valid date-time.");
		t.so("F56", "sdt:dateTime()", d, "dateTime() requires one argument.");
		
		t.so("S57", "sdt:format-dateTime('1968-02-28T12:00','yyyy/MM/dd HH:mm')", d, "1968/02/28 12:00");
		t.so("S58", "sdt:format-dateTime(sdt:millis-to-dateTime(0),'yyyyMMddHHmmss')", d, "19700101000000");
		t.so("F59", "sdt:format-dateTime()", d, "format-dateTime() requires two arguments.");
		
		t.so("S61", "sdt:millis-to-dateTime(0)", d, "1970-01-01T00:00:00Z");
		t.so("S62", "sdt:millis-to-dateTime(-3600000)", d, "1969-12-31T23:00:00Z");
		t.so("S63", "sdt:millis-to-dateTime(3600000)", d, "1970-01-01T01:00:00Z");
		t.so("F64", "sdt:millis-to-dateTime()", d, "millis-to-dateTime() requires one argument.");
		t.so("F65", "sdt:millis-to-dateTime('a')", d, "millis-to-dateTime() requires a number.");

		t.so("S66", "sdt:dateTime-to-millis('1970-01-01T00:00:00Z')", d, "0.0");
		t.so("S67", "sdt:dateTime-to-millis('1969-12-31T23:00:00Z')", d, "-3600000.0");
		t.so("S68", "sdt:dateTime-to-millis('1970-01-01T01:00:00Z')", d, "3600000.0");
		t.so("F69", "sdt:dateTime-to-millis()", d, "dateTime-to-millis() requires one argument.");
		
		t.so("S71", "sdt:parse-dateTime('1968/02/28 12:00','yyyy/MM/dd HH:mm')", d, "1968-02-28T12:00:00");
		t.so("S72", "sdt:parse-dateTime('19700101000000+00:00','yyyyMMddHHmmssz')", d, "1970-01-01T00:00:00Z");
		t.so("F73", "sdt:parse-dateTime('a','yyyyMMddHHmmss')", d, "parse-dateTime() failed to parse 'a'.");
		t.so("F74", "sdt:parse-dateTime()", d, "parse-dateTime() requires two arguments.");
		
		t.so("S75", "sdt:dateTime-to-timezone('2025-03-30T01:00:00Z', 'Europe/Amsterdam')", d, "2025-03-30T03:00:00+02:00[Europe/Amsterdam]");
		t.so("S76", "sdt:dateTime-to-timezone('2025-10-26T00:00:00Z', 'Europe/Amsterdam')", d, "2025-10-26T02:00:00+02:00[Europe/Amsterdam]");
		t.so("S77", "sdt:dateTime-to-timezone('2025-10-26T01:00:00Z', 'Europe/Amsterdam')", d, "2025-10-26T02:00:00+01:00[Europe/Amsterdam]");
		t.so("S78", "sdt:dateTime-to-timezone('2025-03-30T02:00:00', 'Europe/Amsterdam')", d, "2025-03-30T03:00:00+02:00[Europe/Amsterdam]");
		t.so("S79", "sdt:dateTime-to-timezone('2025-10-26T02:00:00', 'Europe/Amsterdam')", d, "2025-10-26T02:00:00+02:00[Europe/Amsterdam]");
		t.so("S80", "sdt:dateTime-to-timezone('2025-10-26T03:00:00', 'Europe/Amsterdam')", d, "2025-10-26T03:00:00+01:00[Europe/Amsterdam]");
		t.so("F81", "sdt:dateTime-to-timezone('2025-03-30T01:00:00Z', 'a')", d, "dateTime-to-timezone() time zone 'a' is invalid.");
		t.so("F82", "sdt:dateTime-to-timezone()", d, "dateTime-to-timezone() requires two arguments.");
				
		t.so("S83", "sdt:dateTime-to-local('1970-01-01T00:00:00')", d, "1970-01-01T00:00:00");
		t.so("S84", "sdt:dateTime-to-local('1970-01-01T00:00:00Z')", d, "1970-01-01T00:00:00");
		t.so("F85", "sdt:dateTime-to-local()", d, "dateTime-to-local() requires one argument.");

		System.out.print("\n	    ");
		
		t.so("S86", "sdt:implicit-timezone()", d, ZoneId.systemDefault().toString());
		t.so("F87", "sdt:implicit-timezone('a')", d, "implicit-timezone() requires no arguments.");
		
		t.so("S88", "sdt:timezone-from-dateTime('1970-01-01T00:00:00')", d, "");
		t.so("S89", "sdt:timezone-from-dateTime('1970-01-01T00:00:00Z')", d, "Z");
		t.so("S90", "sdt:timezone-from-dateTime('1968-02-28T12:00+01:00')", d, "+01:00");
		t.so("F91", "sdt:timezone-from-dateTime()", d, "timezone-from-dateTime() requires one argument.");
		
		t.so("S92", "sdt:compare-dateTime(sdt:current-dateTime(),sdt:current-dateTime())", d, "0.0");
		t.so("S93", "sdt:compare-dateTime(sdt:dateTime-to-local(sdt:current-dateTime()),sdt:current-dateTime())", d, "0.0");
		t.so("S94", "sdt:compare-dateTime('1970-01-01T00:00:00+01:00','1970-01-01T00:00:00Z')", d, "-1.0");
		t.so("S95", "sdt:compare-dateTime('1970-01-01T00:00:00Z','1970-01-01T00:00:00+01:00')", d, "1.0");
		t.so("F96", "sdt:compare-dateTime('1970-01-01T00:00:00Z')", d, "compare-dateTime() requires two arguments.");
		
		t.so("S100", "sdt:add-to-dateTime('1968-02-28T12:00:00',0,0,0)", d, "1968-02-28T12:00:00");
		t.so("S101", "sdt:add-to-dateTime('1968-02-28T12:00:00',11,59,60)", d, "1968-02-29T00:00:00");
		t.so("S102", "sdt:add-to-dateTime('1968-03-01T12:00:00',-35,-59,-60)", d, "1968-02-29T00:00:00");
		t.so("S103", "sdt:add-to-dateTime('2025-03-29T03:00:00+01:00[Europe/Amsterdam]',24,0,0)", d, "2025-03-30T04:00:00+02:00[Europe/Amsterdam]");
		t.so("S104", "sdt:add-to-dateTime('2025-03-30T01:00:00+01:00[Europe/Amsterdam]',1,0,0)", d, "2025-03-30T03:00:00+02:00[Europe/Amsterdam]");
		t.so("S105", "sdt:add-to-dateTime('2025-10-26T02:00:00+02:00[Europe/Amsterdam]',1,0,0)", d, "2025-10-26T02:00:00+01:00[Europe/Amsterdam]");
		t.so("S106", "sdt:add-to-dateTime('2025-10-26T03:00:00+02:00[Europe/Amsterdam]',-1,0,0)", d, "2025-10-26T02:00:00+02:00[Europe/Amsterdam]");
		t.so("S107", "sdt:add-to-dateTime('2025-10-27T01:00:00+01:00[Europe/Amsterdam]',-24,0,0)", d, "2025-10-26T02:00:00+02:00[Europe/Amsterdam]");
		t.so("F108", "sdt:add-to-dateTime()", d, "add-to-dateTime() requires four arguments.");
		
		t.so("S110", "sdt:add-period-to-dateTime('1968-02-29T12:00:00',0,0,0)", d, "1968-02-29T12:00:00");
		t.so("S111", "sdt:add-period-to-dateTime('1968-02-28T12:00:00',0,0,1)", d, "1968-02-29T12:00:00");
		t.so("S112", "sdt:add-period-to-dateTime('1968-03-31T12:00:00',0,-1,0)", d, "1968-02-29T12:00:00");
		t.so("S113", "sdt:add-period-to-dateTime('1968-02-29T12:00:00',1,0,0)", d, "1969-02-28T12:00:00");
		t.so("S114", "sdt:add-period-to-dateTime('2025-03-29T12:00:00+01:00[Europe/Amsterdam]',0,0,1)", d, "2025-03-30T12:00:00+02:00[Europe/Amsterdam]");
		t.so("S115", "sdt:add-period-to-dateTime('2025-04-29T12:00:00+02:00[Europe/Amsterdam]',0,-1,0)", d, "2025-03-29T12:00:00+01:00[Europe/Amsterdam]");
		t.so("S116", "sdt:add-period-to-dateTime('2025-09-26T12:00:00+02:00[Europe/Amsterdam]',0,1,0)", d, "2025-10-26T12:00:00+01:00[Europe/Amsterdam]");
		t.so("S117", "sdt:add-period-to-dateTime('2025-10-26T12:00:00+01:00[Europe/Amsterdam]',0,0,-1)", d, "2025-10-25T12:00:00+02:00[Europe/Amsterdam]");
		t.so("F118", "sdt:add-period-to-dateTime()", d, "add-period-to-dateTime() requires four arguments.");

		System.out.print("\n	    ");
	
		t.so("S120", "sdt:subtract-dateTimes('1970-01-01T00:00:00+01:00','1970-01-01T00:00:00.0005Z')", d, "-3600.001");
		t.so("S121", "sdt:subtract-dateTimes('1970-01-01T00:00:00.0004999Z','1970-01-01T00:00:00+01:00')", d, "3600.0");
		t.so("S122", "sdt:subtract-dateTimes(sdt:dateTime-to-local(sdt:current-dateTime()),sdt:current-dateTime())", d, "0.0");
		t.so("S123", "sdt:subtract-dateTimes('1968-02-28T12:00+01:00[Europe/Amsterdam]','1968-02-28T12:00+01:00[Europe/Berlin]')", d, "0.0");
		t.so("S124", "sdt:subtract-dateTimes('1968-03-01T12:00','1968-02-28T12:00') div 3600", d, "48.0");
		t.so("S125", "sdt:subtract-dateTimes('1968-02-28T12:00-05:00','1968-02-28T12:00+01:00') div 3600", d, "6.0");
		t.so("S126", "sdt:subtract-dateTimes('1968-02-28T12:00-05:00[America/New_York]','1968-02-28T12:00+01:00[Europe/Amsterdam]') div 3600", d, "6.0");
		t.so("S127", "sdt:subtract-dateTimes('2025-03-30T01:00:00+01:00','2025-03-30T03:00:00+02:00') div 3600", d, "-1.0");
		t.so("S128", "sdt:subtract-dateTimes('2025-10-26T02:00:00+02:00','2025-10-26T03:00:00+01:00') div 3600", d, "-2.0");
		t.so("S129", "sdt:subtract-dateTimes()", d, "subtract-dateTimes() requires two arguments.");
				
		t.checkFailures();
	}

}
