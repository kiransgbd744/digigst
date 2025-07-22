package com.ey.advisory.controllers.gstr3b;

import java.math.BigDecimal;
import java.util.Arrays;
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

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.app.gstr3b.Gstr3BSaveToGstnValidationService;
import com.ey.advisory.app.services.refidpolling.gstr1.GSTR1RefIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Divya1.B
 *
 */

@RestController
public class Gstr3BSaveToGstnController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSaveToGstnController.class);

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("Gstr3BSaveToGstnValidationService")
	private Gstr3BSaveToGstnValidationService validateService;

	@Autowired
	@Qualifier("DefaultGSTR1RefIdPollingManager")
	private GSTR1RefIdPollingManager gSTR1RefIdPollingManager;

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	private Gstr3BSaveStatusRepository gstr3BSaveStatusRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userInputRepo;

	@PostMapping(value = "/ui/gstr3BsaveToGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGSTR3BToGstin(
			@RequestBody String jsonString) {
		LOGGER.debug("executing the getStatus method in SaveToGstn");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String retPeriod = requestObject.get("retPeriod").getAsString();
			String gstin = requestObject.get("gstin").getAsString();
			String respMsg = saveToGstn(gstin, retPeriod);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String saveToGstn(String gstin, String retPeriod) {

		List<String> sectionList = Arrays.asList("4(b)(1)", "4(b)(2)", "4(d)(2)",
				"5(a)1", "5(a)2", "5(a)3", "5(a)4", "5.1(a)", "5.1(b)");
		boolean containsNegativeValue = false;

		String respBody = null;
		String groupCode = TenantContext.getTenantId();
		String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
		JsonObject jobj = new JsonObject();
		jobj.addProperty(APIConstants.GSTIN, gstin);
		jobj.addProperty(APIConstants.RETPERIOD, retPeriod);
		if (authStatus.equals("A")) {
			Long saveInprogressCnt = gstr3BSaveStatusRepository
					.findByGstinAndTaxPeriodAndStatus(gstin, retPeriod,
							APIConstants.SAVE_INITIATED);
			if (saveInprogressCnt > 0) {
				respBody = "Save is already Inprogress";
			} else {
				GstrReturnStatusEntity entity = returnStatusRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstin(
								gstin, retPeriod, APIConstants.GSTR3B, false);
				if (entity != null && APIConstants.FILED
						.equalsIgnoreCase(entity.getStatus())) {
					respBody = "GSTR3B is Filed for Selected Taxperiod";
				} else {
					// validation
					respBody = validateService.validateAspCopiedData(gstin,
							retPeriod);

					List<Gstr3BGstinAspUserInputEntity> sectionLists = userInputRepo
							.getITC10PercSectionData( retPeriod,gstin,
									sectionList);

					if (sectionLists == null || sectionLists.isEmpty()) {
						if (respBody == null) {
							asyncJobsService.createJob(groupCode,
									JobConstants.SAVEGSTR3B, jobj.toString(),
									JobConstants.SYSTEM, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);
							respBody = "GSTR3B Save Request Submitted Successfully.";
							
						} else {
							return respBody;
						}
					} else {
						for (Gstr3BGstinAspUserInputEntity section : sectionLists) {

							if (section.getTaxableVal()
									.compareTo(BigDecimal.ZERO) < 0
									|| section.getIgst()
											.compareTo(BigDecimal.ZERO) < 0
									|| section.getCgst()
											.compareTo(BigDecimal.ZERO) < 0
									|| section.getSgst()
											.compareTo(BigDecimal.ZERO) < 0
									|| section.getCess()
											.compareTo(BigDecimal.ZERO) < 0) {
								containsNegativeValue = true;
								break;
							}

						}
						if (containsNegativeValue) {
							respBody = String.format("For sections { %s } only positive amount is allowed to save.",sectionList);
						}else{

							asyncJobsService.createJob(groupCode,
									JobConstants.SAVEGSTR3B, jobj.toString(),
									JobConstants.SYSTEM, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);
							respBody = "GSTR3B Save Request Submitted Successfully.";
						
						}
					}
					
				}
			}

		} else {
			respBody = "Auth Token is Inactive, Please Activate";
		}
		return respBody;

	}

	@PostMapping(value = "/ui/gstr3BBulkSaveToGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr3BBulkSaveToGstn(
			@RequestBody String jsonString) {
		LOGGER.debug("executing the getStatus method in SaveToGstn");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonArray gstinArray = requestObject.get(APIConstants.GSTIN_LIST)
					.getAsJsonArray();
			String retPeriod = requestObject.get(APIConstants.RETPERIOD)
					.getAsString();
			JsonArray respBody = new JsonArray();

			for (JsonElement gstin : gstinArray) {
				JsonObject json = new JsonObject();
				String msg = saveToGstn(gstin.getAsString(), retPeriod);
				json.addProperty("gstin", gstin.getAsString());
				json.addProperty("msg", msg);
				respBody.add(json);
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
