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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.GlDumpProcessedEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlBusinessPlaceMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMappingMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlDocTypeMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlMasterSupplyTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlTaxCodeMasterEntity;
import com.ey.advisory.app.data.repositories.client.GlReconDumpPsdRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlBusinessPlaceMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMappingMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMasterRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.GlDocTypeMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlMasterSupplyTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconSrFileUploadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlTaxCodeMasterRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */
@Component("GLReconCreateMasterDataServiceImpl")
@Slf4j
public class GLReconCreateMasterDataServiceImpl {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("GlBusinessPlaceMasterRepository")
	private GlBusinessPlaceMasterRepository glBusinessPlaceMasterRepository;

	@Autowired
	@Qualifier("GlCodeMappingMasterRepository")
	private GlCodeMappingMasterRepository glCodeMappingMasterRepository;

	@Autowired
	@Qualifier("GlCodeMasterRepo")
	private GlCodeMasterRepo glCodeMasterRepo;

	@Autowired
	@Qualifier("GlMasterSupplyTypeRepository")
	private GlMasterSupplyTypeRepository glMasterSupplyTypeRepository;

	@Autowired
	@Qualifier("GlTaxCodeMasterRepo")
	private GlTaxCodeMasterRepo glTaxCodeMasterRepo;

	@Autowired
	@Qualifier("GlDocTypeMasterRepository")
	private GlDocTypeMasterRepository glDocTypeMasterRepository;

	@Autowired
	@Qualifier("GlReconDumpPsdRepository")
	private GlReconDumpPsdRepository glReconDumpPsdRepository;

	@Autowired
	@Qualifier("GlReconSrFileUploadRepository")
	private GlReconSrFileUploadRepository glReconSrFileUploadRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String GL_RECON_DUMP_ROWS = "gl.recon.dump.processed.chunk.rows";

	private static int CSV_BUFFER_SIZE = 8192;

