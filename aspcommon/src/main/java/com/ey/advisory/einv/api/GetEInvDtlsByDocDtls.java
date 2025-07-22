package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;

/**
 * @author Siva Reddy
 *
 */
public interface GetEInvDtlsByDocDtls {

	public APIResponse getEInvDtlsByDocDetails(String docNum, String docType,
			String docDate,String gstion);

}
