package com.ey.advisory.app.controllers.gstnNotices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.get.notices.handlers.GstnNoticeReqDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author ashutosh.kar
 *
 */

@RestController
public class GstnNoticeReportDownloadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstnNoticeReportDownloadController.class);

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/downloadNoticeDetailedReport")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside GstnNoticeReportDownloadController";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download GSTN Notice Detailed Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try

		{
			GstnNoticeReqDto reqDto = gson.fromJson(json,
					GstnNoticeReqDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			/*entity.setFileId(reqDto.getFileId());
			Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepo
					.findById(reqDto.getFileId());
			if (fileStatusEntity.isPresent()) {
				entity.setUpldFileName(fileStatusEntity.get().getFileName());
			}*/

			entity.setReportType(reqDto.getReportType());
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setCriteria(reqDto.getSelectionCriteria());
			List<String> gstinList = reqDto.getGstins();
			if (gstinList != null && !gstinList.isEmpty()) {
				entity.setGstins(GenUtil
						.convertStringToClob(convertToQueryFormat(gstinList)));
			}
			entity.setEntityId(reqDto.getEntityId());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("Fetch Notices");
			entity.setDataType("Fetch Notices");

			// Handle MMYYYY to YYYYMM conversion
			Long derivedFrom = null;
			Long derivedTo = null;

			String from = reqDto.getFromTaxPeriod();
			String to = reqDto.getToTaxPeriod();

			if (from != null && from.trim().length() == 6) {
				derivedFrom = Long.valueOf(from.trim().substring(2, 6)
						+ from.trim().substring(0, 2));
			}

			if (to != null && to.trim().length() == 6) {
				derivedTo = Long.valueOf(
						to.trim().substring(2, 6) + to.trim().substring(0, 2));
			}

			if ("TaxPeriod wise"
					.equalsIgnoreCase(reqDto.getSelectionCriteria())) {
				entity.setDerivedRetPeriodFrom(derivedFrom);
				entity.setDerivedRetPeriodFromTo(derivedTo);
			} else {
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");

				if (reqDto.getFromDate() != null) {
					entity.setDocDateFrom(
							LocalDate.parse(reqDto.getFromDate(), formatter));
				}
				if (reqDto.getToDate() != null) {
					entity.setDocDateTo(
							LocalDate.parse(reqDto.getToDate(), formatter));

				}
			}

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			String reportType = null;
			if ("Detailed Report".equalsIgnoreCase(reqDto.getReportType())) {
				reportType = "Detailed Download Report";

			} /*
				 * else if (ReportTypeConstants.PROCESSED_RECORDS
				 * .equalsIgnoreCase(reqDto.getType())) { reportType =
				 * "Processed Reports";
				 * 
				 * }
				 */

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType);

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTN_NOTICE_DETAILED_REPORT,
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
					+ "GstnNoticeReportDownloadController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private String convertToQueryFormat(List<String> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}

		return list.stream().filter(Objects::nonNull)
				.map(gstin -> "'" + gstin + "'")
				.collect(Collectors.joining(","));
	}

	/*
	 * public static void main(String[] args) { List<String> gstins =
	 * Arrays.asList("33TDSMH0481Y1DD", "29GSPTN0482G1Z9");
	 * 
	 * String formatted = convertToQueryFormat(gstins);
	 * System.out.println("Formatted GSTIN String:");
	 * System.out.println(formatted); }
	 */

}
