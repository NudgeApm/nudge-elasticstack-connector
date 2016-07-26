package json.bean;

import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary.DictionaryEntry;

/**
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 * 
 *         Description : Build Manage-Bean insertion
 */

public class EventMBean {

	// Attribut MBean attribute info
	private DictionaryEntry nameMbean;
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
	public EventMBean(DictionaryEntry nameMbean2, String objectName, String typeMBean, int typeId, int nameId, String valueMbean,
			long collectingTime, int countAttribute) {
		this.setNameMbean(nameMbean2);
		this.setNameId(nameId);
		this.setTypeMbean(typeMBean);
		this.setTypeId(typeId);
		this.setValueMbean(valueMbean);
		this.setCollectingTime(collectingTime);
		this.setCountAttribute(countAttribute);
		this.setObjectName(objectName);
	}

	// Method
	// public String toString() {
	// return "objectName : " + objectName + "type : " + "mbean" + " type_id : "
	// + typeId + " name : " + nameMbean + " name_id : " + nameId + " value : "
	// + valueMbean + " collecting time : " + collectingTime + " count attribute
	// :" + countAttribute;
	//
	// }

	// Getters and Setters
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObjectName() {
		return objectName;
	}

	public String setObjectName(String objectName) {
		return this.objectName = objectName;
	}

	// @JsonProperty("@timestamp")
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

	public String getTypeMbean() {
		return typeMbean;
	}

	public String setTypeMbean(String typeMbean) {
		return this.typeMbean = typeMbean;
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

	public DictionaryEntry getNameMbean() {
		return nameMbean;
	}

	public DictionaryEntry setNameMbean(DictionaryEntry nameMbean2) {
		return this.nameMbean = nameMbean2;
	}


} // end of class
