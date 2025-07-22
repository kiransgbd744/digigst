package com.ey.advisory.controllers.gstr3b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Table4d1ValidationStatusDto;
import com.ey.advisory.app.gstr3b.Table4d1ValidationtSaveUpdateServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author akhiles.yadav
 *
 */

@RestController
@Slf4j
public class Table4d1ValidationtSaveUpdateController {

	@Autowired
	@Qualifier("Table4d1ValidationtSaveUpdateServiceImpl")
	private Table4d1ValidationtSaveUpdateServiceImpl validationService;

	@PostMapping(value = "/ui/gstr3BValidationInputSave", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr3BUserInputSave(
			@RequestBody String jsonString) {
		Table4d1ValidationStatusDto dto = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Table4d1ValidationtSaveUpdateController gstr3BValidationInputSave");
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			boolean isValidated = false;

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			Table4d1ValidationStatusDto validationInput = new Gson()
					.fromJson(requestObject, Table4d1ValidationStatusDto.class);

			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			isValidated = validationService.isValidated(gstin, taxPeriod);

			if (isValidated) {
				validationService.update4D1ValidationStatus(gstin, taxPeriod);
			}

			dto = validationService.saveValidationStatusInput(gstin, taxPeriod,
					validationInput);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(dto));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/get4D1ValidationStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> get4D1ValidationStatus(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Table4d1ValidationtSaveUpdateController get4D1ValidationStatus");
			}
			Table4d1ValidationStatusDto get4d1ValidationStatus = null;
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			get4d1ValidationStatus = validationService
					.get4D1ValidationStatus(gstin, taxPeriod);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(get4d1ValidationStatus);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

	@PostMapping(value = "/ui/update4D1ValidationStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> update4D1ValidationStatus(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Table4d1ValidationtSaveUpdateController update4D1ValidationStatus");
			}

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			validationService.update4D1ValidationStatus(gstin, taxPeriod);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
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
}
