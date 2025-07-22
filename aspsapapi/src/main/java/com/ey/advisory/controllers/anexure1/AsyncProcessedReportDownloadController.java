package com.ey.advisory.controllers.anexure1;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
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
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.services.search.filestatussearch.AsyncGstr1EntityLevelReportHandlerImpl;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportTypeConstants;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AsyncProcessedReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AsyncProcessedReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	@Autowired
	@Qualifier("AsyncGstr1EntityLevelReportHandlerImpl")
	AsyncGstr1EntityLevelReportHandlerImpl asyncFileStatusEntityReportHandler;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"B2BA", "B2CL", "B2CLA", "B2CS", "B2CSA", "NILEXTNON", "CDNURA",
			"CDNUR", "CDNRA", "CDNR", "EXPORTS-A", "EXPORTS", "CDNUR-EXPORTS",
			"CDNUR-B2CL", GSTConstants.SUP_ECOM, GSTConstants.ECOM_SUP,
			GSTConstants.AT_STR, GSTConstants.TXP_STR);
	private static final List<String> DOCTYPE = ImmutableList.of("CR", "DR",
			"INV", "RCR", "RDR", "RNV", "BOS", GSTConstants.ADJ,
			GSTConstants.ADV);
	private static final List<String> EWB = ImmutableList.of("YES", "NO");
	private static final List<String> EINV = ImmutableList.of("YES", "NO");

	@PostMapping(value = "/ui/downloadProcessedCsvReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Download CSV Report Controller";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download CSV Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			if (reqDto.getTableType() == null
					|| reqDto.getTableType().isEmpty()) {
				reqDto.setTableType(TABLETYPE);
			}

			if (reqDto.getDocType() == null || reqDto.getDocType().isEmpty()) {
				reqDto.setDocType(DOCTYPE);
			}

			/*
			 * if (reqDto.getEInvGenerated().isEmpty() ||
			 * reqDto.getEInvGenerated().size() <= 0) {
			 * reqDto.setEInvGenerated(EINV); }
			 * 
			 * if (reqDto.getEWbGenerated().isEmpty() ||
			 * reqDto.getEWbGenerated().size() <= 0) {
			 * reqDto.setEWbGenerated(EWB); }
			 */

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			if ("entityLevel".equalsIgnoreCase(reqDto.getType())) {
				asyncFileStatusEntityReportHandler.setDataToEntity(entity,
						reqDto);
			} else {
				asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}
			if(reqDto.getReturnFrom() != null && reqDto.getReturnTo() != null){
				entity.setDerivedRetPeriodFrom(Long.parseLong(reqDto.getReturnFrom()));
				entity.setDerivedRetPeriodFromTo(Long.parseLong(reqDto.getReturnTo()));
			}
			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			if ("OUTWARD".equalsIgnoreCase(entity.getDataType())) {
				if ("asUploaded".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_PROCESS_UPLOADED,
							jobParams.toString(), userName, 1L, null, null);
				} else if ("aspError".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_ASP_ERROR, jobParams.toString(),
							userName, 1L, null, null);
				}

				else if ("gstnError".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_GSTN_ERROR, jobParams.toString(),
							userName, 1L, null, null);
				} else if ("entityLevel".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_ENTITY_LEVEL,
							jobParams.toString(), userName, 1L, null, null);

				} else if ("INWARD".equalsIgnoreCase(entity.getDataType())) {
					if ("gstr2Process".equalsIgnoreCase(reqDto.getType())) {
						asyncJobsService.createJob(groupCode,
								JobConstants.GSTR2_PROCESSED,
								jobParams.toString(), userName, 1L, null, null);
					}
				} else if (ReportTypeConstants.STOCK_TRANSFER
						.equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.STOCK_TRANSFER_REPORT,
							jobParams.toString(), userName, 1L, null, null);

				} else if (ReportTypeConstants.SHIPPING_BILL
						.equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.SHIPPING_REPORT, jobParams.toString(),
							userName, 1L, null, null);

				} else if (ReportTypeConstants.RATE_LEVEL_REPORT
						.equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR1RATELEVEL, jobParams.toString(),
							userName, 1L, null, null);

				}

			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			String reportType = getReportType(reqDto.getType(),
					reqDto.getStatus());

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

	private String getReportType(String type, String status) {

		String reportType = null;
		try {
			switch (type) {

			case ReportTypeConstants.AS_UPLOADED:
				reportType = "GSTR1 Processed Transactional Records";
				break;

			case ReportTypeConstants.ASPERROR:
				reportType = "GSTR1 Consolidated Asp Error";
				break;

			case ReportTypeConstants.GSTNERROR:
				reportType = "GSTR1 Consolidated Gstn Error";
				break;

			case ReportTypeConstants.GSTR2PROCESS:
				reportType = "GSTR2 Processed Records";
				break;

			case ReportTypeConstants.GSTR1ENTITYLEVEL:
				reportType = "GSTR1 EntityLevel Summary";
				break;
				
			case ReportTypeConstants.GSTR1AENTITYLEVEL:
				reportType = "GSTR1A EntityLevel Summary";
				break;

			case ReportTypeConstants.STOCK_TRANSFER:
				reportType = "Stock Transfer";
				break;

			case ReportTypeConstants.SHIPPING_BILL:
				reportType = "Missing Shipping Bill Details";
				break;

			case ReportTypeConstants.RATE_LEVEL_REPORT:
				reportType = "Outward - Rate Level (Limited Column)";
				break;

			default:
				reportType = "Invalid report type";

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;
	}

	@GetMapping(value = "/ui/ProcessedDownloadDocument")
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
	
	public static Long getDerivedTaxPeriod(String taxPeriod) {
		return Long.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));
	}

	public static String getCurrentTaxPeriod() {
		LocalDate currDate = LocalDate.now();
		int month = currDate.getMonthValue();
		int year = currDate.getYear();
		if (month == 1)
			return String.format("%s%s", 12, year);
		else
			return String.format("%02d%s", month - 1, year);
	}

}
