package com.ey.advisory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.master.UomMasterEntity;
import com.ey.advisory.app.caches.DefaultUomCache;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
public class UnitOfManagementController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UnitOfManagementController.class);

	@Autowired
	DefaultUomCache defaultUomCache;

	@PostMapping(value = "/ui/getUnitOfManagement.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUnitOfManagement() {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		List<UomMasterEntity> uMasterEntity = defaultUomCache.getUomList();
		try {
			JsonElement jsonBody = gson.toJsonTree(uMasterEntity);
			resp.add("resp", jsonBody);
		} catch (Exception e) {
			LOGGER.error("Exception Occured ", e);
			resp.add("hdr",
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
