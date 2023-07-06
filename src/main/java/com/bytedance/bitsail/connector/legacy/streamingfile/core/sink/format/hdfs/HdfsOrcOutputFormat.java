/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedance.bitsail.connector.legacy.streamingfile.core.sink.format.hdfs;

import com.bytedance.bitsail.common.BitSailException;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.exception.CommonErrorCode;
import com.bytedance.bitsail.connector.legacy.streamingfile.common.option.FileSystemSinkOptions;
import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.core.fs.Path;
import org.apache.flink.orc.vector.Vectorizer;
import org.apache.flink.orc.writer.OrcBulkWriterFactory;
import org.apache.flink.types.Row;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * HdfsOrcOutputFormat.
 */
public class HdfsOrcOutputFormat<IN extends Row> extends AbstractHdfsOutputFormat<IN> {
    private static final Logger LOG = LoggerFactory.getLogger(HdfsOrcOutputFormat.class);

    private static final long serialVersionUID = 1L;

    OrcBulkWriterFactory<IN> factory;

    private String schema = "struct<_col0:string,_col1:string,_col2:string>";

    private String vectorClassName = "";

    private BulkWriter<IN> bulkWriter;

    public HdfsOrcOutputFormat(final BitSailConfiguration jobConf, Path outputPath) {
        super(jobConf, outputPath);
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        this.schema = jobConf.getUnNecessaryOption(FileSystemSinkOptions.HDFS_ORC_SCHEMA, schema);
        // TODO 反射获取对应得 vectorizer
        Vectorizer<IN> vectorizer = null;
        try {
            vectorizer = getRowVectorizerClass(this.jobConf, schema);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Properties writerProps = new Properties();
//        writerProps.setProperty("orc.compress", "LZ4");
        this.factory = new OrcBulkWriterFactory<>(vectorizer, writerProps, new Configuration());
        super.open(taskNumber, numTasks);
    }

    private Vectorizer<IN> getRowVectorizerClass(BitSailConfiguration jobConf, String schema) throws Exception {
        this.vectorClassName = jobConf.getUnNecessaryOption(FileSystemSinkOptions.HDFS_ORC_VECTORIZER, vectorClassName);
//        LOG.info("Vectorizer class name is {}", vectorClassName);
        Class<?> vectorClass = Class.forName(vectorClassName);

        if (Vectorizer.class.isAssignableFrom(vectorClass)) {
            return (Vectorizer) vectorClass.getConstructor(String.class).newInstance(schema);
        }

        throw BitSailException.asBitSailException(CommonErrorCode.CONFIG_ERROR,
                "Transformer " + vectorClass.getName() + "class is not supported ");

    }

    @Override
    public void close() throws IOException {
//        bulkWriter.finish();
        super.close();
    }

    @Override
    protected void sync() throws IOException {
        if (compressionOutputStream != null) {
            compressionOutputStream.flush();
        }
//        outputStream.hflush();
//        hadoopDataOutputStream.flush();
        bulkWriter.flush();
    }

    @Override
    public void writeRecordInternal(IN record) throws IOException {
        if (record == null) {
            return;
        }

//        this.outputStream.write(value);
//        this.outputStream.write(NEWLINE);
//            System.out.println(new String(value, StandardCharsets.UTF_8));

//        写orc 格式文件, 调用orc bulk writer  批量写
//        System.out.println(Thread.currentThread().getId() + " ##### get record count xxxx = " + recordCount);
        bulkWriter.addElement(record);
    }


    @Override
    protected void initRecordWriter() throws IOException {
        initCompressFileOutputStream();
        bulkWriter = factory.create(this.hadoopDataOutputStream);
    }

    @Override
    public String toString() {
        return "HdfsOrcOutputFormat(" + outputPath.toString() + ")";
    }
}
