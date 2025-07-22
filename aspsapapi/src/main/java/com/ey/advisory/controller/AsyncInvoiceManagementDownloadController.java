/**
 * 
 */
package com.ey.advisory.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.search.filestatussearch.AsyncInvManagementReportHandler;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class AsyncInvoiceManagementDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AsyncInvManagementReportHandlerImpl")
	AsyncInvManagementReportHandler asyncInvManagementReportHandler;

	@PostMapping(value = "/ui/downloadInvoiceManagementCsvReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Inv mangmnet Download CSV Report Controller";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String groupCode = TenantContext.getTenantId();

		boolean flag = json.has("flag") ? json.get("flag").getAsBoolean()
				: false;

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Inv mangmnet  Download CSV Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			EInvoiceDocSearchReqDto reqDto = gson.fromJson(json,
					EInvoiceDocSearchReqDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}
			List<String> docNo = reqDto.getDocNums();
			int totalLength = docNo.stream().mapToInt(String::length).sum();
			if (totalLength > 2000) {
				APIRespDto dto = new APIRespDto("Failed",
						"Document Numbers have exceeded the limit of 2000 characters");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				String msg = "Document Numbers have exceeded the limit of 2000 characters";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				// LOGGER.error(msg, e);
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			asyncInvManagementReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Inv mangmnet  Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}
			if (flag == true)
				entity.setReportType("Shipping Bill");

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			asyncJobsService.createJob(groupCode,
					JobConstants.INVOICE_MANAGEMENT_DOWNLOAD_REPORT,
					jobParams.toString(), userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}
			String reportType = null;
			if (flag == true)
				reportType = "Missing Shipping Bill Details";
			else
				reportType = getReportType(reqDto.getProcessingStatus());

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occurred in Async Report Inv mangmnet  CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error occurred in Async Inv mangmnet  Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}
	}

	private String getReportType(String processingStatus) {

		String reportType = null;
		if (Strings.isNullOrEmpty(processingStatus)) {
			processingStatus = "totalData";
		}
		try {
			switch (processingStatus) {

			case "P":
				reportType = "InvoiceMgt_ProcessedRecords";
				break;

			case "E":
				reportType = "InvoiceMgt_ErrorRecords";
				break;

			case "I":
				reportType = "InvoiceMgt_ProcessedInfoData";
				break;

			case "totalData":
				reportType = "InvoiceMgt_data";
				break;

			default:
				reportType = "Invalid report type";

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;
	}

	@GetMapping(value = "/ui/InvoiceManagementDownloadDocument")
	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside Async Report file Downloads");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);
		String id = request.getParameter("configId");
		Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
				.findById(Long.valueOf(id));

		if (entity.isPresent()) {

			String fileName = entity.get().getFilePath();
			String fileFolder = "Anx1FileStatusReport";

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Downloading Document with fileName : %s and Folder Name: %s",
						fileName, fileFolder);
				LOGGER.debug(msg);
			}
			Document document = null;
			if (Strings.isNullOrEmpty(entity.get().getDocId())) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			} else {
				String docId = entity.get().getDocId();
				String msg = String.format(
						"Doc Id is available for File Name %s and Doc Id %s",
						fileName, docId);
				LOGGER.debug(msg);
				document = DocumentUtility
						.downloadDocumentByDocId(entity.get().getDocId());
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}

			if (document == null) {
				return;
			}


			InputStream inputStream = document.getContentStream().getStream();
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
}
