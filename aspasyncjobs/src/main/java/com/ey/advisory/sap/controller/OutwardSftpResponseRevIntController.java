/**
 * 
 */
package com.ey.advisory.sap.controller;

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

import com.ey.advisory.app.docs.dto.erp.OutwardSftpRequestDto;
import com.ey.advisory.app.services.jobs.erp.OutwardSftpResponseRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class OutwardSftpResponseRevIntController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardSftpResponseRevIntController.class);

	@Autowired
	@Qualifier("OutwardSftpResponseRevIntHandler")
	private OutwardSftpResponseRevIntHandler handler;

	@PostMapping(value = "/ui/getSftpResponse.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getSftpResponse(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			OutwardSftpRequestDto dto = gson.fromJson(reqObj,
					OutwardSftpRequestDto.class);
			// Group Code	
			String groupCode = TestController.staticTenantId();
			TenantContext.setTenantId(groupCode);
			dto.setGroupcode(groupCode);
			
				Integer value = handler.getOutwardSftpResp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Succes : {}", value);
				}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}