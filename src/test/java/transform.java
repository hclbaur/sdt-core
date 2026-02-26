

import java.io.File;
import java.io.Writer;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.transform.Transform;

public final class transform {
	
	public static void main(String[] args) throws Exception {
		
		String sdtfile = transform.class.getResource("/example.sdt").getFile();
		Transform tran = SDT.parse(new File(sdtfile));
		
		String sdafile = transform.class.getResource("/example.sda").getFile();
		TransformContext c = new TransformContext.Builder() //.setWriter(SDT.nullWriter())
			.setStringParameter("filename", sdafile).build();
		Writer w = c.getWriter();
		
		w.write("<<\n");
		DataNode node = tran.execute(c); 
		SDA.format(w, node);
		w.write(">>\n");
		w.flush();
	}
}
