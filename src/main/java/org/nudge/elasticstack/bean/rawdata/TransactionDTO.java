package org.nudge.elasticstack.bean.rawdata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction entity coming from Nudge APM data.
 *
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */
public class TransactionDTO {

    private String id;
    private String code;
    private long startTime;
    private long endTime;
    private String userIp;
    private List<LayerDTO> layers;

    public TransactionDTO() {
        this.id = UUID.randomUUID().toString();
    }

    public LayerDTO addNewLayerDTO() {
        if (getLayers() == null) {
            setLayers(new ArrayList<LayerDTO>());
        }
        LayerDTO layerDTO = new LayerDTO();
        getLayers().add(layerDTO);
        return layerDTO;
    }

    /*** Getters and Setters ***/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<LayerDTO> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerDTO> layers) {
        this.layers = layers;
    }
    
}
