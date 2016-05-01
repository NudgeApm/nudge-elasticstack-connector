package org.nudge.elasticstack.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// TODO se débarrasser de ce converter, il doit exister quelque-chose d'autre de plus adapté
import javax.xml.bind.DatatypeConverter;

import json.bean.TimeMeasure;
import json.bean.TimeSerie;

public class LogstashFileLogger implements Logger {

	// TODO maintenir un stream ouvert sur le fichier
	// TODO gérér une rotation du fichier de logs produit
	@Override
	public void log(TimeSerie serie) {
		try {
			final String sep = ",";
			String output = "";
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			File csvTemp = new File("csvTemp.csv");
			for (TimeMeasure ts : serie.getTimeSeries()) {
				Calendar calendar = DatatypeConverter.parseDateTime(ts.getDatetime());
				output = output.concat(sdf.format(calendar.getTime()).concat(sep).concat(ts.getTime()).concat(sep)
						.concat(ts.getCount()).concat(sep).concat(ts.getErrors()).concat(sep)
						.concat("java").concat("\n"));
			}

			FileWriter fileWriter = new FileWriter(csvTemp, true);
			BufferedWriter bw = new BufferedWriter(fileWriter);
			bw.write(output);
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new IllegalStateException(ioe);
		}
	}

}
