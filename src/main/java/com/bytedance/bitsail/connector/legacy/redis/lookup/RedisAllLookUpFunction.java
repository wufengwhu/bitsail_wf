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
import com.bytedance.bitsail.connector.legacy.lookup.AbstractAllLookUpFunction;
import com.bytedance.bitsail.connector.legacy.lookup.config.LookupConfig;
import com.bytedance.bitsail.connector.legacy.redis.config.RedisConfig;
import com.bytedance.bitsail.connector.legacy.redis.connection.RedisSyncClient;
import com.bytedance.bitsail.connector.legacy.redis.util.RedisUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import redis.clients.jedis.commands.JedisCommands;

import java.io.IOException;
import java.util.*;

@Slf4j
public class RedisAllLookUpFunction extends AbstractAllLookUpFunction {

    private static final long serialVersionUID = -1319108555631638546L;

    private final RedisConfig redisConfig;
    private transient RedisSyncClient redisSyncClient;

    public RedisAllLookUpFunction(
            RedisConfig redisConfig,
            LookupConfig lookupConfig,
            String[] fieldNames,
            String[] keyNames,
            AbstractRowConverter rowConverter) {
        super(fieldNames, keyNames, lookupConfig, rowConverter);
        this.redisConfig = redisConfig;
    }

    @Override
    public Collection<Row> lookup(Row keyRow) throws IOException {
        List<String> dataList = Lists.newLinkedList();
        List<Row> hitRowData = Lists.newArrayList();
        for (int i = 0; i < keyRow.getArity(); i++) {
            dataList.add(String.valueOf(keyRow.getField(i)));
        }
        String cacheKey = redisConfig.getTableName() + "_" + String.join("_", dataList);
        List<Map<String, Object>> cacheList =
                ((Map<String, List<Map<String, Object>>>) (cacheRef.get())).get(cacheKey);
        // 有数据才往下发，(左/内)连接flink会做相应的处理
        if (!CollectionUtils.isEmpty(cacheList)) {
            cacheList.forEach(one -> hitRowData.add(fillData(one)));
        }

        return hitRowData;
    }

    @Override
    protected void loadData(Object cacheRef) {
        Map<String, List<Map<String, Object>>> tmpCache =
                (Map<String, List<Map<String, Object>>>) cacheRef;
        if (redisSyncClient == null) {
            redisSyncClient = new RedisSyncClient(redisConfig);
        }
        JedisCommands jedis = redisSyncClient.getJedis();
        StringBuilder keyPattern = new StringBuilder(redisConfig.getTableName());
        for (int i = 0; i < keyNames.length; i++) {
            keyPattern.append("_").append("*");
        }

        Set<String> keys =
                RedisUtil.getRedisKeys(
                        redisConfig.getRedisConnectType(), jedis, keyPattern.toString());
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        try {
            for (String key : keys) {
                Map<String, Object> oneRow = new HashMap<>();
                Map<String, String> hgetAll = jedis.hgetAll(key);
                // 防止一条数据有问题，后面数据无法加载
                try {
                    Row rowData = rowConverter.toInternal(hgetAll);
                    for (int i = 0; i < fieldsName.length; i++) {
                        Object object = rowData.getField(i);
                        oneRow.put(fieldsName[i].trim(), object);
                    }
                    tmpCache.computeIfAbsent(key, k -> Lists.newArrayList()).add(oneRow);
                } catch (Exception e) {
                    log.error("error:{} \n  data:{}", e.getMessage(), hgetAll);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            redisSyncClient.closeJedis(jedis);
        }
    }

    @Override
    public void flatMap(Row row, Collector<Row> collector) throws Exception {
        Collection<Row> outputRows = lookup(row);

        outputRows.forEach(one -> collector.collect(one));
    }
}
