package json.bean;

/**
 * @author Fred
 */
public class Layer {

	private String type;
	private Long time;
	private Long count;

	public Layer(String type, Long time, Long count) {
		this.type = type;
		this.time = time;
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
