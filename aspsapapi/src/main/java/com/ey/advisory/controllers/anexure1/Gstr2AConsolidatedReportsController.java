/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.ey.advisory.app.services.reports.Gstr2AConsolidatedReportHandler;
import com.ey.advisory.app.services.search.datastatus.anx1.ProcessRecordsCommonSecParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@RestController
public class Gstr2AConsolidatedReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2AConsolidatedReportsController.class);

	@Autowired
	@Qualifier("Gstr2AConsolidatedReportHandler")
	private Gstr2AConsolidatedReportHandler gstr2AConsolidatedReportHandler;

	@Autowired
	@Qualifier("ProcessRecordsCommonSecParam")
	private ProcessRecordsCommonSecParam commonSecparam;

	@RequestMapping(value = "/ui/gstr2AReportsDownload", method = RequestMethod.POST, produces = {
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
			Gstr2ProcessedRecordsReqDto criteria = gson.fromJson(json,
					Gstr2ProcessedRecordsReqDto.class);
			Gstr2ProcessedRecordsReqDto setDataSecurity = commonSecparam
					.setGstr2DataSecuritySearchParams(criteria);
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.GETGSTR2A)) {
				workbook = gstr2AConsolidatedReportHandler
						.downloadGet2aReport(setDataSecurity);
				
				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

				LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HHmmss");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);
				fileName = "ConsolidatedGSTR2AReport_" + "_" + date + "" + time;
				//fileName = "ConsolidatedGSTR2AReportDownload";
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
