package com.ey.advisory.controller.gstr2b;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2GetGstr2bController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private GetAnx1BatchRepository getAnx1BatchRepo;

	@RequestMapping(value = "/ui/getGstr2BResp", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> createGstr2BGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> gstnsList = null;
		JsonArray gstins = new JsonArray();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2GetGstr2bController.createGstr2BGstnGetJob() "
							+ "method Request received from UI as {} ",
					request);
		}
		try {
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
			String groupCode = TenantContext.getTenantId();

			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject().getAsJsonObject("req");

			if ((requestObject.has("gstins"))
					&& (requestObject.getAsJsonArray("gstins").size() > 0)) {
				gstins = requestObject.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = gson.fromJson(gstins, listType);
			}

			List<String> activeGstins = getAllActiveGstnList(gstnsList);
			if (activeGstins == null || activeGstins.isEmpty()) {
				String msg = String.format("Auth Token(s) is Inactive, "
						+ "Please Activate gstn(s)");
				LOGGER.error(msg);
				throw new AppException();

			}

//			Map<String, Config> callDateConfigMap = configManager.getConfigs(
//					"GSTR2B_BLOCK_GETCALL", "gstr2b.block.getcall",
//					TenantContext.getTenantId());

			String configValue = "N";
			// N means blocking is not required.
			// Y means blocking is required.
			if (configValue.equalsIgnoreCase("N")) {
				asyncJobsService.createJob(groupCode, JobConstants.GSTR2B_GET,
						request, userName, 1L, null, null);
				return new ResponseEntity<String>(getResponse().toString(),
						HttpStatus.OK);
			}

			String response = postGstr2BGetCall(request, activeGstins,
					userName);
			return new ResponseEntity<String>(response, HttpStatus.OK);

		} catch (AppException e) {
			String msg = "Auth Token(s) is Inactive,Please Activate gstn(s)";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while Creating job for GET GSTR2B ";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private String postGstr2BGetCall(String request, List<String> activeGstins,
			String userName) {

		Map<String, List<String>> find2bGetCallRequest = new HashMap<String, List<String>>();
		JsonArray months = new JsonArray();
		List<String> monthsList = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		String financialYear = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2GetGstr2bController going to Block GSTR2B get call which is scucess"
								+ "and success_with_no_data ",
						request);
			}
			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject().getAsJsonObject("req");

			if ((requestObject.has("month"))
					&& (requestObject.getAsJsonArray("month").size() > 0)) {
				months = requestObject.getAsJsonArray("month");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				monthsList = gson.fromJson(months, listType);
			}
			if ((requestObject.has("fy")) && requestObject.get("fy") != null) {
				financialYear = requestObject.get("fy").getAsString();
			}

			List<String> requestDtolist = new ArrayList<>();
			List<String> findActive2BGetCall = new ArrayList<>();
			List<AsyncExecJob> gstr2BAsyncJobs = new ArrayList<>();

			Pair<Integer, Integer> derivedTaxPeriods = GenUtil
					.getDerivedTaxPeriodsBasedOnMonthAndFY(monthsList,
							financialYear);
			List<GetAnx1BatchEntity> gstr2bGetCallStatuses = getAnx1BatchRepo
					.findGSTR2bGetAllStatus(activeGstins,
							derivedTaxPeriods.getValue0(),
							derivedTaxPeriods.getValue1());

			if (gstr2bGetCallStatuses == null
					|| gstr2bGetCallStatuses.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr2GetGstr2bController no records found for Block"
									+ "all records will be proccessed ",
							request);
				}
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTR2B_GET, request, userName, 1L, null,
						null);
				return getResponse().toString();
			} else {
				List<String> gstr2b2GetCallSuccessMap = gstr2bGetCallStatuses
						.stream()
						.map(entity -> entity.getSgstin() + "_"
								+ entity.getTaxPeriod())
						.collect(Collectors.toList());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Blocked GSTR2B Get Call Count : "
							+ gstr2b2GetCallSuccessMap.size());
				}

				requestDtolist = getAll2BGetCallList(activeGstins,
						GenUtil.getTaxPeriodsBasedOnMonthAndFY(monthsList,
								financialYear));

				findActive2BGetCall = findActive2BGetCall(requestDtolist,
						gstr2b2GetCallSuccessMap);

				find2bGetCallRequest = find2bGetCallRequest(
						findActive2BGetCall);

				if (find2bGetCallRequest != null
						&& !find2bGetCallRequest.isEmpty()) {

					for (String key : find2bGetCallRequest.keySet()) {
						String nRequest = getRequest(Arrays.asList(key),
								find2bGetCallRequest.get(key), financialYear);

						gstr2BAsyncJobs.add(asyncJobsService.createJobAndReturn(
								TenantContext.getTenantId(),
								JobConstants.GSTR2B_GET, nRequest, userName, 1L,
								null, null));

					}
				}
				if (!gstr2BAsyncJobs.isEmpty()) {
					asyncJobsService.createJobs(gstr2BAsyncJobs);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Return GSTR2B GET:{} Based On Config N");
				}
			}
			return getResponse().toString();
		} catch (Exception e) {
			String msg = "Unexpected error while Creating job for GET GSTR2B ";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	private List<String> getAllActiveGstnList(List<String> gstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR2B GET");
		}
		Map<String, String> authStatusMap = authTokenService
				.getAuthTokenStatusForGstins(gstins);

		List<String> activeGstin = new ArrayList<>();

		gstins.forEach(o -> {
			String authStatus = authStatusMap.get(o);
			if (!"A".equalsIgnoreCase(authStatus)) {

				String msg = String.format("Auth Token is Inactive, "
						+ "Please Activate gstin %s ", o);
				LOGGER.debug(msg);
			} else {
				activeGstin.add(o);
			}
		});

		return activeGstin;
	}

	private JsonObject getResponse() {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		APIRespDto dto = new APIRespDto("Sucess",
				"Requested GSTR2B GET successfully");

		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);

		return resp;
	}

	private Map<String, List<String>> find2bGetCallRequest(List<String> map) {
		return map.stream().map(string -> string.split("_"))
				.filter(parts -> parts.length == 2)
				.collect(Collectors.groupingBy(parts -> parts[0],
						Collectors.mapping(parts -> parts[1].substring(0, 2),
								Collectors.toList())));
	}

	private List<String> findActive2BGetCall(List<String> requestDtoList,
			List<String> successList) {

		if (successList == null || successList.isEmpty()) {
			return requestDtoList;
		}

		return requestDtoList.stream()
				.filter(item -> !successList.contains(item))
				.collect(Collectors.toList());

	}

	private List<String> getAll2BGetCallList(List<String> gstins,
			List<String> monthsList) {

		return gstins.stream()
				.flatMap(gstin -> monthsList.stream()
						.map(taxPeriod -> gstin + "_" + taxPeriod))
				.collect(Collectors.toList());
	}

	private String getRequest(List<String> gstins, List<String> months,
			String finYear) {
		JsonObject req = new JsonObject();
		req.add("gstins", new Gson().toJsonTree(gstins).getAsJsonArray());
		req.add("month", new Gson().toJsonTree(months).getAsJsonArray());
		req.addProperty("fy", finYear);

		JsonObject finalReq = new JsonObject();
		finalReq.add("req", req);

		return finalReq.toString();
	}

}
