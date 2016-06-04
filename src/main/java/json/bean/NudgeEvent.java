package json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class NudgeEvent {

	// NudgeEvent Attributs
	private String date;
	private String name;
	protected long responseTime;
	private long count;
	private String type;

	// NudgeEvent constructors
	public NudgeEvent(String name, long responseTime, String date, long count, String type) {
		this.name = name;
		this.date = date;
		this.responseTime = responseTime;
		this.count = count;
		this.type = type;
	}

	// NudgeEvent method
	public String toString() {
		return " response-time :" + responseTime + "name = :" + name + "date = :" + date + "count :" + count;
	}

	// Getters and Setters
	@JsonProperty("@timestamp")
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		if (name.equals("JAX-WS")) {
			name = "JAXWS";
		} else {
			return name;
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("response_time")
	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

} // end of class
