package com.ey.advisory.controllers.gstr3b;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Gstr3bComuteCopyToUserInputService;
import com.ey.advisory.app.gstr3b.Table4d1ValidationtSaveUpdateServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr3BCopyComuteToUserInputController {

	@Autowired
	@Qualifier("Gstr3bComuteCopyToUserInputServiceImpl")
	private Gstr3bComuteCopyToUserInputService copyService;

	@Autowired
	@Qualifier("Table4d1ValidationtSaveUpdateServiceImpl")
	private Table4d1ValidationtSaveUpdateServiceImpl validationService;

	@PostMapping(value = "/ui/getGstr3bCopyToUserInput", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BGenerate(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr3bComuteCopyToUserInputService");
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

			String inwardFlag = requestObject.has("inwardFlag")
					? requestObject.get("inwardFlag").getAsString() : null;

			String outwardFlag = requestObject.has("outwardFlag")
					? requestObject.get("outwardFlag").getAsString() : null;

			String interestAndLateFeeFlag = requestObject
					.has("interestAndLateFeeFlag")
							? requestObject.get("interestAndLateFeeFlag")
									.getAsString()
							: null;

			String respList = copyService.copyToUserInput(taxPeriod, gstin,
					inwardFlag, outwardFlag, interestAndLateFeeFlag);

			validationService.update4D1ValidationStatus(gstin, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Gstr3bComuteCopyToUserInputServiceImpl update4D1ValidationStatus() method called");
			}

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

	@PostMapping(value = "/ui/bulkGstr3bCopyToUserInput", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> bulkGstr3BGenerate(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE BulkGstr3bComuteCopyToUserInputService");
			}
			/*
			 * JsonObject requestObject = (new JsonParser()).parse(jsonString)
			 * .getAsJsonObject().getAsJsonObject("req");
			 */
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			JsonArray gstinArray = requestObject.has("gstinList")
					? requestObject.getAsJsonArray("gstinList")
					: new JsonArray();
			String inwardFlag = requestObject.has("inwardFlag")
					? requestObject.get("inwardFlag").getAsString() : null;

			String outwardFlag = requestObject.has("outwardFlag")
					? requestObject.get("outwardFlag").getAsString() : null;

			String interestAndLateFeeFlag = requestObject
					.has("interestAndLateFeeFlag")
							? requestObject.get("interestAndLateFeeFlag")
									.getAsString()
							: null;

			List<String> gstins = new ArrayList<>();
			for (JsonElement element : gstinArray) {
				gstins.add(element.getAsString());
			}
			if (gstins.size() <= 0)
				throw new AppException("gstin cannot be empty");
			String respList=null;
			for (String gstin : gstins) {
				
				 respList = copyService.copyToUserInput(taxPeriod, gstin,
						inwardFlag, outwardFlag, interestAndLateFeeFlag);

				validationService.update4D1ValidationStatus(gstin, taxPeriod);
			}

			

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Gstr3bComuteCopyToUserInputServiceImpl update4D1ValidationStatus() method called");
			}

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
}