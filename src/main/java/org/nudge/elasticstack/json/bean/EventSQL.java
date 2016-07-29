package org.nudge.elasticstack.json.bean;

/**
 * @author Sarah Bourgeois
 * 		   Frederic Massart
 * 
 *         Description : Build SQL insert
 */

public class EventSQL {

	// Attribut
	private long timestampSql;
	private String codeSql;
	private long countSql;
	private long timeSql;

	// Constructor
	public EventSQL(long timestampSql, String codeSql, long countSql, long timeSql) {
		super();
		this.setTimestampSql(timestampSql);
		this.setCodeSql(codeSql);
		this.setCountSql(countSql);
		this.setTimeSql(timeSql);
	}

	// ===========================
	// Getters and Setters
	// ===========================
	public long getTimestampSql() {
		return timestampSql;
	}

	public void setTimestampSql(long timestampSql) {
		this.timestampSql = timestampSql;
	}

	public String getCodeSql() {
		return codeSql;
	}

	public void setCodeSql(String codeSql) {
		this.codeSql = codeSql;
	}

	public long getCountSql() {
		return countSql;
	}

	public void setCountSql(long countSql) {
		this.countSql = countSql;
	}

	public long getTimeSql() {
		return timeSql;
	}

	public void setTimeSql(long timeSql) {
		this.timeSql = timeSql;
	}

}
