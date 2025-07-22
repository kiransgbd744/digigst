package com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2VendorGSTINPeriodWiseRecordsDto;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2APAndNonAPVendorGSTINPeriodWiseRecords")
public class Gstr2APAndNonAPVendorGSTINPeriodWiseRecords {

	// Vendor_GSTIN_Period_Wise_Records

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

	public void getVendorGSTINPeriodWiseRecords(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName(
				"Vendor GSTIN Tax " + "Period Wise Report");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_AUTO_2APR_RPT_VENDOR_GSTIN_PERIOD");

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

				fullPath = tempDir.getAbsoluteFile() + File.separator
						+ reportType + ".csv";
				List<Gstr2VendorGSTINPeriodWiseRecordsDto> reconDataList = new ArrayList<>();

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
								"gstr2.recon.vendor.gstin.period.wise.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp(
										"gstr2.recon.vendor.gstin.period.wise.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2VendorGSTINPeriodWiseRecordsDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(
								Gstr2VendorGSTINPeriodWiseRecordsDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2VendorGSTINPeriodWiseRecordsDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2VendorGSTINPeriodWiseRecordsDto> beanWriter = builder
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
				Pair<String, String> uploadedDocName = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					/*vendorGSTINPeriodWiseRecordsFilePath = DocumentUtility
							.uploadZipFile(zipFile, "Gstr2ReconReports");*/
					
					uploadedDocName = DocumentUtility
							.uploadFile(zipFile,"Gstr2ReconReports");

				}
				if (uploadedDocName != null)
					/*addlReportRepo.updateReconFilePath(
							vendorGSTINPeriodWiseRecordsFilePath, configId,
							"Vendor_GSTIN_Period_Wise_Records");*/
				
