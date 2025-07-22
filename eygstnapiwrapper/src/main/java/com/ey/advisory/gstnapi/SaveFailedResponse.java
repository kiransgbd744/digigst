/**
 * 
 */
package com.ey.advisory.gstnapi;

import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;

/**
 * @author Khalid1.Khan
 *
 */
public interface SaveFailedResponse {
	public APIResponseEntity saveResponse(APIInvocationReqEntity reqEntity,
			APIInvocationError error);

}
