package com.ey.advisory.controllers.vendorcommunication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
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

import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterApiService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class VendorMasterAPIController {

	private static final String ENTITY_ID = "entityId";
	private static final String FAILED = "Failed";

	@Autowired
	@Qualifier("VendorMasterReportServiceImpl")
	private VendorMasterReportService masterReportService;

	@Autowired
	@Qualifier("VendorMasterApiServiceImpl")
	private VendorMasterApiService vendorMasterService;

	@SuppressWarnings("unused")
	@PostMapping(value = "/ui/getApiVendorPan", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompVendorPan(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			Long entityId = json.get(ENTITY_ID).getAsLong();

			if (entityId == null)
				throw new AppException("Entity Id cannot be null");

			List<GstinDto> listOfVendorPans = masterReportService
					.getListOfNonComplaintVendorPans(entityId);

			List<GstinDto> listOfVendorPans1 = vendorMasterService
					.getListOfApiVendorPans(entityId);
			listOfVendorPans.addAll(listOfVendorPans1);
			HashSet<GstinDto> hashSetVendorPan
	            = new HashSet<GstinDto>(listOfVendorPans);
			if (!CollectionUtils.isEmpty(hashSetVendorPan)) {
				String jsonEINV = gson.toJson(hashSetVendorPan);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorPans", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
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
			String msg = "Unexpected error while fetching the Vendor PAN list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getApiVendorGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompalintVGstin(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorPans = new JsonArray();
		Gson googleJson = new Gson();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorPans = requestObject.getAsJsonArray("vendorPan");
			List<String> listOfVendorPans;
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfVendorPans = googleJson.fromJson(vendorPans, listType);
			if (listOfVendorPans.isEmpty()) {
				throw new AppException("Please select atleast one Vendor PAN");
			}
			List<GstinDto> vendorGstinList = masterReportService
					.getListOfNonComplaintVendorGstin(listOfVendorPans,
							entityId);
			List<GstinDto> vendorGstinList1 = vendorMasterService
					.getListOfApiVendorGstin(listOfVendorPans,
							entityId);
			vendorGstinList.addAll(vendorGstinList1);
			HashSet<GstinDto> hashSetVendorGstin
            = new HashSet<GstinDto>(vendorGstinList);
			if (!CollectionUtils.isEmpty(hashSetVendorGstin)) {
				String jsonEINV = gson.toJson(hashSetVendorGstin);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorGstins", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor Gstin list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
//	@SuppressWarnings("unused")
//	@PostMapping(value = "/ui/getApiVendorStatus", produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> getVendorStatus(
//			@RequestBody String jsonString) {
//
//		JsonObject requestObject = (new JsonParser()).parse(jsonString)
//				.getAsJsonObject();
//
//		JsonObject json = requestObject.get("req").getAsJsonObject();
//		Gson gson = GsonUtil.newSAPGsonInstance();
//		try {
//
//			Long entityId = json.get(ENTITY_ID).getAsLong();
//
//			if (entityId == null)
//				throw new AppException("Entity Id cannot be null");
//
//			List<GstinDto> listOfVendorPans = masterReportService
//					.getListOfNonComplaintVendorPans(entityId);
//
//			List<GstinDto> listOfVendorPans1 = vendorMasterService
//					.getListOfApiVendorPans(entityId);
//			listOfVendorPans.addAll(listOfVendorPans1);
//			HashSet<GstinDto> hashSetVendorPan
//	            = new HashSet<GstinDto>(listOfVendorPans);
//			if (!CollectionUtils.isEmpty(hashSetVendorPan)) {
//				String jsonEINV = gson.toJson(hashSetVendorPan);
//				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
//				JsonObject jsonObject = new JsonObject();
//				jsonObject.add("vendorPans", einvJsonElement);
//				JsonObject resps = new JsonObject();
//				JsonElement respBody = gson.toJsonTree(jsonObject);
//				resps.add("hdr",
//						gson.toJsonTree(APIRespDto.createSuccessResp()));
//				resps.add("resp", respBody);
//				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
//			} else {
//				String msg = "No Data found";
//				JsonObject resp = new JsonObject();
//				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
//				resp.add("resp", gson.toJsonTree(msg));
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			}
//
//		} catch (Exception e) {
//			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
//			JsonObject resp = new JsonObject();
//			JsonElement respBody = gson.toJsonTree(dto);
//			String msg = "Unexpected error while fetching the Vendor PAN list";
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
//			resp.add("resp", respBody);
//			LOGGER.error(msg, e);
//			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//		}
//	}
}
