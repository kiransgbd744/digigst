/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.ITC04AspErrorHandler;
import com.ey.advisory.app.services.reports.ITC04AspProcessAsUploadedHandler;
import com.ey.advisory.app.services.reports.ITC04AspProcessSavableHandler;
import com.ey.advisory.app.services.reports.ITC04ConsolidatedGstnErrorHandler;
import com.ey.advisory.app.services.reports.ITC04ConsolidatedGstnRefidErrorHandler;
import com.ey.advisory.app.services.reports.ITC04EntitylevelHandler;
import com.ey.advisory.app.services.reports.ITC04SavedSubmittedHandler;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@RestController
public class ITC04ProcessedSummaryReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04ProcessedSummaryReportsController.class);

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("ITC04EntitylevelHandler")
	private ITC04EntitylevelHandler iTC04EntitylevelHandler;

	@Autowired
	@Qualifier("ITC04AspProcessAsUploadedHandler")
	private ITC04AspProcessAsUploadedHandler iTC04AspProcessAsUploadedHandler;
	
	@Autowired
	@Qualifier("ITC04AspProcessSavableHandler")
	private ITC04AspProcessSavableHandler iTC04AspProcessSavableHandler;
	
	@Autowired
	@Qualifier("ITC04AspErrorHandler")
	private ITC04AspErrorHandler iTC04AspErrorHandler;

	@Autowired
	@Qualifier("ITC04ConsolidatedGstnErrorHandler")
	private ITC04ConsolidatedGstnErrorHandler iTC04ConsolidatedGstnErrorHandler;
	
	@Autowired
	@Qualifier("ITC04ConsolidatedGstnRefidErrorHandler")
	private ITC04ConsolidatedGstnRefidErrorHandler iTC04ConsolidatedGstnRefidErrorHandler;
	
	@Autowired
	@Qualifier("ITC04SavedSubmittedHandler")
	private ITC04SavedSubmittedHandler iTC04SavedSubmittedHandler;

	@RequestMapping(value = "/ui/ITC04ReportsDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr6SummaryRequestDto criteria = gson.fromJson(json,
					Gstr6SummaryRequestDto.class);
			Gstr6SummaryRequestDto setDataSecurity = basicGstr6SecCommonParam
					.setDataSecuritySearchParams(criteria);
			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ITC04ENTITYLEVEL)) {
				workbook = iTC04EntitylevelHandler
						.downloadEntityLevelSummary(setDataSecurity);
				fileName = "ITC04_Entity_Level_Summary";
			}

			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ITC04ASPUPLOADED)) {
				workbook = iTC04AspProcessAsUploadedHandler
						.downloadAspProcessAsUploaded(setDataSecurity);
				fileName = "ITC04_DigiGST_Process_Data_AS_Uploaded";
			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ITC04GSTNERROR)) {
				workbook = iTC04ConsolidatedGstnErrorHandler
						.downloadconsolidatedgstn(setDataSecurity);
				fileName = "ITC04_GSTN_Error";
			}
			
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ITC04GSTNREFID)) {
				workbook = iTC04ConsolidatedGstnRefidErrorHandler
						.downloadRefidgstn(setDataSecurity);
				fileName = "ITC04_RefId_Records";
			}
			
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ITC04SAVEDSUBMITTED)) {
				workbook = iTC04SavedSubmittedHandler
						.downloadsavedsubmitted(setDataSecurity);
				fileName = "ITC04_Saved_Submitted_Records";
			}
			
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ITC04ASPERROR)) {
				workbook = iTC04AspErrorHandler
						.downloadAspError(setDataSecurity);
				fileName = "ITC04_Consolidated_DigiGST_Error";
			}
			
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ITC04ASPSAVABLE)) {
				workbook = iTC04AspProcessSavableHandler
						.downloadAspProcessSavable(setDataSecurity);
				fileName = "ITC04_DigiGST_Process_Data_Savable";
			}
			else {
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
