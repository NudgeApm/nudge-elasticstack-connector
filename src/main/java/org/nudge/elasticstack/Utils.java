package org.nudge.elasticstack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for the connector.
 */
public class Utils {

	private static final String ES_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final String INDEX_SUFFIX_FMT = "yyyy-MM-dd";

	private static final ThreadLocal<DateFormat> df = new ThreadLocal<>();

	private Utils() {
	}

	public static String formatTimeToString(long timestamp) {
		df.set(new SimpleDateFormat(ES_DATE_FORMAT));
		return df.get().format(timestamp);
	}

	public static String getIndexSuffix() {
		df.set(new SimpleDateFormat(INDEX_SUFFIX_FMT));
		return df.get().format(new Date());
	}


}
