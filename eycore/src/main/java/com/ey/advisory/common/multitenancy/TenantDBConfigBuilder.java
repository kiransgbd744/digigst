package com.ey.advisory.common.multitenancy;

import java.util.List;

import com.ey.advisory.core.async.domain.master.GroupConfig;

/**
 * This interface is responsible for building the DBConfig objects for the 
 * tenant databases. The DB config object contains details like DB URL,
 * DB User name and password.
 * 
 * @author Sai.Pakanati
 *
 */
public interface TenantDBConfigBuilder {

	/**
	 * Return the TenantDBConfig object for the specified group. The list of
	 * available configurations are passed as input to this method. 
	 * 
	 * @param groupCode
	 * @param configs
	 * @return
	 */
	public TenantDBConfig buildDBConfig(
			String groupCode, List<GroupConfig> configs);
}
