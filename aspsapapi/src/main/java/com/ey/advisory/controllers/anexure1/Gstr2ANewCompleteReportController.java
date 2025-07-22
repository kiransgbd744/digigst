package com.ey.advisory.controllers.anexure1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.ey.advisory.app.services.reports.Gstr2AnewReportHandler;
import com.ey.advisory.app.services.search.datastatus.anx1.ProcessRecordsCommonSecParam;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr2ANewCompleteReportController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2ANewCompleteReportController.class);

	@Autowired
	@Qualifier("Gstr2AnewReportHandler")
	private Gstr2AnewReportHandler gstr2AnewReportHandler;

	@Autowired
	@Qualifier("ProcessRecordsCommonSecParam")
	private ProcessRecordsCommonSecParam commonSecparam;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"B2BA", "CDN", "CDNA", "ISD", "ISDA", "IMPG", "IMPGSEZ", "ECOM", "ECOMA");

	private static final List<String> DOCTYPE = ImmutableList.of("INV", "CR",
			"DR", "RNV", "RCR", "RDR", "ISD", "ISDCN", "ISDA", "ISDCNA", "TCS",
			"TDS", "TDSA", "IMPG", "IMPGSEZ", "R", "DE", "SEWP", "SEWOP");

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	@RequestMapping(value = "/ui/gstr2ACompleteReportsDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		/*
		 * public void downloadreviewSummReport(@RequestBody String jsonString,
		 * HttpServletResponse response) {
		 *//*
			 * 
			 * JsonObject requestObject = (new JsonParser()).parse(jsonString)
			 * .getAsJsonObject(); JsonObject json =
			 * requestObject.get("req").getAsJsonObject(); // String groupcode =
			 * TenantContext.getTenantId(); //
			 * TenantContext.setTenantId(groupcode); Gson gson =
			 * GsonUtil.newSAPGsonInstance();
			 * 
			 * try { String fileName = null; Workbook workbook = null;
			 * Gstr2ProcessedRecordsReqDto criteria = gson.fromJson(json,
			 * Gstr2ProcessedRecordsReqDto.class); Gstr2ProcessedRecordsReqDto
			 * setDataSecurity = commonSecparam
			 * .setGstr2DataSecuritySearchParams(criteria); if
			 * (criteria.getType() != null && criteria.getType()
			 * .equalsIgnoreCase(DownloadReportsConstant.GETGSTR2ACOMPLETE)) {
			 * workbook = gstr2AnewReportHandler
			 * .downloadGet2aNewReport(setDataSecurity);
			 * 
			 * String date = null; String time = null; LocalDateTime now =
			 * EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			 * 
			 * LocalDateTime istDateTimeFromUTC =
			 * EYDateUtil.toISTDateTimeFromUTC(now); DateTimeFormatter FOMATTER
			 * = DateTimeFormatter.ofPattern("ddMMyyyy");
			 * 
			 * DateTimeFormatter FOMATTER1 =
			 * DateTimeFormatter.ofPattern("HHmmss");
			 * 
			 * date = FOMATTER.format(istDateTimeFromUTC); time =
			 * FOMATTER1.format(istDateTimeFromUTC); fileName =
			 * "ConsolidatedGSTR2AReport_" + "_" + date + "" + time; //fileName
			 * = "ConsolidatedGSTR2AReportDownload"; } else {
			 * LOGGER.error("invalid request"); throw new
			 * Exception("invalid request"); } if (workbook == null) { workbook
			 * = new Workbook(); } if (fileName != null) {
			 * response.setContentType("APPLICATION/OCTET-STREAM");
			 * response.setHeader("Content-Disposition", String
			 * .format("attachment; filename=" + fileName + ".xlsx"));
			 * workbook.save(response.getOutputStream(), SaveFormat.XLSX);
			 * response.getOutputStream().flush(); } JsonObject resp = new
			 * JsonObject(); resp.add("hdr",
			 * gson.toJsonTree(APIRespDto.createSuccessResp())); } catch
			 * (JsonParseException ex) { String msg =
			 * "Error while parsing the input Json"; LOGGER.error(msg, ex);
			 * JsonObject resp = new JsonObject(); resp.add("hdr", new
			 * Gson().toJsonTree(new APIRespDto("E", msg)));
			 * 
			 * } catch (Exception ex) { String msg =
			 * "Unexpected error while retriving " + "Data from Report ";
			 * LOGGER.error(msg, ex); JsonObject resp = new JsonObject();
			 * resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			 * }
			 */

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Gstr2ProcessedRecordsReqDto reqDto = gson.fromJson(json,
					Gstr2ProcessedRecordsReqDto.class);

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

	public void setDataToEntity(FileStatusDownloadReportEntity entity,
			Gstr2ProcessedRecordsReqDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (reqDto.getTableType() == null
				|| reqDto.getTableType().size() <= 0) {
			reqDto.setTableType(TABLETYPE);
		}

		if (reqDto.getDocType() == null || reqDto.getDocType().size() <= 0) {
			reqDto.setDocType(DOCTYPE);
		}

		String fy = reqDto.getFy();
		String startMonth = "04";
		String endMonth = "03";
		String appendMonthYear = null;
		String appendMonthYear1 = null;
		String[] arrOfStr = null;
		String start = null;
		String end = null;
		if (fy != null && !fy.isEmpty()) {
			arrOfStr = fy.split("-", 2);
			appendMonthYear = arrOfStr[0] + startMonth;
			appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
		}
		int derivedStartPeriod = Integer.parseInt(appendMonthYear);

		int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

		List<String> monthList = reqDto.getMonth();

		if (!monthList.isEmpty()) {
			int size = monthList.size();
			int start_month = Integer.valueOf(monthList.get(0));
			if (start_month < 4) {
				start = "20" + arrOfStr[1] + monthList.get(0);
			} else {
				start = arrOfStr[0] + monthList.get(0);
			}
			int end_month = Integer.valueOf(monthList.get(size -1));
			if (end_month < 4) {
				end = "20" + arrOfStr[1] + monthList.get(size - 1);
			} else {
				end = arrOfStr[0] + monthList.get(size - 1);
			}

			entity.setTaxPeriodFrom(start);
			entity.setTaxPeriodTo(end);
			entity.setDerivedRetPeriodFrom(Long.valueOf(start));
			entity.setDerivedRetPeriodFromTo(Long.valueOf(end));
		} else {

			entity.setTaxPeriodFrom(appendMonthYear);
			entity.setTaxPeriodTo(appendMonthYear1);
			entity.setDerivedRetPeriodFrom(Long.valueOf(derivedStartPeriod));
			entity.setDerivedRetPeriodFromTo(Long.valueOf(derivedEndPeriod));

		}

		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
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
