package com.ey.advisory.controllers.gstr3b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffDto;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffService;
import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffStatusEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffSnapFetchsServiceImpl;
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
public class Gstr3BLiabilitySetOffController {

	@Autowired
	@Qualifier("Gstr3BLiabilitySetOffServiceImpl")
	private Gstr3BLiabilitySetOffService gstr3BLaibilityService;
	
	@Autowired
	@Qualifier("Gstr3BSetOffSnapFetchsServiceImpl")
	private Gstr3BSetOffSnapFetchsServiceImpl snapService;
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;
	
	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository liabilitySetOffStatus;

	@PostMapping(value = "/ui/get3bLiabilitySetOff", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BEntityDashboard(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String authToken = "Active";
		String msg = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"About to render 3B Liability setoff screen, Request Payload is {}",
						jsonString);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			String gstin = requestObject.get("gstin").getAsString();
			
			Gstr3BSaveLiabilitySetOffStatusEntity entity = 
					liabilitySetOffStatus
					.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin, taxPeriod);
			
			String setOffStatus = "";
			
			Gstr3BLiabilitySetOffDto respList = null;
			
			if (entity != null) {
				setOffStatus = entity.getStatus();
			}
			
			if(setOffStatus.equalsIgnoreCase("SAVED")) {
				
				LOGGER.debug("fetching values form snap table{}, {} ", 
						gstin, taxPeriod);
				
				 respList = snapService.fetchFromSnap(gstin, taxPeriod);
			} else {

			// AuthToken check before invoking GSTN.
			if (!authTokenService.getAuthTokenStatusForGstin(gstin)
					.equalsIgnoreCase("A")) {
				authToken = "InActive";
				msg = String.format(
						"Warning - Auth token is inactive for GSTIN %s"
								+ " and Tax Period %s,  hence data displayed is as per "
								+ "DigiGST and not as per GSTN. To fetch data "
								+ "from GSTN, please activate auth token : ",
						gstin, taxPeriod);

			}
			LOGGER.debug("fetching values form live GET call {}, {} ", 
					gstin, taxPeriod);
			 respList = gstr3BLaibilityService
					.get3BLiabilitySetOff(gstin, taxPeriod, authToken, msg);
			
			}
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
