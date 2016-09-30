package org.nudge.elasticstack.bean.rawdata;

import java.util.Date;
import java.util.List;

/**
 * Created by Fred on 29/09/2016.
 */
public class MBeanFred {


    private long collectingTime;
    private String objectName;
    private int attributeInfoCount;

    List<AttributeInfo> attributeInfos;

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



