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
import com.ey.advisory.app.services.einvoice.CancelEWBBillService;
import com.ey.advisory.app.services.einvoice.CancelIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEWBByIrnService;
import com.ey.advisory.app.services.einvoice.GenerateEinvoiceService;
import com.ey.advisory.app.services.einvoice.GetEInvDetailsService;
import com.ey.advisory.app.services.einvoice.GetEWBDetailsByIrnService;
import com.ey.advisory.app.services.einvoice.GetSyncGSTINDetailsService;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.UUIDContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.einv.dto.CancelEWBBillRequest;
import com.ey.advisory.einv.dto.CancelEWBBillResponseDto;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.GenerateEWBByIrnRequest;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.GetEWBDetailsByIrnRespDto;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailsERPRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@RestController
public class ManageEinvoiceController {

	private static final String INVALID_GSTIN = "GSTIN is not valid";

	@Autowired
	@Qualifier("GenerateEinvoiceServiceImpl")
	private GenerateEinvoiceService generateEinvoiceService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	@Qualifier("CancelIrnServiceImpl")
	private CancelIrnService cancelIrnService;

	@Autowired
	@Qualifier("CancelEWBBillServiceImpl")
	private CancelEWBBillService cancelEWBBillService;

	@Autowired
	@Qualifier("GenerateEWBByIrnServiceImpl")
	private GenerateEWBByIrnService generateEWBIrnService;

	@Autowired
	@Qualifier("GetEInvDetailsServiceImpl")
	private GetEInvDetailsService getEInvDetailsService;

	@Autowired
	@Qualifier("GetSyncGSTINDetailsServiceImpl")
	private GetSyncGSTINDetailsService getSyncGSTINDetailsService;

