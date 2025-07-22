package com.ey.advisory.controller.gstr2b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.gstr2b.Gstr2BDashBoardRespDto;
import com.ey.advisory.app.gstr2b.Gstr2BDashboardService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Hema G M
 *
 */
@Slf4j
@RestController
public class Gstr2BDashboardController {

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr2BDashboardServiceImpl")
	private Gstr2BDashboardService service;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	
	@PostMapping(value = "/ui/gstr2getGSTR2bDashboard", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsandStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2BDashboardController"
						+ ".getGSTINsandStatus ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			Long enityId = reqJson.has("enityId")

					? reqJson.get("enityId").getAsLong() : 0L;

			String fy = reqJson.has("fy")

					? reqJson.get("fy").getAsString() : "";

			/*
			 * List<String> gstnsList =
			 * entityService.getGSTINsForEntity(enityId); if
			 * (LOGGER.isDebugEnabled()) { String msg = "EntityServiceImpl" +
			 * ".getGSTINsForEntity Preparing Response Object";
			 * LOGGER.debug(msg); }
			 */

			List<String> gstnsList = null;
			Map<String, String> stateNames = null;
			Map<String, String> authTokenStatus = null;
			try {
				List<Long> entityIds = new ArrayList<>();
				entityIds.add(enityId);
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				
				List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
				ttlGstinList = gstNDetailRepository
						.filterGstinBasedByRegType(ttlGstinList, regTypeList);
				dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);
				
				gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				stateNames = entityService.getStateNames(gstnsList);
				authTokenStatus = authTokenService
						.getAuthTokenStatusForGstins(gstnsList);

			} catch (Exception ex) {
				String msg = String.format(
						"Error while fetching Gstins from entityId ", ex);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			String startMonth = "04";
			String endMonth = "03";
			String appendMonthYear = null;
			String appendMonthYear1 = null;
			if (fy != null && !fy.isEmpty()) {
				String[] arrOfStr = fy.split("-", 2);
				appendMonthYear = arrOfStr[0] + startMonth;
				appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
			}
			int derivedStartPeriod = Integer.parseInt(appendMonthYear);

			int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

			List<Gstr2BDashBoardRespDto> statusData = service.getStatusData(
					gstnsList, derivedStartPeriod, derivedEndPeriod, stateNames,
					authTokenStatus);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(statusData);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2BDashboardController"
						+ ".getGSTINsandStatus ,before returning response";
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
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
