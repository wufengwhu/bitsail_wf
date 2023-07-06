package com.hihonor.datacollector.realtime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
public class TableInformation implements Serializable {
    private String filterFieldName;

    private String tableName;

    private String filterFactor;

    private String isPublic;

    private String saving;

    private String fieldList;

    private String jsonLocationList;

    private String outPutPath;
}
