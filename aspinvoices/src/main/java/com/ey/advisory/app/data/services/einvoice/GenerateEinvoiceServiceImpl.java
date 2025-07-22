/**
 * 
 */
package com.ey.advisory.app.data.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.api.GenerateIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.QrCodeDataResponse;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@Component("GenerateEinvoiceServiceImpl")
public class GenerateEinvoiceServiceImpl
		implements GenerateEinvoiceService {

	@Autowired
	@Qualifier("GenerateIrnImpl")
	private GenerateIrn generateIrn;

	@Autowired
	@Qualifier("EinvoiceRequestConverter")
	private EinvoiceRequestConverter einvReqConverter;

	@Override
	public GenerateIrnResponseDto generateEinvRequest(OutwardTransDocument hdr) {
		String gstin = hdr.getSgstin();
		EinvoiceRequestDto eInvRequestDto = einvReqConverter.convert(hdr, "true");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The OutwardTransDocument is converted to "
					+ "the EinvoiceRequestDto : {}", eInvRequestDto);
		}
		APIResponse response = generateIrn.generateIrn(eInvRequestDto, gstin);
		return createGenerateIrnResponse(response);
	}

	private GenerateIrnResponseDto createGenerateIrnResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			jsonResp = response.getResponse();
			return getParsedResponse(jsonResp);
		} else {
			return createGenerateEwbErrorResponse(response.getErrors());
		}
	}

	private GenerateIrnResponseDto createGenerateEwbErrorResponse(
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

	private GenerateIrnResponseDto getParsedResponse(String jsonResp) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto resp = gson.fromJson(jsonResp,
				GenerateIrnResponseDto.class);
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
		resp.setQrData(qrCodeString);
		
		JsonObject jsonResponse = (new JsonParser()).parse(jsonResp)
				.getAsJsonObject();

		if (jsonResponse.get("InfoDtls") != null) {
			
			String infoJson = jsonResponse.get("InfoDtls").getAsString();
			
			JsonArray infoArray = (new JsonParser()).parse(infoJson)
					.getAsJsonArray().get(0).getAsJsonObject()
					.getAsJsonArray("Desc");

			String infoErrorCode = "";
			String infoErrorDesc = "";
			for (int i = 0; i < infoArray.size(); i++) {
				JsonObject obj = infoArray.get(i).getAsJsonObject();
				if (!Strings.isNullOrEmpty(obj.toString()))
					infoErrorCode = infoErrorCode + (i + 1) + ") "
							+ obj.get("ErrorCode").getAsString() + " ";
				if (!Strings.isNullOrEmpty(obj.toString()))
					infoErrorDesc = infoErrorDesc + (i + 1) + ") "
							+ obj.get("ErrorMessage").getAsString() + " ";
			}

			resp.setInfoErrorCode(infoErrorCode);
			resp.setInfoErrorMessage(infoErrorDesc);
		}
		return resp;
	}

}
