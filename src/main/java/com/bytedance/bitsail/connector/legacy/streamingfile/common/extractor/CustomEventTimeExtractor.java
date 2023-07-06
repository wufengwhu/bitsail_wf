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

package com.bytedance.bitsail.connector.legacy.streamingfile.common.extractor;

import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.util.FieldPathUtils;
import com.bytedance.bitsail.connector.legacy.streamingfile.common.option.FileSystemCommonOptions;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @class: CustomEventTimeExtractor
 * @desc:
 **/

public class CustomEventTimeExtractor extends AbstractEventTimeExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(CustomEventTimeExtractor.class);
    private transient EventTimeExtractor extractor;


    private List<FieldPathUtils.PathInfo> pathInfos;

    public CustomEventTimeExtractor(BitSailConfiguration jobConf) {
        super(jobConf);
    }

    @Override
    public Object parse(byte[] record) throws Exception {
        getCustomExtractor();
        return this.extractor.parse(record);
    }

    @Override
    protected long extract(Object record, long defaultTimestamp) {
        return timeToMs(this.extractor.extract(record));
    }

    @Override
    public String getField(Object record, FieldPathUtils.PathInfo pathInfo, String defaultValue) {
        try {
            return this.extractor.getField(record, pathInfo.getName(), defaultValue);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unsupported get field action.");
        }
    }

    private void getCustomExtractor() throws Exception {
        if (Objects.isNull(this.extractor)) {
            LOG.info("extractor is null, construct custom extractor");

            if (isEventTime) {
                String customExtractorClassPath =  Preconditions.checkNotNull(jobConf.get(FileSystemCommonOptions.ArchiveOptions.CUSTOM_EXTRACTOR_CLASSPATH));
                // 判断是EVENT_TIME_FIELDS 类型还是 EVENT_TIME_INDEX 类型
                String eventTimeFields = jobConf.get(FileSystemCommonOptions.ArchiveOptions.EVENT_TIME_FIELDS);
                if (null != eventTimeFields) {
                    this.extractor = (EventTimeExtractor) Class.forName(customExtractorClassPath)
                            .getConstructor(String.class).newInstance(eventTimeFields);
                } else {
                    Integer eventTimeIndex = Preconditions.checkNotNull(jobConf.get(FileSystemCommonOptions.ArchiveOptions.EVENT_TIME_INDEX));
                    this.extractor = (EventTimeExtractor) Class.forName(customExtractorClassPath)
                            .getConstructor(int.class).newInstance(eventTimeIndex);
                }

            }

        }
    }
}
