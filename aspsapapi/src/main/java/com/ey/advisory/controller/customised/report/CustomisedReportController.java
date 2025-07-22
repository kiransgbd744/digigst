package com.ey.advisory.controller.customised.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.customisedreport.CustomisedReportReqDto;
import com.ey.advisory.app.data.services.customisedreport.CustomisedReportService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@RestController
public class CustomisedReportController {

	@Autowired
	@Qualifier("CustomisedReportServiceImpl")
	private CustomisedReportService custServiceImpl;

	@PostMapping(value = "/ui/saveCustReptData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveCustReptData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		LOGGER.debug("The selected criteria for getGlData is:-> {}", jsonString);
		try {

			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			
			CustomisedReportReqDto reqDto = gson.fromJson(reqJson,
					CustomisedReportReqDto.class);
			
			//JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			String status = custServiceImpl.saveSelectedFields(reqDto);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/api/saveCustReptData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> apisaveCustReptData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		LOGGER.debug("The selected criteria for getGlData is:-> {}", jsonString);
		try {

			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			
			CustomisedReportReqDto reqDto = gson.fromJson(reqJson,
					CustomisedReportReqDto.class);
			
			//JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			String status = custServiceImpl.saveSelectedFields(reqDto);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/ui/getCustReptData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCustReptData(HttpServletRequest request,
			HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			String entityId = request.getParameter("entityId");
			String reportType = request.getParameter("reportType");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			JsonObject respObject = custServiceImpl.getSelectedFields(entityId,
					reportType);
			dataObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			dataObj.add("resp", gson.toJsonTree(respObject));
			return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
