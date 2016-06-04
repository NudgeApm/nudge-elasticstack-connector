package json.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("deprecation")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EventTransaction extends NudgeEvent {

	// WS attributs
	private Long responseTimeLayerJaxws;
	private String layerNameJaxws;
	private Long layerCountJaxws;
	// SQL attributs
	private Long responseTimeLayerSql;
	private String layerNameSql;
	private Long layerCountSql;
	// JMS attributs
	private Long responseTimeLayerJms;
	private String layerNameJms;
	private Long layerCountJms;
	// JAVA attributs
	private Long responseTimeLayerJava;
	private String layerNameJava;
	private Long layerCountJava;

	// Constructor
	public EventTransaction(String name, Long responseTime, String date, Long count) {
		super(name, responseTime, date, count, "transaction");
	}

	// Getters and Setters
	public Long getResponseTimeLayerJaxws() {
		return responseTimeLayerJaxws;
	}

	public void setResponseTimeLayerJaxws(Long responseTimeLayerJaxws) {
		this.responseTimeLayerJaxws = responseTimeLayerJaxws;
	}

	public String getLayerNameJaxws() {
		return layerNameJaxws;
	}

	public void setLayerNameJaxws(String layerNameJaxws) {
		this.layerNameJaxws = layerNameJaxws;
	}

	public Long getLayerCountJaxws() {
		return layerCountJaxws;
	}

	public void setLayerCountJaxws(Long layerCountJaxws) {
		this.layerCountJaxws = layerCountJaxws;
	}

	public Long getResponseTimeLayerSql() {
		return responseTimeLayerSql;
	}

	public void setResponseTimeLayerSql(Long responseTimeLayerSql) {
		this.responseTimeLayerSql = responseTimeLayerSql;
	}

	public String getLayerNameSql() {
		return layerNameSql;
	}

	public void setLayerNameSql(String layerNameSql) {
		this.layerNameSql = layerNameSql;
	}

	public Long getLayerCountSql() {
		return layerCountSql;
	}

	public void setLayerCountSql(Long layerCountSql) {
		this.layerCountSql = layerCountSql;
	}

	public Long getResponseTimeLayerJms() {
		return responseTimeLayerJms;
	}

	public void setResponseTimeLayerJms(Long responseTimeLayerJms) {
		this.responseTimeLayerJms = responseTimeLayerJms;
	}

	public String getLayerNameJms() {
		return layerNameJms;
	}

	public void setLayerNameJms(String layerNameJms) {
		this.layerNameJms = layerNameJms;
	}

	public Long getLayerCountJms() {
		return layerCountJms;
	}

	public void setLayerCountJms(Long layerCountJms) {
		this.layerCountJms = layerCountJms;
	}

	public long getResponseTimeLayerJava() {
		return responseTimeLayerJava;
	}

	public void setResponseTimeLayerJava(long responseTimeLayerJava) {
		this.responseTimeLayerJava = responseTimeLayerJava;
	}

	public String getLayerNameJava() {
		return layerNameJava;
	}

	public void setLayerNameJava(String layerNameJava) {
		this.layerNameJava = layerNameJava;
	}

	public Long getLayerCountJava() {
		return layerCountJava;
	}

	public void setLayerCountJava(Long layerCountJava) {
		this.layerCountJava = layerCountJava;
	}

}
