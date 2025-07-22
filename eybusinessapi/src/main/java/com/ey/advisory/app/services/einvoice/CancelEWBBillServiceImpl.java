package com.ey.advisory.app.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.api.CancelEWBBill;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.CancelEWBBillERPReqDto;
import com.ey.advisory.einv.dto.CancelEWBBillNICReqDto;
import com.ey.advisory.einv.dto.CancelEWBBillRequest;
import com.ey.advisory.einv.dto.CancelEWBBillResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * @author Siva Reddy
 *
 */

@Component("CancelEWBBillServiceImpl")
public class CancelEWBBillServiceImpl implements CancelEWBBillService {

	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("CancelEWBBillImpl")
	private CancelEWBBill cancelEWBBill;

	@Override
	public CancelEWBBillResponseDto cancelEwaybillRequest(
			CancelEWBBillRequest req) {

		CancelEWBBillERPReqDto erpReqDto = req.getCancelEwbBillERPreqdto();
		CancelEWBBillNICReqDto nicReqDto = convertERPtoNIC(erpReqDto);
		return processResponse(cancelEWBBill.cancelEWBservice(nicReqDto));
	}

	private CancelEWBBillNICReqDto convertERPtoNIC(
			CancelEWBBillERPReqDto erpReq) {
		CancelEWBBillNICReqDto nicReqDto = new CancelEWBBillNICReqDto();
		nicReqDto.setGstin(erpReq.getGstin());
		nicReqDto.setEwbNo(Long.valueOf(erpReq.getEwbNo()));
		nicReqDto.setCancelRmrk(erpReq.getCancelRmrk());
		nicReqDto.setCancelRsnCode(Long.valueOf(erpReq.getCancelRsnCode()));
		return nicReqDto;

	}

	private CancelEWBBillResponseDto processResponse(APIResponse response) {
		CancelEWBBillResponseDto cancelEWBBillReponse = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.newSAPGsonInstance();
			jsonResp = response.getResponse();
			cancelEWBBillReponse = gson.fromJson(jsonResp,
					CancelEWBBillResponseDto.class);
		} else {
			List<APIError> apiErrorList = response.getErrors();
			cancelEWBBillReponse = createErrorResponse(apiErrorList);
		}
		return cancelEWBBillReponse;
	}

	private CancelEWBBillResponseDto createErrorResponse(
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
		CancelEWBBillResponseDto resp = new CancelEWBBillResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

}
