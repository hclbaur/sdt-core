package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.statements.Transform;

public final class TestTransform {
	
	public static void main(String[] args) throws Exception {

		InputStream in = TestSDAXPath.class.getResourceAsStream("/addressbook.sdt");
		Transform tran = SDT.parse(new InputStreamReader(in, "UTF-8"));
		
		TransformContext c = new TransformContext.Builder() //.setWriter(SDT.nullWriter())
			.setStringParameter("filename", "c:/tmp/addressbook.sda").build();
		Writer w = c.getWriter();
		
		w.write("<<\n");
		DataNode node = tran.execute(c); 
		SDA.format(w, node);
		w.write(">>\n");
		w.close();
	}
}
