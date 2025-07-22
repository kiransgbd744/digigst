/**
 * 
 */
package com.ey.advisory.gstnapi;

/**
 * @author Khalid1.Khan
 *
 */
@FunctionalInterface
public interface FailureHandler {
	
	public void handleFailure(FailureResult result, String apiParams);

}
