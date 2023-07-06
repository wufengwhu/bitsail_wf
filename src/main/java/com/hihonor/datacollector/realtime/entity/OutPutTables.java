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
public class OutPutTables implements Serializable {

    private String inputPath;

    private String outputPath;

    private TableInformation[] tablesInformation;
}
