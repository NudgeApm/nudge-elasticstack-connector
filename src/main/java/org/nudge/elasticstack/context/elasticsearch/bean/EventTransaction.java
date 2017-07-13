package org.nudge.elasticstack.context.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
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

	public EventTransaction(String appId, String hostname, String name, Long responseTime, String date, Long count, String transactionId) {
		super(appId, hostname, name, responseTime, date, count, "transaction", transactionId);
	}

	// ========================
	//  Getters and Setters
	// =========================
	
	// ******** Layer Jaxws ***********

	@JsonProperty("layer_jaxws_responsetime")
	public Long getResponseTimeLayerJaxws() {
		return responseTimeLayerJaxws;
	}
	
	public void setResponseTimeLayerJaxws(Long responseTimeLayerJaxws) {
		this.responseTimeLayerJaxws = responseTimeLayerJaxws;
	}

	@JsonProperty("layer_jaws_name")
	public String getLayerNameJaxws() {
		return layerNameJaxws;
	}
	
	public void setLayerNameJaxws(String layerNameJaxws) {
		this.layerNameJaxws = layerNameJaxws;
	}

	@JsonProperty("layer_jaxws_count")
	public Long getLayerCountJaxws() {
		return layerCountJaxws;
	}

	public void setLayerCountJaxws(Long layerCountJaxws) {
		this.layerCountJaxws = layerCountJaxws;
	}

	// ********* Layer sql *************
	
	@JsonProperty("layer_sql_responsetime")
	public Long getResponseTimeLayerSql() {
		return responseTimeLayerSql;
	}

	public void setResponseTimeLayerSql(Long responseTimeLayerSql) {
		this.responseTimeLayerSql = responseTimeLayerSql;
	}

	@JsonProperty("layer_sql_name")
	public String getLayerNameSql() {
		return layerNameSql;
	}

	public void setLayerNameSql(String layerNameSql) {
		this.layerNameSql = layerNameSql;
	}

	@JsonProperty("layer_sql_count")
	public Long getLayerCountSql() {
		return layerCountSql;
	}

	public void setLayerCountSql(Long layerCountSql) {
		this.layerCountSql = layerCountSql;
	}

	// ******** Layer jms **************
	
	@JsonProperty("layer_jms_responsetime")
	public Long getResponseTimeLayerJms() {
		return responseTimeLayerJms;
	}

	public void setResponseTimeLayerJms(Long responseTimeLayerJms) {
		this.responseTimeLayerJms = responseTimeLayerJms;
	}

	@JsonProperty("layer_jms_name")
	public String getLayerNameJms() {
		return layerNameJms;
	}

	public void setLayerNameJms(String layerNameJms) {
		this.layerNameJms = layerNameJms;
	}

	@JsonProperty("layer_jms_count")
	public Long getLayerCountJms() {
		return layerCountJms;
	}

	public void setLayerCountJms(Long layerCountJms) {
		this.layerCountJms = layerCountJms;
	}

	// ********** Layer java **************
	
	@JsonProperty("layer_java_responsetime")
	public long getResponseTimeLayerJava() {
		return responseTimeLayerJava;
	}

	public void setResponseTimeLayerJava(long responseTimeLayerJava) {
		this.responseTimeLayerJava = responseTimeLayerJava;
	} 

	@JsonProperty("layer_java_name")
	public String getLayerNameJava() {
		return layerNameJava;
	}

	public void setLayerNameJava(String layerNameJava) {
		this.layerNameJava = layerNameJava;
	}

	@JsonProperty("layer_java_count")
	public Long getLayerCountJava() {
		return layerCountJava;
	}

	public void setLayerCountJava(Long layerCountJava) {
		this.layerCountJava = layerCountJava;
	}
	
} //end of class
