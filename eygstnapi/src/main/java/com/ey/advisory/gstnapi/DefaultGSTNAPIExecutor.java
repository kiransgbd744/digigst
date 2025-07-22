package com.ey.advisory.gstnapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.DefaultNonStubExecutor;
import com.ey.advisory.core.api.impl.DefaultStubExecutor;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;


@Component("DefaultGSTNAPIExecutor")
public class DefaultGSTNAPIExecutor implements APIExecutor {
	
	@Autowired
	DefaultNonStubExecutor defaultEWBNonStubExecutor;
	
	@Autowired
	DefaultStubExecutor defaultEWBStubExecutor;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		
		Config config = configManager.getConfig(
				"API", "api.nic.id(all).use_stubs");
		String useStub = config != null ? config.getValue() : "false";
		if(Boolean.parseBoolean(useStub)) {
			return defaultEWBStubExecutor.execute(params, reqData);
		} else {
			return defaultEWBNonStubExecutor.execute(params, reqData);
		}
	}
}
