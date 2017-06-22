package org.nudge.elasticstack;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Test class for {@link Utils}.
 */
public class UtilsTest {

	// inspired from
	// https://stackoverflow.com/questions/4021151/java-dateformat-is-not-threadsafe-what-does-this-leads-to
	private List<Future<?>> computeTask(Callable<?> task) throws Exception {
		//pool with 5 threads
		ExecutorService exec = Executors.newFixedThreadPool(5);
		List<Future<?>> results = new ArrayList<>();

		//perform 10 date conversions
		for(int i = 0 ; i < 10 ; i++){
			results.add(exec.submit(task));
		}
		exec.shutdown();

		return results;
	}

	/**
	 * Multi-threading test, except no exception and always the same result
	 */
	@Test
	public void formatTimeToString() throws Exception {
		final long ts = 1498147939000L;
		Callable<String> task = new Callable<String>(){
			@Override
			public String call() throws Exception {
				return Utils.formatTimeToString(ts);
			}
		};

		for (Future<?> future : computeTask(task)) {
			Assert.assertEquals("2017-06-22T18:12:19.000+0200", future.get());
		}
	}

	@Test
	public void getIndexSuffix() throws Exception {
		LocalDate localDate = new LocalDate();
		Assert.assertEquals(localDate.toString(), Utils.getIndexSuffix());
	}

}