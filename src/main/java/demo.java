import java.io.File;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.SDT;
import be.baur.sdt.TransformContext;
import be.baur.sdt.transform.Transform;

public class demo {

	public static void main(String[] args) throws Exception {
		
		Transform transform = SDT.parse(new File(args[0]));

		TransformContext c = new TransformContext.Builder()
			.setStringParameter("filename", args[1])
			.setWriter(SDT.nullWriter())
			.build();
		
		DataNode output = transform.execute(c); 
		System.out.println(SDA.format(output));
	}
}
