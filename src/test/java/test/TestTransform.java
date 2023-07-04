package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sdt.SDT;
import be.baur.sdt.Transform;
import be.baur.sdt.TransformContext;

public final class TestTransform {
	
	public static void main(String[] args) throws Exception {

		InputStream in = TestSDAXPath.class.getResourceAsStream("/example.sdt");
		Transform tran = SDT.parser().parse(new InputStreamReader(in, "UTF-8"));
		
		TransformContext c = new TransformContext.Builder()
			.setStringParameter("filename", "c:/tmp/addressbook.sda").build();
		//c = new TransformContext.Builder().setWriter(SDT.nullWriter()).build();
		Writer w = c.getWriter();
		
		w.write("<<\n");
		Node node = tran.execute(c); 
		SDA.formatter().format(w, node);
		w.write(">>\n");
		w.close();
	}
}
