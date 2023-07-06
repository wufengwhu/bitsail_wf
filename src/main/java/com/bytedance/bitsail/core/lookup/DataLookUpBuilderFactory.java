package com.bytedance.bitsail.core.lookup;

import com.bytedance.bitsail.base.connector.lookup.DataLookUpDAGBuilder;
import com.bytedance.bitsail.base.execution.Mode;
import com.bytedance.bitsail.base.packages.PackageManager;
import com.bytedance.bitsail.common.BitSailException;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.exception.CommonErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bytedance.bitsail.common.option.LookUpOptions.LOOKUP_CLASS;

public class DataLookUpBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataLookUpBuilderFactory.class);

    public static <T> DataLookUpDAGBuilder getDataLookUpDAGBuilder(Mode mode,
                                                                   BitSailConfiguration lookupConfiguration,
                                                                   PackageManager packageManager) throws Exception {
        Class<T> lookupClass = DataLookUpBuilderFactory.getLookUpClass(lookupConfiguration, packageManager);
        if (DataLookUpDAGBuilder.class.isAssignableFrom(lookupClass)) {
            return (DataLookUpDAGBuilder) lookupClass.getConstructor().newInstance();
        }

        throw BitSailException.asBitSailException(CommonErrorCode.CONFIG_ERROR,
                "Lookup " + lookupClass.getName() + "class is not supported ");
    }


    @SuppressWarnings("unchecked")
    private static <T> Class<T> getLookUpClass(BitSailConfiguration lookupConfiguration,
                                               PackageManager packageManager) {
        String lookupClassName = lookupConfiguration.get(LOOKUP_CLASS);
        LOG.info("lookup class name is {}", lookupClassName);
        return (Class<T>) packageManager.loadDynamicLibrary(lookupClassName, classLoader -> {
            try {
                return classLoader.loadClass(lookupClassName);
            } catch (Exception e) {
                throw BitSailException.asBitSailException(CommonErrorCode.INTERNAL_ERROR, e);
            }
        });
    }
}
