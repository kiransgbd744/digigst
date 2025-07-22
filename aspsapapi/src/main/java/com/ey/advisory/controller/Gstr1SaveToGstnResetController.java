/**
 * 
 */
package com.ey.advisory.controller;

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

import com.ey.advisory.app.processors.handler.Gstr1SaveToGstnResetHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class Gstr1SaveToGstnResetController {

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private Gstr1SaveToGstnResetHandler resetHandler;
	
	//GSTR1 SAVE functionality
	
	@PostMapping(value = "/ui/gstr1SaveToGstnResetAndSaveJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1SaveToGstnJob(
			@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1SaveToGstnResetAndSaveJob Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);

			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);

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

				boolean isSuccessMsgAddedInResp = false;
				for (int i = 0; i < gstr1Pairs.size(); i++) {

					Pair<String, String> pair = gstr1Pairs.get(i);

					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.SAVE, APIConstants.GSTR1.toUpperCase(),
							groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}
					// If reset Save then sections are mandatory to select
					// 21-01-2021 Commenting this part because sections
					// selection mandate for SaveUnsavedData also
					// if (dto.getIsResetSave()) {
					List<String> sections = dto.getTableSections();
					if (sections == null || sections.isEmpty()) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"No Section is selected to GSTR1 SAVE");
						respBody.add(json);
						continue;
					}
					// }

					// reset and create new async job
					resetHandler.createSectionWiseJobs(pair, dto, groupCode);

					if(!isSuccessMsgAddedInResp) {
					JsonObject json = new JsonObject();
					/*json.addProperty("gstin", pair.getValue0());*/
					json.addProperty("msg",
							/*"GSTR1 Save Request Submitted Successfully"*/
							"GSTR1 Save for selected(active) GSTINs is initiated. Please check the save status");
					respBody.add(json);
					
					isSuccessMsgAddedInResp = true;
					}

				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private JsonArray getAllActiveGstnList(
			List<Pair<String, String>> gstr1Pairs) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR1 save");
		}

		String msg = "";
		List<Pair<String, String>> activeGstins = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (gstr1Pairs != null) {
			for (Pair<String, String> pair : gstr1Pairs) {
				String gstin = pair.getValue0();
				String taxPeriod = pair.getValue1();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
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

	// GSTR1 ASP flags Reset functionality
	@PostMapping(value = "/ui/gstr1SaveToGstnReset", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> resetAspData(@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1SaveToGstnReset Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.gsonWithDisableHtmlEscpaing();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);

			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);

			JsonArray respBody = new JsonArray();

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				boolean isSuccessMsgAddedInResp = false;
				for (int i = 0; i < gstr1Pairs.size(); i++) {

					Pair<String, String> pair = gstr1Pairs.get(i);

					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.SAVE, APIConstants.GSTR1.toUpperCase(),
							groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}

					List<String> sections = dto.getTableSections();
					if (sections == null || sections.isEmpty()) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"No Section is selected to GSTR1 section reset at DigiGST");
						respBody.add(json);
						continue;
					}
					// reset the asp data
					resetHandler.resetAspTableData(pair, dto, APIConstants.ASP);
					if(!isSuccessMsgAddedInResp) {
					JsonObject json = new JsonObject();
					/*json.addProperty("gstin", pair.getValue0());*/
					json.addProperty("msg",
							/*"GSTR1 Reset Request is Successful"*/
							"GSTR1 section reset at DigiGST is successful");
					respBody.add(json);
					
					 isSuccessMsgAddedInResp = true;
					}

				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while Reseting";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// GSTR1 Non responded ASP flags Reset functionality
	@PostMapping(value = "/ui/gstr1AspResetNonRespondedDataOnly", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> resetAspNonrespondedDataOnly(@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1AspResetNonRespondedDataOnly Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);

			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);

			JsonArray respBody = new JsonArray();

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				boolean isSuccessMsgAddedInResp = false;
				for (int i = 0; i < gstr1Pairs.size(); i++) {

					Pair<String, String> pair = gstr1Pairs.get(i);

					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.SAVE, APIConstants.GSTR1.toUpperCase(),
							groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}

					List<String> sections = dto.getTableSections();
					if (sections == null || sections.isEmpty()) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"No Section is selected to GSTR1 section No Response reset at DigiGST");
						respBody.add(json);
						continue;
					}
					// reset the non responded asp data
					resetHandler.resetNonRespondedAspTableData(pair, dto, APIConstants.ASP);
					if(!isSuccessMsgAddedInResp) {
					JsonObject json = new JsonObject();
					/*json.addProperty("gstin", pair.getValue0());*/
					json.addProperty("msg",
							/*"GSTR1 No Response Reset Request is Successful"*/
							"GSTR1 section No Response reset at DigiGST is successful");
					respBody.add(json);
					
					
					isSuccessMsgAddedInResp = true;
					}

				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while Reseting";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//Delete AutoDrafted GSTN Data(Delete GSTN data based on deletion file) functionality
	@PostMapping(value = "/ui/gstr1DeleteGstnDataJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteAutoDraftedGstnData(
			@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1DeleteGstnDataJob Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);

			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);

			JsonArray respBody = getAllActiveGstnList(gstr1Pairs);

			if (gstr1Pairs == null || gstr1Pairs.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR1 Delete");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				boolean isSuccessMsgAddedInResp = false;
				for (int i = 0; i < gstr1Pairs.size(); i++) {

					Pair<String, String> pair = gstr1Pairs.get(i);

					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.DELETE_FILE_UPLOAD,
							APIConstants.GSTR1.toUpperCase(), groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 Delete (File upload) is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}

					// create new async job for each section
					resetHandler.createDeleteGstnJob(pair, dto, groupCode);
					if(!isSuccessMsgAddedInResp) {
					JsonObject json = new JsonObject();
					/*json.addProperty("gstin", pair.getValue0());*/
					json.addProperty("msg",
							/*"GSTR1 Delete Request Submitted Successfully"*/
							"GSTR1 Delete (File upload) for selected(active) GSTINs is initiated. Please check the save status");
					respBody.add(json);
					
					isSuccessMsgAddedInResp = true;
					}

				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	////Delete AutoDrafted GSTN Data(Delete Full GSTN Data) functionality
	@PostMapping(value = "/ui/gstr1GstnResetJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstnReset(@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1GstnResetJob Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);

			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);

			JsonArray respBody = getAllActiveGstnList(gstr1Pairs);

			if (gstr1Pairs == null || gstr1Pairs.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR1 Delete");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				boolean isSuccessMsgAddedInResp = false;
				for (int i = 0; i < gstr1Pairs.size(); i++) {

					Pair<String, String> pair = gstr1Pairs.get(i);

					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.SAVE, APIConstants.GSTR1.toUpperCase(),
							groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}

					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.DELETE_FULL_DATA,
							APIConstants.GSTR1.toUpperCase(), groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 Delete (Full Data) is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}
					// create new async job for each section
					resetHandler.createResetGstnJob(pair, dto, groupCode);

					if(!isSuccessMsgAddedInResp) {
					JsonObject json = new JsonObject();
					/*json.addProperty("gstin", pair.getValue0());*/
					json.addProperty("msg",
							/*"GSTR1 Reset Request Submitted Successfully"*/
							"GSTR1 Delete (Full Data) for selected(active) GSTINs is initiated. Please check the save status");
					respBody.add(json);
					
					isSuccessMsgAddedInResp =true;
					}

				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
