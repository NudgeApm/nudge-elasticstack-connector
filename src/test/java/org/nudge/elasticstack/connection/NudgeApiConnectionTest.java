package org.nudge.elasticstack.connection;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for NudgeApiConnection class.
 *
 * @author Bourgeois Sarah
 *         Frederic Massart
 */
public class NudgeApiConnectionTest {

	// ==========================
	// Empty rawdata
	// ==========================
	@Test
	public void parseRawdataListResponseTest() {
		// ***** given *****
		NudgeApiConnection connection = new NudgeApiConnection("mockApiUrl", "any-token");
		// Simulation of API answer
		InputStream mockResponseStream = new ByteArrayInputStream(
				"[\"collecte_2016-07-28_18-45-02-146_94.dat\",\"collecte_2016-07-28_18-45-02-206_86.dat\"]".getBytes());
		
		List<String> expectedList = new ArrayList<>();
		expectedList.add("collecte_2016-07-28_18-45-02-146_94.dat");
		expectedList.add("collecte_2016-07-28_18-45-02-206_86.dat");

		// ***** when *****
		List<String> rawdataList = connection.parseRawdataListResponse(mockResponseStream);

		// ***** then *****
		Assert.assertEquals(expectedList, rawdataList);

	}

	// ==========================
	// Rawdata without Transaction
	// ==========================
	@Test
	public void parseRawdataListResponseTest2() {
		// ***** given *****
		NudgeApiConnection connection = new NudgeApiConnection("mockApiUrl2", "any-token");
		InputStream mockResponseStream = new ByteArrayInputStream(
				"[\"collecte_2016-06-08_00-53-01-676_1311.dat\"]".getBytes());
		
		List<String> expectedList = new ArrayList<>();
		expectedList.add("collecte_2016-06-08_00-53-01-676_1311.dat");
 
		// ***** when *****
		List<String> rawdataList = connection.parseRawdataListResponse(mockResponseStream);

		// ***** then *****
		Assert.assertEquals(expectedList, rawdataList);
	}
	
	// ==========================
	// No rawdata : empty tab
	// ==========================
	@Test
	public void parseRawdataListResponseTest3() {
		// ***** given *****
		NudgeApiConnection connection = new NudgeApiConnection("mockApiUrl2", "any-token");
		InputStream mockResponseStream = new ByteArrayInputStream(
				"[]".getBytes());
		
		List<String> expectedList = new ArrayList<>();   

 
		// ***** when ***** 
		List<String> rawdataList = connection.parseRawdataListResponse(mockResponseStream);

		// ***** then *****
		Assert.assertEquals(expectedList, rawdataList);
	}
}