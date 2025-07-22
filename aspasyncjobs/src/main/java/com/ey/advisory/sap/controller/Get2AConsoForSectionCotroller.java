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

import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.services.jobs.erp.Get2AConsoForSectionHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Umesha.M
 *
 */
@RestController
public class Get2AConsoForSectionCotroller {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2AConsoForSectionCotroller.class);

	@Autowired
	@Qualifier("Get2AConsoForSectionHandler")
	private Get2AConsoForSectionHandler handler;

	@PostMapping(value = "/erpGet2AConsoForSection", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> erpGet2AConsoForSection(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject req = (new JsonParser()).parse(jsonReq).getAsJsonObject()
					.get("req").getAsJsonObject();

			Get2ARevIntReqDto dto = gson.fromJson(req, Get2ARevIntReqDto.class);
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			Integer responseCode = handler.erpToGet2AConsoForSection(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response Code:{}", responseCode);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
