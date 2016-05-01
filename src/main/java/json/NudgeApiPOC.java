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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author Frédéric Massart
 */
public class NudgeApiPOC {

	private static final String NUDGE_HOST = ""; // redefine other env here
	private static Connection c;
	private static ObjectMapper mapper;

	/**
	 * 
	 * @param config
	 */
	public void init(Configuration config) {

		System.out.println("----Connexion----....");
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

	/**
	 * 
	 * @param config
	 * @param duration
	 * @param now
	 */
	public static void start(Configuration config, Duration duration, Instant now) {

		NudgeApiPOC poc = new NudgeApiPOC();

		poc.init(config);
		System.out.println("----Plugin is starting----");
		Map<String, String> params = new HashMap<>();
		Instant fromInstant = poc.buildFromInstant(duration, now);
		params.put("from", poc.formatInstantToNudgeDate(fromInstant));
		params.put("to", poc.formatInstantToNudgeDate(now));
		params.put("metrics", "time,count,errors"); // TODO : To permit metrics
													// can be write on the
													// config file
		params.put("step", "1m");
		Connection.LayerType layerType = Connection.LayerType.JAVA;
		TimeSeries timeSeries = c.requestTimeSeries(config.getAppid(), params, layerType, mapper);
		extractToCSV(timeSeries, layerType);
	}

	/**
	 * 
	 * @param data
	 * @param layerType
	 */
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
			File csvTemp = new File("csvTemp.csv");
			FileWriter fileWriter = new FileWriter(csvTemp, true);
			BufferedWriter bw = new BufferedWriter(fileWriter);
			bw.write(output);
			bw.close();

			System.out.println("Done. Your file path is " + csvTemp.getAbsolutePath());

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

	
	/**
	 * 
	 * @param instant
	 * @return
	 */
	public String formatInstantToNudgeDate(Instant instant) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(Date.from(instant));
		return nowAsISO;
	}
}
