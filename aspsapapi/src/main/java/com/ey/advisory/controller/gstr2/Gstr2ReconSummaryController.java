package com.ey.advisory.controller.gstr2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ReconSummaryReq;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryMasterDto;
import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2ReconSummaryController {

	@Autowired
	@Qualifier("Gstr2ReconSummaryServiceImpl")
	Gstr2ReconSummaryService service;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository gstr2ReconGstin;

	@PostMapping(value = "/ui/gstr2ReconSummary", 
							produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> reconSummary(@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Long configId;
		List<String> gstinList = null;
		List<String> returnPeriod = null;
		
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Gstr2ReconSummaryController"
								+ ".reconSummary() method :: jsonString : %s",
						jsonString);
				LOGGER.debug(msg);
			}

			Gstr2ReconSummaryReq req = gson.fromJson(json,
					Gstr2ReconSummaryReq.class);

			if (Strings.isNullOrEmpty(req.getConfigId())) {

				String msg = "ConfigId should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			configId = Long.valueOf(req.getConfigId());

			if (req.getSgstins() != null && !req.getSgstins().isEmpty()) {

				gstinList = req.getSgstins();
			}
			if (req.getReturnPeriod() != null
					&& !req.getReturnPeriod().isEmpty()) {

				returnPeriod = req.getReturnPeriod();
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"reconSummary() " + "Parameter configId %s:: ",
						configId);
				LOGGER.debug(msg);
			}
			
			String reconType = req.getReconType();
			Gstr2ReconSummaryMasterDto respList = service.getReconSummary(
					configId, gstinList, returnPeriod, reconType);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2ReconSummaryController"
						+ ".reconSummary, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch(AppException ex){
			String msg = " Unexpected error occured";
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

	@PostMapping(value = "/ui/gstr2GetGstinforReconSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2GetGstinforReconSummary(
			@RequestBody String reqJson) {

		JsonObject requestObject = (new JsonParser()).parse(reqJson)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		Long configId = null;
		List<String> gstinList = null;
		List<GstinDto> gstins = null;

		try {

			if (json.has("configId")) {
				configId = json.get("configId").getAsLong();
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Confid Id  : %d", configId);
				LOGGER.debug(msg);
			}

			if (configId != null) {
				gstinList = gstr2ReconGstin.findAllGstinsByConfigId(configId);

				 gstins = gstinList.stream().map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));

				gstins.sort(Comparator.comparing(GstinDto::getGstin));

			} else {
				String msg = "ConfigID should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(gstins);
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
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	private GstinDto convert(String o) {
		GstinDto entity = new GstinDto();

		entity.setGstin(o);
		//entity.setGstinIdentifier(o.getRegistrationType());
		return entity;
	}

}
