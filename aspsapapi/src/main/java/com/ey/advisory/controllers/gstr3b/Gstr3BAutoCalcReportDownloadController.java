package com.ey.advisory.controllers.gstr3b;

import java.io.Reader;
import java.sql.Clob;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalcReportDownloadDto;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalcReportDownloadReq;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalcReportDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */

@RestController
@Slf4j
public class Gstr3BAutoCalcReportDownloadController {

	@Autowired
	@Qualifier("Gstr3BAutoCalcReportDownloadServiceImpl")
	private Gstr3BAutoCalcReportDownloadService gstr3BAutoCalcDownloadService;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository respRepo;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@RequestMapping(value = "/ui/gstr3bDownloadAutoCalcReport",
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr3bAutoCalcReportDownload(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {

		JsonObject errorResp = new JsonObject();
		Workbook workbook = null;
		int startRow = 4;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		EntityInfoEntity entity = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject jsonReq = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			Gstr3BAutoCalcReportDownloadReq criteria = gson.fromJson(jsonReq,
					Gstr3BAutoCalcReportDownloadReq.class);

			List<String> gstins = criteria.getGstin();

			String taxPeriod = criteria.getTaxPeriod();
			
			Long entityId = criteria.getEntityId();

			if (Strings.isNullOrEmpty(taxPeriod) || gstins.isEmpty()) {
				String msg = "Return Period And Gstin cannot be empty";
				LOGGER.error(msg);
			}

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "Gstr3bAutoCalcReport.xlsx");

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			String[] invoiceHeaders = commonUtility
					.getProp("gstr3b.auto.calc.report.headers").split(",");
			
			Optional<EntityInfoEntity> optional = entityInfoRepo.findById(entityId);
			
			if(optional.isPresent()){
			 entity = optional.get();
			}
			String entityName = entity.getEntityName();

			for (String gstin : gstins) {

				StringBuffer buffer = null;

				String retuenType = "GSTR3B";
				String requestType = "GET";
				GstnUserRequestEntity resp = respRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndRequestType(
								gstin, taxPeriod, retuenType, requestType);

				Clob clobResp = resp != null ? resp.getAutoCalcResponse()
						: null;

				// converting clob to String'

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
				List<Gstr3BAutoCalcReportDownloadDto> gst3BAutoCalcResp = 
					gstr3BAutoCalcDownloadService.getGstrList(respString, gstin);

				if (gst3BAutoCalcResp != null && !gst3BAutoCalcResp.isEmpty()) {

					if (LOGGER.isDebugEnabled()) {
						String msg = "WriteToExcel "
								+ "workbook created writing data to the workbook";
						LOGGER.debug(msg);
					}

					reportCells.importCustomObjects(gst3BAutoCalcResp,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, gst3BAutoCalcResp.size(), true,
							"yyyy-mm-dd", false);
					startRow = startRow + gst3BAutoCalcResp.size() + 2;

				}

			}

			try {
				Worksheet sheet = workbook.getWorksheets().get(0);
				
				Cell cell = sheet.getCells().get("B1");
				Cell cell1 = sheet.getCells().get("E1");
				
				cell.setValue(entityName);
				cell1.setValue(taxPeriod);


				workbook.save(ConfigConstants.GSTR3B_AUTO_CALC_REPORT,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}

			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}
			
			if (workbook != null) {

				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename="
								+ "Gstr3bAutoCalcReport.xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			}
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
