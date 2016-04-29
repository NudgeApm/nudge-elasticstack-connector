package json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.bean.TimeSerie;
import json.bean.TimeSeries;
import json.connection.Connection;
import json.resolver.TimeSeriesModule;
import org.nudge.elasticstack.config.Configuration;

import javax.xml.bind.DatatypeConverter;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author Frédéric Massart
 */
public class NudgeApiPOC {

	private static final String NUDGE_HOST = ""; // redefine other env here

	private static String TIME_FROM;
	private static String TIME_TO;

	private static Connection c;
	private static ObjectMapper mapper;

	public void init(Configuration config) {
		System.out.println("---POC init....");
		String host = Connection.DEFAULT_URL;
		if (!NUDGE_HOST.isEmpty()) {
			host = NUDGE_HOST;
		} else {
			host = Connection.DEFAULT_URL;
		}
		System.out.println("host : " + host);
		c = new Connection(host);
		System.out.println("Login: " + config.getNudgeLogin());
		System.out.println("Password: " + config.getNudgePwd());
		c.login(config.getNudgeLogin(), config.getNudgePwd());

		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.registerModule(new TimeSeriesModule());
	}

	public static void start(Configuration config, Duration duration, Instant now) {
		NudgeApiPOC poc = new NudgeApiPOC();
		poc.init(config);

		System.out.println("---POC starting");
		// metrics=time,count,errors&from=2016-04-19T13:22Z&to=2016-04-19T14:22Z&step=1m
		Map<String, String> params = new HashMap<>();

		Instant fromInstant = poc.buildFromInstant(duration, now);
		params.put("from", poc.formatInstantToNudgeDate(fromInstant));
		params.put("to", poc.formatInstantToNudgeDate(now));
		params.put("metrics", "time,count,errors");
		params.put("step", "1m");

		Connection.LayerType layerType = Connection.LayerType.JAVA;

		// very dirty !!
		String appId = System.getProperty("appId");
		TimeSeries timeSeries = c.requestTimeSeries(appId, params, layerType, mapper);

		extractToCSV(timeSeries, layerType);
	}

	public static void extractToCSV(TimeSeries data, Connection.LayerType layerType) {
		final String sep = ",";
		String output = "";

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

		for (TimeSerie ts : data.getTimeSeries()) {
			Calendar calendar = DatatypeConverter.parseDateTime(ts.getDatetime());

			output = output.concat(sdf.format(calendar.getTime()).concat(sep).concat(ts.getTime()).concat(sep)
					.concat(ts.getCount()).concat(sep).concat(ts.getErrors()).concat(sep)
					.concat(layerType.name().toLowerCase()).concat("\n"));
		}

		try {
			File csvTemp = File.createTempFile("extractapi", ".csv");

			System.out.println("csv path : " + csvTemp.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(new FileWriter(csvTemp));
			bw.write(output);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param duration
	 * @param now
	 * @return
	 */
	public Instant buildFromInstant(Duration duration, Instant now) {
		return now.minusSeconds(duration.getSeconds());
	}

	public String formatInstantToNudgeDate(Instant instant) {
		 TimeZone tz = TimeZone.getTimeZone("UTC");
		 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		 df.setTimeZone(tz);
		 String nowAsISO = df.format(Date.from(instant));
		return nowAsISO;
	}
}
