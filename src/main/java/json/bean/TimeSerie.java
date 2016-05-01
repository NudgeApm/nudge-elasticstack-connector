package json.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frédéric Massart
 */
//@JsonDeserialize(using = TimeSeriesDeserializer.class)

public class TimeSerie {

	List<TimeMeasure> timeSeries = new ArrayList<TimeMeasure>();

	public TimeSerie() {
	}

	public TimeSerie(List<TimeMeasure> timeSeries) {
		this.timeSeries = timeSeries;
	}

	public List<TimeMeasure> getTimeSeries() {
		return timeSeries;
	}

	public void setTimeSeries(List<TimeMeasure> timeSeries) {
		this.timeSeries = timeSeries;
	}

	@Override
	public String toString() {
		return "TimeSeries{" +
				"timeSeries=" + timeSeries +
				'}';
	}
}