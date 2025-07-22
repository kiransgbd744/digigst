package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

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
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2SupplierGSTINSummaryRecordsDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
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
 * @author Hema G M
 *
 */

@Slf4j
@Component("Gstr2SupplierGSTINSummary2BPRRecords")
public class Gstr2SupplierGSTINSummary2BPRRecords {

	// Supplier_GSTIN_Summary_Records

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRReportCommonServiceImpl")
	private Gstr2Recon2BPRReportCommonService commonService;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getSupplierGSTINSummaryRecords(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName("Supplier GSTIN Summary Report");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_RPT_2B_SUPP_GSTIN");

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
				List<Gstr2SupplierGSTINSummaryRecordsDto> reconDataList = new ArrayList<>();

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

						String invoiceHeadersTemplate = commonUtility.getProp(
								"gstr2bpr.recon.supplier.gstin.summary.taxperiod.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp(
										"gstr2.recon.supplier.gstin.summary.taxperiod.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2SupplierGSTINSummaryRecordsDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(
								Gstr2SupplierGSTINSummaryRecordsDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2SupplierGSTINSummaryRecordsDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2SupplierGSTINSummaryRecordsDto> beanWriter = builder
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
					deleteTemporaryDirectory(tempDir);
				}
				String zipFileName = null;
				Pair<String, String> uploadedDocName = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					/*supplierGSTINSummaryRecordsFilePath = DocumentUtility
							.uploadZipFile(zipFile, "Gstr2ReconReports");*/
					
					uploadedDocName = DocumentUtility
							.uploadFile(zipFile,"Gstr2ReconReports");

				}
				if (uploadedDocName != null) {
					// addlReportRepo.updateReconFilePath(supplierGSTINSummaryRecordsFilePath,
					// configId, "Supplier_GSTIN_Summary_Records");
					Gstr2Recon2BPRAddlReportsEntity chunkDetails = addlReportRepo
							.getChunckSizeforReportType(configId,
									"Supplier_GSTIN_Summary_Records");

					commonService.saveReportChunks(chunkDetails.getId(),
							"Supplier_GSTIN_Summary_Records_1",
							uploadedDocName.getValue0(), true, 
							uploadedDocName.getValue1());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Update file path for Supplier_GSTIN_Summary_Records "
									+ ":%s : ",
							uploadedDocName);
					LOGGER.debug(msg);
				}
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);
			deleteTemporaryDirectory(tempDir);
		}

		addlReportRepo.updateIsReportProcExecuted(configId, 
				"Supplier_GSTIN_Summary_Records");

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

	private Gstr2SupplierGSTINSummaryRecordsDto convert(Object[] arr) {

		Gstr2SupplierGSTINSummaryRecordsDto obj = new Gstr2SupplierGSTINSummaryRecordsDto();

		obj.setSupplierGSTINPR((arr[1] != null) ? arr[1].toString() : null);
		obj.setSupplierName2A((arr[2] != null) ? arr[2].toString() : null);

		obj.setExactMatchGST2A((arr[3] != null) ? arr[3].toString() : null);
		obj.setExactMatchGSTPR((arr[4] != null) ? arr[4].toString() : null);

		obj.setAdditionIn2AGST2A((arr[5] != null) ? arr[5].toString() : null);
		obj.setAdditionInPRGSTPR((arr[6] != null) ? arr[6].toString() : null);

		obj.setForceGSTR3BMatchGST2A(
				(arr[7] != null) ? arr[7].toString() : null);
		obj.setForceGSTR3BMatchGSTPR(
				(arr[8] != null) ? arr[8].toString() : null);

		obj.setMatchWithTolerenceGST2A(
				(arr[9] != null) ? arr[9].toString() : null);
		obj.setMatchWithTolerenceGSTPR(
				(arr[10] != null) ? arr[10].toString() : null);

		obj.setValueMismatchGST2A(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setValueMismatchGSTPR(
				(arr[12] != null) ? arr[12].toString() : null);

		obj.setPosMismatchGST2A((arr[13] != null) ? arr[13].toString() : null);
		obj.setPosMismatchGSTPR((arr[14] != null) ? arr[14].toString() : null);

		obj.setDocDateMismatchGST2A(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setDocDateMismatchGSTPR(
				(arr[16] != null) ? arr[16].toString() : null);

		obj.setDocTypeMismatchGST2A(
				(arr[17] != null) ? arr[17].toString() : null);
		obj.setDocTypeMismatchGSTPR(
				(arr[18] != null) ? arr[18].toString() : null);

		obj.setDocNoMismatchIGST2A(
				(arr[19] != null) ? arr[19].toString() : null);
		obj.setDocNoMismatchIGSTPR(
				(arr[20] != null) ? arr[20].toString() : null);

		obj.setMultiMismatchGST2A(
				(arr[21] != null) ? arr[21].toString() : null);
		obj.setMultiMismatchGSTPR(
				(arr[22] != null) ? arr[22].toString() : null);

		obj.setPotentialIGST2A((arr[23] != null) ? arr[23].toString() : null);
		obj.setPotentialIGSTPR((arr[24] != null) ? arr[24].toString() : null);

		obj.setDocNoMismatchIIGST2A(
				(arr[25] != null) ? arr[25].toString() : null);
		obj.setDocNoMismatchIIGSTPR(
				(arr[26] != null) ? arr[26].toString() : null);

		obj.setPotentialIIGST2A((arr[27] != null) ? arr[27].toString() : null);
		obj.setPotentialIIGSTPR((arr[28] != null) ? arr[28].toString() : null);

		obj.setLogicalMatchGST2A((arr[29] != null) ? arr[29].toString() : null);
		obj.setLogicalMatchGSTPR((arr[30] != null) ? arr[30].toString() : null);

		obj.setDocNoDocDateMismatch2A((arr[34] != null) ? arr[34].toString() : null);
		obj.setDocNoDocDateMismatchPR((arr[35] != null) ? arr[35].toString() : null);
		
		obj.setTotalGST2A((arr[31] != null) ? arr[31].toString() : null);
		obj.setTotalGSTPR((arr[32] != null) ? arr[32].toString() : null);
		
		obj.setDifferencePR2A((arr[33] != null) ? arr[33].toString() : null);

		return obj;
	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "Supplier_GSTIN_Summary_Records" + "_"
				+ configId;
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
