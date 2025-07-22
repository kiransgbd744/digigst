package com.ey.advisory.common.multitenancy;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.async.domain.master.GroupConfig;

/**
 * This class implements the DBConfigsModifier interface to be used in 
 * production environments. In fact, this implementation should be used in 
 * all non-hana cloud development environments. For hana cloud development
 * environments, check the class {@link HanaDevGroupConfigsModifier}.
 *
 */
@Component("DefaultDBConfigsModifier")
@Profile("hanadev")
public class DefaultGroupConfigsModifier implements GroupConfigsModifier {
	
	@Override
	public Map<String, List<GroupConfig>> alterGroupConfigs(
			Map<String, List<GroupConfig>> configs) {
		// return the input map without any modification.
		return configs;
	}

	@Override
	public List<GroupConfig> alterGroupConfigs(List<GroupConfig> configs) {
		return configs;
	}}
