package com.ey.advisory.app.data.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.api.GetSyncGSTINDetails;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailsResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * @author Siva Reddy
 *
 */
@Component("GetSyncGSTINDetailsServiceImpl")
public class GetSyncGSTINDetailsServiceImpl implements GetSyncGSTINDetailsService {


	@Autowired
	@Qualifier("GetSyncGSTINDetailsImpl")
	private GetSyncGSTINDetails getSyncGSTINDet;
	
	@Override
	public GetSyncGSTINDetailsResponseDto getSyncDetails(String syncGstin, String gstin) {
		APIResponse response = getSyncGSTINDet.getSyncGSTINDetails(syncGstin, gstin);
		return processResponse(response);
	}
	
	
	private GetSyncGSTINDetailsResponseDto processResponse(APIResponse response) {
		GetSyncGSTINDetailsResponseDto getSyncGSTINResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.newSAPGsonInstance();
			jsonResp = response.getResponse();
			getSyncGSTINResp = gson.fromJson(jsonResp,
					GetSyncGSTINDetailsResponseDto.class);
		} else {
			List<APIError> apiErrorList = response.getErrors();
			getSyncGSTINResp = createErrorResponse(apiErrorList);
		}
		return getSyncGSTINResp;
	}
	
	private GetSyncGSTINDetailsResponseDto createErrorResponse(
			List<APIError> apiErrorList) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < apiErrorList.size(); i++) {
			if (!Strings.isNullOrEmpty(apiErrorList.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ apiErrorList.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(apiErrorList.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ apiErrorList.get(i).getErrorDesc() + " ";
		}
		GetSyncGSTINDetailsResponseDto resp = new GetSyncGSTINDetailsResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

}
