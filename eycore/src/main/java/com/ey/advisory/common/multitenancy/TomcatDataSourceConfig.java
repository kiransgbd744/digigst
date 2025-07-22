package com.ey.advisory.common.multitenancy;

import org.springframework.stereotype.Component;

@Component
public final class TomcatDataSourceConfig {

	/**
	 * The DB URL.
	 * 
	 */
	private String url;
	
	/**
	 *  The fully qualified Java class name of the JDBC driver to be used. 
	 *  The driver has to be accessible from the same classloader as 
	 *  tomcat-jdbc.jar
	 */
	private String driverClassName;
	
	/**
	 * The connection username to be passed to our JDBC driver to establish a 
	 * connection. Note that method DataSource.getConnection(username,password) 
	 * by default will not use credentials passed into the method, but will use 
	 * the ones configured here. See alternateUsernameAllowed 
	 * property for more details.	
	 */
	private String userName;
	
	/**
	 * The connection password to be passed to our JDBC driver to establish a 
	 * connection. Note that method DataSource.getConnection(username,password) 
	 * by default will not use credentials passed into the method, but will use 
	 * the ones configured here. See alternateUsernameAllowed property for more 
	 * details.
	 */
	private String password;
	
	/**
	 * The maximum number of active connections that can be allocated from this 
	 * pool at the same time. The default value is 100
	 */
	private Integer maxActive;
	
	/**
	 * The initial number of connections that are created when the pool is 
	 * started. Default value is 10
	 */
	private Integer initialSize;
	
	/**
	 * The maximum number of milliseconds that the pool will wait (when there 
	 * are no available connections) for a connection to be returned before 
	 * throwing an exception. Default value is 30000 (30 seconds)
	 */
	private Integer maxWait;

	/**
	 * The maximum number of connections that should be kept in the pool at all 
	 * times. Default value is maxActive:100 Idle connections are checked 
	 * periodically (if enabled) and connections that been idle for longer than 
	 * minEvictableIdleTimeMillis will be released.
	 */	
	private Integer maxIdle;
	
	/**
	 * The minimum number of established connections that should be kept in the 
	 * pool at all times. The connection pool can shrink below this number if 
	 * validation queries fail. Default value is derived from initialSize:10 
	 * (also see testWhileIdle)
	 */
	private Integer minIdle;
	
	/**
	 * The SQL query that will be used to validate connections from this pool 
	 * before returning them to the caller. If specified, this query does not 
	 * have to return any data, it just can't throw a SQLException. The default 
	 * value is null. If not specified, connections will be validation by the 
	 * isValid() method. Example values are SELECT 1(mysql), select 1 from 
	 * dual(oracle), SELECT 1(MS Sql Server)
	 */
	private Integer validationQuery;
	
	/**
	 * Avoid excess validation, only run validation at most at this frequency - 
	 * time in milliseconds. If a connection is due for validation, but has 
	 * been validated previously within this interval, it will not be validated 
	 * again. The default value is 3000 (3 seconds).
	 */
	private Integer validationInterval;
	
	/**
	 * The indication of whether objects will be validated by the idle object 
	 * evictor (if any). If an object fails to validate, it will be dropped 
	 * from the pool. The default value is false and this property has to be 
	 * set in order for the pool cleaner/test thread is to run (also see 
	 * timeBetweenEvictionRunsMillis)
	 */
	private Boolean testWhileIdle;
	
	/**
	 * The indication of whether objects will be validated before being 
	 * borrowed from the pool. If the object fails to validate, it will be 
	 * dropped from the pool, and we will attempt to borrow another. In order 
	 * to have a more efficient validation, see validationInterval. Default 
	 * value is false
	 */
	private Boolean testOnBorrow;

	/**
	 * The indication of whether objects will be validated before being 
	 * returned to the pool. The default value is false.
	 */
	private Boolean testOnReturn;	
	
	/**
	 * Register the pool with JMX or not. The default value is true.
	 */
	private Boolean jmxEnabled;
		
	/**
	 * Flag to log stack traces for application code which abandoned a 
	 * Connection. Logging of abandoned Connections adds overhead for every 
	 * Connection borrow because a stack trace has to be generated. The default 
	 * value is false.
	 */
	private Boolean logAbandoned;
	
	/**
	 * Flag to remove abandoned connections if they exceed the 
	 * removeAbandonedTimeout. If set to true a connection is considered 
	 * abandoned and eligible for removal if it has been in use longer than 
	 * the removeAbandonedTimeout Setting this to true can recover db 
	 * connections from applications that fail to close a connection. See also 
	 * logAbandoned The default value is false.
	 */
	private Boolean removeAbandoned;
	
	/**
	 * Timeout in seconds before an abandoned(in use) connection can be 
	 * removed. The default value is 60 (60 seconds). The value should be set 
	 * to the longest running query your applications might have.
	 */
	private Integer removeAbandonedTimeout;

	/**
	 * The number of milliseconds to sleep between runs of the idle connection 
	 * validation/cleaner thread. This value should not be set under 1 second. 
	 * It dictates how often we check for idle, abandoned connections, and how 
	 * often we validate idle connections. The default value is 5000 
	 * (5 seconds). 
	 */
	private Integer timeBetweenEvictionRunsMillis;

	public TomcatDataSourceConfig() { /* Default constructor */ }
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}

	public Integer getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public Integer getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}

	public Integer getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(Integer validationQuery) {
		this.validationQuery = validationQuery;
	}

	public Integer getValidationInterval() {
		return validationInterval;
	}

	public void setValidationInterval(Integer validationInterval) {
		this.validationInterval = validationInterval;
	}

	public Boolean getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(Boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public Boolean getTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(Boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public Boolean getTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(Boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public Boolean getJmxEnabled() {
		return jmxEnabled;
	}

	public void setJmxEnabled(Boolean jmxEnabled) {
		this.jmxEnabled = jmxEnabled;
	}

	public Boolean getLogAbandoned() {
		return logAbandoned;
	}

	public void setLogAbandoned(Boolean logAbandoned) {
		this.logAbandoned = logAbandoned;
	}

	public Boolean getRemoveAbandoned() {
		return removeAbandoned;
	}

	public void setRemoveAbandoned(Boolean removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}

	public Integer getRemoveAbandonedTimeout() {
		return removeAbandonedTimeout;
	}

	public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
		this.removeAbandonedTimeout = removeAbandonedTimeout;
	}

	public Integer getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(
			Integer timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	@Override
	public String toString() {
		return "TomcatDataSourceConfigTemplate [url=" + url
				+ ", driverClassName=" + driverClassName + ", userName="
				+ userName + ", password=" + password + ", maxActive="
				+ maxActive + ", initialSize=" + initialSize + ", maxWait="
				+ maxWait + ", maxIdle=" + maxIdle + ", minIdle=" + minIdle
				+ ", validationQuery=" + validationQuery
				+ ", validationInterval=" + validationInterval
				+ ", testWhileIdle=" + testWhileIdle + ", testOnBorrow="
				+ testOnBorrow + ", testOnReturn=" + testOnReturn
				+ ", jmxEnabled=" + jmxEnabled + ", logAbandoned="
				+ logAbandoned + ", removeAbandoned=" + removeAbandoned
				+ ", removeAbandonedTimeout=" + removeAbandonedTimeout
				+ ", timeBetweenEvictionRunsMillis="
				+ timeBetweenEvictionRunsMillis + "]";
	}
	
}
