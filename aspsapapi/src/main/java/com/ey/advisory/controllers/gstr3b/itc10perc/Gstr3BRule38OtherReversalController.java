package com.ey.advisory.controllers.gstr3b.itc10perc;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.itc10perc.service.Gstr3BItc10PercDto;
import com.ey.advisory.app.gstr3b.itc10perc.service.Gstr3BRule38OtherReversalService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Gstr3BRule38OtherReversalController {

	@Autowired
	@Qualifier("Gstr3BRule38OtherReversalServiceImpl")
	private Gstr3BRule38OtherReversalService othReveralService;

	@PostMapping(value = "/ui/get3bRule38OtherReverals", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> get3bOtherReverals(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr3bTable4OtherReversalController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			List<Gstr3BItc10PercDto> respList = othReveralService
					.getOtherReversalData(gstin, taxPeriod);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
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

	@PostMapping(value = "/ui/save3bRule38OtherReverals", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> save3bOtherReverals(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			JsonElement userInputElement = requestObject.get("userInputList");
			Type listType = new TypeToken<List<Gstr3BGstinAspUserInputDto>>() {
			}.getType();

			List<Gstr3BGstinAspUserInputDto> userInputList = new Gson()
					.fromJson(userInputElement, listType);

			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			othReveralService.saveOthReversalChangesToUserInput(gstin,
					taxPeriod, userInputList);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}  catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while saving other reversal changes";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}
}
