import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("rawtypes")
public class fibonacci {

	public static void main(String[] args) 
		throws ParserConfigurationException, SAXException, IOException, JaxenException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
		factory.setNamespaceAware(true);  
		DocumentBuilder builder = factory.newDocumentBuilder();   
		InputSource data = new InputSource(fibonacci.class.getResourceAsStream("/fibonacci.xml"));
		Node doc = builder.parse(data);   
		
		XPath expression = new org.jaxen.dom.DOMXPath("/SOAP:Envelope/SOAP:Body/f:Fibonacci_Numbers/f:fibonacci");   
		expression.addNamespace("f", "http://namespaces.cafeconleche.org/xmljava/ch3/");   
		expression.addNamespace("SOAP", "http://schemas.xmlsoap.org/soap/envelope/");   
		
		Iterator iterator = expression.selectNodes(doc).iterator();      
		while (iterator.hasNext()) {     
			Node result = (Node) iterator.next();
			expression = new org.jaxen.dom.DOMXPath("concat(position(),' of ',last(),': ',.)");
			String value = expression.evaluate(result).toString();     
			System.out.println(value + " ");   
		} 
	}
}
