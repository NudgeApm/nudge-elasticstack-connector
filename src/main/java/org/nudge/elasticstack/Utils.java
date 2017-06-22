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

	private static final ThreadLocal<DateFormat> indexf = new ThreadLocal<>();
	private static final ThreadLocal<DateFormat> timef = new ThreadLocal<>();

	private Utils() {
	}

	public static String formatTimeToString(long timestamp) {
		DateFormat sdf = timef.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat(ES_DATE_FORMAT);
			timef.set(sdf);
		}
		return sdf.format(timestamp);
	}

	public static String getIndexSuffix() {
		DateFormat sdf = indexf.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat(INDEX_SUFFIX_FMT);
			indexf.set(sdf);
		}
		return sdf.format(new Date());
	}

}
