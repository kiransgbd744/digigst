package com.ey.advisory.controllers.anexure1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.Gstr1ADownloadEntitySave;
import com.ey.advisory.app.services.reports.Gstr1B2BASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2BSectionReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@RestController
@Slf4j
public class Gstr1SectionWiseReportDownloadController {

	@Autowired
	@Qualifier("Gstr1ADownloadEntitySave")
	private Gstr1ADownloadEntitySave downloadEntitySave;
	
	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;
	
	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("Gstr1B2BSectionReportHandler")
	private Gstr1B2BSectionReportHandler gstr1B2BSectionReportHandler;
	
	@Autowired
	@Qualifier("Gstr1B2BASectionReportHandler")
	private Gstr1B2BASectionReportHandler gstr1B2BASectionReportHandler;

	@RequestMapping(value = "/ui/downloadGstr1SectionWiseReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadreviewSummReport(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr1ReviwSummReportsReqDto.class);
			
			
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			
			
			if(Strings.isNullOrEmpty(criteria.getReturnType())){
				criteria.setReturnType(APIConstants.GSTR1.toUpperCase());
			}
			
			
			
			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.B2B)) {
				
				criteria.setReportType("GSTR1_B2B");
				JsonObject resp = getResponse(criteria,"B2B");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.B2BA)) {
				criteria.setReportType("GSTR1_B2BA");
				JsonObject resp = getResponse(criteria,"B2BA");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.B2CL)) {
				criteria.setReportType("GSTR1_B2CL");
				JsonObject resp = getResponse(criteria,"B2CL");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.B2CLA)) {
				criteria.setReportType("GSTR1_B2CLA");
				JsonObject resp = getResponse(criteria,"B2CLA");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.EXPORTS)) {
				criteria.setReportType("GSTR1_EXPORTS");
				JsonObject resp = getResponse(criteria,"EXPORTS");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.EXPORTA)) {
				criteria.setReportType("GSTR1_EXPORTA");
				JsonObject resp = getResponse(criteria,"EXPORTA");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			
			//making async CDNR
			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.CDNR)) {
				criteria.setReportType("GSTR1_CDNR");
				JsonObject resp = getResponse(criteria,"CDNR");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			} 
			
			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.CDNRA)) {
				criteria.setReportType("GSTR1_CDNRA");
				JsonObject resp = getResponse(criteria,"CDNRA");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.CDNUR)) {
				criteria.setReportType("GSTR1_CDNUR");
				JsonObject resp = getResponse(criteria,"CDNUR");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.CDNURA)) {
				criteria.setReportType("GSTR1_CDNURA");
				JsonObject resp = getResponse(criteria,"CDNURA");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			//making async B2CS1
			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.B2CS1)) {
				criteria.setReportType("GSTR1_B2CS");
				JsonObject resp = getResponse(criteria,"B2CS");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			
			if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
					.equalsIgnoreCase(DownloadReportsConstant.B2CSA)) {
				criteria.setReportType("GSTR1_B2CSA");
				JsonObject resp = getResponse(criteria,"B2CSA");
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			
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
		return null;

	}
	
	
	private JsonObject getResponse(Gstr1ReviwSummReportsReqDto criteria,String reportType){
		
		
		
		Gstr1ReviwSummReportsReqDto setDataSecurity = basicCommonSecParamRSReports
				.setDataSecuritySearchParams(criteria);
		
        String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		String jsonString = gson.toJson(criteria);
		JsonObject resParams = new JsonObject();
		Long id = null;
		
		id = downloadEntitySave.saveDownloadEntity(jsonString,
				setDataSecurity);
		
		JsonObject jobParams = new JsonObject();
		jobParams.addProperty("id", id);
		if (criteria.getTaxDocType() != null && criteria.getTaxDocType()
				.equalsIgnoreCase(DownloadReportsConstant.EXPORTA)) {
			jobParams.addProperty("reportType", "EXPORTA");
		}else{
			jobParams.addProperty("reportType", criteria.getTaxDocType());
		}
		//jobParams.addProperty("reportType", criteria.getTaxDocType());
		jobParams.addProperty("jsonString", gson.toJson(criteria));
		
		asyncJobsService.createJob(groupCode,
				JobConstants.GSTR1_SectionWise_Report, jobParams.toString(),
				userName, 1L, null, null);
		
//		gstr1B2BSectionReportHandler.downloadRSProcessedReport(criteria, id);
//		gstr1B2BASectionReportHandler.downloadRSProcessedReport(criteria, id);
		
		resParams.addProperty("id", id);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Successfully created JOB with id : %d", id);
			LOGGER.debug(msg);
		}

		resParams.addProperty("reportType", reportType);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(resParams);
		resp.add("hdr",
				gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		
		return resp;
	}
	
	

}
