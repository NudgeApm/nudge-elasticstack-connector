package org.nudge.elasticstack.json.bean;

/**
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 * 
 *         Description : Build Manage-Bean insertion
 */

public class EventMBean {

	// Attribut MBean attribute info
	private String nameMbean;
	private int nameId;
	private String typeMbean;
	private int typeId;
	private String valueMbean;
	private String type;

	// General Mbean attribute
	private String objectName;
	private int countAttribute;
	private long collectingTime;

	// Constructor
	public EventMBean(String nameMbean, String objectName, String type, int typeId, int nameId, String valueMbean,
			long collectingTime, int countAttribute) {
		this.setNameMbean(nameMbean);
		this.setNameId(nameId);
		this.setTypeId(typeId);
		this.setValueMbean(valueMbean);
		this.setCollectingTime(collectingTime);
		this.setCountAttribute(countAttribute);
		this.setObjectName(objectName);
		this.setType(type);
	}

	// ===========================
	//  Getters and Setters
	// ===========================
	public String getType() {
		return type;
	}

	public String setType(String type) {
		return this.type = type;
	}

	public String getObjectName() {
		return objectName;
	}

	public String setObjectName(String objectName) {
		return this.objectName = objectName;
	}

	public long getCollectingTime() {
		return collectingTime;
	}

	public long setCollectingTime(long collectingTime) {
		return this.collectingTime = collectingTime;
	}

	public int getNameId() {
		return nameId;
	}

	public int setNameId(int nameId) {
		return this.nameId = nameId;
	}

	
	

	public int getTypeId() {
		return typeId;
	}

	public int setTypeId(int typeId) {
		return this.typeId = typeId;
	}

	public String getValueMbean() {
		return valueMbean;
	}

	public String setValueMbean(String valueMbean) {
		return this.valueMbean = valueMbean;
	}

	public int getCountAttribute() {
		return countAttribute;
	}

	public int setCountAttribute(int countAttribute) {
		return this.countAttribute = countAttribute;
	}

	public String getNameMbean() {
		return nameMbean;
	}

	public String setNameMbean(String nameMbean) {
		return this.nameMbean = nameMbean;
	}

} // end of class
