/**
 * 
 */
package com.ey.advisory.gstnapi;

/**
 * @author Khalid1.Khan
 *
 */
@FunctionalInterface
public interface SuccessHandler {
	
	public void handleSuccess(SuccessResult result, String apiParams);

}
