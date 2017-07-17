package org.nudge.elasticstack.context.nudge.api.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * JSON API object from Nudge API. <br/>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class App {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
