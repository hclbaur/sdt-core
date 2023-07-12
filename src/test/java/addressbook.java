import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;

import be.baur.sda.Node;
import be.baur.sdt.xpath.DocumentNavigator;
import be.baur.sdt.xpath.SDAXPath;

@SuppressWarnings("rawtypes")
public class addressbook {

	public static void main(String[] args) throws JaxenException, UnsupportedEncodingException {

		InputStream in = addressbook.class.getResourceAsStream("addressbook.sda");
		Node doc = (Node) DocumentNavigator.getDocument(new InputStreamReader(in, "UTF-8"));

		doc.add(new Node("foo"));
		doc.getNodes().get(1).add(new Node("bar", "test"));
		XPath y = new SDAXPath("/foo/bar");
		System.out.println(y.evaluate(doc).toString());
		
		XPath x;
		SimpleVariableContext v;
		
		x = new SDAXPath("document('/temp/addressbook.sda')");
		System.out.println(x.evaluate(doc).toString());
		
		x = new SDAXPath("/addressbook/contact/phonenumber");
		List numbers = x.selectNodes(doc);
		for (Object number : numbers) {
			x = new SDAXPath("concat(position(), ': ', .., ' ', ../firstname,' ', .)");
			System.out.println(x.evaluate(number).toString());
		}
		
		x = new SDAXPath("concat('Hello ',$var,'.\n')");
		v = (SimpleVariableContext) x.getVariableContext();
		v.setVariableValue("var", "world");
		v.setVariableValue("var2", "world2");
		System.out.println(x.evaluate(doc).toString());
		
		x = new SDAXPath("evaluate($var)/contact[1]/firstname");
		v.setVariableValue("var", "/addressbook"); 
		x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString());

		x = new SDAXPath("$var/contact[2]/firstname");
		v.setVariableValue("var", doc.getNodes().get(0)); 
		x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString());
		
		x = new SDAXPath("/addressbook/contact");
		List contacts = x.selectNodes(doc);
		x = new SDAXPath("$var[2]");
		v.setVariableValue("var", contacts); 
		x.setVariableContext(v);
		System.out.println(x.evaluate(doc).toString());

	}

}
