package com.bytedance.bitsail.connector.legacy.jdbc.constants;

public class JdbcConstants {

    public static final String CONNECTOR_SOURCE_CDC_SERVER_ID = "cdc.server-id";

    public static final String SCAN_STARTUP_MODE_VALUE_INITIAL = "initial";
    public static final String SCAN_STARTUP_MODE_VALUE_EARLIEST = "earliest-offset";
    public static final String SCAN_STARTUP_MODE_VALUE_LATEST = "latest-offset";
    public static final String SCAN_STARTUP_MODE_VALUE_SPECIFIC_OFFSET = "specific-offset";
    public static final String SCAN_STARTUP_MODE_VALUE_TIMESTAMP = "timestamp";

    public static final String CONNECTOR_CDC_STARTUP_MODE = "connector.cdc.startup-mode";
}
