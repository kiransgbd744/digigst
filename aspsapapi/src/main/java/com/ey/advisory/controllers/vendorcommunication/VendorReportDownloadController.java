package com.ey.advisory.controllers.vendorcommunication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorEmailStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReportDownloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqRecipientGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.app.services.vendorcomm.AsyncVendorComm2BPRReportUploadServiceImpl;
import com.ey.advisory.app.services.vendorcomm.VendorCommReportDataService;
import com.ey.advisory.app.services.vendorcomm.VendorCommService;
import com.ey.advisory.app.services.vendorcomm.VendorCommunicationService;
import com.ey.advisory.app.services.vendorcomm.VendorEmailCommunicationService;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.ReportTypesDto;
import com.ey.advisory.app.vendorcomm.dto.VendorCommListingRespDto;
import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;
import com.ey.advisory.app.vendorcomm.dto.VendorEmailStatusReqDto;
import com.ey.advisory.app.vendorcomm.dto.VendorReportDownloadDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class VendorReportDownloadController {

	@Autowired
	@Qualifier("VendorReportDownloadRepository")
	VendorReportDownloadRepository vendorReportDownloadRepository;
	
	@Autowired
	@Qualifier("VendorEmailStatusRepository")
	VendorEmailStatusRepository vendorEmailStatusRepository;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("VendorCommServiceImpl")
	private VendorCommService vendorCommService;

	@Autowired
	@Qualifier("VendorCommReportDataService")
	private VendorCommReportDataService vendorCommReportDataService;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorReqRecipientGstinRepository")
	VendorReqRecipientGstinRepository vendorReqRecipientGstinRepository;

	@Autowired
	VendorReqVendorGstinRepository vendorReqVendorGstinRepository;

	@Autowired
	AsyncVendorComm2BPRReportUploadServiceImpl impl;

	@Autowired
	@Qualifier("VendorCommunicationServiceImpl")
	private VendorCommunicationService vendorCommunicationService;

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	private VendorCommRequestRepository vendorCommRequestRepository;

	@Autowired
	@Qualifier("VendorEmailCommunicationServiceImpl")
	private VendorEmailCommunicationService vendorEmailCommunicationService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	@Autowired
	AsyncJobsService asyncJobsService;

	@GetMapping(value = "/ui/downloadVendorReport")
	public void downloadVendorReportUI(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getParameter("requestId"));

		downloadVendorReport(response, requestId);

	}

	@GetMapping(value = "/vendorApi/downloadVendorReport")
	public byte[] downloadVendorReportStream(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getHeader("requestId"));
		String vendorGstin = request.getHeader("vendorGstin");

		Optional<VendorReqVendorGstinEntity> resultOptional = vendorReqVendorGstinRepository.getFilePathDocId(requestId, vendorGstin);


		String fileFolder = "VendorCommReport";
		String fileName = null;
		String docId = null;
		if (resultOptional.isPresent()) {
		    VendorReqVendorGstinEntity entity = resultOptional.get();
		    fileName = entity.getFilePath();
		    docId = entity.getDocId();
		} 

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Downloading Document with fileName : %s and Folder Name: %s",
					fileName, fileFolder);
			LOGGER.debug(msg);
		}
