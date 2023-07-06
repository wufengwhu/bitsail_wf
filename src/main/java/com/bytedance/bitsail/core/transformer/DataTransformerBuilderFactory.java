package com.bytedance.bitsail.core.transformer;

import com.bytedance.bitsail.base.connector.transformer.DataTransformDAGBuilder;
import com.bytedance.bitsail.base.execution.Mode;
import com.bytedance.bitsail.base.packages.PackageManager;
import com.bytedance.bitsail.common.BitSailException;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.exception.CommonErrorCode;
import com.bytedance.bitsail.common.option.ConfigOption;
import com.bytedance.bitsail.common.option.ConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class DataTransformerBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataTransformerBuilderFactory.class);

    public static ConfigOption<String> TRANSFORMER_CLASS = ConfigOptions.key("job.reader.transformer_class").noDefaultValue(String.class);

    public static <T> DataTransformDAGBuilder getDataTransformerDAGBuilder(Mode mode,
                                                                           BitSailConfiguration globalConfiguration,
                                                                           PackageManager packageManager) throws Exception {
        Class<T> transformerClass = DataTransformerBuilderFactory.<T>geTransformerClass(globalConfiguration, packageManager);
        if (DataTransformDAGBuilder.class.isAssignableFrom(transformerClass)) {
            return (DataTransformDAGBuilder) transformerClass.getConstructor().newInstance();
        }

        throw BitSailException.asBitSailException(CommonErrorCode.CONFIG_ERROR,
                "Transformer " + transformerClass.getName() + "class is not supported ");
    }

    public static <T> List<DataTransformDAGBuilder> getTransformerDAGBuilderList(Mode mode,
                                                                                 List<BitSailConfiguration> readerConfigurations,
                                                                                 PackageManager packageManager) {
        return readerConfigurations.stream()
                .map(readerConf -> {
                    try {
                        return getDataTransformerDAGBuilder(mode, readerConf, packageManager);
                    } catch (Exception e) {
                        LOG.error("failed to create reader DAG builder");
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> geTransformerClass(BitSailConfiguration globalConfiguration,
                                                   PackageManager packageManager) {
        String transformerClassName = globalConfiguration.get(TRANSFORMER_CLASS);
        LOG.info("Transformer class name is {}", transformerClassName);
        return (Class<T>) packageManager.loadDynamicLibrary(transformerClassName, classLoader -> {
            try {
                return classLoader.loadClass(transformerClassName);
            } catch (Exception e) {
                throw BitSailException.asBitSailException(CommonErrorCode.INTERNAL_ERROR, e);
            }
        });
    }
}
