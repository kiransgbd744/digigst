/**
 * 
 */
package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.business.dto.OtrdTransDocSoapHdrRq;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.services.ewb.EwbBusinessService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCommonUtil;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "EwbServiceV3")
@HandlerChain(file = "/handler-chain.xml")
public class ManageEwbVersionSoapController {

	private ERPReqRespLogHelper reqLogHelper;

	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;


	@WebMethod
	public @WebResult(name = "gene-ewb-resp") EwbResponseDto generateEwayBillv3(
			@WebParam(name = "ewb-req") OtrdTransDocSoapHdrRq req1) {
		EwbResponseDto response;
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		jsonSchemaValidatorHelper = StaticContextHolder.getBean("EinvJsonSchemaValidatorHelper",
				EinvJsonSchemaValidatorHelper.class);
		try {
			OutwardTransDocument req = BusinessCommonUtil.convertERPSoapReqToOutwardTransDoc(req1);
			String jsonString = gson.toJson(req);
			JsonObject inputJson = new JsonParser().parse(jsonString).getAsJsonObject();
			JsonObject reqJsonObj = new JsonObject();
			reqJsonObj.add("req", inputJson);
			String convertedJsonString = gson.toJson(reqJsonObj);
			errorList = jsonSchemaValidatorHelper.validateInptJson(convertedJsonString,
					"EwayBillNestedJsonSchema.json");
			if (errorList.isEmpty()) {

				try {
					if (LOGGER.isDebugEnabled()) {
						String reqJson = gson.toJson(req);
						LOGGER.debug("Gen Einv Soap request is {}", reqJson);
					}
				} catch (Exception e) {
					LOGGER.error("Error while parsing the Gen Einv Soap Request", e);
				}
				reqLogHelper.logAdditionalParams(req.getSgstin(), req.getDocType(), req.getDocNo(), true, true);
				EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
						EwbBusinessService.class);
				response = ewbService.generateEwb(req);
				if (!Strings.isNullOrEmpty(response.getEwayBillNo())) {
					nicStatus = true;
					reqLogHelper.logInvDtlsParams(null, null, response.getEwayBillNo(), response.getEwayBillDate());
				}
				String respStr = gson.toJson(response);
				reqLogHelper.updateResponsePayload(respStr, nicStatus);
				return response;
			}
			EwbResponseDto errorResp = generateErrorList(errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			response = new EwbResponseDto();
			response.setErrorCode(BusinessCriticalConstants.XMLSCHEMA_ERROR);
			response.setErrorDesc(respBody.toString());
			reqLogHelper.updateResponsePayload(respBody.toString(), nicStatus);
			return response;
		} catch (Exception e) {
			LOGGER.error("Exception While Processing Generate EWB Req Soap", e);
			response = new EwbResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}

	@WebMethod
	public @WebResult(name = "cancel-ewb-resp") CancelEwbResponseDto cancelEwayBillv3(
			@WebParam(name = "ewb-req-list") CancelEwbRequestListDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		CancelEwbResponseDto response;
		boolean nicStatus = false;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getCancelEwbReqDtoList().get(0).getGstin(), null, null, false, false);
			String ewbNo = req.getCancelEwbReqDtoList().get(0).getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.cancelEwb(req);
			if (response.getCancelDate() != null) {
				nicStatus = true;
			}
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return response;
		} catch (Exception e) {
			response = new CancelEwbResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	private EwbResponseDto generateErrorList(List<ErrorDetailsDto> errorDto) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errorDto.size(); i++) {

			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorField())) {
				errorCode = errorCode + (i + 1) + ") " + errorDto.get(i).getErrorField() + " ";
			}
			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorDesc()))
				errorDesc = errorDesc + (i + 1) + ") " + errorDto.get(i).getErrorDesc() + " ";
		}
		EwbResponseDto errResp = new EwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorDesc(errorDesc);
		return errResp;

	}

}
