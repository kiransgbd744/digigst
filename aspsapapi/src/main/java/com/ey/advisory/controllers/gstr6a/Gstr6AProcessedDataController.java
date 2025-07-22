package com.ey.advisory.controllers.gstr6a;

import java.util.List;
import java.util.Map;

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

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6AProcessedDataService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author SriBhavya
 *
 */
@RestController
public class Gstr6AProcessedDataController {

	@Autowired
	@Qualifier("Gstr6AProcessedDataServiceImpl")
	Gstr6AProcessedDataService gstr6AProcessedDataService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6AProcessedDataController.class);

	@RequestMapping(value = "/ui/Gstr6AProcessedData", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6AProcessedData(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("Request data begin");
		try {
			Gstr6AProcessedDataRequestDto criteria = gson.fromJson(json,
					Gstr6AProcessedDataRequestDto.class);
			List<Long> entityIds = criteria.getEntityId();
			Map<String, String> inwardSecurityAttributeMap = 
					DataSecurityAttributeUtil.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecurityAttrMap = 
					DataSecurityAttributeUtil.dataSecurityAttrMapForQuery(
							entityIds,inwardSecurityAttributeMap);
			if (criteria.getDataSecAttrs() == null
					|| criteria.getDataSecAttrs().isEmpty()) {
				criteria.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				Map<String, List<String>> dataSecReqMap = criteria
						.getDataSecAttrs();
				List<String> gstinList = dataSecReqMap
						.get(OnboardingConstant.GSTIN);
				if ((gstinList == null || gstinList.isEmpty())) {
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				} else {
					if ((gstinList != null && !gstinList.isEmpty())) {
						dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
								gstinList);
					}
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				}
			}
			LOGGER.debug("Response data begin");
			List<Gstr6AProcessedDataResponseDto> responseData = 
					gstr6AProcessedDataService.getGstr6AProcessedData(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(responseData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
