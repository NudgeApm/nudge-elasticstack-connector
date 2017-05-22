package org.nudge.elasticstack;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for the connector.
 */
public class Utils {
	public static final SimpleDateFormat ES_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	public static final SimpleDateFormat INDEX_SUFFIX_FMT = new SimpleDateFormat("yyyy-MM-dd");

	public static String getIndexSuffix() {
		return INDEX_SUFFIX_FMT.format(new Date());
	}
}
