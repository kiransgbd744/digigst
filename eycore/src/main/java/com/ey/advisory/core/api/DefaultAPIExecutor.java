package com.ey.advisory.core.api;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

/**
 * This is the root class that needs to be autowired to the caller of this API
 * framework. This class is implemented as a Decorator, but performs the role of
 * a factory, that instantiates the acutal APIExecutor implementation that's
 * responsible for executing the API.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("DefaultAPIExecutor")
public class DefaultAPIExecutor implements APIExecutor {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public APIResponse execute(APIParams params, String reqData) {
		Config config = configManager.getConfig("PERFORMANCE", "apis.to.stub",
				params.getGroupCode());
		boolean useStubs = config != null && config.getValue() != null
				? Arrays.asList(config.getValue().split(","))
						.contains(params.getApiIdentifier())
				: Boolean.FALSE;

		// Get the type of API Executor to use.
		APIExecutor execToUse = getAPIExecutor(useStubs);
		return execToUse.execute(params, reqData);
	}

	/**
	 * Get the API Executor to use. Currently, this method uses the beans with
	 * predefined names for Stub Executor and Non Stub Executor. Instead it can
	 * look for an API Executor configuration using the config manager to load
	 * the appropriate bean, so that we can switch implementations.
	 * 
	 * @param useStubs
	 * @return
	 */
	private APIExecutor getAPIExecutor(boolean useStubs) {

		String beanName = !useStubs ? APIConstants.DEFAULT_NON_STUB_API_EXECUTOR
				: APIConstants.DEFAULT_STUB_API_EXECUTOR;
		return StaticContextHolder.getBean(beanName, APIExecutor.class);
	}

}