	public void create(Long configId) {
		String userName = null;
		User user = SecurityContext.getUser();

		if (user != null) {
			userName = user.getUserPrincipalName();
		} else {
			userName = "SYSTEM";
		}
		File tempDir = null;
		try {
			tempDir = createTempDir(configId);

			generateMasterCsvFile(
					glBusinessPlaceMasterRepository.findByIsActiveTrue(),
					this::convertBusinessPlace, GlBusinessPlaceDto.class,
					"gl.recon.bp.file.headers", "gl.recon.bp.file.mapping",
					"MASTER_BUSINESS_PLACE_FILE", tempDir, configId, userName);

			generateMasterCsvFile(
					glCodeMappingMasterRepository.findByIsActiveTrue(),
					this::convertCodeMapping, GlCodeMappingDto.class,
					"gl.recon.cm.file.headers", "gl.recon.cm.file.mapping",
					"MASTER_CODE_MAPPING_FILE", tempDir, configId, userName);

			generateMasterCsvFile(glCodeMasterRepo.findByIsActiveTrue(),
					this::convertCodeMaster, GlCodeMasterDto.class,
					"gl.recon.code.file.headers", "gl.recon.code.file.mapping",
					"MASTER_CODE_FILE", tempDir, configId, userName);

			generateMasterCsvFile(
					glMasterSupplyTypeRepository.findByIsActiveTrue(),
					this::convertSupplyType, GlSupplyTypeDto.class,
					"gl.recon.supply.file.headers",
					"gl.recon.supply.file.mapping", "MASTER_SUPPLY_TYPE_FILE",
					tempDir, configId, userName);

			generateMasterCsvFile(glTaxCodeMasterRepo.findByIsActiveTrue(),
					this::convertTaxCode, GlTaxCodeDto.class,
					"gl.recon.tax.file.headers", "gl.recon.tax.file.mapping",
					"MASTER_TAX_CODE_FILE", tempDir, configId, userName);

			generateMasterCsvFile(
					glDocTypeMasterRepository.findByIsActiveTrue(),
					this::convertDocType, GlDocTypeDto.class,
					"gl.recon.doc.file.headers", "gl.recon.doc.file.mapping",
					"MASTER_DOC_TYPE_FILE", tempDir, configId, userName);

			generateMasterCsvFile(
					glReconDumpPsdRepository.findByIsDeleteFalse(),
					this::convertDumpProcessed, GlDumpProcessedDto.class,
					"gl.recon.dump.file.headers", "gl.recon.dump.file.mapping",
					"DUMP_PROCESSSED_TYPE_FILE", tempDir, configId, userName);// Chunking
			// will do

			/*
			 * String[] masterFileNames = { "MASTER_BUSINESS_PLACE_FILE",
			 * "MASTER_CODE_MAPPING_FILE", "MASTER_CODE_FILE",
			 * "MASTER_SUPPLY_TYPE_FILE", "MASTER_TAX_CODE_FILE",
			 * "MASTER_DOC_TYPE_FILE", "DUMP_PROCESSSED_TYPE_FILE" };
			 * 
			 * for (String fileName : masterFileNames) { File file = new
			 * File(tempDir, fileName + ".csv"); if (!file.exists()) {
			 * LOGGER.warn("CSV file not found: {}", file.getName()); continue;
			 * }
			 * 
			 * try { Pair<String, String> uploadedDoc = DocumentUtility
			 * .uploadFile(file, "GLReconWebUploads"); String uploadedFileName =
			 * uploadedDoc.getValue0(); String uploadedDocId =
			 * uploadedDoc.getValue1();
			 * 
			 * LOGGER.debug("Uploaded File Name: '{}', Document ID: '{}'",
			 * uploadedFileName, uploadedDocId);
			 * 
			 * GlReconSrFileUploadEntity entity = new
			 * GlReconSrFileUploadEntity(); entity.setReqId(configId);//same
			 * configid for all master file
			 * entity.setUploadedDocName(uploadedFileName);
			 * entity.setUploadedDocNumber(uploadedDocId);
			 * entity.setUserName(TenantContext.getDispatcherUserName());
			 * entity.setCreatedOn(LocalDateTime.now().toString());
			 * entity.setFileType(fileName);
			 * 
			 * glReconSrFileUploadRepository.save(entity);
			 * 
			 * } catch (Exception e) { LOGGER.error("Failed to upload file: {}",
			 * fileName + ".csv", e); } }
			 */

		} catch (Exception ex) {
			throw new AppException(ex);
		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private <I, O> void generateMasterCsvFile(List<I> rawRecords,
			Function<I, O> converter, Class<O> typeClass, String headerPropKey,
			String mappingPropKey, String fileName, File tempDir, Long configId,
			String userName) throws Exception {
		List<O> records = new ArrayList<>();

		if (!rawRecords.isEmpty()) {
			records = rawRecords.stream().map(converter)
					.collect(Collectors.toList());
		}

		String fullPath = tempDir.getAbsolutePath() + File.separator + fileName
				+ ".csv";

		try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
				CSV_BUFFER_SIZE)) {
			LOGGER.info("Writing {} records to file {}", records.size(),
					fullPath);

			String headers = commonUtility.getProp(headerPropKey);
			writer.append(headers).append("\n");

			String[] columnMappings = commonUtility.getProp(mappingPropKey)
					.split(",");

			ColumnPositionMappingStrategy<O> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(typeClass);
			mappingStrategy.setColumnMapping(columnMappings);

			StatefulBeanToCsv<O> beanWriter = new StatefulBeanToCsvBuilder<O>(
					writer).withMappingStrategy(mappingStrategy)
							.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
							.withLineEnd(CSVWriter.DEFAULT_LINE_END).build();

			beanWriter.write(records);
			LOGGER.info("Successfully wrote CSV: {}", fullPath);

		} catch (Exception e) {
			LOGGER.error("Failed to write CSV: {}", fullPath, e);
			throw new AppException("CSV generation failed for " + fileName, e);
		}

		// After the file is fully written and closed, proceed to upload
		File file = new File(fullPath);

		try {
			Pair<String, String> uploadedDoc = DocumentUtility.uploadFile(file,
					"GLReconWebUploads");
			String uploadedFileName = uploadedDoc.getValue0();
			String uploadedDocId = uploadedDoc.getValue1();

			LOGGER.debug("Uploaded File Name: '{}', Document ID: '{}'",
					uploadedFileName, uploadedDocId);

			GlReconSrFileUploadEntity entity = new GlReconSrFileUploadEntity();
			entity.setReqId(configId);
			entity.setUploadedDocName(uploadedFileName);
			entity.setUploadedDocNumber(uploadedDocId);
			entity.setUserName(userName);
			entity.setCreatedOn(LocalDateTime.now().toString());
			entity.setFileType(fileName);

			glReconSrFileUploadRepository.save(entity);

		} catch (Exception e) {
			LOGGER.error("Failed to upload file or save metadata to DB: {}",
					fullPath, e);
			throw new AppException(
					"File upload or DB save failed for " + fileName, e);
		}

	}

	private GlBusinessPlaceDto convertBusinessPlace(
			GlBusinessPlaceMasterEntity o) {
		if (o == null) {
			LOGGER.warn("convertBusinessPlace: received null object");
			return null;
		}
		GlBusinessPlaceDto dto = new GlBusinessPlaceDto();
		dto.setBusinessPlace(o.getBusinessPlace());
		dto.setBusinessArea(o.getBusinessArea());
		dto.setPlantCode(o.getPlantCode());
		dto.setProfitCentre(o.getProfitCentre());
		dto.setCostCentre(o.getCostCentre());
		dto.setGstin(o.getGstin());
		LOGGER.debug("Mapped BusinessPlaceMaster dto: {}", dto);
		return dto;
	}

