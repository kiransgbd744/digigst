/**
 * 
 */
package com.ey.advisory.gstnapi;

/**
 * @author Khalid1.Khan
 *
 */
@FunctionalInterface

public interface SuccessHandlerWrapper {
	public void handleSuccess(String successHandler, SuccessResult result,
			String apiParams);

}
