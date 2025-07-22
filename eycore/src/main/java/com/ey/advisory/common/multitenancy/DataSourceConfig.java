package com.ey.advisory.common.multitenancy;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DataSourceConfig {

	private final TenantDataSourceProperties tenantDataSourceProperties;

	public DataSourceConfig(
			TenantDataSourceProperties tenantDataSourceProperties) {
		this.tenantDataSourceProperties = tenantDataSourceProperties;
	}

	@Bean(name = "tomcatConnPoolProperties")
    @Scope("prototype")
	public DataSource tomcatConnPoolProperties() {
		DataSource dataSource = new DataSource();
		dataSource.setDriverClassName(
				tenantDataSourceProperties.getDriverClassName());
		dataSource.setInitialSize(tenantDataSourceProperties.getInitialSize());
		dataSource.setRemoveAbandoned(
				tenantDataSourceProperties.getRemoveAbandoned());
		dataSource.setRemoveAbandonedTimeout(
				tenantDataSourceProperties.getRemoveAbandonedTimeout());
		dataSource.setMaxActive(tenantDataSourceProperties.getMaxActive());
		dataSource
				.setTestOnBorrow(tenantDataSourceProperties.getTestOnBorrow());
		dataSource.setMinIdle(tenantDataSourceProperties.getMinIdle());
		dataSource.setMaxIdle(tenantDataSourceProperties.getMaxIdle());
		dataSource
				.setLogAbandoned(tenantDataSourceProperties.getLogAbandoned());
		dataSource.setMaxWait(tenantDataSourceProperties.getMaxWait());
		dataSource.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
		return dataSource;
	}
}
