package com.ey.advisory.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

@Component("PerformanceConfig")
public class PerformanceConfig {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public boolean isMonitoringEnabled(String moduleName) {
		Config config = configManager.getConfig("PERFORMANCE",
				"modules.to.monitor", TenantContext.getTenantId());
		if (config == null)
			return false;
		List<String> modules = Arrays.asList(config.getValue().split(","));
		return modules.contains(moduleName);
	}
}
