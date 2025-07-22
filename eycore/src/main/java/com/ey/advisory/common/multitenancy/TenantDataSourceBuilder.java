package com.ey.advisory.common.multitenancy;

import javax.sql.DataSource;

import com.ey.advisory.core.async.domain.master.Group;

/**
 * This interface is responsible for building a data source (Connection pool)
 * for a group. The inputs are the Group information and the list of 
 * configurations available for the group. We assume that there are certain
 * DB related configurations available for the group.
 * 
 * @author Sai.Pakanati
 *
 */
public interface TenantDataSourceBuilder {
	
	/**
	 * This method builds the data source (connection pool) for the specified
	 * group. The caller will pass the Group Information and the list of 
	 * configurations available for the group. Depending on different types
	 * of data sources, different configurations may be required in the 
	 * group configuration. Individual sub classes of this interface are 
	 * responsible for creating the data source from the group information and
	 * the group configuration information.
	 * 
	 * @param group the basic meta information about the group.
	 * 
	 * @param configs list of configuration information available for the group.
	 * 
	 * @return
	 */
	public DataSource buildDataSource(Group group, TenantDataSourceProperties prop);
}
