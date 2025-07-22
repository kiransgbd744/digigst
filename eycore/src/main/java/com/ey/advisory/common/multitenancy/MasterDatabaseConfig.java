package com.ey.advisory.common.multitenancy;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.ey.advisory.*")
@EnableJpaRepositories(basePackages = { "com.ey.advisory.core.async.repositories.master",
		"com.ey.advisory.gstnapi.repositories.master", "com.ey.advisory.app.master.repositories",
		"com.ey.advisory.admin.data.repositories.master",
		"com.ey.advisory.app.data.repositories.master" }, entityManagerFactoryRef = "masterEMFactory", transactionManagerRef = "masterTransactionManager")

@Order(1)
public class MasterDatabaseConfig {

	@Autowired
	private JpaProperties jpaProperties;

	@Autowired
	private MasterDataSourceProperties masterDataSourceProperties;

	@Value("${master.db.name}")
	private String dbName;

	@Primary
	@Bean(name = "masterEMFactory")
	public LocalContainerEntityManagerFactoryBean masterEMFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(masterDataSource());
		em.setPackagesToScan("com.ey.advisory.core.async.domain.master", "com.ey.advisory.gstnapi.domain.master",
				"com.ey.advisory.app.master.domain", "com.ey.advisory.app.data.entities.master",
				"com.ey.advisory.admin.data.entities.master");
		em.setPersistenceUnitName("masterDataUnit");
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		HibernateJpaDialect jpaDialect = new HibernateJpaDialect();
		em.setJpaDialect(jpaDialect);
		Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
		jpaPropertiesMap.put(Environment.SHOW_SQL, false);
	    jpaPropertiesMap.put("hibernate.jdbc.batch_size", 50); 
	    jpaPropertiesMap.put("hibernate.order_inserts", true);
	    jpaPropertiesMap.put("hibernate.order_updates", true);
	    jpaPropertiesMap.put("hibernate.jdbc.batch_versioned_data", true);
	    jpaPropertiesMap.put("hibernate.bytecode.use_reflection_optimizer", false);
	    jpaPropertiesMap.put("hibernate.current_session_context_class", "thread");
	    jpaPropertiesMap.put("hibernate.generate_statistics", true);
	    jpaPropertiesMap.put("testWhileIdle", true);
	    jpaPropertiesMap.put("timeBetweenEvictionRunsMillis", 60000);
		em.setJpaPropertyMap(jpaPropertiesMap);
		return em;
	}

	@Bean(name = "masterTransactionManager")
	public JpaTransactionManager masterTransactionManager(@Qualifier("masterEMFactory") EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory((jakarta.persistence.EntityManagerFactory) emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean("lockProvider")
	public JdbcTemplateLockProvider lockProvider() {
		return new JdbcTemplateLockProvider(masterDataSource());
	}

	@Bean(name = "masterDataSource", destroyMethod = "close")
	public org.apache.tomcat.jdbc.pool.DataSource masterDataSource() {
		org.apache.tomcat.jdbc.pool.DataSource masterDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		System.out.println(masterDataSourceProperties.getUrl() + dbName);
		masterDataSource.setUrl(masterDataSourceProperties.getUrl() + dbName);
		masterDataSource.setDriverClassName(masterDataSourceProperties.getDriverClassName());
		masterDataSource.setUsername(masterDataSourceProperties.getUsername());
		masterDataSource.setPassword(masterDataSourceProperties.getPassword());
		masterDataSource.setInitialSize(masterDataSourceProperties.getIntialSize());
		masterDataSource.setRemoveAbandoned(masterDataSourceProperties.getRemoveAbandoned());
		masterDataSource.setRemoveAbandonedTimeout(masterDataSourceProperties.getRemoveAbandonedTimeout());
		masterDataSource.setMaxActive(masterDataSourceProperties.getMaxActive());
		masterDataSource.setTestOnBorrow(masterDataSourceProperties.getTestOnBorrow());
		masterDataSource.setMaxIdle(masterDataSourceProperties.getMaxIdle());
		masterDataSource.setMinIdle(masterDataSourceProperties.getMinIdle());
		masterDataSource.setLogAbandoned(masterDataSourceProperties.getLogAbandoned());

		masterDataSource.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
		return masterDataSource;
	}
}
