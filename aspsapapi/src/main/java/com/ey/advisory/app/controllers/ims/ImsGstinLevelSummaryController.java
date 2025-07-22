package com.ey.advisory.app.controllers.ims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.app.service.ims.ImsGstinLevelSummaryService;
import com.ey.advisory.app.service.ims.ImsGstinSummaryResponseDto;
import com.ey.advisory.app.service.ims.ImsGstinSummaryResponseWrapperDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author ashutosh.kar
 *
 */

@RestController
public class ImsGstinLevelSummaryController {

	public static final Logger LOGGER = LoggerFactory.getLogger(ImsGstinLevelSummaryController.class);

	@Autowired
	@Qualifier("ImsGstinLevelSummaryServiceImpl")
	private ImsGstinLevelSummaryService imsGstinLevelSummaryService;

	@RequestMapping(value = "ui/getImsSummaryGstinLvl", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsProcessedRecords(@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getImsSummaryGstinLvl Request received from UI as {}", jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson, ImsEntitySummaryReqDto.class);
			// call service get data
			ImsGstinSummaryResponseWrapperDto response = imsGstinLevelSummaryService
					.getImsSummaryGstinLvlData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			
			JsonObject dataObject = new JsonObject();
	        dataObject.addProperty("timeStamp",  response.getGetCallTimeStamp());
	        resp.add("data", dataObject);
			
			JsonArray responseArray = new JsonArray();
			for (ImsGstinSummaryResponseDto dto : response.getTables()) {
				JsonObject dtoObject = gson.toJsonTree(dto).getAsJsonObject();
				responseArray.add(dtoObject);
			}
			resp.add("resp", responseArray);
			LOGGER.info("Successfully processed IMS Gstin Level Summary request for GSTINs: {}", criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS Gstin Level Summary", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
