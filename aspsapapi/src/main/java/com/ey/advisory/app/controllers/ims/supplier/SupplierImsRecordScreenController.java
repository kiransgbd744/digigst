package com.ey.advisory.app.controllers.ims.supplier;

import java.util.List;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.service.ims.supplier.SupplierImsEntityReqDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGstnTrailPopUpResponseDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsRecodsDataService;
import com.ey.advisory.app.service.ims.supplier.SupplierImsRecordDataResponseDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsRecordScreenService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author ashutosh.kar
 *
 */

@RestController
public class SupplierImsRecordScreenController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SupplierImsRecordScreenController.class);

	@Autowired
	@Qualifier("SupplierImsRecordScreenServiceImpl")
	private SupplierImsRecordScreenService supplierImsRecordScreenService;

	@Autowired
	@Qualifier("SupplierImsRecodsDataServiceImpl")
	private SupplierImsRecodsDataService supplierImsRecordDataService;

	
	/**
	 * SUPPLIER_IMS Record Screen GSTN TrailPopup
	 */
	
	@PostMapping(value = "ui/supplierImsRecordTrailPopup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsGetCallPopup(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"supplierImsRecordGstnTrailPopup Request received from UI as {}",
						jsonString);
			}
			SupplierImsEntityReqDto criteria = gson.fromJson(reqJson,
					SupplierImsEntityReqDto.class);
			List<SupplierImsGstnTrailPopUpResponseDto> response = supplierImsRecordScreenService
					.getSupplierImsGstnTrailPopupData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.info(
					"Successfully processed SUPPLIER_IMS Summary Trail GSTN PopUp request for GSTIN: {}",
					criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while fetching the SUPPLIER_IMS Summary Trail DigiGST PopUp {}",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	/**
	 * SUPPLIER_IMS Record Screen Data API
	 */

	@PostMapping(value = "ui/supplierImsRecordScreenData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsRecordsData(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSupplierImsRecordsData Request received from UI as {}",
						jsonString);
			}

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			SupplierImsEntityReqDto reqDto = gson.fromJson(reqJson,
					SupplierImsEntityReqDto.class);

			int recordsToStart = pageNum;
			int noOfRowstoFetch = pageSize;
			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			//List<ImsRecordDataResponseDto> response = new ArrayList<ImsRecordDataResponseDto>();

			Pair<List<SupplierImsRecordDataResponseDto>, Integer> responseList = 
					supplierImsRecordDataService
					.getSupplierImsRecodsScreenData(reqDto, pageReq);

			JsonObject resp = new JsonObject();

			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(
							responseList.getValue1(), pageNum, pageSize, "S",
							"Successfully fetched records")));

			resp.add("resp", gson.toJsonTree(responseList.getValue0()));
			LOGGER.debug("response {}", resp);
			LOGGER.debug("Successfully processed SUPPLIER_IMS Summary Screen request");
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the SUPPLIER_IMS Summary Screen {} ",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
