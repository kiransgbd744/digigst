package com.ey.advisory.controller.recipientmaster;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.recipientmaster.dto.RecipientGstinDto;
import com.ey.advisory.app.recipientmaster.dto.RecipientMasterDataDto;
import com.ey.advisory.app.recipientmasterupload.RecipientMasterDataService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@RestController
@Slf4j
public class RecipientMasterDataController {

	@Autowired
	private RecipientMasterDataService recipientMasterService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	private static final String RECIPIENT_GSTIN = "recipientGstin";
	private static final String RECIPIENT_PAN = "recipientPan";
	private static final String ENTITY_ID = "entityId";
	private static final String FAILED = "Failed";

	@PostMapping(value = "/ui/getRecipientMasterData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorMasterData(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		JsonArray recipientGstins = new JsonArray();
		JsonArray recipientPan = new JsonArray();

		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> recipientGstinsList = null;
		List<String> recipientPanList = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE RecipientMasterDataController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			if (reqObject.has(RECIPIENT_PAN)
					&& reqObject.getAsJsonArray(RECIPIENT_PAN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("recipientPan PAN are provided in request");
				}
				recipientPan = reqObject.getAsJsonArray(RECIPIENT_PAN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				recipientPanList = googleJson.fromJson(recipientPan, listType);
			}

			if (reqObject.has(RECIPIENT_GSTIN)
					&& reqObject.getAsJsonArray(RECIPIENT_GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Recipient gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				recipientGstins = reqObject.getAsJsonArray(RECIPIENT_GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				recipientGstinsList = googleJson.fromJson(recipientGstins,
						listType);
			}

			if (recipientPanList == null && recipientGstinsList == null) {

				Long entityId = reqObject.get(ENTITY_ID).getAsLong();

				recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);
				if (recipientPanList.isEmpty() || recipientPanList == null) {

					String msg = "No Data found";
					JsonObject resp = new JsonObject();
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.add("resp", gson.toJsonTree(msg));
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
				}

			}

			Pair<List<RecipientMasterDataDto>, Integer> masterUploadResponsePair = recipientMasterService
					.listRecipientMasterData(recipientPanList,
							recipientGstinsList, pageSize, pageNum);

			String responseData = gson
					.toJson(masterUploadResponsePair.getValue0());

			JsonElement jsonElement = new JsonParser().parse(responseData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("recipientMasterData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					masterUploadResponsePair.getValue1(), pageNum, pageSize,
					"S",
					"Successfully fetched Recipient Master Processed records")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@PostMapping(value = "/ui/getRecipientGstnInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorGstInfoList(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		LOGGER.debug(
				"The selected criteria for fetching recipient gstin list is : {}",
				requestObject.get("req"));
		try {
			Long entityId = json.get(ENTITY_ID).getAsLong();
			List<RecipientGstinDto> gstr1FileStatusEntities = recipientMasterService
					.getListOfRecipientGstinList(entityId);
			if (!CollectionUtils.isEmpty(gstr1FileStatusEntities)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(gstr1FileStatusEntities);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
