package org.nudge.elasticstack.bean.rawdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer Bean of a {@link TransactionDTO}. <br/>
 * A layer can be a SQL type, WS type, Messaging (JMS) type...
 * // TODO FMA Use enum for typing
 *
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class LayerDTO {

    private String layerName;
    private long time;
    private long count;
    private List<LayerDetail> layerDetails;

    /*** Getters and Setters ***/

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<LayerDetail> getLayerDetails() {
        return layerDetails;
    }

    public void setLayerDetails(List<LayerDetail> layerDetails) {
        this.layerDetails = layerDetails;
    }

    /*** Utility methods ***/
    public LayerDetail createAddLayerDetail() {
        checkLayerDetailList();
        LayerDetail layerDetail = new LayerDetail();
        getLayerDetails().add(layerDetail);
        return layerDetail;
    }

    public LayerDetail addLayerDetail(LayerDetail layerDetail) {
        if (layerDetail == null) {
            throw new IllegalArgumentException("The layerDetail is invalid, must not be null");
        }
        checkLayerDetailList();

        getLayerDetails().add(layerDetail);
        return layerDetail;
    }

    private void checkLayerDetailList() {
        if (getLayerDetails() == null) {
            setLayerDetails(new ArrayList<LayerDetail>());
        }
    }

    public class LayerDetail {
        private String code;
        private long count;
        private long responseTime;
        private long timestamp;

		public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

    }
}
