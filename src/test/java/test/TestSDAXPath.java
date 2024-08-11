package test;
import java.util.List;

import be.baur.sda.Node;
import be.baur.sdt.xpath.DocumentNavigator;
import be.baur.sdt.xpath.SDAXPath;

public class TestSDAXPath {

	public static void main(String[] args) throws Exception {

		Test t = new Test( (str,obj) -> {
			try {
				return (new SDAXPath(str)).evaluate(obj).toString();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		String file = TestSDAXPath.class.getResource("/addressbook.sda").getFile();
		Node doc = (Node) DocumentNavigator.getInstance().getDocument(file);
		
		Node addressbook = doc.nodes().get(0);
		List<Node> contacts = addressbook.nodes();
		Node alice = contacts.get(0); 
		Node bob = contacts.get(1);
		
		t.so("S01", "/", doc, "["+doc.toString()+"]");
		t.so("S02", ".", doc, "["+doc.toString()+"]");
		t.so("S03", "*", doc, "["+addressbook.toString()+"]");
		
		t.so("S04", "/addressbook", doc, "["+addressbook.toString()+"]");
		t.so("S05", "/addressbook/contact", doc, "["+alice.toString()+", "+bob.toString()+"]");
		t.so("S06", "/addressbook/contact/*", doc, "[firstname \"Alice\", phonenumber \"06-11111111\", phonenumber \"06-22222222\", firstname \"Bob\", phonenumber \"06-33333333\", phonenumber \"06-44444444\"]");
		
		t.so("S07", "/addressbook/contact/phonenumber", doc, "[phonenumber \"06-11111111\", phonenumber \"06-22222222\", phonenumber \"06-33333333\", phonenumber \"06-44444444\"]");
		t.so("S08", "//contact/phonenumber", doc, "[phonenumber \"06-11111111\", phonenumber \"06-22222222\", phonenumber \"06-33333333\", phonenumber \"06-44444444\"]");
		t.so("S09", "//phonenumber", doc, "[phonenumber \"06-11111111\", phonenumber \"06-22222222\", phonenumber \"06-33333333\", phonenumber \"06-44444444\"]");
		
		t.so("S10", "/addressbook/contact/phonenumber[1]", doc, "[phonenumber \"06-11111111\", phonenumber \"06-33333333\"]");
		t.so("S11", "//contact/phonenumber[2]", doc, "[phonenumber \"06-22222222\", phonenumber \"06-44444444\"]");
		t.so("S12", "//phonenumber[3]", doc, "[]");
		
		t.so("S13", "(/addressbook/contact/phonenumber)[1]", doc, "[phonenumber \"06-11111111\"]");
		t.so("S14", "(//contact/phonenumber)[2]", doc, "[phonenumber \"06-22222222\"]");
		t.so("S15", "(//phonenumber)[3]", doc, "[phonenumber \"06-33333333\"]");
		
		t.so("S16", "/addressbook/contact[1]/firstname", doc, "[firstname \"Alice\"]");
		t.so("S17", "/addressbook/contact[firstname='Bob']/firstname", doc, "[firstname \"Bob\"]");
		t.so("S18", "/addressbook/contact[.='1']/firstname", doc, "[firstname \"Alice\"]");
		
		t.so("S19", "/addressbook/contact/@id", doc ,"SDA does not support attributes");
		
		System.out.print("nodeset ");
		// https://www.edankert.com/xpathfunctions.html
		
		t.so("S20", "/addressbook/contact[last()]/firstname", doc, "[firstname \"Bob\"]");
		t.so("S21", "/addressbook/contact[position()<2]/firstname", doc, "[firstname \"Alice\"]");
		t.so("S22", "count(/addressbook/contact)", doc, "2.0");
		t.so("S23", "count(/addressbook/contact[1] | /addressbook/contact[2])", doc, "2.0");
		t.so("S24", "id('1')", doc, "SDA does not support element Ids");

		t.so("S25", "local-name(/addressbook/contact)", doc, "contact");
		t.so("S26", "local-name()", alice, "contact");
		t.so("S27", "local-name()", doc, ""); // returns empty string

		t.so("S28", "namespace-uri(/addressbook/contact)", doc, "[null]");
		t.so("S29", "namespace-uri()", alice, "[null]");

		t.so("S30", "name(/addressbook/contact)", doc, "contact");
		t.so("S31", "name()", alice, "contact");
		t.so("S32", "name()", doc, ""); // returns empty string
		
		System.out.print("\n	    string ");

		t.so("S33", "string(/addressbook/contact)", doc, "1");
		t.so("S34", "string()", addressbook, "");
		t.so("S35", "string()", bob, "2");
		
		t.so("S36", "concat(/addressbook/contact/firstname,/addressbook/contact)", doc, "Alice1");
		t.so("S37", "concat(firstname,.)", bob, "Bob2");
		t.so("S38", "concat(contact[1],contact[2])", addressbook, "12");

		t.so("S39", "starts-with(/addressbook/contact/firstname,'A')", doc, "true");
		t.so("S40", "starts-with(firstname,'B')", bob, "true");
		t.so("S41", "starts-with(contact,'1')", addressbook, "true");

		t.so("S42", "contains(/addressbook/contact/firstname,'i')", doc, "true");
		t.so("S43", "contains(firstname,'o')", bob, "true");
		t.so("S44", "contains(contact/firstname,'o')", addressbook, "false");

		t.so("S45", "substring-before(/addressbook/contact/firstname,'i')", doc, "Al");
		t.so("S46", "substring-after(firstname,'o')", bob, "b");
		t.so("S47", "substring(contact/firstname,3,2)", addressbook, "ic");
		t.so("S48", "substring(contact/firstname,3)", addressbook, "ice");

		t.so("S49", "string-length(/addressbook/contact/firstname)", doc, "5.0");
		t.so("S50", "string-length()", bob, "1.0");
		t.so("S51", "string-length()", addressbook, "0.0");
		
		t.so("S52", "normalize-space(/addressbook/contact/firstname)", doc, "Alice");
		t.so("S53", "normalize-space()", bob, "2");
		
		t.so("S54", "translate(/addressbook/contact/firstname,'ie','13')", doc, "Al1c3");
		
		System.out.print("boolean ");

		t.so("S55", "boolean(/addressbook/contact)", doc, "true");
		t.so("S56", "boolean(firstname)", bob, "true");
		t.so("S57", "boolean(kontact)", addressbook, "false");

		t.so("S58", "not(/addressbook/contact)", doc, "false");
		t.so("S59", "not(firstname)", bob, "false");
		t.so("S60", "not(kontact)", addressbook, "true");
		t.so("S61", "true()", doc, "true");
		t.so("S62", "false()", doc, "false");		
		t.so("S63", "lang(addressbook)", doc, "false");	
		
		System.out.print("\n	    number ");
		
		t.so("S64", "number(/addressbook/contact)", doc, "1.0");
		t.so("S65", "number()", bob, "2.0");
		t.so("S66", "sum(contact)", addressbook, "3.0");
		t.so("S67", "floor(1.5)", doc, "1.0");
		t.so("S68", "ceiling(1.5)", doc, "2.0");
		t.so("S69", "round(1.5)", doc, "2.0");
		t.so("S70", "((1+1)*2mod3)div2-1", doc, "-0.5");
		
		System.out.print("jaxen ");

		t.so("S71", "ends-with(/addressbook/contact/firstname,'e')", doc, "true");
		t.so("S72", "ends-with(firstname,'b')", bob, "true");
		t.so("S73", "ends-with(contact,'1')", addressbook, "true");
		
		t.so("S74", "evaluate('/addressbook/contact/firstname')", doc, "[firstname \"Alice\", firstname \"Bob\"]");
		t.so("S75", "evaluate('firstname')", bob, "[firstname \"Bob\"]");
		t.so("S76", "evaluate('contact')", addressbook, "["+alice.toString()+", "+bob.toString()+"]");
		
		t.so("S77", "lower-case(/addressbook/contact/firstname)", doc, "alice");
		t.so("S78", "upper-case(firstname)", bob, "BOB");
		
		t.so("S79", "document('"+ file + "')", addressbook, "["+doc.toString()+"]");
		
		System.out.print("sdt ");
		
		t.so("S80", "fn:string-join(/addressbook/contact/phonenumber)", doc, "06-1111111106-2222222206-3333333306-44444444");
		t.so("S81", "fn:string-join(contact | contact/firstname,':')", addressbook, "1:Alice:2:Bob");
		
		t.so("S82", "sdt:left(/addressbook/contact[1]/firstname,0)", doc, "");
		t.so("S83", "sdt:left(/addressbook/contact[1]/firstname,2)", doc, "Al");
		t.so("S84", "sdt:left(/addressbook/contact[1]/firstname,6)", doc, "Alice");
		
		t.so("S85", "sdt:right(/addressbook/contact[2]/firstname,0)", doc, "");
		t.so("S86", "sdt:right(/addressbook/contact[2]/firstname,2)", doc, "ob");
		t.so("S87", "sdt:right(/addressbook/contact[2]/firstname,4)", doc, "Bob");
		
		System.out.print("\n	    ");
		
		t.so("S90", "sdt:compare-number(1,3)", doc, "-1.0");
		t.so("S91", "sdt:compare-number(3,'3')", doc, "0.0");
		t.so("S92", "sdt:compare-number('6','4')", doc, "1.0");
		t.so("S93", "sdt:compare-number('a',1)", doc, "1.0");
		t.so("S94", "sdt:compare-number('a','b')", doc, "0.0");
		t.so("S95", "sdt:compare-number('a',1,true())", doc, "-1.0");
		t.so("S96", "sdt:compare-number('a',1,false())", doc, "1.0");
		t.so("S97", "sdt:compare-number('a','b',true())", doc, "0.0");
		t.so("S98", "sdt:compare-number('a','b',false())", doc, "0.0");
		
		t.so("S100", "sdt:compare-string('a','A')", doc, "-1.0");
		t.so("S101", "sdt:compare-string(3,'3')", doc, "0.0");
		t.so("S102", "sdt:compare-string('b','A')", doc, "1.0");
		t.so("S103", "sdt:compare-string('Ångström','Zulu','en')", doc, "-1.0");
		t.so("S104", "sdt:compare-string('Ångström','Zulu','sv')", doc, "1.0");
		
		t.so("S110", "sdt:tokenize('')", doc, "[]");
		t.so("S111", "sdt:tokenize('abc')", doc, "abc");
		t.so("S112", "sdt:tokenize('abc','')", doc, "[a, b, c]");
		t.so("S113", "sdt:tokenize(' a  b   c    ')", doc, "[a, b, c]");
		t.so("S114", "sdt:tokenize('127.0.0.1:80','[\\.:]')", doc, "[127, 0, 0, 1, 80]");
		t.so("S115", "sdt:tokenize('1;2;;3;',';')", doc, "[1, 2, 3]");
		t.so("S116", "sdt:tokenize('1; 2; ; 3; ','; ',true())", doc, "[1, 2, , 3, ]");
		
		t.so("S120", "sdt:render-sda('')", doc, "");
		t.so("S121", "sdt:render-sda(unknown)", doc, "");
		t.so("S122", "sdt:render-sda()", doc, "render-sda() requires one or two arguments.");
		t.so("S123", "sdt:render-sda(/addressbook/contact/firstname)", doc, "firstname \"Alice\"");
		t.so("S124", "sdt:render-sda(/addressbook/contact/phonenumber)", doc, "phonenumber \"06-11111111\"");
		t.so("S125", "sdt:render-sda(/addressbook/contact[2])", doc, "contact \"2\" { firstname \"Bob\" phonenumber \"06-33333333\" phonenumber \"06-44444444\" }");
	}

}
