package com.ey.advisory.app.data.services.compliancerating;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.CompressAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("VendorComplianceSummaryAsyncReportServiceImpl")
public class VendorComplianceSummaryAsyncReportServiceImpl
		implements VendorComplianceAsyncReportService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CompressAndZipXlsxFiles compressAndZipXlsxFiles;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("VendorComplianceRatingHelperServiceImpl")
	private VendorComplianceRatingHelperService ratingHelperService;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static final MonthDay dueDateLogic1 = MonthDay.of(11, 30);

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	public static final String HIST_CONF_KEY = "compliance.hist.report.chunk.size";

	@Override
	public void generateReports(Long id, String source, Long entityId) {

		File tempDir = null;
		Long fileConfigId = null;
		try {
			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			boolean isChannelOneClient = ratingHelperService
					.getChannelOneClientInfo(entityId);
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();
			String fy = entity.getFyYear();

			String fullPath = tempDir.getAbsolutePath() + File.separator;
			String gstins = GenUtil.convertClobtoString(entity.getGstins());
			List<String> gstinList = Arrays.asList(gstins.split(","));

			try {

				List<VendorComplianceSummaryAsyncReportDto> reportDtos = getDataFromSPandConvertToDto(
						id, isChannelOneClient, gstinList, fy);

				if (reportDtos != null) {
					writeToExcel(reportDtos, fullPath, fy, source);
				}

			} catch (Exception e) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				String msg = String.format(
						"Exception occured while generating vendor "
								+ "compliance summary rating report for id:%s",
						id);
				LOGGER.error(msg);
				deleteTemporaryDirectory(tempDir);
				throw new AppException(msg, e);
			}
			if ("vendor".equalsIgnoreCase(source)) {
				fileConfigId = 2L;
			} else if ("customer".equalsIgnoreCase(source)) {
				fileConfigId = 3L;
			} else {
				fileConfigId = 4L;
			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				zipFileName = compressAndZipXlsxFiles.zipfolder(fileConfigId,
						tempDir);

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
				LOGGER.error("No Data found for report id : %s", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}
			deleteTemporaryDirectory(tempDir);

		} catch (Exception ex) {
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occured in Vendor Compliance Rating "
					+ "while generating report ";
			LOGGER.error(msg, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(msg, ex);
		}
	}

	@SuppressWarnings("unchecked")
	private List<VendorComplianceSummaryAsyncReportDto> getDataFromSPandConvertToDto(
			Long id, boolean isChannelOne, List<String> gstinList,
			String finYear) {
		List<VendorComplianceSummaryAsyncReportDto> reportDtosAll = new ArrayList<>();

		String procName = "vendorComplianceSummary";
		try {

			Map<String, Config> configMap = configManager
					.getConfigs("COMPLIANCE_HIST", HIST_CONF_KEY, "DEFAULT");
			Integer chunkSize = configMap.get(HIST_CONF_KEY) == null ? 100
					: Integer.valueOf(configMap.get(HIST_CONF_KEY).getValue());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstin Chunk size for Compliance summary report is {}",
						chunkSize);
			}

			LocalDateTime startTime = LocalDateTime.now();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Start time for Compliance history report id {} is {} for GSTIN size {}",
						id, startTime, gstinList.size());
			}

			List<List<String>> returnChunks = Lists.partition(gstinList,
					chunkSize);
			String procNamePrevFy = "apiVendorComplianceRatingPrevFy";
			Integer year = Integer.parseInt(finYear.substring(0, 4));
			LocalDate dueDateForLogic1 = dueDateLogic1.atYear(year);
			LocalDate currentDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			Map<String, BigDecimal> gstinRatingWithPrevFyMap = new HashMap<>();
			String currentFy = GenUtil.getCurrentFinancialYear();
			for (List<String> chunk : returnChunks) {

				String gstinString = String.join(",", chunk);

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);
				reportDataProc.setParameter("P_GSTIN", gstinString);

				List<Object[]> list = reportDataProc.getResultList();

				if (!list.isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Executed Stored proc to get data and "
										+ "got resultset of size: %d",
								list.size());
						LOGGER.debug(msg);
					}

					reportDtosAll.addAll(list.stream()
							.map(o -> convertToDto(o, isChannelOne))
							.collect(Collectors.toCollection(ArrayList::new)));

				}
				if (finYear.equalsIgnoreCase(currentFy)) {
					if (currentDate.isAfter(dueDateForLogic1)) {
						if (finYear != null
								&& finYear.matches("\\d{4}-\\d{2}")) {
							String prevFy = getPreviousFy(finYear);
							StoredProcedureQuery reportDataProcPrevFy = entityManager
									.createNamedStoredProcedureQuery(
											procNamePrevFy);

							reportDataProcPrevFy.setParameter("P_REPORT_DOWNLOAD_ID",
									id);
							reportDataProcPrevFy.setParameter("P_GSTIN", gstinString);
							reportDataProcPrevFy.setParameter("P_OLD_FY", prevFy);

							List<Object[]> listPrevFy = reportDataProcPrevFy
									.getResultList();
							listPrevFy.stream().filter(
									arr -> arr[0] != null && arr[1] != null)
									.forEach(arr -> gstinRatingWithPrevFyMap
											.put(arr[0].toString(),
													(BigDecimal) arr[1]));
						}
					}
				}
			}

			for (VendorComplianceSummaryAsyncReportDto dto : reportDtosAll) {
				String gstin = dto.getGstin();
				if (currentDate.isAfter(dueDateForLogic1)
						|| (!finYear.equalsIgnoreCase(currentFy))) {
					dto.setAvgRatingWithPrevFy(dto.getAverageRanking());
				} else {
					dto.setAvgRatingWithPrevFy(gstinRatingWithPrevFyMap
							.get(gstin));
				}
			}

			LocalDateTime endTime = LocalDateTime.now();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Record count after converting object array to DTO %d",
						reportDtosAll.size());
				LOGGER.debug(msg);
			}
			if (LOGGER.isDebugEnabled()) {
				int seconds = (int) ChronoUnit.MILLIS.between(startTime,
						endTime);
				LOGGER.debug(
						"Endtime for Compliance history report id {} is {} for GSTIN size {}"
								+ " and total time taken is {}ms",
						id, endTime, gstinList.size(), seconds);
			}

			return reportDtosAll;
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from storedProc";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private String getPreviousFy(String currentFy) {
		try {
			int startYear = Integer.parseInt(currentFy.substring(0, 4)) - 1;
			int endYear = Integer.parseInt(currentFy.substring(5, 7)) - 1;
			if (endYear < 0)
				endYear += 100; // handle "00" case
			String prevFy = String.format("%d-%02d", startYear, endYear);
			return prevFy;
		} catch (Exception ee) {
			String msg = String.format(
					"Exception occered while getting Prev Fy %s", currentFy);
			LOGGER.error(msg, ee);
			return null;
		}
	}

	private void writeToExcel(
			List<VendorComplianceSummaryAsyncReportDto> reportDtos,
			String fullPath, String fy, String source) {
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		try {
			if (reportDtos != null && !reportDtos.isEmpty()) {

				String template = null;
				String[] invoiceHeaders = null;
				String filename = null;
				if ("vendor".equalsIgnoreCase(source)) {
					invoiceHeaders = commonUtility
							.getProp("vendor.compliance.summary.data")
							.split(",");
					template = "VendorComplianceSummaryReport.xlsx";
					filename = ConfigConstants.VENDORCOMPLIANCESUMMARY;
				} else if ("customer".equalsIgnoreCase(source)) {
					invoiceHeaders = commonUtility
							.getProp("vendor.compliance.summary.data")
							.split(",");
					template = "CustomerComplianceSummaryReport.xlsx";
					filename = ConfigConstants.CUSTOMERCOMPLIANCESUMMARY;
				} else {
					invoiceHeaders = commonUtility
							.getProp("my.compliance.summary.data").split(",");
					template = "MyComplianceSummaryReport.xlsx";
					filename = ConfigConstants.MYCOMPLIANCESUMMARY;

				}

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", template);
				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorComplianceSummaryAsyncReportServiceImpl."
							+ "writeToExcel workbook created writing data "
							+ "to the workbook";
					LOGGER.debug(msg);
				}

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				reportCells.importCustomObjects(reportDtos, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						reportDtos.size(), true, "yyyy-mm-dd", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorComplianceSummaryAsyncReportServiceImpl."
							+ "writeToExcel saving workbook";
					LOGGER.debug(msg);
				}
				DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String timeMilli = dtf.format(LocalDateTime.now());

				workbook.save(fullPath + filename + "_" + fy + "_" + timeMilli
						+ ".xlsx", SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " response list in the directory : {}",
							workbook.getAbsolutePath());
				}
			} else {
				throw new AppException(
						"No records found, cannot generate report");
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}
	}

	private VendorComplianceSummaryAsyncReportDto convertToDto(Object[] obj,
			boolean isClientOne) {
		try {
			VendorComplianceSummaryAsyncReportDto dto = new VendorComplianceSummaryAsyncReportDto();

			dto.setGstin(obj[0] != null ? obj[0].toString() : null);
			dto.setBusinessName(obj[1] != null ? obj[1].toString() : null);
			dto.setGstinStatus(obj[2] != null ? obj[2].toString() : null);
			dto.setRegDate(obj[3] != null ? getFormattedDate(obj[3].toString())
					: null);
			dto.setCanDate(obj[4] != null ? getFormattedDate(obj[4].toString())
					: null);
			dto.setTaxPayerType(obj[5] != null ? obj[5].toString() : null);
			dto.setLastPeriodOfFilingGstr1(
					obj[6] != null ? obj[6].toString() : null);
			dto.setLastPeriodOfFilingGstr3b(
					obj[7] != null ? obj[7].toString() : null);
			String filingFrequency = obj[8] != null ? obj[8].toString() : null;
			if (filingFrequency != null) {
				String[] freqArray = filingFrequency.split(", ");
				Arrays.sort(freqArray);
				String freqOfFiling = "";	
				int i = 0;
				for (String fre : freqArray) {
					freqOfFiling = freqOfFiling + fre;
					i++;
					if(i != freqArray.length){
						freqOfFiling = freqOfFiling + ", ";
					}
				}				
				dto.setFilingType(freqOfFiling);
			} else {
				dto.setFilingType(null);
			}
			
			if (!isClientOne) {
				dto.setAverageRanking(
						obj[9] != null ? (BigDecimal) obj[9] : null);
			}
			dto.setPerFiledWithInTime(
					obj[10] != null ? (BigDecimal) obj[10] : null);
			dto.setTotalReturnsFiled(
					obj[11] != null ? obj[11].toString() : null);
			dto.setFiledWithInTime(obj[12] != null ? obj[12].toString() : null);
			dto.setToalLateFiled(obj[13] != null ? obj[13].toString() : null);
			dto.setNotFiled(obj[14] != null ? obj[14].toString() : null);

			return dto;
		} catch (Exception ee) {
			String msg = String.format(
					"Exception occered while converting obj to Dto %s", obj);
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	private String getFormattedDate(String date) {
		try {
			LocalDate ld = LocalDate.parse(date);
			return ld.format(formatter);
		} catch (Exception ee) {
			String msg = String
					.format("Exception occered while formatting date %s", date);
			LOGGER.error(msg, ee);
			return null;
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}
	}
}
