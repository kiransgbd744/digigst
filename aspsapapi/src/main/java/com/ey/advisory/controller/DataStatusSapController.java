package com.ey.advisory.controller;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.DataStatusSapEntity;
import com.ey.advisory.app.data.repositories.client.DataStatusSapRepository;
import com.ey.advisory.app.docs.dto.erp.DataStatusSapDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
/**
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class DataStatusSapController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusSapController.class);
	
	@Autowired
	private DataStatusSapRepository dataStatusSapRepository;
		
	@PostMapping(value = "/ui/sapControlRecord", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> requestForApproval(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			DataStatusSapDto dto = gson.fromJson(reqJson,
					DataStatusSapDto.class); 
			ModelMapper modelmapper = new ModelMapper();
			DataStatusSapEntity entity = modelmapper.map(dto, 
					DataStatusSapEntity.class);
			entity.setDerivedRetPeriod(dto.getReturnPeriod() != null ? 
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()) : null);
			entity.setCreatedBy(APIConstants.SYSTEM);
			entity.setCreatedOn(LocalDateTime.now());
			DataStatusSapEntity saved = dataStatusSapRepository.save(entity);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(saved);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
