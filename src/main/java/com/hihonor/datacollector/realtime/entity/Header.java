package com.hihonor.datacollector.realtime.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author w00027882
 */

@Getter
@Setter
@Data
public class Header implements Serializable {

    private String key;

    @SerializedName("appid")
    @JsonProperty("appid")
    private String appId;

    private String hmac;

    private String timestamp;

    @SerializedName("serviceid")
    @JsonProperty("serviceid")
    private String serviceId;

    @SerializedName("requestid")
    @JsonProperty("requestid")
    private String requestId;

    @SerializedName("servicetag")
    @JsonProperty("servicetag")
    private String serviceTag;

}
