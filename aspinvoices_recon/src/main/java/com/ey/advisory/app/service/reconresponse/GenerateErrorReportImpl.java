/**
 * 
 */
package com.ey.advisory.app.service.reconresponse;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("GenerateErrorReportImpl")
public class GenerateErrorReportImpl implements GenerateErrorReport {

	@Autowired
	@Qualifier("ReconResponseDaoImpl")
	private ReconResponseDao reconResponseDao;
	
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepo;

	@Autowired
	CommonUtility commonUtility;

	public void uploadErrorReport(Long configId, Long fileId) {
		if(LOGGER.isDebugEnabled()){
			String msg = "Begin GenerateErrorReportImpl.uploadErrorReport ";
			LOGGER.debug(msg);
		}
		List<ErrorResponseDto> responseList = reconResponseDao
				.getErrorRecords(fileId);
		
		String fileName = getUniqueFileName(ConfigConstants.ERROR_UPLOAD_FILE_NAME);
		if(LOGGER.isDebugEnabled()){
			String msg = "unique file name generated  " + fileName;
			LOGGER.debug(msg);
		}
		DocumentUtility.uploadDocumentWithFileName(writeToExcel(responseList),
				ConfigConstants.ERROR_UPLOAD_FOLDER_NAME,
				fileName);
		if(LOGGER.isDebugEnabled()){
			String msg = "GenerateErrorReportImpl.uploadErrorReport Document"
					+ " uploaded. updating filestatus table with corresponding"
					+ " filename";
			LOGGER.debug(msg);
		}
		fileStatusRepo.updateErrorFieNameById(fileId, fileName);
		if(LOGGER.isDebugEnabled()){
			String msg = " End GenerateErrorReportImpl.uploadErrorReport";
			LOGGER.debug(msg);
		}
		
	}

	private Workbook writeToExcel(List<ErrorResponseDto> errorResponseList) {
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if(LOGGER.isDebugEnabled()){
			String msg = "Begin GenerateErrorReportImpl.writeToExcel "
					+ "errorList Size = " + errorResponseList.size();
			LOGGER.debug(msg);
		}

		if (errorResponseList != null && !errorResponseList.isEmpty()) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx2.initiate.recon.report.error.header").split(",");
			if(LOGGER.isDebugEnabled()){
				String msg = "GenerateErrorReportImpl.writeToExcel "
						+ "creating workbook ";
				LOGGER.debug(msg);
			}
			workbook = commonUtility.createWorkbookWithCSVTemplate(
					"ReportTemplates", "errorResponseFile" + ".csv");
			if(LOGGER.isDebugEnabled()){
				String msg = "GenerateErrorReportImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}
			Cells reportCells = workbook.getWorksheets().get(0).getCells();
			reportCells.importCustomObjects(errorResponseList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					errorResponseList.size(), true, "yyyy-mm-dd", false);
			try {
				if(LOGGER.isDebugEnabled()){
					String msg = "GenerateErrorReportImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.ERROR_UPLOAD_FILE_NAME,SaveFormat.CSV);
				if(LOGGER.isDebugEnabled()){
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
	
	
	public static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		String[] names = fileName.split("\\.");

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();

		String timeMilli = dtf.format(now);
		// Normalize file name
		String fileNameWithTimeStamp = names[0] + "_" + timeMilli + "."
				+ names[1];

		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created";
			LOGGER.debug(msg);
		}

		return fileNameWithTimeStamp;

	}

}