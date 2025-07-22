/**
 * 
 */
package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.OtrdTransDocSoapHdrRq;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.services.einvoice.CancelEWBBillService;
import com.ey.advisory.app.services.einvoice.CancelIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEWBByIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEinvoiceService;
import com.ey.advisory.app.services.einvoice.GetEInvDetailsService;
import com.ey.advisory.app.services.einvoice.GetSyncGSTINDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCommonUtil;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.dto.CancelEWBBillERPReqDto;
import com.ey.advisory.einv.dto.CancelEWBBillRequest;
import com.ey.advisory.einv.dto.CancelEWBBillResponseDto;
import com.ey.advisory.einv.dto.CancelEWBBillSoapReqDto;
import com.ey.advisory.einv.dto.CancelEWBBillSoapRespDto;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.CancelIrnSoapReqDto;
import com.ey.advisory.einv.dto.CancelIrnSoapRespDto;
import com.ey.advisory.einv.dto.GenerateEWBByIRNSoapReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnRequest;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnSoapRespDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.GetEInvDetailsSoapReqDto;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailSoapReqList;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailsERPRespDto;
import com.google.gson.Gson;

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
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "EinvService")
@HandlerChain(file = "/handler-chain.xml")
public class ManageEinvSoapController {

	private ERPReqRespLogHelper reqLogHelper;

	@WebMethod
	public @WebResult(name = "gene-einv-resp") GenerateIrnResponseDto generateEinvoice(
			@WebParam(name = "einv-req") OtrdTransDocSoapHdrRq req1) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto response;
		boolean nicStatus = false;
		try {
			reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper",
					ERPReqRespLogHelper.class);
			OutwardTransDocument req = BusinessCommonUtil
					.convertToOutwardTransDocWithNested(req1);
			try {
				if (LOGGER.isDebugEnabled()) {
					String reqJson = gson.toJson(req);
					LOGGER.debug("Gen Einv Soap request is {}", reqJson);
				}
			} catch (Exception e) {
				LOGGER.error("Error while parsing the Gen Einv Soap Request",
						e);
			}
			GenerateEinvoiceService generateEinvoice = StaticContextHolder
					.getBean("GenerateEinvoiceServiceImpl",
							GenerateEinvoiceService.class);
			reqLogHelper.logAdditionalParams(req.getSgstin(), req.getDocType(),
					req.getDocNo(), true, true);
			response = generateEinvoice.generateEinvRequest(req, true);

			if (!Strings.isNullOrEmpty(response.getIrn())) {
				nicStatus = true;
				reqLogHelper.logInvDtlsParams(response.getIrn(),
						response.getAckDt(), response.getEwbNo(),
						response.getEwbDt());
				reqLogHelper.logAckNum(response.getAckNo());
			}
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return response;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene Inv Soap Request", e);
			response = new GenerateIrnResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
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
	public @WebResult(name = "cancel-einv-resp") CancelIrnSoapRespDto cancelEinvoice(
			@WebParam(name = "einv-req-list") CancelIrnSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper",
					ERPReqRespLogHelper.class);
			CancelIrnService cancelIrn = StaticContextHolder
					.getBean("CancelIrnServiceImpl", CancelIrnService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false,
					false);
			reqLogHelper.logInvDtlsParams(req.getIrn(), null, null, null);

