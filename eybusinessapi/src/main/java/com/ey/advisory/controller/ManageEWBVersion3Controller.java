/**
 * 
 */
package com.ey.advisory.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.services.ewb.EwbBusinessService;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCommonUtil;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v3")
public class ManageEWBVersion3Controller {

	@Autowired
	@Qualifier("EwbBusinessServiceImpl")
	private EwbBusinessService ewbService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	private BusinessCommonUtil businessCommUtil;

	@PostMapping(value = "/generateEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEwayBill(
			@RequestBody String jsonString, HttpServletRequest request) {
		PerfUtil.logEventToFile("GENERATE_EWB", "GENERATE_EWB_BEGIN",
				"ManageEWBController", "generateEwayBill", "");
		boolean nicStatus = false;
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		EwbResponseDto response;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject resp = new JsonObject();
			errorList = jsonSchemaValidatorHelper.validateInptJson(jsonString,
					"EwayBillNestedJsonSchema.json");
			if (errorList.isEmpty()) {
				OutwardTransDocument req = gson.fromJson(obj.get("req"),
						OutwardTransDocument.class);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request Json {}", req);
				}
				reqLogHelper.logAdditionalParams(req.getSgstin(),
						req.getDocType(), req.getDocNo(), true, true);
				reqLogHelper.logAppMessage(req.getDocNo(), null, null,
						"Generate EwayBill Request Entered to the System");
				response = ewbService.generateEwb(req);
				reqLogHelper.logInvDtlsParams(null, null,
						response.getEwayBillNo(), response.getEwayBillDate());
				if (Strings.isNullOrEmpty(response.getEwayBillNo()))
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
				else {
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					nicStatus = true;
				}
				resp.add("resp", ewbGson.toJsonTree(response));
				reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			EwbResponseDto errorResp = generateErrorList(errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (NumberFormatException ex) {
			JsonObject resp = new JsonObject();
			response = new EwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc("Request contains a field in string format"
					+ " which should be passed as numeric format");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(response));
			LOGGER.error(
					"Exception in Generate ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new EwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(response));
			LOGGER.error(
					"Exception in updating ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new EwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorDesc(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorDesc(
						"Error While Generating Eway bill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(response));
			LOGGER.error(
					"Exception in updating ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();
		}
	}

	@PostMapping(value = "/cancelEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEwayBill(@RequestBody String jsonString,
			HttpServletRequest request) {
		PerfUtil.logEventToFile("CANCEL_EWB", "CANCEL_EWB_BEGIN",
				"ManageEWBController", "cancelEwayBill", "");
		boolean nicStatus = false;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		CancelEwbResponseDto response;
		try {
			CancelEwbRequestListDto dto = gson.fromJson(jsonString,
					CancelEwbRequestListDto.class);
			String gstin = dto.getCancelEwbReqDtoList().get(0).getGstin();
			String ewbNo = dto.getCancelEwbReqDtoList().get(0).getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			reqLogHelper.logAppMessage(ewbNo, null, null,
					"Cancel EwayBill Request Entered to the System");
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			if (Strings.isNullOrEmpty(
					dto.getCancelEwbReqDtoList().get(0).getGstin())) {
				String errMsg = String.format(
						"Gstin is Mandatory while cancelling Ewaybill %s",
						dto.getCancelEwbReqDtoList().get(0).getEwbNo());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			response = ewbService.cancelEwb(dto);
			JsonObject resp = new JsonObject();
			if (response.getCancelDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			LOGGER.error("Exception in cancelling eway bill with request ", ex);
			response = new CancelEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			LOGGER.error("Exception in cancelling eway bill with request ", ex);
			response = new CancelEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Cancelling Eway bill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();
		}
	}

	private EwbResponseDto generateErrorList(List<ErrorDetailsDto> errorDto) {
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
		EwbResponseDto errResp = new EwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorDesc(errorDesc);
		return errResp;

	}

}
