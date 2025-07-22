package com.ey.advisory.admin.onboarding.controller;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.services.onboarding.DataSecurityServiceImpl;
import com.ey.advisory.admin.services.onboarding.MasterCustomerService;
import com.ey.advisory.admin.services.onboarding.MasterItemService;
import com.ey.advisory.admin.services.onboarding.MasterProductService;
import com.ey.advisory.admin.services.onboarding.MasterVendorService;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.docs.dto.FileStatusResponseDto;
import com.ey.advisory.app.services.configuremaster.reports.MasterErrorReportsService;
import com.ey.advisory.app.services.search.filestatussearch.MasterFileStatusService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.MasterCustomerReqDto;
import com.ey.advisory.core.dto.MasterCustomerRespDto;
import com.ey.advisory.core.dto.MasterItemReqDto;
import com.ey.advisory.core.dto.MasterItemRespDto;
import com.ey.advisory.core.dto.MasterProductReqDto;
import com.ey.advisory.core.dto.MasterProductRespDto;
import com.ey.advisory.core.dto.MasterRequestDto;
import com.ey.advisory.core.dto.MasterVendorReqDto;
import com.ey.advisory.core.dto.MasterVendorRespDto;
import com.ey.advisory.core.dto.Messages;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Umesha.M
 *
 */
