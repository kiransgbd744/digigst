/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Khalid1.Khan
 *
 */
public interface GetEwbByConsigner {
	public APIResponse getEwbByConsigner(String docType, String docNo,
			String gstin);
}
