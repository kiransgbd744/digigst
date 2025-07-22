package com.ey.advisory.admin.onboarding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.services.onboarding.DowntimeMaintenanceService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DownTimeMaintenanceReqDto;
import com.ey.advisory.core.dto.DownTimeMaintenanceRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author ashutosh.kar
 * 
 * 
 */
@RestController
public class DowntimeMaintenanceController {

	private static final Logger LOGGER = LoggerFactory
	        .getLogger(DowntimeMaintenanceController.class);
	
	@Autowired
	@Qualifier("DowntimeMaintenanceServiceImpl")
	private DowntimeMaintenanceService service;
	
	
	@PostMapping(value = "/updateDowntimeConfig", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGroupConfig(@RequestBody String jsonReqObj) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestReqObject = JsonParser.parseString(jsonReqObj)
			        .getAsJsonObject().get("req").getAsJsonObject();
			DownTimeMaintenanceReqDto dto = gson.fromJson(requestReqObject,
					DownTimeMaintenanceReqDto.class);
			DownTimeMaintenanceRespDto resDto = service
			       .updateConfigParmetr(dto);
			resp.add("resp", gson.toJsonTree(resDto));
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
