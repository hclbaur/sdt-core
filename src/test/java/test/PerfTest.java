package test;

import java.util.Date;
import java.util.function.Consumer;

import be.baur.sda.dNode;

/** A convenience class with testing methods that accept Lamba expressions */
public final class PerfTest {

	private Consumer<dNode> nodeconsumer;
	
	public PerfTest(Consumer<dNode> nodeconsumer) {
		this.nodeconsumer = nodeconsumer;
	}

	
	public void test(String scenario, dNode input, long iterations, long runs) {

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

}
