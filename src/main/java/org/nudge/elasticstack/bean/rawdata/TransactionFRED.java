package org.nudge.elasticstack.bean.rawdata;


public class TransactionFRED {

	// layer
	private String layerName;
	private long layerResponseTime;
	private String layerDate;

	// sql
	private String sqlCode;
	private long sqlCount;
	private long sqlTime;
	private String sqlTimeStamp;

	// Layer constructor
	public TransactionFRED(String layerName, long responseTime, String date) {
		this.setLayerName(layerName);
		this.setLayerResponseTime(responseTime);
		this.setLayerDate(date);
	}

	// Sql constructor
	public TransactionFRED(String name, long count, long time, String timeStamp) {
		this.setSqlCode(name);
		this.setSqlCount(count);
		this.setSqlTime(time);
		this.setSqlTimeStamp(timeStamp);
	}

	// =========================
	// Layer - Getters Setters
	// ===========================
	public String getLayerName() {
		return layerName;
	}

	public String setLayerName(String layerName) {
		return this.layerName = layerName;
	}

	public long getLayerResponseTime() {
		return layerResponseTime;
	}

	public long setLayerResponseTime(long layerResponseTime) {
		return this.layerResponseTime = layerResponseTime;
	}

	public String getLayerDate() {
		return layerDate;
	}

	public String setLayerDate(String layerDate) {
		return this.layerDate = layerDate;
	}

	// =========================
	// Sql - Getters Setters
	// =========================
	public String getSqlCode() {
		return sqlCode;
	}

	public String setSqlCode(String sqlCode) {
		return this.sqlCode = sqlCode;
	}

	public long getSqlCount() {
		return sqlCount;
	}

	public long setSqlCount(long sqlCount) {
		return this.sqlCount = sqlCount;

	}

	public long getSqlTime() {
		return sqlTime;
	}

	public long setSqlTime(long sqlTime) {
		return this.sqlTime = sqlTime;
	}

	public String getSqlTimeStamp() {
		return sqlTimeStamp;
	}

	public String setSqlTimeStamp(String sqlTimestamp2) {
		return this.sqlTimeStamp = sqlTimestamp2;

	}

} // end of class
