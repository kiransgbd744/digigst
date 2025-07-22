package com.ey.advisory.controller.interest.gstr3b;

import java.io.Reader;
import java.sql.Clob;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalcReportDownloadReq;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.service.interest.gstr3b.GSTR3BInterestDetailsDto;
import com.ey.advisory.service.interest.gstr3b.GSTR3BInterestDto;
import com.ey.advisory.service.interest.gstr3b.GSTR3BInterestReportService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
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
public class GSTR3BInterestReportController {

	@Autowired
	@Qualifier("GSTR3BInterestReportServiceImpl")
	private GSTR3BInterestReportService service;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository respRepo;
	

	@RequestMapping(value = "/ui/gstr3bInterestReportDownload",
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr3bInterestReportDownload(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {

		JsonObject errorResp = new JsonObject();
		Workbook workbook = null;
		int startRow = 4;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject jsonReq = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			Gstr3BAutoCalcReportDownloadReq criteria = gson.fromJson(jsonReq,
					Gstr3BAutoCalcReportDownloadReq.class);

			List<String> gstins = criteria.getGstin();

			String taxPeriod = criteria.getTaxPeriod();
			
			if (Strings.isNullOrEmpty(taxPeriod) || gstins.isEmpty()) {
				String msg = "Return Period And Gstin cannot be empty";
				LOGGER.error(msg);
			}

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GSTR3B_Interest liability details.xlsx");

			Cells reportCell0 = workbook.getWorksheets().get(0).getCells();
			Cells reportCell1 = workbook.getWorksheets().get(1).getCells();

			String[] summaryHeaders = commonUtility
					.getProp("gstr3b.interest.summary.report.headers").split(",");
			
			String[] detailsHeaders = commonUtility
					.getProp("gstr3b.interest.details.report.headers").split(",");
			
			
			for (String gstin : gstins) {

				StringBuffer buffer = null;

				String retuenType = "GSTR3B";
				String requestType = "GET";
				GstnUserRequestEntity resp = respRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndRequestType(
								gstin, taxPeriod, retuenType, requestType);

				Clob clobResp = resp != null ? resp.getIntrtAutoCalcResponse()
						: null;
				if (clobResp != null) {

					// converting clob to String

					if (clobResp != null) {
						Reader respReder = clobResp.getCharacterStream();

						buffer = new StringBuffer();
						int ch = 0;
						while ((ch = respReder.read()) != -1) {
							buffer.append("" + (char) ch);
						}
						respReder.close();
					}
					String respString = buffer != null ? buffer.toString() : null;

					List<GSTR3BInterestDto> gstr3bInterestSummaryReportData = service
							.getGstr3BInterestSummaryReportData(gstin, respString);

					if (gstr3bInterestSummaryReportData != null && !gstr3bInterestSummaryReportData.isEmpty()) {

						if (LOGGER.isDebugEnabled()) {
							String msg = "WriteToExcel " + "workbook created writing data to the summary";
							LOGGER.debug(msg);
						}
						startRow = 4;
						reportCell0.importCustomObjects(gstr3bInterestSummaryReportData, summaryHeaders,
								isHeaderRequired, startRow, startcolumn, gstr3bInterestSummaryReportData.size(), true,
								"yyyy-mm-dd", false);
						startRow = startRow + gstr3bInterestSummaryReportData.size() + 2;

					}

					List<GSTR3BInterestDetailsDto> gstr3bInterestDetailsReportData = service
							.getGstr3BInterestDetailsReportData(gstin, respString);

					if (gstr3bInterestDetailsReportData != null && !gstr3bInterestDetailsReportData.isEmpty()) {

						if (LOGGER.isDebugEnabled()) {
							String msg = "WriteToExcel " + "workbook created writing data to the Details";
							LOGGER.debug(msg);
						}
						startRow = 3;

						reportCell1.importCustomObjects(gstr3bInterestDetailsReportData, detailsHeaders,
								isHeaderRequired, startRow, startcolumn, gstr3bInterestDetailsReportData.size(), true,
								"yyyy-mm-dd", false);
						startRow = startRow + gstr3bInterestDetailsReportData.size() + 2;

					}

				}
			}
			
			String fileName = DocumentUtility.getUniqueFileName
					("GSTR3B_Interest liability details.xlsx");
			
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename="
								+ fileName));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

		} catch (Exception ex) {
			String msg = "Error occured while generating Auto calc report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}

}
