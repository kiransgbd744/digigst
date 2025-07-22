package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.processors.handler.Itc04SaveToGstnResetHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Slf4j
@RestController
public class Itc04SaveToGstnResetController {

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private Itc04SaveToGstnResetHandler resetHandler;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepo;

	@PostMapping(value = "/ui/itc04SaveToGstnResetAndSaveJob", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createItc04SaveToGstnJob(@RequestBody String jsonParam) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Itc04SaveToGstnJob Request received from UI as {} ", jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonObject requestObject = (new JsonParser()).parse(jsonParam).getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject, Gstr1SaveToGstnReqDto.class);
			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			List<Pair<String, String>> gstr1Pairs = screenExtractor.getItc04CombinationPairs(dto, groupCode);
			
			JsonArray respBody = getAllActiveGstnList(gstr1Pairs);			
			if (gstr1Pairs == null || gstr1Pairs.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR1 save");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				for (int i = 0; i < gstr1Pairs.size(); i++) {
					Pair<String, String> pair = gstr1Pairs.get(i);
					if (!gstnUserRequestUtil.isNextSaveRequestEligible(pair.getValue0(), pair.getValue1(), "SAVE",
							"ITC04", groupCode)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg", "ITC04 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;
					}
					// If reset Save then sections are mandatory to select
					List<String> sections = dto.getTableSections();
					if (dto.getIsResetSave()) {
						if (sections == null || sections.isEmpty()) {
							JsonObject json = new JsonObject();
							json.addProperty("gstin", pair.getValue0());
							json.addProperty("msg", "No Section is selected to reset the SAVE ITC04");
							respBody.add(json);
							continue;
						}
					}
					// reset and create new async job
					resetHandler.itc04ResetAndCreateJob(pair, dto, groupCode);
					JsonObject json = new JsonObject();
					json.addProperty("gstin", pair.getValue0());
					json.addProperty("msg",
							"Save initiated for selected(active) GSTINs. Please review after 15 minutes.");
					respBody.add(json);
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private JsonArray getAllActiveGstnList(List<Pair<String, String>> gstr1Pairs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for ITC04 save");
		}
		String msg = "";
		List<Pair<String, String>> activeGstins = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (gstr1Pairs != null) {
			for (Pair<String, String> pair : gstr1Pairs) {
				String gstin = pair.getValue0();
				String taxPeriod = pair.getValue1();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
				if (authStatus.equals("A")) {
					activeGstins.add(new Pair<>(gstin, taxPeriod));
				} else {
					msg = "Auth Token is Inactive, Please Activate";
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				}
			}
			gstr1Pairs.clear();
			gstr1Pairs.addAll(activeGstins);
		}
		return respBody;
	}

	// Section Selection method
	@PostMapping(value = "/ui/itc04SaveToGstnSectionSelection", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> itc04SaveSectionSelection(@RequestBody String request) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("itc04SaveSectionSelection Request received from UI as {} ", request);
		}
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(request).getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);
			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("groupCode {} is set", groupCode);
			}
			JsonArray respBody = new JsonArray();			
			for (Gstr1GetInvoicesReqDto dto : dtos) {				
				List<String> savedSections = batchSaveStatusRepo.findRerurnSavedSections(
						APIConstants.ITC04.toUpperCase(), dto.getGstin(), dto.getReturnPeriod());				
				String sections = savedSections.toString().replaceAll(", ", "', '")
						.replace("[", "['").replace("]","']");
				JsonObject json = new JsonObject();
				json.addProperty("gstin", dto.getGstin());
				json.addProperty("sections", sections);
				respBody.add(json);
			}
			APIRespDto apiResp = APIRespDto.createSuccessResp();
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception e) {
			String msg = "Unexpected error while fetching  Itc04 Save Reset sections";
			LOGGER.error(msg, e.getMessage());
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
