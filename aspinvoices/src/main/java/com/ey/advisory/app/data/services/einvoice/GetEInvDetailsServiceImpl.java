package com.ey.advisory.app.data.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.api.GetEInvDetails;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.QrCodeDataResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;

/**
 * @author Siva Reddy
 *
 */
@Component("GetEInvDetailsServiceImpl")
public class GetEInvDetailsServiceImpl implements GetEInvDetailsService {

	@Autowired
	@Qualifier("GetEInvDetailsImpl")
	private GetEInvDetails getEInvDetails;

	@Override
	public GenerateIrnResponseDto getEInvDetails(String irnNo, String gstin,
			String source) {
		APIResponse response = getEInvDetails.getEInvDetails(irnNo, gstin,
				source);
		return createGenerateIrnResponse(response);
	}

	private GenerateIrnResponseDto createGenerateIrnResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return getParsedResponse(
					gson.fromJson(jsonResp, GenerateIrnResponseDto.class));
		} else {
			return createGenerateEwbErrorResponse(response.getErrors());
		}
	}

	public static GenerateIrnResponseDto createGenerateEwbErrorResponse(
			List<APIError> errors) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}
		GenerateIrnResponseDto errResp = new GenerateIrnResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		return errResp;
	}

	public static GenerateIrnResponseDto getParsedResponse(
			GenerateIrnResponseDto resp) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		Claims claims = JwtParserUtility
				.getJwtBodyWithoutSignature(resp.getSignedQRCode());
		String claimResp = gson.toJson(claims);
		JsonObject requestObject = (new JsonParser()).parse(claimResp)
				.getAsJsonObject();
		String qrCodeString = requestObject.get("data").getAsString();
		QrCodeDataResponse qrCodeData = gson.fromJson(qrCodeString,
				QrCodeDataResponse.class);
		resp.setBuyerGstin(qrCodeData.getBuyerGstin());
		resp.setSellerGstin(qrCodeData.getSellerGstin());
		resp.setDocDate(qrCodeData.getDocDt());
		resp.setDocNum(qrCodeData.getDocNo());
		resp.setDocType(qrCodeData.getDocTyp());
		resp.setFormattedQrCode(qrCodeString);
		resp.setQrData(resp.getSignedQRCode());
		return resp;
	}

}