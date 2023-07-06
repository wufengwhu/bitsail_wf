package com.hihonor.datacollector.realtime.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author w00027882
 */
@Getter
@Setter
public class EventsProperties {

    @SerializedName("_os")
    @JsonProperty("_os")
    private String os;

    @SerializedName("_os_ver")
    @JsonProperty("_os_ver")
    private String osVer;

    @SerializedName("_screenheight")
    @JsonProperty("_screenheight")
    private String screenHeight;

    @SerializedName("_screenwidth")
    @JsonProperty("_screenwidth")
    private String screenWidth;

    @SerializedName("event_session_name")
    @JsonProperty("event_session_name")
    private String eventSessionName;

    @SerializedName("first_session_event")
    @JsonProperty("first_session_event")
    private String firstSessionEvent;

    @SerializedName("event")
    @JsonProperty("event")
    private String event;

    @SerializedName("_sys_language")
    @JsonProperty("_sys_language")
    private String sysLanguage;


    @SerializedName("_cust_version")
    @JsonProperty("_cust_version")
    private String custVersion;

    @SerializedName("_start_type")
    @JsonProperty("_start_type")
    private String startType;

    @SerializedName("_start_cmd")
    @JsonProperty("_start_cmd")
    private String startCmd;

}
