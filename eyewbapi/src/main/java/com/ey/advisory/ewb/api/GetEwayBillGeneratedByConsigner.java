package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Ravindra
 *
 */
public interface GetEwayBillGeneratedByConsigner {

	public APIResponse getEwayBillGeneratedByConsigner(String docType,
			String docNo, String gstin);

}
