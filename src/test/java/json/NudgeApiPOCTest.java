package json;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

/**
 * @author Frédéric Massart
 */
public class NudgeApiPOCTest {

	NudgeApiPOC nudgeApiPOC = new NudgeApiPOC();

	@Test
	public void buildFromInstantTest() {
		// given
		Duration duration = Duration.ofDays(2);

		Instant now = Instant.parse("2014-03-18T00:00:00.000Z");
		Instant expectedInstant = Instant.parse("2014-03-16T00:00:00.000Z");

		// when
		Instant fromInstant = nudgeApiPOC.buildFromInstant(duration, now);

		// then
		assertEquals(expectedInstant, fromInstant);
	}


	@Test
	public void formatInstantToNudgeDateTestWithLocale() {
		// given
		String date = "2014-03-16T12:00:00.000Z";

		// when
		String nudgeDate = nudgeApiPOC.formatInstantToNudgeDate(Instant.parse("2014-03-16T12:00:00.000Z"));

		// then
		assertEquals(date, nudgeDate);

	}
}