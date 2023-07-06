package com.hihonor.datacollector.realtime.crypto.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class ABClass implements Serializable {
    //actionCode
    private String actionCode = StringUtils.EMPTY;
    //uid
    private String uid = StringUtils.EMPTY;
    //deviceType
    private String deviceType = StringUtils.EMPTY;
    //uuid
    private String uuid = StringUtils.EMPTY;
    //ac
    private String ac = "ods";
    //strategies
    private String strategies = StringUtils.EMPTY;

    private String eventType = StringUtils.EMPTY;
    //groupId
    private String groupId = StringUtils.EMPTY;

    private String contents = StringUtils.EMPTY;
    //
    private String eventTime = StringUtils.EMPTY;
}
