/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedance.bitsail.connector.legacy.lookup.options;


import com.alibaba.fastjson.TypeReference;
import com.bytedance.bitsail.common.option.ConfigOption;
import com.bytedance.bitsail.connector.legacy.enums.CacheType;

import java.util.List;
import java.util.Map;

import static com.bytedance.bitsail.common.option.ConfigOptions.key;
import static com.bytedance.bitsail.common.option.LookUpOptions.LOOKUP_PREFIX;


public class LookupOptions {
    // look up config options
    public static final ConfigOption<Long> LOOKUP_CACHE_PERIOD =
            key(LOOKUP_PREFIX + "cache-period")
                    .defaultValue(3600 * 1000L);

    /**
     * the max number of rows of lookup cache, over this value, the oldest rows will "
     * + "be eliminated. \"cache.max-rows\" and \"cache.ttl\" options must all be specified if any of them is "
     * + "specified. Cache is not enabled as default.
     */
    public static final ConfigOption<Long> LOOKUP_CACHE_MAX_ROWS =
            key(LOOKUP_PREFIX + "cache-max-rows")
                    .defaultValue(1000L);


    public static final ConfigOption<Long> LOOKUP_CACHE_TTL =
            key(LOOKUP_PREFIX + "cache-ttl")
                    .defaultValue(60 * 1000L);


    public static final ConfigOption<String> LOOKUP_CACHE_TYPE =
            key(LOOKUP_PREFIX + "cache-type")
                    .defaultValue(CacheType.LRU.name());


    public static final ConfigOption<Integer> LOOKUP_MAX_RETRIES =
            key(LOOKUP_PREFIX + "max-retries")
                    .defaultValue(3);


    public static final ConfigOption<Long> LOOKUP_ERROR_LIMIT =
            key(LOOKUP_PREFIX + "error-limit")
                    .defaultValue(Long.MAX_VALUE);


    public static final ConfigOption<Integer> LOOKUP_FETCH_SIZE =
            key(LOOKUP_PREFIX + "fetch-size")
                    .defaultValue(1000);


    public static final ConfigOption<Integer> LOOKUP_ASYNC_TIMEOUT =
            key(LOOKUP_PREFIX + "async-timeout")
                    .defaultValue(10000);


    public static final ConfigOption<Map<String, String>> CONNECTOR_PROPERTIES =
            key(LOOKUP_PREFIX + "connector")
                    .onlyReference(new TypeReference<Map<String, String>>() {
                    });

    public static final ConfigOption<List<Integer>> KEY_INDEX_IN_ROW =
            key(LOOKUP_PREFIX + "key-index-in-row")
                    .onlyReference(new TypeReference<List<Integer>>() {
                    });

    public static final ConfigOption<Integer> VALUE_INDEX_IN_ROW =
            key(LOOKUP_PREFIX + "value-index-in-row")
                    .noDefaultValue(Integer.class);

    public static final ConfigOption<String> REDIS_KEY_PREFIX =
            key(LOOKUP_PREFIX + "redis-key-prefix")
                    .defaultValue("cloud_");
}
