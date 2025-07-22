package com.ey.advisory.controller.gl.recon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.ey.advisory.controller.gl.recon.GLProcessedRecordsRespDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ram Sundar
 *
 */
@Slf4j
@RestController
public class GLProcessedSummaryController {

	
	
	@Autowired
	@Qualifier("GLProcessedSummaryServiceImpl")
	GLProcessedSummaryServiceImpl glProcessedSummaryServiceImpl;

	@RequestMapping(value = "/ui/glProcessedSummary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			
			GLProcessedSummaryReqDto glProcessedSummaryReqDto = gson
					.fromJson(json, GLProcessedSummaryReqDto.class);

			SearchResult<GLProcessedRecordsRespDto> respDtos = glProcessedSummaryServiceImpl
					.find(glProcessedSummaryReqDto, null,
							GLProcessedRecordsRespDto.class);


			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respDtos.getResult()));

			if(LOGGER.isDebugEnabled()){
			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving EInvoice  processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
