package com.ey.advisory.gstr2.ap.recon.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Akhilesh.Yadav
 *
 */
@RestController
@Slf4j
public class Gstr2APRIMSRespPsdInfoErrReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/gstr2APRIMSReportGenerate")
	public ResponseEntity<String> get2AAutoRespReport(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2APRIMSRespPsdInfoErrReportDownloadController";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download 'Recon Response + IMS (2AvsPR)' Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try

		{
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			entity.setFileId(reqDto.getFileId());
			Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepo
					.findById(reqDto.getFileId());
			if (fileStatusEntity.isPresent()) {
				entity.setUpldFileName(fileStatusEntity.get().getFileName());
			}

			entity.setReportType(reqDto.getType());
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("Recon Response + IMS (2AvsPR)");
			entity.setDataType("Inward");

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			String reportType = null;
			if (ReportTypeConstants.ERROR.equalsIgnoreCase(reqDto.getType())) {
				reportType = "Error Reports";

			} else if (ReportTypeConstants.PROCESSED_RECORDS
					.equalsIgnoreCase(reqDto.getType())) {
				reportType = "Processed Reports";

			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType);

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR2A_PR_IMS_RESP_DOWNLOAD,
					jobParams.toString(), userName, 1L, null, null);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Error occured while creating the request");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in "
					+ "Gstr2AAutoRespPsdInfoErrReportDownloadController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "/ui/gstr2APRImsTotalRptDownload")
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("Inside Gstr2APRImsTotalRptDownload ");

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String fileId = json.get("fileId").getAsString();

		Optional<Gstr1FileStatusEntity> entity = fileStatusRepo
				.findById(Long.valueOf(fileId));

		if (entity.isPresent()) {

			String fileName = entity.get().getFileName();
			String fileFolder = ConfigConstants.GSTR2APR_IMS_USERRESPONSE_UPLOADS;

			String docId = entity.get().getDocId();

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
			
			//file name change
			String[] extension = fileName.split("\\.");
			String newFileName = "ReconResponse_2APR+IMS_Total_" + fileId + "." + extension[1];
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Gstr2APRImsTotalRptDownload Downloading Document with fileName : %s",
						newFileName);
				LOGGER.debug(msg);
			}
			
//			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", newFileName));
//			String encodedFileName = URLEncoder.encode(newFileName, "UTF-8").replace("+", " ");

			
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"", newFileName));
//					String.format("attachment; filename = %s", newFileName));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);

			}
		}

	}
	
}
