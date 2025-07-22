package com.ey.advisory.controller.recon3way;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.service.upload.way3recon.Ewb3WayReconCommUtility;
import com.ey.advisory.common.EwbStatusInputDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class EwableManualGetCallController {

	@Autowired
	Ewb3WayReconCommUtility ewbCommUtility;

	@RequestMapping(value = "/ui/manualEwbGetCall", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"EwableManualGetCallController");
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			EwbStatusInputDto criteria = gson.fromJson(json,
					EwbStatusInputDto.class);
			List<String> gstinList = criteria.getGstins();
			LocalDate fromDate = LocalDate.parse(criteria.getFromdate());
			LocalDate toDate = LocalDate.parse(criteria.getToDate());
			LOGGER.debug(
					"Manual EWB Get Call Initiation Started, Please Check After Sometime");
			ewbCommUtility.postEwbJobsbasedOnStatus(gstinList, fromDate, toDate,
					false);
			LOGGER.debug("Manual EWB Get Call Initiation Completed");

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("resp", "The Request has been successfully initiated.Please check GET EWB status report for status of the request");
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Get E Invoice Details", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
