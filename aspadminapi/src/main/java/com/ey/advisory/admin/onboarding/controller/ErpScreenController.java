package com.ey.advisory.admin.onboarding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx1.ErpGroupReqDto;
import com.ey.advisory.app.docs.dto.anx1.ErpGroupResDto;
import com.ey.advisory.app.docs.dto.anx1.ErpGroupSaveReqDto;
import com.ey.advisory.app.services.jobs.erp.processedrecords.ErpGroupService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

/*
 * This controller responsible for based Group code  getting the ERP 
 * Informations , Based Group code and Entity Id it will set the ERP Id to
 * Gstn information  
 */

@RestController
@Slf4j
public class ErpScreenController {

	@Autowired
	@Qualifier("ErpGroupService")
	private ErpGroupService erpGroupService;
	/*
	 * This method is responsible for Fetching the data from ERP Info based on 
	 * Group code 
	 */

	@PostMapping(path = "/getErpIds", 
			produces = {MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getErpId(@RequestBody String jsonErps) {
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Groud Code of this application", groupCode);
		}

		try {
			JsonObject jsonReqObj = (new JsonParser().parse(jsonErps)
					                                       .getAsJsonObject());
			JsonObject json = jsonReqObj.get("req").getAsJsonObject();
			JsonObject resp = new JsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ErpGroupReqDto req = gson.fromJson(json, ErpGroupReqDto.class);
			List<ErpGroupResDto> listErp = erpGroupService.getErpValues(req);

			JsonElement respBody = gson.toJsonTree(listErp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Erp details ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/*
	 * This method is responsible for Saving the ERP Info into GSTN info 
	 * based on Group Code , Entity ID and Gstin 
	 * Fetching the data from ERP INFO Based on Group Code and Entity ID
	 * then save into GSTN Info
	 */
	@PostMapping(path = "/saveErpIds",
			produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveErpId(@RequestBody String jsonSaveErps) {
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Groud Code of this application", groupCode);
		}

		try {
			JsonObject jsonReqObj = (new JsonParser().parse(jsonSaveErps)
					.getAsJsonObject());

			JsonObject json = jsonReqObj.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			ErpGroupSaveReqDto req = gson.fromJson(json,
					                                 ErpGroupSaveReqDto.class);
			
			return erpGroupService.saveErpValues(req);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Erp details ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

}
