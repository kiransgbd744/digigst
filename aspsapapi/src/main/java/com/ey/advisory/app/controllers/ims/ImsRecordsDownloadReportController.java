package com.ey.advisory.app.controllers.ims;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.app.services.ims.ImsRecordsAsyncReportServiceImpl;
import com.ey.advisory.app.services.ims.ImsRecordsTableReportsServiceImpl;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ImsRecordsDownloadReportController {

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ImsRecordsTableReportsServiceImpl")
	private ImsRecordsTableReportsServiceImpl imsRecordsTableReports;

	@Autowired
	@Qualifier("ImsRecordsAsyncReportServiceImpl")
	private ImsRecordsAsyncReportServiceImpl imsRecordsAsyncReportService;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam commonSecParam;

	@RequestMapping(value = "/ui/getImsRecordsSummaryReportData", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadSummaryReport(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			Gstr1GetInvoicesReqDto criteria = gson.fromJson(json,
					Gstr1GetInvoicesReqDto.class);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			
			LOGGER.debug("ImsRecordsDownloadReportController Report Type : "+criteria.getReportType());

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil.convertStringToClob(
					String.join(",", criteria.getGstins())));
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setDataType("Inward");
			entity.setType(criteria.getType());
			entity.setReportCateg("IMS");
//			entity.setReportType("IMS Records Report");
			entity.setTableType(String.join(",", criteria.getTableType()));

			if ("ALL".equalsIgnoreCase(criteria.getReportType())) {
//				entity.setReportType("IMS Records Report");
				entity.setReportType("IMS Records Active+Inactive Report");
			} else if ("ACTIVE".equalsIgnoreCase(criteria.getReportType())) {
				entity.setReportType("IMS Records Active Report");
			} else if ("ALL_IR".equalsIgnoreCase(criteria.getReportType())) {
				entity.setReportType("IMS Records Report");
			} else if (ReportTypeConstants.IMS_AMD_ORG_TRACK_REPORT.equalsIgnoreCase(criteria.getReportType())) {
                entity.setReportType(ReportTypeConstants.IMS_AMD_ORG_TRACK_REPORT);
			} else {
				LOGGER.error("Invalid report type");
				throw new Exception("invalid request");
			}

			if ("IMSRecords".equalsIgnoreCase(criteria.getType())) {

				// For IMS RECORDS
				entity.setVendorPan(String.join(",", criteria.getVendorPan()));
				entity.setVendorGstin(
						String.join(",", criteria.getVendorGstins()));
				entity.setDocNum(String.join(",", criteria.getDocNums()));

				if (criteria.getId() != null && !criteria.getId().isEmpty()) {
					entity.setListingIds(GenUtil.convertStringToClob(
							criteria.getId().stream().map(obj -> obj.toString())
									.collect(Collectors.joining(","))));
				}

				entity.setFmResp(String.join(",", criteria.getGstnAction()));
				entity.setUsrResp(
						String.join(",", criteria.getDigiGstAction()));
				entity.setDocType(String.join(",", criteria.getDocType()));
				if (((criteria.getFromDate() != null)
						&& (!criteria.getFromDate().isEmpty()))
						|| (criteria.getToDate() != null)
								&& (!criteria.getToDate().isEmpty())) {

					LocalDate fromDate = LocalDate
							.parse(criteria.getFromDate());
					LocalDate toDate = LocalDate.parse(criteria.getToDate());
					entity.setRequestFromDate(fromDate.atStartOfDay());
					entity.setRequestToDate(toDate.atStartOfDay());
				}

			} else if(ReportTypeConstants.IMS_AMD_ORG_TRACK_REPORT.equalsIgnoreCase(criteria.getReportType())){
				entity.setDerivedRetPeriodFrom(toYearMonthAsLong(criteria.getFromPeriod()));
				entity.setDerivedRetPeriodFromTo(toYearMonthAsLong(criteria.getToPeriod()));
			}

			entity = downloadRepository.save(entity);
			Long id = entity.getId();

			// imsRecordsAsyncReportService.generateReports(id);
			
//			if ("IMS Records Report".equalsIgnoreCase(entity.getReportType())) {
//				entity.setReportType("IMS Records Active+Inactive Report");
//			}

			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());
			
			String jobName = JobConstants.IMS_SUMMARY_REPORT;
            if (ReportTypeConstants.IMS_AMD_ORG_TRACK_REPORT.equalsIgnoreCase(entity.getReportType())) {
                    jobName = JobConstants.IMS_AMD_ORG_TRACK_REPORT;
            }

			asyncJobsService.createJob(TenantContext.getTenantId(),
					jobName, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected error occured in Ims Records Download Async Report";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
	
	public static long toYearMonthAsLong(String taxPeriod) {
	    if (taxPeriod == null || taxPeriod.length() != 6) {
	        throw new IllegalArgumentException("taxPeriod must be in MMyyyy format");
	    }

	    String yearMonth = taxPeriod.substring(2) 
	                     + taxPeriod.substring(0, 2);

	    return Long.parseLong(yearMonth);
	}

	@RequestMapping(value = "/ui/getImsTableReportsDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getImsTableReportsDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			ImsEntitySummaryReqDto criteria = gson.fromJson(json,
					ImsEntitySummaryReqDto.class);

			if ("getCallTableData".equalsIgnoreCase(criteria.getType())) {
				workbook = imsRecordsTableReports
						.findImsRecordsGetcall(criteria, null);
				fileName = "IMS_GetCall_Dashboard";

			} else if ("ImsSummaryTableData"
					.equalsIgnoreCase(criteria.getType())) {

				workbook = imsRecordsTableReports
						.findImsSummaryEntityLevel(criteria, null);
				fileName = "IMS_Count_Summary";
			} else {
				LOGGER.error("invalid request");
				throw new Exception("invalid request");
			}
			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving "
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}

}
