package org.nudge.elasticstack.context.nudge.filter.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.UUID;

/**
 * Service JSON object, no completely fulfill
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {

    private UUID id;
    private String name;
    private Date lastData;
    private String host;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastData() {
        return lastData;
    }

    public void setLastData(Date lastData) {
        this.lastData = lastData;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
