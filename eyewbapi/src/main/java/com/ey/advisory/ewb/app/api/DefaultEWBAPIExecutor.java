package com.ey.advisory.ewb.app.api;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.app.api.stubs.DefaultEWBStubExecutor;


@Component("DefaultEWBAPIExecutor")
public class DefaultEWBAPIExecutor implements APIExecutor {
	
	@Autowired
	DefaultEWBNonStubExecutor defaultEWBNonStubExecutor;
	
	@Autowired
	DefaultEWBStubExecutor defaultEWBStubExecutor;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		
		Config config = configManager.getConfig("PERFORMANCE", "apis.to.stub",
				TenantContext.getTenantId());
		boolean useStubs = config != null
				? Arrays.asList(config.getValue().split(","))
						.contains(params.getApiAction())
				: Boolean.FALSE;
		if(useStubs) {
			return defaultEWBStubExecutor.execute(params, reqData);
		} else {
			return defaultEWBNonStubExecutor.execute(params, reqData);
		}
	}
}
