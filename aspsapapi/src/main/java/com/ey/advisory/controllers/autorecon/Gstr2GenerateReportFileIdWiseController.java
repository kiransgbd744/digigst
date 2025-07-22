package com.ey.advisory.controllers.autorecon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.filereport.ReportDownloadDto;
import com.ey.advisory.app.filereport.ReportFileStatusDownloadService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.verma
 *
 */
@RestController
@Slf4j
public class Gstr2GenerateReportFileIdWiseController {

	@Autowired
	@Qualifier("Gstr2GenerateReportFileIdWiseServiceImpl")
	private ReportFileStatusDownloadService service;

	@RequestMapping(value = "/ui/generateReportFileDownloadIdWise", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getdownloadDataforRecon(
			@RequestBody String jsonString) {

		LOGGER.debug("Inside Gstr2GenerateReportFileIdWiseController"
				+ ".reportFileDownloadIdWise() method and file type is {} ");

		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();
			Gson gson = new Gson();
			ReportDownloadDto reqDto = gson.fromJson(reqObject,
					ReportDownloadDto.class);

			List<ReportDownloadDto> respList = service
					.getDownloadData(reqDto.getConfigId());

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
