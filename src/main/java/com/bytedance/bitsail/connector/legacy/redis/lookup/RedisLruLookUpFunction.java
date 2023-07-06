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

package com.bytedance.bitsail.connector.legacy.redis.lookup;


import com.bytedance.bitsail.connector.legacy.converter.AbstractRowConverter;
import com.bytedance.bitsail.connector.legacy.enums.ECacheContentType;
import com.bytedance.bitsail.connector.legacy.lookup.AbstractLruLookUpFunction;
import com.bytedance.bitsail.connector.legacy.lookup.cache.CacheMissVal;
import com.bytedance.bitsail.connector.legacy.lookup.cache.CacheObj;
import com.bytedance.bitsail.connector.legacy.lookup.config.LookupConfig;
import com.bytedance.bitsail.connector.legacy.redis.config.RedisConfig;
import com.bytedance.bitsail.connector.legacy.redis.connection.RedisAsyncClient;
import com.bytedance.bitsail.connector.legacy.redis.enums.RedisDataType;
import com.google.common.collect.Lists;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisHashAsyncCommands;
import io.lettuce.core.api.async.RedisKeyAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;

@Slf4j
public class RedisLruLookUpFunction extends AbstractLruLookUpFunction {

    private static final long serialVersionUID = -7190171034606939751L;

    private transient RedisAsyncClient redisAsyncClient;
    private RedisKeyAsyncCommands<String, String> redisKeyAsyncCommands;
    private final RedisConfig redisConfig;

    public RedisLruLookUpFunction(
            RedisConfig redisConfig, LookupConfig lookupConfig, AbstractRowConverter rowConverter) {
        super(lookupConfig, rowConverter);
        this.redisConfig = redisConfig;
        this.lookupConfig = lookupConfig;
    }

    @Override
    public void open(Configuration context) throws Exception {
        super.open(context);
        redisAsyncClient = new RedisAsyncClient(redisConfig);
        redisKeyAsyncCommands = redisAsyncClient.getRedisKeyAsyncCommands();
    }

    @Override
    public void handleAsyncInvoke(ResultFuture<Row> resultFuture, String cacheKey, Row keyRow) {
        RedisFuture<Map<String, String>> redisMapFuture = null;
        RedisFuture<Long> redisExistsFuture = null;
        if (redisConfig.getType().equals(RedisDataType.HASH)) {
            redisMapFuture = ((RedisHashAsyncCommands) redisKeyAsyncCommands).hgetall(cacheKey);
            redisMapFuture.thenAccept(
                    resultValues -> {
                        if (MapUtils.isNotEmpty(resultValues)) {
                            List<Map<String, String>> cacheContent = Lists.newArrayList();
                            List<Row> rowList = Lists.newArrayList();
                            try {
                                Row rowData = rowConverter.toInternalLookup(keyRow, resultValues);
                                if (openCache()) {
                                    cacheContent.add(resultValues);
                                }
                                rowList.add(rowData);
                            } catch (Exception e) {
                                log.error(
                                        "error:{} \n cacheKey:{} \n data:{}",
                                        e.getMessage(),
                                        cacheKey,
                                        resultValues);
                            }
                            dealCacheData(
                                    cacheKey,
                                    CacheObj.buildCacheObj(ECacheContentType.MultiLine, cacheContent));
                            resultFuture.complete(rowList);
                        } else {
                            dealMissKey(resultFuture);
                            dealCacheData(cacheKey, CacheMissVal.getMissKeyObj());
                        }
                    });
        }
        if (redisConfig.getType().equals(RedisDataType.STRING)) {
//            redisStringFuture = ((RedisStringAsyncCommands) redisKeyAsyncCommands).get(cacheKey);
            // 判断已录入的appid 上报的埋点是否是上线状态
            redisExistsFuture = (redisKeyAsyncCommands).exists(cacheKey);
            redisExistsFuture.thenAccept(
                    resultValues -> {
                        List<Long> cacheContent = Lists.newArrayList();
                        List<Row> rowList = Lists.newArrayList();
                        try {
                            Row rowData = rowConverter.toInternalLookup(keyRow, resultValues);
                            if (openCache()) {
                                cacheContent.add(resultValues);
                            }
                            rowList.add(rowData);
                        } catch (Exception e) {
                            log.error(
                                    "error:{} \n cacheKey:{} \n data:{}",
                                    e.getMessage(),
                                    cacheKey,
                                    resultValues);
                        }
                        dealCacheData(
                                cacheKey,
                                CacheObj.buildCacheObj(ECacheContentType.SingleLine, cacheContent));
                        resultFuture.complete(rowList);
                    });
        }
    }

    @Override
    public String buildCacheKey(Object... keys) {
        // row =()
        // keyPrefix_appid_event
        Row row = (Row) keys[0];
        List<Integer> keyIndexes = redisConfig.getKeyIndexes();
        Object[] redisKeys = keyIndexes.stream().map(index -> row.getField(index)).toArray();
        return redisConfig.getKeyPrefix() + super.buildCacheKey(redisKeys);
    }

    @Override
    public void close() {
        redisAsyncClient.close();
    }
}
