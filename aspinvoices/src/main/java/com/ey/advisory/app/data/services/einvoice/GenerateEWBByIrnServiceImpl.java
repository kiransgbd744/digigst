package com.ey.advisory.app.data.services.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.data.services.ewb.EwbDbUtilService;
import com.ey.advisory.app.data.services.ewb.EwbRequestConverter;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.api.GenerateEWBByIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GenerateEWBByIrnServiceImpl")
public class GenerateEWBByIrnServiceImpl implements GenerateEWBByIrnService {

	@Autowired
	@Qualifier("DefaultEINVNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("GenerateEWBByIrnImpl")
	private GenerateEWBByIrn generateEWBByIrn;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("EinvoiceRepository")
	EinvoiceRepository einvoiceRepo;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	private EwbRequestConverter ewbRequestConverter;

	@Autowired
	@Qualifier("EwbDbUtilServiceImpl")
	private EwbDbUtilService ewbDbService;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Override
	public GenerateEWBByIrnResponseDto generateEwayIrnRequest(
			GenerateEWBByIrnNICReqDto req, OutwardTransDocument outwardDoc) {
		return processResponse(generateEWBByIrn.generateEWBByIrn(req),
				outwardDoc, req);
	}

	private GenerateEWBByIrnResponseDto processResponse(APIResponse response,
			OutwardTransDocument doc, GenerateEWBByIrnNICReqDto req) {
		GenerateEWBByIrnResponseDto genereateewayirnResp = null;
		EwayBillRequestDto ewayRequestDto = ewbRequestConverter.convert(doc);
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			genereateewayirnResp = gson.fromJson(jsonResp,
					GenerateEWBByIrnResponseDto.class);
			JsonObject jsonResponse = (new JsonParser()).parse(jsonResp)
					.getAsJsonObject();

			if (jsonResponse.get("InfoDtls") != null) {
				extractInfoDtlsandSetNICDis(genereateewayirnResp, jsonResponse,
						req);
			}
			reqLogHelper.updateResponsePayload(response.getResponse(), true);
		} else {
			List<APIError> apiErrorList = response.getErrors();
			genereateewayirnResp = createErrorResponse(apiErrorList);
			reqLogHelper.updateResponsePayload(apiErrorList.toString(), false);
		}
		EwbResponseDto createdEwbResp = createGenerateEwbResponse(response);
		ewbDbService.generateEwbDbUpdate(doc, ewayRequestDto, createdEwbResp,
				APIIdentifiers.GENERATE_EWBByIRN);
		return genereateewayirnResp;
	}

	private EwbResponseDto createGenerateEwbResponse(APIResponse response) {
		GenerateEWBByIrnResponseDto genEWBResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			genEWBResp = gson.fromJson(jsonResp,
					GenerateEWBByIrnResponseDto.class);
			EwbResponseDto ewbRespDto = new EwbResponseDto();
			ewbRespDto.setEwayBillDate(genEWBResp.getEwbDt());
			ewbRespDto.setEwayBillNo(String.valueOf(genEWBResp.getEwbNo()));
			ewbRespDto.setValidUpto(genEWBResp.getEwbValidTill());

			return ewbRespDto;
		} else {
			return createGenerateEwbErrorResponse(response.getErrors());
		}
	}

	private EwbResponseDto createGenerateEwbErrorResponse(
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
		EwbResponseDto errResp = new EwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorDesc(errorDesc);
		return errResp;

	}

	private GenerateEWBByIrnResponseDto createErrorResponse(
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
		GenerateEWBByIrnResponseDto resp = new GenerateEWBByIrnResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

	private void extractInfoDtlsandSetNICDis(
			GenerateEWBByIrnResponseDto genEwbIrnResp, JsonObject jsonResponse,
			GenerateEWBByIrnNICReqDto req) {

		String infoErrorCode = "";
		String infoErrorDesc = "";

		String infoJson = jsonResponse.get("InfoDtls").getAsString();

		String infCode = (new JsonParser()).parse(infoJson).getAsJsonArray()
				.get(0).getAsJsonObject().get("InfCd").getAsString();

		if ("EWBALERT".equalsIgnoreCase(infCode)
				|| "EWBPPD".equalsIgnoreCase(infCode)) {
			infoErrorDesc = (new JsonParser()).parse(infoJson).getAsJsonArray()
					.get(0).getAsJsonObject().get("Desc").getAsString();
		}
		else if ("EWBVEH".equalsIgnoreCase(infCode)) {
			infoErrorDesc = (new JsonParser()).parse(infoJson).getAsJsonArray()
					.get(0).getAsJsonObject().get("Desc").getAsString();
		} else if ("ADDNLNFO".equalsIgnoreCase(infCode)) {
			infoErrorDesc = (new JsonParser()).parse(infoJson).getAsJsonArray()
					.get(0).getAsJsonObject().get("Desc").getAsString();
		}

		genEwbIrnResp.setInfoErrorCode(infoErrorCode);
		genEwbIrnResp.setInfoErrorMessage(infoErrorDesc);

		extractDistanceandSetNICDis(infoErrorDesc, genEwbIrnResp, req);
	}

	private void extractDistanceandSetNICDis(String infoMessage,
			GenerateEWBByIrnResponseDto genEwbIrnResp,
			GenerateEWBByIrnNICReqDto req) {
		String nicDistance = "";
		int distanceIndex = infoMessage.lastIndexOf("distance:");
		if (distanceIndex != -1) {
			nicDistance = infoMessage.substring(distanceIndex);
		}
		if (!Strings.isNullOrEmpty(nicDistance)) {
			req.setNicDistance(nicDistance.replaceAll("[^0-9]", ""));
		}
	}

}
