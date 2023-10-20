import java.io.FileReader;
import java.io.PrintWriter;

import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sdt.SDT;
import be.baur.sdt.Transform;
import be.baur.sdt.TransformContext;

public class demo {

	public static void main(String[] args) throws Exception {
		
		FileReader sdt = new FileReader(args[0]);
		Transform transform = SDT.parser().parse(sdt);

		TransformContext c = new TransformContext.Builder()
			.setStringParameter("filename", args[1])
			.setWriter(SDT.nullWriter())
			.build();
		
		DataNode output = transform.execute(c); 
		SDA.formatter().format(new PrintWriter(System.out), output);
	}
}
