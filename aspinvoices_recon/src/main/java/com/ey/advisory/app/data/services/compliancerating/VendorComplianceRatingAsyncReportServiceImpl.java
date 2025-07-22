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
@Component("VendorComplianceRatingAsyncReportServiceImpl")
public class VendorComplianceRatingAsyncReportServiceImpl
		implements VendorComplianceAsyncReportService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CompressAndZipXlsxFiles compressAndZipXlsxFiles;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("VendorComplianceRatingHelperServiceImpl")
	private VendorComplianceRatingHelperService ratingHelperService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	public static final String HIST_CONF_KEY = "compliance.hist.report.chunk.size";

	private static final MonthDay dueDateLogic1 = MonthDay.of(11, 30);

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
			String fullPath = tempDir.getAbsolutePath() + File.separator;

			boolean isChannelOneClient = ratingHelperService
					.getChannelOneClientInfo(entityId);
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();
			String fy = entity.getFyYear();
			String gstins = GenUtil.convertClobtoString(entity.getGstins());
			List<String> gstinList = Arrays.asList(gstins.split(","));
			try {

				List<VendorComplianceRatingAsyncReportDto> reportDtos = getDataFromSPandConvertToDto(
						id, isChannelOneClient, gstinList, fy);

				if (reportDtos != null) {
					writeToExcel(reportDtos, fullPath, fy, source);
				}

			} catch (Exception e) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				String msg = String
						.format("Exception occured while generating vendor "
								+ "compliance rating report for id:%s", id);
				LOGGER.error(msg);
				deleteTemporaryDirectory(tempDir);
				throw new AppException(msg, e);
			}

			if ("vendor".equalsIgnoreCase(source)) {
				fileConfigId = 5L;
			} else if ("customer".equalsIgnoreCase(source)) {
				fileConfigId = 6L;
			} else {
				fileConfigId = 7L;
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
			LOGGER.error(msg);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(msg, ex);
		}
	}

	@SuppressWarnings("unchecked")
	private List<VendorComplianceRatingAsyncReportDto> getDataFromSPandConvertToDto(
			Long id, boolean isChannelOne, List<String> gstinList, String finYear) {

		List<VendorComplianceRatingAsyncReportDto> reportDtosAll = new ArrayList<>();
		String procName = "vendorComplianceRating";
		try {

			Map<String, Config> configMap = configManager
					.getConfigs("COMPLIANCE_HIST", HIST_CONF_KEY, "DEFAULT");
			Integer chunkSize = configMap.get(HIST_CONF_KEY) == null ? 100
					: Integer.valueOf(configMap.get(HIST_CONF_KEY).getValue());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstin Chunk size for Compliance history report is {}",
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
					if (!currentDate.isAfter(dueDateForLogic1)) {
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
			
			for (VendorComplianceRatingAsyncReportDto dto : reportDtosAll) {
				String gstin = dto.getGstin();
				if (currentDate.isAfter(dueDateForLogic1)
						|| (!finYear.equalsIgnoreCase(currentFy))) {
					dto.setAvgRatingWithPrevFy(dto.getAvgFYRating());
				} else {
					dto.setAvgRatingWithPrevFy(
							gstinRatingWithPrevFyMap.get(gstin));
				}
			}
			
			return reportDtosAll;
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from storedProc";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private void writeToExcel(
			List<VendorComplianceRatingAsyncReportDto> reportDtos,
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
							.getProp("vendor.compliance.rating.data")
							.split(",");
					template = "VendorComplianceRatingReport.xlsx";
					filename = ConfigConstants.VENDORCOMPLIANCERATING;
				} else if ("customer".equalsIgnoreCase(source)) {
					invoiceHeaders = commonUtility
							.getProp("vendor.compliance.rating.data")
							.split(",");
					template = "VendorComplianceRatingReport.xlsx";
					filename = ConfigConstants.CUSTOMERCOMPLIANCERATING;

				} else {
					invoiceHeaders = commonUtility
							.getProp("my.compliance.rating.data").split(",");
					template = "MyComplianceRatingReport.xlsx";
					filename = ConfigConstants.MYCOMPLIANCERATING;
				}

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", template);
				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorComplianceRatingAsyncReportServiceImpl."
							+ "writeToExcel workbook created writing data "
							+ "to the workbook";
					LOGGER.debug(msg);
				}

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				reportCells.importCustomObjects(reportDtos, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						reportDtos.size(), true, "yyyy-mm-dd", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorComplianceRatingServiceImpl.writeToExcel "
							+ "saving workbook";
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

	private VendorComplianceRatingAsyncReportDto convertToDto(Object[] obj,
			boolean isClientOne) {
		try {
			VendorComplianceRatingAsyncReportDto dto = new VendorComplianceRatingAsyncReportDto();

			dto.setGstin(obj[0] != null ? obj[0].toString() : null);
			dto.setLegalName(obj[1] != null ? obj[1].toString() : null);
			dto.setDateOfReg(obj[2] != null
					? getFormattedDate(obj[2].toString()) : null);
			String filingFrequency = obj[3] != null ? obj[3].toString() : null;
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
				dto.setFreqOfFiling(freqOfFiling);
			} else {
				dto.setFreqOfFiling(null);
			}
			if (!isClientOne) {
				dto.setAvgFYRating(obj[4] != null ? (BigDecimal) obj[4] : null);
				dto.setGstr1RankApr(obj[7] != null ? obj[7].toString() : null);
				dto.setGstr3bRankApr(
						obj[10] != null ? obj[10].toString() : null);
				dto.setGstr1RankMay(
						obj[13] != null ? obj[13].toString() : null);
				dto.setGstr3bRankMay(
						obj[16] != null ? obj[16].toString() : null);
				dto.setGstr1RankJun(
						obj[19] != null ? obj[19].toString() : null);
				dto.setGstr3bRankJun(
						obj[22] != null ? obj[22].toString() : null);
				dto.setGstr1RankJul(
						obj[25] != null ? obj[25].toString() : null);
				dto.setGstr3bRankJul(
						obj[28] != null ? obj[28].toString() : null);
				dto.setGstr1RankAug(
						obj[31] != null ? obj[31].toString() : null);
				dto.setGstr3bRankAug(
						obj[34] != null ? obj[34].toString() : null);
				dto.setGstr1RankSep(
						obj[37] != null ? obj[37].toString() : null);
				dto.setGstr3bRankSep(
						obj[40] != null ? obj[40].toString() : null);
				dto.setGstr1RankOct(
						obj[43] != null ? obj[43].toString() : null);
				dto.setGstr3bRankOct(
						obj[46] != null ? obj[46].toString() : null);
				dto.setGstr1RankNov(
						obj[49] != null ? obj[49].toString() : null);
				dto.setGstr3bRankNov(
						obj[52] != null ? obj[52].toString() : null);
				dto.setGstr1RankDec(
						obj[55] != null ? obj[55].toString() : null);
				dto.setGstr3bRankDec(
						obj[58] != null ? obj[58].toString() : null);
				dto.setGstr1RankJan(
						obj[61] != null ? obj[61].toString() : null);
				dto.setGstr3bRankJan(
						obj[64] != null ? obj[64].toString() : null);
				dto.setGstr1RankFeb(
						obj[67] != null ? obj[67].toString() : null);
				dto.setGstr3bRankFeb(
						obj[70] != null ? obj[70].toString() : null);
				dto.setGstr1RankMar(
						obj[73] != null ? obj[73].toString() : null);
				dto.setGstr3bRankMar(
						obj[76] != null ? obj[76].toString() : null);
			}
			dto.setGstr1DueApr(obj[5] != null
					? getFormattedDate(obj[5].toString()) : null);
			dto.setGstr1DofApr(obj[6] != null
					? getFormattedDate(obj[6].toString()) : null);
			dto.setGstr3bDueApr(obj[8] != null
					? getFormattedDate(obj[8].toString()) : null);
			dto.setGstr3bDofApr(obj[9] != null
					? getFormattedDate(obj[9].toString()) : null);

			dto.setGstr1DueMay(obj[11] != null
					? getFormattedDate(obj[11].toString()) : null);
			dto.setGstr1DofMay(obj[12] != null
					? getFormattedDate(obj[12].toString()) : null);
			dto.setGstr3bDueMay(obj[14] != null
					? getFormattedDate(obj[14].toString()) : null);
			dto.setGstr3bDofMay(obj[15] != null
					? getFormattedDate(obj[15].toString()) : null);

			dto.setGstr1DueJun(obj[17] != null
					? getFormattedDate(obj[17].toString()) : null);
			dto.setGstr1DofJun(obj[18] != null
					? getFormattedDate(obj[18].toString()) : null);
			dto.setGstr3bDueJun(obj[20] != null
					? getFormattedDate(obj[20].toString()) : null);
			dto.setGstr3bDofJun(obj[21] != null
					? getFormattedDate(obj[21].toString()) : null);

			dto.setGstr1DueJul(obj[23] != null
					? getFormattedDate(obj[23].toString()) : null);
			dto.setGstr1DofJul(obj[24] != null
					? getFormattedDate(obj[24].toString()) : null);
			dto.setGstr3bDueJul(obj[26] != null
					? getFormattedDate(obj[26].toString()) : null);
			dto.setGstr3bDofJul(obj[27] != null
					? getFormattedDate(obj[27].toString()) : null);

			dto.setGstr1DueAug(obj[29] != null
					? getFormattedDate(obj[29].toString()) : null);
			dto.setGstr1DofAug(obj[30] != null
					? getFormattedDate(obj[30].toString()) : null);
			dto.setGstr3bDueAug(obj[32] != null
					? getFormattedDate(obj[32].toString()) : null);
			dto.setGstr3bDofAug(obj[33] != null
					? getFormattedDate(obj[33].toString()) : null);

			dto.setGstr1DueSep(obj[35] != null
					? getFormattedDate(obj[35].toString()) : null);
			dto.setGstr1DofSep(obj[36] != null
					? getFormattedDate(obj[36].toString()) : null);
			dto.setGstr3bDueSep(obj[38] != null
					? getFormattedDate(obj[38].toString()) : null);
			dto.setGstr3bDofSep(obj[39] != null
					? getFormattedDate(obj[39].toString()) : null);

			dto.setGstr1DueOct(obj[41] != null
					? getFormattedDate(obj[41].toString()) : null);
			dto.setGstr1DofOct(obj[42] != null
					? getFormattedDate(obj[42].toString()) : null);
			dto.setGstr3bDueOct(obj[44] != null
					? getFormattedDate(obj[44].toString()) : null);
			dto.setGstr3bDofOct(obj[45] != null
					? getFormattedDate(obj[45].toString()) : null);

			dto.setGstr1DueNov(obj[47] != null
					? getFormattedDate(obj[47].toString()) : null);
			dto.setGstr1DofNov(obj[48] != null
					? getFormattedDate(obj[48].toString()) : null);
			dto.setGstr3bDueNov(obj[50] != null
					? getFormattedDate(obj[50].toString()) : null);
			dto.setGstr3bDofNov(obj[51] != null
					? getFormattedDate(obj[51].toString()) : null);

			dto.setGstr1DueDec(obj[53] != null
					? getFormattedDate(obj[53].toString()) : null);
			dto.setGstr1DofDec(obj[54] != null
					? getFormattedDate(obj[54].toString()) : null);
			dto.setGstr3bDueDec(obj[56] != null
					? getFormattedDate(obj[56].toString()) : null);
			dto.setGstr3bDofDec(obj[57] != null
					? getFormattedDate(obj[57].toString()) : null);

			dto.setGstr1DueJan(obj[59] != null
					? getFormattedDate(obj[59].toString()) : null);
			dto.setGstr1DofJan(obj[60] != null
					? getFormattedDate(obj[60].toString()) : null);
			dto.setGstr3bDueJan(obj[62] != null
					? getFormattedDate(obj[62].toString()) : null);
			dto.setGstr3bDofJan(obj[63] != null
					? getFormattedDate(obj[63].toString()) : null);

			dto.setGstr1DueFeb(obj[65] != null
					? getFormattedDate(obj[65].toString()) : null);
			dto.setGstr1DofFeb(obj[66] != null
					? getFormattedDate(obj[66].toString()) : null);
			dto.setGstr3bDueFeb(obj[68] != null
					? getFormattedDate(obj[68].toString()) : null);
			dto.setGstr3bDofFeb(obj[69] != null
					? getFormattedDate(obj[69].toString()) : null);

			dto.setGstr1DueMar(obj[71] != null
					? getFormattedDate(obj[71].toString()) : null);
			dto.setGstr1DofMar(obj[72] != null
					? getFormattedDate(obj[72].toString()) : null);
			dto.setGstr3bDueMar(obj[74] != null
					? getFormattedDate(obj[74].toString()) : null);
			dto.setGstr3bDofMar(obj[75] != null
					? getFormattedDate(obj[75].toString()) : null);

			dto.setGstinStatus(obj[77] != null ? obj[77].toString() : null);
			dto.setCanDate(obj[78] != null
					? getFormattedDate(obj[78].toString()) : null);

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
}
