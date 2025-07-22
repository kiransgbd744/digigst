package com.ey.advisory.app.controllers.ims;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.service.ims.ImsEntityLevelSummaryService;
import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.app.service.ims.ImsEntitySummaryResponseDto;
import com.ey.advisory.app.service.ims.ImsSaveQueueStatusResponseDto;
import com.ey.advisory.app.service.ims.ImsSaveSatusPopUpResponseDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.OnboardingConstant;
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
public class ImsEntityLevelSummaryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImsEntityLevelSummaryController.class);

	@Autowired
	@Qualifier("ImsEntityLevelSummaryServiceImpl")
	private ImsEntityLevelSummaryService imsEntityLevelSummaryService;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	@RequestMapping(value = "ui/getImsSummaryEntityLvl", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsProcessedRecords(@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getImsSummaryEntityLvl Request received from UI as {}", jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson, ImsEntitySummaryReqDto.class);

			List<String> regTypeList = Arrays.asList(REGULAR, SEZ ,SEZU , SEZD);

			// Process if GSTINs are empty
			if (CollectionUtils.isEmpty(criteria.getGstins())) {
				List<String> gstnsList = new ArrayList<>();

				List<Long> entityIds = new ArrayList<>();
				entityIds.add(Long.valueOf(criteria.getEntityId()));
				Map<String, String> outwardSecurityAttributeMap = 
						DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);
				
				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, regTypeList);

				criteria.setGstins(gstnsList);
				LOGGER.debug("GSTINs were empty, fetched GSTINs based on entityId: {}", gstnsList);
			}
			// call service get data
			List<ImsEntitySummaryResponseDto> responseList = imsEntityLevelSummaryService
					.getImsSummaryEntityLvlData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);

			JsonArray responseArray = new JsonArray();
			for (ImsEntitySummaryResponseDto dto : responseList) {
				JsonObject dtoObject = gson.toJsonTree(dto).getAsJsonObject();
				responseArray.add(dtoObject);
			}
			resp.add("resp", responseArray);
			LOGGER.info("Successfully processed IMS Entity Level Summary request for GSTINs: {}", criteria.getGstins());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS Entity Level Summary", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "ui/imsSummarySaveStatusPopup", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsGetCallPopup(@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("imsSummarySaveStatusPopup Request received from UI as {}", jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson, ImsEntitySummaryReqDto.class);
			List<ImsSaveSatusPopUpResponseDto> response = imsEntityLevelSummaryService
					.getImsDetailCallPopupData(criteria.getGstin());

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.info("Successfully processed IMS Save Status PopUp request for GSTIN: {}", criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS Save Status PopUp", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "ui/imsSummarySaveQueueStatus", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsQueuePopup(@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("imsSummarySaveQueueStatus Request received from UI as {}", jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson, ImsEntitySummaryReqDto.class);
			
			List<ImsSaveQueueStatusResponseDto> response = imsEntityLevelSummaryService
					.getImsDetailQueueData(criteria.getGstin());
			 // Sorting response based on dateTime in descending order
		    response.sort((dto1, dto2) -> {
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		        LocalDateTime dateTime1 = LocalDateTime.parse(dto1.getDateTime(), formatter);
		        LocalDateTime dateTime2 = LocalDateTime.parse(dto2.getDateTime(), formatter);
		        return dateTime2.compareTo(dateTime1); // Descending order
		    });
		    
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.info("Successfully processed IMS Save Queue Status PopUp request for GSTIN: {}", criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS Save Queue Status PopUp", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
