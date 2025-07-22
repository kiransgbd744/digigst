/**
 * 
 */
package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.ewb.EwbPartBService;
import com.ey.advisory.app.docs.dto.einvoice.TransportPartBDetailsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class GetPartBDetailsController {

	@Autowired
	@Qualifier("EwbPartBServiceImpl")
	private EwbPartBService ewbPartBService;

	@PostMapping(value = "/ui/getPartBHistoryDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPartBHistoryDetails(
			@RequestBody String jsonString) {
		return getTransportPartBDetails(jsonString);
	}

	private ResponseEntity<String> getTransportPartBDetails(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			JsonArray ewbNosArray = req.get("ewbNo").getAsJsonArray();
			Type listType = new TypeToken<List<String>>() {
			}.getType();
			List<String> ewbNos = gson.fromJson(ewbNosArray, listType);
			if (ewbNos == null || ewbNos.isEmpty())
				throw new AppException("Provide at least one eway bill number");
			List<TransportPartBDetailsDto> response = ewbPartBService
					.getPartBDetailsByEwbNo(ewbNos);

			JsonObject resp = new JsonObject();
			if (response != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}
			resp.add("resp", gsonEwb.toJsonTree(response));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (NullPointerException ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception in getUpdatePart b details with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception get updatepartb details with request ", ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
