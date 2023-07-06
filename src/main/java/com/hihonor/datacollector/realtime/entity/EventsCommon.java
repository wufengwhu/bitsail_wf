package com.hihonor.datacollector.realtime.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author w00027882
 */
@Getter
@Setter
public class EventsCommon implements Serializable {

    private String imei;

    @SerializedName("androidid")
    @JsonProperty("androidid")
    private String androidId;

    private String uuid;

    private String udid;

    private String oaid;

    private String upid;

    private String sn;

    @SerializedName("events_global_properties")
    @JsonProperty("events_global_properties")
    private String eventsGlobalProperties;

    @SerializedName("properties")
    @JsonProperty("properties")
    private EventsCommonProperties eventsCommonProperties;

}
