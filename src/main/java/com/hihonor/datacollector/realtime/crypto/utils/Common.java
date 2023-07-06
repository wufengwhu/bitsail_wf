package com.hihonor.datacollector.realtime.crypto.utils;


import java.nio.charset.Charset;

public class Common extends HiAnalyticsCommon {
    public static final String pig_key = "first";
    public static final String dog_key = "second";
    public static final String duck_key = "third";
    public static final String cat_key = "salt";
    public static final String hota_workKey_key = "hotaWorkKey";
    public static final String huawei_workKey_key = "aesHuaweiWorkKey";
    public static final String honor_workKey_key = "aesHonorWorkKey";
    public static final String sdk_workKey_key = "sdkWorkKey";
    public static final String psi_workKey_key = "psiWorkKey";
    public static final String aes128_workKey_key = "aes128WorkKey";
    public static final String gcm_workKey_key = "gcmWorkKey";

    public static final String KEY_FILE = "com.hihonor.datacollector.realtime.config.commonConfig";

    public static final String LOG_TAG = "HianalyticsSDK";

    public static final String FLASH_KEY = "flashKeyTime";

    public static final String WORK_KEY = "PrivacyData";

    public static final String ANALYTICS_KEY = "analytics_key";

    public static final String PRIVACY_KEY = "Privacy_MY";

    public static final String ASSEMBLY_FLASH = "assemblyFlash";

    public static final String CHANNEL = "_channel";

    public static final String ROM_VERSION = "_rom_ver";

    public static final String EMUI_VERSION = "_emui_ver";

    public static final String DEVICE_NAME = "_model";

    public static final String OPTA = "_mcc";

    public static final String OPTB = "_mnc";

    public static final String PA_NAME = "_package_name";

    public static final String APP_VER = "_app_ver";

    public static final String LIB_VER = "_lib_ver";

    public static final String LIB_NAME = "_lib_name";

    public static final String REQUEST_KEY = "request_id";

    public static final String OAID_TRACKING = "_oaid_tracking_flag";

    public static final String HANSET_BRAND_ID = "_brand";

    public static final String ACCOUNT_BRAND_ID = "upid_brand";

    public static final String APP_BRAND_ID = "_app_brand";

    public static final String HANSET_MANUFACTURER_ID = "_manufacturer";

    public static final String HANDLER_V2DATA_FLAG = "v2cacheHandlerFlag";

    public static final String PROPERTIES = "properties";

    public static final String EVENTS_GLOBAL_PRO = "events_global_properties";

    public static final int MAX_VALUE_LENGTH = 65536;

    public static final String TYPE_ERROR = "allType";

    public static final String UNDER_LINE = "-";

    // 只用于异常数据缓存异常文件时 spKey的拼接
    public static final String LINK_CHAR = "#";

    public static final String UPLOAD_URL = "upload_url";

    public static final String UPLOAD_URL_TIME = "upload_url_time";

    public static final String WOEK_TYPE_2G = "2G";

    // 即时信息标识
    public static final int IM_EVENT_FLAG = 0;

    // 刷新本地数据加密秘钥
    public static final int FLUSH_KEY_FLAG = 1;

    public static final int INT_MAX_CACHE_SIZE = 10;

    public static final int INT_MIN_CACHE_SIZE = 5;

    public static final int INT_MAX_BACKUP_SIZE = 5;

    /**
     * 两次发送时间的间隔
     * 对于实时发送的接口onReport，该属性不起作用
     */
    public static final int AUTO_REPORT_INTERVAL = 30 * 1000;

    public static final String VIEWNAME_MODEL = "[a-zA-Z_][a-zA-Z0-9. _-]{0,255}";

    public static final String CONFIG_NAME_PUBKEY = "hianalytics_njjn";
    public static final String CONFIG_NAME_ADDRESS = "hianalytics_config";

    /**
     * event发送失败的数据（多通道）
     */
    public static final String TYPE_EVENT_CACHED_V2_1 = "cached_v2_1";

    /**
     * event数据（多通道）
     */
    public static final String TYPE_EVENT_STAT_V2_1 = "stat_v2_1";

    /**
     * 当没有扩展的字段的数据，使用此标记
     */
    public static final String NO_EX_HASH_FLAG = "noExHashFlag";

    /**
     * 备份缓存
     */
    public static final String TYPE_EVENT_BACKUP = "backup_event";

    /**
     * V2版本缓存
     */
    public static final String HIANALYTICS_STATE_V2 = "stat_v2";

    public static final String HIANALYTICS_CACHE_V2 = "cached_v2";

    public static final int SAVE_KEY_TIME = 12 * HOUR_MILLISECONDS;

    /**
     * Number of events to send in every uploading action
     */
    public static final int EVENT_NUMBER_PER_UPLOAD = 500;

    /**
     * When the stat sp file reach this size limit, the events should be reported automatically
     */
    public static final int STATE_FILE_SIZE_LIMIT_FOR_REPORT = 10;

    /**
     * When the stat sp file reach this size limit, new event will be discarded.
     */
    public static final int MAX_FILE_SIZE_LIMIT_FOR_DISCARD = 5 * MB;

    // 失败缓存条数的上限
    public static final int MAX_DATA_SIZE_FOR_CACHE = 6000;

    // 预装全球化域名允许的最大个数
    public static final int MAX_PRE_BASE_URL_SIZE = 13;

    // 配置文件 通过此字段获取地址
    public static final String BASE_URI = "base_uri";
    public static final String UTF_8 = "UTF-8";
    public static final Charset UTF8 = Charset.forName("UTF-8");
    /**
     * The Constant DEFAULT_ENCODING.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String PRELOAD_URL_TAG = "preload_url_tag";
    public static final String PRE_GLOBAL_URL_TAG = "pre_report_url_tag";
    public static final String PRE_GLOBAL_BACKUP_URL_TAG = "pre_report_backup_url_tag";

    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_ID_TYPE_CD = "device_id_type_cd";

    public static String getAesKey2() {
        return "b0df1dcca5fda619b6f7f459f2ff8d70ddb7b601592fe29fcae58c028f319b3b12495e67aa5390942a997";
    }

    /**
     * 接口类型
     */
    public abstract static class InterfaceType {
        /**
         * HiAnalytics SDK
         */
        public static final String HMSHI = "hmshi";

        /**
         * jssdk
         */
        public static final String JSSDK = "jssdk";
    }

    /**
     * 生命周期
     */
    public abstract static class LifeEvent {
        /**
         * 生命周期OnPauss
         */
        public static final String EVENT_PAUSE = "$AppOnPause";

        /**
         * 生命周期OnResume
         */
        public static final String EVENT_RESUME = "$AppOnResume";

        /**
         * 生命周期OnPauss
         */
        public static final String TYPE_ONPAUSE = "OnPause";

        /**
         * 生命周期OnResume
         */
        public static final String TYPE_ONRESUME = "OnResume";
    }

    /**
     * 数据上报规则
     * 实时(rt)、准实时(qrt)、批(batch)
     */
    public abstract static class ReportRule {
        /**
         * 实时(rt)
         */
        public static final String RT = "rt";

        /**
         * 准实时(qrt)
         */
        public static final String QRT = "qrt";

        /**
         * 批(batch)
         */
        public static final String BATCH = "batch";
    }
}
