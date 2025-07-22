/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.OtrdTransDocSoapHdrRq;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.services.ewb.EwbBusinessService;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCommonUtil;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsReqDto;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetaiilsReqDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.GetEWBByConsignerSoapReqDto;
import com.ey.advisory.ewb.dto.GetEWBDetailsSoapReqDto;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleReqDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;
import com.ey.advisory.ewb.dto.RejectEwbRequestparamDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.RejectEwbSoapReqDto;
import com.ey.advisory.ewb.dto.RejectEwbSoapRespDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
@Component
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "EwbService")
@HandlerChain(file = "/handler-chain.xml")
public class ManageEwbSoapController {

	private ERPReqRespLogHelper reqLogHelper;

	@WebMethod
	public @WebResult(name = "cancel-ewb-resp") CancelEwbResponseDto cancelEwayBill(
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

	@WebMethod
	public @WebResult(name = "gene-ewb-resp") EwbResponseDto generateEwayBill(
			@WebParam(name = "ewb-req") OtrdTransDocSoapHdrRq req1) {
		EwbResponseDto response;
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		try {
			OutwardTransDocument req = BusinessCommonUtil.convertToOutwardTransDocWithNested(req1);
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
			reqLogHelper.logInvDtlsParams(null, null, response.getEwayBillNo(), response.getEwayBillDate());
			if (!Strings.isNullOrEmpty(response.getEwayBillNo())) {
				nicStatus = true;
			}
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
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
	public @WebResult(name = "partb-ewb-resp") UpdatePartBEwbResponseDto updatePartBEwayBill(
			@WebParam(name = "ewb-req") UpdatePartBEwbRequestDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		UpdatePartBEwbResponseDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			String ewbNo = req.getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.updateEwbPartB(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Cancel EWB Soap Request", e);
			response = new UpdatePartBEwbResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "consol-ewb-resp") ConsolidateEWBResponseDto consolidateEwayBill(
			@WebParam(name = "ewb-req") ConsolidateEWBReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		ConsolidateEWBResponseDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			response = ewbService.consolidateEWB(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new ConsolidateEWBResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "extend-ewb-resp") ExtendEWBResponseDto extendEwbValidity(
			@WebParam(name = "ewb-req") ExtendEWBReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		ExtendEWBResponseDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			String ewbNo = req.getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.extendEWB(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new ExtendEWBResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "ut-ewb-rsp") UpdateEWBTransporterRespDto updateEwbTransporter(
			@WebParam(name = "ewb-req") UpdateEWBTransporterReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
		UpdateEWBTransporterRespDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			String ewbNo = req.getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.updateTransporter(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new UpdateEWBTransporterRespDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "add-mv-ewb-rsp") AddMultiVehicleDetailsRespDto addMultiVehicle(
			@WebParam(name = "ewb-req") AddMultiVehicleDetailsReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
		AddMultiVehicleDetailsRespDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			String ewbNo = req.getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.addMultiVehicles(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new AddMultiVehicleDetailsRespDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "chng-mv-ewb-rsp") ChangeMultiVehicleDetailsRespDto changeMultiVehicle(
			@WebParam(name = "ewb-req") ChangeMultiVehicleDetaiilsReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
		ChangeMultiVehicleDetailsRespDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			String ewbNo = req.getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.changeMultiVehicles(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new ChangeMultiVehicleDetailsRespDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "init-mv-ewb-rsp") InitiateMultiVehicleRespDto initiateMultiVehicle(
			@WebParam(name = "ewb-req") InitiateMultiVehicleReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
		InitiateMultiVehicleRespDto response;
		try {
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			String ewbNo = req.getEwbNo();
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.initiateMultiVehicles(req, req.getGstin());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new InitiateMultiVehicleRespDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}

	}

	@WebMethod
	public @WebResult(name = "reject-ewb-resp") RejectEwbSoapRespDto rejectEwb(
			@WebParam(name = "rejectewb-req") RejectEwbSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		RejectEwbResponseDto response;
		boolean nicStatus = false;
		try {
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false, false);
			reqLogHelper.logInvDtlsParams(null, null, req.getEwbNo(), null);
			RejectEwbReqDto reqObj = new RejectEwbReqDto();
			reqObj.setGstin(req.getGstin());
			reqObj.setEwbNo(req.getEwbNo());
			RejectEwbRequestparamDto rejectReq = new RejectEwbRequestparamDto();
			rejectReq.setRejectEwbReqDto(reqObj);
			rejectReq.setIdToken(req.getIdToken());
			EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl",
					EwbBusinessService.class);
			response = ewbService.rejectEwb(rejectReq);
			if (!Strings.isNullOrEmpty(response.getEwayBillNo())) {
				nicStatus = true;
			}
			RejectEwbSoapRespDto respObj = new RejectEwbSoapRespDto();
			respObj.setEwayBillNo(response.getEwayBillNo());
			respObj.setEwbRejectedDate(response.getEwbRejectedDate());
			respObj.setErrorMessage(response.getErrorMessage());
			respObj.setErrorCode(response.getErrorCode());
			String respStr = gson.toJson(respObj);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return respObj;
		} catch (Exception e) {
			RejectEwbSoapRespDto soapResponse = new RejectEwbSoapRespDto();
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

	@WebMethod
	public @WebResult(name = "get-ewbdetails-resp") GetEwbResponseDto getEwbDetails(
			@WebParam(name = "getewbdetails-req") GetEWBDetailsSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl", EwbBusinessService.class);

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GetEwbResponseDto response;
		try {
			String ewbNo = req.getEwbNo();
			String gstin = req.getGstin();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String reqJson = gson.toJson(req);
					LOGGER.debug("Gen EWB Soap request is {}", reqJson);
				}
			} catch (Exception e) {
				LOGGER.error("Error while parsing the Get EWB Soap Request ", e);
			}
			if (Strings.isNullOrEmpty(ewbNo) || Strings.isNullOrEmpty(gstin))
				throw new AppException("ewbNo and gstin Both are mandatory.");

			response = ewbService.getEWB(ewbNo, gstin);
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, response.getEwayBillDate());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, true);
			return response;
		} catch (Exception e) {
			response = new GetEwbResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}

	@WebMethod
	public @WebResult(name = "get-ewbbyconsigner-resp") GetEwbResponseDto getEwbByConsigner(
			@WebParam(name = "getewbbyconsigner-req") GetEWBByConsignerSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper", ERPReqRespLogHelper.class);
		EwbBusinessService ewbService = StaticContextHolder.getBean("EwbBusinessServiceImpl", EwbBusinessService.class);

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GetEwbResponseDto response;
		boolean nicStatus = false;
		try {
			String docNo = req.getDocNo();
			String docType = req.getDocType();
			String gstin = req.getGstin();
			reqLogHelper.logAdditionalParams(gstin, docType, docNo, true, true);

			try {
				if (LOGGER.isDebugEnabled()) {
					String reqJson = gson.toJson(req);
					LOGGER.debug("Gen EWB By Consigner Soap request is {}", reqJson);
				}
			} catch (Exception e) {
				LOGGER.error("Error while parsing the Gen EWB by consigner Soap Request ", e);
			}
			if (Strings.isNullOrEmpty(docType) || Strings.isNullOrEmpty(docNo))
				throw new AppException("docType and docNo Both are mandatory.");
			response = ewbService.getEwbByConsigner(gstin, docType, docNo);
			reqLogHelper.logInvDtlsParams(null, null, response.getEwbNo(), response.getEwayBillDate());
			JsonObject resp = new JsonObject();
			if (response.getEwbNo() != null) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return response;
		} catch (Exception e) {
			response = new GetEwbResponseDto();
			response.setErrorCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return response;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}
}
