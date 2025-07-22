/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.gstr1.AsyncGstr2ProcessedRecordsReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
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
public class AsyncGstr2AConsolidatedReportsController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"B2BA", "CDN", "CDNA", "ISD", "ISDA", "IMPG", "IMPGSEZ", "ECOM", "ECOMA");

	private static final List<String> DOCTYPE = ImmutableList.of("INV", "CR",
			"DR", "RNV", "RCR", "RDR", "ISD", "ISDCN", "ISDA", "ISDCNA", "TCS",
			"TDS", "TDSA", "IMPG", "IMPGSEZ", "R", "DE", "SEWP", "SEWOP");

	@PostMapping(value = "/ui/asyncgstr2AConsolidatedReports")
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

			AsyncGstr2ProcessedRecordsReqDto reqDto = gson.fromJson(json,
					AsyncGstr2ProcessedRecordsReqDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			setDataToEntity(entity, reqDto);
			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			String reportType = "GSTR2A  consolidated report";
			jobParams.addProperty("reportType", reportType);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job calling for gstr2a");
				LOGGER.debug(msg);
			}

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2A_CONSOLIDATED, jobParams.toString(),
					userName, 1L, null, null);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job end for gstr2a");
				LOGGER.debug(msg);
			}

			
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

	@GetMapping(value = "/ui/asyncgstr2AConsolidatedReportsById")
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
			AsyncGstr2ProcessedRecordsReqDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (reqDto.getTableType() == null
				|| reqDto.getTableType().size() <= 0) {
			reqDto.setTableType(TABLETYPE);
		}

		if (reqDto.getDocType() == null || reqDto.getDocType().size() <= 0) {
			reqDto.setDocType(DOCTYPE);
		}
		entity.setTaxPeriodFrom(reqDto.getFromPeriod());
		entity.setTaxPeriodTo(reqDto.getToPeriod());
		entity.setDerivedRetPeriodFrom(Long.valueOf(
				GenUtil.convertTaxPeriodToInt(reqDto.getFromPeriod())));
		entity.setDerivedRetPeriodFromTo(Long
				.valueOf(GenUtil.convertTaxPeriodToInt(reqDto.getToPeriod())));

		entity.setCreatedBy(userName);
		entity.setCreatedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setReportStatus(ReportStatusConstants.INITIATED);
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> list = dataSecAttrs.get("GSTIN");
		if (list == null || list.isEmpty()) {
			List<String> gstinList = gstnDetailRepo
					.findByEntityId(reqDto.getEntityId());
			dataSecAttrs.put("GSTIN", gstinList);
			reqDto.setDataSecAttrs(dataSecAttrs);
		}

		entity.setEntityId(reqDto.getEntityId().get(0));
		List<String> tableTypeList = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(reqDto.getTableType())) {
			for (String table : reqDto.getTableType()) {
				tableTypeList.add("'" + table.toUpperCase() + "'");
			}

			entity.setTableType(
					tableTypeList.stream().collect(Collectors.joining(",")));
		}
		List<String> docTypeList = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(reqDto.getDocType())) {
			for (String table : reqDto.getDocType()) {
				docTypeList.add("'" + table.toUpperCase() + "'");
			}
			entity.setGstins(GenUtil.convertStringToClob(convertToQueryFormat(
					dataSecAttrs.get(OnboardingConstant.GSTIN))));
			entity.setDocType(
					docTypeList.stream().collect(Collectors.joining(",")));
			entity.setReportType("GSTR-2A (Compete)");
			entity.setDataType("Inward");
			entity.setReportCateg("GSTR-2A");
		}

	}

	private String convertToQueryFormat(List<String> list) {

		String queryData = null;

		if (list == null || list.isEmpty())
			return null;

		queryData = "'" + list.get(0) + "'";
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + "'" + list.get(i) + "'";
		}

		return queryData;

	}
}
