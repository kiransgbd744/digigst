/**
 * 
 */
package com.ey.advisory.gstnapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

/**
 * @author Khalid1.Khan
 *
 */
@Component("SaveFailedResponseImpl")
public class SaveFailedResponseImpl implements SaveFailedResponse {

	@Autowired
	APIResponseRepository apiRespRepo;
	
	@Override
	public APIResponseEntity saveResponse(APIInvocationReqEntity reqEntity,
			APIInvocationError error) {
		APIResponseEntity responseEntity = new APIResponseEntity(
				error.getErrorCode(), error.getErrorDesc(), null,
				GstnApiWrapperConstants.FAILED);
		responseEntity.setErrorCode(error.getErrorCode());
		responseEntity.setErrorDesc(error.getErrorDesc());
		responseEntity.setInvocationId(null);
		responseEntity.setStatus(GstnApiWrapperConstants.FAILED);
		apiRespRepo.save(responseEntity);
		return responseEntity;
		
	}

}
