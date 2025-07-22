package com.ey.advisory.common.multitenancy;



import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 
 * This class returns the data source of a tenant (groupname). It uses the
 * TenantDataSource instance to actually get the data source. TenantDataSource
 * is responsible for creating and maintaining the data sources for each group.
 * 
 * @author Sai.Pakanati
 * 
 */
@Component
public class TenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 1L;

	/**
	 * Tenant data source object.
	 */
	@Autowired
	private TenantDataSource tenantDataSource;

	@Autowired
	@Qualifier("masterDataSource")
	private DataSource masterDataSource;

	/**
	 * This method is to select the default master data source.
	 * 
	 */
	@Override
	protected DataSource selectAnyDataSource() {
		return masterDataSource;
	}

	/**
	 * This method is to select the data source for the given tenant(group).
	 */

	@Override
	protected DataSource selectDataSource(Object tenantIdentifier) {
		// TODO Auto-generated method stub
		return tenantDataSource.getDataSource((String) tenantIdentifier);
	}

}
