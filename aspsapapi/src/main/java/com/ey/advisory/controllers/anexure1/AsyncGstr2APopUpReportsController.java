/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
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

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva Krishna
 *
 * 
 */

@RestController
@Slf4j
public class AsyncGstr2APopUpReportsController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"CDN");

	@PostMapping(value = "/ui/asyncgstr2APopUpReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {
		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			GstnConsolidatedReqDto reqDto = gson.fromJson(json,
					GstnConsolidatedReqDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			setDataToEntity(entity, reqDto);
			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job calling for gstr2a");
				LOGGER.debug(msg);
			}

			asyncJobsService.createJob(groupCode, JobConstants.POPUP_REPORTS,
					jobParams.toString(), userName, 1L, null, null);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job end for gstr2a");
				LOGGER.debug(msg);
			}

			String reportType = "GSTR2A  popUp report";
			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@GetMapping(value = "/ui/asyncgstr2APopUpReportsById")
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

	public void setDataToEntity(FileStatusDownloadReportEntity entity,
			GstnConsolidatedReqDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (reqDto.getTableType() == null
				|| reqDto.getTableType().size() <= 0) {
			reqDto.setTableType(TABLETYPE);
		}

		entity.setTaxPeriod(reqDto.getTaxPeriod());
		entity.setDerivedRetPeriod(Long
				.valueOf(GenUtil.convertTaxPeriodToInt(reqDto.getTaxPeriod())));
		// entity.setGstins(reqDto.getGstin());
		String gstin = reqDto.getGstin();
		String a = "'" + gstin + "'";
		// entity.setFyYear(a);
		entity.setGstins(GenUtil.convertStringToClob(a));
		entity.setCreatedBy(userName);
		entity.setCreatedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setReportStatus(ReportStatusConstants.INITIATED);

		entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
		List<String> tableTypeList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(reqDto.getTableType())) {
			for (String table : reqDto.getTableType()) {
				tableTypeList.add("'" + table.toUpperCase() + "'");
			}

			entity.setTableType(
					tableTypeList.stream().collect(Collectors.joining(",")));
		}

		entity.setReportType("GSTR-2A (popUp)");
		entity.setDataType("Get GSTR-2A");
		entity.setReportCateg("GSTR-2A");

	}
}
