package com.ey.advisory.controller.inward.einvoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class InwardEinvoiceDownloadReportController {

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	private static final String ISD = "ISD";

	@RequestMapping(value = "/ui/downloadJsonReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadJsonReport(
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

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setCriteria(criteria.getCriteria());
			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil.convertStringToClob(
					String.join(",", criteria.getGstins())));

			entity.setReportCateg("E-invoice");
			entity.setReportType("JSON Data");

			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(jsonString);
			entity.setDataType("Inward");
			entity.setSupplyType(String.join(",", criteria.getSupplyType()));
			entity.setIrnNum(criteria.getIrn());
			entity.setVendorGstin(criteria.getVendorGstin());
			entity.setType(criteria.getType());
			entity.setDocNum(criteria.getDocNum());
			entity.setTaxPeriodFrom(criteria.getFromPeriod());
			entity.setTaxPeriodTo(criteria.getToPeriod());
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd");

			if (((criteria.getFromDate() != null)
					&& (!criteria.getFromDate().isEmpty()))
					|| (criteria.getToDate() != null)
							&& (!criteria.getToDate().isEmpty())) {

				LocalDate fromDate = LocalDate.parse(criteria.getFromDate(),
						formatter);
				LocalDate toDate = LocalDate.parse(criteria.getToDate(),
						formatter);
				entity.setDocDateFrom(fromDate);
				entity.setDocDateTo(toDate);
			}
			entity.setIrnSts(String.join(",", criteria.getIrnStatus()));

			if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
				entity.setListingIds(GenUtil.convertStringToClob(
						criteria.getIds().stream().map(obj -> obj.toString())
								.collect(Collectors.joining(","))));
			}
			entity.setTaxPeriod(criteria.getReturnPeriod());
			entity = downloadRepository.save(entity);
			Long id = entity.getId();

			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.JSON_REPORT_DOWNLOAD, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected error occured in Inward E-Invoice Json Download Async Report";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/ui/getInwardIrnSummaryReportData", method = RequestMethod.POST, produces = {
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

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD,
					ISD);

			if (CollectionUtils.isEmpty(criteria.getGstins())) {
				List<String> gstnsList = new ArrayList<>();

				List<Long> entityIds = new ArrayList<>();
				entityIds.add(Long.valueOf(criteria.getEntityId()));
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);
				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, regTypeList);

				criteria.setGstins(gstnsList);
			}
			entity.setCriteria(criteria.getCriteria());
			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil.convertStringToClob(
					String.join(",", criteria.getGstins())));
			if ("SUMMARY".equalsIgnoreCase(criteria.getType())) {
				entity.setReportCateg("E-invoice");
				entity.setReportType("Summary Report");
			} else {
				entity.setReportCateg("E-invoice");
				entity.setReportType("Detailed Report (Invoice Level)");
			}

			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(jsonString);
			entity.setDataType("Inward");
			entity.setSupplyType(String.join(",", criteria.getSupplyType()));
			entity.setIrnNum(criteria.getIrn());
			entity.setVendorGstin(criteria.getVendorGstin());
			entity.setType(criteria.getType());
			entity.setDocNum(criteria.getDocNum());

			if (((criteria.getFromPeriod() != null)
					&& (!criteria.getFromPeriod().isEmpty()))
					|| (criteria.getToPeriod() != null)
							&& (!criteria.getToPeriod().isEmpty())) {

				entity.setTaxPeriodFrom(criteria.getFromPeriod().substring(2, 6)
						+ criteria.getFromPeriod().substring(0, 2));
				entity.setTaxPeriodTo(criteria.getToPeriod().substring(2, 6)
						+ criteria.getToPeriod().substring(0, 2));
			}

			if (((criteria.getFromDate() != null)
					&& (!criteria.getFromDate().isEmpty()))
					|| (criteria.getToDate() != null)
							&& (!criteria.getToDate().isEmpty())) {

				LocalDate fromDate = LocalDate.parse(criteria.getFromDate());
				LocalDate toDate = LocalDate.parse(criteria.getToDate());
				entity.setRequestFromDate(fromDate.atStartOfDay());
				entity.setRequestToDate(toDate.atStartOfDay());
			}
			entity.setIrnSts(String.join(",", criteria.getIrnStatus()));

			if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
				entity.setListingIds(GenUtil.convertStringToClob(
						criteria.getIds().stream().map(obj -> obj.toString())
								.collect(Collectors.joining(","))));
			}
			entity.setTaxPeriod(criteria.getReturnPeriod());
			entity = downloadRepository.save(entity);
			Long id = entity.getId();

			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.EINVOICE_SUMMARY_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected error occured in Inward E-Invoice Json Download Async Report";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

}
