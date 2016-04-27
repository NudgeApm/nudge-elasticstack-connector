package json.bean;

/**
 * @author Frédéric Massart
 */
public class TimeSerie {

	String datetime;
	String time;
	String count;
	String errors;

	public TimeSerie() {
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "TimeSerie{" +
				"datetime='" + datetime + '\'' +
				", time='" + time + '\'' +
				", count='" + count + '\'' +
				", errors='" + errors + '\'' +
				'}';
	}
}
