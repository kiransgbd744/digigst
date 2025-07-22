/**
 * @author kiran s
 
 
 */
package com.ey.advisory.controller.gl.recon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
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

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GLProcessedConsolidatedSummaryController {
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;
/*	//testing
	@Autowired
	GLConsolidatedSummaryReptProcessorTest gLConsolidatedSummaryReptProcessorTest;
*/
	@PostMapping(value = "/ui/downloadGlConsolidatedReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside GLProcessedConsolidatedSummaryController ";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams).getAsJsonObject();
		JsonObject json = requestObject.getAsJsonObject("req");

		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download GLProcessedConsolidatedSummary Report : %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			GLProcessedSummaryReqDto reqDto = gson.fromJson(json,
					GLProcessedSummaryReqDto.class);

			

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

				setDataToEntity(entity, reqDto);
			
			
			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

		/*	//for testing 
			gLConsolidatedSummaryReptProcessorTest.execute(id);
			*/
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
					asyncJobsService.createJob(groupCode,
							JobConstants.GL_CONSOLIDATED_SUMMARY_REPORT,
							jobParams.toString(), userName, 1L, null, null);
				
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}
			jobParams.addProperty("reportType", "GL Consolidated Report");
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
	
	private void setDataToEntity(FileStatusDownloadReportEntity entity,
			GLProcessedSummaryReqDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		entity.setCreatedBy(userName);
		entity.setTransType(reqDto.getTransactionType());
		entity.setCreatedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setReportStatus(ReportStatusConstants.INITIATED);
			
		entity.setReportCateg("GL Recon");
		entity.setReportType("Consolidated GL Records");
		entity.setDataType("GL Recon");
		entity.setTaxPeriodFrom(reqDto.getTaxPeriodFrom());
		entity.setTaxPeriodTo(reqDto.getTaxPeriodTo());
		
		entity.setGstins(GenUtil.convertStringToClob(
				convertToQueryFormat(reqDto.getGstins())));
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
