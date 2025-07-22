package com.ey.advisory.gstr1A.controller;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.Gstr1SubmitGstnDto;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@RestController
public class Gstr1ASummaryPollingController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository saveStatusRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	Gstr1SummaryAtGstn gstr1SummaryGstnData;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@PostMapping(value = "/ui/gstr1AGetSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1GetSummary(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String groupCode = TenantContext.getTenantId();
			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			List<String> gstinList = null;
			Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest
					.getDataSecAttrs();
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}
			}
			Pair<String, String> msgStatuses = doGetCalls(gstinList.get(0),
					annexure1SummaryRequest.getTaxPeriod(), groupCode);
			String msg = msgStatuses.getValue0();
			String msgCode = msgStatuses.getValue1();
			LOGGER.debug("msgCode is {}", msgCode);
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto(msgCode, msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fetching summary data "
					+ "from GSTN " + ex;
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//DONE
	@PostMapping(value = "/ui/gstr1ABulkGetSumm", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1BulkGetSumm(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");

			JsonArray gstinArray = request.get(APIConstants.GSTIN_LIST)
					.getAsJsonArray();
			String retPeriod = request.get(APIConstants.RETPERIOD)
					.getAsString();
			JsonArray respBody = new JsonArray();

			for (JsonElement gstin : gstinArray) {
				JsonObject json = new JsonObject();
				Pair<String, String> msgStatues = doGetCalls(
						gstin.getAsString(), retPeriod, groupCode);
				String msg = msgStatues.getValue0();
				json.addProperty("gstin", gstin.getAsString());
				json.addProperty("msg", msg);
				respBody.add(json);
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*@GetMapping(value = "/ui/downloadGstr1AGetSumErrResp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadGstr1GetSumErrResp(HttpServletRequest request,
			HttpServletResponse response) {
		JsonObject resp = new JsonObject();
		try {
			String refId = request.getParameter("refId");
			if (refId == null || refId.isEmpty()) {
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto("E", "refId is Mandatory")));
				response.getWriter().println(resp.toString());
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin download save status in GSTR1 with id : %s ",
						refId.toString());
				LOGGER.debug(msg);
			}

			downloadGstr1GetSummErrDetails(refId, response);

		} catch (Exception ex) {
			String msg = " Exception while Downloading the 3B Error Json ";
			LOGGER.error(msg, ex);
		}
	}*/

	/*private void downloadGstr1GetSummErrDetails(String refId,
			HttpServletResponse response) throws Exception {
		File tempFile = null;
		try {
			Gstr1SaveBatchEntity entity = saveStatusRepo.findByRefId(refId);

			Clob respData = entity.getGetResponsePayload();

			String errorRep = GenUtil.convertClobtoString(respData);

			String fileName = String.format("Gstr1ErrorResponse_%s_%s",
					entity.getSgstin(), entity.getReturnPeriod());

			tempFile = File.createTempFile(fileName, ".json");
			FileWriter writer = new FileWriter(tempFile);
			writer.write(errorRep);
			writer.close();
			LOGGER.debug("Temp file has been created and written"
					+ " successfully for fileName{}", fileName);
			InputStream inputStream = new FileInputStream(tempFile);

			response.setContentType("application/json");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =%s ", fileName));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ee) {
			String msg = "Exception while downloading Gstr1 json error response";
			LOGGER.error(msg);
		} finally {
			GenUtil.deleteTempDir(tempFile);
			LOGGER.debug("Deleting the temporary file");
		}
	}
*/
	
	@PostMapping(value = "/ui/fetchGstr1AGetSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchGstr1GetSummary(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		String responseMsg = null;
		String msgCode = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> gstinList = null;
		try {
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			String groupCode = TenantContext.getTenantId();
			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest
					.getDataSecAttrs();
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}
			}

			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstinList.get(0));

			if (!"A".equalsIgnoreCase(authStatus)) {
				responseMsg = "Auth Token is Inactive, Please Activate.";
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto("E", responseMsg)));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();
			reqDto.setGstin(gstinList.get(0));
			reqDto.setReturnPeriod(annexure1SummaryRequest.getTaxPeriod());
			String getResp = gstr1SummaryGstnData.getGstr1ASummary(reqDto,
					groupCode);
			if (!getResp.equals("SUCCESS")) {
				responseMsg = getResp;
				msgCode = "E";
			} else {
				responseMsg = "Fetch Summary has been completed.";
				msgCode = "S";
			}

			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto(msgCode, responseMsg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = String.format(
					"Unexpected error while fetching summary data for selected GSTIN");
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/fetchGstr1ABulkGetSumm", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchGstr1BulkGetSumm(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");

			JsonArray gstinArray = request.get(APIConstants.GSTIN_LIST)
					.getAsJsonArray();
			String retPeriod = request.get(APIConstants.RETPERIOD)
					.getAsString();
			JsonArray respBody = new JsonArray();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> gstnsList = gson.fromJson(gstinArray, listType);
			Map<String, String> gstinAuthMap = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstins(gstnsList);

			for (JsonElement gstin : gstinArray) {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", gstin.getAsString());
				if (gstinAuthMap.get(gstin.getAsString()).equalsIgnoreCase("I")) {
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate.");
				} else {
					JsonObject jobj = new JsonObject();
					jobj.addProperty(APIConstants.GSTIN, gstin.getAsString());
					jobj.addProperty(APIConstants.RETPERIOD, retPeriod);
					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR1A_GET_SUMMARY, jobj.toString(),
							JobConstants.SYSTEM, JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);
					json.addProperty("msg",
							"Fetch Summary Call has been initiated.");
				}
				respBody.add(json);
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Pair<String, String> doGetCalls(String gstin, String returnPeriod,
			String groupCode) {

		String responseMsg = null;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);

		// String authStatus = "A";
		if (!"A".equalsIgnoreCase(authStatus)) {
			responseMsg = " Auth Token is InActive, Please Activate";
			return new Pair<>(responseMsg, "E");
		}
		
		Gstr1SaveBatchEntity existingEntry = saveStatusRepo
				.findGstr1APollingRefId(gstin, returnPeriod);

		if (existingEntry != null) {
			responseMsg = String.format(
					"Generate Summary (PTF) is already Inprogress for %s and %s",
					gstin, returnPeriod);
			return new Pair<>(responseMsg, "E");
		}
		Gstr1SubmitGstnDto dto = new Gstr1SubmitGstnDto();
		dto.setGstin(gstin);
		dto.setRet_period(returnPeriod);
		// dto.setGenerate_summary("Y");
		String data = gson.toJson(dto, Gstr1SubmitGstnDto.class);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("GSTR1A Generate summary (PTF) Gstn Call initiated"
							+ " for Polling with request: %s", data);
			LOGGER.debug(msg);
		}
		// GET Call//Proceed to File

		//TODO
		APIResponse apiResp = hitGstnServer.gstr1AProceedToFileApiCall(groupCode,
				data, gstin, returnPeriod, APIConstants.GSTR1A.toUpperCase(),
				false);

		Pair<String, String> isRefIdAvail = saveRefId(gstin, returnPeriod,
				groupCode, apiResp, userName);

		String apiStatus = isRefIdAvail.getValue0();

		if ("Success".equalsIgnoreCase(apiStatus)) {
			responseMsg = String.format(
					"GSTR1A Generate Summary (PTF) has been"
							+ " initiated Successfully. RefId: %s",
					isRefIdAvail.getValue1());
			return new Pair<>(responseMsg, "S");
		} else {
			LOGGER.debug("Error from GSTN");
			return new Pair<>(apiStatus, "E");
		}

	}

	private Pair<String, String> saveRefId(String gstin, String ret_period,
			String groupCode, APIResponse resp, String userName) {
		String refId = null;

		if (resp.isSuccess()) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR1A Generate Summary (PTF) Gstn Call Completed"
								+ " for Polling and recieved response as :%s",
						resp.getResponse());
				LOGGER.debug(msg);
			}

			String saveJsonResp = resp.getResponse();
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject) jsonParser.parse(saveJsonResp);
			refId = jsonObject.get(APIIdentifiers.REFERENECE_ID).getAsString();

			saveStatusRepo.softDeleteGstr1APollingEntries(gstin, ret_period);

			LocalDateTime now = LocalDateTime.now();

			Gstr1SaveBatchEntity entity = new Gstr1SaveBatchEntity();
			entity.setSgstin(gstin);
			entity.setReturnPeriod(ret_period);
			entity.setReturnType(APIConstants.GSTR1A.toUpperCase());
			entity.setOperationType("Generate Summary (PTF)");
			entity.setGstnStatus("INITIATED");
			entity.setCreatedOn(now);
			entity.setCreatedBy(userName);
			entity.setModifiedOn(now);
			entity.setModifiedBy(userName);
			entity.setGstnRespDate(now);
			entity.setSection("GET_SUM");
			entity.setDerivedTaxperiod(GenUtil.getDerivedTaxPeriod(ret_period));
			entity.setRefId(refId);
			entity.setStatus(APIConstants.GET_INITIATED);
			saveStatusRepo.save(entity);
			return new Pair<>("Success", refId);
		} else {
			String errorCode = resp.getError().getErrorCode();
			String errorDesc = resp.getError().getErrorDesc();
			LOGGER.error(
					"Generate GSTR1A summary failed for gstin{} and taxPeriod{}."
							+ " ErrorCode: {} and ErrorDesc: {}",
					gstin, ret_period, errorCode, errorDesc);
			return new Pair<>(resp.getError().getErrorDesc(), null);

		}
	}

}
