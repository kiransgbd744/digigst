package com.ey.advisory.app.services.docs.gstr7;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrItemEntity;
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
@Service("Gstr7TransDocErrDocConvertion")
@Slf4j
public class Gstr7TransDocErrDocConvertion {

	public List<Gstr7TransDocErrHeaderEntity> convertSRFileToGstr7TransError(
			Map<String, List<Object[]>> errDocMapObj,
			Gstr1FileStatusEntity fileStatus, String userName) {
		LOGGER.error("Gstr7TransDocErrDocConvertion "
				+ "convertSRFileToOutWardTransError Begining");
		List<Gstr7TransDocErrHeaderEntity> errorHeaders = new ArrayList<>();
		try {
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				LOGGER.error("Entry Value {} ", entry.getValue());
				List<Object[]> objs = entry.getValue();
				Gstr7TransDocErrHeaderEntity errorDocument = new Gstr7TransDocErrHeaderEntity();
				List<Gstr7TransDocErrItemEntity> lineItems = new ArrayList<>();
				String source = fileStatus.getSource();
				for (Object[] obj : objs) {

					Gstr7TransDocErrItemEntity lineItem = new Gstr7TransDocErrItemEntity();

					String sourceIdentifier = getWithValue(obj[0]);
					String sourceFilename = getWithValue(obj[1]);
					String glAccountCode = getWithValue(obj[2]);
					String division = getWithValue(obj[3]);
					String subDivision = getWithValue(obj[4]);
					String profitCentre1 = getWithValue(obj[5]);
					String profitCentre2 = getWithValue(obj[6]);
					String plantCode = getWithValue(obj[7]);
					String returnPeriod = getWithValue(obj[8]);
					
//					String returnPeriod = obj[8] != null ? obj[8].toString().trim()
//							: null;
					
					String documentType = getWithValue(obj[9]);
					String supplyType = getWithValue(obj[10]);
					String deductorGstin = getWithValue(obj[11]);
					String documentNumber = getWithValue(obj[12]);
					String documentDate = getWithValue(obj[13]);
					String originalDocumentNumber = getWithValue(obj[14]);
					String originalDocumentDate = getWithValue(obj[15]);
					String deducteeGstin = getWithValue(obj[16]);
					String originalDeducteeGstin = getWithValue(obj[17]);
					String originalReturnPeriod = getWithValue(obj[18]);
					String originalTaxableValue = getWithValue(obj[19]);
					String originalInvoiceValue = getWithValue(obj[20]);

					// Line Items
					String lineNumber = getWithValue(obj[21]);
					String taxableValue = getWithValue(obj[22]);
					String igstAmount = getWithValue(obj[23]);
					String cgstAmount = getWithValue(obj[24]);
					String sgstAmount = getWithValue(obj[25]);
					String invoiceValue = getWithValue(obj[26]);
					String contractNumber = getWithValue(obj[27]);
					String contractDate = getWithValue(obj[28]);
					String contractValue = getWithValue(obj[29]);
					String paymentAdviceNumber = getWithValue(obj[30]);
					String paymentAdviceDate = getWithValue(obj[31]);
					// usd
					String userDefined1 = getWithValue(obj[32]);
					String userDefined2 = getWithValue(obj[33]);
					String userDefined3 = getWithValue(obj[34]);
					String userDefined4 = getWithValue(obj[35]);
					String userDefined5 = getWithValue(obj[36]);
					String userDefined6 = getWithValue(obj[37]);
					String userDefined7 = getWithValue(obj[38]);
					String userDefined8 = getWithValue(obj[39]);
					String userDefined9 = getWithValue(obj[40]);
					String userDefined10 = getWithValue(obj[41]);
					String userDefined11 = getWithValue(obj[42]);
					String userDefined12 = getWithValue(obj[43]);
					String userDefined13 = getWithValue(obj[44]);
					String userDefined14 = getWithValue(obj[45]);
					String userDefined15 = getWithValue(obj[46]);

					Integer derivedRetPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					if (derivedRetPeriod != null) {
						errorDocument.setDerivedRetPeriod(derivedRetPeriod);
						lineItem.setDerRetPeriod(derivedRetPeriod);
					} else {
						errorDocument.setDerivedRetPeriod(999999);
						lineItem.setDerRetPeriod(derivedRetPeriod);
					}

					/**
					 * Start - Outward Document Structural Validation Error
					 * Correction Implementation
					 */

					lineItem.setGlAccountCode(glAccountCode);
					lineItem.setSubDivision(subDivision);
					lineItem.setLineItemNumber(lineNumber);
					lineItem.setTaxableValue(taxableValue);
					lineItem.setIgstAmt(igstAmount);
					lineItem.setCgstAmt(cgstAmount);
					lineItem.setSgstAmt(sgstAmount);

					lineItem.setUserdefinedField11(userDefined11);
					lineItem.setUserdefinedField12(userDefined12);
					lineItem.setUserdefinedField13(userDefined13);
					lineItem.setUserdefinedField14(userDefined14);
					lineItem.setUserdefinedField15(userDefined15);
					lineItem.setIsError("true");
					lineItem.setIsProcessed("false");

					int maxlength = 99;
					if (key.length() > maxlength) {
						lineItem.setDocKey(key.substring(0, maxlength));
					} else {
						lineItem.setDocKey(key);
					}
					lineItem.setCreatedBy(userName);
					lineItem.setCreatedOn(LocalDateTime.now());

					/*
					 * Headers into Line Items
					 */
					lineItems.add(lineItem); // Add Line Items

					errorDocument.setSourceIdentifier(sourceIdentifier);
					errorDocument.setSourceFilename(sourceFilename);
					errorDocument.setDivision(division);
					errorDocument.setProfitCentre1(profitCentre1);
					errorDocument.setProfitCentre2(profitCentre2);
					errorDocument.setPlantCode(plantCode);
					errorDocument.setReturnPeriod(returnPeriod);
					errorDocument.setDocType(documentType);
					errorDocument.setSupplyType(supplyType);
					errorDocument.setDeductorGstin(deductorGstin);
					errorDocument.setDocNum(documentNumber);
					LocalDate localDocDate = DateUtil
							.parseObjToDate(documentDate);
					errorDocument.setDocDate(localDocDate != null
							? localDocDate.toString() : documentDate);
					errorDocument.setOriginalDocNum(originalDocumentNumber);
					LocalDate localOrigDocDate = DateUtil
							.parseObjToDate(originalDocumentDate);
					errorDocument.setOriginalDocDate(localOrigDocDate != null
							? localOrigDocDate.toString()
							: originalDocumentDate);
					errorDocument.setDeducteeGstin(deducteeGstin);
					errorDocument
							.setOriginalDeducteeGstin(originalDeducteeGstin);
					errorDocument.setOriginalReturnPeriod(originalReturnPeriod);
					errorDocument.setOriginalTaxableValue(originalTaxableValue);
					errorDocument.setOriginalInvoiceValue(originalInvoiceValue);
					errorDocument.setInvoiceValue(invoiceValue);
					errorDocument.setContractNumber(contractNumber);
					errorDocument.setContractValue(contractValue);
					errorDocument.setContractDate(contractDate);
					errorDocument.setPaymentAdviceDate(paymentAdviceDate);
					errorDocument.setPaymentAdviceNumber(paymentAdviceNumber);
					errorDocument.setFileId(fileStatus.getId());
					// errorDocument.setSection(errorDocument.getDocType() !
					// =null ? errorDocument.getDGSTConstants.INV ?
					// GSTConstants.TDS : GSTConstants.TDSA);
					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.SFTP
										.getDataOriginTypeCode());
					} else {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
										.getDataOriginTypeCode());
					}
					errorDocument.setCreatedBy(userName);
					errorDocument.setIsError("true");
					errorDocument.setIsProcessed("false");
					errorDocument.setIsDelete("false");
					errorDocument.setUserdefinedField1(userDefined1);
					errorDocument.setUserdefinedField2(userDefined2);
					errorDocument.setUserdefinedField3(userDefined3);
					errorDocument.setUserdefinedField4(userDefined4);
					errorDocument.setUserdefinedField5(userDefined5);
					errorDocument.setUserdefinedField6(userDefined6);
					errorDocument.setUserdefinedField7(userDefined7);
					errorDocument.setUserdefinedField8(userDefined8);
					errorDocument.setUserdefinedField9(userDefined9);
					errorDocument.setUserdefinedField10(userDefined10);

					if (key.length() > maxlength) {
						errorDocument.setDocKey(key.substring(0, maxlength));
					} else {
						errorDocument.setDocKey(key);
					}
				}
				errorDocument.setLineItems(lineItems);
				errorDocument.setCreatedOn(LocalDateTime.now());
				errorHeaders.add(errorDocument);

				lineItems.forEach(item -> {
					item.setDocument(errorDocument);
				});
			});

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
		return errorHeaders;
	}

	private String getWithValue(Object obj) {
		String value = obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj.toString().trim()) : null;
		return value;
	}
}
