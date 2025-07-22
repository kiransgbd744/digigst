package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2LockedCFSNAmendedRecords")
public class Gstr2LockedCFSNAmendedRecords {
	//Locked_CFS_N_Amended_Records

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getGstr2LockedCFSNAmendedRecords(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName("Locked CFS N Amended Records");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_RPT_CFSN_AMDLINK");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			long dbLoadStTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs,"
								+ " Report Name and Data count: '%s' - '%s'",
						dbLoadTimeDiff, reportType, records.size());
				LOGGER.debug(msg);
			}

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ reportType + ".csv";
				List<CSFFlagNDto> reconDataList = new ArrayList<>();

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {
					reconDataList = records.stream().map(o -> convert(o))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								reportType, reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility
								.getProp("gstr2.recon.cfs.flag.n.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2.recon.cfs.flag.n.column")
								.split(",");
						ColumnPositionMappingStrategy
						<CSFFlagNDto> mappingStrategy = 
						
						new ColumnPositionMappingStrategy<>();
						mappingStrategy
								.setType(CSFFlagNDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<CSFFlagNDto> 
						builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<CSFFlagNDto> 
						beanWriter = builder
								.withMappingStrategy(mappingStrategy)
								.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
								.withLineEnd(CSVWriter.DEFAULT_LINE_END)
								.withEscapechar(
										CSVWriter.DEFAULT_ESCAPE_CHARACTER)
								.build();
						long generationStTime = System.currentTimeMillis();
						beanWriter.write(reconDataList);
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Total Time taken to"
											+ " Generate the report is '%d' "
											+ " millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									generationTimeDiff, reportType,
									records.size());
							LOGGER.debug(msg);
						}
					}
				} catch (Exception ex) {
					LOGGER.error("Exception while executing the Store Proc for "
							+ "Report Type :{}", reportType, ex);

				}
				String zipFileName = null;
				String lockCfsNFlagPath = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					lockCfsNFlagPath = DocumentUtility
							.uploadZipFile(zipFile, "Gstr2ReconReports");

				}
				if (lockCfsNFlagPath != null)
					addlReportRepo.updateReconFilePath(
							lockCfsNFlagPath, configId,
							"Locked_CFS_N_Amended_Records");
				if (LOGGER.isDebugEnabled()) {
					String msg = String
						.format("Update file path for Locked_CFS_N_Amended_Records "
									+ ":%s : ", lockCfsNFlagPath);
					LOGGER.debug(msg);
				}
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);

		}

		deleteTemporaryDirectory(tempDir);

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

	private CSFFlagNDto convert(Object[] arr) {

		CSFFlagNDto obj = new CSFFlagNDto();
		
		obj.setRemarks((arr[0] != null) ? arr[0].toString() : null);
		obj.setDiffCurrPrev2A((arr[1] != null) ? arr[1].toString() : null);
		obj.setTaxPeriod2APrevious((arr[2] != null) ? "`".concat(arr[2].toString()) : null);
		obj.setTaxPeriod2ACurrent((arr[3] != null) ? "`".concat(arr[3].toString()) : null);
		obj.setPrevReportTypePR((arr[4] != null) ? arr[4].toString() : null);
		obj.setPrevReportType2A((arr[5] != null) ? arr[5].toString() : null);
		obj.setSupplierGSTINPRPrevious(
				(arr[6] != null) ? arr[6].toString() : null);
		obj.setSupplierGSTIN2APrevious(
				(arr[7] != null) ? arr[7].toString() : null);
		obj.setSupplierGSTIN2ACurrent(
				(arr[8] != null) ? arr[8].toString() : null);
		obj.setSupplierName2APrevious(
				(arr[9] != null) ? arr[9].toString() : null);
		obj.setSupplierName2ACurrent(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setRecipientGSTINPRPrevious(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setRecipientGSTIN2APrevious(
				(arr[12] != null) ? arr[12].toString() : null);
		obj.setRecipientGSTIN2ACurrent(
				(arr[13] != null) ? arr[13].toString() : null);
		obj.setDOCTYPEPRPrevious((arr[14] != null) ? arr[14].toString() : null);
		obj.setDOCTYPE2APrevious((arr[15] != null) ? arr[15].toString() : null);
		obj.setDOCTYPE2ACurrent((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocumentNumberPRPrevious(
				(arr[17] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[17].toString()) : null);
		obj.setDocumentNumber2APrevious(
				(arr[18] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[18].toString()) : null);
		obj.setDocumentNumber2ACurrent(
				(arr[19] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[19].toString()) : null);
		obj.setDatePRPrevious((arr[20] != null) ? arr[20].toString() : null);
		obj.setDate2APrevious((arr[21] != null) ? arr[21].toString() : null);
		obj.setDate2ACurrent((arr[22] != null) ? arr[22].toString() : null);
		obj.setGSTPerPRPrevious((arr[23] != null) ? arr[23].toString() : null);
		obj.setGSTPer2APrevious((arr[24] != null) ? arr[24].toString() : null);
		obj.setGSTPer2ACurrent((arr[25] != null) ? arr[25].toString() : null);
		obj.setTaxablePRPrevious((arr[26] != null) ? arr[26].toString() : null);
		obj.setTaxable2APrevious((arr[27] != null) ? arr[27].toString() : null);
		obj.setTaxable2ACurrent((arr[28] != null) ? arr[28].toString() : null);
		obj.setIGSTPRPrevious((arr[29] != null) ? arr[29].toString() : null);
		obj.setIGST2APrevious((arr[30] != null) ? arr[30].toString() : null);
		obj.setIGST2ACurrent((arr[31] != null) ? arr[31].toString() : null);
		obj.setCGSTPRPrevious((arr[32] != null) ? arr[32].toString() : null);
		obj.setCGST2APrevious((arr[33] != null) ? arr[33].toString() : null);
		obj.setCGST2ACurrent((arr[34] != null) ? arr[34].toString() : null);
		obj.setSGSTPRPrevious((arr[35] != null) ? arr[35].toString() : null);
		obj.setSGST2APrevious((arr[36] != null) ? arr[36].toString() : null);
		obj.setSGST2ACurrent((arr[37] != null) ? arr[37].toString() : null);
		obj.setCESSPRPrevious((arr[38] != null) ? arr[38].toString() : null);
		obj.setCESS2APrevious((arr[39] != null) ? arr[39].toString() : null);
		obj.setCESS2ACurrent((arr[40] != null) ? arr[40].toString() : null);
		obj.setTotalTaxPRPrevious(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setTotalTax2APrevious(
				(arr[42] != null) ? arr[42].toString() : null);
		obj.setTotalTax2ACurrent((arr[43] != null) ? arr[43].toString() : null);
		obj.setInvoiceValuePRPrevious(
				(arr[44] != null) ? arr[44].toString() : null);
		obj.setInvoiceValue2APrevious(
				(arr[45] != null) ? arr[45].toString() : null);
		obj.setInvoiceValue2ACurrent(
				(arr[46] != null) ? arr[46].toString() : null);
		obj.setPOSPRPrevious((arr[47] != null) ? arr[47].toString() : null);
		obj.setPOS2APrevious((arr[48] != null) ? arr[48].toString() : null);
		obj.setPOS2ACurrent((arr[49] != null) ? arr[49].toString() : null);
		obj.setGSTR1FilingStatusPrevious(
				(arr[50] != null) ? arr[50].toString() : null);
		obj.setGSTR1FilingStatusCurrent(
				(arr[51] != null) ? arr[51].toString() : null);
		obj.setGSTR1FilingDatePrevious(
				(arr[52] != null) ? arr[52].toString() : null);
		obj.setGSTR1FilingDateCurrent(
				(arr[53] != null) ? arr[53].toString() : null);
		obj.setGSTR1FilingPeriodPrevious(
				(arr[54] != null) ? arr[54].toString() : null);
		obj.setGSTR1FilingPeriodCurrent(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setOrgInvAmendmentPeriodPrevious(
				(arr[56] != null) ? arr[56].toString() : null);
		obj.setOrgInvAmendmentPeriodCurrent(
				(arr[57] != null) ? arr[57].toString() : null);
		obj.setOrgAmendmentTypePrevious(
				(arr[58] != null) ? arr[58].toString() : null);
		obj.setOrgAmendmentTypeCurrent(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setReverseChargeFlag2APrevious(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setReverseChargeFlag2ACurrent(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setOrgDocNumber2APrevious(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setOrgDocNumber2ACurrent(
				(arr[63] != null) ? arr[63].toString() : null);
		obj.setOrgDocDate2APrevious(
				(arr[64] != null) ? arr[64].toString() : null);
		obj.setOrgDocDate2ACurrent(
				(arr[65] != null) ? arr[65].toString() : null);
		obj.setInvoiceNumberPrevious(
				(arr[66] != null) ? arr[66].toString() : null);
		obj.setInvoiceNumberCurrent(
				(arr[67] != null) ? arr[67].toString() : null);
		obj.setInvoiceDatePrevious(
				(arr[68] != null) ? arr[68].toString() : null);
		obj.setInvoiceDateCurrent(
				(arr[69] != null) ? arr[69].toString() : null);
		obj.setCRDRPreGSTPrevious(
				(arr[70] != null) ? arr[70].toString() : null);
		obj.setCRDRPreGSTCurrent((arr[71] != null) ? arr[71].toString() : null);
		obj.setITCEligiblePrevious(
				(arr[72] != null) ? arr[72].toString() : null);
		obj.setITCEligibleCurrent(
				(arr[73] != null) ? arr[73].toString() : null);
		obj.setDifferentialPercentagePrevious(
				(arr[74] != null) ? arr[74].toString() : null);
		obj.setDifferentialPercentageCurrent(
				(arr[75] != null) ? arr[75].toString() : null);

		return obj;

	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "Locked_CFS_N_Amended_Records" + "_" + configId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
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

}