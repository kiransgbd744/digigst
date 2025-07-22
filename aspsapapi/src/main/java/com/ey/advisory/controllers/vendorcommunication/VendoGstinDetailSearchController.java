package com.ey.advisory.controllers.vendorcommunication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.itcmatching.vendorupload.GstinSearchResponse;
import com.ey.advisory.app.itcmatching.vendorupload.VendorGstinSearchServiceImpl;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMappingRespDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */
@RestController
@Slf4j
public class VendoGstinDetailSearchController {

	private static final String EINVOICE_APPLICABILITY = "einvoiceApplicability";
	private static final String GSTIN_STATUS = "gstinStatus";
	private static final String ENTITY_ID = "entityId";

	@Autowired
	private VendorGstinSearchServiceImpl vendorGstinSearchServiceImpl;

	@PostMapping(value = "/ui/vendorGstinDetailSearch", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorMasterData(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		JsonArray einvoiceApplicability = new JsonArray();
		JsonArray gstinStatus = new JsonArray();
		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> einvoiceApplicabilityList = null;
		List<String> gstinStatusList = null;
		String statusNotUpdatedInLastDays = null;
		JsonObject resp = new JsonObject();
		JsonObject respBody = new JsonObject();
		JsonElement jsonBody = null;
		List<VendorMappingRespDto> paginatedList = null;
		int totalCount = 0;
		int activeGstinStatusSummeryResultCount = 0;
		int suspendedGstinStatusSummeryResultCount = 0;
		int cancelledGstinStatusSummeryResultCount = 0;
		int inActiveGstinStatusSummeryResultCount = 0;
		int eInvApplicableStatusSummeryResultCount = 0;
		int eInvNotApplicableStatusSummeryResultCount = 0;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendoGstinDetailSearchController begin");
			}

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqObject = requestObject.getAsJsonObject("req");
			Long entityId = reqObject.get(ENTITY_ID).getAsLong();

			statusNotUpdatedInLastDays = reqObject
					.get("statusNotUpdatedInLastDays").getAsString();

			if (reqObject.has(EINVOICE_APPLICABILITY)
					&& reqObject.getAsJsonArray(EINVOICE_APPLICABILITY)
							.size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"EINVOICE_APPLICABILITY are provided in request");
				}
				einvoiceApplicability = reqObject
						.getAsJsonArray(EINVOICE_APPLICABILITY);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				einvoiceApplicabilityList = googleJson
						.fromJson(einvoiceApplicability, listType);
			}

			if (reqObject.has(GSTIN_STATUS)
					&& reqObject.getAsJsonArray(GSTIN_STATUS).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GSTIN_STATUS are provided in request");
				}
				gstinStatus = reqObject.getAsJsonArray(GSTIN_STATUS);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstinStatusList = googleJson.fromJson(gstinStatus, listType);
			}
			GstinSearchResponse vendorGstinDtlResponsePair = null;
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"trying to get the values from the vendorGstinSearchServiceImpl for search result");
				LOGGER.debug(msg);
			}
			try {
				// getting all the search result
				vendorGstinDtlResponsePair = vendorGstinSearchServiceImpl
						.getVendorGstinSearchResult(einvoiceApplicabilityList,
								gstinStatusList, entityId,
								statusNotUpdatedInLastDays,
								pageSize, pageNum);
			} catch (Exception e) {
				LOGGER.error(
						"Exception in creating getting the data for the search result: ",
						e);
			}
			if (vendorGstinDtlResponsePair != null) {
			 paginatedList = vendorGstinDtlResponsePair
					.getPaginatedList();
			/*List<VendorMappingRespDto> activeGstinStatusSummeryResult = vendorGstinDtlResponsePair
					.getActiveGstinStatusSummeryResult();
			List<VendorMappingRespDto> suspendedGstinStatusSummeryResult = vendorGstinDtlResponsePair
					.getSuspendedGstinStatusSummeryResult();
			List<VendorMappingRespDto> cancelledGstinStatusSummeryResult = vendorGstinDtlResponsePair
					.getCancelledGstinStatusSummeryResult();
			List<VendorMappingRespDto> inActiveGstinStatusSummeryResult = vendorGstinDtlResponsePair
					.getInActiveGstinStatusSummeryResult();
			List<VendorMappingRespDto> eInvApplicableStatusSummeryResult = vendorGstinDtlResponsePair
					.getEInvApplicableStatusSummeryResult();
			List<VendorMappingRespDto> eInvNotApplicableStatusSummeryResult = vendorGstinDtlResponsePair
					.getEInvNotApplicableStatusSummeryResult();
*/			
			 totalCount = vendorGstinDtlResponsePair.getTotalCount();

			activeGstinStatusSummeryResultCount = vendorGstinDtlResponsePair
					.getActiveGstinStatusSummeryResult().size();
			suspendedGstinStatusSummeryResultCount = vendorGstinDtlResponsePair
					.getSuspendedGstinStatusSummeryResult().size();
			cancelledGstinStatusSummeryResultCount = vendorGstinDtlResponsePair
					.getCancelledGstinStatusSummeryResult().size();
			inActiveGstinStatusSummeryResultCount = vendorGstinDtlResponsePair
					.getInActiveGstinStatusSummeryResult().size();
			eInvApplicableStatusSummeryResultCount = vendorGstinDtlResponsePair
					.getEInvApplicableStatusSummeryResult().size();
			eInvNotApplicableStatusSummeryResultCount = vendorGstinDtlResponsePair
					.getEInvNotApplicableStatusSummeryResult().size();
			
			}

			VendorMasterSearchResponseDTO responseDTO = new VendorMasterSearchResponseDTO();

			// Create response object
			JsonObject response = new JsonObject();
			JsonObject hdr = new JsonObject();
			respBody = new JsonObject();

			// Set header values
			hdr.addProperty("status", "S");
			hdr.addProperty("pageNum", pageNum);
			hdr.addProperty("pageSize", pageSize);
			hdr.addProperty("totalRecords", totalCount);

			response.add("hdr", hdr);

			// Create response body object
			respBody.add("vendorMappingRespDtoList",
					gson.toJsonTree(paginatedList));
			/*respBody.add("activeGstin",
					gson.toJsonTree(activeGstinStatusSummeryResult));
			respBody.add("suspendedGstin",
					gson.toJsonTree(suspendedGstinStatusSummeryResult));
			respBody.add("cancelledGstin",
					gson.toJsonTree(cancelledGstinStatusSummeryResult));
			respBody.add("inactiveGstin",
					gson.toJsonTree(inActiveGstinStatusSummeryResult));
			respBody.add("einvApplicable",
					gson.toJsonTree(eInvApplicableStatusSummeryResult));
			respBody.add("einvNotApplicable",
					gson.toJsonTree(eInvNotApplicableStatusSummeryResult));
*/
			response.add("resp", respBody);
			response.addProperty("activeGstinCount",
					activeGstinStatusSummeryResultCount);
			response.addProperty("suspendedGstinCount",
					suspendedGstinStatusSummeryResultCount);
			response.addProperty("cancelledGstinCount",
					cancelledGstinStatusSummeryResultCount);
			response.addProperty("inactiveCount",
					inActiveGstinStatusSummeryResultCount);
			response.addProperty("einvApplicableCount",
					eInvApplicableStatusSummeryResultCount);
			response.addProperty("einvNotApplicableCount",
					eInvNotApplicableStatusSummeryResultCount);

			// Convert response object to JsonElement
			jsonBody = gson.toJsonTree(response);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
		}
		
		String responseString = (jsonBody != null) ? jsonBody.toString() : "{}"; 
		return new ResponseEntity<>(responseString, HttpStatus.OK);

		//return new ResponseEntity<>(jsonBody.toString(), HttpStatus.OK);
	}

}
