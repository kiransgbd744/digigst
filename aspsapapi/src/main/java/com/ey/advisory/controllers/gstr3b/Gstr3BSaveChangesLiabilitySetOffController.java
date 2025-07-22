package com.ey.advisory.controllers.gstr3b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr3b.Gstr3BPdItcDto;
import com.ey.advisory.app.gstr3b.Gstr3BSaveChangesLiabilitySetOffServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
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

@Slf4j
@RestController
public class Gstr3BSaveChangesLiabilitySetOffController {

	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffServiceImpl")
	private Gstr3BSaveChangesLiabilitySetOffServiceImpl gstr3BLaibilityService;

	@PostMapping(value = "/ui/saveChangesLiabilityOffSet", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BEntityDashboard(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Gstr3BSaveChangesLiabilitySetOffController");
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gstr3BPdItcDto requestDto = gson.fromJson(json,
					Gstr3BPdItcDto.class);

			String respList = gstr3BLaibilityService
					.saveOffSetchanges(requestDto);

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
