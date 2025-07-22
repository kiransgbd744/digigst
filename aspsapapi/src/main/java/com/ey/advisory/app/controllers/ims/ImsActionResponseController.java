package com.ey.advisory.app.controllers.ims;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
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
import com.ey.advisory.app.data.repositories.client.ImsReconStatusConfigRepository;
import com.ey.advisory.app.service.ims.ImsActioResponseReqDto;
import com.ey.advisory.app.service.ims.ImsRequestStatusRespDto;
import com.ey.advisory.app.service.ims.ImsRequestStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class ImsActionResponseController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("ImsRequestStatusServiceImpl")
	ImsRequestStatusService reqService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	private static ImmutableList<String> actions = ImmutableList.of("A", "P",
			"R");
	
	@Autowired
	@Qualifier("ImsReconStatusConfigRepository")
	ImsReconStatusConfigRepository imsReconRepo;
	
	private static List<String> autoimsStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");

	@RequestMapping(value = "/ui/imsActionResponse", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsActionData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject reqObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		JsonObject resp = new JsonObject();
		Gstr1FileStatusEntity savedEntity = new Gstr1FileStatusEntity();
		try {
			
			// monitor ims changes
			int imstatusCount = imsReconRepo
					.findByStatusIn(autoimsStatusList);

			if (imstatusCount != 0) {
				String msg = String.format(
						"Auto IMS action based on Auto recon parameters is in progress,"
						+ " Please try after sometime.");
				LOGGER.error(msg);
				APIRespDto dto1 = new APIRespDto("Information",msg);
				JsonObject resp1 = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto1);
				resp1.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp1.add("resp", respBody);
				
				return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
			}
			
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			ImsActioResponseReqDto reqDto = gson.fromJson(reqJson,
					ImsActioResponseReqDto.class);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" inside ImsActionResponseController"
								+ ".getImsActionData() for jsonReq %s",
						reqJson.toString());
				LOGGER.debug(msg);
			}

			Gstr1FileStatusEntity entity = new Gstr1FileStatusEntity();

			entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
			entity.setTotal(0);
			entity.setFileStatus("Initiated");
			entity.setProcessed(0);
			entity.setError(0);
			LocalDateTime localDate = LocalDateTime.now();
			entity.setReceivedDate(localDate.toLocalDate());
			entity.setActionTaken(reqDto.getActionTaken());
			
			switch (reqDto.getActionTaken()) {

			case "A":
				entity.setDataType("Accepted");
				break;

			case "R":
				entity.setDataType("Rejected");
				break;

			case "P":
				entity.setDataType("Pending");
				break;

			}

			entity.setSource("IMS_UI");
			entity.setUpdatedBy(userName);
			entity.setUpdatedOn(LocalDateTime.now());

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
			jsonParams.add("reqDto", gson.toJsonTree(reqDto));
			jsonParams.addProperty("userName", userName);

			gstr1FileStatusRepository.updateReqPayload(savedEntity.getId(),
					jsonParams.toString());

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode, JobConstants.IMS_RESPONSE_UI,
					jsonParams.toString(), userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" successfully saved in tbl for batchId %s",
						reqId.toString());
				LOGGER.debug(msg);
			}
			if (reqId != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp",
						gson.toJsonTree(String.format(
								"Request for IMS Action '%s' has been "
										+ "initiated successfully. "
										+ "Request Id - %s",
								entity.getDataType(), reqId.toString())));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("No Data"));
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception in "
					+ "ImsActionResponseController.getImsActionData()";
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			gstr1FileStatusRepository.updateFailedStatus(savedEntity.getId(),
					ex.getLocalizedMessage(), "Failed");
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ui/imsRequestStatusData", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> imsRequestStatusData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			Long entityId = reqJson.get("entityId").getAsLong();

			List<ImsRequestStatusRespDto> respdata = reqService
					.requestStatusData(entityId);

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
			String msg = "Exception on imsRequestStatusData {}";
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ui/imsRequestStatusErrorDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getRequestStatusErrorFile(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String docId = json.get("docId").getAsString();
		String reqId = json.get("reqId").getAsString();

		try {
			Document document = null;

			if (docId != null) {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition", String
						.format("attachment; filename =" + reqId +"_ErrorRecords.csv"));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception occured ImsActionResponseController"
					+ ".imsRequestStatusErrorDownload() {} ",
					ex.getMessage());
			throw new AppException(ex);
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
