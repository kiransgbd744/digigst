package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Ravindra
 *
 */
public interface GetEWBDetailsByDate {
	
	public APIResponse getEWBDetailsByDate(
			String date, String gstin);

}