				addlReportRepo.updateReconFilePathAndDocIdByReportName(uploadedDocName
						.getValue0(), uploadedDocName.getValue1(), configId, 
						"Vendor_GSTIN_Period_Wise_Records");
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Update file path for Vendor_GSTIN_Period_Wise_Records "
									+ ":%s : ",
									uploadedDocName);
					LOGGER.debug(msg);
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);

		}
		//updating flag in download table 
				addlReportRepo.updateIsReportProcExecuted(configId, 
						"Vendor_GSTIN_Period_Wise_Records");
		deleteTemporaryDirectory(tempDir);

	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsoluteFile()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsoluteFile());
				LOGGER.error(msg, ex);
			}
		}

	}

	private Gstr2VendorGSTINPeriodWiseRecordsDto convert(Object[] arr) {

		Gstr2VendorGSTINPeriodWiseRecordsDto obj = new Gstr2VendorGSTINPeriodWiseRecordsDto();

		obj.setSupplierGstin((arr[0] != null) ? arr[0].toString() : null);
		obj.setSupplierNamePR((arr[1] != null) ? arr[1].toString() : null);

		obj.setPeriodPR(
				(arr[2] != null) ? "`".concat(arr[2].toString()) : null);
		String docType = (arr[3] != null) ? arr[3].toString() : null;
		obj.setDocType(docType);

		obj.setTotalCount((arr[4] != null) ? arr[4].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR"))) {
			obj.setTotalTaxable(CheckForNegativeValue(arr[5]));
			obj.setTotalIGST(CheckForNegativeValue(arr[6]));
			obj.setTotalCGST(CheckForNegativeValue(arr[7]));
			obj.setTotalSGST(CheckForNegativeValue(arr[8]));
			obj.setTotalCESS(CheckForNegativeValue(arr[9]));
		} else {
			obj.setTotalTaxable((arr[5] != null) ? arr[5].toString() : null);
			obj.setTotalIGST((arr[6] != null) ? arr[6].toString() : null);
			obj.setTotalCGST((arr[7] != null) ? arr[7].toString() : null);
			obj.setTotalSGST((arr[8] != null) ? arr[8].toString() : null);
			obj.setTotalCESS((arr[9] != null) ? arr[9].toString() : null);

		}
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR"))) {
			obj.setExactMatchTaxable(CheckForNegativeValue(arr[11]));
			obj.setExactMatchIGST(CheckForNegativeValue(arr[12]));
			obj.setExactMatchCGST(CheckForNegativeValue(arr[13]));
			obj.setExactMatchSGST(CheckForNegativeValue(arr[14]));
			obj.setExactMatchCESS(CheckForNegativeValue(arr[15]));
			obj.setMatchWithTolerenceTaxable(CheckForNegativeValue(arr[17]));
			obj.setMatchWithTolerenceIGST(CheckForNegativeValue(arr[18]));
			obj.setMatchWithTolerenceCGST(CheckForNegativeValue(arr[19]));
			obj.setMatchWithTolerenceSGST(CheckForNegativeValue(arr[20]));
			obj.setMatchWithTolerenceCESS(CheckForNegativeValue(arr[21]));
			obj.setValueMismatchTaxable(CheckForNegativeValue(arr[23]));
			obj.setValueMismatchIGST(CheckForNegativeValue(arr[24]));
			obj.setValueMismatchCGST(CheckForNegativeValue(arr[25]));
			obj.setValueMismatchSGST(CheckForNegativeValue(arr[26]));
			obj.setValueMismatchCESS(CheckForNegativeValue(arr[27]));
			obj.setPosMismatchTaxable(CheckForNegativeValue(arr[29]));
			obj.setPosMismatchIGST(CheckForNegativeValue(arr[30]));
			obj.setPosMismatchCGST(CheckForNegativeValue(arr[31]));
			obj.setPosMismatchSGST(CheckForNegativeValue(arr[32]));
			obj.setPosMismatchCESS(CheckForNegativeValue(arr[33]));
			obj.setDocumentDateMishmatchTaxable(CheckForNegativeValue(arr[35]));
			obj.setDocumentDateMishmatchIGST(CheckForNegativeValue(arr[36]));
			obj.setDocumentDateMishmatchCGST(CheckForNegativeValue(arr[37]));
			obj.setDocumentDateMishmatchSGST(CheckForNegativeValue(arr[38]));
			obj.setDocumentDateMishmatchCESS(CheckForNegativeValue(arr[39]));
			obj.setDocumentTypeMishmatchTaxable(CheckForNegativeValue(arr[41]));
			obj.setDocumentTypeMishmatchIGST(CheckForNegativeValue(arr[42]));
			obj.setDocumentTypeMishmatchCGST(CheckForNegativeValue(arr[43]));
			obj.setDocumentTypeMishmatchSGST(CheckForNegativeValue(arr[44]));
			obj.setDocumentTypeMishmatchCESS(CheckForNegativeValue(arr[45]));
			obj.setDocumentNumberMishmatch1Taxable(
					CheckForNegativeValue(arr[47]));
			obj.setDocumentNumberMishmatch1IGST(CheckForNegativeValue(arr[48]));
			obj.setDocumentNumberMishmatch1CGST(CheckForNegativeValue(arr[49]));
			obj.setDocumentNumberMishmatch1SGST(CheckForNegativeValue(arr[50]));
			obj.setDocumentNumberMishmatch1CESS(CheckForNegativeValue(arr[51]));
			obj.setMultiMismatchTaxable(CheckForNegativeValue(arr[53]));
			obj.setMultiMismatchIGST(CheckForNegativeValue(arr[54]));
			obj.setMultiMismatchCGST(CheckForNegativeValue(arr[55]));
			obj.setMultiMismatchSGST(CheckForNegativeValue(arr[56]));
			obj.setMultiMismatchCESS(CheckForNegativeValue(arr[57]));
			obj.setPotentialMatch1Taxable(CheckForNegativeValue(arr[59]));
			obj.setPotentialMatch1IGST(CheckForNegativeValue(arr[60]));
			obj.setPotentialMatch1CGST(CheckForNegativeValue(arr[61]));
			obj.setPotentialMatch1SGST(CheckForNegativeValue(arr[62]));
			obj.setPotentialMatch1CESS(CheckForNegativeValue(arr[63]));
			obj.setDocumentNumberMishmatch2Taxable(
					CheckForNegativeValue(arr[65]));
			obj.setDocumentNumberMishmatch2IGST(CheckForNegativeValue(arr[66]));
			obj.setDocumentNumberMishmatch2CGST(CheckForNegativeValue(arr[67]));
			obj.setDocumentNumberMishmatch2SGST(CheckForNegativeValue(arr[68]));
			obj.setDocumentNumberMishmatch2CESS(CheckForNegativeValue(arr[69]));
			obj.setPotentialMatch2Taxable(CheckForNegativeValue(arr[71]));
			obj.setPotentialMatch2IGST(CheckForNegativeValue(arr[72]));
			obj.setPotentialMatch2CGST(CheckForNegativeValue(arr[73]));
			obj.setPotentialMatch2SGST(CheckForNegativeValue(arr[74]));
			obj.setPotentialMatch2CESS(CheckForNegativeValue(arr[75]));
			obj.setLogicalMatchTaxable(CheckForNegativeValue(arr[77]));
			obj.setLogicalMatchIGST(CheckForNegativeValue(arr[78]));
			obj.setLogicalMatchCGST(CheckForNegativeValue(arr[79]));
			obj.setLogicalMatchSGST(CheckForNegativeValue(arr[80]));
			obj.setLogicalMatchCESS(CheckForNegativeValue(arr[81]));
			obj.setAdditionTaxable2A(CheckForNegativeValue(arr[83]));
			obj.setAdditionIGST2A(CheckForNegativeValue(arr[84]));
			obj.setAdditionCGST2A(CheckForNegativeValue(arr[85]));
			obj.setAdditionSGST2A(CheckForNegativeValue(arr[86]));
			obj.setAdditionCESS2A(CheckForNegativeValue(arr[87]));
			obj.setAdditionTaxablePR(CheckForNegativeValue(arr[89]));
			obj.setAdditionIGSTPR(CheckForNegativeValue(arr[90]));
			obj.setAdditionCGSTPR(CheckForNegativeValue(arr[91]));
			obj.setAdditionSGSTPR(CheckForNegativeValue(arr[92]));
			obj.setAdditionCESSPR(CheckForNegativeValue(arr[93]));
			obj.setDocNoDocDateMisMatchTaxable(CheckForNegativeValue(arr[101]));
			obj.setDocNoDocDateMisMatchIGST(CheckForNegativeValue(arr[102]));
			obj.setDocNoDocDateMisMatchCGST(CheckForNegativeValue(arr[103]));
			obj.setDocNoDocDateMisMatchSGST(CheckForNegativeValue(arr[104]));
			obj.setDocNoDocDateMisMatchCESS(CheckForNegativeValue(arr[105]));

		} else {
			obj.setExactMatchTaxable(
					(arr[11] != null) ? arr[11].toString() : null);
			obj.setExactMatchIGST(
					(arr[12] != null) ? arr[12].toString() : null);
			obj.setExactMatchCGST(
					(arr[13] != null) ? arr[13].toString() : null);
			obj.setExactMatchSGST(
					(arr[14] != null) ? arr[14].toString() : null);
			obj.setExactMatchCESS(
					(arr[15] != null) ? arr[15].toString() : null);
			obj.setMatchWithTolerenceTaxable(
					(arr[17] != null) ? arr[17].toString() : null);
			obj.setMatchWithTolerenceIGST(
					(arr[18] != null) ? arr[18].toString() : null);
			obj.setMatchWithTolerenceCGST(
					(arr[19] != null) ? arr[19].toString() : null);
			obj.setMatchWithTolerenceSGST(
					(arr[20] != null) ? arr[20].toString() : null);
			obj.setMatchWithTolerenceCESS(
					(arr[21] != null) ? arr[21].toString() : null);
			obj.setValueMismatchTaxable(
					(arr[23] != null) ? arr[23].toString() : null);
			obj.setValueMismatchIGST(
					(arr[24] != null) ? arr[24].toString() : null);
			obj.setValueMismatchCGST(
					(arr[25] != null) ? arr[25].toString() : null);
			obj.setValueMismatchSGST(
					(arr[26] != null) ? arr[26].toString() : null);
			obj.setValueMismatchCESS(
					(arr[27] != null) ? arr[27].toString() : null);
			obj.setPosMismatchTaxable(
					(arr[29] != null) ? arr[29].toString() : null);
			obj.setPosMismatchIGST(
					(arr[30] != null) ? arr[30].toString() : null);
			obj.setPosMismatchCGST(
					(arr[31] != null) ? arr[31].toString() : null);
			obj.setPosMismatchSGST(
					(arr[32] != null) ? arr[32].toString() : null);
			obj.setPosMismatchCESS(
					(arr[33] != null) ? arr[33].toString() : null);
			obj.setDocumentDateMishmatchTaxable(
					(arr[35] != null) ? arr[35].toString() : null);
			obj.setDocumentDateMishmatchIGST(
					(arr[36] != null) ? arr[36].toString() : null);
			obj.setDocumentDateMishmatchCGST(
					(arr[37] != null) ? arr[37].toString() : null);
			obj.setDocumentDateMishmatchSGST(
					(arr[38] != null) ? arr[38].toString() : null);
			obj.setDocumentDateMishmatchCESS(
					(arr[39] != null) ? arr[39].toString() : null);
			obj.setDocumentTypeMishmatchTaxable(
					(arr[41] != null) ? arr[41].toString() : null);
			obj.setDocumentTypeMishmatchIGST(
					(arr[42] != null) ? arr[42].toString() : null);
			obj.setDocumentTypeMishmatchCGST(
					(arr[43] != null) ? arr[43].toString() : null);
			obj.setDocumentTypeMishmatchSGST(
					(arr[44] != null) ? arr[44].toString() : null);
			obj.setDocumentTypeMishmatchCESS(
					(arr[45] != null) ? arr[45].toString() : null);
			obj.setDocumentNumberMishmatch1Taxable(
					(arr[47] != null) ? arr[47].toString() : null);
			obj.setDocumentNumberMishmatch1IGST(
					(arr[48] != null) ? arr[48].toString() : null);
			obj.setDocumentNumberMishmatch1CGST(
					(arr[49] != null) ? arr[49].toString() : null);
			obj.setDocumentNumberMishmatch1SGST(
					(arr[50] != null) ? arr[50].toString() : null);
			obj.setDocumentNumberMishmatch1CESS(
					(arr[51] != null) ? arr[51].toString() : null);
			obj.setMultiMismatchTaxable(
					(arr[53] != null) ? arr[53].toString() : null);
			obj.setMultiMismatchIGST(
					(arr[54] != null) ? arr[54].toString() : null);
			obj.setMultiMismatchCGST(
					(arr[55] != null) ? arr[55].toString() : null);
			obj.setMultiMismatchSGST(
					(arr[56] != null) ? arr[56].toString() : null);
			obj.setMultiMismatchCESS(
					(arr[57] != null) ? arr[57].toString() : null);
			obj.setPotentialMatch1Taxable(
					(arr[59] != null) ? arr[59].toString() : null);
			obj.setPotentialMatch1IGST(
					(arr[60] != null) ? arr[60].toString() : null);
			obj.setPotentialMatch1CGST(
					(arr[61] != null) ? arr[61].toString() : null);
			obj.setPotentialMatch1SGST(
					(arr[62] != null) ? arr[62].toString() : null);
			obj.setPotentialMatch1CESS(
					(arr[63] != null) ? arr[63].toString() : null);
			obj.setDocumentNumberMishmatch2Taxable(
					(arr[65] != null) ? arr[65].toString() : null);
			obj.setDocumentNumberMishmatch2IGST(
					(arr[66] != null) ? arr[66].toString() : null);
			obj.setDocumentNumberMishmatch2CGST(
					(arr[67] != null) ? arr[67].toString() : null);
			obj.setDocumentNumberMishmatch2SGST(
					(arr[68] != null) ? arr[68].toString() : null);
			obj.setDocumentNumberMishmatch2CESS(
					(arr[69] != null) ? arr[69].toString() : null);
			obj.setPotentialMatch2Taxable(
					(arr[71] != null) ? arr[71].toString() : null);
			obj.setPotentialMatch2IGST(
					(arr[72] != null) ? arr[72].toString() : null);
			obj.setPotentialMatch2CGST(
					(arr[73] != null) ? arr[73].toString() : null);
			obj.setPotentialMatch2SGST(
					(arr[74] != null) ? arr[74].toString() : null);
			obj.setPotentialMatch2CESS(
					(arr[75] != null) ? arr[75].toString() : null);
			obj.setLogicalMatchTaxable(
					(arr[77] != null) ? arr[77].toString() : null);
			obj.setLogicalMatchIGST(
					(arr[78] != null) ? arr[78].toString() : null);
			obj.setLogicalMatchCGST(
					(arr[79] != null) ? arr[79].toString() : null);
			obj.setLogicalMatchSGST(
					(arr[80] != null) ? arr[80].toString() : null);
			obj.setLogicalMatchCESS(
					(arr[81] != null) ? arr[81].toString() : null);
			obj.setAdditionTaxable2A(
					(arr[83] != null) ? arr[83].toString() : null);
			obj.setAdditionIGST2A(
					(arr[84] != null) ? arr[84].toString() : null);
			obj.setAdditionCGST2A(
					(arr[85] != null) ? arr[85].toString() : null);
			obj.setAdditionSGST2A(
					(arr[86] != null) ? arr[86].toString() : null);
			obj.setAdditionCESS2A(
					(arr[87] != null) ? arr[87].toString() : null);
			obj.setAdditionTaxablePR(
					(arr[89] != null) ? arr[89].toString() : null);
			obj.setAdditionIGSTPR(
					(arr[90] != null) ? arr[90].toString() : null);
			obj.setAdditionCGSTPR(
					(arr[91] != null) ? arr[91].toString() : null);
			obj.setAdditionSGSTPR(
					(arr[92] != null) ? arr[92].toString() : null);
			obj.setAdditionCESSPR(
					(arr[93] != null) ? arr[93].toString() : null);
			obj.setDocNoDocDateMisMatchTaxable(
					(arr[101] != null) ? arr[101].toString() : null);
			obj.setDocNoDocDateMisMatchIGST(
					(arr[102] != null) ? arr[102].toString() : null);
			obj.setDocNoDocDateMisMatchCGST(
					(arr[103] != null) ? arr[103].toString() : null);
			obj.setDocNoDocDateMisMatchSGST(
					(arr[104] != null) ? arr[104].toString() : null);
			obj.setDocNoDocDateMisMatchCESS(
					(arr[105] != null) ? arr[105].toString() : null);
		}
		obj.setExactMatchCount((arr[10] != null) ? arr[10].toString() : null);
		obj.setMatchWithTolerenceCount(
				(arr[16] != null) ? arr[16].toString() : null);
		obj.setValueMismatchCount(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setPosMismatchCount((arr[28] != null) ? arr[28].toString() : null);
		obj.setDocumentDateMishmatchCount(
				(arr[34] != null) ? arr[34].toString() : null);
		obj.setDocumentTypeMishmatchCount(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setDocumentNumberMishmatch1Count(
				(arr[46] != null) ? arr[46].toString() : null);
		obj.setMultiMismatchCount(
				(arr[52] != null) ? arr[52].toString() : null);
		obj.setPotentialMatch1Count(
				(arr[58] != null) ? arr[58].toString() : null);
		obj.setDocumentNumberMishmatch2Count(
				(arr[64] != null) ? arr[64].toString() : null);
		obj.setPotentialMatch2Count(
				(arr[70] != null) ? arr[70].toString() : null);
		obj.setLogicalMatchCount((arr[76] != null) ? arr[76].toString() : null);
		obj.setAdditionCount2A((arr[82] != null) ? arr[82].toString() : null);
		obj.setAdditionCountPR((arr[88] != null) ? arr[88].toString() : null);
		obj.setDocNoDocDateMisMatchCount((arr[100] != null) ? arr[100].toString() : null);

		/*
		 * obj.setForceGSTR3BMatchCount( (arr[93] != null) ? arr[93].toString()
		 * : null); obj.setForceGSTR3BMatchTaxable( (arr[94] != null) ?
		 * arr[94].toString() : null); obj.setForceGSTR3BMatchIGST( (arr[95] !=
		 * null) ? arr[95].toString() : null); obj.setForceGSTR3BMatchCGST(
		 * (arr[96] != null) ? arr[96].toString() : null);
		 * obj.setForceGSTR3BMatchSGST( (arr[97] != null) ? arr[97].toString() :
		 * null); obj.setForceGSTR3BMatchCESS( (arr[98] != null) ?
		 * arr[98].toString() : null);
		 */

		return obj;
	}

	private String CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if(!value.toString().isEmpty()){
					return "-" + value.toString().replaceFirst("-", "");
				} else{
					return null;
				}
			}
		}
		return null;
	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "Vendor_GSTIN_Period_Wise_Records" + "_"
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
