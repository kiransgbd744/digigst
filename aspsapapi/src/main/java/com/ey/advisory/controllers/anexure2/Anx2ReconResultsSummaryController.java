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

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.EntityIRDto;
import com.ey.advisory.app.services.daos.initiaterecon.Anx2ReconResultsMisMatchSummaryResDto;
import com.ey.advisory.app.services.daos.initiaterecon.Anx2ReconResultsPotentialMatchSummaryResDto;
import com.ey.advisory.app.services.daos.initiaterecon.Anx2ReconResultsSummaryResDto;
import com.ey.advisory.app.services.daos.initiaterecon.Anx2ReconResultsSummaryService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class Anx2ReconResultsSummaryController {
	
	@Autowired
	@Qualifier("Anx2ReconResultsSummaryService")
	private Anx2ReconResultsSummaryService anx2ReconResultsSummaryService;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2ReconResultsSummaryController.class);

	@PostMapping(value = "/ui/getAbsoluteMatchSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAbsoluteMatchSummary(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {

			EntityIRDto criteria = gson.fromJson(json, EntityIRDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getSgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = entityRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setSgstins(gstins);
				}
			}

			List<Anx2ReconResultsSummaryResDto> resp = 
					anx2ReconResultsSummaryService
					.absoluteMatchSummary(criteria);
			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson
						.toJsonTree("No data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
		}
		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/ui/getMisMatchSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMisMatchSummary(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {

			EntityIRDto criteria = gson.fromJson(json, EntityIRDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getSgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = entityRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setSgstins(gstins);
				}
			}
			List<Anx2ReconResultsMisMatchSummaryResDto> resp = 
					anx2ReconResultsSummaryService
					.<EntityIRDto>misMatchSummary(criteria);
			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson
						.toJsonTree("No data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
		}
		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/getPotentialMatchSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPotentialMatchSummary(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {

			EntityIRDto criteria = gson.fromJson(json, EntityIRDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getSgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = entityRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setSgstins(gstins);
				}
			}

			List<Anx2ReconResultsPotentialMatchSummaryResDto> resp = 
					anx2ReconResultsSummaryService
					.<EntityIRDto>potentialMatchSummary(criteria);
			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson
						.toJsonTree("No data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
		}
		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}




}
