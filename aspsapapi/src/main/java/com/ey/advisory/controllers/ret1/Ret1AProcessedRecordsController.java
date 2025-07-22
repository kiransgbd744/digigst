package com.ey.advisory.controllers.ret1;

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

import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsRequestDto;
import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsResponseDto;
import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsService;
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
public class Ret1AProcessedRecordsController {

	@Autowired
	@Qualifier("Ret1AProcessedRecordsServiceImpl")
	private Ret1AProcessedRecordsService ret1AProcessedRecordsService;

	@PostMapping(value = "/ui/getRet1AProcessedRecords", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchProcessedRecords(
			@RequestBody String jsonString) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			JsonObject reqObj = new JsonParser().parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonObject();
			Ret1AProcessedRecordsRequestDto dto = gson.fromJson(reqObj,
					Ret1AProcessedRecordsRequestDto.class);
			List<Ret1AProcessedRecordsResponseDto> ret1AProcessedRecordsResponseDto = ret1AProcessedRecordsService
					.fetchProcessedRecords(dto);

			JsonElement respBody = gson
					.toJsonTree(ret1AProcessedRecordsResponseDto);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while getting the ret-1A processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", gson.toJsonTree(new ArrayList<>()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
