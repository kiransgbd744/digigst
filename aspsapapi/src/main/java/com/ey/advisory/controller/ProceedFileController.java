package com.ey.advisory.controller;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.ProceedFileDto;
import com.ey.advisory.app.services.proceedtofile.Gstr6ProceedToFileService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@RestController
public class ProceedFileController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr6ProceedToFileServiceImpl")
	private Gstr6ProceedToFileService gstr6ProceedToFileService;

	@PostMapping(value = "/ui/proceedToFile", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProceedToFileData(
			@RequestBody String jsonParam) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ProceedToFile Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();

			ProceedFileDto dto = gson.fromJson(reqObject, ProceedFileDto.class);
			if ((dto.getGstin() != null && !dto.getGstin().isEmpty())
					&& (dto.getRet_period() != null
							&& !dto.getRet_period().isEmpty())) {
				JsonObject respBody = checkActiveGstin(dto.getGstin());
				if (respBody != null && !respBody.equals(new JsonObject())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"No active GSTNs found for GSTR6 ProceedToFile");
					}
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.add("resp", gson.toJsonTree(respBody));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
				String refId = gstr6ProceedToFileService
						.findProceedToFileStatus(reqObject.toString(), dto);
				String msg = "";
				JsonObject json = new JsonObject();
				if (refId != null && !refId.isEmpty()) {
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					msg = "ProceedToFile Is Successful";
					json.addProperty("RefId", refId);
					json.addProperty("msg", msg);
					//respBody.add(json);
					resp.add("resp", gson.toJsonTree(json));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				} else {
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					msg = "ProceedToFile Is Failed";
					// json.addProperty("RefId", refId);
					json.addProperty("msg", msg);
					//respBody.add(json);
					resp.add("resp", gson.toJsonTree(json));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
			} else {
				String msg = "Gstin/ReturnProid can not be null or Empty";
				LOGGER.error(msg);
				JsonObject respn = new JsonObject();
				respn.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				return new ResponseEntity<>(respn.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while ProceedToFile";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private JsonObject checkActiveGstin(String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Checking active GSTN for ProceedToFile");
		}
		String msg = "";
		JsonObject json = new JsonObject();
		if (gstin != null) {			
			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("A")) {
			} else {
				msg = "Auth Token is Inactive, Please Activate";
				json.addProperty("gstin", gstin);
				json.addProperty("msg", msg);
			}
		}
		return json;
	}

	@PostMapping(value = "/ui/getProceedToFileStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSaveGstnStatsu(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"getProceedToFileStatus request param is '%s'",
					jsonString));
		}
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ProceedFileDto dto = gson.fromJson(reqJson.toString(),
					ProceedFileDto.class);

			Quartet<String, String, String, String> latestStatusAndTime = gstr6ProceedToFileService
					.getLatestStatusAndTime(dto.getGstin(), dto.getRet_period(),
							APIConstants.GSTR6.toUpperCase());

			JsonObject response = new JsonObject();
			response.add("status",
					gson.toJsonTree(latestStatusAndTime.getValue0()));
			response.add("updatedDate",
					gson.toJsonTree(latestStatusAndTime.getValue1()));
			response.add("errorCode",
					gson.toJsonTree(latestStatusAndTime.getValue2()));
			response.add("errorDesc",
					gson.toJsonTree(latestStatusAndTime.getValue3()));

			JsonElement respBody = gson.toJsonTree(response);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error in getProceedToFileStatus ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}