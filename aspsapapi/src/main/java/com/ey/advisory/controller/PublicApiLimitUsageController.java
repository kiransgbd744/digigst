package com.ey.advisory.controller;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.common.PublicApiLimitUsageService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
@Slf4j
@RestController
public class PublicApiLimitUsageController {

	@Autowired
	@Qualifier("PublicApiLimitUsageServiceImpl")
	private PublicApiLimitUsageService counterService;

	@GetMapping(value = "/ui/getLimitAndUsageCount", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLimitAndUsageCount() {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			String groupCode = TenantContext.getTenantId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getLimitAndUsageCount for groupCode{}",
						groupCode);
			}

			Pair<String, String> respStatus = counterService
					.getLimitAndUsageForGroupCode(groupCode);

			if ("S".equalsIgnoreCase(respStatus.getValue0())) {
				JsonElement respBody = gson.fromJson(respStatus.getValue1(),
						JsonElement.class);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));

			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respStatus.getValue1()));

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Limit Details";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}
}
