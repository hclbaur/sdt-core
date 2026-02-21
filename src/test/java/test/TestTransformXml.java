package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.jaxen.dom.DocumentNavigator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.transform.Transform;

public final class TestTransformXml {
	
	public static void main(String[] args) throws Exception {
		
		InputStream in = TestTransformXml.class.getResourceAsStream("/example-xml.sdt");
		Transform tran = SDT.parse(new InputStreamReader(in, "UTF-8"));
		
		String file = TestTransformXml.class.getResource("/example.xml").getFile();
		TransformContext c = new TransformContext.Builder() //.setWriter(SDT.nullWriter())
			.setNavigator(DocumentNavigator.getInstance())
			.setStringParameter("filename", file).build();
		Writer w = c.getWriter();
		
		w.write("<<\n");
		DataNode node = tran.execute(c); 
		SDA.format(w, node);
		w.write(">>\n");
		w.flush();
	}
}
