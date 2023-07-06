package com.hihonor.datacollector.realtime.crypto.utils;

public class HiAnalyticsCommon {
    public static final String LOG_TAG = "HianalyticsSDK";

    public static final String SDK_VERSION = "2.1.4.322";

    public static final String HA_NAME = "hianalytics";

    public static final String PREFIX = "hianalytics_";

    public static final String UUID = "uuid";

    public static final String APPID_MODEL = "[a-zA-Z0-9_][a-zA-Z0-9. _-]{0,255}";

    public static final String TAG_MODEL = "[a-zA-Z0-9][a-zA-Z0-9_]{0,255}";

    public static final String SP_APPVER = "app_ver";

    public static final String SYSTEMCXX_DEBUG = "C00";

    /**
     * When some abnormal information cannot be printed, print this information.
     */
    public static final String EXCEPTION_STR = "An exception occurred";

    /**
     * 时间基数
     */
    public static final int MINUTE_MILLISECONDS = 60 * 1000;

    public static final int HOUR_MILLISECONDS = 60 * MINUTE_MILLISECONDS;

    public static final long DAY = 24 * HOUR_MILLISECONDS;

    public static final int DEFAULT_EXPIRE_TIME = 7 * 24 * HOUR_MILLISECONDS;

    public static final int INT_MAX_DATE = 7;

    public static final int INT_MIN_DATE = 2;

    // event 扩展字段中map允许的最大值
    public static final int EVENTEX_MAP_SIZE = 10;

    // map key允许的最大长度
    public static final long EVENTEX_MAP_KEY_LIMIT_LENGTH = 128;

    // map value 允许的最大长度
    public static final long EVENTEX_MAP_VALUE_LIMIT_LENGTH = 512;

    // map key需要以此字段开头
    public static final String EVENTEX_MAP_KEYWORK = "x_";

    // 网络请求头 key的格式要求
    public static final String X_HASDK = "x-hasdk";

    /**
     * http_header 最大个数
     */
    public static final int MAX_HTTP_HEADER_SIZE = 50;

    /**
     * size基数
     */
    public static final int KB = 1024;

    public static final int MB = 1024 * KB;

    /**
     * 最大业务注册数
     */
    public static final int MAX_USER_TAGS = 50;

    public static final int MAX_HTTPHEADER_VALUE_LENGTH = 1024;

    /**
     * 一些参数长度上限
     */
    public static final int CONSTANT_4096 = 4096;

    public static final int MAX_STRING_LENGTH = 256;

    public static final int MAX_LIMIT_SIZE = 500;

    public static final int MAP_LENGTH = 200 * KB;

    public static final String TYPE_GLOBAL_INFO_V2 = "global_v2";

    public static final String TYPE_NC_COMMON = "common_nc";
    public static final String EX_HEADER_CONSTANT = "headerEx";
    public static final String EX_COMMON = "commonEx";
    public static final String CONSTANTS = "constants";

    public static final String UNDERLINE_CONSTANTS = "_constants";

    public static final String URL_PALCEHOLDER = "{url}";

    public static final String DEFAULT_TAG = "_default_config_tag";

    public static final String ABTEST_TAG = "ABTesting";

    public static final String INSTANCE_TAG_EX = "_instance_ex_tag";

    public static final long EOF = -1L;

    public static final String EMPTY = "";

    public static final String EMUI_VER = "ro.build.version.emui";

    /**
     * 穿戴网络类型
     */
    public static final int TYPE_PROXY = 16;

    /**
     * 日志文件最小可设置存储数量
     */
    public static final int LOG_FILE_MIN_SIZE = 3;

    /**
     * 上传失败的日志文件最小可设置存储数量
     */
    public static final int FAILED_LOG_FILE_MIN_SIZE = 5;

    /**
     * 数据类型
     * 运营（oper)、运维（maint）、预装上报（preins）、差分隐私（diffPrivacy）
     */
    public abstract static class DataType {
        /**
         * 运营（oper)
         */
        public static final String STRING_OPER = "oper";

        /**
         * 运维（maint）
         */
        public static final String STRING_MAINT = "maint";

        /**
         * 预装上报（preins）
         */
        public static final String STRING_PREINS = "preins";

        /**
         * 差分隐私通道（diffPrivacy）
         */
        public static final String STRING_DIFFPRIVACY = "diffprivacy";

        /**
         * 运营（oper)
         */
        public static final int INT_OPER = 0;

        /**
         * 运维（maint）
         */
        public static final int INT_MAINT = 1;

        /**
         * 预装上报（preins）
         */
        public static final int INT_PREINS = 2;

        /**
         * 差分隐私通道（diffPrivacy）
         */
        public static final int INT_DIFFPRIVACY = 3;
    }

    public static class StreamFlag {
        public static final int FILE_OUTPUT_STREAM = 0;

        public static final int FILE_INPUT_STREAM = 1;

        public static final int BUFFERED_INPUT_STREAM = 2;

        public static final int BUFFERED_OUTPUT_STREAM = 3;

        public static final int ZIP_INPUT_STREAM = 4;

        public static final int ZIP_OUTPUT_STREAM = 5;

        public static final int OUTPUTSTREAM = 6;

        public static final int INPUT_STREAM = 7;

        public static final int BYTE_ARRAY_OUTPUT_STREAM = 8;

        public static final int BUFFERED_WRITER = 9;

        public static final int OUTPUT_STREAM_WRITER = 10;
    }

    public abstract static class AppInfoKeys {
        public static final String LOG_VERSION = "LogVersion";

        public static final String LOG_SUBVERSION = "LogSubversion";

        public static final String PRODUCT_VERSION = "ProductVersion";

        public static final String HAPPEN_TIME = "HappenTime";

        public static final String EVENT_ID = "Eventid";

        public static final String APP_ID = "App-Id";

        public static final String APP_VER = "App-Ver";

        public static final String SDK_NAME = "Sdk-Name";

        public static final String SDK_VER = "Sdk-Ver";

        public static final String DEVICE_TYPE = "Device-Type";

        public static final String PACKAGE_NAME = "Package-Name";

        public static final String REQUEST_ID = "Request-Id";
    }

    public static class NetworkType {
        public static final String SECOND_GENERATION = "2G";
        public static final String THIRD_GENERATION = "3G";
        public static final String FOURTH_GENERATION = "4G";
        public static final String WIFI = "WIFI";
        public static final String NETWORK_TYPE_UNKNOWN = "UNKNOWN";
    }
}
