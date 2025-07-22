package com.ey.advisory.app.services.docs;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.EinvGstinMasterEntity;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GstinValidFinalReportDetails")
public class GstinValidFinalReportDetails {

	@Autowired
	CommonUtility commonUtility;
	
	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier(value = "GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;

	public Pair<String,String> getGstinDetailsReport(List<TaxPayerDetailsDto> gstinsDetails,
			Long requestId, boolean isEinvApplicable) throws IOException {
		Pair<String, String> downloadFileDetails=null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Gstin Validator Details " + "with requestId:'%s'",
					requestId);
			LOGGER.debug(msg);
		}
		String fileName = null;
		String folderName = "GstinValidatorReport";

		if (!isEinvApplicable) {

			String fileTemplate = "GSTINValidator";

			if (gstinsDetails != null && !gstinsDetails.isEmpty()) {
				Workbook workbook = null;
				int startRow = 1;
				int startcolumn = 0;
				boolean isHeaderRequired = false;
				fileName = createExcelFileName(requestId);
				String[] gstinDetailHeaders = commonUtility
						.getProp("gstn.taxpayerdetails.report.header")
						.split(",");
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileTemplate + ".xlsx");
				
			/*	workbook.setFileName(fileName);*/
				
				Cells reportCells = workbook.getWorksheets().get(0).getCells();
				reportCells.importCustomObjects(gstinsDetails,
						gstinDetailHeaders, isHeaderRequired, startRow,
						startcolumn, gstinsDetails.size(), true, "yyyy-mm-dd",
						false);
				
				try {
					/*DocumentUtility.uploadDocumentWithFileName(workbook,
							folderName, fileName);*/
					
					downloadFileDetails = DocumentUtility
							.uploadDocumentAndReturnDocID(workbook, folderName, "XLSX");
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully in and"
										+ "Uploaded in folder name : %s and fileName : "
										+ "%s) ",
								folderName, fileName);
					}
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Exception occured while "
								+ "Uploading into DocRepo,", e);
						LOGGER.error(msg);
					}
				}
			}
		} else {
			String fileTemplate = "EinvoiceApplicability";
			
			GstinValidatorEntity entity = gstinValidRepo
					.findByRequestId(requestId);

			if (gstinsDetails != null && !gstinsDetails.isEmpty()) {
				Workbook workbook = null;
				int startRow = 1;
				int startcolumn = 0;
				boolean isHeaderRequired = false;
				if(entity.getCreatedBy() !=null && entity.getCreatedBy().equalsIgnoreCase("SFTP")){
					fileName = "E-invoiceApplicability" + "_" + requestId;
				}else{
				fileName = "EinvoiceApplicability" + "_" + requestId;
				}
				String[] gstinDetailHeaders = commonUtility
						.getProp(
								"gstn.einv.applicability.taxpayerdetails.report.header")
						.split(",");
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileTemplate + ".xlsx");
				
				
				Cells reportCells = workbook.getWorksheets().get(0).getCells();
				reportCells.importCustomObjects(gstinsDetails,
						gstinDetailHeaders, isHeaderRequired, startRow,
						startcolumn, gstinsDetails.size(), true, "yyyy-mm-dd",
						false);
				try {
					/*workbook.setFileName(fileName);*/
					
					downloadFileDetails = DocumentUtility
							.uploadDocumentAndReturnDocID(workbook, folderName, "XLSX");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully in and"
										+ "Uploaded in folder name : %s and fileName : "
										+ "%s) ",
								folderName, fileName);
					}
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Exception occured while "
								+ "Uploading into DocRepo,", e);
						LOGGER.error(msg);
					}
				}
			}
		}

		return downloadFileDetails;

	}

	private static String createExcelFileName(Long requestId) {
		return "GstinValidator" + "_" + requestId;
	}

}
