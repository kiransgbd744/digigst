package com.ey.advisory.gstnapi;

/**
 * 
 * @author Khalid1.Khan
 *
 */
@FunctionalInterface
public interface FailureHandlerWrapper {

	public void handleFailure(String failureHandler, FailureResult result,
			String apiParams);

}
