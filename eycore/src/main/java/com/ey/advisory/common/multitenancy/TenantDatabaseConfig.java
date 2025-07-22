package com.ey.advisory.common.multitenancy;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.ey.advisory")
@EnableJpaRepositories(basePackages = { "com.ey.advisory.sap.repositories.client",
		"com.ey.advisory.gstnapi.repositories.client", "com.ey.advisory.repositories.client",
		"com.ey.advisory.app.data.repositories.client", "com.ey.advisory.jobs.repositories.client",
		"com.ey.advisory.admin.data.repositories.client", "com.ey.advisory.app.data.repositories.client.gstr2",
		"com.ey.advisory.app.data.repositories.clientBusiness", "com.ey.advisory.ewb.client.repositories",
		"com.ey.advisory.common.client.repositories","com.ey.advisory.repositories","com.ey.advisory.app.data.repositories.client.gstr7trans","com.ey.advisory.app.data.repositories.client.gstr9","com.ey.advisory.app.data.gstr1A.repositories.client","com.ey.advisory.app.data.repositories.client.supplier.ims" }, entityManagerFactoryRef = "clientEMFactory", transactionManagerRef = "clientTransactionManager")
public class TenantDatabaseConfig {

	@Autowired
	private TenantJpaProperties tenantJpaProperties;

	@Bean(name = "clientEMFactory")
	public LocalContainerEntityManagerFactoryBean clientEMFactory(
			@Qualifier("tenantConnectionProviderImpl") MultiTenantConnectionProvider connectionProvider,
			@Qualifier("tenantIdentifierResolverImpl") CurrentTenantIdentifierResolver tenantResolver) {

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan("com.ey.advisory");
		em.setPersistenceUnitName("clientDataUnit");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		HibernateJpaDialect jpaDialect = new HibernateJpaDialect();
		em.setJpaDialect(jpaDialect);
		em.setJpaPropertyMap(this.jpaPropertiesMapClient(connectionProvider, tenantResolver));
		return em;
	}

	@Bean(name = "clientTransactionManager")
	public JpaTransactionManager clientTransactionManager(
			@Qualifier("clientEMFactory") EntityManagerFactory clientEntityManager) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(clientEntityManager);
		return transactionManager;
	}

	Map<String, Object> jpaPropertiesMapClient(MultiTenantConnectionProvider connectionProvider,
			CurrentTenantIdentifierResolver tenantResolver) {
		Map<String, Object> jpaPropertiesMap = new HashMap<>(tenantJpaProperties.getProperties());
		jpaPropertiesMap.put("hibernate.multiTenancy", "DATABASE");
		jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
		jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
		jpaPropertiesMap.put(Environment.SHOW_SQL, tenantJpaProperties.isShowSql());
		jpaPropertiesMap.put(Environment.DIALECT, tenantJpaProperties.getDatabasePlatform());
	    jpaPropertiesMap.put("hibernate.jdbc.batch_size", 50); 
	    jpaPropertiesMap.put("hibernate.order_inserts", true);
	    jpaPropertiesMap.put("hibernate.order_updates", true);
	    jpaPropertiesMap.put("current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
	    jpaPropertiesMap.put("hibernate.bytecode.use_reflection_optimizer", false);
	    jpaPropertiesMap.put("hibernate.connection.defaultNChar", false);
	    jpaPropertiesMap.put("hibernate.generate_statistics", true);
	    jpaPropertiesMap.put("testWhileIdle", true);
	    jpaPropertiesMap.put("timeBetweenEvictionRunsMillis", 60000);
	    jpaPropertiesMap.put("hibernate.flushMode", "COMMIT");
		return jpaPropertiesMap;
	}
}
