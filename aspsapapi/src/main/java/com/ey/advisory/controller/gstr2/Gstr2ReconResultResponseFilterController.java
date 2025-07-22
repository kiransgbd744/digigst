package com.ey.advisory.controller.gstr2;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsReqDto;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsRequestStatusRespDto;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*

Recon Result


*/
@RestController
@Slf4j
public class Gstr2ReconResultResponseFilterController {

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

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static ImmutableList<String> VALID_ITC_IDENTIFIER = ImmutableList
			.of("T1", "T2", "T3", "T4");

	@RequestMapping(value = "/ui/gstr2ReconResultResponse", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResultData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject reqObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		JsonObject resp = new JsonObject();
		Gstr1FileStatusEntity savedEntity = new Gstr1FileStatusEntity();
		try {
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Gstr2ReconResultsReqDto reqDto = gson.fromJson(reqJson,
					Gstr2ReconResultsReqDto.class);
			

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" inside gstr2ReconResultResponse for jsonReq %s",
						reqJson.toString());
				LOGGER.debug(msg);
			}
			if (reqDto.getReportType() != null && reqDto.getReportType()
					.equalsIgnoreCase("Doc Number & Doc Date Mismatch")) {

				reqDto.setReportType("Doc No & Doc Date Mismatch");
			}
			
		/*	String isEligible = isElligibleForLocking(Long.valueOf(reqDto.getEntityId()));
			if(Strings.isNullOrEmpty(isEligible))
			{
				throw new AppException(
						" Recon Response is already Inprogress.");

			}*/
			
			switch (reqDto.getIndentifier()) {
			case "3B":
				if (Strings.isNullOrEmpty(reqDto.getTaxPeriodGstr3b()))
					throw new AppException(
							"No Tax Period for GSTR-3B has been selected.");
				else if (reqDto.getItcReversal() != null
						&& validateITCIdentifier(reqDto.getItcReversal())) {
					throw new AppException(
							"Invalid ITC Reversal Identifier, Only T1/T2/T3/T4/Blank responses are allowed.");

				}
			}
			Gstr1FileStatusEntity entity = new Gstr1FileStatusEntity();

			entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
			entity.setTotal(0);
			entity.setFileStatus("Uploaded");
			entity.setProcessed(0);
			entity.setError(0);
			LocalDateTime localDate = LocalDateTime.now();
			entity.setReceivedDate(localDate.toLocalDate());

			switch (reqDto.getIndentifier()) {

			case "Force":
				entity.setDataType("Force Match");
				break;

			case "3B":
				entity.setDataType("3B Response");
				break;

			case "Unlock":
				entity.setDataType("Unlock");
				break;
				
			case "IMS":
				entity.setDataType("IMS Action (Accept/Reject/Pending)");
				
				break;

			}
			if(!"IMS".equalsIgnoreCase(reqDto.getIndentifier())){

			entity.setSource(reqDto.getReconType().equalsIgnoreCase("2A_PR")
					? "2A/6AvsPR" : "2BvsPR");
			}else{
				if("2A_PR".equalsIgnoreCase(reqDto.getReconType())){
					entity.setSource("2A/6AvsPR + IMS");
					entity.setFileType("2APR_IMS_RECON_RESULT");

				}else{
					entity.setSource("2BvsPR + IMS");	
					entity.setFileType("2BPR_IMS_RECON_RESULT");

				}
			}
			entity.setUpdatedBy(userName);
			entity.setUpdatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			savedEntity = gstr1FileStatusRepository.save(entity);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" File Status entity created successfully for fileId %s",
						savedEntity.getId());
				LOGGER.debug(msg);
			}

			String reqId = getBatchid(savedEntity.getId());
			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("batchId", Long.valueOf(reqId));
			jsonParams.addProperty("fileId", savedEntity.getId());
			jsonParams.addProperty("reconType", reqDto.getReconType());
			jsonParams.add("reqDto", gson.toJsonTree(reqDto));
			jsonParams.addProperty("respIden", "response");
			jsonParams.addProperty("userName", userName);
			
			gstr1FileStatusRepository.updateReqPayload(savedEntity.getId(),jsonParams.toString());
			
			
			String groupCode = TenantContext.getTenantId();
