package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8UploadPsdRepository;
import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("Gstr8SaveToGstnServiceImpl")
public class Gstr8SaveToGstnServiceImpl implements Gstr8SaveToGstnService {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	@Qualifier("Gstr8UploadPsdRepository")
	private Gstr8UploadPsdRepository gstr8UploadPsdRepository;

	@Autowired
	@Qualifier("Gstr8SummaryAtGstnImpl")
	private Gstr8SummaryAtGstnImpl gstr8SummaryAtGstn;

	@Override
	public ResponseEntity<String> saveGstr8DataToGstn(String requestBody) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonArray respBody = new JsonArray();
		String userName = SecurityContext.getUser().getUserPrincipalName();
		List<AsyncExecJob> asyncJobs = new ArrayList<>();
		boolean isSuccessMsgAddedInResp = false;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1SaveToGstnResetAndSaveJob Request received from UI as {} ",
						requestBody);
			}
			String groupCode = TenantContext.getTenantId();
			LOGGER.info("groupCode {} is set", groupCode);
			JsonObject requestObject = JsonParser.parseString(requestBody)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			List<String> gstnList = dtos.stream().map(dto -> dto.getGstin())
					.collect(Collectors.toList());
			String retPeriod = dtos.get(0).getReturnPeriod();
			Map<String, String> gstinAuthMap = authTokenService
					.getAuthTokenStatusForGstins(gstnList);
			for (String gstin : gstnList) {
				String authStatus = gstinAuthMap.get(gstin);
				if (!"A".equalsIgnoreCase(authStatus)) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate");
					respBody.add(json);
					continue;
				}
				if (!gstnUserRequestUtil.isNextSaveRequestEligibleGstr8(gstin,
						retPeriod)) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"GSTR8 SAVE is Inprogress, New request Cannot be taken.");
					respBody.add(json);
					continue;
				}

				Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
						gstin, retPeriod, APIConstants.SAVE,
						APIConstants.GSTR8.toUpperCase(), groupCode, userName,
						false, false, false);

				SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(gstin,
						retPeriod, userRequestId, null);
				String sectionJson = gson.toJson(sectionDto);
				asyncJobs.add(asyncJobsService.createJobAndReturn(groupCode,
						JobConstants.GSTR8_SAVETOGSTN, sectionJson, userName,
						JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS));
				if (!isSuccessMsgAddedInResp) {
					JsonObject json = new JsonObject();
					json.addProperty("msg",
							"GSTR8 Save for selected(active) GSTINs is initiated. Please check the save status");
					respBody.add(json);
					isSuccessMsgAddedInResp = true;
				}
			}
			asyncJobsService.createJobs(asyncJobs);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving GSTR8.";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<String> getGstr8Summary(String requestBody) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonArray respBody = new JsonArray();
		try {
			String groupCode = TenantContext.getTenantId();
			LOGGER.info("groupCode {} is set", groupCode);
			JsonObject requestObject = JsonParser.parseString(requestBody)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			List<String> gstnList = dtos.stream().map(dto -> dto.getGstin())
					.collect(Collectors.toList());
			String retPeriod = dtos.get(0).getReturnPeriod();
			Map<String, String> gstinAuthMap = authTokenService
					.getAuthTokenStatusForGstins(gstnList);
			for (String gstin : gstnList) {
				String authStatus = gstinAuthMap.get(gstin);
				if (!"A".equalsIgnoreCase(authStatus)) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate");
					respBody.add(json);
					continue;
				}
				Anx2GetInvoicesReqDto summDto = new Anx2GetInvoicesReqDto();
				summDto.setGstin(gstin);
				summDto.setReturnPeriod(retPeriod);
				Pair<String, String> gstr8SummaryStatus = gstr8SummaryAtGstn
						.getGstr8Summary(summDto, groupCode);
				JsonObject json = new JsonObject();
				json.addProperty("gstin", gstin);
				json.addProperty("msg", gstr8SummaryStatus.getValue1());
				respBody.add(json);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String errMsg = String.format(
					"Failed to fetch GSTR-8 Summary Data. Please try again or contact support.");
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", errMsg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
