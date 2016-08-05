package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sarah Bourgeois
 * 		   Frederic Massart
 * 
 *         Description : Build SQL insert
 */

public class EventSQL {

	// Attribut
	private String timestampSql;
	private String codeSql;
	private long countSql;
	private long timeSql;

	// Constructor
	public EventSQL(String timestampSql, String codeSql, long countSql, long timeSql) {
		super();
		this.setTimestampSql(timestampSql);
		this.setCodeSql(codeSql);
		this.setCountSql(countSql);
		this.setTimeSql(timeSql);
	}

	// ===========================
	// Getters and Setters
	// ===========================
	
	@JsonProperty("@timestamp")
	public String getTimestampSql() {
		return timestampSql;
	}

	public void setTimestampSql(String timestampSql) {
		this.timestampSql = timestampSql;
	}

	@JsonProperty("sql_code")
	public String getCodeSql() {
		return codeSql;
	}

	public void setCodeSql(String codeSql) {
		this.codeSql = codeSql;
	}

	@JsonProperty("sql_count")
	public long getCountSql() {
		return countSql;
	}

	public void setCountSql(long countSql) {
		this.countSql = countSql;
	}

	@JsonProperty("sql_responsetime")
	public long getTimeSql() {
		return timeSql;
	}

	public void setTimeSql(long timeSql) {
		this.timeSql = timeSql;
	}

}
