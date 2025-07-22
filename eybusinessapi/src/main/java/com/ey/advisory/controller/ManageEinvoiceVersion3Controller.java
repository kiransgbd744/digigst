package com.ey.advisory.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.services.einvoice.CancelIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEWBByIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEinvoiceService;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.GenerateEWBByIrnRequest;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author SivaReddy
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v3")
public class ManageEinvoiceVersion3Controller {

	@Autowired
	@Qualifier("GenerateEinvoiceServiceImpl")
	private GenerateEinvoiceService generateEinvoiceService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	@Qualifier("GenerateEWBByIrnServiceImpl")
	private GenerateEWBByIrnService generateEWBIrnService;

	@Autowired
	@Qualifier("CancelIrnServiceImpl")
	private CancelIrnService cancelIrnService;

	@RequestMapping(value = "/generateEinvoice", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> generateBulkEinvoice(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		GenerateIrnResponseDto response;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			errorList = jsonSchemaValidatorHelper.validateInptJson(jsonString,
					"EinvoiceNestedJsonSchema.json");
			if (errorList.isEmpty()) {
				OutwardTransDocument hdr = gson.fromJson(
						requestObject.get("req"), OutwardTransDocument.class);
				reqLogHelper.logAdditionalParams(hdr.getSgstin(),
						hdr.getDocType(), hdr.getDocNo(), true, true);
				reqLogHelper.logAppMessage(hdr.getDocNo(), null, null,
						"Generate E Invoice Request Entered to the System");
				response = generateEinvoiceService.generateEinvRequest(hdr,
						true);
				if (LOGGER.isDebugEnabled()) {
					String msg = "End generateBulkEinvoice before returning response";
					LOGGER.debug(msg);
				}
				JsonElement respBody = gson.toJsonTree(response);
				if (Strings.isNullOrEmpty(response.getIrn()))
					jsonObj.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
				else {
					jsonObj.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					reqLogHelper.logInvDtlsParams(response.getIrn(),
							response.getAckDt(), response.getEwbNo(),
							response.getEwbDt());
					reqLogHelper.logAckNum(response.getAckNo());
					nicStatus = true;
				}
				jsonObj.add("resp", respBody);
				reqLogHelper.updateResponsePayload(jsonObj.toString(),
						nicStatus);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			GenerateIrnResponseDto errorResp = generateErrorList(errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));

			jsonObj.add("resp", respBody);
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (NumberFormatException ex) {
			LOGGER.error("Exception while Generating E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateIrnResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage("Request contains a field in string format"
					+ " which should be passed as numeric format");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			LOGGER.error("Exception while Generating E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateIrnResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Generating E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateIrnResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Generating E Invoice, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	@PostMapping(value = "/cancelEinvoice", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEinvoiceApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		CancelIrnERPResponseDto response;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			errorList = jsonSchemaValidatorHelper.validateInptJson(jsonString,
					"EinvoiceNestedJsonSchema.json");
			if (errorList.isEmpty()) {
				OutwardTransDocument hdr = gson.fromJson(
						requestObject.get("req"), OutwardTransDocument.class);
				reqLogHelper.logAdditionalParams(hdr.getSgstin(),
						hdr.getDocType(), hdr.getDocNo(), true, true);
				reqLogHelper.logAppMessage(hdr.getDocNo(), null, null,
						"Cancel E Invoice Request Entered to the System");
				reqLogHelper.logInvDtlsParams(hdr.getIrn(), null, null, null);
				CancelIrnReqList cancelIrnReq = convertOutwardToCancelReqDto(
						hdr);
				List<CancelIrnERPResponseDto> cancelResp = cancelIrnService
						.cancelEinvRequest(cancelIrnReq);
				if (LOGGER.isDebugEnabled()) {
					String msg = "End Cancel Einvoice Version before returning response";
					LOGGER.debug(msg);
				}
				JsonElement respBody = gson.toJsonTree(cancelResp);
				if (cancelResp.get(0).getCancelDate() != null) {
					jsonObj.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					nicStatus = true;
				} else {
					jsonObj.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
				}
				jsonObj.add("resp", respBody);
				reqLogHelper.updateResponsePayload(jsonObj.toString(),
						nicStatus);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			List<CancelIrnERPResponseDto> errorResp = createErrorResponse(
					errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp", respBody);
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (NumberFormatException ex) {
			LOGGER.error("Exception while Version Cancel E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			response = new CancelIrnERPResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage("Request contains a field in string format"
					+ " which should be passed as numeric format");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			LOGGER.error("Exception while Version Cancel E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			response = new CancelIrnERPResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Version Cancel E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			response = new CancelIrnERPResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Cancelling E Invoice, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	@PostMapping(value = "/generateEWBByIRN", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEWBByIrnapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return generateEWBByIrn(jsonString);
	}

	public ResponseEntity<String> generateEWBByIrn(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		try {
			GenerateEWBByIrnRequest req = gson.fromJson(jsonString,
					GenerateEWBByIrnRequest.class);
			reqLogHelper.logAdditionalParams(req.getGenerateEWBReq().getGstin(),
					null, null, false, false);
			reqLogHelper.logAppMessage(null, req.getGenerateEWBReq().getIrn(),
					null, "Generate Ewb By Irn Request Entered to the System");
			GenerateEWBByIrnResponseDto response = generateEWBIrnService
					.generateEwayIrnRequest(req);
			reqLogHelper.logInvDtlsParams(req.getGenerateEWBReq().getIrn(),
					null, response.getEwbNo(), response.getEwbDt());
			JsonElement respBody = gson.toJsonTree(response);
			if (Strings.isNullOrEmpty(response.getEwbNo()))
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
			else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			}
			jsonObj.add("resp", respBody);

			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of General Eway Bill Irn";
				LOGGER.debug(msg);
			}
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			GenerateEWBByIrnResponseDto response = new GenerateEWBByIrnResponseDto();
			LOGGER.error("Exception while Generating E-Invoice By IRN ", ex);
			JsonObject resp = new JsonObject();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			GenerateEWBByIrnResponseDto response = new GenerateEWBByIrnResponseDto();
			LOGGER.error("Exception while Generating E-Invoice By IRN ", ex);
			JsonObject resp = new JsonObject();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Generating E-Invoice By IRN, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	private GenerateIrnResponseDto generateErrorList(
			List<ErrorDetailsDto> errorDto) {

		String errorCode = "";
		String errorDesc = "";
		String infoErrorCode = "";
		String infoErrorDesc = "";
		for (int i = 0; i < errorDto.size(); i++) {

			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorField())) {
				errorCode = errorCode + (i + 1) + ") "
						+ errorDto.get(i).getErrorField() + " ";
			}
			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorDesc()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errorDto.get(i).getErrorDesc() + " ";
		}
		GenerateIrnResponseDto errResp = new GenerateIrnResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		errResp.setInfoErrorCode(infoErrorCode);
		errResp.setInfoErrorMessage(infoErrorDesc);
		return errResp;
	}

	private List<CancelIrnERPResponseDto> createErrorResponse(
			List<ErrorDetailsDto> errorDto) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errorDto.size(); i++) {

			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorField())) {
				errorCode = errorCode + (i + 1) + ") "
						+ errorDto.get(i).getErrorField() + " ";
			}
			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorDesc()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errorDto.get(i).getErrorDesc() + " ";
		}
		CancelIrnERPResponseDto resp = new CancelIrnERPResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return Arrays.asList(resp);
	}

	private CancelIrnReqList convertOutwardToCancelReqDto(
			OutwardTransDocument req) {

		CancelIrnReqDto reqDto = new CancelIrnReqDto();
		reqDto.setGstin(req.getSgstin());
		reqDto.setIrn(req.getIrn());
		reqDto.setCnlRem(req.getCancellationRemarks());
		reqDto.setCnlRsn(req.getCancellationReason());
		CancelIrnReqList reqList = new CancelIrnReqList();
		reqList.setReqList(Arrays.asList(reqDto));
		return reqList;
	}

}