@RestController
public class MasterController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataSecurityServiceImpl.class);

	private static final String NO_RECORD_FOUND = "No Record Found";

	private static final String RESP = "resp";

	private static final String HRD = "hrd";

	private static final String EXCEPTION_OCCUR = "Exception Occure: ";

	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";

	private static final String DELETED_SUCCESSFULLY = "Deleted Succefully";

	private static final String REQ = "req";

	@Autowired
	@Qualifier("masterCustomerService")
	private MasterCustomerService masterCustomerService;

	@Autowired
	@Qualifier("masterItemService")
	private MasterItemService masterItemService;

	@Autowired
	@Qualifier("masterProductService")
	private MasterProductService masterProductService;

	@Autowired
	@Qualifier("masterVendorService")
	private MasterVendorService masterVendorService;

	@Autowired
	@Qualifier("masterFileStatusService")
	private MasterFileStatusService masterFileStatusService;

	@Autowired
	@Qualifier("masterErrorReportsService")
	private MasterErrorReportsService masterErrorReportsService;

	/**
	 * Get All Master Customer Data
	 * 
	 * @return
	 */
	@PostMapping(value = "/getMasterCustomer.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMasterCustomer(@RequestBody String req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestJson = (new JsonParser()).parse(req)
					.getAsJsonObject().get(REQ).getAsJsonObject();

			MasterRequestDto dto = gson.fromJson(requestJson,
					MasterRequestDto.class);
			Long entityId = dto.getEntityId();
			List<MasterCustomerRespDto> masterCustomerRespDtos = new ArrayList<>();
			if (entityId != null) {
				masterCustomerRespDtos = masterCustomerService
						.getMasterCustomer(entityId);
			}
			if (masterCustomerRespDtos != null
					&& !masterCustomerRespDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(masterCustomerRespDtos);
				resp.add(RESP, respBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.SUCCESS_STATUS, NO_RECORD_FOUND)));
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Update Master Cutomer Details
	 */
	@PostMapping(value = "/updateMasterCustomer.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateMasterCustomer(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<MasterCustomerReqDto>>() {
			}.getType();
			List<MasterCustomerReqDto> dtos = gson.fromJson(reqJsonObject,
					listType);
			Messages messages = masterCustomerService
					.updateMasterCustomer(dtos);
			if (messages.getMessages() != null
					&& !messages.getMessages().isEmpty()) {
				JsonElement jsonBody = gson.toJsonTree(messages);
				resp.add(RESP, jsonBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.SUCCESS_STATUS, UPDATE_SUCCESSFULLY)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteMasterCustomer.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteMasterCustomer(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type typeList = new TypeToken<List<MasterCustomerReqDto>>() {
			}.getType();
			List<MasterCustomerReqDto> dtos = gson.fromJson(reqJsonObject,
					typeList);
			masterCustomerService.deleteMasterCustomer(dtos);
			resp.add(RESP,
					gson.toJsonTree(new APIRespDto(APIRespDto.SUCCESS_STATUS,
							DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Get All Master Item Details
	 */
	@PostMapping(value = "/getMasterItem.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMasterItem(@RequestBody String req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestJson = (new JsonParser()).parse(req)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			MasterRequestDto dto = gson.fromJson(requestJson,
					MasterRequestDto.class);
			List<MasterItemRespDto> dtos = new ArrayList<>();
			if (dto.getEntityId() != null) {
				dtos = masterItemService.getMasterItem(dto.getEntityId());
			}
			if (dtos != null && !dtos.isEmpty()) {
				JsonElement responseBody = gson.toJsonTree(dtos);
				resp.add(RESP, responseBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.SUCCESS_STATUS, NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Update Master Item Details
	 */
	@PostMapping(value = "/updateMasterItem.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateMasterItem(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonArray jsonReqObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<MasterItemReqDto>>() {
			}.getType();
			List<MasterItemReqDto> dtos = gson.fromJson(jsonReqObject,
					listType);
			Messages errorMsg = masterItemService.updateMasterItem(dtos);
			if (errorMsg.getMessages() != null
					&& !errorMsg.getMessages().isEmpty()) {
				resp.add(RESP, gson.toJsonTree(errorMsg));
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.SUCCESS_STATUS, UPDATE_SUCCESSFULLY)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteMasterItem.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteMasterItem(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type typeList = new TypeToken<List<MasterItemReqDto>>() {
			}.getType();
			List<MasterItemReqDto> dtos = gson.fromJson(reqJsonObject,
					typeList);
			masterItemService.deleteMasterItem(dtos);
			resp.add(RESP,
					gson.toJsonTree(new APIRespDto(APIRespDto.SUCCESS_STATUS,
							DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Get All Master Product Details
	 */
	@PostMapping(value = "/getMasterProduct.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMasterProduct(@RequestBody String req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestJson = (new JsonParser()).parse(req)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			MasterRequestDto dto = gson.fromJson(requestJson,
					MasterRequestDto.class);
			List<MasterProductRespDto> dtos = new ArrayList<>();
			if (dto.getEntityId() != null) {
				dtos = masterProductService.getMasterProduct(dto.getEntityId());
			}

			if (dtos != null && !dtos.isEmpty()) {
				JsonElement responseBody = gson.toJsonTree(dtos);
				resp.add(RESP, responseBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Update Master Product Details
	 */
	@PostMapping(value = "/updateMasterProduct.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateMasterProduct(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<MasterProductReqDto>>() {
			}.getType();
			List<MasterProductReqDto> dtos = gson.fromJson(jsonReqObj,
					listType);
			Messages message = masterProductService.updateMasterProduct(dtos);
			if (message.getMessages() != null
					&& message.getMessages().isEmpty()) {
				resp.add(RESP, gson.toJsonTree(message));
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.SUCCESS_STATUS, UPDATE_SUCCESSFULLY)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteMasterProduct.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteMasterProduct(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type typeList = new TypeToken<List<MasterProductReqDto>>() {
			}.getType();
			List<MasterProductReqDto> dtos = gson.fromJson(jsonReqObj,
					typeList);
			masterProductService.deleteMasterProduct(dtos);
			resp.add(RESP,
					gson.toJsonTree(new APIRespDto(APIRespDto.SUCCESS_STATUS,
							DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Get All Vendor Master Details
	 */
	@PostMapping(value = "/getMasterVendor.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMasterVendor(@RequestBody String req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestJson = (new JsonParser()).parse(req)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			MasterRequestDto dto = gson.fromJson(requestJson,
					MasterRequestDto.class);
			List<MasterVendorRespDto> dtos = new ArrayList<>();
			if (dto.getEntityId() != null) {
				dtos = masterVendorService.getMasterVendor(dto.getEntityId());
			}
			if (dtos != null && !dtos.isEmpty()) {
				JsonElement responseBody = gson.toJsonTree(dtos);
				resp.add(RESP, responseBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/*
	 * Update Vendor Master based on requirement.
	 */
	@PostMapping(value = "/updateMasterVendor.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateMasterVendor(
			@RequestBody String jsonReq) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<MasterVendorReqDto>>() {
			}.getType();
			List<MasterVendorReqDto> dtos = gson.fromJson(jsonReqObj, listType);
			Messages msg = masterVendorService.updateMasterVendor(dtos);
			if (msg.getMessages() != null && !msg.getMessages().isEmpty()) {
				resp.add(RESP, gson.toJsonTree(msg));
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.SUCCESS_STATUS, UPDATE_SUCCESSFULLY)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteMasterVendor.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteMasterVendor(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type typeList = new TypeToken<List<MasterVendorReqDto>>() {
			}.getType();
			List<MasterVendorReqDto> dtos = gson.fromJson(jsonReqObj, typeList);
			masterVendorService.deleteMasterVendor(dtos);
			resp.add(RESP,
					gson.toJsonTree(new APIRespDto(APIRespDto.SUCCESS_STATUS,
							DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getMasterFileStatus.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFileStatus(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			
			JsonObject jsonReqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			FileStatusReqDto reqDto = gson.fromJson(jsonReqObj,
					FileStatusReqDto.class);
			List<FileStatusResponseDto> fileStatusResponseDtos = masterFileStatusService
					.getMasterFileStatus(reqDto);
			if (fileStatusResponseDtos != null
					&& !fileStatusResponseDtos.isEmpty()) {
				resp.add(RESP, gson.toJsonTree(fileStatusResponseDtos));
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/downloadMasterErrorReport.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public void downloadMasterErrorReport(@RequestBody String jsonReq,
			HttpServletResponse response) {
		JsonObject resp = new JsonObject();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		String fileName = null;
		Workbook workbook = null;
		try {
			Anx1FileStatusReportsReqDto criteria = gson.fromJson(json,
					Anx1FileStatusReportsReqDto.class);
			if (criteria.getFileName() != null
					&& !criteria.getFileName().isEmpty()) {
				fileName = "Master_Error_Details";
				workbook = masterErrorReportsService
						.downloadMasterErrorReports(criteria, null);
			}

			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
	}

	@PostMapping(value = "/downloadMasterReport.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public void downloadMasterReport(@RequestBody String jsonReq,
			HttpServletResponse response) {
		JsonObject resp = new JsonObject();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		try {
			Anx1FileStatusReportsReqDto reqDto = gson.fromJson(json,
					Anx1FileStatusReportsReqDto.class);
			String fileFolder = GSTConstants.MASTER_FOLDER_NAME;
			if (reqDto.getFileName() != null) {
				Document document = DocumentUtility
						.downloadDocument(reqDto.getFileName(), fileFolder);
				if (document == null) {
					return;
				}
				InputStream inputStream = document.getContentStream()
						.getStream();
				Workbook workbook = new Workbook(inputStream);
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format(
						"attachment; filename = " + reqDto.getFileName()));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
	}
}
