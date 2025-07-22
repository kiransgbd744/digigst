package com.ey.advisory.controller.gstr9;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.services.gstr9.Gstr9HsnSaveDataService;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnDetailsSummaryDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnSummaryDetailsDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9ItemsResponseDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table17ItemsReqDto;
import com.ey.advisory.app.docs.enums.gstr9.SectionType;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh NK
 *
 */
@Slf4j
@RestController
public class Gstr9HsnDetailsController {

	@Autowired
	private Gstr9HsnSaveDataService gstr9HsnSaveDataService;

	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@PostMapping(value = "/ui/saveGstr9HSNInwardAndOutwardData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr9HSNOutwardData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		JsonObject response = new JsonObject();

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		LOGGER.debug(
				"The selected criteria for Gstr9 HSN InwardOrOutward Data is:->"
						+ requestObject.get("req"));
		try {

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			Gstr9HsnSummaryDetailsDto req = gson.fromJson(reqJson.toString(),
					Gstr9HsnSummaryDetailsDto.class);

			List<Gstr9Table17ItemsReqDto> gstr9Table17ListDto = req
					.getGstr9Table17ListDto();

			JsonArray jsonArray = new JsonArray();

			for (Gstr9Table17ItemsReqDto reqDto : gstr9Table17ListDto) {

				String hsn = reqDto.getHsnSc();
				boolean isActive = hsnCache.isValidHSN(hsn);

				if (!isActive) {
					String msg = String.format("Invalid HSN Code - %s", hsn);
					LOGGER.error(msg);
					JsonObject resp = new JsonObject();
					resp.addProperty("hsn_sc", hsn);
					jsonArray.add(resp);
				}

			}
			if (jsonArray.size() != 0) {

				JsonElement respBody = gson.toJsonTree(jsonArray);
				response.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				response.add("invalidHsNList", respBody);
				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}
			String status = gstr9HsnSaveDataService.saveHsnOutwardInwardData(
					gstr9Table17ListDto, req.getType(), req.getGstin(),
					GenUtil.getFormattedFy(req.getFy()));

			JsonElement respBody = gson.toJsonTree(status);
			response.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			response.add("resp", respBody);
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			response.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(response.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			response.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(response.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/ui/deleteGstr9HSNInwardAndOutwardData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteGstr9HSNOutwardData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		JsonObject response = new JsonObject();

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		LOGGER.debug(
				"The selected criteria for Gstr9 HSN InwardOrOutward Data is:->"
						+ requestObject.get("req"));
		try {

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			Gstr9HsnSummaryDetailsDto req = gson.fromJson(reqJson.toString(),
					Gstr9HsnSummaryDetailsDto.class);

			List<Gstr9Table17ItemsReqDto> gstr9Table17ListDto = req
					.getGstr9Table17ListDto();

			JsonArray jsonArray = new JsonArray();

			for (Gstr9Table17ItemsReqDto reqDto : gstr9Table17ListDto) {

				String hsn = reqDto.getHsnSc();
				boolean isActive = hsnCache.isValidHSN(hsn);

				if (!isActive) {
					String msg = String.format("Invalid HSN Code - %s", hsn);
					LOGGER.error(msg);
					JsonObject resp = new JsonObject();
					resp.addProperty("hsn_sc", hsn);
					jsonArray.add(resp);
				}

			}
			if (jsonArray.size() != 0) {

				JsonElement respBody = gson.toJsonTree(jsonArray);
				response.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				response.add("invalidHsNList", respBody);
				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}
			String status = gstr9HsnSaveDataService.deleteHsnOutwardInwardData(
					gstr9Table17ListDto, req.getType(), req.getGstin(),
					GenUtil.getFormattedFy(req.getFy()));

			JsonElement respBody = gson.toJsonTree(status);
			response.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			response.add("resp", respBody);
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			response.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(response.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			response.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(response.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@PostMapping(value = "/ui/copyGstr9HSNInwardAndOutwardData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> copyGstr9HSNOutwardData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		JsonObject response = new JsonObject();

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		LOGGER.debug(
				"The selected criteria for Gstr9 HSN InwardOrOutward Data is:->"
						+ requestObject.get("req"));
		try {

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			Gstr9HsnSummaryDetailsDto req = gson.fromJson(reqJson.toString(),
					Gstr9HsnSummaryDetailsDto.class);

			List<Gstr9Table17ItemsReqDto> gstr9Table17ListDto = req
					.getGstr9Table17ListDto();

			JsonArray jsonArray = new JsonArray();

			for (Gstr9Table17ItemsReqDto reqDto : gstr9Table17ListDto) {

				String hsn = reqDto.getHsnSc();
				boolean isActive = hsnCache.isValidHSN(hsn);

				if (!isActive) {
					String msg = String.format("Invalid HSN Code - %s", hsn);
					LOGGER.error(msg);
					JsonObject resp = new JsonObject();
					resp.addProperty("hsn_sc", hsn);
					jsonArray.add(resp);
				}

			}
			if (jsonArray.size() != 0) {

				JsonElement respBody = gson.toJsonTree(jsonArray);
				response.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				response.add("invalidHsNList", respBody);
				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}
			String status = gstr9HsnSaveDataService.saveHsnOutwardInwardData(
					gstr9Table17ListDto, req.getType(), req.getGstin(),
					GenUtil.getFormattedFy(req.getFy()));

			if(status.equalsIgnoreCase("Data Saved Successfully"))
				status = "Copy saved successfully";
				
			JsonElement respBody = gson.toJsonTree(status);
			response.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			response.add("resp", respBody);
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			response.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(response.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			response.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(response.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/ui/getGstr9HsnOutwardInward", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9HsnOutwardInward(
			HttpServletRequest request, HttpServletResponse response) {

		try {

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

			LOGGER.debug(
					"The selected criteria for Gstr9 HSN Outward/Inward Data is:->"
							+ request);

			String gstin = request.getParameter("gstin");

			String fyYear = GenUtil.getFormattedFy(request.getParameter("fy"));

			String type = request.getParameter("type");

			List<Gstr9ItemsResponseDto> listResponse = gstr9HsnSaveDataService
					.getHsnProcessedData(gstin, fyYear, type);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(listResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping(value = "/ui/getGstr9HsnDetailsSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9HsnDetailsSummary(
			HttpServletRequest request, HttpServletResponse response) {

		try {

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

			LOGGER.debug(
					"The selected criteria for Gstr9 HSN INWARD/Outward Data is:->"
							+ request);

			String gstin = request.getParameter("gstin");

			String fyYear = GenUtil.getFormattedFy(request.getParameter("fy"));

			List<Gstr9HsnDetailsSummaryDto> listResponse = gstr9HsnSaveDataService
					.getHsnSummaryDetails(gstin, fyYear);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(listResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
