package com.ey.advisory.controllers.aspreports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.aspreports.MasterAspReportsDetailsRequestDto;
import com.ey.advisory.app.services.aspreports.MasterAspReportsDetailsResponseDto;
import com.ey.advisory.app.services.aspreports.MasterAspReportsResponseDto;
import com.ey.advisory.app.services.aspreports.MasterAspReportsService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@RestController
public class MasterAspReportsController {

	@Autowired
	@Qualifier("MasterAspReportsServiceImpl")
	private MasterAspReportsService masterAspReportsService;

	@PostMapping(value = "/ui/getMasterReports", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMasterReports() throws Exception {
		try {
			MasterAspReportsResponseDto aspReportsResponseDto = masterAspReportsService
					.getMasterAspReports();

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(aspReportsResponseDto);
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting the master asp reports";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/getDetailsReports", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDetailsReports(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			MasterAspReportsDetailsRequestDto masterDetailsReportsRequestDto = gson
					.fromJson(reqJson.toString(),
							MasterAspReportsDetailsRequestDto.class);

			MasterAspReportsDetailsResponseDto resp = masterAspReportsService
					.fetchDetailsReports(masterDetailsReportsRequestDto);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting the master details reports";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
