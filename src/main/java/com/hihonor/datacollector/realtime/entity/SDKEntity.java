package com.hihonor.datacollector.realtime.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author w00027882
 */
@Getter
@Setter
@AllArgsConstructor
public class SDKEntity implements Serializable {

    private Header header;

    @SerializedName("events_common")
    private EventsCommon eventsCommon;

    private Events events;

    @SerializedName("ip")
    private String clientIp;

    @SerializedName("time")
    private String serverTime;

    public static byte[] toByteArray(SDKEntity sdkEntity) {
        return SerializationUtils.serialize(sdkEntity);
    }

}
