package com.ey.advisory.gstr2.ap.recon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.gstr2.recon.summary.Gstr2ConfigDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class Gstr2APAndNonAPReconResponseConfigListController {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	private Gstr2ReconConfigRepository reconConfigRepo;
	
	private static final String NON_AP_M_2APR = "NON_AP_M_2APR";
	private static final String AP_M_2APR = "AP_M_2APR";

	@PostMapping(value = "/ui/gstr2ApAndNonAPGetConfigIds", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<String> getConfigId(
				@RequestBody String jsonString) {

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "Begin Gstr2ReconResponseConfigListController"
							+ ".getConfigId() method";
					LOGGER.debug(msg);
				}
				
				String entityId = null;
				String toTaxPeriod = null;
				String fromTaxPeriod = null;
				String reconType = null;
				boolean flag = true;
				
				JsonObject requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();
				Gson gson = GsonUtil.newSAPGsonInstance();

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();

			}

			if (requestObject.has("toTaxPeriod")) {
				toTaxPeriod = requestObject.get("toTaxPeriod").getAsString();

			}

			if (requestObject.has("fromTaxPeriod")) {
				fromTaxPeriod = requestObject.get("fromTaxPeriod")
						.getAsString();

			}
			if (requestObject.has("reconType")) {
				reconType = requestObject.get("reconType")
						.getAsString();
			}
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "I27");
			
			if(optedEntities == null || optedEntities.isEmpty()){
				flag = false;
			}
			
			if (reconType != null && !reconType.equalsIgnoreCase("2BPR")) {
				reconType = flag ? AP_M_2APR : NON_AP_M_2APR;
			}
			
			Integer toTaxPeriodInt = GenUtil.convertTaxPeriodToInt(toTaxPeriod);
			Integer fromTaxPeriodInt = GenUtil
					.convertTaxPeriodToInt(fromTaxPeriod);
			List<Long> configList = reconConfigRepo.findAllNonAPConfigId(
					Long.valueOf(entityId), toTaxPeriodInt, fromTaxPeriodInt,
					reconType);

			List<Gstr2ConfigDto> configDtoList = configList.stream()
						.map(Gstr2ConfigDto::new).collect(Collectors.toList());
				JsonObject resp = new JsonObject();
				JsonObject configIdResp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(configDtoList);
				configIdResp.add("configIdList", respBody);
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", configIdResp);
				
				if (LOGGER.isDebugEnabled()) {
					String msg = "End Gstr2ReconSummaryConfigIdController"
							+ ".getConfigId, before returning response";
					LOGGER.debug(msg);
				}
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}catch(

	JsonParseException ex)
	{
		String msg = "Error while parsing the input Json";
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);

	}catch(
	Exception ex)
	{
		String msg = " Unexpected error occured";
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

}


