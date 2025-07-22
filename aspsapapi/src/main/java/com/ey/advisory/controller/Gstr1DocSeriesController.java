package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.Gstr1EinvSeriesCompEntity;
import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AEinvSeriesCompEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AInvoiceFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AEinvSeriesCompRepo;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EinvSeriesCompRepo;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.data.services.einvseries.GSTR1EinvSeriesCompService;
import com.ey.advisory.app.docs.dto.DocSeriesSDeleteReqDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1DocSeriesCompDto;
import com.ey.advisory.app.services.gstr1afileupload.Gstr1ADocSeriesUpdateService;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1DocSeriesUpdateService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1DocSeriesReqResponse;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@RestController
@Slf4j
public class Gstr1DocSeriesController {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("Gstr1DocSeriesUpdateService")
	private Gstr1DocSeriesUpdateService gstr1DocSeriesUpdateService;

	@Autowired
	@Qualifier("Gstr1ADocSeriesUpdateService")
	private Gstr1ADocSeriesUpdateService gstr1ADocSeriesUpdateService;

	@Autowired
	@Qualifier("Gstr1DocSeriesReqResponse")
	private Gstr1DocSeriesReqResponse reqResp;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1InvoiceRepository;
	
	@Autowired
	private Gstr1EinvSeriesCompRepo gstr1EinvSeriesCompRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("EinvSeriRecPattServiceImpl")
	private GSTR1EinvSeriesCompService invServiceImpl;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private InwardTransDocRepository inDocRepository;
	
	@Autowired
	private Gstr1AEinvSeriesCompRepo gstr1aEinvSeriesCompRepo;
	
	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1AInvoiceRepository;

	private static final ImmutableList<String> STATUS = ImmutableList
			.of("INITIATED", "INPROGRESS");

	@PostMapping(value = "ui/getdocseries", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> advancedSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);
			JsonElement retReqRespDetails = reqResp
					.gstr1ReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Gstr1 Doc series "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * This method is responsible for Save and Edit of Doc series Request param
	 * is String json param Response param is ResponseEntity<String>
	 */

