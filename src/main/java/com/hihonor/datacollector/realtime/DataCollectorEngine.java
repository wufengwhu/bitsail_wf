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

package com.hihonor.datacollector.realtime;

import com.bytedance.bitsail.base.execution.Mode;
import com.bytedance.bitsail.base.statistics.VMInfo;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.configuration.ConfigParser;
import com.bytedance.bitsail.common.option.CommonOptions;
import com.bytedance.bitsail.core.command.CommandArgsParser;
import com.bytedance.bitsail.core.command.CoreCommandArgs;
import com.bytedance.bitsail.core.job.UnificationJob;
import com.bytedance.bitsail.core.util.ExceptionTracker;
import com.hihonor.datacollector.realtime.utils.JobConfUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

public class DataCollectorEngine {
    private static final Logger LOG = LoggerFactory.getLogger(DataCollectorEngine.class);
    private final Mode mode;
    @Getter
    private static BitSailConfiguration bitSailConfiguration = null;
    private final CoreCommandArgs coreCommandArgs;

    public DataCollectorEngine(String[] args) {
        coreCommandArgs = new CoreCommandArgs();
        CommandArgsParser.parseArguments(args, coreCommandArgs);
        if (StringUtils.isNotEmpty(coreCommandArgs.getJobConfPath())) {
              Map<String, Object> objectMap = ConfigFactory.load(coreCommandArgs.getJobConfPath()).getObject("bitsail").unwrapped();
//            bitSailConfiguration = ConfigParser.fromRawConfPath(coreCommandArgs.getJobConfPath());
            System.out.println(String.format("BitSail job conf path : %s", coreCommandArgs.getJobConfPath()));
//            try {
//                bitSailConfiguration = JobConfUtils.fromClasspath(coreCommandArgs.getJobConfPath());
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            bitSailConfiguration = BitSailConfiguration.from(objectMap);
        } else {
            bitSailConfiguration = BitSailConfiguration.from(
                    new String(Base64.getDecoder().decode(coreCommandArgs.getJobConfBase64())));
        }
        LOG.info("BitSail configuration: {}", bitSailConfiguration.desensitizedBeautify());
        mode = Mode.getJobRunMode(bitSailConfiguration.get(CommonOptions.JOB_TYPE));
    }

    public static void main(String[] args) throws Throwable {
        DataCollectorEngine engine = new DataCollectorEngine(args);
        engine.start();
    }

    public void start() throws Throwable {
        VMInfo vmInfo = VMInfo.getVmInfo();
        if (null != vmInfo) {
            LOG.info(vmInfo.toString());
        }
        try {
            run();
        } catch (Throwable e) {
            LOG.error("\n\nThe cause of the job failure maybe due to:\n" + ExceptionTracker.trace(e));
            exitWhenException(e);
        }
    }

    private void exitWhenException(Throwable e) throws Throwable {
        if (Mode.BATCH.equals(mode)) {
            System.exit(1);
        }
        throw e;
    }

    private <T> void run() throws Exception {
        UnificationJob<T> job = new UnificationJob<>(bitSailConfiguration, coreCommandArgs);
        try {
            job.start();
        } finally {
            if (bitSailConfiguration.fieldExists(CommonOptions.SLEEP_TIME)) {
                Thread.sleep(bitSailConfiguration.get(CommonOptions.SLEEP_TIME));
            }
        }
    }
}
