package com.bytedance.bitsail.connector.legacy.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.sql.ResultSet;

@Slf4j
public abstract class AbstractRowConverter<SourceT, LookupT, SinkT, T> implements Serializable {

    /**
     * Convert data retrieved from {@link ResultSet} to internal {@link RowData}.
     *
     * @param input from JDBC
     * @return return
     * @throws Exception Exception
     */
    public abstract Row toInternal(SourceT input) throws Exception;

    /**
     * @param input input
     * @return RowData
     * @throws Exception Exception
     */
    public Row toInternalLookup(SourceT sourceT, LookupT input) throws Exception {
        throw new RuntimeException("Subclass need rewriting");
    }

    /**
     * BinaryRowData
     *
     * @param rowData rowData
     * @param output  output
     * @return return
     * @throws Exception Exception
     */
    public abstract SinkT toExternal(Row rowData, SinkT output) throws Exception;

}
