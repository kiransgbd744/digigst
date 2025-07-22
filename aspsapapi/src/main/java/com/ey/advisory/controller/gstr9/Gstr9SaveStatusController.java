package com.ey.advisory.controller.gstr9;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.savetogstn.gstr9.Gstr9SaveStatus;
import com.ey.advisory.app.docs.dto.gstr9.Gst9SaveStatusDto;
import com.ey.advisory.app.docs.dto.gstr9.Gst9StatusTimeStampsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@RestController
public class Gstr9SaveStatusController {

	@Autowired
	@Qualifier("Gstr9SaveStatusImpl")
	private Gstr9SaveStatus gstr9SaveStatus;

	@PostMapping(value = "/ui/getGstr9SaveStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9SaveStatusDetails(
			@RequestBody String jsonReq) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String gstin = json.get("gstin").getAsString();
			String fy = json.get("fy").getAsString();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				String msg = "Gstin and Financial year is mandatory.";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Begin save status in GSTR9 with gstin : %s , fy"
								+ ": %s", gstin, fy);
				LOGGER.debug(msg);
			}

			List<Gst9SaveStatusDto> saveStatusList = gstr9SaveStatus
					.getSaveStatus(gstin, fy);

			JsonObject statusResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(saveStatusList);
			statusResp.add("details", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", statusResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/downloadGstr9SaveResponse", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadSaveStatusRecords(HttpServletRequest request,
			HttpServletResponse response) {
		JsonObject resp = new JsonObject();
		try {
			String fileId = request.getParameter("id");
			if (fileId == null || fileId.isEmpty()) {
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto("E", "Id is Mandatory")));
				response.getWriter().println(resp.toString());
				return;
			}
			Long id = Long.valueOf(fileId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin download save status in GSTR9 with id : %d ",
						id);
				LOGGER.debug(msg);
			}

			gstr9SaveStatus.downloadSaveStatusDetails(id, response);

		} catch (Exception ex) {
			String msg = " Exception while Downloading the Gstr9 Error Json ";
			LOGGER.error(msg, ex);
		}
	}

	@PostMapping(value = "/ui/getGstr9StatusTimeStamps", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9StatusTimeStamps(
			@RequestBody String jsonReq) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String gstin = json.get("gstin").getAsString();
			String fy = json.get("fy").getAsString();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				String msg = "Gstin and Financial year is mandatory.";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Begin save status in GSTR9 with gstin : %s , fy"
								+ ": %s", gstin, fy);
				LOGGER.debug(msg);
			}

			Gst9StatusTimeStampsDto statusTimeStamps = gstr9SaveStatus
					.getStatusTimeStamps(gstin, fy);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(statusTimeStamps);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = " Unexpected error occured in getGstr9StatusTimeStamps ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
