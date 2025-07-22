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

import com.ey.advisory.app.gstr3b.Gst3BSaveStatusDto;
import com.ey.advisory.app.gstr3b.Gstr3BSaveStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@RestController
@Slf4j
public class Gstr3BSaveStatusController {

	@Autowired
	@Qualifier("Gstr3BSaveStatusImpl")
	Gstr3BSaveStatus gstr3BSaveStatus;

	@PostMapping(value = "/ui/getSaveStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSaveStatusDetails(
			@RequestBody String jsonReq) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String gstin = json.get("gstin").getAsString();
			String taxPeriod = json.get("taxPeriod").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin save status in GSTR3 with gstin : %s , taxPeriod"
						+ ": %s",gstin, taxPeriod);
				LOGGER.debug(msg);
			}

			List<Gst3BSaveStatusDto> saveStatusList = gstr3BSaveStatus
					.getSaveStatus(gstin, taxPeriod);

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
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
