package com.ey.advisory.controllers.gstr1.einv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvResponseReconSummaryService;
import com.ey.advisory.app.gstr1.einv.ResponseSummaryDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@RestController
public class ResponseReconSummaryController {

	@Autowired
	@Qualifier("Gstr1EinvResponseReconSummaryServiceImpl")
	private Gstr1EinvResponseReconSummaryService respReconSummService;

	@PostMapping(value = "/ui/getResponseSumm", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getResponseSumm(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside getResponseSumm with request as " + " : %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("gstins");
			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			Gson googleJson = new Gson();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> rGstins = googleJson.fromJson(gstins, listType);

			if (CollectionUtils.isEmpty(rGstins))
				throw new AppException("User did not select any gstin");

			List<ResponseSummaryDto> respSummData = respReconSummService
					.getResponseSummData(rGstins, taxPeriod);

			String jsonRespSummData = gson.toJson(respSummData);
			JsonElement jsonElement = new JsonParser().parse(jsonRespSummData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("respSummary", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(
					"Exception while Processing the getResponseSummary " + ex));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/downloadRespReconReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadRespReconReport(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside downloadPreReconReport with request as "
								+ " : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("gstins");

			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			Long entityId = reqJson.get("entityId").getAsLong();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> recipientGstins = gson.fromJson(gstins, listType);

			if (CollectionUtils.isEmpty(recipientGstins))
				throw new AppException("User did not select any gstin");

			Workbook workBook = respReconSummService.getResponseSummReport(
					recipientGstins, taxPeriod, entityId);

			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.RESPONSE_RECON_SUMMARY);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			}

		} catch (Exception ex) {
			String msg = "Error occured while generating ResponseReconSummary report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();

		}
	}
}
