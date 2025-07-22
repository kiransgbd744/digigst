package com.ey.advisory.controller.gstr2;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsReqDto;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/*

Recon Result unlock


*/
@RestController
@Slf4j
public class Gstr2ReconResultUnlockController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Gstr2ReconResultsServiceImpl")
	Gstr2ReconResultsService reconResultservice;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@RequestMapping(value = "/ui/gstr2ReconResultUnlock", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResultData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject reqObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		
		String msg;
		
		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					" inside Gstr2ReconResultUnlockController /gstr2ReconResultUnlock %s",
					jsonString);
			LOGGER.debug(msg);
		}

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		JsonObject resp = new JsonObject();
		Gstr1FileStatusEntity savedEntity = new Gstr1FileStatusEntity();
		try {
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Gstr2ReconResultsReqDto reqDto = gson.fromJson(reqJson,
					Gstr2ReconResultsReqDto.class);
			
			List<String> reportTypes = reqDto.getReportTypes();
			
			if ("BulkUnlock".equalsIgnoreCase(reqDto.getIndentifier()))

			{
				if (reqDto.getGstins().isEmpty()) {
					throw new AppException("No Recipient GSTIN selected.");
				} else if (reportTypes == null || reportTypes.isEmpty()) {
					throw new AppException("No Report Type has been selected.");
				}

			}
			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						" inside gstr2ReconResultResponse for jsonReq %s",
						reqJson.toString());
				LOGGER.debug(msg);
			}
			
			//Vendor Gstin Number
			List<String> vendorGstins = reqDto.getVendorGstins();
			int vendorGstinsLength = 0;
			if (vendorGstins != null) {
			    // Filter out null elements and calculate the total length
				vendorGstinsLength = vendorGstins.stream()
			                                        .filter(Objects::nonNull) // Exclude null elements
			                                        .mapToInt(String::length)
			                                        .sum();
			}
            // Vendor GSTIN character validation
			if (vendorGstinsLength > 4000) {
			    APIRespDto dto = new APIRespDto("Failed",
			            "Vendor GSTIN has exceeded the limit of 4000 characters");
//			    JsonObject resp = new JsonObject();
			    JsonElement respBody = gson.toJsonTree(dto);
			     msg = "Vendor GSTIN has exceeded the limit of 4000 characters";
			    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			    resp.add("resp", respBody);
			    // Log the error message
			    LOGGER.error(msg);
			    return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Gstr1FileStatusEntity entity = new Gstr1FileStatusEntity();

			entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
			entity.setTotal(0);
			entity.setFileStatus("Uploaded");
			entity.setProcessed(0);
			entity.setError(0);
			LocalDateTime localDate = LocalDateTime.now();
			entity.setReceivedDate(localDate.toLocalDate());
			String response = null;
			switch (reqDto.getIndentifier()) {
			case "Unlock":
				entity.setDataType("Multi Unlock");
				response = "Single";
				break;
			case "BulkUnlock":
				entity.setDataType("Multi Unlock (Bulk Response)");
				response = "Multi";
				break;
			}

			entity.setSource(reqDto.getReconType().equalsIgnoreCase("2A_PR")
					? "2A/6AvsPR" : "2BvsPR");
			entity.setUpdatedBy(userName);
			entity.setUpdatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			savedEntity = gstr1FileStatusRepository.save(entity);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						" File Status entity created successfully for unlock fileId %s",
						savedEntity.getId());
				LOGGER.debug(msg);
			}

			String reqId = getBatchid(savedEntity.getId());
			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("batchId", Long.valueOf(reqId));
			jsonParams.addProperty("fileId", savedEntity.getId());
			jsonParams.addProperty("reconType", reqDto.getReconType());
			jsonParams.add("reqDto", gson.toJsonTree(reqDto));
			jsonParams.addProperty("respIden", response);
			jsonParams.addProperty("userName", userName);
			
			gstr1FileStatusRepository.updateReqPayload(savedEntity.getId(),jsonParams.toString());
			
			
		/*	String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_MULTI_UNLOCK, jsonParams.toString(),
					userName, 1L, null, null);*/

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						" successfully saved in tbl for batchId %s",
						reqId.toString());
				LOGGER.debug(msg);
			}
			if (reqId != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				if ("BulkUnlock".equalsIgnoreCase(reqDto.getIndentifier())) {
					resp.add("resp",
							gson.toJsonTree(String.format(
									"Bulk Recon response request initiated with Req Id - %s",
									reqId.toString())));
				} else {
					resp.add("resp",
							gson.toJsonTree(String.format(
									"Recon response request initiated with Req Id - %s",
									reqId.toString())));
				}
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("No Data"));
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			msg = "Exception on gstr2ReconResultResponse" + ex;
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			String msg1 = "Technical error while loading reconciliation data on screen. Please connect with EY Team for the solution";
			
			gstr1FileStatusRepository.updateFailedStatus(savedEntity.getId(),
					ex.getLocalizedMessage(), "Failed");
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg1));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}	
	
	private String getBatchid(Long fileId) {

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth))
				+ (currentDay < 10 ? ("0" + currentDay)
						: String.valueOf(currentDay));

		return currentDate.concat(String.valueOf(fileId));
	}

}
