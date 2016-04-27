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
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Frédéric Massart
 */
public class NudgeApiPOC {

	private static final String NUDGE_HOST = ""; // redefine other env here
	private static final String NUDGE_LOGIN = "";
	private static final String NUDGE_PWD = "";

	private static final String APP_ID = "";

	private static String TIME_FROM;
	private static String TIME_TO;

	private static Connection c;
	private static ObjectMapper mapper;

	static {

		System.out.println("---POC init....");
		String host = Connection.DEFAULT_URL;
		if (!NUDGE_HOST.isEmpty()) {
			host = NUDGE_HOST;
		} else {
			host = Connection.DEFAULT_URL;
		}
		System.out.println("host : " + host);
		c = new Connection(host);
		c.login(NUDGE_LOGIN, NUDGE_PWD);

		TIME_FROM = "2016-04-25_09:40";
		TIME_TO   = "2016-04-27_09:30";


		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.registerModule(new TimeSeriesModule());
	}



	public static void start(Configuration config) {
		System.out.println("---POC starting");
		// metrics=time,count,errors&from=2016-04-19T13:22:38Z&to=2016-04-19T14:22:38Z&step=1m
		Map<String, String> params = new HashMap<>();
		params.put("from", TIME_FROM);
		params.put("to", TIME_TO);
		params.put("metrics", "time,count,errors");
		params.put("step", "1m");

		Connection.LayerType layerType = Connection.LayerType.JAVA;

		TimeSeries timeSeries = c.requestTimeSeries(APP_ID, params, layerType, mapper);

		extractToCSV(timeSeries, layerType);
	}

	public static void extractToCSV(TimeSeries data, Connection.LayerType layerType) {
		final String sep = ",";
		String output = "";

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");


		for (TimeSerie ts : data.getTimeSeries()) {
			Calendar calendar = DatatypeConverter.parseDateTime(ts.getDatetime());


			output = output.concat(
					sdf.format(calendar.getTime()).concat(sep)
					.concat(ts.getTime()).concat(sep)
					.concat(ts.getCount()).concat(sep)
					.concat(ts.getErrors()).concat(sep)
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

}