	@Autowired
	@Qualifier("GetEWBDetailsByIrnServiceImpl")
	private GetEWBDetailsByIrnService getEWBDetailsByIrnService;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@PostMapping(value = "/api/generateEinvoice", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> generateBulkEinvoice(
			@RequestBody String jsonString, HttpServletRequest req) {

		UUID uuid = UUID.randomUUID();
		UUIDContext.setUniqueID(uuid.toString());

		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_BEGIN",
				"ManageEinvoiceController", "generateBulkEinvoice",
				"METHOD_START", true);
		JsonObject jsonObj = new JsonObject();
		boolean nicStatus = false;
		boolean isSchemaValidationReq = false;
		GenerateIrnResponseDto response;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			if (isSchemaValidationReq) {
				errorList = jsonSchemaValidatorHelper.validateInptJson(
						jsonString, "EinvoiceNestedJsonSchema.json");
			}
			if (errorList.isEmpty()) {
				OutwardTransDocument hdr = gson.fromJson(
						requestObject.get("req"), OutwardTransDocument.class);
				reqLogHelper.logAppMessage(hdr.getDocNo(), null, null,
						"Generate E Invoice Request Entered to the System");
				reqLogHelper.logAdditionalParams(hdr.getSgstin(),
						hdr.getDocType(), hdr.getDocNo(), true, true);
				PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_BEGIN",
						"ManageEinvoiceController", "generateBulkEinvoice",
						"SERVICE_METHOD_START", true);
				response = generateEinvoiceService.generateEinvRequest(hdr,
						true);
				
				PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_BEGIN",
						"ManageEinvoiceController", "generateBulkEinvoice",
						"SERVICE_METHOD_END", true);

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
			JsonElement respBody = gson.toJsonTree(errorList);
			jsonObj.add("hdr", new Gson().toJsonTree(new APIRespDto("E",
					"Error occured while validating json schema")));
			jsonObj.add("resp", respBody);
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			
			PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_BEGIN",
					"ManageEinvoiceController", "generateBulkEinvoice",
					"METHOD_END", true);
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

	private ResponseEntity<String> cancelEinvoice(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		UUID uuid = UUID.randomUUID();
		UUIDContext.setUniqueID(uuid.toString());
		try {
			PerfUtil.logEventToFile("CANCEL_EINVOICE", "CANCEL_EINVOICE_BEGIN",
					"ManageEinvoiceController", "cancelEinvoice",
					"METHOD_END", true);
			CancelIrnReqList req = gson.fromJson(jsonString,
					CancelIrnReqList.class);
			reqLogHelper.logAdditionalParams(req.getReqList().get(0).getGstin(),
					null, null, false, false);
			reqLogHelper.logInvDtlsParams(req.getReqList().get(0).getIrn(),
					null, null, null);
			reqLogHelper.logAppMessage(null, req.getReqList().get(0).getIrn(),
					null, "Cancel E Invoice Request Entered to the System");
			List<CancelIrnERPResponseDto> response = cancelIrnService
					.cancelEinvRequest(req);
			if (response.get(0).getCancelDate() != null) {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
			}
			jsonObj.add("resp", gson.toJsonTree(response));
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Cancel IRN Einvoice";
				LOGGER.debug(msg);
			}
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			PerfUtil.logEventToFile("CANCEL_EINVOICE", "CANCEL_EINVOICE_BEGIN",
					"ManageEinvoiceController", "cancelEinvoice",
					"METHOD_END", true);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			LOGGER.error("Exception while Cancelling E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			CancelIrnERPResponseDto response = new CancelIrnERPResponseDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Cancelling E-Invoice ", ex);
			JsonObject resp = new JsonObject();
			CancelIrnERPResponseDto response = new CancelIrnERPResponseDto();
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

	public ResponseEntity<String> generateEWBByIrn(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		UUID uuid = UUID.randomUUID();
		UUIDContext.setUniqueID(uuid.toString());
		try {

			PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_BEGIN",
					"ManageEinvoiceController", "generateEWBByIrn",
					"METHOD_START", true);

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
			PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_BEGIN",
					"ManageEinvoiceController", "generateEWBByIrn",
					"METHOD_END", true);
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

	@PostMapping(value = "api/cancelEinvoice", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEinvoiceApi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return cancelEinvoice(jsonString);

	}

	@PostMapping(value = "/api/generateEWBByIRN", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEWBByIrnapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return generateEWBByIrn(jsonString);
	}

	@PostMapping(value = "/api/cancelEInvEwayBill", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEWBBillapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return cancelEWBBill(jsonString);
	}

	private ResponseEntity<String> cancelEWBBill(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		try {
			CancelEWBBillRequest req = gson.fromJson(jsonString,
					CancelEWBBillRequest.class);
			reqLogHelper.logAdditionalParams(
					req.getCancelEwbBillERPreqdto().getGstin(), null, null,
					false, false);
			CancelEWBBillResponseDto response = cancelEWBBillService
					.cancelEwaybillRequest(req);

			reqLogHelper.logInvDtlsParams(null, null, response.getEwayBillNo(),
					null);

			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", gson.toJsonTree(response));
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Cancel EWB Bill";
				LOGGER.debug(msg);
			}
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			CancelEWBBillResponseDto response = new CancelEWBBillResponseDto();
			LOGGER.error("Exception occured while Cancel E-way Bill", ex);
			JsonObject resp = new JsonObject();
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
		} finally {
			reqLogHelper.saveLogEntity();
		}
	}

	@PostMapping(value = "/api/getEInvDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEInvDetailsapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getEInvDetails(jsonString);

	}

	@PostMapping(value = "/api/getEInvDtlsByDocDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEInvDtlsByDocDetailsapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getEInvDtlsByDocDetails(jsonString);

	}

	@PostMapping(value = "/api/getSyncDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSyncDetailsapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getSyncDetails(jsonString);
	}

	private ResponseEntity<String> getEInvDetails(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		GenerateIrnResponseDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String irnNo = req.get("irnNo").getAsString();
			String gstin = req.get("gstin").getAsString();
			if (Strings.isNullOrEmpty(irnNo))
				throw new AppException("irnNo is mandatory.");
			reqLogHelper.logAdditionalParams(gstin, null, null, false, false);
			response = getEInvDetailsService.getEInvDetails(irnNo, gstin, null);
			if (Strings.isNullOrEmpty(response.getIrn()))
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
			else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			}
			jsonObj.add("resp", gson.toJsonTree(response));
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Get E Invoice Details";
				LOGGER.debug(msg);
			}
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Get E Invoice Details", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateIrnResponseDto();
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
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	private ResponseEntity<String> getEInvDtlsByDocDetails(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		GenerateIrnResponseDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String docNum = req.get("docNum").getAsString();
			String docType = req.get("docType").getAsString();
			String docDate = req.get("docDate").getAsString();
			String gstin = req.get("gstin").getAsString();
			if (Strings.isNullOrEmpty(docNum))
				throw new AppException("docNum is mandatory.");
			reqLogHelper.logAdditionalParams(gstin, docType, docNum, true,
					true);
			response = getEInvDetailsService.getEInvDtlsByDocDtls(docNum,
					docType, docDate, gstin);

			reqLogHelper.logInvDtlsParams(response.getIrn(),
					response.getAckDt(), response.getEwbNo(),
					response.getEwbDt());

			if (Strings.isNullOrEmpty(response.getIrn()))
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
			else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			}
			jsonObj.add("resp", gson.toJsonTree(response));
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Get E Invoice Details";
				LOGGER.debug(msg);
			}
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Get E Invoice Details", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateIrnResponseDto();
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
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	private ResponseEntity<String> getSyncDetails(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		GetSyncGSTINDetailsERPRespDto response;
		List<GetSyncGSTINDetailsERPRespDto> respList = new ArrayList<>();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonArray req = obj.get("req").getAsJsonArray();

			for (int i = 0; i < req.size(); i++) {
				JsonObject reqObj = req.get(i).getAsJsonObject();
				String syncGstin = reqObj.get("syncgstin").getAsString();
				String gstin = reqObj.get("gstin").getAsString();
				if (Strings.isNullOrEmpty(syncGstin))
					throw new AppException("syncgstin is mandatory.");

				if (syncGstin.length() != 15
						|| !syncGstin.matches("[A-Za-z0-9]+")
						|| syncGstin.matches("[A-Za-z]+")
						|| syncGstin.matches("[0-9]+")) {
					throw new AppException(INVALID_GSTIN);
				}
				if (gstin.length() != 15 || !gstin.matches("[A-Za-z0-9]+")
						|| gstin.matches("[A-Za-z]+")
						|| gstin.matches("[0-9]+")) {
					throw new AppException(INVALID_GSTIN);
				}

				reqLogHelper.logAdditionalParams(gstin, null, null, false,
						false);
				response = getSyncGSTINDetailsService.getSyncDetails(syncGstin,
						gstin);

				if (!Strings.isNullOrEmpty(response.getGstin())) {
					nicStatus = true;
				}
				respList.add(response);
			}
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", gson.toJsonTree(respList));
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			LOGGER.error("Exception occured while Get Sync Details", ex);
			JsonObject resp = new JsonObject();
			response = new GetSyncGSTINDetailsERPRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respList.add(response);
			resp.add("resp", gson.toJsonTree(respList));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Get Sync Details", ex);
			JsonObject resp = new JsonObject();
			response = new GetSyncGSTINDetailsERPRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(
						"Error While Syncronising Details, Please Contact HelpDesk");
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respList.add(response);
			resp.add("resp", gson.toJsonTree(respList));
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	@PostMapping(value = "/api/getEWBDetailsByIrn", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwayBillByIrn(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getEWBDetailsByIrn(jsonString);
	}

	private ResponseEntity<String> getEWBDetailsByIrn(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		boolean nicStatus = false;
		GetEWBDetailsByIrnRespDto response;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String irnNo = req.get("irnNo").getAsString();
			String gstin = req.get("gstin").getAsString();
			if (Strings.isNullOrEmpty(irnNo))
				throw new AppException("irnNo is mandatory.");
			response = getEWBDetailsByIrnService.getEWBDetailsByIrn(irnNo,
					gstin);

			reqLogHelper.logInvDtlsParams(irnNo, null, response.getEwbNo(),
					response.getEwbDt());

			if (Strings.isNullOrEmpty(response.getEwbNo()))
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
			else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			}
			jsonObj.add("resp", gson.toJsonTree(response));
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Get EWB Details By Irn";
				LOGGER.debug(msg);
			}
			reqLogHelper.updateResponsePayload(jsonObj.toString(), nicStatus);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Get EWB Details By Irn", ex);
			JsonObject resp = new JsonObject();
			response = new GetEWBDetailsByIrnRespDto();
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
		} finally {
			reqLogHelper.saveLogEntity();

		}
	}

	public class GstnData {
		private String gstin;

		public String getGstin() {
			return gstin;
		}

		public void setGstin(String gstin) {
			this.gstin = gstin;
		}
	}
}
