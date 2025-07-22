package com.ey.advisory.app.controllers.ims;

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

import com.ey.advisory.app.service.ims.ImsDigiGstTrailPopUpResponseDto;
import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.app.service.ims.ImsGstnTrailPopUpResponseDto;
import com.ey.advisory.app.service.ims.ImsRecodsDataService;
import com.ey.advisory.app.service.ims.ImsRecordDataRequestDto;
import com.ey.advisory.app.service.ims.ImsRecordDataResponseDto;
import com.ey.advisory.app.service.ims.ImsRecordScreenService;
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
public class ImsRecordScreenController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ImsRecordScreenController.class);

	@Autowired
	@Qualifier("ImsRecordScreenServiceImpl")
	private ImsRecordScreenService imsRecordScreenService;

	@Autowired
	@Qualifier("ImsRecodsDataServiceImpl")
	private ImsRecodsDataService imsRecordDataService;

	
	/**
	 * IMS Record Screen DIGIGST TrailPopup
	 */
	
	@PostMapping(value = "ui/imsRecordTrailPopup", produces = {
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
						"imsRecordTrailPopup Request received from UI as {}",
						jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson,
					ImsEntitySummaryReqDto.class);
			List<ImsDigiGstTrailPopUpResponseDto> response = imsRecordScreenService
					.getImsDigiGstTrailPopupData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.info(
					"Successfully processed IMS Summary Trail DigiGST PopUp request for GSTIN: {}",
					criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while fetching the IMS Summary Trail DigiGST PopUp {}",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	/**
	 * IMS Record Screen GSTN TrailPopup
	 */
	
	@PostMapping(value = "ui/imsRecordGstnTrailPopup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsGstnGetCallPopup(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"imsRecordGstnTrailPopup Request received from UI as {}",
						jsonString);
			}
			ImsEntitySummaryReqDto criteria = gson.fromJson(reqJson,
					ImsEntitySummaryReqDto.class);
			List<ImsGstnTrailPopUpResponseDto> response = imsRecordScreenService
					.getImsGstTrailPopupData(criteria);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject hdrObject = resp.getAsJsonObject("hdr");
			hdrObject.addProperty("status", "S");
			resp.add("hdr", hdrObject);
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.info(
					"Successfully processed IMS Summary GSTN Trail PopUp request for GSTIN: {}",
					criteria.getGstin());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while fetching the IMS Summary GSTN Trail  PopUp {}",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * IMS Record Screen Data API
	 */

	@PostMapping(value = "ui/imsRecordScreenData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsRecordsData(
			@RequestBody String jsonString) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getImsRecordsData Request received from UI as {}",
						jsonString);
			}

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			ImsRecordDataRequestDto reqDto = gson.fromJson(reqJson,
					ImsRecordDataRequestDto.class);

			int recordsToStart = pageNum;
			int noOfRowstoFetch = pageSize;
			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			//List<ImsRecordDataResponseDto> response = new ArrayList<ImsRecordDataResponseDto>();

			Pair<List<ImsRecordDataResponseDto>, Integer> responseList = 
					imsRecordDataService
					.getImsRecodsScreenData(reqDto, pageReq);

			JsonObject resp = new JsonObject();

			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(
							responseList.getValue1(), pageNum, pageSize, "S",
							"Successfully fetched records")));

			resp.add("resp", gson.toJsonTree(responseList.getValue0()));
			LOGGER.debug("response {}", resp);
			LOGGER.debug("Successfully processed IMS Summary Screen request");
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the IMS Summary Screen {} ",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
