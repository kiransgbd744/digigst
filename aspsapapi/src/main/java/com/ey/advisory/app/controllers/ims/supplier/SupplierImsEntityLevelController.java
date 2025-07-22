package com.ey.advisory.app.controllers.ims.supplier;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.service.ims.supplier.SupplierImsEntityDetailsResponseDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsEntityLevelService;
import com.ey.advisory.app.service.ims.supplier.SupplierImsEntityReqDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsEntitySummaryResponseDto;
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
public class SupplierImsEntityLevelController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierImsEntityLevelController.class);

	@Autowired
	@Qualifier("SupplierImsEntityLevelServiceImpl")
	private SupplierImsEntityLevelService supplierImsEntityLevelService;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	@RequestMapping(value = "ui/getSupplierImsSummaryEntityLvl", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSupplierImsSummaryEntityLvl(@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSupplierImsSummaryEntityLvl Request received from UI as {}", jsonString);
			}
			SupplierImsEntityReqDto criteria = gson.fromJson(reqJson, SupplierImsEntityReqDto.class);

			List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

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
			// call service get data
			List<SupplierImsEntitySummaryResponseDto> responseList = supplierImsEntityLevelService
					.getSupplierImsSummaryEntityLvlData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);

			JsonArray responseArray = new JsonArray();
			for (SupplierImsEntitySummaryResponseDto dto : responseList) {
				JsonObject dtoObject = gson.toJsonTree(dto).getAsJsonObject();
				responseArray.add(dtoObject);
			}
			resp.add("resp", responseArray);
			LOGGER.info("Successfully processed Supplier IMS Entity Level Summary request for GSTINs: {}",
					criteria.getGstins());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the Supplier IMS Entity Level Summary", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "ui/getSupplierImsDetailEntityLvl", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSupplierImsDetailEntityLvl(@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSupplierImsDetailEntityLvl Request received from UI as {}", jsonString);
			}
			SupplierImsEntityReqDto criteria = gson.fromJson(reqJson, SupplierImsEntityReqDto.class);

			List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

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
			// call service get data
			List<SupplierImsEntityDetailsResponseDto> responseList = supplierImsEntityLevelService
					.getSupplierImsDetailEntityLvlData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);

			JsonArray responseArray = new JsonArray();
			for (SupplierImsEntityDetailsResponseDto dto : responseList) {
				JsonObject dtoObject = gson.toJsonTree(dto).getAsJsonObject();
				responseArray.add(dtoObject);
			}
			resp.add("resp", responseArray);
			LOGGER.info("Successfully processed Supplier IMS Entity Level Detail request for GSTINs: {}",
					criteria.getGstins());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the Supplier IMS Detail Level Summary", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