	private GlCodeMappingDto convertCodeMapping(GlCodeMappingMasterEntity o) {
		if (o == null) {
			LOGGER.warn("convertCodeMapping: received null object");
			return null;
		}

		GlCodeMappingDto dto = new GlCodeMappingDto();
		dto.setBaseHeaders(o.getBaseHeaders());
		dto.setInputFileHeaders(o.getInputFileHeaders());
		LOGGER.debug("Mapped CodeMappingMaster dto: {}", dto);
		return dto;
	}

	private GlCodeMasterDto convertCodeMaster(GlCodeMasterEntity o) {
		if (o == null) {
			LOGGER.warn("convertCodeMaster: received null object");
			return null;
		}
		GlCodeMasterDto dto = new GlCodeMasterDto();
		dto.setCgstTaxGlCode(o.getCgstTaxGlCode());
		dto.setSgstTaxGlCode(o.getSgstTaxGlCode());
		dto.setIgstTaxGlCode(o.getIgstTaxGlCode());
		dto.setUgstTaxGlCode(o.getUgstTaxGlCode());
		dto.setCompensationCessGlCode(o.getCompensationCessGlCode());
		dto.setKeralaCessGlCode(o.getKeralaCessGlCode());
		dto.setRevenueGls(o.getRevenueGls());
		dto.setExpenceGls(o.getExpenceGls());
		dto.setDiffGl(o.getDiffGl());// might change
		dto.setExportGl(o.getExportGl());
		dto.setForexGlsPor(o.getForexGlsPor());
		dto.setTaxableAdvanceLiabilityGls(o.getTaxableAdvanceLiabilityGls());
		dto.setNonTaxableAdvanceLiabilityGls(
				o.getNonTaxableAdvanceLiabilityGls());
		dto.setCcAndStGls(o.getCcAndStGls());
		dto.setUnbilledRevenueGls(o.getUnbilledRevenueGls());
		dto.setBankAccGls(o.getBankAccGls());
		dto.setInputTaxGls(o.getInputTaxGls());
		dto.setFixedAssetGls(o.getFixedAssetGls());
		LOGGER.debug("Mapped CodeMaster dto: {}", dto);
		return dto;
	}

	private GlSupplyTypeDto convertSupplyType(GlMasterSupplyTypeEntity o) {
		if (o == null) {
			LOGGER.warn("convertSupplyType: received null object");
			return null;
		}
		GlSupplyTypeDto dto = new GlSupplyTypeDto();
		dto.setSupplyTypeReg(o.getSupplyTypeReg());
		dto.setSupplyTypeReg(o.getSupplyTypeReg());
		LOGGER.debug("Mapped SupplyType dto: {}", dto);
		return dto;
	}

	private GlTaxCodeDto convertTaxCode(GlTaxCodeMasterEntity o) {
		if (o == null) {
			LOGGER.warn("convertTaxCode: received null object");
			return null;
		}
		GlTaxCodeDto dto = new GlTaxCodeDto();
		dto.setTransactionTypeGl(o.getTransactionTypeGl());
		dto.setTaxCodeDescriptionMs(o.getTaxCodeDescriptionMs());
		dto.setTaxTypeMs(o.getTaxTypeMs());
		dto.setEligibilityMs(o.getEligibilityMs());
		dto.setTaxRateMs(o.getTaxRateMs());
		LOGGER.debug("Mapped TaxCodeTypeMaster dto: {}", dto);
		return dto;
	}

	private GlDocTypeDto convertDocType(GlDocTypeMasterEntity o) {
		if (o == null) {
			LOGGER.warn("convertDocType: received null object");
			return null;
		}
		GlDocTypeDto dto = new GlDocTypeDto();
		dto.setDocType(o.getDocType());
		dto.setDocTypeMs(o.getDocTypeMs());
		LOGGER.debug("Mapped DocTypeMaster dto: {}", dto);
		return dto;
	}

