package json.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frédéric Massart
 */
//@JsonDeserialize(using = TimeSeriesDeserializer.class)

public class TimeSeries {

	List<TimeSerie> timeSeries = new ArrayList<TimeSerie>();

	public TimeSeries() {
	}

	public TimeSeries(List<TimeSerie> timeSeries) {
		this.timeSeries = timeSeries;
	}

	public List<TimeSerie> getTimeSeries() {
		return timeSeries;
	}

	public void setTimeSeries(List<TimeSerie> timeSeries) {
		this.timeSeries = timeSeries;
	}

	@Override
	public String toString() {
		return "TimeSeries{" +
				"timeSeries=" + timeSeries +
				'}';
	}
}