package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;

/**
 * @author Siva Reddy
 *
 */
public interface GetSyncGSTINDetails {

	public APIResponse getSyncGSTINDetails(String irnNo,String gstIn);

}
