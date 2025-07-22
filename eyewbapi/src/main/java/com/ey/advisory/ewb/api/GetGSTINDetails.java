package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Ravindra
 *
 */
public interface GetGSTINDetails {
	
	public APIResponse getGSTINDetails(String gstin, String getGstin);

}
