package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;

/**
 * @author Siva Reddy
 *
 */
public interface GetEInvDetails {

	public APIResponse getEInvDetails(String irnNo, String gstIn,
			String source);

}
