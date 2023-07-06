package com.hihonor.datacollector.realtime.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author w00027882
 */
@Getter
@Setter
public class Events implements Serializable {

    private String event;

    @SerializedName("eventtime")
    @JsonProperty("eventtime")
    private String eventTime;

    private String type;

    @SerializedName("properties")
    @JsonProperty("properties")
    private Map<String, Object> eventsProperties;

}
