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
    private List<Call> calls;

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

    public List<Call> getCalls() {
        return calls;
    }

    public void setCalls(List<Call> calls) {
        this.calls = calls;
    }

    /*** Utility methods ***/
    public Call createAddLayerDetail() {
        checkLayerDetailList();
        Call layerCall = new Call();
        getCalls().add(layerCall);
        return layerCall;
    }

    public Call addLayerDetail(Call layerCall) {
        if (layerCall == null) {
            throw new IllegalArgumentException("The layerCall is invalid, must not be null");
        }
        checkLayerDetailList();

        getCalls().add(layerCall);
        return layerCall;
    }

    private void checkLayerDetailList() {
        if (getCalls() == null) {
            setCalls(new ArrayList<Call>());
        }
    }

    public class Call {
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
