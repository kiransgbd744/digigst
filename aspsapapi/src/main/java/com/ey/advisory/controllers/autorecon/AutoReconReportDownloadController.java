package com.ey.advisory.controllers.autorecon;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.AutoReconRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2GenerateReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoReconRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2GenerateReportTypeRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoReconReportDownloadReqDto;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoReconReportDownloadService;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoReconReqListingDto;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.ReportTypesDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@RestController
public class AutoReconReportDownloadController {

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("AutoReconRequestRepository")
	private AutoReconRequestRepository autoReconRequestRepo;

	@Autowired
	@Qualifier("AutoReconReportDownloadServiceImpl")
	private AutoReconReportDownloadService autoReconReportDownloadService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2GenerateReportTypeRepository")
	Gstr2GenerateReportTypeRepository childRepo;

	@PostMapping(value = "/ui/generateAutoReconReportReq", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateAutoReconReportRequest(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"generateAutoReconReportReq received Request from UI as {} ",
						jsonString);
			}

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			AutoReconReportDownloadReqDto reconReportDownloadDto = gson
					.fromJson(reqJson, AutoReconReportDownloadReqDto.class);
			String fromTaxPeriod = reconReportDownloadDto.getFromTaxPeriod();
			String toTaxPeriod = reconReportDownloadDto.getToTaxPeriod();
			String reconFromDate = reconReportDownloadDto.getReconFromDate();
			String reconToDate = reconReportDownloadDto.getReconToDate();
			String entityId = reconReportDownloadDto.getEntityId();
			List<String> recipientGstinsList = new ArrayList<>();

			if (CollectionUtils
					.isEmpty(reconReportDownloadDto.getRecipientGstins())) {

				if (entityId.isEmpty()) {
					throw new AppException("EntityID is empty");
				}
				recipientGstinsList = gSTNDetailRepository
						.findgstinByEntityId(Long.valueOf(entityId));
			} else {
				recipientGstinsList = reconReportDownloadDto
						.getRecipientGstins().stream().map(GstinDto::getGstin)
						.collect(Collectors.toList());

			}

			String reportTypes = reconReportDownloadDto.getReportTypes()
					.stream()
					.map(reportTypeDto -> String
							.valueOf(reportTypeDto.getReportType()))
					.collect(Collectors.joining(","));

			List<String> reportTypeList = reconReportDownloadDto
					.getReportTypes().stream()
					.map(ReportTypesDto::getReportType)
					.collect(Collectors.toList());

			Pair<Long,Integer> requestId = autoReconReportDownloadService
					.createEntryAutoReconReportRequest(
							Long.valueOf(recipientGstinsList.size()),
							Long.valueOf(reportTypeList.size()), reportTypes,
							fromTaxPeriod, toTaxPeriod, reconFromDate,
							reconToDate, Long.valueOf(entityId));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Created entry in request table with"
								+ " requestId :%d, for noOfRecipientGstins :%s "
								+ " and noOfReportTypes :%s",
								requestId.getValue0(), recipientGstinsList.size(),
						reportTypeList.size());
				LOGGER.debug(msg);
			}

			if (recipientGstinsList.size() > 0) {
				recipientGstinsList.forEach(
						recipientsGstin -> autoReconReportDownloadService
								.createEntryAutoReconReqRecipientGstin(
										requestId.getValue0(), recipientsGstin));
			}
			if(requestId.getValue1() == 0) {
			// submit job for report generation
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("id", requestId.getValue0());
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR2A_AUTO_RECON_CLOUD_REPORT,
					jsonParams.toString(), userName, 5L, 0L, 0L);
			LOGGER.info(
					"Auto Recon Report Request has been submitted & RequestId is {}",
					requestId);
			}else {
				
				LOGGER.info(
						"Auto Recon Report Request has been not submitted & RequestId is {}",
						requestId);
			}
			

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("Auto Recon Report Request has "
					+ " been submitted & RequestId is " + requestId.getValue0()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = " Unexpected error while generating Auto Recon Request";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getAutoReconReqDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorReportData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			Long entityId = reqJson.get("entityId").getAsLong();

			int recordsToStart = pageNum;
			int noOfRowstoFetch = pageSize;
			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "requestId");

			Pair<List<AutoReconRequestEntity>, Integer> autoReconReqList = autoReconReportDownloadService
					.getAutoReconReqData(entityId, pageReq);
			List<AutoReconReqListingDto> autoReconDataList = autoReconReportDownloadService
					.getAutoReconReqResponse(autoReconReqList.getValue0());

			String jsonReconReqData = gson.toJson(autoReconDataList);
			JsonElement jsonElement = new JsonParser().parse(jsonReconReqData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("requestData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					autoReconReqList.getValue1(), pageNum, pageSize, "S",
					"Successfully fetched Auto Recon Request records")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(
					"Exception while Processing the getAutoRecon request "
							+ ex));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/downloadAutoReconReport")
	public void downloadAutoReconReportUI(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getParameter("requestId"));

		downloadAutoReconReport(response, requestId);

	}

	private void downloadAutoReconReport(HttpServletResponse response,
			Long requestId) throws Exception {

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		Document document = null;
		String fileName = null;
		InputStream inputStream = null;
		// foldername
		String fileFolder = "Gstr2aAutoCloudReport";

		Optional<Gstr2GenerateReportTypeEntity> chileEntity = childRepo
				.findById(requestId);

		if (chileEntity.isPresent()) {
			String docId = chileEntity.get().getDocId();
			fileName = chileEntity.get().getFilePath();

			LOGGER.debug("Inside doc id block ");
			document = DocumentUtility.downloadDocumentByDocId(docId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}
			if (document == null) {
				return;
			}
		} else {

			Optional<AutoReconRequestEntity> entity = autoReconRequestRepo
					.findById(requestId);

			if (entity.isPresent()) {
				fileName = entity.get().getFilePath();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Downloading Document with fileName : %s and Folder Name: %s",
							fileName, fileFolder);
					LOGGER.debug(msg);
				}

				String docId = entity.get().getDocId();

				if (Strings.isNullOrEmpty(docId)) {
					document = DocumentUtility.downloadDocument(fileName,
							fileFolder);
				} else {
					LOGGER.debug("Inside doc id block ");
					document = DocumentUtility.downloadDocumentByDocId(docId);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Document to download : %s",
							document);
					LOGGER.debug(msg);
				}
				if (document == null) {
					return;
				}
			}
		}
         if(document!=null){
		 inputStream = document.getContentStream().getStream();
         }
		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = " + fileName));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}

	}

}
