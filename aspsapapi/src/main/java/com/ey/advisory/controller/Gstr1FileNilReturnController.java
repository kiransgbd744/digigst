package com.ey.advisory.controller;

import java.time.LocalDateTime;
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

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.app.docs.dto.Gstr1SubmitGstnDto;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva/Kiran
 *
 */

@Slf4j
@RestController
public class Gstr1FileNilReturnController {

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
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private GstnCommonServiceUtil gstnCommUtil;

	@PostMapping(value = "/ui/gstr1FileNilReturn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1GetSummary(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
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
			JsonObject resp = new JsonObject();
			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstinList.get(0));
			if (!"A".equalsIgnoreCase(authStatus)) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.addProperty("resp",
						"Auth Token is InActive, Please Activate");
			} else {
				boolean isNilApp = gstnCommUtil.isNilRetApp(gstinList.get(0),
						annexure1SummaryRequest.getTaxPeriod());
				if (!isNilApp) {
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.addProperty("resp",
							"Nil Return cannot be filed as data is available in system");
				} else {
					Pair<String, APIRespDto> msgStatuses = doGetCalls(
							gstinList.get(0),
							annexure1SummaryRequest.getTaxPeriod(), groupCode);
					String msg = msgStatuses.getValue0();
					resp.add("hdr", gson.toJsonTree(msgStatuses.getValue1()));
					resp.addProperty("resp", msg);
				}
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Filing a Nil Return for selected GSTIN and Taxperiod.");
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", msg);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr1BulkFileNilReturn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1BulkGetSumm(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		String msg = null;
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
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin.getAsString());
				if (!"A".equalsIgnoreCase(authStatus)) {
					msg = " Auth Token is InActive, Please Activate";
				} else {
					boolean isNilApp = gstnCommUtil
							.isNilRetApp(gstin.getAsString(), retPeriod);
					if (!isNilApp) {
						msg = "Nil Return cannot be filed as data Available in system";
					} else {
						Pair<String, APIRespDto> msgStatues = doGetCalls(
								gstin.getAsString(), retPeriod, groupCode);
						msg = msgStatues.getValue0();
					}
				}
				json.addProperty("gstin", gstin.getAsString());
				json.addProperty("msg", msg);
				respBody.add(json);
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Pair<String, APIRespDto> doGetCalls(String gstin,
			String returnPeriod, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1SubmitGstnDto dto = new Gstr1SubmitGstnDto();
		dto.setGstin(gstin);
		dto.setRet_period(returnPeriod);
		dto.setIsnil("Y");
		String data = gson.toJson(dto, Gstr1SubmitGstnDto.class);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR1 File Nil Return Call initiated"
					+ " for Polling with request: %s", data);
			LOGGER.debug(msg);
		}
		APIResponse apiResp = hitGstnServer.gstnProceedToFileApiCall(groupCode,
				data, gstin, returnPeriod, APIConstants.GSTR1.toUpperCase(),
				true);
		Pair<String, String> isRefIdAvail = saveRefId(gstin, returnPeriod,
				groupCode, apiResp, userName, data);
		String apiStatus = isRefIdAvail.getValue0();
		if ("Success".equalsIgnoreCase(apiStatus)) {
			String responseMsg = String.format(
					"GSTR1 File Nil Return"
							+ " initiated Successfully. RefId: %s",
					isRefIdAvail.getValue1());
			return new Pair<>(responseMsg, APIRespDto.createSuccessResp());
		} else {
			LOGGER.debug("Error from GSTN");
			return new Pair<>(apiStatus, APIRespDto.creatErrorResp());
		}
	}

	private Pair<String, String> saveRefId(String gstin, String ret_period,
			String groupCode, APIResponse resp, String userName,
			String requestPayload) {
		String refId = null;
		try {
			if (resp.isSuccess()) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"GSTR1 File Nil Return Call Completed"
									+ " for Polling and recieved response as :%s",
							resp.getResponse());
					LOGGER.debug(msg);
				}
				String saveJsonResp = resp.getResponse();
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
						.getAsString();
				if (!Strings.isNullOrEmpty(refId)) {
					saveStatusRepo.softDeleteGstr1PollingEntries(gstin,
							ret_period, APIIdentifiers.NIL_RETURN);
					LocalDateTime now = LocalDateTime.now();
					Gstr1SaveBatchEntity entity = new Gstr1SaveBatchEntity();
					entity.setSgstin(gstin);
					entity.setReturnPeriod(ret_period);
					entity.setReturnType("GSTR1_" + APIIdentifiers.NIL_RETURN);
					entity.setOperationType(APIIdentifiers.NIL_RETURN);
					entity.setCreatedOn(now);
					entity.setCreatedBy(userName);
					entity.setModifiedOn(now);
					entity.setModifiedBy(userName);
					entity.setGstnRespDate(now);
					entity.setSection("NIL");
					entity.setDerivedTaxperiod(
							GenUtil.getDerivedTaxPeriod(ret_period));
					entity.setRefId(refId);
					entity.setSaveRequestPayload(
							new javax.sql.rowset.serial.SerialClob(
									requestPayload.toCharArray()));
					entity.setSaveResponsePayload(
							new javax.sql.rowset.serial.SerialClob(
									saveJsonResp.toCharArray()));
					entity.setStatus(APIConstants.COMPLETED);
					saveStatusRepo.save(entity);
				}
				return new Pair<>("Success", refId);
			} else {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				LOGGER.error(
						"Generate GSTR1 File Nil Return failed for gstin{} and taxPeriod{}."
								+ " ErrorCode: {} and ErrorDesc: {}",
						gstin, ret_period, errorCode, errorDesc);
				return new Pair<>(resp.getError().getErrorDesc(), null);
			}
		} catch (Exception e) {
			String msg = String.format(
					"Error occurred while saving the Ref Id for gstin %s and taxperiod %s",
					gstin, ret_period);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}
}
