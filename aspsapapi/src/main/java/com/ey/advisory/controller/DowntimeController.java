package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 * 
 * @author ashutosh.kar
 * 
 * 
 */
@RestController
public class DowntimeController {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(DowntimeController.class);

	@RequestMapping(value = "/ui/downTimeMaintenanceMsg", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getDownTimeMsg() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Config downTimeConfig = configManager.getConfig("DOWNTIMECONFIG", "downtime_maintenance");

			if (downTimeConfig == null || downTimeConfig.getValue() == null
					|| downTimeConfig.getValue().trim().isEmpty()) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(""));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			String downTimeMsg = downTimeConfig.getValue();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("DownTime message: {}", downTimeMsg);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(downTimeMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			String errorMsg = "Unexpected error while fetching DownTime message.";
			LOGGER.error(errorMsg, e);

			resp.add("hdr", gson.toJsonTree(new APIRespDto("E", errorMsg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
