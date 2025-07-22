package com.ey.advisory.app.controllers.ims;

import java.sql.Clob;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("ImsSaveErrorReportDownloadServiceImpl")
public class ImsSaveErrorReportDownloadServiceImpl
		implements
			ImsSaveErrorReportDownloadService {
	
	@Autowired
	CommonUtility commonUtility;

	public Workbook convertPayload(Clob clobResponsePayloadResp,
			List<ImsErrorReportResponseDownloadDto> errorReports,
			ImsErrorReportResponseDownloadDto dto1) {
		String clobString = GenUtil
				.convertClobtoString(clobResponsePayloadResp);
		
		if (clobString == null || clobString.isEmpty()) {
		    if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("clobString is null or empty.");
		    }
		}

		JsonObject reqObj = JsonParser.parseString(clobString)
				.getAsJsonObject();
		
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Parsed reqObj: {}", reqObj);
		}

		JsonObject errorReportObj = reqObj.getAsJsonObject("error_report");
		
		if (errorReportObj != null) {
		    for (Map.Entry<String, JsonElement> entry : errorReportObj.entrySet()) {
		        String tableType = entry.getKey().toUpperCase(); // Extracting "B2B", "CN", etc.
		        JsonArray suppliers = entry.getValue().getAsJsonArray(); // Getting supplier array

		        for (JsonElement supplierElement : suppliers) {
		            JsonObject supplierObj = supplierElement.getAsJsonObject();
		            String supplierGstin = supplierObj.get("stin").getAsString();
		            String errorCode = supplierObj.get("error_cd").getAsString();
		            String errorMessage = supplierObj.get("error_msg").getAsString();

		            JsonArray invoices = supplierObj.getAsJsonArray("inv");
		            for (JsonElement invoiceElement : invoices) {
		                JsonObject invoiceObj = invoiceElement.getAsJsonObject();
		                String returnPeriod = invoiceObj.get("rtnprd").getAsString();
		                String documentNumber = invoiceObj.get("inum").getAsString();

		                // **Create a new DTO for each entry**
		                ImsErrorReportResponseDownloadDto dto = new ImsErrorReportResponseDownloadDto();
		                dto.setTableType(tableType);
		                dto.setSupplierGstin(supplierGstin);
		                dto.setReturnPeriod(returnPeriod);
		                dto.setDocumentNumber(documentNumber);
		                dto.setErrorCode(errorCode);
		                dto.setErrorMessage(errorMessage);

		                // **Add DTO to the list**
		                errorReports.add(dto);
		            }
		        }
		    }
		}
	

		if (!errorReports.isEmpty()) {
			Workbook imsSaveErrorReportDownload = ImsSaveErrorReportDownload(errorReports);
			return imsSaveErrorReportDownload;
		}
		return null;
	}

	private Workbook ImsSaveErrorReportDownload(
			List<ImsErrorReportResponseDownloadDto> imsErrorReport) {
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ imsErrorReport.size();
			LOGGER.debug(msg);
		}
		try {
			// if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"ImsSaveErrorResposneDownload.xlsx");

			Worksheet sheet = workbook.getWorksheets().get(0);

			// Style without bold formatting
			Style normalStyle = workbook.createStyle();
			Font normalFont = normalStyle.getFont();
			normalFont.setBold(false);

			for (int i = 0; i < 1; i++) {

				Cells reportCell = workbook.getWorksheets().get(i)
						.getCells();

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(false);
				font.setSize(12);

				String[] invoiceHeaders = null;

				if (imsErrorReport != null && !imsErrorReport.isEmpty()) {
					invoiceHeaders = commonUtility
							.getProp(
									"ims.save.error.report.download.header")
							.split(",");
					reportCell.importCustomObjects(imsErrorReport,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, imsErrorReport.size(), true,
							"yyyy-mm-dd", false);
					if (LOGGER.isDebugEnabled()) {
						String msg = "inside ImsSaveErrorReportDownload class imsSaveErrorReportDownloadServiceImpl ";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully  : %s",
								workbook.getAbsolutePath());
					}
				}

			}

			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

}
