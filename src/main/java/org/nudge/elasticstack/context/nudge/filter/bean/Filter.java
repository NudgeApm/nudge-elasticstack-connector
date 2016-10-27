package org.nudge.elasticstack.context.nudge.filter.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Filter {

    public enum Status {
		ACTIVE, INACTIVE, DELETED;

        @JsonCreator
        public static Status fromString(String key) {
            for(Status status : Status.values()) {
                if(status.name().equalsIgnoreCase(key)) {
                    return status;
                }
            }
            return null;
        }
    }

    private UUID id;
    private String name;
    @JsonProperty(value = "target_code")
    private String targetCode;
    private Status status;

    private boolean exclusion;

    private List<Scope> scopes;

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

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isExclusion() {
        return exclusion;
    }

    public void setExclusion(boolean exclusion) {
        this.exclusion = exclusion;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", targetCode='" + targetCode + '\'' +
                ", status=" + status +
                ", exclusion=" + exclusion +
                ", scopes=" + scopes +
                '}';
    }
}
