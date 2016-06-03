package org.nudge.elasticstack;

import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Frédéric Massart
 */
public class DaemonTest {

	@Test
	public void testRetrieveDuration() throws Exception {
		// given
		long period = 10;
		TimeUnit timeUnit = TimeUnit.DAYS;
		Duration expectedDuration = Duration.ofDays(10);
	}
}