/*			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_RESPONSE, jsonParams.toString(),
					userName, 1L, null, null);
*/
			/*
			 * String reqId = reconResultservice.reconResponseUpload(reqDto,
			 * savedEntity.getId(), userName);
			 */
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" successfully saved in tbl for batchId %s",
						reqId.toString());
				LOGGER.debug(msg);
			}
			if (reqId != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				if(!"IMS".equalsIgnoreCase(reqDto.getIndentifier())){
				resp.add("resp",
						gson.toJsonTree(String.format(
								"Recon response request initiated with Req Id - %s",
								reqId.toString())));
				}else{
					resp.add("resp",
						gson.toJsonTree(String.format(
							"IMS Action response request initiated with Req Id - %s",
								reqId.toString())));	
				}
				
				
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("No Data"));
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception on gstr2ReconResultResponse" + ex;
			LOGGER.error(msg, ex);
			String msg1 = "Technical error while loading reconciliation data on screen. Please connect with EY Team for the solution";
			
			JsonObject respMsg = new JsonObject();
			gstr1FileStatusRepository.updateFailedStatus(savedEntity.getId(),
					ex.getLocalizedMessage(), "Failed");
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg1));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ui/gstr2RequestStatusData", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2RequestStatusData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			Long entityId = reqJson.get("entityId").getAsLong();
			//
			String identifier = reqJson.get("identifier").getAsString();

			List<Gstr2ReconResultsRequestStatusRespDto> respdata = reconResultservice
					.requestStatusData(entityId, identifier);

			if (respdata != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respdata));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("No Data"));
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception on gstr2ReconResultResponse" + ex;
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ui/getRequestStatusErrorFile", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRequestStatusErrorFile(
			@RequestBody String jsonString, HttpServletResponse response)
			throws Exception {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		
		Gstr1FileStatusEntity fileEntity = null;
		try {

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			Long fileId = reqJson.get("fileId").getAsLong();
			Optional<Gstr1FileStatusEntity> fileEntityList = gstr1FileStatusRepository
					.findById(fileId);
			
			FileStatusReportDto reqDto = gson.fromJson(reqJson,
					FileStatusReportDto.class);
			

			if(fileEntityList.isPresent()){
			 fileEntity = fileEntityList.get();
			}
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setFileId(fileId);
			entity.setReportCateg("Recon Result");
			entity.setDataType("Inward");
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			
			if(fileEntity!=null){

			if ("2A/6AvsPR".equalsIgnoreCase(fileEntity.getSource())) {
				entity.setReportType("Error 2A-6AvsPR Report (Recon Result)");
			} else if("2BvsPR".equalsIgnoreCase(fileEntity.getSource())){
				entity.setReportType("Error 2BvsPR Report (Recon Result)");

			}else if("2BvsPR + IMS".equalsIgnoreCase(fileEntity.getSource())){
				entity.setReportType("IMS Action Error 2BvsPR (Recon Result)");

			}else if("2A/6AvsPR + IMS".equalsIgnoreCase(fileEntity.getSource())){
				entity.setReportType("IMS Action Error 2AvsPR (Recon Result)");

			}
			}
			String reportType = entity.getReportType();
			
			entity = fileStatusDownloadReportRepo.save(entity);

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", entity.getId());
			jobParams.addProperty("reportType", reportType);
			

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.Gstr2_RECON_RESULT_ERROR_DOWNLOAD,
					jobParams.toString(), userName, 1L, null, null);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed",
					"Error occured while creating the request");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in " + "getRequestStatusErrorFile";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ui/reqBulkResponse", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> reqBulkResponse(
			@RequestBody String jsonString, HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		
		String msg = null;
		
		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					" inside Gstr2ReconResultResponseFilterController /reqBulkResponse for jsonReq %s",
					jsonString);
			LOGGER.debug(msg);
		}

		Gstr1FileStatusEntity savedEntity = new Gstr1FileStatusEntity();
		JsonObject resp = new JsonObject();
		try {

			JsonObject json = requestObject.get("req").getAsJsonObject();

			Gstr2ReconResultsReqDto reqDto = gson.fromJson(json,
					Gstr2ReconResultsReqDto.class);
			
		/*	String isEligible = isElligibleForLocking(Long.valueOf(reqDto.getEntityId()));
			if(Strings.isNullOrEmpty(isEligible))
			{
				throw new AppException(
						" Recon Response is already Inprogress.");

			}*/
			
			List<String> reportTypes = reqDto.getReportTypes();
			
			if (reqDto.getGstins().isEmpty()) {
				throw new AppException("No Recipient GSTIN selected.");
			} else if (reportTypes == null || reportTypes.isEmpty()) {
				throw new AppException("No Report Type has been selected.");
			}

			if (reqDto.getIndentifier().equalsIgnoreCase("3B")) {
				if (Strings.isNullOrEmpty(reqDto.getTaxPeriodGstr3b()))
					throw new AppException(
							"No Tax Period for GSTR-3B has been selected.");
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

			if (vendorGstinsLength > 4000) {
			    APIRespDto dto = new APIRespDto("Failed",
			            "Vendor GSTIN has exceeded the limit of 4000 characters");
			    JsonElement respBody = gson.toJsonTree(dto);
			    msg = "Vendor GSTIN has exceeded the limit of 4000 characters";
			    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			    resp.add("resp", respBody);
			    // Log the error message
			    LOGGER.error(msg);
			    return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						" inside reqBulkResponse for jsonReq %s",
						json.toString());
				LOGGER.debug(msg);
			}

			Gstr1FileStatusEntity entity = new Gstr1FileStatusEntity();

			entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
			entity.setTotal(0);
			entity.setFileStatus("Uploaded");
			entity.setProcessed(0);
			entity.setError(0);
			LocalDateTime localDate = LocalDateTime.now();
			entity.setReceivedDate(localDate.toLocalDate());
			switch (reqDto.getIndentifier()) {

			case "Force":
				entity.setDataType("Force Match (Bulk Response)");
				break;

			case "3B":
				entity.setDataType("3B Response (Bulk Response)");
				break;

			case "Unlock":
				entity.setDataType("Unlock (Bulk Response)");
				break;
				
			case "IMS":
				entity.setDataType("IMS Action (Accept/Reject/Pending)");
				break;

			}
			
			if(!"IMS".equalsIgnoreCase(reqDto.getIndentifier())){

				entity.setSource(reqDto.getReconType().equalsIgnoreCase("2A_PR")
						? "2A/6AvsPR" : "2BvsPR");
				}else{
					if("2A_PR".equalsIgnoreCase(reqDto.getReconType())){
						entity.setSource("2A/6AvsPR + IMS");
						entity.setFileType("2APR_IMS_RECON_RESULT");

					}else{
						entity.setSource("2BvsPR + IMS");
						entity.setFileType("2BPR_IMS_RECON_RESULT");

					}
				}

			/*entity.setSource(reqDto.getReconType().equalsIgnoreCase("2A_PR")
					? "2A/6AvsPR" : "2BvsPR");*/
			entity.setUpdatedBy(userName);

			entity.setUpdatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
	

			savedEntity = gstr1FileStatusRepository.save(entity);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						" File Status entity created successfully for fileId %s",
						savedEntity.getId());
				LOGGER.debug(msg);
			}

			String reqId = getBatchid(savedEntity.getId());
			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("batchId", Long.valueOf(reqId));
			jsonParams.addProperty("fileId", savedEntity.getId());
			jsonParams.addProperty("reconType", reqDto.getReconType());
			jsonParams.add("reqDto", gson.toJsonTree(reqDto));
			jsonParams.addProperty("respIden", "Bulkresp");
			jsonParams.addProperty("userName", userName);
			

			gstr1FileStatusRepository.updateReqPayload(savedEntity.getId(),jsonParams.toString());
			
			
			String groupCode = TenantContext.getTenantId();
			/*asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_RESPONSE, jsonParams.toString(),
					userName, 1L, null, null);
*/
			if (reqId != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				if(!"IMS".equalsIgnoreCase(reqDto.getIndentifier())){

				resp.add("resp",
						gson.toJsonTree(String.format(
								"Bulk Recon response request initiated with Req Id - %s",
								reqId.toString())));
				}else{

				resp.add("resp",
						gson.toJsonTree(String.format(
								"Bulk IMS Action response request initiated with Req Id - %s",
								reqId.toString())));	
				
				}
				
				
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("No Data"));
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			msg = "Exception in Gstr2Recon Result Controller";
			LOGGER.error(msg, ex);
			String msg1 = "Technical error while loading reconciliation data on screen. Please connect with EY Team for the solution";
			
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg1));
			return new ResponseEntity<String>(respMsg.toString(),
					HttpStatus.OK);
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

	private boolean validateITCIdentifier(String itcIdentifer) {

		if (Strings.isNullOrEmpty(itcIdentifer)
				|| VALID_ITC_IDENTIFIER.contains(itcIdentifer))
			return false;
		else
			return true;
	}

	private String isElligibleForLocking(Long entityId) {

		String queryStr = createQuery();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"query string created for recon result request status {}",
					queryStr);
		}
		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("entityId", entityId);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = q.getResultList();

		if (Collections.isEmpty(resultList))
			return null;
		else
		return "INPROGRESS";
	}

	private String createQuery() {

		return " SELECT id"
				+ "  from FILE_STATUS WHERE SOURCE IN('2A/6AvsPR','2BvsPR') "
				+ " AND DATA_TYPE IN ('Multi Unlock','Multi Unlock (Bulk Response),'Force Match','Unlock (Bulk Response)','Force Match (Bulk Response)','3B Response','3B Response (Bulk Response)','Unlock') "
				+ " AND ENTITY_ID =:entityId AND FILE_STATUS = 'Uploaded' ORDER BY ID;";
	}

}
