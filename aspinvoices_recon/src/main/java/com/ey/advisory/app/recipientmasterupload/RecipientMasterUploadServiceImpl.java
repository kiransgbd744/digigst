package com.ey.advisory.app.recipientmasterupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.asprecon.RecipientMasterErrorReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */

@Slf4j
@Component("RecipientMasterUploadServiceImpl")
public class RecipientMasterUploadServiceImpl
		implements RecipientMasterUploadService {

	@Autowired
	private RecipientMasterErrorReportRepository recipientMasterErrorRepo;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public Workbook getRecipientMasterErrorReport(Long fileId,
			String typeOfFlag) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Requested %s Report for fileId %s:: ",
					typeOfFlag, fileId);
			LOGGER.debug(msg);
		}

		List<RecipientMasterErrorReportEntity> errorReportEntities = recipientMasterErrorRepo
				.findByFileId(fileId);

		if (CollectionUtils.isEmpty(errorReportEntities)) {
			throw new AppException(String.format(
					"Error or Information record for the given fileId %s is not found",
					fileId));
		}
		List<RecipientMasterErrorReportEntity> errorRecords = errorReportEntities
				.stream()
				.filter(eachObject -> Objects.nonNull(eachObject.getError())
						&& !Objects.equals("null", eachObject.getError()))
				.collect(Collectors.toList());
		List<RecipientMasterErrorReportEntity> informatonRecords = errorReportEntities
				.stream()
				.filter(eachObject -> Objects
						.nonNull(eachObject.getInformation())
						&& !Objects.equals("null", eachObject.getInformation()))
				.collect(Collectors.toList());

		if (typeOfFlag.equalsIgnoreCase("informationrecords")) {
			if (!CollectionUtils.isEmpty(informatonRecords)
					&& informatonRecords != null
					&& !informatonRecords.equals("null")
					&& !informatonRecords.contains("null")) {
				List<RecipientMasterErrorReportEntity> infoRecordsList = new ArrayList<>();

				informatonRecords.forEach(e -> {
					RecipientMasterErrorReportEntity info = new RecipientMasterErrorReportEntity();

					info.setInformation(e.getInformation());
					info.setRecipientPAN(e.getRecipientPAN());
					info.setRecipientGstin(e.getRecipientGstin());
					info.setRecipientPrimEmailId(e.getRecipientPrimEmailId());
					info.setRecipientEmailId2(e.getRecipientEmailId2());
					info.setRecipientEmailId3(e.getRecipientEmailId3());
					info.setRecipientEmailId4(e.getRecipientEmailId4());
					info.setRecipientEmailId5(e.getRecipientEmailId5());
					info.setCceEmailId1(e.getCceEmailId1());
					info.setCceEmailId2(e.getCceEmailId2());
					info.setCceEmailId3(e.getCceEmailId3());
					info.setCceEmailId4(e.getCceEmailId4());
					info.setCceEmailId5(e.getCceEmailId5());
					info.setIsGetGstr2AEmail(e.getIsGetGstr2AEmail());
					info.setIsGetGstr2BEmail(e.getIsGetGstr2BEmail());
					info.setIsRetCompStatusEmail(e.getIsRetCompStatusEmail());
					info.setIsDRC01BEmail(e.getIsDRC01BEmail());
					info.setIsDRC01CEmail(e.getIsDRC01CEmail());
					info.setIsDelete(e.getIsDelete());
					infoRecordsList.add(info);
				});
				return writeToExcel(infoRecordsList, "information");
			} else {
				String msg = String.format("Information Record for the given"
						+ "fileId %s is not found", fileId);
				throw new AppException(msg);
			}
		} else if (typeOfFlag.equalsIgnoreCase("errorrecords")) {
			if (!CollectionUtils.isEmpty(errorRecords) && errorRecords != null
					&& !errorRecords.equals("null")
					&& !errorRecords.contains("null")) {
				List<RecipientMasterErrorReportEntity> errorRecordsList = new ArrayList<>();

				errorRecords.forEach(e -> {
					RecipientMasterErrorReportEntity error = new RecipientMasterErrorReportEntity();

					error.setError(e.getError());
					error.setRecipientPAN(e.getRecipientPAN());
					error.setRecipientGstin(e.getRecipientGstin());
					error.setRecipientPrimEmailId(e.getRecipientPrimEmailId());
					error.setRecipientEmailId2(e.getRecipientEmailId2());
					error.setRecipientEmailId3(e.getRecipientEmailId3());
					error.setRecipientEmailId4(e.getRecipientEmailId4());
					error.setRecipientEmailId5(e.getRecipientEmailId5());
					error.setCceEmailId1(e.getCceEmailId1());
					error.setCceEmailId2(e.getCceEmailId2());
					error.setCceEmailId3(e.getCceEmailId3());
					error.setCceEmailId4(e.getCceEmailId4());
					error.setCceEmailId5(e.getCceEmailId5());
					error.setIsGetGstr2AEmail(e.getIsGetGstr2AEmail());
					error.setIsGetGstr2BEmail(e.getIsGetGstr2BEmail());
					error.setIsRetCompStatusEmail(e.getIsRetCompStatusEmail());
					error.setIsDRC01BEmail(e.getIsDRC01BEmail());
					error.setIsDRC01CEmail(e.getIsDRC01CEmail());
					error.setIsDelete(e.getIsDelete());
					errorRecordsList.add(error);
				});
				return writeToExcel(errorRecordsList, "error");
			} else {
				String msg = String.format(
						"Error Record for the given fileId %s is not found",
						fileId);
				throw new AppException(msg);
			}
		} else {
			throw new AppException(
					"Please provide proper value for type of record");
		}
	}

	private Workbook writeToExcel(
			List<RecipientMasterErrorReportEntity> recipientErrEntities,
			String typeOfFlag) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (recipientErrEntities != null && !recipientErrEntities.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("recipient.master." + typeOfFlag + ".data")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"RecipientMaster" + typeOfFlag + "Report.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "VendorMasterReportServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(recipientErrEntities,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					recipientErrEntities.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "RecipientMasterUploadServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.RECIPIENTDATAUPLOAD,
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
}
