package com.ey.advisory.app.service.ims.supplier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DetailedSupplierImsReportServiceImpl")
public class DetailedSupplierImsReportServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	CommonUtility commonUtility;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void generateReports(Long id) {

		Pair<String, String> downloadFileDetails = null;
		String folderName = ConfigConstants.ASYNC_IMS_REPORT;
		Workbook workbook = null;
		String docId = null;
		String uploadedDocName = null;

		String fileName = null;
		File tempDir = null;

		try {

			workbook = findGstr1ReviewSummRecords(null, null,id);

			if (workbook != null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Detailed Supplier Ims Summary template file name is : {}",
							workbook.getFileName());
				}

				tempDir = createTempDir();
				
				/*DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String timeMilli = dtf.format(LocalDateTime.now());*/

				fileName = tempDir.getAbsolutePath() + File.separator
						+ "Supplier IMS_DetailedSummary" + ".xlsx";

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Detailed Supplier Ims Summary file name is : {}", fileName);
				}
				

				workbook.save(fileName, SaveFormat.XLSX);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " Detailed Supplier Ims Summary"
									+ " response list in the directory : {}",
							workbook.getAbsolutePath());
				}

				if (id != null) {
					try {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"DetailedSupplierImsReportServiceImpl - "
											+ "Going to upload Document "
											+ "in folder name : %s) ",
									folderName);

						}

						downloadFileDetails = DocumentUtility
								.uploadDocumentAndReturnDocID(workbook,
										folderName, "XLSX");

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"DetailedSupplierImsReportServiceImpl - "
											+ "Workbook has been generated successfully in and"
											+ "Uploaded in folder name : %s ",
									folderName);

						}

						uploadedDocName = downloadFileDetails.getValue0();
						docId = downloadFileDetails.getValue1();

						fileStatusDownloadReportRepo.updateStatus(id,
								ReportStatusConstants.REPORT_GENERATED,
								uploadedDocName, LocalDateTime.now(), docId);
						if (LOGGER.isDebugEnabled()) {
							String msg = "Sucessfully uploaded  file and updating the "
									+ "status as 'Report Generated'";
							LOGGER.debug(msg);
						}

					} catch (Exception e) {

						String msg = String
								.format("DetailedSupplierImsReportServiceImpl - "
										+ "Exception occured while "
										+ "Uploading into DocRepo, %s", e);
						LOGGER.error(msg);
						fileStatusDownloadReportRepo.updateStatus(id,
								ReportStatusConstants.REPORT_GENERATION_FAILED,
								null, LocalDateTime.now(), null);
						throw new AppException(msg);
					}
				}
			} else {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Detailed SupplierImsReport No Data found for report id : {} ",
							id);
				}

				if (id != null) {

					fileStatusDownloadReportRepo.updateStatus(id,
							ReportStatusConstants.NO_DATA_FOUND, null,
							LocalDateTime.now(), null);
				}

			}
		} catch (Exception e) {

			String msg = String.format("DetailedSupplierImsReportServiceImpl - "
					+ "Exception occured while "
					+ "invoking report service. %s", e);
			LOGGER.error(msg);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), null);
			throw new AppException(msg);
		} finally {
			if (tempDir != null)
				GenUtil.deleteTempDir(tempDir);
		}

	}
	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}
	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
			PageRequest pageReq,Long id) {
		//Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = null;
		//Workbook workbook = new Workbook();

		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		//List<Object> responseFromView = new ArrayList<>();
		List<Object[]> list = null;

		/*responseFromView = gstr1ReviewSummaryReportsDao
				.getGstr1RSReports(request);*/
		
		StoredProcedureQuery storedProcSummaryReport = entityManager
				.createStoredProcedureQuery(
						"USP_SUPP_IMS_DETAILED_DISP_RPT");

		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_REPORT_DOWNLOAD_ID", id);
		
		
		
		/*StoredProcedureQuery reportDataProc = entityManager
				.createNamedStoredProcedureQuery("USP_SUPP_IMS_DETAILED_DISP_RPT");
		
		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_REPORT_DOWNLOAD_ID", id);

		reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);*/


		list = storedProcSummaryReport.getResultList();
		
		List<DetailedSupplierImsSummaryReportDto> dataList = list
				.stream().map(o -> convert(o))
				.collect(Collectors
						.toCollection(ArrayList::new));

		LOGGER.debug("Detailed Supplier Ims Summary Data" + dataList);
		

		if (dataList != null && !dataList.isEmpty()) {
			 workbook = new Workbook();

			String[] invoiceHeaders = commonUtility
					.getProp("detailed.summary.supplier.ims.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"Supplier_Ims_Download_Detailed_Summary.xlsx");
			Worksheet worksheet = workbook.getWorksheets().get(0);
			 Cells errorDumpCells = worksheet.getCells();
					//.getCells();
			errorDumpCells.importCustomObjects(dataList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					dataList.size(), true, "yyyy-mm-dd", false);
			//to delete last row of the report.
			int lastRowIndex = worksheet.getCells().getMaxDataRow();
			worksheet.getCells().deleteRow(lastRowIndex + 1);
        
		}
				return workbook;
	}
	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}
	private DetailedSupplierImsSummaryReportDto convert(Object[] arr) {

		DetailedSupplierImsSummaryReportDto obj = new DetailedSupplierImsSummaryReportDto();
		
       
		obj.setGstin((arr[0] != null) ? arr[0].toString() : null);
		obj.setReturnPeriod((arr[1] != null) ? arr[1].toString() : null);
		obj.setTableType((arr[2] != null) ? arr[2].toString() : null);
		
		obj.setTotalRecordsCount((arr[3] != null) ? arr[3].toString() : null);
		obj.setTotalRecordsTotalTaxableVal((arr[4] != null) ? arr[4].toString() : null);
		obj.setTotalRecordsTotalTax((arr[5] != null) ? arr[5].toString() : null);
		obj.setTotalRecordsIgst((arr[6] != null) ? arr[6].toString() : null);
		obj.setTotalRecordsCgst((arr[7] != null) ? arr[7].toString() : null);
		obj.setTotalRecordsSgst((arr[8] != null) ? arr[8].toString() : null);
		obj.setTotalRecordsCess((arr[9] != null) ? arr[9].toString() : null);
		
		obj.setAcceptedCount((arr[10] != null) ? arr[10].toString() : null);
		obj.setAcceptedTotalTaxableVal((arr[11] != null) ? arr[11].toString() : null);
		obj.setAcceptedTotalTax((arr[12] != null) ? arr[12].toString() : null);
		obj.setAcceptedIgst((arr[13] != null) ? arr[13].toString() : null);
		obj.setAcceptedCgst((arr[14] != null) ? arr[14].toString() : null);
		obj.setAcceptedSgst((arr[15] != null) ? arr[15].toString() : null);
		obj.setAcceptedCess((arr[16] != null) ? arr[16].toString() : null);
		
		obj.setPendingCount((arr[17] != null) ? arr[17].toString() : null);
		obj.setPendingTotalTaxableVal((arr[18] != null) ? arr[18].toString() : null);
		obj.setPendingTotalTax((arr[19] != null) ? arr[19].toString() : null);
		obj.setPendingIgst((arr[20] != null) ? arr[20].toString() : null);
		obj.setPendingCgst((arr[21] != null) ? arr[21].toString() : null);
		obj.setPendingSgst((arr[22] != null) ? arr[22].toString() : null);
		obj.setPendingCess((arr[23] != null) ? arr[23].toString() : null);
		
		obj.setRejectedCount((arr[24] != null) ? arr[24].toString() : null);
		obj.setRejectedTotalTaxableVal((arr[25] != null) ? arr[25].toString() : null);
		obj.setRejectedTotalTax((arr[26] != null) ? arr[26].toString() : null);
		obj.setRejectedIgst((arr[27] != null) ? arr[27].toString() : null);
		obj.setRejectedCgst((arr[28] != null) ? arr[28].toString() : null);
		obj.setRejectedSgst((arr[29] != null) ? arr[29].toString() : null);
		obj.setRejectedCess((arr[30] != null) ? arr[30].toString() : null);
		
		obj.setNoActionCount((arr[31] != null) ? arr[31].toString() : null);
		obj.setNoActionTotalTaxableVal((arr[32] != null) ? arr[32].toString() : null);
		obj.setNoActionTotalTax((arr[33] != null) ? arr[33].toString() : null);
		obj.setNoActionIgst((arr[34] != null) ? arr[34].toString() : null);
		obj.setNoActionCgst((arr[35] != null) ? arr[35].toString() : null);
		obj.setNoActionSgst((arr[36] != null) ? arr[36].toString() : null);
		obj.setNoActionCess((arr[37] != null) ? arr[37].toString() : null);
		
		obj.setGstr1Count((arr[38] != null) ? arr[38].toString() : null);
		obj.setGstr1TotalTaxableVal((arr[39] != null) ? arr[39].toString() : null);
		obj.setGstr1TotalTax((arr[40] != null) ? arr[40].toString() : null);
		obj.setGstr1Igst((arr[41] != null) ? arr[41].toString() : null);
		obj.setGstr1Cgst((arr[42] != null) ? arr[42].toString() : null);
		obj.setGstr1Sgst((arr[43] != null) ? arr[43].toString() : null);
		obj.setGstr1Cess((arr[44] != null) ? arr[44].toString() : null);
		
		obj.setGstr1aCount((arr[45] != null) ? arr[45].toString() : null);
		obj.setGstr1aTotalTaxableVal((arr[46] != null) ? arr[46].toString() : null);
		obj.setGstr1aTotalTax((arr[47] != null) ? arr[47].toString() : null);
		obj.setGstr1aIgst((arr[48] != null) ? arr[48].toString() : null);
		obj.setGstr1aCgst((arr[49] != null) ? arr[49].toString() : null);
		obj.setGstr1aSgst((arr[50] != null) ? arr[50].toString() : null);
		obj.setGstr1aCess((arr[51] != null) ? arr[51].toString() : null);
		
		obj.setDiffCount((arr[52] != null) ? arr[52].toString() : null);
		obj.setDiffTotalTaxableVal((arr[53] != null) ? arr[53].toString() : null);
		obj.setDiffTotalTax((arr[54] != null) ? arr[54].toString() : null);
		obj.setDiffIgst((arr[55] != null) ? arr[55].toString() : null);
		obj.setDiffCgst((arr[56] != null) ? arr[56].toString() : null);
		obj.setDiffSgst((arr[57] != null) ? arr[57].toString() : null);
		obj.setDiffCess((arr[58] != null) ? arr[58].toString() : null);

		return obj;
	}
	
}
