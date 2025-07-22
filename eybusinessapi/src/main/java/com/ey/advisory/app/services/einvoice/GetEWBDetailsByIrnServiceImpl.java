package com.ey.advisory.app.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.api.GetEWBDetailsByIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.GetEWBDetailsByIrnRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * @author Siva Reddy
 *
 */
@Component("GetEWBDetailsByIrnServiceImpl")
public class GetEWBDetailsByIrnServiceImpl
		implements GetEWBDetailsByIrnService {

	@Autowired
	@Qualifier("GetEWBDetailsByIrnImpl")
	private GetEWBDetailsByIrn getEWBDetailsByIrn;

	@Override
	public GetEWBDetailsByIrnRespDto getEWBDetailsByIrn(String irnNo,
			String gstin) {
		APIResponse response = getEWBDetailsByIrn.getEWBDetailsByIrn(irnNo,
				gstin);
		return processResponse(response);
	}

	private GetEWBDetailsByIrnRespDto processResponse(
			APIResponse response) {
		GetEWBDetailsByIrnRespDto getEWBDetailsByIrnResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.newSAPGsonInstance();
			jsonResp = response.getResponse();
			getEWBDetailsByIrnResp = gson.fromJson(jsonResp,
					GetEWBDetailsByIrnRespDto.class);
		} else {
			List<APIError> apiErrorList = response.getErrors();
			getEWBDetailsByIrnResp = createErrorResponse(apiErrorList);
		}
		return getEWBDetailsByIrnResp;
	}

	private GetEWBDetailsByIrnRespDto createErrorResponse(
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
		GetEWBDetailsByIrnRespDto resp = new GetEWBDetailsByIrnRespDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

}
