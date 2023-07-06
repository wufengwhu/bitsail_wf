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

package com.bytedance.bitsail.connector.legacy.lookup.config;

import com.bytedance.bitsail.common.configuration.BitSailConfiguration;

import static com.bytedance.bitsail.connector.legacy.lookup.options.LookupOptions.*;


public class LookupConfigFactory {

    public static LookupConfig createLookupConfig(BitSailConfiguration lookupConfiguration) {
        LookupConfig lookupConfig = LookupConfig.build()
                .setPeriod(lookupConfiguration.get(LOOKUP_CACHE_PERIOD))
                .setCacheSize(lookupConfiguration.get(LOOKUP_CACHE_MAX_ROWS))
                .setCacheTtl(lookupConfiguration.get(LOOKUP_CACHE_TTL))
                .setCache(lookupConfiguration.get(LOOKUP_CACHE_TYPE))
                .setMaxRetryTimes(lookupConfiguration.get(LOOKUP_MAX_RETRIES))
                .setErrorLimit(lookupConfiguration.get(LOOKUP_ERROR_LIMIT))
                .setFetchSize(lookupConfiguration.get(LOOKUP_FETCH_SIZE))
                .setAsyncTimeout(lookupConfiguration.get(LOOKUP_ASYNC_TIMEOUT));
        return lookupConfig;
    }


}
