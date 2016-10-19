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
    private List<LayerCallDTO> calls;

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

    public List<LayerCallDTO> getCalls() {
        return calls;
    }

    public void setCalls(List<LayerCallDTO> calls) {
        this.calls = calls;
    }

    /*** Utility methods ***/
    public LayerCallDTO createAddLayerDetail() {
        checkLayerDetailList();
        LayerCallDTO layerCall = new LayerCallDTO();
        getCalls().add(layerCall);
        return layerCall;
    }

    public LayerCallDTO addLayerDetail(LayerCallDTO layerCall) {
        if (layerCall == null) {
            throw new IllegalArgumentException("The layerCall is invalid, must not be null");
        }
        checkLayerDetailList();

        getCalls().add(layerCall);
        return layerCall;
    }

    private void checkLayerDetailList() {
        if (getCalls() == null) {
            setCalls(new ArrayList<LayerCallDTO>());
        }
    }

}
