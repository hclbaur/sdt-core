
import java.io.File;

import org.jaxen.dom.DocumentNavigator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.transform.Transform;

public final class transformxml {

	public static void main(String[] args) throws Exception {

		String sdtfile = transform.class.getResource("/example-xml.sdt").getFile();
		Transform tran = SDT.parse(new File(sdtfile));

		String sdafile = transformxml.class.getResource("/example.xml").getFile();
		TransformContext c = new TransformContext.Builder()
			.setNavigator(DocumentNavigator.getInstance())
			.setStringParameter("filename", sdafile)
			.build();

		DataNode node = tran.execute(c);
		c.write(SDA.format(node));
	}
}
