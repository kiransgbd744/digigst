/**
 * 
 */
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
@Component("Gstr2CdnrInvRefRegGstin2ARecords")
public class Gstr2CdnrInvRefRegGstin2ARecords {

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

	public void getCdnrInvRefRegGstin2ARecords(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName(
				"CRD-INV_Ref_Reg_GSTR_2A_6A_Records");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_RPT_CDNRINV2A");

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
				List<Gstr2CdnrInvRefRegDto> reconDataList = new ArrayList<>();

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
								.getProp("gstr2.recon.cdnr.ref.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2.recon.cdnr.ref.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2CdnrInvRefRegDto> 
						mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(Gstr2CdnrInvRefRegDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2CdnrInvRefRegDto> builder 
						= new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2CdnrInvRefRegDto> beanWriter = 
								builder
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
				String cdr2AFilePath = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId, 
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					cdr2AFilePath = DocumentUtility.uploadZipFile(zipFile,
							"Gstr2ReconReports");

				}
				if(cdr2AFilePath != null)
				addlReportRepo.updateReconFilePath(cdr2AFilePath, configId,
						"CRD-INV_Ref_Reg_GSTR_2A_Records");
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Update file path for CRD-INV_Ref_Reg_GSTR_2A_Records :%s : ",
							cdr2AFilePath);
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

	private Gstr2CdnrInvRefRegDto convert(Object[] arr) {

		Gstr2CdnrInvRefRegDto obj = new Gstr2CdnrInvRefRegDto();
		
		obj.setTaxPeriodCR((arr[0] != null) ? "`".concat(arr[0].toString()) : null);
		obj.setTaxPeriodNV((arr[1] != null) ? "`".concat(arr[1].toString()) : null);
		obj.setReportTypeCR((arr[2] != null) ? arr[2].toString() : null);
		obj.setReportTypeINV((arr[3] != null) ? arr[3].toString() : null);
		obj.setErrors((arr[4] != null) ? arr[4].toString() : null);
		obj.setSupplierNameINV((arr[5] != null) ? arr[5].toString() : null);
		obj.setSupplierGSTINCR((arr[6] != null) ? (arr[6].toString()) : null);
		obj.setSupplierGSTININV((arr[7] != null) ? (arr[7].toString()) : null);
		obj.setRecipientGSTINCR((arr[8] != null) ? arr[8].toString() : null);
		obj.setRecipientGSTININV((arr[9] != null) ? arr[9].toString() : null);
		obj.setDocTYPECR((arr[10] != null) ? arr[10].toString() : null);
		obj.setDocTYPEINV((arr[11] != null) ? arr[11].toString() : null);
		obj.setDocNoCR((arr[12] != null) ? "`".concat(arr[12].toString()) : null);
		obj.setDocNoINV((arr[13] != null) ? "`".concat(arr[13].toString()) : null);
		obj.setDocDateCR((arr[14] != null) ? (arr[14].toString()) : null);
		obj.setDocDateINV((arr[15] != null) ? (arr[15].toString()) : null);
		obj.setGstPercentCR((arr[16] != null) ? (arr[16].toString()) : null);
		obj.setGstPercentINV((arr[17] != null) ? (arr[17].toString()) : null);
		obj.setTaxableValueCR((arr[18] != null) ? (arr[18].toString()) : null);
		obj.setTaxableValueINV((arr[19] != null) ? (arr[19].toString()) : null);
		obj.setCGSTCR((arr[20] != null) ? (arr[20].toString()) : null);
		obj.setCGSTINV((arr[21] != null) ? (arr[21].toString()) : null);
		obj.setSGSTCR((arr[22] != null) ? (arr[22].toString()) : null);
		obj.setSGSTINV((arr[23] != null) ? (arr[23].toString()) : null);
		obj.setIGSTCR((arr[24] != null) ? (arr[24].toString()) : null);
		obj.setIGSTINV((arr[25] != null) ? (arr[25].toString()) : null);
		obj.setCESSCR((arr[26] != null) ? (arr[26].toString()) : null);
		obj.setCESSINV((arr[27] != null) ? (arr[27].toString()) : null);
		obj.setInvoiceValueCR((arr[28] != null) ? (arr[28].toString()) : null);
		obj.setInvoiceValueINV((arr[29] != null) ? (arr[29].toString()) : null);
		obj.setPOSCR((arr[30] != null) ? (arr[30].toString()) : null);
		obj.setPOSINV((arr[31] != null) ? (arr[31].toString()) : null);
		obj.setRCMCR((arr[32] != null) ? (arr[32].toString()) : null);
		obj.setRCMINV((arr[33] != null) ? (arr[33].toString()) : null);
		obj.setCFSFlagCR((arr[34] != null) ? (arr[34].toString()) : null);
		obj.setCFSFlagINV((arr[35] != null) ? (arr[35].toString()) : null);
		obj.setTableTypeCR((arr[36] != null) ? (arr[36].toString()) : null);
		obj.setTableTypeINV((arr[37] != null) ? (arr[37].toString()) : null);
		obj.setCRDRPreGSTCR((arr[38] != null) ? (arr[38].toString()) : null);
		obj.setRequestIDCR((arr[39] != null) ? (arr[39].toString()) : null);
		obj.setRequestIDINV((arr[40] != null) ? (arr[40].toString()) : null);
		obj.setInvoiceKeyCR((arr[41] != null) ? (arr[41].toString()) : null);
		obj.setInvoiceKeyINV((arr[42] != null) ? (arr[42].toString()) : null);
		obj.setReferenceIDCR((arr[43] != null) ? (arr[43].toString()) : null);
		obj.setReferenceIDINV((arr[44] != null) ? (arr[44].toString()) : null);
		
		return obj;
	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "CRD-INV_Ref_Reg_GSTR_2A_Records" + "_" + configId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}
	private static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString(); 
		
		timeMilli = timeMilli.replace("." , "");
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
