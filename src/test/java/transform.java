

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.transform.Transform;

public final class transform {
	
	public static void main(String[] args) throws Exception {
		
		InputStream in = transform.class.getResourceAsStream("/example.sdt");
		Transform tran = SDT.parse(new InputStreamReader(in, "UTF-8"));
		
		String file = transform.class.getResource("/example.sda").getFile();
		TransformContext c = new TransformContext.Builder() //.setWriter(SDT.nullWriter())
			.setStringParameter("filename", file).build();
		Writer w = c.getWriter();
		
		w.write("<<\n");
		DataNode node = tran.execute(c); 
		SDA.format(w, node);
		w.write(">>\n");
		w.flush();
	}
}