	@PostMapping(value = "ui/docseriesSaveAndEdit", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> docSeriesSaveAndEdit(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1VerticalDocSeriesRespDto>>() {
			}.getType();
			List<Gstr1VerticalDocSeriesRespDto> reqUserInputs = gson
					.fromJson(jsonObject, listType);
			JsonObject resp = gstr1DocSeriesUpdateService
					.updateVerticalData(reqUserInputs);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Gstr1 Doc Series "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr1DocseriesDelete", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> basicVerticalRecordDelete(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();
			DocSeriesSDeleteReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), DocSeriesSDeleteReqDto.class);
			List<Long> id = ret1SummaryRequest.getId();
			int deleteRecord = gstr1InvoiceRepository.deleteRecord(id);
			JsonObject resp = new JsonObject();
			if (deleteRecord != 0) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.getErrorStatus()));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr1BulkDocSerAutoComp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1BulkGetSumm(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String msg = null;
		JsonObject jsonObj = new JsonObject();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		JsonArray respBody = new JsonArray();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1DocSeriesCompDto dto = gson.fromJson(reqObject,
					Gstr1DocSeriesCompDto.class);
			boolean isOpted = isInvSeriesOpted(Long.valueOf(dto.getEntityId()));
			if (!isOpted) {
				String errMsg = "Auto Compute Invoice Series is not Opted for this Entity.";
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("errMsg", errMsg);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			String optedImpl = invSeriesOptedImpl(
					Long.valueOf(dto.getEntityId()));
			String retPeriod = dto.getReturnPeriod();
			for (String gstin : dto.getGstins()) {
				JsonObject json = new JsonObject();
				boolean isInvSerApp = isDataAvail(gstin, retPeriod);
				if (!isInvSerApp) {
					msg = String.format(
							"No data available in the System for "
									+ " %s and %s combination. AutoCompute Invoices series will not be initiated.",
							gstin, retPeriod);
				} else if (isCompRequestEligible(gstin, retPeriod)) {
					msg = String.format(
							"AutoCompute Invoice Series is already Initiated/InProgress "
									+ "for  %s and %s combination.",
							gstin, retPeriod);
				} else {
					msg = String.format(
							"AutoCompute Invoice Series is Successfully Initiated "
									+ "for  %s and %s combination.",
							gstin, retPeriod);
					postInvSeriesAsyncJob(dto.getEntityId(), gstin, retPeriod,
							optedImpl, userName);
				}
				json.addProperty("gstin", gstin);
				json.addProperty("msg", msg);
				respBody.add(json);
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			msg = "Unexpected error while Auto Computing Invoice Series.";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
//phase2
	@PostMapping(value = "/ui/gstr1DocSeriesAutoComp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1DocSeriesAutoComp(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1DocSeriesAutoComp Request received from UI as {} ",
						jsonString);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1DocSeriesCompDto dto = gson.fromJson(reqObject,
					Gstr1DocSeriesCompDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			boolean isOpted = isInvSeriesOpted(Long.valueOf(dto.getEntityId()));
			if (!isOpted) {
				String errMsg = "Auto Compute Invoice Series is not Opted for this Entity.";
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", errMsg);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			boolean isInvSerApp = isDataAvail(gstin, retPeriod);
			if (!isInvSerApp) {
				String errMsg = String.format(
						"No data available in the System for "
								+ " %s and %s combination. AutoCompute Invoices series will not be initiated.",
						gstin, retPeriod);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", errMsg);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			if (isCompRequestEligible(gstin, retPeriod)) {
				String errMsg = String.format(
						"AutoCompute Invoice Series is already Initiated/InProgress "
								+ "for  %s and %s combination.",
						gstin, retPeriod);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", errMsg);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			String optedImpl = invSeriesOptedImpl(
					Long.valueOf(dto.getEntityId()));
			postInvSeriesAsyncJob(dto.getEntityId(), gstin, retPeriod,
					optedImpl, userName);
			String msg = String
					.format("AutoCompute Invoice Series is Successfully Initiated "
							+ "for  %s and %s combination.", gstin, retPeriod);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.addProperty("resp", msg);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", msg);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//phase2
	@PostMapping(value = "/ui/gstr1DocSeriesAutoCompStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1DocSeriesAutoCompStatus(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put(APIConstants.SUCCESS, "Completed");
		statusMap.put(APIConstants.INPROGRESS, "InProgress");
		statusMap.put(APIConstants.FAILED, "Failed");
		statusMap.put(APIConstants.INITIATED, "Initiated");
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1DocSeriesAutoComp Request received from UI as {} ",
						jsonString);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1DocSeriesCompDto dto = gson.fromJson(reqObject,
					Gstr1DocSeriesCompDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			String identi = dto.getIdentifier();
			if (identi.equalsIgnoreCase("CB")) {
				
				Optional<Gstr1EinvSeriesCompEntity> isAvailable = gstr1EinvSeriesCompRepo
						.findByGstinAndReturnPeriodAndIsActiveTrue(gstin,
								retPeriod);
				if (isAvailable.isPresent()) {
					dto.setCompletedOn(
							EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
									isAvailable.get().getModifiedOn())));
					dto.setRequestStatus(statusMap
							.get(isAvailable.get().getRequestStatus()));
				} else {
					dto.setRequestStatus(APIConstants.NOT_INITIATED);
				}
			} else {
				Optional<Gstr1InvoiceFileUploadEntity> isAvailable = gstr1InvoiceRepository
						.findTop1BySgstinAndReturnPeriodAndIsDeleteFalseOrderByCreatedOnDesc(
								gstin, retPeriod);
				if (isAvailable.isPresent()) {

					String dataOrg = "C".equalsIgnoreCase(
							isAvailable.get().getDataOriginType())
									? "DigiGST compute" : "File upload";
					dto.setCompletedOn(EYDateUtil
							.fmt(EYDateUtil.toISTDateTimeFromUTC(
									isAvailable.get().getCreatedOn()))
							+ "-" + dataOrg);
				} else {
					dto.setCompletedOn(APIConstants.NOT_INITIATED);
				}
			}
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", gson.toJsonTree(dto));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retrieving the details.";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("errMsg", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean isCompRequestEligible(String gstin, String taxPeriod) {

		Optional<Gstr1EinvSeriesCompEntity> isCompAllowed = gstr1EinvSeriesCompRepo
				.findByGstinAndReturnPeriodAndRequestStatusInAndIsActiveTrue(
						gstin, taxPeriod, STATUS);
		return isCompAllowed.isPresent();
	}

	private Long persisEinvAutoConfigDetails(String entityId, String retPeriod,
			String gstin, String userName) {
		try {
			Gstr1EinvSeriesCompEntity entity = new Gstr1EinvSeriesCompEntity();
			entity.setEntityId(Long.valueOf(entityId));
			entity.setGstin(gstin);
			entity.setReturnPeriod(retPeriod);
			entity.setRequestStatus(STATUS.get(0));
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy(userName);
			entity.setActive(true);
			entity.setModifiedOn(LocalDateTime.now());
			gstr1EinvSeriesCompRepo.save(entity);
			return entity.getId();
		} catch (Exception ee) {
			String msg = String.format(
					"Exception occured while persisting"
							+ " recon report Config Entity for " + "  :%s",
					LocalDate.now());
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private boolean isInvSeriesOpted(Long entityId) {
		boolean isOpted = false;
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId, "A",
				"Do you want DigiGST to auto compute invoice series for GSTR1 ?");
		if (Strings.isNullOrEmpty(optAns)) {
			isOpted = false;
		} else {
			if ("B".equalsIgnoreCase(optAns)) {
				isOpted = false;
			} else {
				isOpted = true;
			}
		}
		return isOpted;
	}

	private String invSeriesOptedImpl(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"Invoice Series to be computed by DigiGST , basis below options");
		return optAns;
	}

	private boolean isDataAvail(String gstin, String retPeriod) {
		int outCount = docRepository.isOutDataAvaCount(gstin, retPeriod);
		int inwCount = inDocRepository.isInvDataAvaCount(gstin, retPeriod);
		if (outCount > 0 || inwCount > 0) {
			return true;
		}
		return false;
	}

	private void postInvSeriesAsyncJob(String entityId, String gstin,
			String retPeriod, String optedImpl, String userName) {
		Optional<Gstr1EinvSeriesCompEntity> isAvai = gstr1EinvSeriesCompRepo
				.findByGstinAndReturnPeriodAndIsActiveTrue(gstin, retPeriod);
		if (isAvai.isPresent()) {
			gstr1EinvSeriesCompRepo.updateRequestStatus(gstin, retPeriod);
		}
		persisEinvAutoConfigDetails(entityId, retPeriod, gstin,
				userName);
		
//		JsonObject jobParams = new JsonObject();
//		jobParams.addProperty("gstin", gstin);
//		jobParams.addProperty("retPeriod", retPeriod);
//		jobParams.addProperty("configId", configId);
//		jobParams.addProperty("implType", optedImpl);
//		asyncJobsService.createJob(TenantContext.getTenantId(),
//				JobConstants.GSTR1_EINV_SERIES_COMPUTE, jobParams.toString(),
//				userName, 1L, null, null);
	}
	
	//gstr1a code
	@PostMapping(value = "/ui/gstr1aDocSeriesAutoCompStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1aDocSeriesAutoCompStatus(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put(APIConstants.SUCCESS, "Completed");
		statusMap.put(APIConstants.INPROGRESS, "InProgress");
		statusMap.put(APIConstants.FAILED, "Failed");
		statusMap.put(APIConstants.INITIATED, "Initiated");
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1DocSeriesAutoComp Request received from UI as {} ",
						jsonString);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1DocSeriesCompDto dto = gson.fromJson(reqObject,
					Gstr1DocSeriesCompDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			String identi = dto.getIdentifier();
			if (identi.equalsIgnoreCase("CB")) {
				
				Optional<Gstr1AEinvSeriesCompEntity> isAvailable = gstr1aEinvSeriesCompRepo
						.findByGstinAndReturnPeriodAndIsActiveTrue(gstin,
								retPeriod);
				if (isAvailable.isPresent()) {
					dto.setCompletedOn(
							EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
									isAvailable.get().getModifiedOn())));
					dto.setRequestStatus(statusMap
							.get(isAvailable.get().getRequestStatus()));
				} else {
					dto.setRequestStatus(APIConstants.NOT_INITIATED);
				}
			} else {
				Optional<Gstr1AInvoiceFileUploadEntity> isAvailable = gstr1AInvoiceRepository
						.findTop1BySgstinAndReturnPeriodAndIsDeleteFalseOrderByCreatedOnDesc(
								gstin, retPeriod);
				if (isAvailable.isPresent()) {

					String dataOrg = "C".equalsIgnoreCase(
							isAvailable.get().getDataOriginType())
									? "DigiGST compute" : "File upload";
					dto.setCompletedOn(EYDateUtil
							.fmt(EYDateUtil.toISTDateTimeFromUTC(
									isAvailable.get().getCreatedOn()))
							+ "-" + dataOrg);
				} else {
					dto.setCompletedOn(APIConstants.NOT_INITIATED);
				}
			}
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", gson.toJsonTree(dto));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retrieving the details.";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("errMsg", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/ui/gstr1ADocseriesDelete", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> basicgstr1AVerticalRecordDelete(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();
			DocSeriesSDeleteReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), DocSeriesSDeleteReqDto.class);
			List<Long> id = ret1SummaryRequest.getId();
			int deleteRecord = gstr1AInvoiceRepository.deleteRecord(id);
			JsonObject resp = new JsonObject();
			if (deleteRecord != 0) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.getErrorStatus()));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "ui/gstr1adocseriesSaveAndEdit", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1AdocSeriesSaveAndEdit(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1VerticalDocSeriesRespDto>>() {
			}.getType();
			List<Gstr1VerticalDocSeriesRespDto> reqUserInputs = gson
					.fromJson(jsonObject, listType);
			JsonObject resp = gstr1ADocSeriesUpdateService
					.updateVerticalData(reqUserInputs);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}  catch (Exception ex) {
			String msg = "Unexpected error while fecthing Gstr1 Doc Series "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

}
