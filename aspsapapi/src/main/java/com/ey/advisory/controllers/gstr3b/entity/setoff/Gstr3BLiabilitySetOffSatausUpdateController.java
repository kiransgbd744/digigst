package com.ey.advisory.controllers.gstr3b.entity.setoff;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffStatusDto;
import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffStatusEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
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
public class Gstr3BLiabilitySetOffSatausUpdateController {

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository liabilitySetOffStatus;

	@PostMapping(value = "/ui/get3bLiabilitySetOffStatusUpdate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BEntityDashboard(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String msg = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside Gstr3BLiabilitySetOffSatausUpdateController"
						+" Request Payload is {}",
						jsonString);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			String gstin = requestObject.get("gstin").getAsString();

			
			Gstr3BSaveLiabilitySetOffStatusEntity entity = 
					liabilitySetOffStatus
					.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin, taxPeriod);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside Gstr3BLiabilitySetOffSatausUpdateController"
						+" resp entity is {}",
						entity);
			}
			
			Gstr3BLiabilitySetOffStatusDto respList = new 
					Gstr3BLiabilitySetOffStatusDto();
			
			String liabilityStatus = null;
			LocalDateTime statusUptatedOn = null;
			String updatedDateTime = null;
			if (entity != null) {
				liabilityStatus = entity.getStatus();
				statusUptatedOn = entity.getUpdatedOn() != null
						? EYDateUtil.toISTDateTimeFromUTC(
								entity.getUpdatedOn())
						: null;
				if (statusUptatedOn != null) {
					String dateTime = statusUptatedOn.toString();
					String date = dateTime.substring(0, 10);
					String time = dateTime.substring(11, 19);
					updatedDateTime = (date + " " + time);
				}
			} else {
				liabilityStatus = "Not Initiated";
			}
			
			respList.setLiabilitySetoffStatus(liabilityStatus);
			respList.setUpdatedOn(updatedDateTime);
			
			JsonElement respBody = gson.toJsonTree(respList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String errMsg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", errMsg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String errMsg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", errMsg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String errMsg = "Unexpected error while retriving Data ";
			LOGGER.error(msg, ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", errMsg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

}
