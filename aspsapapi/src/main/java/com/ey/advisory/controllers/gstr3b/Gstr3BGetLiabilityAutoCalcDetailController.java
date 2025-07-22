package com.ey.advisory.controllers.gstr3b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.Gstr3bGenerateStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3bDigiStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalcReportDownloadReq;
import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcDetailService;
import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */

@RestController
@Slf4j
public class Gstr3BGetLiabilityAutoCalcDetailController {

	@Autowired
	@Qualifier("Gstr3BGetLiabilityAutoCalcDetailServiceImpl")
	Gstr3BGetLiabilityAutoCalcDetailService gstr3BGetLiabilityService;

	@Autowired
	@Qualifier("Gstr3bDigiStatusRepository")
	private Gstr3bDigiStatusRepository returnStatusRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/gstr3BAutoCalc", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bAutoCalcAndSave(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("inside getGstr3bAutoCalcAndSave Controller");

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			Gstr3BGetLiabilityAutoCalcReqDto req = gson.fromJson(
					reqJson.toString(), Gstr3BGetLiabilityAutoCalcReqDto.class);

			String gstin = req.getGstin();

			String taxPeriod = req.getTaxPeriod();

			if (Strings.isNullOrEmpty(taxPeriod)
					|| Strings.isNullOrEmpty(gstin)) {
				String msg = "Return Period And Gstin cannot be empty";
				LOGGER.error(msg);
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			String msg = gstr3BGetLiabilityService.getGstnAutoCalc(gstin,
					taxPeriod);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while get3BAutoCalAndSave";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/gstr3BBulkSaveAutoCalc", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bBulkAutoCalcAndSave(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("inside getGstr3bAutoCalcAndSave Controller");

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject jsonReq = requestObject.get("req").getAsJsonObject();

			Gstr3BAutoCalcReportDownloadReq criteria = gson.fromJson(jsonReq,
					Gstr3BAutoCalcReportDownloadReq.class);

			List<String> gstins = criteria.getGstin();

			String taxPeriod = criteria.getTaxPeriod();

			JsonArray respBody = new JsonArray();

			for (String gstin : gstins) {
				JsonObject json = new JsonObject();
				String apiResponse = gstr3BGetLiabilityService
						.getGstnAutoCalc(gstin, taxPeriod);
				json.addProperty("gstin", gstin.toString());
				json.addProperty("msg", apiResponse);
				respBody.add(json);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while get3BBulkAutoCalAndSave";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getGstr3BDigiCalculate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bAutoDigiCalcAndSave(
			@RequestBody String jsonString) {

		LOGGER.debug(
				"executing the getStatus method in getGstr3bAutoDigiCalcAndSave");
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
			if (Strings.isNullOrEmpty(retPeriod))
				throw new AppException("tax period cannot be empty");

			JsonArray respBody = new JsonArray();
			List<String> gstinList = new ArrayList<>();
			for (JsonElement gstin : gstinArray) {
				JsonObject json = new JsonObject();
				Pair<String, String> pair = bulkGstr3BCalculate(
						gstin.getAsString(), retPeriod);
				String msg = pair.getValue0();
				if (pair.getValue1() != null) {
					gstinList.add(pair.getValue1());
				}
				json.addProperty("gstin", gstin.getAsString());
				json.addProperty("msg", msg);
				respBody.add(json);
			}

			if (!gstinList.isEmpty()) {
				List<Gstr3bGenerateStatusEntity> entityList = new ArrayList<>();
				for (String gstn : gstinList) {
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Gstr3bGenerateStatusEntity entity = new Gstr3bGenerateStatusEntity();
					entity.setCreatedBy(userName);
					entity.setCreatedOn(LocalDateTime.now());
					entity.setIsActive(true);
					entity.setGstin(gstn);
					entity.setTaxPeriod(retPeriod);
					entity.setModifiedBy(userName);
					entity.setModifiedOn(LocalDateTime.now());
					entity.setCalculationType("DigiGst");
					entity.setStatus("Initiated");
					entityList.add(entity);
				}
				returnStatusRepo.saveAll(entityList);
				String userName = SecurityContext.getUser()
						.getUserPrincipalName();
				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("retPeriod", retPeriod);
				String gstins = String.join(",", gstinList);
				jobParams.addProperty("gstins", gstins);
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR3B_DIGI_CALCULATE,
						jobParams.toString(), userName, 1L, null, null);
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

	private Pair<String, String> bulkGstr3BCalculate(String gstin,
			String retPeriod) {
		String respBody = null;
		String availableGstin = null;
		Gstr3bGenerateStatusEntity entity = returnStatusRepo
				.findByGstinAndTaxPeriodAndIsActive(gstin, retPeriod, true);
		if (entity != null) {
			if (entity.getStatus().equalsIgnoreCase("Inprogress")) {
				respBody = "Auto Calculate 3B - DigiGST is in progress";
			} else if (entity.getStatus().equalsIgnoreCase("Initiated")) {
				respBody = "Auto Calculate 3B - DigiGST is already Initiated";
			} else {
				respBody = "Auto Calculate 3B - DigiGST is initiated successfully";
				availableGstin = gstin;
			}
		} else {
			respBody = "Auto Calculate 3B - DigiGST is initiated successfully";
			availableGstin = gstin;
		}
		return new Pair<String, String>(respBody, availableGstin);

	}
}
