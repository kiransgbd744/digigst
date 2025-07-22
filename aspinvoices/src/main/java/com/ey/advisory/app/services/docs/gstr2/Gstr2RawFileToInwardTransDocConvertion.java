package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr2FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr2RawFileToInwardTransDocConvertion")
public class Gstr2RawFileToInwardTransDocConvertion {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2RawFileToInwardTransDocConvertion.class);

	private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyyMMdd");

	public List<InwardTransDocument> convertGstr2RawFileToInwardTransDoc(
			Map<String, List<Object[]>> documentMap,
			InwardDocumentKeyBuilder gstr2documentKeyBuilder,
			Gstr2FileStatusEntity fileStatus) {
		List<InwardTransDocument> documents = new ArrayList<>();

		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			InwardTransDocument document = new InwardTransDocument();
			List<InwardTransDocLineItem> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				// TO-DO Structural Validations
				InwardTransDocLineItem lineItem = new InwardTransDocLineItem();
				String sourceIdentifier = (String) obj[0];
				String sourceFileName = (String) obj[1];
				String glAccountCode = (String) obj[2];
				String division = (String) obj[3];
				String subDivision = (String) obj[4];
				String profitCentre = (String) obj[5];
				String profitCentre2 = (String) obj[6];
				String plantCode = (String) obj[7];
				String returnPeriod = (String) obj[8];
				String rgstin = (String) obj[9];
				String docType = (String) obj[10];
				String supplyType = (String) obj[11];
				String docNo = (String) obj[12];
				String docDate = gstr2documentKeyBuilder
						.convertDateToStr(obj[13]);
				LocalDate localDocDate = null;
				if (!docDate.equals("")) {
					localDocDate = LocalDate.parse(docDate,
							DATE_TIME_FORMATTER);
				}
				String origDocNo = (String) obj[14];
				String origDocDate = gstr2documentKeyBuilder
						.convertDateToStr(obj[15]);
				LocalDate localOrigDocDate = null;
				if (!origDocDate.equals("")) {
					localOrigDocDate = LocalDate.parse(origDocDate,
							DATE_TIME_FORMATTER);
				}
				String crDrPreGst = (String) obj[16];

				// int lineNo = (Integer) obj[17];
				Integer lineNo = null;
				if (obj[17] instanceof String) {
					String lineNoStr = String.valueOf(obj[17]);
					lineNo = Integer.valueOf(lineNoStr);
				}
				if (obj[17] instanceof Integer) {
					lineNo = (Integer) obj[17];
				}
				String sgstin = (String) obj[18];
				String origSgstin = (String) obj[19];
				String suppName = (String) obj[20];
				String suppCode = (String) obj[21];
				String pos = (String.valueOf(obj[22])).trim();
				String portCode = (String) obj[23];
				String billOfEntryNo = (String) obj[24];
				/*Integer billOfEntryNo = 0;
				if (obj[24] != null && !obj[24].toString().trim().isEmpty()) {
					BigInteger billOfEntry = new BigInteger(
							String.valueOf(obj[24]));
					billOfEntryNo = billOfEntry.intValue();
				}*/

				String billOfEntryDate = gstr2documentKeyBuilder
						.convertDateToStr(obj[25]);
				LocalDateTime localbillOfEntryDate = null;
				if (!billOfEntryDate.equals("")) {
					localbillOfEntryDate = LocalDateTime.parse(billOfEntryDate,
							DATE_TIME_FORMATTER);
				}
				BigDecimal cifValue = (BigDecimal) obj[26];
				BigDecimal customsDuty = (BigDecimal) obj[27];

				String hsnOrSacStr = null;
				if (obj[28] instanceof Integer) {
					Integer hsnOrSac = (Integer) obj[28];
					if (hsnOrSac != null) {
						hsnOrSacStr = hsnOrSac.toString();
					}
				}

				if (obj[28] instanceof String) {
					hsnOrSacStr = (String) obj[28];
				}

				String itemCode = (String) obj[29];
				String itemDesc = (String) obj[30];
				String itemCategory = (String) obj[31];
				String uom = (String) obj[32];

				BigDecimal qty = BigDecimal.ZERO;
				if (obj[33] instanceof Integer) {
					Integer qtyInt = (Integer) obj[33];
					qty = new BigDecimal(qtyInt);
				}

				BigDecimal taxableVal = BigDecimal.ZERO;
				if (obj[34] instanceof Integer) {
					Integer taxableValInt = (Integer) obj[34];
					taxableVal = new BigDecimal(taxableValInt);
				}
				BigDecimal igstRate = BigDecimal.ZERO;
				if (obj[35] instanceof Integer) {
					Integer igstRateInt = (Integer) obj[35];
					igstRate = new BigDecimal(igstRateInt);
				}
				BigDecimal igstAmount = BigDecimal.ZERO;
				if (obj[36] instanceof Integer) {
					Integer igstAmountInt = (Integer) obj[36];
					igstAmount = new BigDecimal(igstAmountInt);
				}
				BigDecimal cgstRate = BigDecimal.ZERO;
				if (obj[37] instanceof Integer) {
					Integer cgstRateInt = (Integer) obj[37];
					cgstRate = new BigDecimal(cgstRateInt);
				}
				BigDecimal cgstAmount = BigDecimal.ZERO;
				if (obj[38] instanceof Integer) {
					Integer cgstAmtInt = (Integer) obj[38];
					cgstAmount = new BigDecimal(cgstAmtInt);
				}
				BigDecimal sgstRate = BigDecimal.ZERO;
				if (obj[39] instanceof Integer) {
					Integer sgstRateInt = (Integer) obj[39];
					sgstRate = new BigDecimal(sgstRateInt);
				}
				BigDecimal sgstAmount = BigDecimal.ZERO;
				if (obj[40] instanceof Integer) {
					Integer sgstAmtInt = (Integer) obj[40];
					sgstAmount = new BigDecimal(sgstAmtInt);
				}
				BigDecimal cessRateAdvalorem = BigDecimal.ZERO;
				if (obj[41] instanceof Integer) {
					Integer cessRateAdvaloremInt = (Integer) obj[41];
					cessRateAdvalorem = new BigDecimal(cessRateAdvaloremInt);
				}
				BigDecimal cessAmtAdvalorem = BigDecimal.ZERO;
				if (obj[42] instanceof Integer) {
					Integer cessAmtAdvaloremInt = (Integer) obj[42];
					cessAmtAdvalorem = new BigDecimal(cessAmtAdvaloremInt);
				}
				BigDecimal cessRateSpecific = BigDecimal.ZERO;
				if (obj[43] instanceof Integer) {
					Integer cessRateSpecificInt = (Integer) obj[43];
					cessRateSpecific = new BigDecimal(cessRateSpecificInt);
				}
				BigDecimal cessAmtSpecific = BigDecimal.ZERO;
				if (obj[44] instanceof Integer) {
					Integer cessAmtSpecificInt = (Integer) obj[44];
					cessAmtSpecific = new BigDecimal(cessAmtSpecificInt);
				}
				BigDecimal lineItemAmt = BigDecimal.ZERO;
				if (obj[45] instanceof Integer) {
					Integer lineItemAmtInt = (Integer) obj[45]; // Invoice Value
					lineItemAmt = new BigDecimal(lineItemAmtInt);
				}

				String reverseChargeFlag = (String) obj[46];

				String eligibilityIndicator = (String) obj[47];
				String commonSupplyIndicator = (String) obj[48];

				BigDecimal availableIgst = BigDecimal.ZERO;
				if (obj[60] instanceof Integer) {
					Integer availableIgstInt = (Integer) obj[49];
					availableIgst = new BigDecimal(availableIgstInt);
				}

				BigDecimal availableCgst = BigDecimal.ZERO;
				if (obj[60] instanceof Integer) {
					Integer availableCgstInt = (Integer) obj[50];
					availableCgst = new BigDecimal(availableCgstInt);
				}

				BigDecimal availableSgst = BigDecimal.ZERO;
				if (obj[60] instanceof Integer) {
					Integer availableSgstInt = (Integer) obj[51];
					availableSgst = new BigDecimal(availableSgstInt);
				}

				BigDecimal availableCess = BigDecimal.ZERO;
				if (obj[52] instanceof Integer) {
					Integer availableCessInt = (Integer) obj[52];
					availableCess = new BigDecimal(availableCessInt);
				}

				String itcReversallIndetifier = (String) obj[53];
				String crDrReason = (String) obj[54];
				String purchaseVoucherNum = (String) obj[55];
				String purchaseVoucherDate = gstr2documentKeyBuilder
						.convertDateToStr(obj[56]);
				LocalDate localPurchaseVoucherDate = null;
				if (!purchaseVoucherDate.equals("")) {
					localPurchaseVoucherDate = LocalDate
							.parse(purchaseVoucherDate, DATE_TIME_FORMATTER);
				}
				String paymentVoucherNum = (String) obj[57];
				String paymentVoucherDate = gstr2documentKeyBuilder
						.convertDateToStr(obj[58]);
				LocalDate localPaymentVoucherDate = null;
				if (!paymentVoucherDate.equals("")) {
					localPaymentVoucherDate = LocalDate
							.parse(paymentVoucherDate, DATE_TIME_FORMATTER);
				}
				String contractNum = (String) obj[59];
				String contractDate = gstr2documentKeyBuilder
						.convertDateToStr(obj[60]);
				LocalDate localContractDate = null;
				if (!contractDate.equals("")) {
					localContractDate = LocalDate.parse(contractDate,
							DATE_TIME_FORMATTER);
				}

				BigDecimal contractVal = BigDecimal.ZERO;
				if (obj[60] instanceof Integer) {
					Integer contractValInt = (Integer) obj[61];
					contractVal = new BigDecimal(contractValInt);
				}

				String userDefinedField1 = (String) obj[62];
				String userDefinedField2 = (String) obj[63];
				String userDefinedField3 = (String) obj[64];

				// Line Items

				lineItem.setLineNo(lineNo);
				lineItem.setHsnSac(hsnOrSacStr);
				lineItem.setSupplyType(supplyType);
				lineItem.setItemCode(itemCode);
				lineItem.setItemDescription(itemDesc);
				lineItem.setItemCategory(itemCategory);
				lineItem.setUom(uom);
				lineItem.setQty(qty);
				lineItem.setTaxableValue(taxableVal);
				lineItem.setIgstRate(igstRate);
				lineItem.setIgstAmount(igstAmount);
				lineItem.setCgstRate(cgstRate);
				lineItem.setCgstAmount(cgstAmount);
				lineItem.setSgstRate(sgstRate);
				lineItem.setSgstAmount(sgstAmount);
				lineItem.setCessRateAdvalorem(cessRateAdvalorem);
				lineItem.setCessAmountAdvalorem(cessAmtAdvalorem);
				lineItem.setCessRateSpecific(cessRateSpecific);
				lineItem.setCessAmountSpecific(cessAmtSpecific);
				lineItem.setEligibilityIndicator(eligibilityIndicator);
				lineItem.setCommonSupplyIndicator(commonSupplyIndicator);
				lineItem.setAvailableIgst(availableIgst);
				lineItem.setAvailableCgst(availableCgst);
				lineItem.setAvailableSgst(availableSgst);
				lineItem.setAvailableCess(availableCess);
				lineItem.setItcReversalIdentifier(itcReversallIndetifier);
				lineItem.setPaymentVoucherNumber(paymentVoucherNum);
				lineItem.setPaymentDate(localPaymentVoucherDate);
				// lineItem.setContractNo(contractNum);
				lineItem.setContractDate(localContractDate);
				lineItem.setContractValue(contractVal);
				lineItem.setLineItemAmt(lineItemAmt);// INVOICE VALUE
				lineItems.add(lineItem); // Add Line Items

				document.setSourceIdentifier(sourceIdentifier);
				document.setSourceFileName(sourceFileName);
				document.setDivision(division);
				document.setSubDivision(subDivision);
				document.setProfitCentre(profitCentre);
				document.setPlantCode(plantCode);
				document.setTaxperiod(returnPeriod);
				document.setCgstin(rgstin);
				document.setDocType(docType);
				document.setDocNo(docNo);
				document.setDocDate(localDocDate);
				document.setOrigDocNo(origDocNo);
				document.setOrigDocDate(localOrigDocDate);
				document.setCrDrPreGst(crDrPreGst);
				document.setSgstin(sgstin);
				document.setOrigSgstin(origSgstin);
				document.setCustOrSuppName(suppName);
				document.setCustOrSuppCode(suppCode);
				document.setPos(pos);
				document.setPortCode(portCode);
				document.setBillOfEntryNo(billOfEntryNo);
				// document.setBillOfEntryDate(localbillOfEntryDate);
				// document.setCifValue(cifValue);
				// document.setCustomsDuty(customsDuty);
				document.setReverseCharge(reverseChargeFlag);
				// document.setCrDrReason(crDrReason);
				/*
				 * document.setAccountingVoucherNumber(purchaseVoucherNum);
				 * document.setAccountingVoucherDate(localPurchaseVoucherDate);
				 */
				document.setUserdefinedfield1(userDefinedField1);
				document.setUserdefinedfield2(userDefinedField2);
				document.setUserdefinedfield3(userDefinedField3);

				document.setIsError(false);
				document.setIsProcessed(true);
				document.setDocKey(key);
			}
			document.setLineItems(lineItems);
			document.setCreatedDate(LocalDateTime.now());
			documents.add(document);

		});
		return documents;
	}

}
