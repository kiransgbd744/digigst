package com.ey.advisory.controllers.anexure1;

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
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Gstr7VerticalErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr7VerticalProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr7VerticalTotalReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Gstr7VerticalDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr7VerticalDownloadReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr7VerticalTotalReportHandler")
	private Gstr7VerticalTotalReportHandler gstr7VerticalTotalReportHandler;
	
	@Autowired
	@Qualifier("Gstr7VerticalErrorReportHandler")
	private Gstr7VerticalErrorReportHandler gstr7VerticalErrorReportHandler;
	
	@Autowired
	@Qualifier("Gstr7VerticalProcessedReportHandler")
	private Gstr7VerticalProcessedReportHandler gstr7VerticalProcessedReportHandler;

	@RequestMapping(value = "/ui/downloadGstr7Reports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadProcessedReport(@RequestBody String jsonString, HttpServletResponse response) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from downloadProcessedReport request : {}", jsonString);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Gstr1VerticalDownloadReportsReqDto criteria = gson.fromJson(json, Gstr1VerticalDownloadReportsReqDto.class);

			Long fileId = criteria.getFileId();
			if (fileId == null && fileId == 0L) {
				LOGGER.error("File Id not found in the request and it is invalid");
				throw new Exception("File Id not found in the request and it is invalid");
			}

			String fileName = null;
			Workbook workbook = null;

			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				workbook = gstr7VerticalTotalReportHandler.downloadGstr7VerticalTotalReport(criteria);
			fileName = "Gstr7TotalRecords";
}
			
			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				workbook = gstr7VerticalErrorReportHandler.downloadGstr7VerticalErrorReport(criteria);
			fileName = "Gstr7ErrorRecords";
}
			else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				workbook = gstr7VerticalProcessedReportHandler.downloadGstr7VerticalProcessedReport(criteria);
			fileName = "Gstr7ProcessedRecords";
}		
			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format("attachment; filename=" + fileName + ".xlsx"));
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
			String msg = "Unexpected error while retriving download report";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
}
