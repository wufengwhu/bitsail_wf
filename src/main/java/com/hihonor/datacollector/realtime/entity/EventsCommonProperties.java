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
public class EventsCommonProperties implements Serializable {

    @SerializedName("_app_ver")
    @JsonProperty("_app_ver")
    private String appVer;

    @SerializedName("_channel")
    @JsonProperty("_channel")
    private String channel;

    @SerializedName("_emui_ver")
    @JsonProperty("_emui_ver")
    private String emuiVer;


    @SerializedName("_lib_ver")
    @JsonProperty("_lib_ver")
    private String libVer;

    @SerializedName("_mcc")
    @JsonProperty("_mcc")
    private String mcc;

    @SerializedName("_mnc")
    @JsonProperty("_mnc")
    private String mnc;


    @SerializedName("_model")
    @JsonProperty("_model")
    private String model;


    @SerializedName("_package_name")
    @JsonProperty("_package_name")
    private String packageName;


    @SerializedName("_rom_ver")
    @JsonProperty("_rom_ver")
    private String romVer;

    @SerializedName("_lib_name")
    @JsonProperty("_lib_name")
    private String libName;

    @SerializedName("_oaid_tracking_flag")
    @JsonProperty("_oaid_tracking_flag")
    private String oaidTrackingFlag;

    @SerializedName("_brand")
    @JsonProperty("_brand")
    private String brand;

    @SerializedName("_manufacturer")
    @JsonProperty("_manufacturer")
    private String manufacturer;
}
