package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;

/**
 * @author Siva Reddy
 *
 */
public interface GetEWBDetailsByIrn {

	public APIResponse getEWBDetailsByIrn(String irnNo,String gstIn);

}
