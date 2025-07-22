package com.ey.advisory.controllers.gstr3b.entity.setoff;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityDashboardService;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3bSetOffEntityDashboardRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Gstr3bSetOffEntityDashboardController {

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityDashboardServiceImpl")
	Gstr3BSetOffEntityDashboardService dashboardService;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	private static final String GSTIN = "gstins";

	@PostMapping(value = "/ui/gstr3bSetOffEntityDashboard", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsandStatus(
			@RequestBody String jsonString) {
		
		JsonObject errorResp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr3bSetOffEntityDashboardController"
						+ ".getGSTINsandStatus ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			JsonArray gstins = new JsonArray();

			List<String> gstinsList = null;
			Long entityId = reqJson.has("entityId")

					? reqJson.get("entityId").getAsLong() : 0L;

			String taxPeriod = reqJson.has("taxPeriod")

					? reqJson.get("taxPeriod").getAsString() : "";

			if (reqJson.has(GSTIN)
					&& reqJson.getAsJsonArray(GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstins = reqJson.getAsJsonArray(GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstinsList = gson.fromJson(gstins, listType);
			}
			
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);
			
			List<String> gstnsList = null;
			
			if (gstinsList != null && !gstinsList.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstnsList = gstinsList;
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invoking service to get gstins for entity");
				}
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				
				gstnsList = gstinDetailRepo.getRegularandSezGstins(gstnsList);
			}
			if (gstnsList == null || gstnsList.isEmpty()) {
				String msg = "Gstins cannot be empty";
				errorResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				LOGGER.error(msg);
				return new ResponseEntity<>(errorResp.toString(),
						HttpStatus.OK);
			}

			List<Gstr3bSetOffEntityDashboardRespDto> statusData = dashboardService
					.getStatusData(gstnsList, taxPeriod);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(statusData);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
		
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr3BDashboardController"
						+ ".getGSTINsandStatus ,before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
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
