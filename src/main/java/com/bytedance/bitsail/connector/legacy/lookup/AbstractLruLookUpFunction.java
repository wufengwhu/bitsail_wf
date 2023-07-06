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

package com.bytedance.bitsail.connector.legacy.lookup;

import com.bytedance.bitsail.connector.legacy.converter.AbstractRowConverter;
import com.bytedance.bitsail.connector.legacy.enums.CacheType;
import com.bytedance.bitsail.connector.legacy.enums.ECacheContentType;
import com.bytedance.bitsail.connector.legacy.lookup.cache.AbstractSideCache;
import com.bytedance.bitsail.connector.legacy.lookup.cache.CacheObj;
import com.bytedance.bitsail.connector.legacy.lookup.cache.LRUCache;
import com.bytedance.bitsail.connector.legacy.lookup.config.LookupConfig;
import com.bytedance.bitsail.core.Metrics;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.runtime.execution.SuppressRestartsException;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractLruLookUpFunction extends RichAsyncFunction<Row, Row> {

    private static final long serialVersionUID = 8054577160378024212L;
    /**
     * 指标
     */
    protected transient Counter parseErrorRecords;
    /**
     * 缓存
     */
    protected AbstractSideCache sideCache;
    /**
     * 维表配置
     */
    protected LookupConfig lookupConfig;
    /**
     * 数据类型转换器
     */
    protected final AbstractRowConverter rowConverter;

    private static final int TIMEOUT_LOG_FLUSH_NUM = 10;
    private int timeOutNum = 0;

    public AbstractLruLookUpFunction(LookupConfig lookupConfig, AbstractRowConverter rowConverter) {
        this.lookupConfig = lookupConfig;
        this.rowConverter = rowConverter;
    }

    @Override
    public void open(Configuration context) throws Exception {
        super.open(context);

        initCache();
        initMetric(getRuntimeContext());

        log.info("async dim table lookupOptions info: {} ", lookupConfig.toString());
    }

    /**
     * 初始化缓存
     */
    protected void initCache() {
        if (CacheType.NONE.name().equalsIgnoreCase(lookupConfig.getCache())) {
            return;
        }

        if (CacheType.LRU.name().equalsIgnoreCase(lookupConfig.getCache())) {
            sideCache = new LRUCache(lookupConfig.getCacheSize(), lookupConfig.getCacheTtl());
        } else {
            throw new RuntimeException(
                    "not support side cache with type:" + lookupConfig.getCache());
        }

        sideCache.initCache();
    }

    /**
     * 初始化Metric
     *
     * @param context 上下文
     */
    protected void initMetric(RuntimeContext context) {

        parseErrorRecords = context.getMetricGroup().counter(Metrics.NUM_SIDE_PARSE_ERROR_RECORDS);
    }

    /**
     * 通过key得到缓存数据
     *
     * @param key
     * @return
     */
    protected CacheObj getFromCache(String key) {
        return sideCache.getFromCache(key);
    }

    /**
     * 数据放入缓存
     *
     * @param key
     * @param value
     */
    protected void putCache(String key, CacheObj value) {
        sideCache.putCache(key, value);
    }

    /**
     * 是否开启缓存
     *
     * @return
     */
    protected boolean openCache() {
        return sideCache != null;
    }

    /**
     * 如果缓存获取不到，直接返回空即可，无需判别左/内连接
     *
     * @param resultFuture
     */
    public void dealMissKey(ResultFuture<Row> resultFuture) {
        try {
            resultFuture.complete(Collections.emptyList());
        } catch (Exception e) {
            dealFillDataError(resultFuture, e);
        }
    }

    /**
     * 判断是否需要放入缓存
     *
     * @param key
     * @param missKeyObj
     */
    protected void dealCacheData(String key, CacheObj missKeyObj) {
        if (openCache()) {
            putCache(key, missKeyObj);
        }
    }


    @Override
    public void timeout(Row input, ResultFuture<Row> resultFuture) {
    }


    /**
     * 异步查询数据
     *
     * @param keyRow 关联数据
     */
    @Override
    public void asyncInvoke(Row keyRow, ResultFuture<Row> resultFuture) {
        try {
            String cacheKey = buildCacheKey(keyRow);
            // 缓存判断
            if (isUseCache(cacheKey)) {
                invokeWithCache(cacheKey, resultFuture, keyRow);
            }
            handleAsyncInvoke(resultFuture, cacheKey, keyRow);
        } catch (Exception e) {
            // todo 优化
            log.error(e.getMessage());
        }
    }

    /**
     * 判断缓存是否存在
     *
     * @param cacheKey 缓存健
     * @return
     */
    protected boolean isUseCache(String cacheKey) {
        return openCache() && getFromCache(cacheKey) != null;
    }

    /**
     * 从缓存中获取数据
     *
     * @param cacheKey     缓存健
     * @param resultFuture
     */
    private void invokeWithCache(String cacheKey, ResultFuture<Row> resultFuture, Row keyRow) {
        if (openCache()) {
            CacheObj val = getFromCache(cacheKey);
            if (val != null) {
                if (ECacheContentType.MissVal == val.getType()) {
                    dealMissKey(resultFuture);
                    return;
                } else if (ECacheContentType.SingleLine == val.getType()) {
                    try {
                        Row row = rowConverter.toInternalLookup(keyRow, val.getContent());
                        resultFuture.complete(Collections.singleton(row));
                    } catch (Exception e) {
                        dealFillDataError(resultFuture, e);
                    }
                } else if (ECacheContentType.MultiLine == val.getType()) {
                    try {
                        List<Row> rowList = Lists.newArrayList();
                        for (Object one : (List) val.getContent()) {
                            Row row = rowConverter.toInternalLookup(keyRow, one);
                            rowList.add(row);
                        }
                        resultFuture.complete(rowList);
                    } catch (Exception e) {
                        dealFillDataError(resultFuture, e);
                    }
                } else {
                    resultFuture.completeExceptionally(
                            new RuntimeException("not support cache obj type " + val.getType()));
                }
            }
        }
    }

    /**
     * 请求数据库获取数据
     *
     * @param cacheKey     关联字段数据
     * @param keyRow
     * @param resultFuture
     * @throws Exception
     */
    public abstract void handleAsyncInvoke(
            ResultFuture<Row> resultFuture, String cacheKey, Row keyRow) throws Exception;

    /**
     * 构建缓存key值
     *
     * @param keys
     * @return
     */
    public String buildCacheKey(Object... keys) {
        return Arrays.stream(keys).map(String::valueOf).collect(Collectors.joining("_"));
    }

    /**
     * 发送异常
     *
     * @param resultFuture
     * @param e
     */
    protected void dealFillDataError(ResultFuture<Row> resultFuture, Throwable e) {
        parseErrorRecords.inc();
        if (parseErrorRecords.getCount() > lookupConfig.getErrorLimit()) {
            log.error("dealFillDataError", e);
            resultFuture.completeExceptionally(new SuppressRestartsException(e));
        } else {
            dealMissKey(resultFuture);
        }
    }
}
