package json.bean;

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
	public EventMBean(String nameMbean, String objectName, String typeMBean, int typeId, int nameId, String valueMbean,
			long collectingTime, int countAttribute) {
		this.setNameMbean(nameMbean);
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

	public void setCollectingTime(long collectingTime) {
		this.collectingTime = collectingTime;
	}

	public int getNameId() {
		return nameId;
	}

	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

	public String getTypeMbean() {
		return typeMbean;
	}

	public void setTypeMbean(String typeMbean) {
		this.typeMbean = typeMbean;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getValueMbean() {
		return valueMbean;
	}

	public void setValueMbean(String valueMbean) {
		this.valueMbean = valueMbean;
	}

	public int getCountAttribute() {
		return countAttribute;
	}

	public void setCountAttribute(int countAttribute) {
		this.countAttribute = countAttribute;
	}

	public String getNameMbean() {
		return nameMbean;
	}

	public void setNameMbean(String nameMbean) {
		this.nameMbean = nameMbean;
	}


} // end of class
