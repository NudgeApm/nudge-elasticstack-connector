package org.nudge.elasticstack.bean.rawdata;

import java.util.List;

/**
 * Transaction entity coming from Nudge APM data.
 *
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */
public class TransactionFred {

    private String code;
    private long startTime;
    private long endTime;

    private String userIp;

    private List<LayerFred> layers;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public List<LayerFred> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerFred> layers) {
        this.layers = layers;
    }
    
} // end of class
