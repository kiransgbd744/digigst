package com.ey.advisory.controllers.anexure2;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchResDto;
import com.ey.advisory.app.services.search.anx2.Anx2ReconResultsAbsoluteMatchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Anand3.M
 *
 */

@RestController
public class Anx2ReconResultsMatchController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2ReconResultsMatchController.class);

	@Autowired
	@Qualifier("Anx2ReconResultsAbsoluteMatchService")
	private Anx2ReconResultsAbsoluteMatchService anx2ReconResultsAbsoluteMatchService;

	@PostMapping(value = "/ui/getAbsoluteMatchDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAbsoluteMatchDetails(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest = gson
					.fromJson(reqJson.toString(),
							Anx2ReconResultsAbsoluteMatchReqDto.class);

			List<Anx2ReconResultsAbsoluteMatchResDto> resp = anx2ReconResultsAbsoluteMatchService
					.<Anx2ReconResultsAbsoluteMatchResDto>fetchAbsoluteDetails(
							anx2ReconResultsAbsoluteMatchRequest);

			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson.toJsonTree(
						"No Absolute match details data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}

		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json for Absolute match details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error for Absolute match details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/getMismatchDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMismatchDetails(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest = gson
					.fromJson(reqJson.toString(),
							Anx2ReconResultsAbsoluteMatchReqDto.class);

			List<Anx2ReconResultsAbsoluteMatchResDto> resp = anx2ReconResultsAbsoluteMatchService
					.<Anx2ReconResultsAbsoluteMatchResDto>fetchMismatchDetails(
							anx2ReconResultsAbsoluteMatchRequest);

			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson.toJsonTree(
						"No Mismatch details data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}

		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json for Mismatch details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error occured while fetching the Mismatch details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/getPotentialMatchDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPotentialMatchDetails(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest = gson
					.fromJson(reqJson.toString(),
							Anx2ReconResultsAbsoluteMatchReqDto.class);

			List<Anx2ReconResultsAbsoluteMatchResDto> resp = anx2ReconResultsAbsoluteMatchService
					.<Anx2ReconResultsAbsoluteMatchResDto>fetchPotentialMatchDetails(
							anx2ReconResultsAbsoluteMatchRequest);

			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson.toJsonTree(
						"No PotentialMatch Details data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}

		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json for PotentialMatch Details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fetching the PotentialMatch Details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
