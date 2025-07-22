package com.ey.advisory.app.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.api.GetSyncGSTINDetails;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailsERPRespDto;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailsResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * @author Siva Reddy
 *
 */
@Component("GetSyncGSTINDetailsServiceImpl")
public class GetSyncGSTINDetailsServiceImpl
		implements GetSyncGSTINDetailsService {

	@Autowired
	@Qualifier("GetSyncGSTINDetailsImpl")
	private GetSyncGSTINDetails getSyncGSTINDet;

	@Override
	public GetSyncGSTINDetailsERPRespDto getSyncDetails(String syncGstin,
			String gstin) {
		APIResponse response = getSyncGSTINDet.getSyncGSTINDetails(syncGstin,
				gstin);
		return processResponse(response, syncGstin);
	}

	private GetSyncGSTINDetailsERPRespDto processResponse(APIResponse response,
			String syncGstin) {
		GetSyncGSTINDetailsERPRespDto getSyncGSTINERPResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.newSAPGsonInstance();
			jsonResp = response.getResponse();
			GetSyncGSTINDetailsResponseDto getSyncGSTINResp = gson
					.fromJson(jsonResp, GetSyncGSTINDetailsResponseDto.class);
			getSyncGSTINERPResp = convertNICtoERPDto(getSyncGSTINResp);
		} else {
			List<APIError> apiErrorList = response.getErrors();
			getSyncGSTINERPResp = createErrorResponse(apiErrorList, syncGstin);
		}
		return getSyncGSTINERPResp;
	}

	private GetSyncGSTINDetailsERPRespDto createErrorResponse(
			List<APIError> apiErrorList, String gstin) {
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
		GetSyncGSTINDetailsERPRespDto resp = new GetSyncGSTINDetailsERPRespDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		resp.setGstin(gstin);
		return resp;
	}

	private GetSyncGSTINDetailsERPRespDto convertNICtoERPDto(
			GetSyncGSTINDetailsResponseDto respDto) {
		GetSyncGSTINDetailsERPRespDto erpDto = new GetSyncGSTINDetailsERPRespDto();
		erpDto.setGstin(respDto.getGstin());
		erpDto.setTradeName(respDto.getTradeName());
		erpDto.setLegalName(respDto.getLegalName());
		erpDto.setAddrBnm(respDto.getAddrBnm());
		erpDto.setAddrBno(respDto.getAddrBno());
		erpDto.setAddrSt(respDto.getAddrSt());
		erpDto.setAddrFlno(respDto.getAddrFlno());
		erpDto.setAddrLoc(respDto.getAddrLoc());
		erpDto.setStateCode(respDto.getStateCode());
		erpDto.setAddrPncd(respDto.getAddrPncd());
		erpDto.setTxpType(respDto.getTxpType());
		erpDto.setStatus(respDto.getStatus());
		erpDto.setBlkStatus(respDto.getBlkStatus());
		erpDto.setDtDReg(respDto.getDtDReg());
		erpDto.setDtReg(respDto.getDtReg());

		return erpDto;
	}
}
