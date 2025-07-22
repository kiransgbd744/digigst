package com.ey.advisory.app.services.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.EwbTotalRecordsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportStatusConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EwbAsyncReportServiceImpl")
public class EwbAsyncReportServiceImpl implements EwbAsyncReportService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipTxtFiles combinAndZipReportFiles;

	private static final String ERR_COLUMNS = "ewb.download.error.report.columns";

	private static final String PSD_COLUMNS = "ewb.download.psd.report.columns";

	@Override
	public void generateReports(Long id, String reportType) {

		File tempDir = null;
		String filename = null;
		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			String reportCateg = "EWB";

			Integer chunkCount = getChunkCount(reportCateg, id);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking ChuckCount StoreProc and got response as : %d",
						chunkCount);
				LOGGER.debug(msg);
			}

			if (chunkCount == 0) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return;
			}

			Workbook workbook = writetoXlsx(chunkCount, id, reportType);

			/*
			 * String fullPath = tempDir.getAbsolutePath() + File.separator +
			 * workbook + ".xlsx";
			 */

			if (reportType.equalsIgnoreCase("Error Reports")) {
				filename = getUniqueFileName("EWB-NIC-Error-Report");
				} else if (reportType.equalsIgnoreCase("Total Reports")) {
				filename = getUniqueFileName("EWB-NIC-Total-Report");
			} else {
				filename = getUniqueFileName("EWB-NIC-Processed-Report");
			}

			try {
				workbook.save(tempDir.getAbsolutePath() + File.separator
						+ filename + ".xlsx", SaveFormat.XLSX);
				LOGGER.debug("tempDir {} ", tempDir);
				LOGGER.debug("tempDir.getAbsolutePath {} ",
						tempDir.getAbsolutePath());
				LOGGER.debug("TempDir full path {}: ",
						tempDir.getAbsolutePath() + "\\" + filename + ".xlsx");
				LOGGER.debug("tempDir.list().length {} ",
						tempDir.list().length);

			} catch (Exception e) {
				e.printStackTrace();
			}

			String zipFileName = "";
			 if (tempDir.list().length > 0) {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Creating Zip folder";
				LOGGER.debug(msg);
			}

			zipFileName = combinAndZipReportFiles.zipfolder(id, tempDir,
					reportType, "", "");

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("ZipFile name : %s", zipFileName);
				LOGGER.debug(msg);
			}

			File zipFile = new File(tempDir, zipFileName);

			Pair<String, String> uploadedZipName = DocumentUtility
					.uploadFile(zipFile, "Anx1FileStatusReport");
			String uploadedDocName = uploadedZipName.getValue0();
			String docId = uploadedZipName.getValue1();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded zip file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					docId);
			 } else {
			
			 LOGGER.error("No Data found for report id : {}", id);
			  fileStatusDownloadReportRepo.updateStatus(id,
			  ReportStatusConstants.NO_DATA_FOUND, null, LocalDateTime.now());
			 }
			 

		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());
			String msg = "Exception occued while generating EWB Excel file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {

			if (tempDir != null && tempDir.exists()) {
				try {
					FileUtils.deleteDirectory(tempDir);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								tempDir.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("EwbReports").toFile();
	}

	private Integer getChunkCount(String reportCateg, Long id) {

		String procName = "ewbChunkCount";
		Integer chunkSplit = 10000;
		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);
		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);
		counterProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSplit);
		Integer response = (Integer) counterProc.getSingleResult();
		return response;
	}

	@SuppressWarnings("unchecked")
	private Workbook writetoXlsx(Integer chunkNo, Long id, String reportType) {

		List<Object[]> list = null;
		List<EwbTotalRecordsDto> responseFromDao = null;
		String procName = "ewbChunkData";
		Workbook workbook = null;
		int rowCont = 1;
		int columnCnt = 0;
		Cells reportCell0 = null;
		String[] headerColumns = null;

		try {

			if (reportType.equalsIgnoreCase("Error Reports")) {

				workbook = commonUtility
						.createWorkbookWithExcelTemplate(
								"ReportTemplates",
								"Ewb_Error_Report.xlsx");

				 reportCell0 = workbook.getWorksheets().get(0)
						.getCells();

				 headerColumns = commonUtility
						.getProp(ERR_COLUMNS).split(",");
				
			}

			if (reportType.equalsIgnoreCase("Processed Reports")) {

				workbook = commonUtility
						.createWorkbookWithExcelTemplate(
								"ReportTemplates",
								"Ewb_Processed_Report.xlsx");

				 reportCell0 = workbook.getWorksheets().get(0)
						.getCells();

				 headerColumns = commonUtility
						.getProp(PSD_COLUMNS).split(",");


			}

			if (reportType.equalsIgnoreCase("Total Reports")) {

				workbook = commonUtility
						.createWorkbookWithExcelTemplate(
								"ReportTemplates",
								"Ewb_Total_Report.xlsx");

				 reportCell0 = workbook.getWorksheets().get(0)
						.getCells();

				 headerColumns = commonUtility
						.getProp(ERR_COLUMNS).split(",");

			}

			
			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Inside for loop with count: %d ReportType : %s ",
							i, reportType);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);
				reportDataProc.setParameter("P_CHUNK_VALUE", i);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d", list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream()
							.map(o -> convertObjToDto(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Record count after converting object array to DTO ",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {

							reportCell0.importCustomObjects(responseFromDao,
									headerColumns, false, rowCont, columnCnt,
									responseFromDao.size(), true, "yyyy-mm-dd",
									false);
							
							rowCont = rowCont + responseFromDao.size();


						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Successfully writing into xlsx for chunk count: %d ",
									i);
							LOGGER.debug(msg);
						}
					}

				}

			}
			return workbook;
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private EwbTotalRecordsDto convertObjToDto(Object[] arr,
			String reportType) {
		EwbTotalRecordsDto dto = new EwbTotalRecordsDto();
		// EwbProcessedRecordsDto dtop = new EwbProcessedRecordsDto();

		if (reportType.equalsIgnoreCase("Error Reports")
				|| reportType.equalsIgnoreCase("Total Reports")) {

			dto.setEWBNo((String) arr[0]);
			
			String time = "";
			String ewbDateTime = (String) arr[1];
			String[] splitedEwbDateTime = (ewbDateTime != null && !ewbDateTime.isEmpty()) ? 
					ewbDateTime.split("T") : null;
			if( splitedEwbDateTime == null ) {
				dto.setEWBDate(ewbDateTime);
			}
			else if(splitedEwbDateTime.length > 1) {
				for (int i = 1; i < splitedEwbDateTime.length; i++) {
					if (!splitedEwbDateTime[i].isEmpty())
						time = splitedEwbDateTime[i];

				}
				dto.setEWBDate(convertDate(splitedEwbDateTime[0])
						+ " " + time);
			} else {
				dto.setEWBDate(convertDate((String) arr[1])
						+ " " + (String) arr[27]);
			}
				
			dto.setStatus((String) arr[2]);
			dto.setValidTillDate(convertDate((String) arr[3]));
			dto.setSupplyType((String) arr[4]);
			dto.setOtherPartyGSTIN((String) arr[5]);
			dto.setFromGSTINInfo((String) arr[6]);
			dto.setToGSTINInfo((String) arr[7]);
			dto.setModeofGeneration((String) arr[8]);
			dto.setCancelledBy((String) arr[9]);
			dto.setCancelledDate(convertDate((String) arr[10]));
			dto.setDocNo((String) arr[11]);
			
			dto.setDocDate(convertDate((String) arr[12]));
			
			dto.setNoofItems((String) arr[13]);
			dto.setMainHSNDesc((String) arr[14]);
			dto.setMainHSNCode((String) arr[15]);
			dto.setAssessableValue((String) arr[16]);
			dto.setIGSTValue((String) arr[17]);
			dto.setCGSTValue((String) arr[18]);
			dto.setSGSTValue((String) arr[19]);
			dto.setCESSValue((String) arr[21]);
			dto.setCESSNonAdvolValue((String) arr[20]);
			dto.setOtherValue((String) arr[22]);
			dto.setTotalInvoiceValue((String) arr[23]);
			dto.setTransporterDetails((String) arr[24]);
			dto.setErrorCode((String) arr[25]);
			dto.setErrorDescription((String) arr[26]);
		

		} else {
			dto.setEWBNo((String) arr[0]);
			dto.setEWBDate(convertDate((String) arr[1]) 
					+" " +(String) arr[25]);
			dto.setStatus((String) arr[2]);
			dto.setValidTillDate(convertDate((String) arr[3]));
			dto.setSupplyType((String) arr[4]);
			dto.setOtherPartyGSTIN((String) arr[5]);
			dto.setFromGSTINInfo((String) arr[6]);
			dto.setToGSTINInfo((String) arr[7]);
			dto.setModeofGeneration((String) arr[8]);
			dto.setCancelledBy((String) arr[9]);
			dto.setCancelledDate(convertDate((String) arr[10]));
			dto.setDocNo((String) arr[11]);
			
			String stringDocDate = ((String) arr[12]);
			dto.setDocDate(convertDate(stringDocDate));
			
			dto.setNoofItems((String) arr[13]);
			dto.setMainHSNDesc((String) arr[14]);
			dto.setMainHSNCode((String) arr[15]);
			dto.setAssessableValue((String) arr[16]);
			dto.setIGSTValue((String) arr[17]);
			dto.setCGSTValue((String) arr[18]);
			dto.setSGSTValue((String) arr[19]);
			dto.setCESSValue((String) arr[21]);
			dto.setCESSNonAdvolValue((String) arr[20]);
			dto.setOtherValue((String) arr[22]);
			dto.setTotalInvoiceValue((String) arr[23]);
			dto.setTransporterDetails((String) arr[24]);
		}
		return dto;

	}
	
	private static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();

		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");

		String fileNameWithTimeStamp = fileName + "_" + timeMilli;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithTimeStamp);
		}

		return fileNameWithTimeStamp;
	}
	
	
	
	private String convertDate(String stringDocDate) {
		
		if(stringDocDate == null || stringDocDate.isEmpty()) {
			return null;
		}

		String[] splitedDocDate = stringDocDate.split("T");
		String string = splitedDocDate[0];
		LocalDate parseObjToDate = DateUtil.parseObjToDate(string);
		if(parseObjToDate == null) {
			return stringDocDate;
		}

		String month = parseObjToDate.getMonthValue() < 10 ? 
				0 + "" + parseObjToDate.getMonthValue()
				: parseObjToDate.getMonthValue() + "";

		String day = parseObjToDate.getDayOfMonth() < 10 ? 
						0 + "" + parseObjToDate.getDayOfMonth()
						: parseObjToDate.getDayOfMonth() + "";
						
		String date = day + "-" 
		+ month + "-" + parseObjToDate.getYear();
		return date;

	}
	
	public static void main(String[] args) {
		
		String s[] = {
				"12-2021",
				"tre-2021",
				"2021.12.15"};
		
		for (String s1 : s) {
			
			System.out.println("s1" +" : " + s1 );
			
			String[] splitedDocDate = s1.split("T");
			System.out.println("splitedDocDate" +" : " + splitedDocDate.toString() );
			String zerothElement = splitedDocDate[0];
			System.out.println("zerothElement" +" : " + zerothElement );
		}
		

	}

}
