package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */

@Service("Gstr2InitiateReconReportDownloadServiceImpl")
@Slf4j
public class Gstr2InitiateReconReportDownloadServiceImpl
		implements Gstr2InitiateReconReportDownloadService {

	@Autowired
	Gstr2InitiateReconService gstr2InitiateReconService;

	@Autowired
	CommonUtility commonUtility;

	@Override
	public Workbook find(Gstr2InitiateReconDto req) {

		Workbook workbook = null;

		String fromTaxPeriod2A = req.getFromTaxPeriod2A() != null
				? req.getFromTaxPeriod2A().toString()
				: req.getFromDocDate().toString();

		String toTaxPeriod2A = req.getToTaxPeriod2A() != null
				? req.getToTaxPeriod2A().toString()
				: req.getToDocDate().toString();
		String fromTaxPeriodPR = req.getFromTaxPeriodPR() != null
				? req.getFromTaxPeriodPR().toString()
				: req.getFromDocDate().toString();

		String toTaxPeriodPR = req.getToTaxPeriodPR() != null
				? req.getToTaxPeriodPR().toString()
				: req.getToDocDate().toString();
				
		String reconType = req.getReconType();//2APR or 2BPR		

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {

					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		List<Gstr2InitiateReconLineItemDto> response = gstr2InitiateReconService
				.find(req);

		/*
		if (isIndexOutOfBounds(response, 0)) {

			if (response.get(0).getSection().contains("B2B")) {
				//response.get(0).getSection().replace("Inv", "B2B");
				response.get(0).setSection("B2B");

			}
		}
		if (isIndexOutOfBounds(response, 1)) {
			if (response.get(1).getSection().contains("B2B Amendment")) {
				//response.get(1).getSection().replace("InvA", "B2B Amendment");
				response.get(1).setSection("B2B Amendment");
			}
		}
		if (isIndexOutOfBounds(response, 4)) {
			if (response.get(4).getSection().contains("DR Note Amendment")) {
				//response.get(4).getSection().replace("DRA Note",
					//	"DR Note Amendment");
				response.get(4).setSection("DR Note Amendment");
			}
		}
		if (isIndexOutOfBounds(response, 5)) {
			if (response.get(5).getSection().contains("CR Note Amendment")) {
				//response.get(5).getSection().replace("CRA Note",
					//	"CR Note Amendment");
				response.get(5).setSection("CR Note Amendment");
			}
		}
*/		
		String yearF2A = fromTaxPeriod2A.substring(0, 4);
		String monthF2A = fromTaxPeriod2A.substring(4, 6);
		String fromTaxP2A = monthF2A + yearF2A;

		String yearT2A = toTaxPeriod2A.substring(0, 4);
		String monthT2A = toTaxPeriod2A.substring(4, 6);
		String toTaxP2A = monthT2A + yearT2A;

		String yearFPR = fromTaxPeriodPR.substring(0, 4);
		String monthFPR = fromTaxPeriodPR.substring(4, 6);
		String fromTaxPPR = monthFPR + yearFPR;

		String yearTPR = toTaxPeriodPR.substring(0, 4);
		String monthTPR = toTaxPeriodPR.substring(4, 6);
		String toTaxPPR = monthTPR + yearTPR;

		workbook = writeToExcel(response, gstinList, fromTaxP2A, toTaxP2A,
				fromTaxPPR, toTaxPPR, reconType);

		return workbook;

	}

	private Workbook writeToExcel(
			List<Gstr2InitiateReconLineItemDto> gstr2InitiateRecon,
			List<String> gstinList, String fromTaxPeriod2A,
			String toTaxPeriod2A, String fromTaxPeriodPR,
			String toTaxPeriodPR, String reconType) {
		// TODO Auto-generated method stub

		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (gstr2InitiateRecon != null && !gstr2InitiateRecon.isEmpty()) {
				
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.initiate.recon.summary.report.header")
					.split(",");

			if(reconType != null && reconType.equalsIgnoreCase("2BPR")) {
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "Gstr2InitiateRecon_2BPR.xlsx");
			}else if (reconType != null && reconType.equalsIgnoreCase("EINVPR")){
			
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "InwardEinvoiceInitiateRecon.xlsx");
			}else
			{	workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "Gstr2InitiateRecon.xlsx");
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "GenerateErrorReportImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}
			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			Cell cell1 = sheet.getCells().get("B1");
			Cell cell2 = sheet.getCells().get("B2");
			Cell cell3 = sheet.getCells().get("B3");
			Cell cell4 = sheet.getCells().get("E2");
			Cell cell5 = sheet.getCells().get("E3");

			cell1.setValue(gstinList);
			cell2.setValue(fromTaxPeriodPR);
			cell3.setValue(toTaxPeriodPR);
			cell4.setValue(fromTaxPeriod2A);
			cell5.setValue(toTaxPeriod2A);

			reportCells.importCustomObjects(gstr2InitiateRecon, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					gstr2InitiateRecon.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "GenerateErrorReportImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.GSTR2A_INITIATE_RECON_REPORT,
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

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	/*private static boolean isIndexOutOfBounds(
			final List<Gstr2InitiateReconLineItemDto> response, int index) {
		return index < 0 || index > response.size();
	}*/

}
