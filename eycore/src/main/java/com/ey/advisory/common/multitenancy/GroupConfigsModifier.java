package com.ey.advisory.common.multitenancy;

import java.util.List;
import java.util.Map;

import com.ey.advisory.core.async.domain.master.GroupConfig;

/**
 * Once the DBConfigs for a group are loaded from the repository, this interface
 * gives us a chance to modify the group configs for different groups. For
 * example, in a development environment for Hana Cloud, we will be using the
 * JDBC URL based on the tunnel created on individual developer's systems. So,
 * we cannot have this config value stored in the DEV DB (as it might differ for
 * different developers). In such a scenario, we can have a DBConfigModifier
 * implementation where the existing config value is replaced with the tunnel
 * value. The default implementation of this interface does nothing (which
 * should be the case in production systems).
 *
 */
public interface GroupConfigsModifier {

	public Map<String, List<GroupConfig>> alterGroupConfigs(
			Map<String, List<GroupConfig>> configs);

	public List<GroupConfig> alterGroupConfigs(List<GroupConfig> configs);
}