	private GlDumpProcessedDto convertDumpProcessed(GlDumpProcessedEntity o) {
		if (o == null) {
			LOGGER.warn("convertDumpProcessed: received null object");
			return null;
		}
		GlDumpProcessedDto dto = new GlDumpProcessedDto();
		dto.setTransactionType(o.getTransactionType());
		dto.setCompanyCode(o.getCompanyCode());
		dto.setFiscalYear(o.getFiscalYear());
		dto.setDerivedTaxPeriod(o.getDerivedTaxPeriod());
		dto.setBussinessPlace(o.getBussinessPlace());
		dto.setBusinessArea(o.getBusinessArea());
		dto.setGlAccount(o.getGlAccount());
		dto.setGlDescription(o.getGlDescription());
		dto.setText(o.getText());
		dto.setAssignmentNumber(o.getAssignmentNumber());
		dto.setErpDocType(o.getErpDocType());
		dto.setAccountingVoucherNumber(o.getAccountingVoucherNumber());
		dto.setAccountingVoucherDate(o.getAccountingVoucherDate());
		dto.setItemNumber(o.getItemNumber());// ask
		dto.setPostingKey(o.getPostingKey());
		dto.setPostingDate(o.getPostingDate());
		dto.setAmountInLocalCurrency(o.getAmountInLocalCurrency());
		dto.setLocalCurrencyCode(o.getLocalCurrencyCode());
		dto.setClearingDocNumber(o.getClearingDocNumber());
		dto.setClearingDocDate(o.getClearingDocDate());
		dto.setCustomerCode(o.getCustomerCode());
		dto.setCustomerName(o.getCustomerName());
		dto.setCustomerGstin(o.getCustomerGstin());
		dto.setSupplierCode(o.getSupplierCode());
		dto.setSupplierName(o.getSupplierName());
		dto.setSupplierGstin(o.getSupplierGstin());
		dto.setPlantCode(o.getPlantCode());
		dto.setCostCentre(o.getCostCentre());
		dto.setProfitCentre(o.getProfitCentre());
		dto.setSpecialGlIndicator(o.getSpecialGlIndicator());
		dto.setReference(o.getReference());
		dto.setAmountinDocumentCurrency(o.getAmountinDocumentCurrency());
		dto.setEffectiveExchangeRate(o.getEffectiveExchangeRate());
		dto.setDocumentCurrencyCode(o.getDocumentCurrencyCode());
		dto.setAccountType(o.getAccountType());
		dto.setTaxCode(o.getTaxCode());
		dto.setWithHoldingTaxAmount(o.getWithHoldingTaxAmount());
		dto.setWithHoldingExemptAmount(o.getWithHoldingExemptAmount());
		dto.setWithHoldingTaxBaseAmount(o.getWithHoldingTaxBaseAmount());
		dto.setInvoiceReference(o.getInvoiceReference());
		dto.setDebitCreditIndicator(o.getDebitCreditIndicator());
		dto.setPaymentDate(o.getPaymentDate());
		dto.setPaymentBlock(o.getPaymentBlock());
		dto.setPaymentReference(o.getPaymentReference());
		dto.setPaymentReference(o.getPaymentReference());
		dto.setMaterial(o.getMaterial());
		dto.setReferenceKey1(o.getReferenceKey1());
		dto.setOffSettingAccountType(o.getOffSettingAccountType());
		dto.setOffSettingAccountNumber(o.getOffSettingAccountNumber());
		dto.setDocumentHeaderText(o.getDocumentHeaderText());
		dto.setBillingDocNumber(o.getBillingDocNumber());
		dto.setBillingDocDate(o.getBillingDocDate());
		dto.setMigoNumber(o.getMigoNumber());
		dto.setMigoDate(o.getMigoDate());
		dto.setMiroNumber(o.getMiroNumber());
		dto.setMiroDate(o.getMiroDate());
		dto.setExpenseGlMapping(o.getExpenseGlMapping());
		dto.setSegment(o.getSegment());
		dto.setGeoLevel(o.getGeoLevel());
		dto.setStateName(o.getStateName());
		dto.setUserId(o.getUserId());
		dto.setParkedBy(o.getParkedBy());
		dto.setEntryDate(o.getEntryDate());
		dto.setTimeOfEntry(o.getTimeOfEntry());
		dto.setRemarks(o.getRemarks());
		dto.setUserDefinedField1(o.getUserDefinedField1());
		dto.setUserDefinedField2(o.getUserDefinedField2());
		dto.setUserDefinedField3(o.getUserDefinedField3());
		dto.setUserDefinedField4(o.getUserDefinedField4());
		dto.setUserDefinedField5(o.getUserDefinedField5());
		dto.setUserDefinedField6(o.getUserDefinedField6());
		dto.setUserDefinedField7(o.getUserDefinedField7());
		dto.setUserDefinedField8(o.getUserDefinedField8());
		dto.setUserDefinedField9(o.getUserDefinedField9());
		dto.setUserDefinedField10(o.getUserDefinedField10());
		LOGGER.debug("Mapped DumpProcessedMaster dto: {}", dto);
		return dto;
	}

	private static File createTempDir(Long batchId) throws IOException {

		String tempFolderPrefix = "GL" + "_" + batchId;
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
								+ "lead to cLOGGERging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

}
