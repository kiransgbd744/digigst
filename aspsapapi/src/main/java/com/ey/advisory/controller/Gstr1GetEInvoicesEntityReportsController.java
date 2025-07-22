package com.ey.advisory.controller;

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
import com.ey.advisory.app.docs.dto.gstr1.Gstr1EInvReportsReqDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.Gstr1GetEInvoicesEntityReportHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Anand3.M
 *
 */
@RestController
public class Gstr1GetEInvoicesEntityReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GetEInvoicesEntityReportsController.class);

	@Autowired
	@Qualifier("Gstr1GetEInvoicesEntityReportHandler")
	private Gstr1GetEInvoicesEntityReportHandler gstr1GetEInvoicesEntityReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@RequestMapping(value = "/ui/gstr1EntityReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void Gstr1EntityReport(@RequestBody String jsonString,
			HttpServletResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		/*
		 * JsonArray jsonReqArray = (new JsonParser()).parse(jsonString)
		 * .getAsJsonObject().get("req").getAsJsonArray();
		 */
		// @SuppressWarnings("serial")
		// Type listType = new TypeToken<List<Gstr1EInvReportsReqDto>>() {
		// }.getType();

		Gstr1EInvReportsReqDto reqDtoCriterias = gson.fromJson(json,
				Gstr1EInvReportsReqDto.class);

		try {
			String fileName = null;
			Workbook workbook = null;

			workbook = gstr1GetEInvoicesEntityReportHandler
					.downloadEntityReport(reqDtoCriterias);

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("ddMMyyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HHmmss");

			String date = FOMATTER.format(istDateTimeFromUTC);
			String time = FOMATTER1.format(istDateTimeFromUTC);

			fileName = "EInvoices_Entity_Level_Summary_Report" + "_" + date + ""
					+ time;

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
			String msg = "Unexpected error while retrieving "
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
}
