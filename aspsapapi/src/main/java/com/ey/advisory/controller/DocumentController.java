package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.dto.DocDeleteReqDto;
import com.ey.advisory.app.docs.dto.DocSaveRespDto;
import com.ey.advisory.app.docs.dto.DocumentDto;
import com.ey.advisory.app.docs.dto.OutwardDocSaveRespDto;
import com.ey.advisory.app.docs.dto.OutwardDocSvErrSaveRespDto;
import com.ey.advisory.app.docs.dto.PageReqDto;
import com.ey.advisory.app.services.delete.DocDeleteService;
import com.ey.advisory.app.services.docs.DocSaveService;
import com.ey.advisory.app.services.docs.OutwardDocSvErrCorrectionSaveService;
import com.ey.advisory.app.services.search.docsearch.BasicDocSearchService;
import com.ey.advisory.app.services.search.docsearch.BasicOutwardSvErrDocSearchService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.functions.EinvJsonSchemaValidatorHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * This class is responsible for APIs related to Save Document, Get Error
 * Documents for Correction, Update Corrected Document and other APIs which are
 * required for Error Correction UI screen
 *
 * @author Mohana.Dasari
 */
@RestController
public class DocumentController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DocumentController.class);

	@Autowired
	@Qualifier("BasicDocSearchService")
	private BasicDocSearchService docSearch;

	@Autowired
	@Qualifier("BasicOutwardSvErrDocSearchService")
	private BasicOutwardSvErrDocSearchService outwardSvErrDocSearch;

	@Autowired
	@Qualifier("DefaultOutwardDocSvErrCorrectionSaveService")
	private OutwardDocSvErrCorrectionSaveService outwardSvErrDocSaveSvc;

	@Autowired
	@Qualifier("DefaultDocSaveService")
	private DocSaveService docService;

	@Autowired
	@Qualifier("DefaultDocDeleteServiceImpl")
	private DocDeleteService docDeleteService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@PostMapping(value = "/ui/saveDocUI", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDocumentsFromUI(
			@RequestBody String jsonString) {
		return saveDocumentsForUI(jsonString);
	}

	@PostMapping(value = "/api/saveDocAPI", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDocumentsFromERP(
			@RequestBody String jsonString) {
		return saveDocuments(jsonString);
	}

	/**
	 * This method persists Document header and Document line item from SCI
	 * 
	 * @method saveDocuments
	 * @param jsonString
	 * @return ResponseEntity
	 */
	private ResponseEntity<String> saveDocumentsForUI(String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {

			// Set Req Received Time for Performance Testing
			// LocalDateTime reqReceivedTime = LocalDateTime.now();
			LocalDateTime reqReceivedTime = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<OutwardTransDocument>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + json);
			}
			List<OutwardTransDocument> documents = gsonEwb.fromJson(json,
					listType);
			User user = SecurityContext.getUser();
			documents.forEach(document -> {
				document.setReqReceivedOn(reqReceivedTime);
				LocalDateTime ewbDate = document.geteWayBillDate();
				if (ewbDate != null) {
					LocalDateTime convertEwbDate = EYDateUtil
							.toUTCDateTimeFromLocal(ewbDate);
					document.seteWayBillDate(convertEwbDate);
				}
				LocalDateTime hciRecevedOn = document.getHciReceivedOn();
				if (hciRecevedOn != null) {
					LocalDateTime convertHciRecevedOn = EYDateUtil
							.toUTCDateTimeFromLocal(hciRecevedOn);
					document.setHciReceivedOn(convertHciRecevedOn);
				}
				LocalDate docDate = document.getDocDate();
				String finYear = GenUtil.getFinYear(docDate);
				document.setFinYear(finYear);
				document.setCreatedBy(user.getUserPrincipalName());
			});
			GstnApi gstnApi = StaticContextHolder.getBean("GstnApi",
					GstnApi.class);
			CommonContext.setDelinkingFlagContext(gstnApi
					.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()));
			OutwardDocSaveRespDto finalResp = docService
					.saveDocuments(documents);
			List<DocSaveRespDto> docSaveResponse = finalResp.getSavedDocsResp();
			JsonElement respBody = gson.toJsonTree(docSaveResponse);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			LOGGER.error("saveOutwardDoc method exception ", ex);
			return InputValidationUtil.createJsonErrResponse(ex);
		} finally {
			CommonContext.clearContext();
		}
	}

	private ResponseEntity<String> saveDocuments(String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString,
							"SaveDocumentsJsonSchema.json");

			if (errorList.isEmpty()) {
				// Set Req Received Time for Performance Testing
				// LocalDateTime reqReceivedTime = LocalDateTime.now();
				LocalDateTime reqReceivedTime = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				JsonObject requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();

				Type listType = new TypeToken<List<OutwardTransDocument>>() {
				}.getType();

				JsonArray json = requestObject.get("req").getAsJsonArray();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request Json " + json);
				}
				List<OutwardTransDocument> documents = gsonEwb.fromJson(json,
						listType);
				User user = SecurityContext.getUser();
				documents.forEach(document -> {
					document.setReqReceivedOn(reqReceivedTime);
					LocalDateTime ewbDate = document.geteWayBillDate();
					if (ewbDate != null) {
						LocalDateTime convertEwbDate = EYDateUtil
								.toUTCDateTimeFromLocal(ewbDate);
						document.seteWayBillDate(convertEwbDate);
					}
					LocalDateTime hciRecevedOn = document.getHciReceivedOn();
					if (hciRecevedOn != null) {
						LocalDateTime convertHciRecevedOn = EYDateUtil
								.toUTCDateTimeFromLocal(hciRecevedOn);
						document.setHciReceivedOn(convertHciRecevedOn);
					}
					LocalDate docDate = document.getDocDate();
					String finYear = GenUtil.getFinYear(docDate);
					document.setFinYear(finYear);
					document.setCreatedBy(user.getUserPrincipalName());
				});
				GstnApi gstnApi = StaticContextHolder.getBean("GstnApi",
						GstnApi.class);
				CommonContext.setDelinkingFlagContext(gstnApi
						.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()));
				OutwardDocSaveRespDto finalResp = docService
						.saveDocuments(documents);
				List<DocSaveRespDto> docSaveResponse = finalResp
						.getSavedDocsResp();
				JsonElement respBody = gson.toJsonTree(docSaveResponse);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);
			}
			JsonElement respBody = gson.toJsonTree(errorList);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E",
					"Error occured while validating json schema")));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("saveOutwardDoc method exception ", ex);
			return InputValidationUtil.createJsonErrResponse(ex);
		} finally {
			CommonContext.clearContext();
		}
	}

	/**
	 * This method searches for the document based on the input filter
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/docSearch", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDocuments(@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			// Gson gson = GsonUtil.gsonInstanceWithExpose();
			// Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
			SearchCriteria dto = gson.fromJson(reqJson, DocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Hdr Json " + hdrJson);
			}
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<DocumentDto> searchResult = docSearch.find(dto,
					pageRequest, DocumentDto.class);
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in retrieving Outward Docs " + ex);
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	/**
	 * This method is responsible for Saving Structurally Validated Outward
	 * Error Document from UI
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/saveOutwardSvErrDoc", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveOutwardSvErrDoc(
			@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered saveOutwardSvErrDoc method");
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<Anx1OutWardErrHeader>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + json);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<Anx1OutWardErrHeader> documents = gson.fromJson(json,
					listType);
			List<OutwardDocSvErrSaveRespDto> saveResp = outwardSvErrDocSaveSvc
					.saveSvErrDocuments(documents);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(saveResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error(
					"saveOutwardSvErrDoc method exception " + ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	/**
	 * This method searches for structurally validated Outward error documents
	 * based on the input filter
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/outwardSvErrDocSearch", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOutwardSvErrDocuments(
			@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered getOutwardSvErrDocuments method");
		}
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			SearchCriteria dto = gson.fromJson(reqJson, DocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Hdr Json " + hdrJson);
			}
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<DocumentDto> searchResult = outwardSvErrDocSearch
					.find(dto, pageRequest, DocumentDto.class);
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("getOutwardSvErrDocuments method exception "
					+ ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	/**
	 * This method soft deletes the document
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/deleteGstr1Docs", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteDocuments(
			@RequestBody String jsonString) {
		try {
			JsonObject resp = new JsonObject();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			DocDeleteReqDto dto = gson.fromJson(reqJson, DocDeleteReqDto.class);
			docDeleteService.deleteDocuments(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while deleting documents";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