//		Document document = DocumentUtility.downloadDocument(fileName,
//				fileFolder);
		
		Document document = null;
		if (Strings.isNullOrEmpty(docId)) {
			document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
		} else {
			document = DocumentUtility.downloadDocumentByDocId(docId);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Document to download : %s", document);
			LOGGER.debug(msg);
		}
		if (document == null) {
			return null;
		}

		InputStream inputStream = document.getContentStream().getStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);

		}
		return outputStream.toByteArray();
	}

	private void downloadVendorReport(HttpServletResponse response,
			Long requestId) throws Exception {
		LOGGER.debug("inside Async Report file Downloads");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

//		String fileName = vendorCommRequestRepository.getFilePath(requestId);
		
		Optional<VendorCommRequestEntity> resultOptional = vendorCommRequestRepository.getFilePathDocId(requestId);


		String fileFolder = "VendorCommReport";
		String fileName = null;
		String docId = null;
		if (resultOptional.isPresent()) {
			VendorCommRequestEntity entity = resultOptional.get();
		    fileName = entity.getFilePath();
		    docId = entity.getDocId();
		} 

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Downloading Document with fileName : %s and Folder Name: %s",
					fileName, fileFolder);
			LOGGER.debug(msg);
		}
		Document document = null;
		if (Strings.isNullOrEmpty(docId)) {
			document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
		} else {
			document = DocumentUtility.downloadDocumentByDocId(docId);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Document to download : %s", document);
			LOGGER.debug(msg);
		}
		if (document == null) {
			return;
		}
		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];

		response.setHeader("Content-Disposition",
				String.format("attachment; filename = %s", fileName));
		OutputStream outputStream = response.getOutputStream();
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);

		}
	}

	@PostMapping(value = "/ui/getVendorRequestDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorReportData(
			@RequestBody String jsonParam) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			String reconType = reqObject.get("reconType") != null
					? reqObject.get("reconType").getAsString() : null;
			if (reconType == null) {
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("ReconType is  mandatory"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			List<VendorCommRequestEntity> vendorComReqList = vendorCommService
					.getVendorCommDataByUserName(userName, reconType);
			List<VendorCommListingRespDto> vendorCommDataList = vendorCommReportDataService
					.getVendorCommResponse(vendorComReqList);
			Collections.sort(vendorCommDataList,
					new Comparator<VendorCommListingRespDto>() {
						public int compare(VendorCommListingRespDto o1,
								VendorCommListingRespDto o2) {
							return (int) (o2.getRequestId()
									- o1.getRequestId());
						}
					});
			String jsonVendorData = gson.toJson(vendorCommDataList);
			JsonElement vendorJsonElement = new JsonParser()
					.parse(jsonVendorData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("vendorData", vendorJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Error", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getVendorReport request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/generateVendorReportRequest", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateVendorReportRequest(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		boolean isAPOpted = true;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"generateVendorReportRequest Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			VendorReportDownloadDto vendorReportDownloadDto = gson
					.fromJson(reqJson, VendorReportDownloadDto.class);
			String fromTaxPeriod = vendorReportDownloadDto.getFromTaxPeriod();
			String toTaxPeriod = vendorReportDownloadDto.getToTaxPeriod();
			String entityId = vendorReportDownloadDto.getEntityId();
			String reconType = vendorReportDownloadDto.getReconType();
			if (reconType == null) {
				String msg = "ReconType is  mandatory";
				LOGGER.error(msg);
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			List<String> recipientGstinsList = null;
			List<String> vendorGstinsList = new ArrayList<>();
			Integer derivedFromTaxPeriod = Integer.valueOf(fromTaxPeriod
					.substring(2).concat(fromTaxPeriod.substring(0, 2)));
			Integer derivedToTaxPeriod = Integer.valueOf(toTaxPeriod
					.substring(2).concat(toTaxPeriod.substring(0, 2)));

			if (CollectionUtils
					.isEmpty(vendorReportDownloadDto.getRecipientGstins())) {

				if (entityId.isEmpty()) {
					throw new AppException("EntityID is empty");
				}
				
				if("2A_PR".equalsIgnoreCase(reconType))
				{
				recipientGstinsList = gSTNDetailRepository
						.findgstinByEntityId(Long.valueOf(entityId));
				}else
				{
						
					recipientGstinsList = gSTNDetailRepository
							.findgstinByEntityIdWithOutISD(Arrays.asList(Long.valueOf(entityId)));

				}
			} else {
				recipientGstinsList = vendorReportDownloadDto
						.getRecipientGstins().stream().map(GstinDto::getGstin)
						.collect(Collectors.toList());

			}

			if (CollectionUtils
					.isEmpty(vendorReportDownloadDto.getVendorGstins())
					&& !CollectionUtils
							.isEmpty(vendorReportDownloadDto.getVendorPans())) {
				List<String> recPanList = recipientGstinsList.stream()
						.map(o -> o.substring(2, 12))
						.collect(Collectors.toList());

				List<String> vendorPans = vendorReportDownloadDto
						.getVendorPans().stream().map(GstinDto::getGstin)
						.collect(Collectors.toList());
				List<VendorMasterUploadEntity> uploadEntities = new ArrayList<>();

				List<List<String>> chunks = Lists.partition(vendorPans, 2000);
				for (List<String> chunk : chunks) {
					uploadEntities.addAll(vendorMasterUploadEntityRepository
							.findByIsDeleteFalseAndRecipientPANInAndVendorPANIn(
									recPanList, chunk));
				}

				vendorGstinsList = uploadEntities.stream()
						.map(o -> o.getVendorGstin())
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"vendor Gstin List: {} when vendor pans is {} and recipient pans {} ",
							vendorGstinsList, vendorPans, recPanList);
				}

			}
			if (!CollectionUtils
					.isEmpty(vendorReportDownloadDto.getVendorGstins())) {
				vendorGstinsList = vendorReportDownloadDto.getVendorGstins()
						.stream().map(GstinDto::getGstin)
						.collect(Collectors.toList());
			}
			String reportTypes = vendorReportDownloadDto.getReportTypes()
					.stream()
					.map(reportTypeDto -> String
							.valueOf(reportTypeDto.getReportType()))
					.collect(Collectors.joining(","));

			List<String> reportTypeList = vendorReportDownloadDto
					.getReportTypes().stream()
					.map(ReportTypesDto::getReportType)
					.collect(Collectors.toList());

			if ("2A_PR".equalsIgnoreCase(reconType)) {
				List<Long> optedEntities = entityConfigPemtRepo
						.getAllEntitiesOpted2B(
								Arrays.asList(Long.valueOf(entityId)), "I27");

				if (optedEntities.isEmpty()) {
					isAPOpted = false;
				}
			}
			Pair<List<String>, List<String>> validGstins = vendorCommunicationService
					.getDistinctCombination(derivedFromTaxPeriod,
							derivedToTaxPeriod, reportTypeList,
							recipientGstinsList, vendorGstinsList, isAPOpted,
							reconType);

			List<String> validRGstins = validGstins.getValue0().stream()
					.distinct().collect(Collectors.toList());
			List<String> validVGstins = validGstins.getValue1().stream()
					.distinct().collect(Collectors.toList());

			Long requestID = vendorCommunicationService
					.createEntryVendorCommRequest(
							Long.valueOf(validRGstins.size()),
							Long.valueOf(validVGstins.size()),
							Long.valueOf(reportTypeList.size()), reportTypes,
							fromTaxPeriod, toTaxPeriod, reconType);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Valid RGSTIN size: {}, Valid VGSTIN size: {} , reconType",
						validRGstins.size(), validVGstins.size(), reconType);
			}
			if (!validVGstins.isEmpty()) {
				validRGstins
						.forEach(recipientsGstin -> vendorCommunicationService
								.createEntryVendorReqRecipientGstin(requestID,
										recipientsGstin));

				validVGstins.forEach(vendorGstin -> vendorCommunicationService
						.createEntryVendorReqVendorGstin(requestID,
								vendorGstin));

				vendorCommunicationService.generateVendorReportUploadAsync(
						requestID, isAPOpted, reconType);

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Vendor Report Request has been submitted & RequestId is {}",
						requestID);
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Vendor Report Request has been submitted & RequestId is "
									+ requestID));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception on Vendor Report Request";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorEmailCommunicationDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEmailCommunicationDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getVendorEmailCommunicationDetails Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Long requestID = reqJson.get("requestID").getAsLong();
			Long entityId = reqJson.get("entityId").getAsLong();
			String reconType = reqJson.get("reconType") != null
					? reqJson.get("reconType").getAsString() : null;

			if (reconType == null) {
				String msg = "ReconType is  mandatory";
				LOGGER.error(msg);
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Pair<List<VendorEmailCommDto>, Integer> vendorEmailCommDtoListPair = vendorEmailCommunicationService
					.getEmailCommunicationDetails(requestID, entityId, pageSize,
							pageNum);
			List<VendorEmailCommDto> vendorEmailCommDtoList = vendorEmailCommDtoListPair
					.getValue0();
			Collections.sort(vendorEmailCommDtoList,
					new Comparator<VendorEmailCommDto>() {
						public int compare(VendorEmailCommDto o1,
								VendorEmailCommDto o2) {
							return (o1.getVendorGstin()
									.compareTo(o2.getVendorGstin()));
						}
					});
			String latestEntity = vendorEmailStatusRepository
					.getLatestRequestId(requestID,entityId);
			
			String latestStatus = (latestEntity != null) ? latestEntity : "COMPLETED";

			
			/*String latestStatus = (latestEntity != null
					? latestEntity != null
							? latestEntity : null
					: "COMPLETED");*/
            
			String jsonEINV = gson.toJson(vendorEmailCommDtoList);
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					vendorEmailCommDtoListPair.getValue1(), pageNum, pageSize,
					"S",
					"Successfully fetched Vendor email Communication details")));
			resp.addProperty("reconType", reconType);
			resp.add("resp", einvJsonElement);
			resp.add("latestStatus", gson.toJsonTree(latestStatus));
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception on getVendorEmailCommunicationDetails ",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/postVendorEmailCommDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> postEmailCommunicationDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"postVendorEmailCommDetails Request received from UI as {} ",
						jsonString);
			}

			String res = vendorEmailCommunicationService
					.createReqPayloadForEmail(jsonString);
			if (res.equals("SUCCESS")) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp",
						gson.toJsonTree("Vendor's Mail has been sent"));
			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(res));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception on postVendorEmailCommDetails ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	@PostMapping(value = "/ui/getAndPostVendorEmailCommDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAndPostVendorEmailCommDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			
			String groupCode = TenantContext.getTenantId();

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();


			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Long requestID = reqJson.get("requestID").getAsLong();
			Long entityId = reqJson.get("entityId").getAsLong();
			String reconType = reqJson.get("reconType") != null
					? reqJson.get("reconType").getAsString() : null;

			if (reconType == null) {
				String msg = "ReconType is  mandatory";
				LOGGER.error(msg);
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			VendorEmailStatusReqDto reqDto = gson.fromJson(reqJson,
					VendorEmailStatusReqDto.class);
	
			vendorEmailStatusRepository.softlyDeleteRequestIds(requestID,entityId);
			VendorEmailStatusEntity entity = new VendorEmailStatusEntity();
              //  entity.setId(reqDto.getId());
				entity.setStatus(ReportStatusConstants.INITIATED);
				entity.setRequestId(requestID);
				entity.setReconType(reconType);
				entity.setEntityId(entityId);
				entity.setCreatedBy(userName);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setIsDelete(false);
				entity = vendorEmailStatusRepository.save(entity);

				Long id = entity.getId();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Successfully saved to DB with Report Id : %d", id);
					LOGGER.debug(msg);
				}

				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("id", id);
				jobParams.addProperty("entityId", entityId);
				jobParams.addProperty("requestID", requestID);

				asyncJobsService.createJob(groupCode,
						JobConstants.Vendor_Bulk_Email, jobParams.toString(),
						userName, 1L, null, null);

			//String jsonEINV = gson.toJson(vendorEmailCommDtoList);
			//JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			resp.add("resp",
					gson.toJsonTree("Email has been successfully initiated for all selected email ids"));
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			
		} catch (Exception ex) {
			LOGGER.error("Exception on getVendorEmailCommunicationDetails ",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}	
	
}