			List<CancelIrnReqDto> list = new ArrayList<>();
			CancelIrnReqDto reqObj = new CancelIrnReqDto();
			reqObj.setIrn(req.getIrn());
			reqObj.setGstin(req.getGstin());
			reqObj.setCnlRsn(req.getCnlRsn());
			reqObj.setCnlRem(req.getCnlRem());
			list.add(reqObj);
			CancelIrnReqList reqList = new CancelIrnReqList();
			reqList.setReqList((list));
			reqList.setIdToken(req.getIdToken());
			List<CancelIrnERPResponseDto> respList = cancelIrn
					.cancelEinvRequest(reqList);
			CancelIrnERPResponseDto dto = respList.get(0);
			CancelIrnSoapRespDto respObj = new CancelIrnSoapRespDto();
			respObj.setIrn(dto.getIrn());
			respObj.setCancelDate(dto.getCancelDate());
			respObj.setErrorMessage(dto.getErrorMessage());
			respObj.setErrorCode(dto.getErrorCode());
			String respStr = gson.toJson(respObj);
			reqLogHelper.updateResponsePayload(respStr, true);
			return respObj;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Cancel Inv Soap Request", e);
			CancelIrnSoapRespDto response = new CancelIrnSoapRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
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
	public @WebResult(name = "gene-ewbbyirn-resp") GenerateEWBByIrnSoapRespDto generateEWBByIrn(
			@WebParam(name = "genewbbyirn-req") GenerateEWBByIRNSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateEWBByIrnResponseDto response;
		boolean nicStatus = false;
		try {
			reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper",
					ERPReqRespLogHelper.class);
			GenerateEWBByIrnService generateEWBByIrn = StaticContextHolder
					.getBean("GenerateEWBByIrnServiceImpl",
							GenerateEWBByIrnService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false,
					false);
			GenerateEWBByIrnERPReqDto reqObj = BusinessCommonUtil
					.convertERPSoapReqToGenIrn(req);
			GenerateEWBByIrnRequest geneReq = new GenerateEWBByIrnRequest();
			geneReq.setGenerateEWBReq(reqObj);
			geneReq.setIdToken(req.getIdToken());
			response = generateEWBByIrn.generateEwayIrnRequest(geneReq);
			reqLogHelper.logInvDtlsParams(req.getIrn(), null,
					response.getEwbNo(), response.getEwbDt());
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
			LOGGER.error("Error while parsing the Gene EWB By Irn Soap Request",
					e);
			GenerateEWBByIrnSoapRespDto soapResponse = new GenerateEWBByIrnSoapRespDto();
			soapResponse.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
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
	public @WebResult(name = "cance-einvewaybill-resp") CancelEWBBillSoapRespDto cancelEWBBill(
			@WebParam(name = "caneinvewaybill-req") CancelEWBBillSoapReqDto req) {

		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper",
				ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		CancelEWBBillResponseDto response;
		try {
			CancelEWBBillService cancelEWBBill = StaticContextHolder.getBean(
					"CancelEWBBillServiceImpl", CancelEWBBillService.class);
			reqLogHelper.logAdditionalParams(req.getGstin(), null, null, false,
					false);
			reqLogHelper.logInvDtlsParams(null, null, req.getEwbNo(), null);
			CancelEWBBillERPReqDto reqObj = new CancelEWBBillERPReqDto();
			reqObj.setGstin(req.getGstin());
			reqObj.setEwbNo(req.getEwbNo());
			reqObj.setCancelRmrk(req.getCancelRmrk());
			reqObj.setCancelRsnCode(req.getCancelRsnCode());
			CancelEWBBillRequest canelReq = new CancelEWBBillRequest();
			canelReq.setCancelEwbBillERPreqdto(reqObj);
			canelReq.setIdToken(req.getIdToken());
			response = cancelEWBBill.cancelEwaybillRequest(canelReq);
			CancelEWBBillSoapRespDto respObj = new CancelEWBBillSoapRespDto();
			respObj.setEwayBillNo(response.getEwayBillNo());
			respObj.setCancelDate(response.getCancelDate());
			respObj.setErrorMessage(response.getErrorMessage());
			respObj.setErrorCode(response.getErrorCode());
			String respStr = gson.toJson(respObj);
			reqLogHelper.updateResponsePayload(respStr, true);
			return respObj;
		} catch (Exception e) {
			CancelEWBBillSoapRespDto soapResponse = new CancelEWBBillSoapRespDto();
			soapResponse.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			soapResponse.setErrorMessage(e.getMessage());
			String respStr = gson.toJson(soapResponse);
			reqLogHelper.updateResponsePayload(respStr, false);
			return soapResponse;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}

	@WebMethod
	public @WebResult(name = "get-einvdetails-resp") GenerateIrnResponseDto getEInvDetails(
			@WebParam(name = "geteinvdetails-req") GetEInvDetailsSoapReqDto req) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper",
				ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto response;
		boolean nicStatus = false;
		try {

			String irn = req.getIrn();
			String gstin = req.getGstin();

			try {
				if (LOGGER.isDebugEnabled()) {
					String reqJson = gson.toJson(req);
					LOGGER.debug("Gen Einv Soap request is {}", reqJson);
				}
			} catch (Exception e) {
				LOGGER.error("Error while parsing the Gen Einv Soap Request");
			}
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			GetEInvDetailsService getEInvDetails = StaticContextHolder.getBean(
					"GetEInvDetailsServiceImpl", GetEInvDetailsService.class);
			response = getEInvDetails.getEInvDetails(irn, gstin, null);
			if (!Strings.isNullOrEmpty(response.getIrn())) {
				nicStatus = true;
			}
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return response;
		} catch (Exception e) {
			response = new GenerateIrnResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
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
	public @WebResult(name = "get-syncgstindetails-resp") List<GetSyncGSTINDetailsERPRespDto> getSyncGSTINDetails(
			@WebParam(name = "getsyncgstindetails-req") GetSyncGSTINDetailSoapReqList reqList) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean("ERPReqRespLogHelper",
				ERPReqRespLogHelper.class);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GetSyncGSTINDetailsERPRespDto response;
		boolean nicStatus = false;
		List<GetSyncGSTINDetailsERPRespDto> respList = new ArrayList<>();
		try {
			if (reqList != null && reqList.getReqDto() != null && !reqList.getReqDto().isEmpty()) {
				 
			for (int i = 0; i < reqList.getReqDto().size(); i++) {
				String syncGstin = reqList.getReqDto().get(i).getSyncGstin();
				String gstin = reqList.getReqDto().get(i).getGstin();

				if (LOGGER.isDebugEnabled()) {
					String reqJson = gson.toJson(reqList);
					LOGGER.debug("Get Sync GSTIN Details Soap request is {}",
							reqJson);
				}
				reqLogHelper.logAdditionalParams(gstin, null, null, false,
						false);

				GetSyncGSTINDetailsService getSyncGSTINDetailsService = StaticContextHolder
						.getBean("GetSyncGSTINDetailsServiceImpl",
								GetSyncGSTINDetailsService.class);
				response = getSyncGSTINDetailsService.getSyncDetails(syncGstin,
						gstin);
				if (!Strings.isNullOrEmpty(response.getGstin())) {
					nicStatus = true;
				}
				respList.add(response);
			}
			}
			String respStr = gson.toJson(respList);
			reqLogHelper.updateResponsePayload(respStr, nicStatus);
			return respList;
		} catch (Exception e) {
			response = new GetSyncGSTINDetailsERPRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(e.getMessage());
			respList.add(response);
			String respStr = gson.toJson(response);
			reqLogHelper.updateResponsePayload(respStr, false);
			return respList;
		} finally {
			reqLogHelper.saveLogEntity();

			TenantContext.clearTenant();
		}
	}
}
