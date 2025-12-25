import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jaxen.Navigator;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sdt.xpath.DocumentNavigator;

@SuppressWarnings("rawtypes")
public class addressbook {

	public static void main(String[] args) throws UnsupportedEncodingException, SAXPathException {

		Navigator nav = DocumentNavigator.getInstance();
		String file = addressbook.class.getResource("/addressbook.sda").getFile();
		Node doc = DocumentNavigator.newDocumentNode((DataNode) nav.getDocument(file));
		
		
		XPath x;
		SimpleVariableContext v;
		
		x = nav.parseXPath("document('/temp/addressbook.sda')");
		System.out.println(x.evaluate(doc).toString()); // prints entire document
		
		x = nav.parseXPath("/addressbook/contact/phonenumber");
		List numbers = x.selectNodes(doc);
		System.out.println("Selected " + numbers.size() + " numbers:");
		for (Object number : numbers) { // position() and last() do not work
			x = nav.parseXPath("concat(position(), ': ', .., ' ', ../firstname,' ', .)");
			System.out.println(x.evaluate(number).toString());
		}
		
		x = nav.parseXPath("concat('Hello ', $var, '.\n')");
		v = (SimpleVariableContext) x.getVariableContext();
		v.setVariableValue("var", "world");
		System.out.println(x.evaluate(doc).toString()); //Hello world.\n
		
		x = nav.parseXPath("evaluate($var)/contact[1]/firstname");
		v.setVariableValue("var", "/addressbook"); 
		x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString()); //[firstname "Alice"]

		x = nav.parseXPath("$var/contact[2]/firstname");
		v.setVariableValue("var", doc.nodes().get(0)); 
		x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString()); //[firstname "Bob"]
		
		x = nav.parseXPath("/addressbook/contact");
		List contacts = x.selectNodes(doc);
		x = nav.parseXPath("$var[2]/firstname");
		v.setVariableValue("var", contacts); 
		x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString()); //[firstname "Bob"]

		v.setVariableValue("_", "0");
		x = nav.parseXPath("$_"); x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString());
	}

}
