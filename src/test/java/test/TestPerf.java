package test;

import java.io.File;
import java.util.Date;
import java.util.function.Consumer;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sdt.parser.SDTParser;

/** A convenience class with testing methods that accept Lamba expressions */
public final class TestPerf {

	private Consumer<DataNode> nodeconsumer;
	
	public TestPerf(Consumer<DataNode> nodeconsumer) {
		this.nodeconsumer = nodeconsumer;
	}

	
	public void test(String scenario, DataNode input, long iterations, long runs) {

		System.out.print(scenario);
		long total = 0, r = 0;
		while (r < runs) {
			
			long i = iterations; ++r;
			long start = new Date().getTime();
			while (i > 0) {
				nodeconsumer.accept(input); --i;
			}
			long duration = new Date().getTime() - start;
			total += duration; System.out.print(" " + total/r);
		}
	}

	public static void main(String[] args) throws Exception {
	
		// test performance
		DataNode sdt = SDA.parse(new File(TestSDTParser.class.getResource("/addressbook.sdt").getFile()));
		
		TestPerf p = new TestPerf(sdtnode -> {
			try {
				SDTParser.parse(sdtnode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		p.test("\nPerfTest  : P01", sdt, 12500, 5);
		
	}	
}
