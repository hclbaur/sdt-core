import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.jaxen.dom.DocumentNavigator;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.transform.Transform;

@SuppressWarnings("rawtypes")
public class fibonacci {

	public static void main(String[] args) throws Exception {
		
		// with W3C DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
		factory.setNamespaceAware(true);  
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream source = fibonacci.class.getResourceAsStream("/fibonacci.xml");
		Node doc = builder.parse(new InputSource(source));   
		
		XPath exp = new DOMXPath("/SOAP:Envelope/SOAP:Body/f:Fibonacci_Numbers/f:fibonacci");   
		exp.addNamespace("f", "http://namespaces.cafeconleche.org/xmljava/ch3/");   
		exp.addNamespace("SOAP", "http://schemas.xmlsoap.org/soap/envelope/");   
		
		Iterator iterator = exp.selectNodes(doc).iterator();      
		while (iterator.hasNext()) {     
			Node result = (Node) iterator.next();
			exp = new DOMXPath("concat(@index, ': ', .)");
			String value = exp.evaluate(result).toString();     
			System.out.println(value + " ");
		}

		// with SDT
		String sdtfile = transform.class.getResource("/fibonacci.sdt").getFile();
		Transform tran = SDT.parse(new File(sdtfile));
		
		String sdafile = transform.class.getResource("/fibonacci.xml").getFile();
		TransformContext context = new TransformContext.Builder()
			.addNamespace("f", "http://namespaces.cafeconleche.org/xmljava/ch3/")
			.addNamespace("SOAP", "http://schemas.xmlsoap.org/soap/envelope/")
			.setNavigator(DocumentNavigator.getInstance())
			.setStringParameter("filename", sdafile).build();
		tran.execute(context); 
		
	}
}
