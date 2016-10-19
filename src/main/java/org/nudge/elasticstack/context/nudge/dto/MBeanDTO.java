package org.nudge.elasticstack.context.nudge.dto;


import java.util.ArrayList;
import java.util.List;

/**
 * Entity corresponding to a MBean Java object, originally coming from Nudge APM data.
 *
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */
public class MBeanDTO {

    private long collectingTime;
    private String objectName;
    private int attributeInfoCount;

    private List<AttributeInfo> attributeInfos;

    public long getCollectingTime() {
        return collectingTime;
    }

    public void setCollectingTime(long l) {
        this.collectingTime = l;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public int getAttributeInfoCount() {
        return attributeInfoCount;
    }

    public void setAttributeInfoCount(int attributeInfoCount) {
        this.attributeInfoCount = attributeInfoCount;
    }

    public List<AttributeInfo> getAttributeInfos() {
        return attributeInfos;
    }

    public void setAttributeInfos(List<AttributeInfo> attributeInfos) {
        this.attributeInfos = attributeInfos;
    }

    public AttributeInfo addNewAttributeInfo() {
        if (getAttributeInfos() == null) {
            setAttributeInfos(new ArrayList<AttributeInfo>());
        }
        AttributeInfo attributeInfo = this.new AttributeInfo();
        getAttributeInfos().add(attributeInfo);
        return attributeInfo;
    }


    public class AttributeInfo {

        private int nameId;
        private String value;

        public int getNameId() {
            return nameId;
        }

        public void setNameId(int nameId) {
            this.nameId = nameId;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}



