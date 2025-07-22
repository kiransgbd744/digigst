/**
 * 
 */
package com.ey.advisory.controllers.gstr3b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.daos.client.DashboardDao;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BExcemptNilNonGstnDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardService;
import com.ey.advisory.app.gstr3b.Gstr3BInterStateSuppliesDto;
import com.ey.advisory.app.gstr3b.Gstr3bDashboardDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@RestController
@Slf4j
public class Gstr3BGstinDashboardController {

	@Autowired
	private Gstr3BGstinDashboardService gstr3bGstinService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository statusRepo;
	
	@Autowired
	@Qualifier("dashboardDaoImpl")
	private DashboardDao dashboardDao;

	@PostMapping(value = "/ui/getGstr3BgstnDashboard", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BgstnDashboard(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr3BgstnDashboard");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");
			
			Long entityId = requestObject.has("entityId")
					? requestObject.get("entityId").getAsLong() : 0L;
			if (entityId.equals(0L))
						throw new AppException("entityId is mandatory");
			
			Gstr3bDashboardDto respList = gstr3bGstinService
					.getgstr3bgstnDashBoardList(taxPeriod, gstin,entityId);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

	@PostMapping(value = "/ui/getInterStateSupplies", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInterStateSupplies(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr3BgstnDashboard");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			List<Gstr3BInterStateSuppliesDto> respList = gstr3bGstinService
					.getInterStateSupplies(taxPeriod, gstin);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

	@PostMapping(value = "/ui/getUserInterStateSupplies", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUserInterStateSupplies(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE get User inter state supplies");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			List<Gstr3BGstinAspUserInputDto> respList = gstr3bGstinService
					.getUserInterStateSupplies(taxPeriod, gstin);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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
	
	@PostMapping(value = "/ui/getUserPastLiabDtls", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUserPastLiabDtls(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE get User inter state supplies");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			List<Gstr3BGstinAspUserInputDto> respList = gstr3bGstinService
					.getPastLiabDtls(taxPeriod, gstin);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

	@PostMapping(value = "/ui/getExcemptNilNonGstIS", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getExcemptNilNonGstIS(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr3BgstnDashboard");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			List<Gstr3BExcemptNilNonGstnDto> respList = gstr3bGstinService
					.getExcemptNilNonGstIS(taxPeriod, gstin);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

	

}
