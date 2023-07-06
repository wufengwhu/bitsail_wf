package com.hihonor.datacollector.realtime;

import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.option.CommonOptions;
import com.bytedance.bitsail.core.command.CommandArgsParser;
import com.bytedance.bitsail.core.command.CoreCommandArgs;
import com.bytedance.bitsail.core.job.UnificationJob;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

public class EmbeddedFlinkClusterTest {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedFlinkCluster.class);

    private static final long DEFAULT_JOB_ID = -1L;

    public static <T> void submitJob(BitSailConfiguration globalConfiguration) throws Exception {
        if (Objects.isNull(globalConfiguration)) {
            LOG.error("Submit failed, configuration is empty.");
            throw new IllegalStateException("Submit failed, configuration is empty");
        }
        overwriteConfiguration(globalConfiguration);
        LOG.info("Final Configuration: {}.\n", globalConfiguration.desensitizedBeautify());
        CoreCommandArgs coreCommandArgs = new CoreCommandArgs();
        coreCommandArgs.setEngineName("flink");
        UnificationJob<T> job = new UnificationJob<>(globalConfiguration, coreCommandArgs);
        job.start();
    }

    private static void overwriteConfiguration(BitSailConfiguration globalConfiguration) {
        globalConfiguration
                .set(CommonOptions.SYNC_DDL, false)
                .set(CommonOptions.DRY_RUN, true)
                .set(CommonOptions.ENABLE_DYNAMIC_LOADER, false);
    }

    public static void main(String[] args) {
        CoreCommandArgs coreCommandArgs = new CoreCommandArgs();

        CommandArgsParser.parseArguments(args, coreCommandArgs);
        try {
            Map<String, Object> objectMap = ConfigFactory.load(coreCommandArgs.getJobConfPath()).getObject("bitsail").unwrapped();
            BitSailConfiguration bitSailConfiguration = BitSailConfiguration.from(objectMap);
            submitJob(bitSailConfiguration);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}