package com.ey.advisory.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataDto;
import com.ey.advisory.app.data.entities.simplified.client.Gstr6StatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6StatusRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CalculateTurnOverRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeGstr1SummaryRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeGstr1SummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeTimeStampResponseDto;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.app.gstr1.einv.Gstr6CredDistDaoImpl;
import com.ey.advisory.app.services.daos.gstr6.Gstr6CalculateTurnOverGstnService;
import com.ey.advisory.app.services.daos.gstr6.Gstr6CalculateTurnOverGstnServiceImpl;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author SriBhavya
 *
 */
@RestController
public class Gstr6CalculateTurnOverComputationController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6CalculateTurnOverComputationController.class);

	@Autowired
	@Qualifier("Gstr6CalculateTurnOverGstnServiceImpl")
	private Gstr6CalculateTurnOverGstnService gstr6CalculateTurnOverGstnService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr6CalculateTurnOverGstnServiceImpl")
	private Gstr6CalculateTurnOverGstnServiceImpl reportStatus;

	@Autowired
	@Qualifier("Gstr6CredDistDaoImpl")
	private Gstr6CredDistDaoImpl gstinEinvService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr6StatusRepository")
	private Gstr6StatusRepository gstr6StatusRepository;

	@RequestMapping(value = "/ui/Gstr6CalculateTurnOverGstn", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6CalTurnOverGstnData(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject respData = new JsonObject();
		LOGGER.debug("Request data {}", jsonString);
		Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
				Gstr6CalculateTurnOverRequestDto.class);
		List<String> isdGstins = criteria.getIsdGstin();
		try {

			LOGGER.debug("Response data begin");
			String groupCode = TenantContext.getTenantId();
			List<String> stausList = Arrays.asList("INITIATED", "INPROGRESS");
			Long entityId = criteria.getEntityId();
			String taxPeriod = criteria.getTaxPeriod();
			String taxPeriodFrom = criteria.getTaxPeriodFrom();
			String taxPeriodTo = criteria.getTaxPeriodTo();
			List<String> gstins = criteria.getGstin();
			List<String> isdGstin = criteria.getIsdGstin();

			List<String> activeGstinsList = getAllActiveGstnList(gstins);
			//List<String> activeGstinsList = Arrays.asList("33GSPTN0481G1ZA");
			if (activeGstinsList == null || activeGstinsList.isEmpty()) {
				String msg = String.format(
						"No active GSTINs found for GSTR -6 Turnover Computation");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(msg);
				}
				msg = String.format(
						"No active Token for GSTR -6 Turnover Computation.");
				respData = createResponse(msg, APIRespDto.creatErrorResp());

				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			}

			if ((gstins != null && !gstins.isEmpty())
					&& !Strings.isNullOrEmpty(taxPeriodFrom)
					&& !Strings.isNullOrEmpty(taxPeriodTo)) {

				String userName = SecurityContext.getUser()
						.getUserPrincipalName() != null
								? SecurityContext.getUser()
										.getUserPrincipalName()
								: "SYSTEM";
				Gstr6StatusEntity entity = null;
				String gstinStatus = "";
				Long batchId= 0L;
				
				List<Gstr6StatusEntity> entities = gstr6StatusRepository
						.getGstinStatus(isdGstins, taxPeriod);

				if (entities != null && !entities.isEmpty()) {
					entity = entities.get(0);
					gstinStatus = entity.getGstinStatus()!=null ? 
							 entity.getGstinStatus() : "";
					
					if (stausList.contains(gstinStatus)) {
						String msg = String.format(
								"GSTR6 Turnover Computation for ISD GSTN : %s and"
										+ " Tax Period : %s is %s you can not reinitiate it.",
								isdGstins.toString(), taxPeriod, gstinStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(msg);
						}
						respData = createResponse(msg,
								APIRespDto.createSuccessResp());
						return new ResponseEntity<>(respData.toString(),
								HttpStatus.OK);
					}
					
				}
				int count = gstr6StatusRepository.softDelete(
						LocalDateTime.now(), isdGstins, taxPeriod);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("NO of soft delete : {}", count);
				}
				
				entity = gstr6StatusRepository.save(makeGstr6Entity(criteria));
				if (entity != null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("New record inserted with id {} ",
								entity.getId());
					}
					criteria.setId(Arrays.asList(entity.getId().intValue()));
					criteria.setBatchId(entity.getBatchId());
				}
				
				criteria.setGstins(criteria.getGstin());
				criteria.setGstin(null);
				criteria.setActiveGstinsList(activeGstinsList);
				String jobParam = gson.toJson(criteria);
				
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR6_CALC_TURNOVER_ENTITY_GSTIN_LEVEL,
						jobParam, userName, 1L, null, null);
				
				/*
				List<String> taxperoidList = gstr6CalculateTurnOverGstnService
						.getListOfTaxperoids(criteria);
				List<Pair<String, String>> listOfPairs = gstr6CalculateTurnOverGstnService
						.getListOfCombinationPairs(activeGstinsList, taxperoidList);
				
				gstr6CalculateTurnOverGstnService.getGstr6CalTurnOverGstnData(
						listOfPairs, groupCode, activeGstinsList, criteria,
						isdGstins);
				*/		
				
				String msg = "Turnover Computation for GSTN is initiated Successfully for "
						+ "the Selected Active GSTINS";
				respData = createResponse(msg, APIRespDto.createSuccessResp());
				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			} else {
				gstr6CalculateTurnOverGstnService.updateGstnStatus(criteria,APIConstants.FAILED);
				String msg = String.format(
						"No Data to Compute GSTR6 Turnover for "
								+ "Tax period %s , Entity Id %s and ISDGSTN %s .",
						taxPeriod, entityId, isdGstin);
				respData = createResponse(msg, APIRespDto.creatErrorResp());
				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			gstr6CalculateTurnOverGstnService.updateGstnStatus(criteria,APIConstants.FAILED);
			String msg = String.format(
					"Exception occured while Computing GSTR6 Turnover for "
							+ "Tax period %s , Entity Id %s and ISDGSTN %s .",
					criteria.getTaxPeriod(), criteria.getEntityId(),criteria.getIsdGstin());
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private List<String> getAllActiveGstnList(List<String> gstinsList) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Getting all the active GSTNs for GSTR6 TurnOver GSTN");
		}
		// String msg = "";
		List<String> activeGstins = new ArrayList<>();
		// JsonArray respBody = new JsonArray();
		if (gstinsList != null) {
			for (String gstin : gstinsList) {
				// JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (authStatus.equals("A")) {
					activeGstins.add(gstin);
				} /*
					 * else { msg = "Auth Token is Inactive, Please Activate";
					 * json.addProperty("gstin", gstin); json.addProperty("msg",
					 * msg); respBody.add(json); }
					 */
			}
			gstinsList.clear();
			gstinsList.addAll(activeGstins);
		}
		return gstinsList;
	}

	@RequestMapping(value = "/ui/Gstr6CalculateTurnOverDigiGst", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6CalTurnOverDigiGstData(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
					Gstr6CalculateTurnOverRequestDto.class);
			LOGGER.debug("Response data begin");
			gstr6CalculateTurnOverGstnService
					.getGstr6CalTurnOverDigiGstData(criteria);
			JsonArray respBody = new JsonArray();
			String msg = "Turnover Computation for DigiGST is initiated Successfully";
			JsonObject jsonBody = new JsonObject();
			jsonBody.addProperty("msg", msg);
			respBody.add(jsonBody);
			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/Gstr6ComputeCreditDistribution", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ComputeCreditDistributionData(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
					Gstr6CalculateTurnOverRequestDto.class);
			// gstr6CalculateTurnOverGstnService.getGstr6ComputeCreditDistributionData(criteria);
			gstr6CalculateTurnOverGstnService
					.gstr6ComputeCredDistData(criteria);
			JsonArray respBody = new JsonArray();
			String msg = "Please click on Request ID wise Link to download the Reports";
			JsonObject jsonBody = new JsonObject();
			jsonBody.addProperty("msg", msg);
			respBody.add(jsonBody);
			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/Gstr6ComputeTurnOverUserInput", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ComputeTurnOverUserInputData(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
					Gstr6CalculateTurnOverRequestDto.class);
			LOGGER.debug("Response data begin");
			if (criteria.getFromPeriod() == null) {
				criteria.setFromPeriod("0");
			}
			gstr6CalculateTurnOverGstnService
					.getGstr6ComputeTurnOverUserInputData(criteria);
			JsonArray respBody = new JsonArray();
			String msg = "GSTR6 Compute Turnover User Input is successfully updated";
			JsonObject jsonBody = new JsonObject();
			jsonBody.addProperty("msg", msg);
			respBody.add(jsonBody);
			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/Gstr6ComputeTimeStamp", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ComputeTimeStamp(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
					Gstr6CalculateTurnOverRequestDto.class);
			LOGGER.debug("Response data begin");
			Gstr6ComputeTimeStampResponseDto timeStampDto = gstr6CalculateTurnOverGstnService
					.getGstr6ComputeTimeStamp(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(timeStampDto);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/Gstr6ComputeGstr1SummaryStatus", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ComputeGstr1SummaryStatus(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6ComputeGstr1SummaryRequestDto criteria = gson.fromJson(json,
					Gstr6ComputeGstr1SummaryRequestDto.class);
			LOGGER.debug("Response data begin");
			List<Gstr6ComputeGstr1SummaryResponseDto> SummaryData = gstr6CalculateTurnOverGstnService
					.getGstr1SummaryStatus(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(SummaryData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static void main(String[] args) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = JsonParser
				.parseString(
						"{\"req\":{\"entityId\":\"63\",\"taxPeriod\":\"072024\",\"taxPeriodFrom\":\"072024\",\"taxPeriodTo\":\"072024\",\"gstin\":[\"33ABOPS9546G1Z3\"],\"tableType\":[]}}")
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
				Gstr6CalculateTurnOverRequestDto.class);

		criteria.setGstins(criteria.getGstin());

		String str1 = gson.toJson(criteria);
		System.out.println(str1);
		criteria.setGstin(null);
		String str2 = gson.toJson(criteria);
		System.out.println(str2);

	}

	@RequestMapping(value = "/ui/Gstr6CalculateTurnOverGstnProcessed", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedGstr6CalTurnOverGstnData(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject respData = new JsonObject();
		LOGGER.debug("Request data {} ", jsonString);
		Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
				Gstr6CalculateTurnOverRequestDto.class);
		// gstin is Isd gstin here  {isdGstin =null from UI}
		List<String> isdGstins = criteria.getGstin();
		criteria.setIsdGstin(isdGstins);
		try {

			LOGGER.debug("Response data begin");
			String groupCode = TenantContext.getTenantId();
			List<String> stausList = Arrays.asList("INITIATED", "INPROGRESS");
			Long entityId = criteria.getEntityId();
			String taxPeriod = criteria.getTaxPeriod();
			String taxPeriodFrom = criteria.getTaxPeriodFrom();
			String taxPeriodTo = criteria.getTaxPeriodTo();
			
			List<String> gstins = gstr6CalculateTurnOverGstnService
					.getRegGstins(entityId);
			List<String> activeGstinsList = getAllActiveGstnList(gstins);
			//List<String> activeGstinsList = Arrays.asList("33GSPTN0481G1ZA");
			if (activeGstinsList == null || activeGstinsList.isEmpty()) {
				String msg = String.format(
						"No active GSTINs found for GSTR -6 Turnover Computation");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(msg);
				}
				msg = String.format(
						"No active Token for GSTR -6 Turnover Computation.");
				respData = createResponse(msg, APIRespDto.creatErrorResp());
				
				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			}

			if ((gstins != null && !gstins.isEmpty())
					&& !Strings.isNullOrEmpty(taxPeriodFrom)
					&& !Strings.isNullOrEmpty(taxPeriodTo)) {

				String userName = SecurityContext.getUser()
						.getUserPrincipalName() != null
								? SecurityContext.getUser()
										.getUserPrincipalName()
								: "SYSTEM";
				Gstr6StatusEntity entity = null;
				String gstinStatus = "";
				
				List<Gstr6StatusEntity> entities = gstr6StatusRepository
						.getGstinStatus(isdGstins, taxPeriod);

				if (entities != null && !entities.isEmpty()) {
					entity = entities.get(0);
					gstinStatus = entity.getGstinStatus()!=null ? 
							 entity.getGstinStatus() : "";
					
					if (stausList.contains(gstinStatus)) {
						String msg = String.format(
								"GSTR6 Turnover Computation for ISD GSTN : %s and"
										+ " Tax Period : %s is %s you can not reinitiate it.",
								isdGstins.toString(), taxPeriod, gstinStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(msg);
						}
						respData = createResponse(msg,
								APIRespDto.createSuccessResp());
						return new ResponseEntity<>(respData.toString(),
								HttpStatus.OK);
					}
					
				}
				
				int count = gstr6StatusRepository.softDelete(
						LocalDateTime.now(), isdGstins, taxPeriod);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("NO of soft delete : {}", count);
				}

				entity = gstr6StatusRepository.save(makeGstr6Entity(criteria));
				if (entity != null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("New record inserted with id {} ",
								entity.getId());
					}
					criteria.setId(Arrays.asList(entity.getId().intValue()));
					criteria.setBatchId(entity.getBatchId());
				}						

				criteria.setGstins(criteria.getGstin());
				criteria.setGstin(null);
				criteria.setActiveGstinsList(activeGstinsList);
				String jobParam = gson.toJson(criteria);
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR6_CALC_TURNOVER_ENTITY_GSTIN_LEVEL,
						jobParam.toString(), userName, 1L, null, null);

				String msg = "Turnover Computation for GSTN is initiated Successfully for "
						+ "the Selected Active GSTINS";
				respData = createResponse(msg, APIRespDto.createSuccessResp());
				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			} else {
				gstr6CalculateTurnOverGstnService.updateGstnStatus(criteria,APIConstants.FAILED);
				String msg = String.format(
						"No Data to Compute GSTR6 Turnover for "
								+ "Tax period %s , Entity Id %s and ISDGSTN %s .",
						taxPeriod, entityId, gstins, isdGstins);
				respData = createResponse(msg, APIRespDto.creatErrorResp());
				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			}

		} catch (Exception ex) {
			gstr6CalculateTurnOverGstnService.updateGstnStatus(criteria,APIConstants.FAILED);
			String msg = String.format(
					"Exception occured while Computing GSTR6 Turnover for "
							+ "Tax period %s , Entity Id %s and ISDGSTN %s .",
					criteria.getTaxPeriod(), criteria.getEntityId(),isdGstins);
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/Gstr6CalculateTurnOverDigiGstProcessed", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ProcessedCalTurnOverDigiGstData(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(json,
					Gstr6CalculateTurnOverRequestDto.class);
			LOGGER.debug("Response data begin");
			Long entityId = criteria.getEntityId();
			if (entityId != null) {
				gstr6CalculateTurnOverGstnService
						.getGstr6CalTurnOverDigiGstProcessedData(criteria,
								entityId);
				JsonArray respBody = new JsonArray();
				String msg = "Turnover Computation for DigiGST is initiated Successfully";
				JsonObject jsonBody = new JsonObject();
				jsonBody.addProperty("msg", msg);
				respBody.add(jsonBody);
				JsonObject resps = new JsonObject();
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonArray respBody = new JsonArray();
				JsonObject respData = new JsonObject();
				String msg = "EntityId is Empty, Please Provide EntityId to Compute.";
				JsonObject jsonBody = new JsonObject();
				jsonBody.addProperty("msg", msg);
				respBody.add(jsonBody);
				respData.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				respData.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(respData.toString(), HttpStatus.OK);
			}

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/getGstr6CredDistReqIdWise", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6CredDistReqIdWise(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin getGstr6CredDistReqIdWise"
						+ ".getGstr6CredDistReqIdWise ,Parsing Input request";
				LOGGER.debug(msg);
			}

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			JsonObject request = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = request.get("req").getAsJsonObject();

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(json,
					Gstr2InitiateReconReqDto.class);

			List<Gstr6ComputeCredDistDataDto> status = reportStatus
					.getGstr6reqIdWiseScreenData(reqDto, userName);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr6ComputeCredDistData "
						+ ".getReportRequestStatus, before returning response";
				LOGGER.debug(msg);
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
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/gstr6CredDistRptDwnldButton", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr6CredDistRptDwnldButton(
			@RequestBody String jsonString) {

		try {

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String requestId = json.get("requestId").getAsString();

			LOGGER.debug("Inside gstr6CredDistRptDwnldButton controller"
					+ " requestId : %s ", requestId);

			List<Gstr1EinvRequesIdWiseDownloadTabDto> resp = gstinEinvService
					.getGstr6CredDistData(Long.valueOf(requestId));
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/gstr6CredDistZDownload")
	public void gstr6CredDistZDownload(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject errorResp = new JsonObject();

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"gstr6CredDistZDownload reports");
		Document document = null;
		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String documentName = null;
			String docId = json.has("DocId") ? json.get("DocId").getAsString()
					: null;
			// String fileFolder = "Gstr1ReconReports";

			if (docId != null && !docId.isEmpty()) {

				String msg = String.format("Doc Id is available  and Doc Id %s",
						docId);
				LOGGER.debug(msg);
				document = DocumentUtility.downloadDocumentByDocId(docId);
				documentName = document.getName();
				if (LOGGER.isDebugEnabled()) {
					String str = String.format("file name is : %s",
							documentName);
					LOGGER.debug(str);
				}

			}

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition", String
						.format("attachment; filename = " + documentName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			String msg = "Error occured while generating gstr6 cred distribution report report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();
		}

	}
	
	private JsonObject createResponse(String msg, APIRespDto resType) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject respData = new JsonObject();
		JsonArray respBody = new JsonArray();
		JsonObject msgJson = new JsonObject();
		msgJson.addProperty("msg", msg);
		respBody.add(msgJson);
		respData.add("hdr", gson.toJsonTree(resType));
		respData.add("resp", gson.toJsonTree(respBody));

		return respData;
	}

	private Gstr6StatusEntity makeGstr6Entity(
			Gstr6CalculateTurnOverRequestDto dto) {

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

		Gstr6StatusEntity entity = new Gstr6StatusEntity();
		entity.setBatchId(getNextBatchId());
		if (dto.getIsdGstin() != null) {
			entity.setIsdGstin(dto.getIsdGstin().get(0));
		}
		entity.setGstin(null);
		if (dto.getTaxPeriod() != null) {
			entity.setCurrentRetPeriod(dto.getTaxPeriod());
		}
		if (dto.getTaxPeriodFrom() != null) {
			entity.setFromDerRetPeroid(
					GenUtil.convertTaxPeriodToInt(dto.getTaxPeriodFrom()));
		}
		if (dto.getTaxPeriodTo() != null) {
			entity.setToDerRetPeroid(
					GenUtil.convertTaxPeriodToInt(dto.getTaxPeriodTo()));
		}
		entity.setGstinStatus(APIConstants.INITIATED);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setGstinTimeStamp(LocalDateTime.now());

		return entity;

	}
	
	public Long getNextBatchId() {
        Long maxBatchId = gstr6StatusRepository.findMaxBatchId();
        if (maxBatchId == null) {
            maxBatchId = 0L;
        }
        return maxBatchId + 1;
    }
}
