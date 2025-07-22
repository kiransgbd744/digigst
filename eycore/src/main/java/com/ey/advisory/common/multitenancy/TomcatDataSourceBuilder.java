package com.ey.advisory.common.multitenancy;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * Our application has one DB per client. So every client will require it's own
 * DB connection pool. This class is responsible for creating the connection
 * pool for a specified group, using the tomcat Connection Pool library (which
 * is basically an advanced version of the Apache DBCP connection pool library).
 * Since there are several configuration pool properties that can be specified,
 * we can either choose to store each of these properties in the master database
 * (as client configuration properties) and load these properties and create the
 * connection pool at runtime OR we can choose to store only the variable
 * details for each client (like the DB URL, DB user name and DB password) as
 * part of the client DB configuration and maintain a single copy of the the
 * remaining configuration properties that can be applied for connection pool
 * created for every client. We achieve this by having these common properties
 * configured in a PoolProperties spring bean, which will be read at runtime to
 * create the client specific connection pool. A new PoolProperties will be
 * instantiated at runtime and the common properties will be copied into this
 * object from the singleton spring PoolProperties bean and the client specific
 * properties will be loaded from the DB and set to the same PoolProperties
 * bean, thereby leaving us with a fully configured PoolProperties instance that
 * will be used to create the client connection pool to the DB.
 * 
 * @author Sai.Pakanati
 * 
 */

@Slf4j
@Component("TomcatDataSourceBuilder")
public class TomcatDataSourceBuilder implements TenantDataSourceBuilder {

	/**
	 * This is an autowired proptotype spring bean that are injected with the
	 * common DataSource properties (like maxPoolSize, testOnBorrow etc). Since
	 * we have a multi-tenant DB, individual connection pools need to be
	 * maintained, one for each client DB. So, the properties that vary across
	 * clients, like - DB URL, connection user name and connection password will
	 * be maintained as part of client configuration in the database. But other
	 * properties that are common across the clients like connection pool size
	 * configuration etc can be configured here. Depending on the type of
	 * connection pool library used, there can be different sets of
	 * configurations available. This particular object is for tomcat pool
	 * configuration.
	 * 
	 * Since this class 'tomcatConnPoolProperties' is declared as a Spring
	 * prototype bean, and instantiated using the ObjectFactory.getObject()
	 * Spring method, everytime this method is called, a new instance of the
	 * bean will be handed over to the application with these common properties
	 * set in it. Then the application will set the additional properties that
	 * are stored in the DB, into this bean. This final instance will be used to
	 * create a Tomact pool DataSource with these configurations. This process
	 * is done for all of the tenant databases.
	 * 
	 */
	@Autowired
	@Qualifier("tomcatConnPoolProperties")
	private ObjectFactory<PoolProperties> tomcatPoolPropertiesFactory;

	@Override
	public DataSource buildDataSource(Group group,
			TenantDataSourceProperties dataSourceProperties) {

		// Get the DBConfig object for the group.
		TenantDBConfig pConfig = group.getDBConfig();

		// Some of the groups may not have a valid DBConfig. For such groups,
		// return the data source as null.
		if (pConfig == null) {
			String msg = String.format(
					"Invalid DB Config parameters "
							+ "detected for the Group: '%s'. "
							+ "Returning null Data Source",
					group.getGroupCode());
			LOGGER.error(msg);
			return null;
		}

		// Copy all the properties present in the common properties object
		// injected into this class to the new PoolProperties object that we'll
		// use to cretae the connection pool.
		// Set the 3 values that are extracted from the DB. These are the
		// variable configurations for the client.

		// Now that we have a fully populated PoolProperties object, we create
		// a data source object and return it to the caller.
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setUrl(pConfig.getUrl());
		ds.setUsername(pConfig.getUsername());
		ds.setPassword(pConfig.getPassword());
//		ds.setDriverClassName(dataSourceProperties.getDriverClassName());
//		ds.setInitialSize(dataSourceProperties.getInitialSize());
//		ds.setRemoveAbandoned(dataSourceProperties.getRemoveAbandoned());
//		ds.setRemoveAbandonedTimeout(
//				dataSourceProperties.getRemoveAbandonedTimeout());
//		ds.setMaxActive(dataSourceProperties.getMaxActive());
//		ds.setTestOnBorrow(dataSourceProperties.getTestOnBorrow());
//		ds.setMaxIdle(dataSourceProperties.getMaxIdle());
//		ds.setMinIdle(dataSourceProperties.getMinIdle());
//		ds.setLogAbandoned(dataSourceProperties.getLogAbandoned());
//		ds.setMaxWait(dataSourceProperties.getMaxWait());
//		ds.setValidationQuery("SELECT 1 FROM DUMMY");
//		ds.setTestOnBorrow(true);
//		ds.setJdbcInterceptors(
//				"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
//						+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
//						+ "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
		
		ds.setDriverClassName("com.sap.db.jdbc.Driver");
		ds.setInitialSize(10);
		ds.setRemoveAbandoned(true);
		ds.setRemoveAbandonedTimeout(7200);
		ds.setMaxActive(100);
		ds.setTestOnBorrow(true);
		ds.setMaxIdle(10);
		ds.setMinIdle(5);
		ds.setLogAbandoned(true);
		ds.setMaxWait(120000);
		ds.setValidationQuery("SELECT 1 FROM DUMMY");
		ds.setTestOnBorrow(true);
//		ds.setJdbcInterceptors(
//				"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
//						+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
//						+ "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
		ds.setMinEvictableIdleTimeMillis(600000);
		ds.setTimeBetweenEvictionRunsMillis(300000);
		ds.setValidationInterval(300000);

		return ds;
	}

}
