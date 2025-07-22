package com.ey.advisory.app.glrecon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconSrFileUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;
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
 * 
 * @author ashutosh.kar
 *
 */
@Component("PRFileCreationDaoImpl")
@Slf4j
public class PRFileCreationDaoImpl implements PRFileCreationDao {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;

	@Autowired
	@Qualifier("GlReconGstinRepository")
	private GlReconGstinRepository glReconGstinConfig;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	@Autowired
	@Qualifier("GlReconSrFileUploadRepository")
	private GlReconSrFileUploadRepository glReconSrFileUploadRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Override
	public String generateReports(Long configId) {

		try {
			String userName = null;
			User user = SecurityContext.getUser();
			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			List<GlReconSrFileUploadEntity> entitiesToSave = new ArrayList<>();
			GlReconSrFileUploadEntity srEntity = new GlReconSrFileUploadEntity();
			Optional<GlReconReportConfigEntity> entity = glReconReportConfig
					.findById(configId);

			Long entityId = entity.get().getEntityId();

			List<String> gstins = glReconGstinConfig
					.findAllGstinsByConfigId(configId);

			// chunking logic
			int chunkNo = getChunkValue(configId);
			if (chunkNo == 0) {
				glReconReportConfig.updateReconConfigStatusAndReportName(
						"NO_DATA_FOUND", null, null, LocalDateTime.now(),
						configId);
				return "NO_DATA_FOUND";
			}
			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery storedProcQuery = entityManager
						.createStoredProcedureQuery(
								"USP_DISP_CHUNK_PR_GL_RECON");
				storedProcQuery.registerStoredProcedureParameter(
						"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);
				storedProcQuery.registerStoredProcedureParameter(
						"P_CHUNK_VALUE", Integer.class, ParameterMode.IN);
				storedProcQuery.setParameter("P_REPORT_DOWNLOAD_ID", configId);
				storedProcQuery.setParameter("P_CHUNK_VALUE", i);

				List<Object[]> list = storedProcQuery.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() == 0) {
					glReconReportConfig.updateReconConfigStatusAndReportName(
							"NO_DATA_FOUND", null, null, LocalDateTime.now(),
							configId);
					return "NO_DATA_FOUND";
				}
				Pair<String, String> uploadedDocIdAndName = createPRFile(list,
						configId, entityId);
				String uploadedDocName = uploadedDocIdAndName.getValue0();
				String uploadedDocId = uploadedDocIdAndName.getValue1();
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed Stored proc to get ChunkData and got result set of size: %d. "
									+ "Uploaded Document Name: '%s', Uploaded Document ID: '%s',chunk value is: '%d'",
							list.size(), uploadedDocName, uploadedDocId,
							chunkNo);
					LOGGER.debug(msg);
				}

