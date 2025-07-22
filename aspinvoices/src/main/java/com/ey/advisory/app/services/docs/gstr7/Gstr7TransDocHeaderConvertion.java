package com.ey.advisory.app.services.docs.gstr7;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocItemEntity;
import com.ey.advisory.app.services.docs.DocumentKeyBuilder;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Gstr7TransDocHeaderConvertion")
@Slf4j
public class Gstr7TransDocHeaderConvertion {

	public List<Gstr7TransDocHeaderEntity> convertSRFileToOutwardTransDoc(
			Map<String, List<Object[]>> documentMap,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<Gstr7TransDocHeaderEntity> documents = new ArrayList<>();

		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			Gstr7TransDocHeaderEntity docHeader = new Gstr7TransDocHeaderEntity();
			List<Gstr7TransDocItemEntity> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				Gstr7TransDocItemEntity lineItem = new Gstr7TransDocItemEntity();

				String sourceIdentifier = obj[0] != null
						? obj[0].toString().trim() : null;
				String sourceFilename = obj[1] != null
						? obj[1].toString().trim() : null;
				String glAccountCode = obj[2] != null ? obj[2].toString().trim()
						: null;
				String division = obj[3] != null ? obj[3].toString().trim()
						: null;
				String subDivision = obj[4] != null ? obj[4].toString().trim()
						: null;
				String profitCentre1 = obj[5] != null ? obj[5].toString().trim()
						: null;
				String profitCentre2 = obj[6] != null ? obj[6].toString().trim()
						: null;
				String plantCode = obj[7] != null ? obj[7].toString().trim()
						: null;
				String returnPeriod = obj[8] != null ? obj[8].toString().trim()
						: null;
				String documentType = obj[9] != null ? obj[9].toString().trim()
						: null;
				String supplyType = obj[10] != null ? obj[10].toString().trim()
						: null;
				String deductorGSTIN = obj[11] != null
						? obj[11].toString().trim() : null;
				String documentNumber = obj[12] != null
						? obj[12].toString().trim() : null;
				String documentDateStr = obj[13] != null
						? obj[13].toString().trim() : null;
				LocalDate documentDate = DateUtil
						.parseObjToDate(documentDateStr);

				String originalDocumentNumber = obj[14] != null
						? obj[14].toString().trim() : null;
				String originalDocumentDateStr = obj[15] != null
						? obj[15].toString().trim() : null;

				LocalDate originalDocumentDate = DateUtil
						.parseObjToDate(originalDocumentDateStr);

				String deducteeGSTIN = obj[16] != null
						? obj[16].toString().trim() : null;
				String originalDeducteeGSTIN = obj[17] != null
						? obj[17].toString().trim() : null;
				String originalReturnPeriod = obj[18] != null
						? obj[18].toString().trim() : null;

				BigDecimal originalTaxableValue = null;
				if (obj[19] != null && !obj[19].toString().trim().isEmpty()) {
					originalTaxableValue = new BigDecimal(
							obj[19].toString().trim());
					originalTaxableValue = originalTaxableValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				BigDecimal originalInvoiceValue = null;
				if (obj[20] != null && !obj[20].toString().trim().isEmpty()) {
					originalInvoiceValue = new BigDecimal(
							obj[20].toString().trim());
					originalInvoiceValue = originalInvoiceValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				Integer lineNumber = null;
				if (obj[21] != null && !obj[21].toString().trim().isEmpty()) {
					lineNumber = Integer.valueOf(obj[21].toString().trim());
				}

				BigDecimal taxableValue = null;
				if (obj[22] != null && !obj[22].toString().trim().isEmpty()) {
					taxableValue = new BigDecimal(obj[22].toString().trim());
					taxableValue = taxableValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				BigDecimal igstAmount = null;
				if (obj[23] != null && !obj[23].toString().trim().isEmpty()) {
					igstAmount = new BigDecimal(obj[23].toString().trim());
					igstAmount = igstAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				BigDecimal cgstAmount = null;
				if (obj[24] != null && !obj[24].toString().trim().isEmpty()) {
					cgstAmount = new BigDecimal(obj[24].toString().trim());
					cgstAmount = cgstAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				BigDecimal sgstAmount = null;
				if (obj[25] != null && !obj[25].toString().trim().isEmpty()) {
					sgstAmount = new BigDecimal(obj[25].toString().trim());
					sgstAmount = sgstAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				BigDecimal invoiceValue = null;
				if (obj[26] != null && !obj[26].toString().trim().isEmpty()) {
					invoiceValue = new BigDecimal(obj[26].toString().trim());
					invoiceValue = invoiceValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				String contractNumber = obj[27] != null
						? obj[27].toString().trim() : null;
				String contractDateStr = obj[28] != null
						? obj[28].toString().trim() : null;

				LocalDate contractDate = DateUtil
						.parseObjToDate(contractDateStr);

				BigDecimal contractValue = null;
				if (obj[29] != null && !obj[29].toString().trim().isEmpty()) {
					contractValue = new BigDecimal(obj[29].toString().trim());
					contractValue = contractValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}

				String paymentAdviceNumber = obj[30] != null
						? obj[30].toString().trim() : null;
				String paymentAdviceDateStr = obj[31] != null
						? obj[31].toString().trim() : null;

				LocalDate paymentAdviceDate = DateUtil
						.parseObjToDate(paymentAdviceDateStr);

				// User Defined Fields (UDFs)
				String udf1 = obj[32] != null ? obj[32].toString().trim()
						: null;
				String udf2 = obj[33] != null ? obj[33].toString().trim()
						: null;
				String udf3 = obj[34] != null ? obj[34].toString().trim()
						: null;
				String udf4 = obj[35] != null ? obj[35].toString().trim()
						: null;
				String udf5 = obj[36] != null ? obj[36].toString().trim()
						: null;
				String udf6 = obj[37] != null ? obj[37].toString().trim()
						: null;
				String udf7 = obj[38] != null ? obj[38].toString().trim()
						: null;
				String udf8 = obj[39] != null ? obj[39].toString().trim()
						: null;
				String udf9 = obj[40] != null ? obj[40].toString().trim()
						: null;
				String udf10 = obj[41] != null ? obj[41].toString().trim()
						: null;
				String udf11 = obj[42] != null ? obj[42].toString().trim()
						: null;
				String udf12 = obj[43] != null ? obj[43].toString().trim()
						: null;
				String udf13 = obj[44] != null ? obj[44].toString().trim()
						: null;
				String udf14 = obj[45] != null ? obj[45].toString().trim()
						: null;
				String udf15 = obj[46] != null ? obj[46].toString().trim()
						: null;


				/**
				 * Start - Outward Document Structural Validation Error
				 * Correction Implementation
				 */

				lineItem.setLineItemNumber(lineNumber);
				lineItem.setGlAccountCode(
						GenUtil.trimString(glAccountCode, 25));
				lineItem.setSubDivision(GenUtil.trimString(subDivision, 20));
				lineItem.setPlantCode(GenUtil.trimString(plantCode, 20));
				lineItem.setTaxableValue(taxableValue);
				lineItem.setIgstAmt(igstAmount);
				lineItem.setCgstAmt(cgstAmount);
				lineItem.setSgstAmt(sgstAmount);

//				lineItem.setOriginalTaxableValue(originalTaxableValue);
				lineItem.setUserdefinedField11(GenUtil.trimString(udf11, 500));
				lineItem.setUserdefinedField12(GenUtil.trimString(udf12, 500));
				lineItem.setUserdefinedField13(GenUtil.trimString(udf13, 500));
				lineItem.setUserdefinedField14(GenUtil.trimString(udf14, 500));
				lineItem.setUserdefinedField15(GenUtil.trimString(udf15, 500));
				lineItem.setTaxperiod(returnPeriod);
				Integer derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(returnPeriod);

				Integer originalDerivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(originalReturnPeriod);

				lineItem.setDerivedTaxperiod(derivedRetPeriod);
				lineItem.setCreatedBy("SYSTEM");
				lineItem.setCreatedOn(LocalDateTime.now());

				lineItems.add(lineItem); // Add Line Items

				docHeader.setSourceIdentifier(
						GenUtil.trimString(sourceIdentifier, 25));
				docHeader.setSourceFilename(
						GenUtil.trimString(sourceFilename, 50));
				docHeader.setDivision(GenUtil.trimString(division, 20));
				docHeader.setProfitCentre1(
						GenUtil.trimString(profitCentre1, 20));
				docHeader.setProfitCentre2(
						GenUtil.trimString(profitCentre2, 20));
				docHeader.setReturnPeriod(returnPeriod);
				docHeader.setDerivedRetPeriod(derivedRetPeriod);
				docHeader.setOriginalDerivedRetPeriod(originalDerivedRetPeriod);
				docHeader.setDocType(documentType);
				docHeader.setSupplyType(supplyType);
				docHeader.setDeductorGstin(deductorGSTIN);
				docHeader.setDocNum(documentNumber);
				docHeader.setDocDate(documentDate);
				docHeader.setFiYear(GenUtil.getFinYear(documentDate));
				docHeader.setOriginalDocNum(originalDocumentNumber);
				docHeader.setOriginalDocDate(originalDocumentDate);
				docHeader.setDeducteeGstin(deducteeGSTIN);
				docHeader.setOriginalDeducteeGstin(originalDeducteeGSTIN);
				docHeader.setOriginalReturnPeriod(originalReturnPeriod);
				docHeader.setInvoiceValue(invoiceValue);
				docHeader.setTaxableValue(taxableValue);
				docHeader.setOriginalTaxableValue(originalTaxableValue);
				docHeader.setOriginalInvoiceValue(originalInvoiceValue);
				docHeader.setIgstAmt(igstAmount);
				docHeader.setCgstAmt(cgstAmount);
				docHeader.setSgstAmt(sgstAmount);
				docHeader.setContractNumber(
						GenUtil.trimString(contractNumber, 20));

				docHeader.setContractDate(contractDate);

				docHeader.setContractValue(contractValue);
				docHeader.setPaymentAdviceNumber(
						GenUtil.trimString(paymentAdviceNumber, 20));
				docHeader.setPaymentAdviceDate(paymentAdviceDate);
				docHeader.setUserdefinedField1(GenUtil.trimString(udf1, 500));
				docHeader.setUserdefinedField2(GenUtil.trimString(udf2, 500));
				docHeader.setUserdefinedField3(GenUtil.trimString(udf3, 500));
				docHeader.setUserdefinedField4(GenUtil.trimString(udf4, 500));
				docHeader.setUserdefinedField5(GenUtil.trimString(udf5, 500));
				docHeader.setUserdefinedField6(GenUtil.trimString(udf6, 500));
				docHeader.setUserdefinedField7(GenUtil.trimString(udf7, 500));
				docHeader.setUserdefinedField8(GenUtil.trimString(udf8, 500));
				docHeader.setUserdefinedField9(GenUtil.trimString(udf9, 500));
				docHeader.setUserdefinedField10(GenUtil.trimString(udf10, 500));

				docHeader.setCreatedBy("SYSTEM");
				docHeader.setCreatedOn(LocalDateTime.now());
				
				LOGGER.error("doCkey {} ", key );
				docHeader.setDocKey(key);
				
				docHeader.setFileId(fileStatus.getId());
				// docHeader.setUserId(...);
				// docHeader.setFiYear(...);
				// docHeader.setDerivedRetPeriod(...);
				// docHeader.setOriginalDocPeriod(...);
				// docHeader.setOriginalDerivedRetPeriod(...);
				// docHeader.setDataOriginTypeCode(...);
				// docHeader.setBatchId(...);
				// docHeader.setIsError(...);
				// docHeader.setIsProcessed(...);
				// docHeader.setIsInformation(...);
				// docHeader.setIsSavedToGstn(...);
				// docHeader.setIsSentToGstn(...);
				// docHeader.setIsDelete(...);
				// docHeader.setGstnError(...);
				// docHeader.setDocKey(...);
				// docHeader.setFileId(...);
				// docHeader.setSentToGstnDate(...);
				// docHeader.setSavedToGstnDate(...);
				// docHeader.setErrorCodes(...);
				// docHeader.setInformationCodes(...);
				// docHeader.setPayloadId(...);
				// docHeader.setIsFiled(...);
				// docHeader.setFiledDate(...);
				// docHeader.setGstnSaveRefId(...);
				// docHeader.setModifiedBy(...);
				// docHeader.setModifiedOn(...);

				String source = fileStatus.getSource();
				if (source != null && source
						.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					docHeader.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.SFTP
									.getDataOriginTypeCode());
				} else {
					docHeader.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
									.getDataOriginTypeCode());
				}
				docHeader.setLineItems(lineItems);
				documents.add(docHeader);
				lineItems.forEach(item -> {
					item.setDocument(docHeader);
				});
			}
		});
		return documents;
	}
}
