package com.ey.advisory.controllers.anexure1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2ConsolidatedDigiGSTErrorReportController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AsyncGstr2ReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"B2BA", "CDN", "CDNA", "RCURD", "RCURDA", "IMPGS", "IMPG", "IMPS",
			"ISD", "ISDA", "IMPSA", "IMPGA", "IMPGSA", "RCMADV");
	private static final List<String> DOCTYPE = ImmutableList.of("CR", "CR23",
			"DOC", "DR", "EXT", "HJHH", "ICN", "INC", "INV", "INVA", "INVAA",
			"IVN", "NIL", "NON", "NVI", "RCR", "RDR", "RNV", "RPV", "RSLF",
			"SLF", "SMP", "ADV", "ADJ");

	@PostMapping(value = "/ui/downloadGstr2ConsolidatedAspErrorCsvReport")
	public ResponseEntity<String> getconsolidatedErrorReport(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2ConsolidatedDigiGSTErrorReportController";
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
					|| reqDto.getTableType().size() <= 0) {
				reqDto.setTableType(TABLETYPE);
			}

			if (reqDto.getDocType() == null
					|| reqDto.getDocType().size() <= 0) {
				reqDto.setDocType(DOCTYPE);
			}

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
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

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_Consolidated_DigiGST_Error_Report,
					jobParams.toString(), userName, 1L, null, null);

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
					"Unexpected occurred in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error occurred in Async Report Controller";
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

			case ReportTypeConstants.Consolidated_DigiGST_Error_Report:
				reportType = "Consolidated DigiGST Error";
				break;

			default:
				reportType = "Invalid report type";

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;
	}

}
