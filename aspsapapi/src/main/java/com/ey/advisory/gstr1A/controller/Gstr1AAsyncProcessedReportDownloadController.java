package com.ey.advisory.gstr1A.controller;

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
import com.ey.advisory.app.services.search.filestatussearch.AsyncGstr1EntityLevelReportHandlerImpl;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.GSTConstants;
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

@RestController
@Slf4j
public class Gstr1AAsyncProcessedReportDownloadController {

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
	
	@PostMapping(value = "/ui/downloadGstr1aProcessedCsvReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Download CSV Report Controller";
			LOGGER.debug(msg);
		}

        JsonObject requestObject = JsonParser.parseString(jsonParams).getAsJsonObject();

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


			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			
			/*reqDto.setReportCateg("GSTR1A");
			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}*/

			if ("entityLevel".equalsIgnoreCase(reqDto.getType())) {
				reqDto.setType("gstr1aEntityLevel");
				asyncFileStatusEntityReportHandler.setDataToEntity(entity,
						reqDto);
				entity.setReportCateg("GSTR1A");
				
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
			if(reqDto.getType().equalsIgnoreCase("Gstr1A Shipping Bill")){
				entity.setReportType(ReportTypeConstants.GSTR1ASHIPPING_BILL);
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

			if ("OUTWARD".equalsIgnoreCase(entity.getDataType())|| "OUTWARD_1A".equalsIgnoreCase(entity.getDataType())) {
				if ("gstr1aAsUploaded".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_GSTR1A_PROCESS_UPLOADED,
							jobParams.toString(), userName, 1L, null, null);
				} else if ("gstr1aAspError".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_GSTR1A_ASP_ERROR, jobParams.toString(),
							userName, 1L, null, null);
				} else if ("gstr1aGstnError".equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_GSTR1A_GSTN_ERROR, jobParams.toString(),
							userName, 1L, null, null);
				}else if ("gstr1aEntityLevel".equalsIgnoreCase(reqDto.getType()))
				{
					asyncJobsService.createJob(groupCode,
							JobConstants.ASYNC_ENTITY_LEVEL,
							jobParams.toString(), userName, 1L, null, null);
				
				} else if ("Gstr1A Shipping Bill"
						.equalsIgnoreCase(reqDto.getType())) {
					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR1_SHIPPING_REPORT, jobParams.toString(),
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
					"Unexpected occured in Async Gstr1a Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Gstr1a Async Report Controller";
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

			case ReportTypeConstants.GSTR1A_AS_UPLOADED:
				reportType = "GSTR1A Transactional As Processed";
				break;

			case ReportTypeConstants.GSTR1A_ASPERROR:
				reportType = "GSTR1A Consolidated Asp Error";
				break;

			case ReportTypeConstants.GSTR1A_GSTNERROR:
				reportType = "GSTR1A Consolidated Gstn Error";
				break;
				
			case ReportTypeConstants.GSTR1AENTITYLEVEL:
				reportType = "GSTR1A EntityLevel Summary";
				break;

			case "Gstr1A Shipping Bill":
				reportType = "GSTR1A Missing Shipping Bill Details";
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
