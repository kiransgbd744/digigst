/**
 * 
 */
package com.ey.advisory.common;

public class AppStartUpConstants {

	public static final String ASYNC_CORE_POOL_SIZE = "CorePoolSize";
	public static final String ASYNC_MAX_POOL_SIZE = "MaxPoolSize";
	public static final String KAFKA_PURCHASE_TOPIC_NAME = "purchaseTopicName";
	public static final String BOOTSTRAP_SERVERS_CONFIG = "bootStrapServer";
	public static final String KAFKA_SALES_TOPIC_NAME = "salesTopicName";
	public static final String IS_PROXY_REQ_ACCESSBLOB = 
				"isProxyReqTOAccessBolb";
	
	public static final String ASYNC_IO_POOL_SIZE = "IOPoolSize";
	public static final int DEFAULT_IO_POOL_SIZE = 30;
	public static final String ASYNC_LOGGER_POOL_SIZE = "LoggerPoolSize";
	public static final int DEFAULT_LOGGER_POOL_SIZE = 5;
	
	public static final String ASYNC_URL_POOL_SIZE = "URLPoolSize";
	public static final int DEFAULT_URL_POOL_SIZE = 60;
}
