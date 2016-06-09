package json.connection;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Frédéric Massart
 */
public class ConnectionTest {


	@Test
	public void build_from_and_to_for_the_api_request() {
		// given
		long time = 1420107000000L; // 01/01/2015 @ 11:10am (GMT)
		Connection con = new Connection(null);

		// when
		String fromTo = con.buildFromTo(new Date(time));

		// then
		assertEquals("from=2015-01-01T11:00:00Z&to=2015-01-01T11:10:00Z", fromTo);
	}

}