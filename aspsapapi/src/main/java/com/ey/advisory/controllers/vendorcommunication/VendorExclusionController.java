package com.ey.advisory.controllers.vendorcommunication;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorExclusionDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorExclusionService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorGstinDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@RestController
@Slf4j
public class VendorExclusionController {

	@Autowired
	@Qualifier("VendorExclusionServiceImpl")
	private VendorExclusionService vendorExclusionService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	private static final String VENDOR_GSTIN = "vendorGstin";
	private static final String ENTITY_ID = "entityId";
	private static final String ATTACHMENT_FILENAME = "attachment; filename=";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_OCTET_STREAM = "APPLICATION/OCTET-STREAM";
	private static final String FAILED = "Failed";

	@PostMapping(value = "/ui/getVendorExclusionReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getVendorExclusionReport(@RequestBody String jsonString,
			HttpServletResponse response) throws IOException {

		JsonObject errorResp = new JsonObject();
		Workbook workbook = null;
		JsonArray vendorGstins = new JsonArray();

		Gson googleJson = new Gson();
		List<String> vendorGstinsList = null;
		List<String> recipientPanList = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendorExclusionController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			if (reqObject.has(VENDOR_GSTIN)
					&& reqObject.getAsJsonArray(VENDOR_GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Vendor gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				vendorGstins = reqObject.getAsJsonArray(VENDOR_GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorGstinsList = googleJson.fromJson(vendorGstins, listType);
			}

			if (vendorGstinsList == null) {

				Long entityId = reqObject.get(ENTITY_ID).getAsLong();

				recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);
				if (recipientPanList.isEmpty() || recipientPanList == null) {

					String msg = "No Data found";
					throw new AppException(msg);
				}

			}

			workbook = vendorExclusionService.downlaodVendorExclusionReport(
					recipientPanList, vendorGstinsList);

			LocalDateTime now = LocalDateTime.now();
			String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
			timeMilli = timeMilli.replace(".", "");
			timeMilli = timeMilli.replace("-", "");
			timeMilli = timeMilli.replace(":", "");

			if (workbook != null) {
				response.setContentType(APPLICATION_OCTET_STREAM);
				response.setHeader(CONTENT_DISPOSITION,
						String.format(
								ATTACHMENT_FILENAME + "VendorExclusionData"
										+ "_" + timeMilli + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
		} catch (Exception ex) {
			String msg = "Error occured while generating VendorExclusionReport ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));

		}
	}

	@PostMapping(value = "/ui/getExcludedVendors", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getExcludedVendors(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		LOGGER.debug(
				"The selected criteria for fetching vendor gstin list is : {}",
				requestObject.get("req"));
		try {
			Long entityId = json.get(ENTITY_ID).getAsLong();
			List<VendorGstinDto> excludedVendorEntities = vendorExclusionService
					.getExcludedVendorGstinList(entityId);
			if (!CollectionUtils.isEmpty(excludedVendorEntities)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(excludedVendorEntities);
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
			String msg = "Unexpected error while fetching excluded Vendor Gstins";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorExclusionData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorExclusionData(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		JsonArray vendorGstins = new JsonArray();
		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> vendorGstinsList = null;
		List<String> recipientPanList = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendorExclusionController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			if (reqObject.has(VENDOR_GSTIN)
					&& reqObject.getAsJsonArray(VENDOR_GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Vendor gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				vendorGstins = reqObject.getAsJsonArray(VENDOR_GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorGstinsList = googleJson.fromJson(vendorGstins, listType);
			}

			if (vendorGstinsList == null) {

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

			List<VendorExclusionDto> vendorExcludedData = vendorExclusionService
					.getVendorExclusionData(recipientPanList, vendorGstinsList);

			String jsonVendorData = gson.toJson(vendorExcludedData);
			JsonElement vendorJsonElement = new JsonParser()
					.parse(jsonVendorData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("vendorExcludedData", vendorJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

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
			String msg = "Unexpected error while retriving Vendor Excluded Data ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/ui/deleteExcludedVendor", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> softDeleteExcludedVendor(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		JsonArray vendorGstins = new JsonArray();

		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> vendorGstinsList = null;
		List<String> recipientPanList = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendorExclusionController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			if (reqObject.has(VENDOR_GSTIN)
					&& reqObject.getAsJsonArray(VENDOR_GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Vendor gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				vendorGstins = reqObject.getAsJsonArray(VENDOR_GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorGstinsList = googleJson.fromJson(vendorGstins, listType);
			}

			Long entityId = reqObject.get(ENTITY_ID).getAsLong();

			recipientPanList = entityInfoRepository.findPanByEntityId(entityId);

			if (vendorGstinsList == null || recipientPanList == null) {

				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			int deletedRecords = vendorExclusionService
					.softDeleteExcludedVendorGstins(recipientPanList,
							vendorGstinsList);

			String jsonVendorData = gson.toJson(deletedRecords);
			JsonElement vendorJsonElement = new JsonParser()
					.parse(jsonVendorData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("Number of Records Deleted", vendorJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

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
			String msg = "Unexpected error while retriving Vendor Excluded Data ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

}