				srEntity = new GlReconSrFileUploadEntity();
				srEntity.setReqId(configId);
				srEntity.setUploadedDocName(uploadedDocName);
				srEntity.setUploadedDocNumber(uploadedDocId);
				srEntity.setFileType("PR");
				srEntity.setUserName(userName);
				srEntity.setCreatedOn(LocalDateTime.now().toString());
				entitiesToSave.add(srEntity);
			}
			if (!entitiesToSave.isEmpty()) {
				glReconSrFileUploadRepository.saveAll(entitiesToSave);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"inside !entitiesToSave save condition after execution of save");
					LOGGER.debug(msg);
				}
			}

			return "SUCCESS";
		} catch (Exception ex) {
			throw new AppException(ex);
		}

	}

	private PRFileUploadDto convert(Object[] arr, String MainQuesValue,
			String SubQuesValue) {

		PRFileUploadDto dto = new PRFileUploadDto();
		try {

			// write the setter method of PRFILEUploadDto with null conditions
			// check also
			dto.setReturnPeriod(
					arr[0] != null ? "'" + arr[0].toString() : null);
			dto.setDocumentType(arr[1] != null ? arr[1].toString() : null);
			dto.setSupplyType(arr[2] != null ? arr[2].toString() : null);
			dto.setDocumentNumber(arr[3] != null ? arr[3].toString() : null);
			dto.setDocumentDate(
					arr[4] != null ? "'" + arr[4].toString() : null);
			dto.setOriginalDocumentNumber(
					arr[5] != null ? arr[5].toString() : null);
			dto.setOriginalDocumentDate(
					arr[6] != null ? "'" + arr[6].toString() : null);
			dto.setReasonForCreditDebitNote(
					arr[7] != null ? arr[7].toString() : null);
			dto.setSupplierCode(arr[8] != null ? arr[8].toString() : null);
			dto.setSupplierGSTIN(arr[9] != null ? arr[9].toString() : null);
			dto.setOriginalSupplierGSTIN(
					arr[10] != null ? arr[10].toString() : null);
			dto.setSupplierName(arr[11] != null ? arr[11].toString() : null);
			dto.setRecipientGSTIN(arr[12] != null ? arr[12].toString() : null);
			dto.setPOS(arr[13] != null ? arr[13].toString() : null);
			dto.setLineNumber(arr[14] != null ? arr[14].toString() : null);
			dto.setHSNorSAC(arr[15] != null ? arr[15].toString() : null);
			dto.setItemDescription(arr[16] != null ? arr[16].toString() : null);
			dto.setItemCode(arr[17] != null ? arr[17].toString() : null);
			dto.setCategoryOfItem(arr[18] != null ? arr[18].toString() : null);
			dto.setUnitOfMeasurement(
					arr[19] != null ? arr[19].toString() : null);
			dto.setQuantity(arr[20] != null ? arr[20].toString() : null);
			dto.setInvoiceValue(arr[32] != null ? arr[32].toString() : null);
			dto.setPortCode(arr[33] != null ? arr[33].toString() : null);
			dto.setBillOfEntry(arr[34] != null ? arr[34].toString() : null);
			dto.setBillOfEntryDate(
					arr[35] != null ? "'" + arr[35].toString() : null);
			dto.setCIFValue(arr[36] != null ? arr[36].toString() : null);
			// Conditional mapping based on optedAns

			if ("A".equalsIgnoreCase(SubQuesValue)) {
				dto.setTaxableValue(
						arr[21] != null ? arr[21].toString() : null);
				dto.setIntegratedTaxRate(null);
				dto.setIntegratedTaxAmount(null);
				dto.setCentralTaxRate(null);
				dto.setCentralTaxAmount(null);
				dto.setStateUTTaxRate(null);
				dto.setStateUTTaxAmount(null);
				dto.setCessRateAdvalorem(null);
				dto.setCessAmountAdvalorem(null);
				dto.setCessRateSpecific(null);
				dto.setCessAmountSpecific(null);
				dto.setAvailableIGST(null);
				dto.setAvailableCGST(null);
				dto.setAvailableSGST(null);
				dto.setAvailableCess(null);

			} else if ("B".equalsIgnoreCase(SubQuesValue)) {
				dto.setTaxableValue(null);
				dto.setIntegratedTaxRate(
						arr[22] != null ? arr[22].toString() : null);
				dto.setIntegratedTaxAmount(
						arr[23] != null ? arr[23].toString() : null);
				dto.setCentralTaxRate(
						arr[24] != null ? arr[24].toString() : null);
				dto.setCentralTaxAmount(
						arr[25] != null ? arr[25].toString() : null);
				dto.setStateUTTaxRate(
						arr[26] != null ? arr[26].toString() : null);
				dto.setStateUTTaxAmount(
						arr[27] != null ? arr[27].toString() : null);
				dto.setCessRateAdvalorem(
						arr[28] != null ? arr[28].toString() : null);
				dto.setCessAmountAdvalorem(
						arr[29] != null ? arr[29].toString() : null);
				dto.setCessRateSpecific(
						arr[30] != null ? arr[30].toString() : null);
				dto.setCessAmountSpecific(
						arr[31] != null ? arr[31].toString() : null);
				dto.setAvailableIGST(
						arr[41] != null ? arr[41].toString() : null);
				dto.setAvailableCGST(
						arr[42] != null ? arr[42].toString() : null);
				dto.setAvailableSGST(
						arr[43] != null ? arr[43].toString() : null);
				dto.setAvailableCess(
						arr[44] != null ? arr[44].toString() : null);
			}

			else if ("C".equalsIgnoreCase(SubQuesValue)) {
				dto.setTaxableValue(
						arr[21] != null ? arr[21].toString() : null);
				dto.setIntegratedTaxRate(
						arr[22] != null ? arr[22].toString() : null);
				dto.setIntegratedTaxAmount(
						arr[23] != null ? arr[23].toString() : null);
				dto.setCentralTaxRate(
						arr[24] != null ? arr[24].toString() : null);
				dto.setCentralTaxAmount(
						arr[25] != null ? arr[25].toString() : null);
				dto.setStateUTTaxRate(
						arr[26] != null ? arr[26].toString() : null);
				dto.setStateUTTaxAmount(
						arr[27] != null ? arr[27].toString() : null);
				dto.setCessRateAdvalorem(
						arr[28] != null ? arr[28].toString() : null);
				dto.setCessAmountAdvalorem(
						arr[29] != null ? arr[29].toString() : null);
				dto.setCessRateSpecific(
						arr[30] != null ? arr[30].toString() : null);
				dto.setCessAmountSpecific(
						arr[31] != null ? arr[31].toString() : null);
				dto.setAvailableIGST(
						arr[41] != null ? arr[41].toString() : null);
				dto.setAvailableCGST(
						arr[42] != null ? arr[42].toString() : null);
				dto.setAvailableSGST(
						arr[43] != null ? arr[43].toString() : null);
				dto.setAvailableCess(
						arr[44] != null ? arr[44].toString() : null);
			}

			dto.setCustomDuty(arr[37] != null ? arr[37].toString() : null);
			dto.setEligibilityIndicator(
					arr[38] != null ? arr[38].toString() : null);
			dto.setCommonSupplyIndicator(
					arr[39] != null ? arr[39].toString() : null);
			dto.setITCReversalIdentifier(
					arr[40] != null ? arr[40].toString() : null);

			dto.setContractNumber(arr[45] != null ? arr[45].toString() : null);
			dto.setContractDate(
					arr[46] != null ? "'" + arr[46].toString() : null);
			dto.setContractValue(arr[47] != null ? arr[47].toString() : null);
			dto.setReverseChargeFlag(
					arr[48] != null ? arr[48].toString() : null);
			dto.setCRDRPreGST(arr[49] != null ? arr[49].toString() : null);
			dto.setPaymentVoucherNumber(
					arr[50] != null ? arr[50].toString() : null);
			dto.setPaymentDate(
					arr[51] != null ? "'" + arr[51].toString() : null);
			dto.setSourceIdentifier(
					arr[52] != null ? arr[52].toString() : null);
			dto.setSourceFileName(arr[53] != null ? arr[53].toString() : null);
			dto.setPlantCode(arr[54] != null ? arr[54].toString() : null);
			dto.setDivision(arr[55] != null ? arr[55].toString() : null);
			dto.setSubDivision(arr[56] != null ? arr[56].toString() : null);
			dto.setProfitCentre1(arr[57] != null ? arr[57].toString() : null);
			dto.setProfitCentre2(arr[58] != null ? arr[58].toString() : null);
			dto.setGLAccountCode(arr[59] != null ? arr[59].toString() : null);
			dto.setPurchaseVoucherNumber(
					arr[60] != null ? arr[60].toString() : null);
			dto.setPurchaseVoucherDate(
					arr[61] != null ? "'" + arr[61].toString() : null);
			dto.setUserdefinedfield1(
					arr[62] != null ? arr[62].toString() : null);
			dto.setUserdefinedfield2(
					arr[63] != null ? arr[63].toString() : null);
			dto.setUserdefinedfield3(
					arr[64] != null ? arr[64].toString() : null);

		} catch (Exception ex) {
			String msg = String.format("Error while converting dto 2A %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private Pair<String, String> createPRFile(List<Object[]> records,
			Long configId, Long entityId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		String docId = null;
		String gLReconoptedMainAns = "B";
		String gLReconoptedSubAns = "C";
		try {
			gLReconoptedMainAns = entityConfigPemtRepo.findAnsbyQuestionGLRecon(
					entityId, "Do you want to enable GL Recon Functionality?",
					"R");

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("selected GLReconOptedMainAns: %s",
						gLReconoptedMainAns);
				LOGGER.debug(msg);
			}

			if ("A".equalsIgnoreCase(gLReconoptedMainAns)) {
				gLReconoptedSubAns = entityConfigPemtRepo
						.findAnsbyQuestionGLRecon(entityId,
								"What type of GL Recon should be enabled?",
								"SR");

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"selected GLReconOptedSubAns: %s",
							gLReconoptedSubAns);
					LOGGER.debug(msg);
				}
			}
			tempDir = createTempDir(configId);
			long dbLoadStTime = System.currentTimeMillis();
			@SuppressWarnings("unchecked")
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs,"
								+ " Report Name and Data count: '%s' - '%s'",
						dbLoadTimeDiff, "PR_FILE", records.size());
				LOGGER.debug(msg);
			}

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "PR_FILE" + ".csv";
				List<PRFileUploadDto> reconDataList = new ArrayList<>();
				final String mainQuesvalueToPass = gLReconoptedMainAns;
				final String SubQuesvalueToPass = gLReconoptedSubAns;

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {

					reconDataList = records.stream()
							.map(o -> convert(o, mainQuesvalueToPass,
									SubQuesvalueToPass))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								"PR_FILE", reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility
								.getProp("gl.recon.pr.file.headers");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gl.recon.pr.file.mapping").split(",");

						ColumnPositionMappingStrategy<PRFileUploadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(PRFileUploadDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<PRFileUploadDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<PRFileUploadDto> beanWriter = builder
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
							String msg = String.format("Total Time taken to"
									+ " Generate the report is '%d' millisecs,"
									+ " Report Name and Data count:"
									+ " '%s' - '%s'", generationTimeDiff,
									"PR_FILE", records.size());
							LOGGER.debug(msg);
						}
					}

				} catch (Exception ex) {
					throw new AppException(ex);
				}
			}

			File zipFile = new File(fullPath);
			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(zipFile, "GLReconWebUploads");
			String uploadedFileName = uploadedDocName.getValue0();
			docId = uploadedDocName.getValue1();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"inside createPRFile Uploaded File Name: '%s', Document ID: '%s'",
						uploadedFileName, docId);
				LOGGER.debug(msg);
			}

			return new Pair<>(uploadedFileName, docId);
		} catch (Exception ex) {
			throw new AppException(ex);
		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private static File createTempDir(Long batchId) throws IOException {

		String tempFolderPrefix = "GL_PR" + "_" + batchId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
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

	public int getChunkValue(Long reportDownlId) {
		int chunksize = 0;
		try {
			StoredProcedureQuery storedProcQuery = entityManager
					.createStoredProcedureQuery("USP_INS_CHUNK_PR_GL_RECON");

			storedProcQuery.registerStoredProcedureParameter(
					"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter(
					"P_CHUNK_SPILIT_VAL", Integer.class, ParameterMode.IN);
			storedProcQuery.setParameter("P_REPORT_DOWNLOAD_ID", reportDownlId);
			storedProcQuery.setParameter("P_CHUNK_SPILIT_VAL", 10000);

			// storedProcQuery.execute();
			chunksize = (Integer) storedProcQuery.getSingleResult();
			// chunksize = (Integer)
			// storedProcQuery.getOutputParameterValue("VAR_CHUNK_VAL");

		} catch (Exception e) {

			glReconReportConfig.updateReconConfigStatusAndReportName(
					"RECON_FAILED", null, null, LocalDateTime.now(),
					reportDownlId);
			throw new AppException(e);
		}
		return chunksize;
	}

}
