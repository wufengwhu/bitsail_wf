package com.hihonor.datacollector.realtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DoubleColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.orc.*;

public class OrcReaderWriterTest {
    private static final int BATCH_SIZE = 2048;


    public static List<Map<String, Object>> read(Configuration configuration, String path)
            throws IOException {
        List<Map<String, Object>> rows = new LinkedList<>();


        try (Reader reader = OrcFile.createReader(new Path(path), OrcFile.readerOptions(configuration))) {
            TypeDescription schema = reader.getSchema();
            System.out.println(schema.toString());


            try (RecordReader records = reader.rows(reader.options())) {
                VectorizedRowBatch batch = reader.getSchema().createRowBatch(BATCH_SIZE);
                BytesColumnVector dataColumnVector = (BytesColumnVector) batch.cols[0];
                BytesColumnVector ipColumnVector = (BytesColumnVector) batch.cols[1];
                BytesColumnVector timeColumnVector = (BytesColumnVector) batch.cols[2];


                while (records.nextBatch(batch)) {
                    for (int rowNum = 0; rowNum < batch.size; rowNum++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("data", dataColumnVector.toString(rowNum));
                        map.put("ip", ipColumnVector.toString(rowNum));
                        map.put("time", timeColumnVector.toString(rowNum));
                        rows.add(map);
                    }
                }
            }
        }
        return rows;
    }

    public static void write(Configuration config, String path, String struct, List<Map<String, Object>> data) throws IOException {
        TypeDescription schema = TypeDescription.fromString(struct);
        VectorizedRowBatch batch = schema.createRowBatch();

        LongColumnVector orderIdColumnVector = (LongColumnVector) batch.cols[0];
        BytesColumnVector itemNameColumnVector = (BytesColumnVector) batch.cols[1];
        DoubleColumnVector priceColumnVector = (DoubleColumnVector) batch.cols[2];

        try (Writer writer = OrcFile.createWriter(new Path(path),OrcFile.writerOptions(config).setSchema(schema))) {
            for (Map<String, Object> row : data) {
                int rowNum = batch.size++;

                orderIdColumnVector.vector[rowNum] = (Integer) row.get("order_id");
                byte[] buffer = row.get("item_name").toString().getBytes(StandardCharsets.UTF_8);
                itemNameColumnVector.setRef(rowNum, buffer, 0, buffer.length);
                priceColumnVector.vector[rowNum] = (Float) row.get("price");

                if (batch.size == batch.getMaxSize()) {
                    writer.addRowBatch(batch);
                    batch.reset();
                }
            }

            if (batch.size != 0) {
                writer.addRowBatch(batch);
            }
        }
    }


    public static void main(String[] args) {
        String path = "D:\\tmp\\streaming_file_hdfs\\unknown\\pt_d=20230616\\pt_h=14";

        try {
            List<Map<String, Object>> orcFile = read(new Configuration(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
