/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Ravindra
 *
 */
public interface GetHSNDetails {

	public APIResponse getHSNDetails(String hsnCode, String gstin);
}
