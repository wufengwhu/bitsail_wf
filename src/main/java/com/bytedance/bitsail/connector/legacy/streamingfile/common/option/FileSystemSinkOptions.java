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

package com.bytedance.bitsail.connector.legacy.streamingfile.common.option;

import com.alibaba.fastjson.TypeReference;
import com.bytedance.bitsail.common.option.ConfigOption;
import com.bytedance.bitsail.common.option.ConfigOptions;
import com.bytedance.bitsail.common.option.WriterOptions;

import java.util.Map;

import static com.bytedance.bitsail.common.option.ConfigOptions.key;
import static com.bytedance.bitsail.common.option.WriterOptions.WRITER_PREFIX;

/**
 * Created 2022/8/16
 */
public interface FileSystemSinkOptions extends WriterOptions.BaseWriterOptions {
    ConfigOption<Boolean> HDFS_OVERWRITE =
            key(WRITER_PREFIX + "hdfs.overwrite")
                    .defaultValue(false);

    ConfigOption<Boolean> HDFS_COMPRESSION_CONFIG =
            key(WRITER_PREFIX + "hdfs.compression.config")
                    .defaultValue(true);

    /**
     * compression type for hdfs/hive dump, optional choice:
     */
    ConfigOption<String> HDFS_COMPRESSION_CODEC =
            key(WRITER_PREFIX + "hdfs.compression_codec")
                    .defaultValue("none");

    ConfigOption<String> HDFS_DUMP_TYPE =
            key(WRITER_PREFIX + "hdfs.dump_type")
                    .noDefaultValue(String.class);

    ConfigOption<String> HIVE_OUTPUTFORMAT_PROPERTIES =
            key(WRITER_PREFIX + "hive.outputformat.properties")
                    .noDefaultValue(String.class);

    ConfigOption<String> PARQUET_COMPRESSION =
            key(WRITER_PREFIX + "parquet_compression")
                    .noDefaultValue(String.class);

  ConfigOption<String> HDFS_ORC_SCHEMA =
          key(WRITER_PREFIX + "hdfs.orc_schema")
                  .noDefaultValue(String.class);
    ConfigOption<String> HDFS_ORC_VECTORIZER =
        ConfigOptions.key("job.writer.vectorizer_class").noDefaultValue(String.class);

    ConfigOption<Short> HDFS_REPLICATION =
            key(WRITER_PREFIX + "hdfs.replication")
                    .defaultValue((short) 3);

    ConfigOption<Map<String, String>> CUSTOMIZED_HADOOP_CONF =
            key(WRITER_PREFIX + "customized_hadoop_conf")
                    .onlyReference(new TypeReference<Map<String, String>>() {
                    });

    ConfigOption<String> HIVE_METASTORE_PROPERTIES =
            key(WRITER_PREFIX + "metastore_properties")
                    .noDefaultValue(String.class);
}
