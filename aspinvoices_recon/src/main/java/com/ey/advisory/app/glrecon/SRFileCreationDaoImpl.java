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
 * @author Sakshi.jain
 *
 */
@Component("SRFileCreationDaoImpl")
@Slf4j
public class SRFileCreationDaoImpl implements SRFileCreationDao {

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
						.createStoredProcedureQuery("USP_DISP_CHUNK_GL_RECON");
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
				Pair<String, String> uploadedDocIdAndName = createSRFile(list,
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

	public String createQueryString() {
		// need to add the fields in the query string

		return " SELECT  " + " HDR.SOURCE_IDENTIFIER AS SourceIdentifier, "
				+ " HDR.SOURCE_FILENAME AS SourceFileName, "
				+ " ITM.GLCODE_TAXABLEVALUE AS GLAccountCode, "
				+ " HDR.DIVISION AS Division, "
				+ " ITM.SUB_DIVISION AS SubDivision, "
				+ " HDR.PROFIT_CENTRE AS ProfitCentre1, "
				+ " HDR.PROFIT_CENTRE2 AS ProfitCentre2, "
				+ " HDR.PLANT_CODE AS PlantCode, "
				+ " HDR.RETURN_PERIOD AS ReturnPeriod, "
				+ " HDR.SUPPLIER_GSTIN AS SupplierGSTIN, "
				+ " HDR.DOC_TYPE AS DocumentType, "
				+ " HDR.SUPPLY_TYPE AS SupplyType, "
				+ " HDR.DOC_NUM AS DocumentNumber, "
				+ " HDR.DOC_DATE AS DocumentDate, "
				+ " HDR.PRECEEDING_INV_NUM AS OriginalDocumentNumber, "
				+ " HDR.PRECEEDING_INV_DATE AS OriginalDocumentDate, "
				+ " HDR.CRDR_PRE_GST AS CRDRPreGST, "
				+ " ITM.ITM_NO AS LineNumber, "
				+ " HDR.CUST_GSTIN AS CustomerGSTIN, "
				+ " HDR.CUST_SUPP_TYPE AS UINorComposition, "
				+ " HDR.ORIGINAL_CUST_GSTIN AS OriginalCustomerGSTIN, "
				+ " HDR.CUST_SUPP_NAME AS CustomerName, "
				+ " HDR.CUST_SUPP_CODE AS CustomerCode, "
				+ " HDR.BILL_TO_STATE AS BillToState, "
				+ " HDR.SHIP_TO_STATE AS ShipToState, " + " HDR.POS AS POS, "
				+ " HDR.SHIP_PORT_CODE AS PortCode, "
				+ " HDR.SHIP_BILL_NUM AS ShippingBillNumber, "
				+ " HDR.SHIP_BILL_DATE AS ShippingBillDate, " + " ITM.FOB, "
				+ " ITM.EXPORT_DUTY AS ExportDuty, "
				+ " ITM.ITM_HSNSAC AS HSNorSAC, "
				+ " ITM.PRODUCT_CODE AS ProductCode, "
				+ " ITM.ITM_DESCRIPTION AS ProductDescription, "
				+ " ITM.ITM_TYPE AS CategoryOfProduct, "
				+ " ITM.ITM_UQC AS UnitOfMeasurement, "
				+ " ITM.ITM_QTY AS Quantity, "
				+ " ITM.TAXABLE_VALUE AS TaxableValue, "
				+ " ITM.IGST_RATE AS IntegratedTaxRate, "
				+ " ITM.IGST_AMT AS IntegratedTaxAmount, "
				+ " ITM.CGST_RATE AS CentralTaxRate, "
				+ " ITM.CGST_AMT AS CentralTaxAmount, "
				+ " ITM.SGST_RATE AS StateUTTaxRate, "
				+ " ITM.SGST_AMT AS StateUTTaxAmount, "
				+ " ITM.CESS_RATE_ADVALOREM AS CessRateAdvalorem, "
				+ " ITM.CESS_AMT_ADVALOREM AS CessAmountAdvalorem, "
				+ " ITM.CESS_RATE_SPECIFIC AS CessRateSpecific, "
				+ " ITM.CESS_AMT_SPECIFIC AS CessAmountSpecific, "
				+ " HDR.DOC_AMT AS InvoiceValue, "
				+ " HDR.REVERSE_CHARGE AS ReverseChargeFlag, "
				+ " ITM.TCS_FLAG AS TCSFlag, "
				+ " HDR.ECOM_GSTIN AS eComGSTIN, "
				+ " ITM.ITC_FLAG AS ITCFlag, "
				+ " ITM.CRDR_REASON AS ReasonForCreditDebitNote, "
				+ " HDR.ACCOUNTING_VOUCHER_NUM AS AccountingVoucherNumber, "
				+ " HDR.ACCOUNTING_VOUCHER_DATE AS AccountingVoucherDate, "
				+ " ITM.USERDEFINED_FIELD1 AS Userdefinedfield1, "
				+ " ITM.USERDEFINED_FIELD2 AS Userdefinedfield2, "
				+ " ITM.USERDEFINED_FIELD3 AS Userdefinedfield3 "
				+ " FROM ANX_OUTWARD_DOC_HEADER HDR  "
				+ " INNER JOIN ANX_OUTWARD_DOC_ITEM ITM   "
				+ " ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  "
				+ " AND HDR.ASP_INVOICE_STATUS=2   "
				+ " AND HDR.SUPPLIER_GSTIN IN (:gstins)  AND HDR.IS_DELETE=FALSE "
				+ " AND HDR.DERIVED_RET_PERIOD BETWEEN (:fromTaxPerd) AND (:toTaxPerd)  ";
	}

	private SRFileUploadDto convert(Object[] arr, String MainQuesValue,String SubQuesValue) {

		SRFileUploadDto dto = new SRFileUploadDto();
		try {

			// write the setter method of SRFILEUploadDto with null conditions
			// check also
			dto.setSourceIdentifierReg(
					arr[0] != null ? arr[0].toString() : null);
			dto.setSourceFileNameReg(arr[1] != null ? arr[1].toString() : null);
			dto.setGLAccountCodeReg(arr[2] != null ? arr[2].toString() : null);
			dto.setDivisionReg(arr[3] != null ? arr[3].toString() : null);
			dto.setSubDivisionReg(arr[4] != null ? arr[4].toString() : null);
			dto.setProfitCentre1Reg(arr[5] != null ? arr[5].toString() : null);
			dto.setProfitCentre2Reg(arr[6] != null ? arr[6].toString() : null);
			dto.setPlantCodeReg(arr[7] != null ? arr[7].toString() : null);
			dto.setReturnPeriodReg(
					arr[8] != null ? "'" + arr[8].toString() : null);
			dto.setSupplierGSTINReg(arr[9] != null ? arr[9].toString() : null);
			dto.setDocumentTypeReg(arr[10] != null ? arr[10].toString() : null);
			dto.setSupplyTypeReg(arr[11] != null ? arr[11].toString() : null);
			dto.setDocumentNumberReg(
					arr[12] != null ? arr[12].toString() : null);
			dto.setDocumentDateReg(
					arr[13] != null ? "'" + arr[13].toString() : null);
			dto.setOriginalDocumentNumberReg(
					arr[14] != null ? arr[14].toString() : null);
			dto.setOriginalDocumentDateReg(
					arr[15] != null ? arr[15].toString() : null);
			dto.setCRDRPreGSTReg(arr[16] != null ? arr[16].toString() : null);
			dto.setLineNumberReg(arr[17] != null ? arr[17].toString() : null);
			dto.setCustomerGSTINReg(
					arr[18] != null ? arr[18].toString() : null);
			dto.setUINorCompositionReg(
					arr[19] != null ? arr[19].toString() : null);
			dto.setOriginalCustomerGSTINReg(
					arr[20] != null ? arr[20].toString() : null);
			dto.setCustomerNameReg(arr[21] != null ? arr[21].toString() : null);
			dto.setCustomerCodeReg(arr[22] != null ? arr[22].toString() : null);
			dto.setBillToStateReg(arr[23] != null ? arr[23].toString() : null);
			dto.setShipToStateReg(arr[24] != null ? arr[24].toString() : null);
			dto.setPOSReg(arr[25] != null ? arr[25].toString() : null);
			dto.setPortCodeReg(arr[26] != null ? arr[26].toString() : null);
			dto.setShippingBillNumberReg(
					arr[27] != null ? arr[27].toString() : null);
			dto.setShippingBillDateReg(
					arr[28] != null ? "'" + arr[28].toString() : null);
			dto.setFOBReg(arr[29] != null ? arr[29].toString() : null);
			dto.setExportDutyReg(arr[30] != null ? arr[30].toString() : null);
			dto.setHSNorSACReg(arr[31] != null ? arr[31].toString() : null);
			dto.setProductCodeReg(arr[32] != null ? arr[32].toString() : null);
			dto.setProductDescriptionReg(
					arr[33] != null ? arr[33].toString() : null);
			dto.setCategoryOfProductReg(
					arr[34] != null ? arr[34].toString() : null);
			dto.setUnitOfMeasurementReg(
					arr[35] != null ? arr[35].toString() : null);
			dto.setQuantityReg(arr[36] != null ? arr[36].toString() : null);
			// Conditional mapping based on optedAns
			
				if ("A".equalsIgnoreCase(SubQuesValue)) {
					dto.setTaxableValueReg(
							arr[37] != null ? arr[37].toString() : null);
					dto.setIntegratedTaxRateReg(null);
					dto.setIntegratedTaxAmountReg(null);
					dto.setCentralTaxRateReg(null);
					dto.setCentralTaxAmountReg(null);
					dto.setStateUTTaxRateReg(null);
					dto.setStateUTTaxAmountReg(null);
					dto.setCessRateAdvaloremReg(null);
					dto.setCessAmountAdvaloremReg(null);
					dto.setCessRateSpecificReg(null);
					dto.setCessAmountSpecificReg(null);
				} else if ("B".equalsIgnoreCase(SubQuesValue)) {
					dto.setTaxableValueReg(null);
					dto.setIntegratedTaxRateReg(
							arr[38] != null ? arr[38].toString() : null);
					dto.setIntegratedTaxAmountReg(
							arr[39] != null ? arr[39].toString() : null);
					dto.setCentralTaxRateReg(
							arr[40] != null ? arr[40].toString() : null);
					dto.setCentralTaxAmountReg(
							arr[41] != null ? arr[41].toString() : null);
					dto.setStateUTTaxRateReg(
							arr[42] != null ? arr[42].toString() : null);
					dto.setStateUTTaxAmountReg(
							arr[43] != null ? arr[43].toString() : null);
					dto.setCessRateAdvaloremReg(
							arr[44] != null ? arr[44].toString() : null);
					dto.setCessAmountAdvaloremReg(
							arr[45] != null ? arr[45].toString() : null);
					dto.setCessRateSpecificReg(
							arr[46] != null ? arr[46].toString() : null);
					dto.setCessAmountSpecificReg(
							arr[47] != null ? arr[47].toString() : null);
				} else if ("C".equalsIgnoreCase(SubQuesValue)) {
					dto.setTaxableValueReg(
							arr[37] != null ? arr[37].toString() : null);
					dto.setIntegratedTaxRateReg(
							arr[38] != null ? arr[38].toString() : null);
					dto.setIntegratedTaxAmountReg(
							arr[39] != null ? arr[39].toString() : null);
					dto.setCentralTaxRateReg(
							arr[40] != null ? arr[40].toString() : null);
					dto.setCentralTaxAmountReg(
							arr[41] != null ? arr[41].toString() : null);
					dto.setStateUTTaxRateReg(
							arr[42] != null ? arr[42].toString() : null);
					dto.setStateUTTaxAmountReg(
							arr[43] != null ? arr[43].toString() : null);
					dto.setCessRateAdvaloremReg(
							arr[44] != null ? arr[44].toString() : null);
					dto.setCessAmountAdvaloremReg(
							arr[45] != null ? arr[45].toString() : null);
					dto.setCessRateSpecificReg(
							arr[46] != null ? arr[46].toString() : null);
					dto.setCessAmountSpecificReg(
							arr[47] != null ? arr[47].toString() : null);
				}
			 
			
		
			dto.setInvoiceValueReg(arr[48] != null ? arr[48].toString() : null);
			dto.setReverseChargeFlagReg(
					arr[49] != null ? arr[49].toString() : null);
			dto.setTCSFlagReg(arr[50] != null ? arr[50].toString() : null);
			dto.setEComGSTINReg(arr[51] != null ? arr[51].toString() : null);
			dto.setITCFlagReg(arr[52] != null ? arr[52].toString() : null);
			dto.setReasonForCreditDebitNoteReg(
					arr[53] != null ? arr[53].toString() : null);
			dto.setAccountingVoucherNumberReg(
					arr[54] != null ? arr[54].toString() : null);
			dto.setAccountingVoucherDateReg(
					arr[55] != null ? arr[55].toString() : null);
			dto.setUserdefinedfield1Reg(
					arr[56] != null ? arr[56].toString() : null);
			dto.setUserdefinedfield2Reg(
					arr[57] != null ? arr[57].toString() : null);
			dto.setUserdefinedfield3Reg(
					arr[58] != null ? arr[58].toString() : null);

		} catch (Exception ex) {
			String msg = String.format("Error while converting dto 2A %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private Pair<String, String> createSRFile(List<Object[]> records,
			Long configId, Long entityId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		String docId = null;
		String gLReconoptedMainAns="B";
		String gLReconoptedSubAns = "C";
		try {
			 gLReconoptedMainAns = entityConfigPemtRepo
						.findAnsbyQuestionGLRecon(entityId,
								"Do you want to enable GL Recon Functionality?",
								"R");
			 
			 if (LOGGER.isDebugEnabled()) {
				    String msg = String.format("selected GLReconOptedMainAns: %s", gLReconoptedMainAns);
				    LOGGER.debug(msg);
				}

				if ("A".equalsIgnoreCase(gLReconoptedMainAns)) {
					gLReconoptedSubAns = entityConfigPemtRepo
							.findAnsbyQuestionGLRecon(entityId,
									"What type of GL Recon should be enabled?",
									"SR");
					
					  if (LOGGER.isDebugEnabled()) {
					        String msg = String.format("selected GLReconOptedSubAns: %s", gLReconoptedSubAns);
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
						dbLoadTimeDiff, "SR_FILE", records.size());
				LOGGER.debug(msg);
			}
			

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "SR_FILE" + ".csv";
				List<SRFileUploadDto> reconDataList = new ArrayList<>();
				final String mainQuesvalueToPass = gLReconoptedMainAns;
				final String SubQuesvalueToPass = gLReconoptedSubAns;
			
				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {

					reconDataList = records.stream()
							.map(o -> convert(o, mainQuesvalueToPass,SubQuesvalueToPass))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								"SR_FILE", reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility
								.getProp("gl.recon.sr.file.headers");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gl.recon.sr.file.mapping").split(",");

						ColumnPositionMappingStrategy<SRFileUploadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(SRFileUploadDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<SRFileUploadDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<SRFileUploadDto> beanWriter = builder
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
									"SR_FILE", records.size());
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
						"inside createSRFile Uploaded File Name: '%s', Document ID: '%s'",
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

		String tempFolderPrefix = "GL_SR" + "_" + batchId;
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
					.createStoredProcedureQuery("USP_INS_CHUNK_GL_RECON");

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
