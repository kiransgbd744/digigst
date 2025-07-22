package com.ey.advisory.controllers.anexure2;

import java.lang.reflect.Type;
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

import com.ey.advisory.app.anx2.reconresponse.reviewsummary.ReconrResponseReviewSummaryDto;
import com.ey.advisory.app.anx2.reconresponse.reviewsummary.ReconrResponseReviewSummaryService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class ReconrResponseReviewSummaryController {

	@Autowired
	@Qualifier("ReconrResponseReviewSummaryServiceImpl")
	private ReconrResponseReviewSummaryService reconService;

	@PostMapping(value = "/ui/getReconResponseReviewSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResponseReviewSummary(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();
		Gson googleJson = new Gson();

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside getReconResponseReviewSummaryController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			JsonArray tableType = new JsonArray();
			JsonArray docType = new JsonArray();
			JsonArray type = new JsonArray();

			if (!requestObject.has("taxPeriod")
					|| !requestObject.has("gstin")) {
				throw new AppException("taxPeriod  or gstin cannot be empty");
			}
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			String gstin = requestObject.get("gstin").getAsString();
			List<String> docTypeList = null;
			List<String> tableTypeList = null;
			List<String> typeList = null;

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			if (requestObject.has("tableType")) {
				tableType = requestObject.get("tableType").getAsJsonArray();
				tableTypeList = googleJson.fromJson(tableType, listType);
			}

			if (requestObject.has("docType")) {
				docType = requestObject.get("docType").getAsJsonArray();
				docTypeList = googleJson.fromJson(docType, listType);
			}

			if (requestObject.has("type")) {
				type = requestObject.get("type").getAsJsonArray();
				typeList = googleJson.fromJson(type, listType);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking getReconResponseSummary() method gstin %s,  "
						+ "taxPeriod %s, docTypeList %s, tableTypeList %s, "
						+ " typeList %s",
						gstin, taxPeriod, docTypeList, tableTypeList, typeList);
				LOGGER.debug(msg);
			}
			
			List<ReconrResponseReviewSummaryDto> respList = reconService
					.getReconResponseSummary(taxPeriod, gstin, docTypeList,
							tableTypeList, typeList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Invoked getReconResponseSummary() "
						+ "method, respList : " + respList);
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
			String msg = "Unexpected error while retriving Data";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}
