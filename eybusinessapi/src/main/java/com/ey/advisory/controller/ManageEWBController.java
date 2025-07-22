/**
 * 
 */
package com.ey.advisory.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.ey.advisory.common.UUIDContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
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
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleReqDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.RejectEwbRequestparamDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;
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
public class ManageEWBController {

	@Autowired
	@Qualifier("EwbBusinessServiceImpl")
	private EwbBusinessService ewbService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	private BusinessCommonUtil businessCommUtil;

	@PostMapping(value = "/api/generateEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEwayBill(
			@RequestBody String jsonString, HttpServletRequest request) {
		UUIDContext.setUniqueID(UUID.randomUUID().toString());
		PerfUtil.logEventToFile("GENERATE_EWB", "GENERATE_EWB_BEGIN",
				"ManageEWBController", "generateEwayBill", "");
		boolean nicStatus = false;
		boolean isSchemaValidationReq = false;
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		EwbResponseDto response;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		try {
			JsonObject resp = new JsonObject();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			OutwardTransDocument req = gson.fromJson(obj.get("req"),
					OutwardTransDocument.class);
			reqLogHelper.logAdditionalParams(req.getSgstin(), req.getDocType(),
					req.getDocNo(), true, true);
			reqLogHelper.logAppMessage(req.getDocNo(), null, null,
					"Generate EwayBill Request Entered to the System");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", req);
			}
			if (isSchemaValidationReq)
				errorList = jsonSchemaValidatorHelper.validateInptJson(
						jsonString, "EwayBillNestedJsonSchema.json");

			if (errorList.isEmpty()) {
				response = ewbService.generateEwb(req);
				PerfUtil.logEventToFile("GENERATE_EWB", "GENERATE_EWB_BEGIN",
						"ManageEWBController", "GEN_EWB_BEFORE_RESP_TO_CALLER", "", true);
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
				PerfUtil.logEventToFile("GENERATE_EWB", "GENERATE_EWB_BEGIN",
						"ManageEWBController", "GEN_EWB_ABOUT_RESP_TO_CALLER", "", true);
				
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			JsonElement respBody = gson.toJsonTree(errorList);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E",
					"Error occured while validating json schema")));
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

	@PostMapping(value = "/api/cancelEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEwayBill(@RequestBody String jsonString,
			HttpServletRequest request) {
		UUIDContext.setUniqueID(UUID.randomUUID().toString());
		PerfUtil.logEventToFile("CANCEL_EWB", "CANCEL_EWB_BEGIN",
				"ManageEWBController", "CANCEL_EWB_BEGIN", "", true);
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

			if (Strings.isNullOrEmpty(gstin)) {
				String errMsg = String.format(
						"Gstin is Mandatory while cancelling Ewaybill %s",
						ewbNo);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			response = ewbService.cancelEwb(dto);
			PerfUtil.logEventToFile("CANCEL_EWB", "CANCEL_EWB_BEGIN",
					"ManageEWBController", "CANCEL_BEFORE_RESP_TO_ERP", "", true);
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
			PerfUtil.logEventToFile("CANCEL_EWB", "CANCEL_EWB_BEGIN",
					"ManageEWBController", "CANCEL_AFTER_RESP_TO_ERP", "", true);
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
		} finally {reqLogHelper.saveLogEntity();}
	}

	// This API is to reject eway bill based on the eway bill number provided
	@PostMapping(value = "/api/rejectEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> rejectEwayBill(@RequestBody String jsonString,
			HttpServletRequest request) {
		PerfUtil.logEventToFile("REJECT_EWB", "REJECT_EWB_BEGIN",
				"ManageEWBController", "rejectEwayBill", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		RejectEwbResponseDto response;
		boolean nicStatus = false;
		try {
			RejectEwbRequestparamDto dto = gson.fromJson(jsonString,
					RejectEwbRequestparamDto.class);
			reqLogHelper.logAdditionalParams(
					dto.getRejectEwbReqDto().getGstin(), null, null, false,
					false);
			reqLogHelper.logInvDtlsParams(null, null,
					dto.getRejectEwbReqDto().getEwbNo(), null);

			response = ewbService.rejectEwb(dto);
			JsonObject resp = new JsonObject();
			if (response.getEwbRejectedDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			LOGGER.error("Exception in Reject eway bill with request ", ex);
			response = new RejectEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(ex.getMessage());
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/updatePartBEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updatePartBEwayBillApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return updatePartBEwayBill(jsonString);
	}

	private ResponseEntity<String> updatePartBEwayBill(String jsonString) {
		PerfUtil.logEventToFile("UPDATE_PARTB", "UPDATE_PARTB_BEGIN",
				"ManageEWBController", "updatePartBEwayBill", "");
		boolean nicStatus = false;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		UpdatePartBEwbResponseDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", obj);
			}
			UpdatePartBEwbRequestDto dto = gson.fromJson(obj.get("req"),
					UpdatePartBEwbRequestDto.class);
			String gstin = dto.getGstin();
			String ewbNo = dto.getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);

			response = ewbService.updateEwbPartB(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getVehUpdDate() != null) {
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
			response = new UpdatePartBEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in updating ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new UpdatePartBEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Updating Eway bill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in updating ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/consolidateEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> consolidateEwayBillApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return consolidateEwayBill(jsonString);
	}

	private ResponseEntity<String> consolidateEwayBill(String jsonString) {
		PerfUtil.logEventToFile("GENERATE_CEWB", "GENERATE_CEWB_BEGIN",
				"ManageEWBController", "consolidateEwayBill", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		ConsolidateEWBResponseDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			ConsolidateEWBReqDto dto = gson.fromJson(gson.toJsonTree(obj),
					ConsolidateEWBReqDto.class);
			String gstin = dto.getGstin();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			response = ewbService.consolidateEWB(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getCEwbNo() != null) {
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
			response = new ConsolidateEWBResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in consolidate ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new ConsolidateEWBResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorDesc(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorDesc(
						"Error While consolidating ewayBill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in consolidate ewayBill eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/extendEwbValidity", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> extendEwbValidityApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return extendEwbValidity(jsonString);
	}

	private ResponseEntity<String> extendEwbValidity(String jsonString) {
		PerfUtil.logEventToFile("EXTEND_EWB", "EXTEND_EWB_BEGIN",
				"ManageEWBController", "extendEwbValidity", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		ExtendEWBResponseDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			ExtendEWBReqDto dto = gson.fromJson(obj.get("req"),
					ExtendEWBReqDto.class);
			String gstin = dto.getGstin();
			String ewbNo = dto.getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.extendEWB(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getUpdatedDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new ExtendEWBResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorDesc(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			LOGGER.error("Exception in extend ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new ExtendEWBResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorDesc(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorDesc(
						"Error While Extend ewayBill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			LOGGER.error("Exception in extend ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/updateEwbTransporter", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateEwbTransporterApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return updateEwbTransporter(jsonString);
	}

	private ResponseEntity<String> updateEwbTransporter(String jsonString) {
		PerfUtil.logEventToFile("UPDATE_TRANSPORTER",
				"UPDATE_TRANSPORTER_BEGIN", "ManageEWBController",
				"updateEwbTransporter", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		UpdateEWBTransporterRespDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			UpdateEWBTransporterReqDto dto = gson.fromJson(obj.get("req"),
					UpdateEWBTransporterReqDto.class);
			String gstin = dto.getGstin();
			String ewbNo = dto.getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.updateTransporter(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getTransUpdateDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new UpdateEWBTransporterRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in updateTransporter eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new UpdateEWBTransporterRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While updating Transporter, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in updateTransporter eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/getEwb", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbApi(@RequestBody String jsonString,
			HttpServletRequest req) {
		return getEwb(jsonString);
	}

	private ResponseEntity<String> getEwb(String jsonString) {
		PerfUtil.logEventToFile("GET_EWB", "GET_EWB_BEGIN",
				"ManageEWBController", "getEwb", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		GetEwbResponseDto response;
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			String ewbNo = req.get("ewbNo").getAsString();
			String gstin = req.get("gstin").getAsString();
			if (Strings.isNullOrEmpty(ewbNo) || Strings.isNullOrEmpty(gstin))
				throw new AppException("ewbNo and gstin Both are mandatory.");
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);

			response = ewbService.getEWB(ewbNo, gstin);

			reqLogHelper.logInvDtlsParams(null, null, ewbNo,
					response.getEwayBillDate());

			JsonObject resp = new JsonObject();
			if (response.getItemList() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new GetEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error("Exception get eway bill with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new GetEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Get EwayBill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error("Exception get eway bill with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/addMultiVehicle", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> addMultiVehicleApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return addMultiVehicle(jsonString);
	}

	private ResponseEntity<String> addMultiVehicle(String jsonString) {
		PerfUtil.logEventToFile("ADD MULTIVEHICLE", "ADD_MULTIVEHICLE",
				"ManageEWBController", "addMultiVehicle", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		AddMultiVehicleDetailsRespDto response;
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			AddMultiVehicleDetailsReqDto dto = gson.fromJson(obj.get("req"),
					AddMultiVehicleDetailsReqDto.class);
			String gstin = dto.getGstin();
			String ewbNo = dto.getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			response = ewbService.addMultiVehicles(dto, gstin);
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			JsonObject resp = new JsonObject();
			if (response.getVehAddedDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new AddMultiVehicleDetailsRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in add multi vehicle eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new AddMultiVehicleDetailsRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While adding multi vehicle, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in add multi vehicle eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {reqLogHelper.saveLogEntity();}
	}

	@PostMapping(value = "/api/changeMultiVehicle", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> changeMultiVehicleApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return changeMultiVehicle(jsonString);
	}

	private ResponseEntity<String> changeMultiVehicle(String jsonString) {
		PerfUtil.logEventToFile("CHANGE MULTIVEHICLE", "CHANGE_MULTIVEHICLE",
				"ManageEWBController", "changeMultiVehicle", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ChangeMultiVehicleDetailsRespDto response;
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			ChangeMultiVehicleDetaiilsReqDto dto = gson.fromJson(obj.get("req"),
					ChangeMultiVehicleDetaiilsReqDto.class);
			String gstin = dto.getGstin();
			String ewbNo = dto.getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);

			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);

			response = ewbService.changeMultiVehicles(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getVehUpdDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new ChangeMultiVehicleDetailsRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in change multi vehicle eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new ChangeMultiVehicleDetailsRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Changing multi vehicle eway bill, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in change multi vehicle eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			PerfUtil.logEventToFile("CHANGE MULTIVEHICLE",
					"CHANGE MULTIVEHICLE_END", "ManageEWBController",
					"changeMultiVehicle", "");
			reqLogHelper.saveLogEntity();
		}
	}

	@PostMapping(value = "/api/initiateMultiVehicle", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> initiateMultiVehicleApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return initiateMultiVehicle(jsonString);
	}

	private ResponseEntity<String> initiateMultiVehicle(String jsonString) {
		PerfUtil.logEventToFile("INITIATE_MULTIVEHICLE",
				"INITIATE_MULTIVEHICLE_BEGIN", "ManageEWBController",
				"initiateMultiVehicle", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		InitiateMultiVehicleRespDto response;
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			InitiateMultiVehicleReqDto dto = gson.fromJson(obj.get("req"),
					InitiateMultiVehicleReqDto.class);
			String gstin = dto.getGstin();
			String ewbNo = dto.getEwbNo();
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			reqLogHelper.logInvDtlsParams(null, null, ewbNo, null);
			response = ewbService.initiateMultiVehicles(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getCreatedDate() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			resp.add("resp", gsonEwb.toJsonTree(response));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new InitiateMultiVehicleRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in initiate multi vehicle eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new InitiateMultiVehicleRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Initiating multi vehicle eway bill with request, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in initiate multi vehicle eway bill with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			PerfUtil.logEventToFile("INITIATE_MULTIVEHICLE",
					"INITIATE_MULTIVEHICLE_END", "ManageEWBController",
					"initiateMultiVehicle", "");
			reqLogHelper.saveLogEntity();
		}
	}

	@PostMapping(value = "/api/getEwbByConsigner", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbByConsigner(
			@RequestBody String jsonString, HttpServletRequest request) {
		PerfUtil.logEventToFile("getEwbByConsigner", "getEwbByConsigner_Begin",
				"ManageEWBController", "getEwbByConsigner", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		GetEwbResponseDto response = null;
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String docType = req.get("docType").getAsString();
			String docNo = req.get("docNo").getAsString();
			if (Strings.isNullOrEmpty(docType) || Strings.isNullOrEmpty(docNo))
				throw new AppException("docType and docNo Both are mandatory.");
			reqLogHelper.logAdditionalParams(gstin, docType, docNo, true, true);
			response = ewbService.getEwbByConsigner(gstin, docType, docNo);
			JsonObject resp = new JsonObject();
			if (response.getEwbNo() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));

			reqLogHelper.logInvDtlsParams(null, null, response.getEwbNo(),
					response.getEwayBillDate());

			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			JsonObject resp = new JsonObject();
			response = new GetEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			LOGGER.error("Exception in getEwbByConsigner with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new GetEwbResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While getEwbByConsigner, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			LOGGER.error("Exception in getEwbByConsigner with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();
		}
	}
}
