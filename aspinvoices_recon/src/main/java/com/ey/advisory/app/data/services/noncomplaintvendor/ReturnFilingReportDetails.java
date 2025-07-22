package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.io.IOException;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.GstinValidatorAndReturnFilingReportDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ReturnFilingReportDetails")
public class ReturnFilingReportDetails {

	@Autowired
	CommonUtility commonUtility;

	public Pair<String,String> generateAndUploadReturnFillingFile(
			List<GstinValidatorAndReturnFilingReportDto> gstinsDetails, Long requestId)
			throws IOException {
		Workbook workbook = null;
		Pair<String, String> downloadFileDetails=null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to Generate Return Filling File, requestId:'%s'",
					requestId);
			LOGGER.debug(msg);
		}
		String fileName = null;
		String folderName = "ReturnFilingReport";
		String fileTemplate = "ReturnFilingReport";
		if (gstinsDetails != null && !gstinsDetails.isEmpty()) {
			int startRow = 1;
			int startcolumn = 0;
			boolean isHeaderRequired = false;
			fileName = getReturnFillingFileName(requestId);
			String[] gstinDetailHeaders = commonUtility
					.getProp("gstn.returnfiling.report.header").split(",");
			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", fileTemplate + ".xlsx");
			Cells reportCells = workbook.getWorksheets().get(0).getCells();
			reportCells.importCustomObjects(gstinsDetails, gstinDetailHeaders,
					isHeaderRequired, startRow, startcolumn,
					gstinsDetails.size(), true, "yyyy-mm-dd", false);
			try {
				/*DocumentUtility.uploadDocumentWithFileName(workbook, folderName,
						fileName);
				*/
				downloadFileDetails = DocumentUtility
						.uploadDocumentAndReturnDocID(workbook, folderName, "XLSX");
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Return Filling Report has been generated successfully in and"
									+ "Uploaded in folder name : %s and fileName : "
									+ "%s) ",
							folderName, fileName);
				}
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Exception occured while "
									+ "Uploading Return Filling Report into DocRepo, %s ",
							e.getMessage());
					LOGGER.debug(msg);
				}
			}
		}

		return downloadFileDetails;

	}

	private String getReturnFillingFileName(Long requestId) {
		return String.format("ReturnFillingReport_%d.xlsx", requestId);
	}

}
