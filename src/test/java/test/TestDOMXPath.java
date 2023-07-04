package test;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestDOMXPath {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		Test t = new Test( (str,obj) -> {
			try {
				return (new DOMXPath(str)).evaluate(obj).toString();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
		factory.setNamespaceAware(true);  
		DocumentBuilder builder = factory.newDocumentBuilder();   
		InputSource data = new InputSource(TestDOMXPath.class.getResourceAsStream("/addressbook.xml"));
		Node doc = builder.parse(data); 
		
		Node addressbook = doc.getChildNodes().item(0);
		NodeList contacts = addressbook.getChildNodes();
		Node alice = contacts.item(1); 
		Node bob = contacts.item(3);
		
		t.so("S01", "/", doc, "["+doc.toString()+"]");
		t.so("S02", ".", doc, "["+doc.toString()+"]");
		t.so("S03", "*", doc, "["+addressbook.toString()+"]");
		
		t.so("S04", "/addressbook", doc, "["+addressbook.toString()+"]");
		t.so("S05", "/addressbook/contact", doc, "["+alice.toString()+", "+bob.toString()+"]");
		t.so("S06", "/addressbook/contact/*", doc, "[[firstname: null], [phonenumber: null], [phonenumber: null], [firstname: null], [phonenumber: null], [phonenumber: null]]");
		
		t.so("S07", "/addressbook/contact/phonenumber/text()", doc, "[[#text: 06-11111111], [#text: 06-22222222], [#text: 06-33333333], [#text: 06-44444444]]");
		t.so("S08", "//contact/phonenumber/text()", doc, "[[#text: 06-11111111], [#text: 06-22222222], [#text: 06-33333333], [#text: 06-44444444]]");
		t.so("S09", "//phonenumber/text()", doc, "[[#text: 06-11111111], [#text: 06-22222222], [#text: 06-33333333], [#text: 06-44444444]]");
		
		t.so("S10", "/addressbook/contact/phonenumber[1]/text()", doc, "[[#text: 06-11111111], [#text: 06-33333333]]");
		t.so("S11", "//contact/phonenumber[2]/text()", doc, "[[#text: 06-22222222], [#text: 06-44444444]]");
		t.so("S12", "//phonenumber[3]/text()", doc, "[]");
		
		t.so("S13", "(/addressbook/contact/phonenumber)[1]/text()", doc, "[[#text: 06-11111111]]");
		t.so("S14", "(//contact/phonenumber)[2]/text()", doc, "[[#text: 06-22222222]]");
		t.so("S15", "(//phonenumber)[3]/text()", doc, "[[#text: 06-33333333]]");
		
		t.so("S16", "/addressbook/contact[1]/firstname/text()", doc, "[[#text: Alice]]");
		t.so("S17", "/addressbook/contact[firstname='Bob']/firstname/text()", doc, "[[#text: Bob]]");
		t.so("S18", "/addressbook/contact[@id='1']/firstname/text()", doc, "[[#text: Alice]]");
		
		t.so("S19", "/addressbook/contact/@id", doc ,"[id=\"1\", id=\"2\"]");
		
		System.out.println("\n===== node-set functions =====");
		// https://www.edankert.com/xpathfunctions.html
		
		t.so("S20", "/addressbook/contact[last()]/firstname/text()", doc, "[[#text: Bob]]");
		t.so("S21", "/addressbook/contact[position()<2]/firstname/text()", doc, "[[#text: Alice]]");
		t.so("S22", "count(/addressbook/contact)", doc, "2.0");
		t.so("S23", "id('1')", doc, "[]"); // will not work without associated schema

		t.so("S24", "local-name(/addressbook/contact)", doc, "contact");
		t.so("S25", "local-name()", alice, "contact");
		t.so("S26", "local-name()", doc, ""); // returns empty string

		t.so("S28", "namespace-uri(/addressbook/contact)", doc, "[null]");
		t.so("S29", "namespace-uri()", alice, "[null]");

		t.so("S30", "name(/addressbook/contact)", doc, "contact");
		t.so("S31", "name()", alice, "contact");
		t.so("S32", "name()", doc, ""); // returns empty string
		
		System.out.println("\n===== string functions =====");

		t.so("S33", "string(/addressbook/contact)", doc, "\n\t\tAlice\n\t\t06-11111111\n\t\t06-22222222\n\t");
		t.so("S34", "string()", addressbook, "\n\t\n\t\tAlice\n\t\t06-11111111\n\t\t06-22222222\n\t\n\t\n\t\tBob\n\t\t06-33333333\n\t\t06-44444444\n\t\n");
		t.so("S35", "string()", bob, "\n\t\tBob\n\t\t06-33333333\n\t\t06-44444444\n\t");
		
		t.so("S36", "concat(/addressbook/contact/firstname,/addressbook/contact/@id)", doc, "Alice1");
		t.so("S37", "concat(firstname,@id)", bob, "Bob2");
		t.so("S38", "concat(contact[1]/@id,contact[2]/@id)", addressbook, "12");

		t.so("S39", "starts-with(/addressbook/contact/firstname,'A')", doc, "true");
		t.so("S40", "starts-with(firstname,'B')", bob, "true");
		t.so("S41", "starts-with(contact/@id,'1')", addressbook, "true");
		
		t.so("S42", "contains(/addressbook/contact/firstname,'i')", doc, "true");
		t.so("S43", "contains(firstname,'o')", bob, "true");
		t.so("S44", "contains(contact/firstname,'o')", addressbook, "false");
		
		t.so("S45", "substring-before(/addressbook/contact/firstname,'i')", doc, "Al");
		t.so("S46", "substring-after(firstname,'o')", bob, "b");
		t.so("S47", "substring(contact/firstname,3,2)", addressbook, "ic");
		t.so("S48", "substring(contact/firstname,3)", addressbook, "ice");

		t.so("S49", "string-length(/addressbook/contact/firstname)", doc, "5.0");
		t.so("S50", "string-length()", bob, "36.0");
		t.so("S51", "string-length()", addressbook, "79.0");
		
		t.so("S52", "normalize-space(/addressbook/contact/firstname)", doc, "Alice");
		t.so("S53", "normalize-space()", bob, "Bob 06-33333333 06-44444444");
		
		t.so("S54", "translate(/addressbook/contact/firstname,'ie','13')", doc, "Al1c3");
		
		System.out.println("\n===== boolean functions =====");

		t.so("S55", "boolean(/addressbook/contact)", doc, "true");
		t.so("S56", "boolean(firstname)", bob, "true");
		t.so("S57", "boolean(kontact)", addressbook, "false");

		t.so("S58", "not(/addressbook/contact)", doc, "false");
		t.so("S59", "not(firstname)", bob, "false");
		t.so("S60", "not(kontact)", addressbook, "true");
		t.so("S61", "true()", doc, "true");
		t.so("S62", "false()", doc, "false");
		t.so("S63", "lang(addressbook)", doc, "false");	

		System.out.println("\n===== number functions =====");

		t.so("S64", "number(/addressbook/contact/@id)", doc, "1.0");
		t.so("S65", "number(@id)", bob, "2.0");
		t.so("S66", "sum(contact/@id)", addressbook, "3.0");
		t.so("S67", "floor(1.5)", doc, "1.0");
		t.so("S68", "ceiling(1.5)", doc, "2.0");
		t.so("S69", "round(1.5)", doc, "2.0");
		t.so("S70", "((1+1)*2mod3)div2-1", doc, "-0.5");

		System.out.println("\n===== Jaxen extensions =====");

		t.so("S71", "ends-with(/addressbook/contact/firstname,'e')", doc, "true");
		t.so("S72", "ends-with(firstname,'b')", bob, "true");
		t.so("S73", "ends-with(contact/@id,'1')", addressbook, "true");
		
		t.so("S74", "evaluate('/addressbook/contact/firstname')", doc, "[[firstname: null], [firstname: null]]");
		t.so("S75", "evaluate('firstname')", bob, "[[firstname: null]]");
		t.so("S76", "evaluate('contact/@id')", addressbook, "[id=\"1\", id=\"2\"]");
		
		t.so("S77", "lower-case(/addressbook/contact/firstname)", doc, "alice");
		t.so("S78", "upper-case(firstname)", bob, "BOB");
		
		t.so("S79", "document('/tmp/addressbook.xml')", addressbook, "[[#document: null]]");
	}

}
