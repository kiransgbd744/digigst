package com.ey.advisory.app.controllers.ims;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.app.service.ims.ImsGetCallPopUpResponseDto;
import com.ey.advisory.app.service.ims.ImsGetCallResponseDto;
import com.ey.advisory.app.service.ims.ImsGetCallService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Slf4j
@RestController
public class ImsGetCallScreenController {

	@Autowired
	@Qualifier("ImsGetCallServiceImpl")
	private ImsGetCallService service;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	@PostMapping(value = "ui/getImsCalls", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsEntitySummary(@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getImsCallSummary Request received from UI as {}", jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson, ImsEntitySummaryReqDto.class);

			List<String> regTypeList = Arrays.asList(REGULAR, SEZ ,SEZU , SEZD);

			// Process if GSTINs are empty
			if (CollectionUtils.isEmpty(criteria.getGstins())) {
				List<String> gstnsList = new ArrayList<>();

				List<Long> entityIds = new ArrayList<>();
				entityIds.add(Long.valueOf(criteria.getEntityId()));
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds, outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs.get(OnboardingConstant.GSTIN);

				gstnsList = gstinDetailRepo.filterGstinBasedByRegType(dataSecList, regTypeList);

				criteria.setGstins(gstnsList);
				LOGGER.debug("GSTINs were empty, fetched GSTINs based on entityId: {}", gstnsList);
			}
			List<ImsGetCallResponseDto> responseList = service.getImsCallSummary(criteria);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);

			JsonArray responseArray = new JsonArray();
			for (ImsGetCallResponseDto dto : responseList) {
				JsonObject dtoObject = gson.toJsonTree(dto).getAsJsonObject();
				responseArray.add(dtoObject);
			}
			resp.add("resp", responseArray);
			LOGGER.info("Successfully processed IMS GetCall request for GSTINs: {}", criteria.getGstins());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS GetCall", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "ui/getImsDetailCallPopup", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsGetCallPopup(@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getImsDetailCallPopup Request received from UI as {}", jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson, ImsEntitySummaryReqDto.class);
			List<ImsGetCallPopUpResponseDto> response = service.getImsDetailCallPopupData(criteria.getGstin());

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.info("Successfully processed IMS Detail Call PopUp request for GSTIN: {}", criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS Detail Call PopUp", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
