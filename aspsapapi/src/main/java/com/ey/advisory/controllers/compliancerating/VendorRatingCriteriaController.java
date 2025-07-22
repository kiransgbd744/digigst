package com.ey.advisory.controllers.compliancerating;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorRatingCriteriaListDTO;
import com.ey.advisory.app.data.services.compliancerating.VendorRatingCriteriaService;
import com.ey.advisory.app.data.services.compliancerating.VendorRatingTimestampDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@RestController
public class VendorRatingCriteriaController {

	@Autowired
	@Qualifier("VendorRatingCriteriaServiceImpl")
	private VendorRatingCriteriaService rtngService;

	@Autowired
	private DocRepository docRepo;

	@PostMapping(value = "/ui/saveVendorRatingCriteria", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveVendorRatingCriteria(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE saveVendorRatingCriteria");
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			VendorRatingCriteriaListDTO dto = gson.fromJson(reqJson,
					VendorRatingCriteriaListDTO.class);
			rtngService.saveVendorRatingCriteria(dto);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(
					"Rating Criterias have been saved successfully "));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Rating Criterias Details";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

	@GetMapping(value = "/ui/getVendorRatingCriteria", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorRatingCriteria(
			@RequestParam Long entityId, @RequestParam String source) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getVendorRatingCriteria");
			}

			if (entityId == null) {
				throw new AppException("EntityID is empty");
			}

			VendorRatingCriteriaListDTO dto = rtngService
					.getVendorRatingCriteria(entityId, source);

			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting Rating Criterias Details";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

	@GetMapping(value = "/ui/getVendorRtngTimeStamps")
	public ResponseEntity<String> getVendorRtngTimeStamps(
			HttpServletResponse response, @RequestParam String financialYear,
			@RequestParam Long entityId) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();

		LOGGER.debug("inside getVendorRtngTimeStamps");
		try {
			if (financialYear == null) {
				throw new AppException("FinancialYear is empty");
			}
			VendorRatingTimestampDTO dto = rtngService
					.getTimeStamp(financialYear, entityId);

			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while timeStamps";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@PostMapping(value = "/ui/getCustomerPans", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCutomerPans(HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();

		try {
			LOGGER.debug("inside getCustomerPans");

			List<String> pans = docRepo.getDistinctCustomerPans();

			List<GstinDto> panList = pans.stream().map(e -> convertToDto(e))
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("Number of cust pans fetched {} ", panList.size());
			JsonObject panBody = new JsonObject();
			JsonElement respBody = gson.toJsonTree(panList);
			panBody.add("pans", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", panBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting customer pans";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@PostMapping(value = "/ui/getCustomerGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCutomerGstins(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();

		JsonArray custPans = new JsonArray();
		Gson googleJson = new Gson();
		List<String> gstins = new ArrayList<>();
		LOGGER.debug("inside getCustomerGstins");
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			custPans = requestObject.getAsJsonArray("custPans");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> listOfcustPans = googleJson.fromJson(custPans,
					listType);

			if (listOfcustPans.isEmpty()) {
				throw new AppException(
						"Please select atleast one Customer PAN");
			}

			List<List<String>> returnChunks = Lists
					.partition(listOfcustPans, 2000);
			for (List<String> chunk : returnChunks) {
			gstins.addAll(docRepo
					.getDistinctCustomerGstin(chunk));
			}
			List<GstinDto> vendorGstinList = gstins.stream()
					.map(e -> convertToDto(e))
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("Number of cust gstins fetched {} ",
					vendorGstinList.size());
			JsonObject gstinBody = new JsonObject();
			JsonElement respBody = gson.toJsonTree(vendorGstinList);
			gstinBody.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting Cgstins";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	private GstinDto convertToDto(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}
}
