package com.ey.advisory.app.services.einvoice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.api.CancelIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.common.BusinessStatisticsLogHelper;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnNICResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * @author Arun K.A
 *
 */
@Component("CancelIrnServiceImpl")
public class CancelIrnServiceImpl implements CancelIrnService {

	@Autowired
	@Qualifier("DefaultEINVNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("CancelIrnImpl")
	private CancelIrn cancelIrn;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	BusinessStatisticsLogHelper businessStatisticsLogHelper;

	@Override
	public List<CancelIrnERPResponseDto> cancelEinvRequest(
			CancelIrnReqList req) {
		List<CancelIrnReqDto> reqList = req.getReqList();
		List<CancelIrnERPResponseDto> rspList = new ArrayList<>();
		reqList.forEach(o -> {
			reqLogHelper.logAppMessage(null, o.getIrn(), null,
					"Processing Cancel E Invoice Request");
			CancelIrnERPResponseDto resp = processResponse(
					cancelIrn.cancelIrn(o), o.getGstin());
			rspList.add(resp);
		});
		return rspList;
	}

	private CancelIrnERPResponseDto processResponse(APIResponse response,
			String gstin) {
		CancelIrnERPResponseDto cancelResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
			jsonResp = response.getResponse();
			CancelIrnNICResponseDto cancelNicResp = gson.fromJson(jsonResp,
					CancelIrnNICResponseDto.class);

			cancelResp = convertNICtoERP(cancelNicResp);
			reqLogHelper.logAppMessage(null, cancelResp.getIrn(), null,
					"Success Response from NIC for Cancel E invoice");

			businessStatisticsLogHelper.logBusinessApiResponse(null, null,
					cancelNicResp.getIrn(), null, null, null, gstin, null, null,
					null, null, cancelNicResp.getCancelDate(), null,
					APIIdentifiers.CANCEL_EINV, false, null, null);

		} else {
			List<APIError> apiErrorList = response.getErrors();
			cancelResp = createErrorResponse(apiErrorList);
		}
		return cancelResp;

	}

	private CancelIrnERPResponseDto createErrorResponse(
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
		CancelIrnERPResponseDto resp = new CancelIrnERPResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

	private CancelIrnERPResponseDto convertNICtoERP(
			CancelIrnNICResponseDto response) {
		CancelIrnERPResponseDto erpReqDto = new CancelIrnERPResponseDto();
		erpReqDto.setCancelDate(response.getCancelDate());
		erpReqDto.setIrn(response.getIrn());
		erpReqDto.setErrorCode(response.getErrorCode());
		erpReqDto.setErrorMessage(response.getErrorMessage());
		return erpReqDto;
	}

}
