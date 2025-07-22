package com.ey.advisory.controllers.gstr1.einv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconGstinDetailsDto;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */

@Slf4j
@RestController
public class Gstr1EinvInitiateReconController {

	@Autowired
	@Qualifier("Gstr1EinvInitiateReconServiceImpl")
	private Gstr1EinvInitiateReconService gstinEinvService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@PostMapping(value = "/ui/getGstr1EinvGstinsList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGstr1EinvGstinList(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin Gstr1-Einv  to get Gstins :" + " %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			Long entityId = requestObject.has("entityId") && NumberUtils
					.isCreatable(requestObject.get("entityId").toString())
							? requestObject.get("entityId").getAsLong() : null;
			String taxPeriod = requestObject.get("taxPeriod").getAsString();

			if (Strings.isNullOrEmpty(taxPeriod) || entityId == null) {
				String msg = "EntityId or Taxperiod cannot be empty";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);

			Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();

			Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							outwardSecurityAttributeMap);
			List<String> gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

			if (gstnsList == null || gstnsList.isEmpty()) {
				String msg = "There is no gstins found for requested entity";
				errorResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				return new ResponseEntity<>(errorResp.toString(),
						HttpStatus.OK);
			}

			List<GSTNDetailEntity> gstnList = gstnDetailRepository
					.getRegandSezGstins(gstnsList);

			List<Gstr1EinvInitiateReconGstinDetailsDto> listResp = gstinEinvService
					.getGstins(gstnList, taxPeriod);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(listResp);
			gstinDetResp.add("gstinDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End Gstr2-Einv with Gstins"
								+ " before returning response : %s",
						gstinDetResp.toString());
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

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/ui/gstr1EinvInitiateMatching", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> Gstr1EinvInitiateReconcile(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Begin Gstr1EinvInitiateReconcile to"
						+ " Initiate Recon : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();

			String entId = requestObject.get("entityId").getAsString();
			Long entityId = Long.valueOf(entId);

			JsonArray gstins = requestObject.getAsJsonArray("gstins");
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);

			String status = gstinEinvService.initiatRecon(gstinlist, taxPeriod,
					entityId);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End Gstr1Einv InitiateRecon to Initiate Recon"
								+ " before returning response : %s",
						gstinDetResp);
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}
	
	
	@PostMapping(value = "/ui/gstr1PRvsSubmittedInitiateMatching", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> Gstr1PRvsSubmittedInitiateReconcile(
			@RequestBody String jsonString){

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Begin Gstr1PRvsEinvInitiateReconcile to"
						+ " Initiate Recon : %s", jsonString);
				LOGGER.debug(msg);
			}
			
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();
			String reqJson = requestObject.get("req").getAsJsonObject().toString();
			String entId = requestObject.get("req").getAsJsonObject().get("entityId")
					.getAsString();
			
			Long entityId = Long.valueOf(entId);
			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);
			Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest.getDataSecAttrs();
			
			String gstin = null;

			List<String> gstinlist = null;
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						gstin = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinlist = dataSecAttrs.get(OnboardingConstant.GSTIN);
						}
					}

				}
			}

			String fromTaxPeriod = requestObject.get("req").getAsJsonObject().get("taxPeriodFrom").getAsString();
			String toTaxPeriod = requestObject.get("req").getAsJsonObject().get("taxPeriodTo").getAsString();

			String status = gstinEinvService.initiatPRvsSubmRecon(gstinlist, fromTaxPeriod,toTaxPeriod,
					entityId);

			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End Gstr1Pr vs Submission InitiateRecon to Initiate Recon"
								+ " before returning response : %s",
						gstinDetResp);
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	
	}
}


