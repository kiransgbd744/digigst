/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Khalid1.Khan
 *
 */
public interface GetEWBDetails {
	
	public APIResponse getEWBDetails(
			String ewbNo, String gstin);

}
