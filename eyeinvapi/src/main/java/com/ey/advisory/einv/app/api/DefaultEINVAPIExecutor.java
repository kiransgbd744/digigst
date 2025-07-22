package com.ey.advisory.einv.app.api;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.app.api.stubs.DefaultEINVStubExecutor;

@Component("DefaultEINVAPIExecutor")
public class DefaultEINVAPIExecutor implements APIExecutor {

	@Autowired
	DefaultEINVNonStubExecutor defaultEWBNonStubExecutor;

	@Autowired
	DefaultEINVStubExecutor defaultEWBStubExecutor;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		Config config = configManager.getConfig("PERFORMANCE", "apis.to.stub",
				TenantContext.getTenantId());
		boolean useStubs = config != null && config.getValue() != null
				? Arrays.asList(config.getValue().split(","))
						.contains(params.getApiAction())
				: Boolean.FALSE;

		if (useStubs) {
			return defaultEWBStubExecutor.execute(params, reqData);
		} else {
			return defaultEWBNonStubExecutor.execute(params, reqData);
		}
	}
}
