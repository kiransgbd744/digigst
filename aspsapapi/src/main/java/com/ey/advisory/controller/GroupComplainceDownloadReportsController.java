/**
 * 
 */
package com.ey.advisory.controller;

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
import com.ey.advisory.app.data.returns.compliance.service.GroupComplainceReportHandler;
import com.ey.advisory.app.data.returns.compliance.service.GroupComplianceHistoryDataRecordsReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
public class GroupComplainceDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupComplainceDownloadReportsController.class);

	@Autowired
	@Qualifier("GroupComplainceReportHandler")
	private GroupComplainceReportHandler groupComplainceReportHandler;

	@RequestMapping(value = "/ui/groupComplainceReportDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			String fileName = null;
			Workbook workbook = null;
			GroupComplianceHistoryDataRecordsReqDto criteria = gson.fromJson(
					json, GroupComplianceHistoryDataRecordsReqDto.class);

			workbook = groupComplainceReportHandler
					.downloadComplainceReport(criteria);

			fileName = "ComplianceHistory" + "_" + criteria.getReturnType()
					+ "_" + criteria.getFinancialYear() + "_Transaction";

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
