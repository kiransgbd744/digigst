/**
 * 
 */
package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.OtrdTransDocSoapHdrRq;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.services.einvoice.CancelIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEWBByIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEinvoiceService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCommonUtil;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.CancelIrnSoapRespDto;
import com.ey.advisory.einv.dto.GenEWBByIrnDispERPReqDto;
import com.ey.advisory.einv.dto.GenEWBByIrnExpShpERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIRNSoapReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnRequest;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnSoapRespDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
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
 * @author Arun.KA
 *
 */

@Component
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "EinvServiceV3")
@HandlerChain(file = "/handler-chain.xml")
public class ManageEinvVersionSoapController {

	private ERPReqRespLogHelper reqLogHelper;

	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	private BusinessCommonUtil businessCommUtil;

	@WebMethod
	public @WebResult(name = "gene-einv-resp") GenerateIrnResponseDto generateEinvoicev3(
			@WebParam(name = "einv-req") OtrdTransDocSoapHdrRq req1) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);

		jsonSchemaValidatorHelper = StaticContextHolder.getBean("EinvJsonSchemaValidatorHelper",
				EinvJsonSchemaValidatorHelper.class);

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto response;
		boolean nicStatus = false;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		try {
			OutwardTransDocument req = BusinessCommonUtil.convertERPSoapReqToOutwardTransDoc(req1);
			String jsonString = gson.toJson(req);
			JsonObject inputJson = new JsonParser().parse(jsonString).getAsJsonObject();
			JsonObject reqJsonObj = new JsonObject();
			reqJsonObj.add("req", inputJson);
			String convertedJsonString = gson.toJson(reqJsonObj);
			errorList = jsonSchemaValidatorHelper.validateInptJson(convertedJsonString,
					"EinvoiceNestedJsonSchema.json");

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
				GenerateEinvoiceService generateEinvoice = StaticContextHolder.getBean("GenerateEinvoiceServiceImpl",
						GenerateEinvoiceService.class);
				response = generateEinvoice.generateEinvRequest(req, true);
				reqLogHelper.logInvDtlsParams(response.getIrn(), response.getAckDt(), response.getEwbNo(),
						response.getEwbDt());
				reqLogHelper.logAckNum(response.getAckNo());
				if (!Strings.isNullOrEmpty(response.getIrn())) {
					nicStatus = true;
				}
				String respStr = gson.toJson(response);
				reqLogHelper.updateResponsePayload(respStr, nicStatus);
				return response;
			}
			GenerateIrnResponseDto errorResp = generateErrorList(errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			response = new GenerateIrnResponseDto();
			response.setErrorCode(respBody.getAsJsonObject().get("errorCode").getAsString());
			response.setErrorMessage(respBody.getAsJsonObject().get("errorMessage").getAsString());
			reqLogHelper.updateResponsePayload(respBody.toString(), nicStatus);
			return response;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene Inv Soap Request", e);
			response = new GenerateIrnResponseDto();
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

	@WebMethod
	public @WebResult(name = "cancel-einv-resp") CancelIrnSoapRespDto cancelEinvoicev3(
			@WebParam(name = "einv-req-list") OtrdTransDocSoapHdrRq req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		jsonSchemaValidatorHelper = StaticContextHolder.getBean("EinvJsonSchemaValidatorHelper",
				EinvJsonSchemaValidatorHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		CancelIrnSoapRespDto respObj;
		try {
			OutwardTransDocument hdrReq = BusinessCommonUtil.convertERPSoapReqToOutwardTransDoc(req);
			String jsonString = gson.toJson(hdrReq);
			JsonObject inputJson = new JsonParser().parse(jsonString).getAsJsonObject();
			JsonObject reqJsonObj = new JsonObject();
			reqJsonObj.add("req", inputJson);
			String convertedJsonString = gson.toJson(reqJsonObj);
			errorList = jsonSchemaValidatorHelper.validateInptJson(convertedJsonString,
					"EinvoiceNestedJsonSchema.json");
			if (errorList.isEmpty()) {
				try {
					if (LOGGER.isDebugEnabled()) {
						String reqJson = gson.toJson(hdrReq);
						LOGGER.debug("cancel Einv Soap request is {}", reqJson);
					}
				} catch (Exception e) {
					LOGGER.error("Error while parsing the cancel Einv Soap Request", e);
				}

				CancelIrnService cancelIrn = StaticContextHolder.getBean("CancelIrnServiceImpl",
						CancelIrnService.class);
				CancelIrnReqList cancelIrnReq = convertOutwardToCancelReqDto(hdrReq);
				reqLogHelper.logAdditionalParams(req.getSgstin(), req.getDocType(), req.getDocNo(), true, true);
				reqLogHelper.logInvDtlsParams(req.getIrn(), null, null, null);

				cancelIrnReq.setIdToken(req.getIdToken());
				List<CancelIrnERPResponseDto> respList = cancelIrn.cancelEinvRequest(cancelIrnReq);
				if (!Strings.isNullOrEmpty(respList.get(0).getIrn())) {
					nicStatus = true;
				}
				CancelIrnERPResponseDto dto = respList.get(0);
				respObj = new CancelIrnSoapRespDto();
				respObj.setIrn(dto.getIrn());
				respObj.setCancelDate(dto.getCancelDate());
				respObj.setErrorMessage(dto.getErrorMessage());
				respObj.setErrorCode(dto.getErrorCode());
				String respStr = gson.toJson(respObj);
				reqLogHelper.updateResponsePayload(respStr, nicStatus);
				return respObj;
			}
			CancelIrnSoapRespDto errorResp = createCancelErrorResponse(errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			respObj = new CancelIrnSoapRespDto();
			respObj.setErrorCode(respBody.getAsJsonObject().get("ErrorCode").getAsString());
			respObj.setErrorMessage(respBody.getAsJsonObject().get("ErrorMessage").getAsString());
			reqLogHelper.updateResponsePayload(respBody.toString(), nicStatus);
			return respObj;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Cancel Inv Soap Request", e);
			CancelIrnSoapRespDto soapResp = new CancelIrnSoapRespDto();
			soapResp.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			soapResp.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(soapResp);
			reqLogHelper.updateResponsePayload(respStr, false);
			return soapResp;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}

	@WebMethod
	public @WebResult(name = "gene-ewbbyirn-resp") GenerateEWBByIrnSoapRespDto generateEWBByIrnv3(
			@WebParam(name = "genewbbyirn-req") GenerateEWBByIRNSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateEWBByIrnResponseDto response;
		boolean nicStatus = false;
		try {
			GenerateEWBByIrnService generateEWBByIrn = StaticContextHolder.getBean("GenerateEWBByIrnServiceImpl",
					GenerateEWBByIrnService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);

			GenerateEWBByIrnERPReqDto reqObj = new GenerateEWBByIrnERPReqDto();
			reqObj.setGstin(req.getGstin());
			reqObj.setDistance(req.getDistance());
			reqObj.setIrn(req.getIrn());
			reqObj.setTransMode(req.getTransMode());
			reqObj.setTrnDocDt(req.getTrnDocDt());
			reqObj.setTrnDocNo(req.getTrnDocNo());
			reqObj.setTransId(req.getTransId());
			reqObj.setTransName(req.getTransName());
			reqObj.setVehNo(req.getVehNo());
			reqObj.setVehType(req.getVehType());
			reqObj.setCustPincd(req.getCustPincd());
			reqObj.setDispatcherPincd(req.getDispatcherPincd());
			reqObj.setShipToPincd(req.getShipToPincd());
			reqObj.setSuppPincd(req.getSuppPincd());
			reqObj.setDocCategory(req.getDocCategory());
			reqObj.setSupplyType(req.getSupplyType());

			if (req.getDispDtls() != null) {

				GenEWBByIrnDispERPReqDto erpReqdispDtls = new GenEWBByIrnDispERPReqDto();
				erpReqdispDtls.setAddr1(req.getDispDtls().getAddr1());
				if (!Strings.isNullOrEmpty(req.getDispDtls().getAddr2())) {
					erpReqdispDtls.setAddr2(req.getDispDtls().getAddr2());
				}
				erpReqdispDtls.setLoc(req.getDispDtls().getLoc());
				erpReqdispDtls.setPin(req.getDispDtls().getPin());
				erpReqdispDtls.setStcd(req.getDispDtls().getStcd());
				erpReqdispDtls.setNm(req.getDispDtls().getNm());
				reqObj.setDispDtls(erpReqdispDtls);
			}
			if (req.getExpShipDtls() != null) {
				GenEWBByIrnExpShpERPReqDto erpReqExpShpDtls = new GenEWBByIrnExpShpERPReqDto();
				erpReqExpShpDtls.setAddr1(req.getExpShipDtls().getAddr1());
				if (!Strings.isNullOrEmpty(req.getExpShipDtls().getAddr2())) {
					erpReqExpShpDtls.setAddr2(req.getExpShipDtls().getAddr2());
				}
				erpReqExpShpDtls.setLoc(req.getExpShipDtls().getLoc());
				erpReqExpShpDtls.setPin(req.getExpShipDtls().getPin());
				erpReqExpShpDtls.setStcd(req.getExpShipDtls().getStcd());
				reqObj.setExpShipDtls(erpReqExpShpDtls);

			}

			GenerateEWBByIrnRequest geneReq = new GenerateEWBByIrnRequest();
			geneReq.setGenerateEWBReq(reqObj);
			geneReq.setIdToken(req.getIdToken());
			response = generateEWBByIrn.generateEwayIrnRequest(geneReq);
			reqLogHelper.logInvDtlsParams(req.getIrn(), null, response.getEwbNo(), response.getEwbDt());
			GenerateEWBByIrnSoapRespDto respObj = new GenerateEWBByIrnSoapRespDto();
			respObj.setEwbDt(response.getEwbDt());
			respObj.setEwbNo(response.getEwbNo());
			respObj.setEwbValidTill(response.getEwbValidTill());
			if (!Strings.isNullOrEmpty(response.getNicDistance())) {
				respObj.setNicDistance(response.getNicDistance());
			}
			respObj.setErrorMessage(response.getErrorMessage());
			respObj.setErrorCode(response.getErrorCode());
			String respStr = gson.toJson(respObj);
			if (!Strings.isNullOrEmpty(response.getEwbNo())) {
				nicStatus = true;
			}
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return respObj;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene EWB By Irn Soap Request", e);
			GenerateEWBByIrnSoapRespDto soapResponse = new GenerateEWBByIrnSoapRespDto();
			soapResponse.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			soapResponse.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(soapResponse);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return soapResponse;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}

	private GenerateIrnResponseDto generateErrorList(List<ErrorDetailsDto> errorDto) {

		String errorCode = "";
		String errorDesc = "";
		String infoErrorCode = "";
		String infoErrorDesc = "";
		for (int i = 0; i < errorDto.size(); i++) {

			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorField())) {
				errorCode = errorCode + (i + 1) + ") " + errorDto.get(i).getErrorField() + " ";
			}
			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorDesc()))
				errorDesc = errorDesc + (i + 1) + ") " + errorDto.get(i).getErrorDesc() + " ";
		}
		GenerateIrnResponseDto errResp = new GenerateIrnResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		errResp.setInfoErrorCode(infoErrorCode);
		errResp.setInfoErrorMessage(infoErrorDesc);
		return errResp;
	}

	private CancelIrnReqList convertOutwardToCancelReqDto(OutwardTransDocument req) {

		CancelIrnReqDto reqDto = new CancelIrnReqDto();
		reqDto.setGstin(req.getSgstin());
		reqDto.setIrn(req.getIrn());
		reqDto.setCnlRem(req.getCancellationRemarks());
		reqDto.setCnlRsn(req.getCancellationReason());
		CancelIrnReqList reqList = new CancelIrnReqList();
		reqList.setReqList(Arrays.asList(reqDto));
		return reqList;
	}

	private CancelIrnSoapRespDto createCancelErrorResponse(List<ErrorDetailsDto> errorDto) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errorDto.size(); i++) {

			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorField())) {
				errorCode = errorCode + (i + 1) + ") " + errorDto.get(i).getErrorField() + " ";
			}
			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorDesc()))
				errorDesc = errorDesc + (i + 1) + ") " + errorDto.get(i).getErrorDesc() + " ";
		}
		CancelIrnSoapRespDto resp = new CancelIrnSoapRespDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

}
