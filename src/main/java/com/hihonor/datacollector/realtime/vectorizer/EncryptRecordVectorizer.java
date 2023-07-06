package com.hihonor.datacollector.realtime.vectorizer;

import org.apache.flink.orc.vector.Vectorizer;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static com.bytedance.bitsail.flink.core.serialization.AbstractDeserializationSchema.DUMP_ROW_VALUE_INDEX;

public class EncryptRecordVectorizer extends Vectorizer<Row> implements Serializable {

    public EncryptRecordVectorizer(String schema) {
        super(schema);
    }

    @Override
    public void vectorize(Row row, VectorizedRowBatch batch) throws IOException {
        BytesColumnVector stringVector0 = (BytesColumnVector) batch.cols[0];
        BytesColumnVector stringVector1 = (BytesColumnVector) batch.cols[1];
        BytesColumnVector stringVector2 = (BytesColumnVector) batch.cols[2];

        String input = new String((byte[]) row.getField(DUMP_ROW_VALUE_INDEX), StandardCharsets.UTF_8);
        if (!StringUtils.isNullOrWhitespaceOnly(input)) {
            int index = batch.size++;
            String[] fields = input.split("\u0001");
            stringVector0.setVal(index, fields[0].getBytes(StandardCharsets.UTF_8));
            stringVector1.setVal(index, fields[1].getBytes(StandardCharsets.UTF_8)); // ip
            stringVector2.setVal(index, fields[2].getBytes(StandardCharsets.UTF_8));  // server_time
        }
    }
}
