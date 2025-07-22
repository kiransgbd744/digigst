package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("Anx2RawFileToInwardTrans115DocConvertion")
@Slf4j
public class Anx2RawFileToInwardTrans115DocConvertion {

	public List<InwardTransDocument> convertAnx2RawFileToInwardTransDoc(
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity fileStatus, String userName) {
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
				String userId = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
				String sourceFileName = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				String profitCentre = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;

				String plant = (obj[3] != null
						&& !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
				String division = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				String location = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;

				String purchaseOrganisation = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				String userAccess1 = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;
				String userAccess2 = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				String userAccess3 = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				String userAccess4 = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				String userAccess5 = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;
				String userAccess6 = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;

				String gLCodeTaxableValue = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;

				String gLCodeIGST = (obj[14] != null
						&& !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;

				String gLCodeCGST = (obj[15] != null
						&& !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]).trim() : null;

				String gLCodeSGST = (obj[16] != null
						&& !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;

				String gLCodeAdvaloremCess = (obj[17] != null
						&& !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]).trim() : null;

				String gLCodeSpecificCess = (obj[18] != null
						&& !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;

				String gLCodeStateCess = (obj[19] != null
						&& !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;
				String returnPeriod = (obj[20] != null
						&& !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;
				if (returnPeriod != null
						&& returnPeriod.startsWith(GSTConstants.SPE_CHAR)) {
					returnPeriod = returnPeriod.substring(1);
				}
				String recipientGSTIN = (obj[21] != null
						&& !obj[21].toString().trim().isEmpty())
								? String.valueOf(obj[21]).trim() : null;

				String documentType = (obj[22] != null
						&& !obj[22].toString().trim().isEmpty())
								? String.valueOf(obj[22]).trim() : null;

				String supplyType = (obj[23] != null
						&& !obj[23].toString().trim().isEmpty())
								? String.valueOf(obj[23]).trim() : null;

				String documentNumber = (obj[24] != null
						&& !obj[24].toString().trim().isEmpty())
								? String.valueOf(obj[24]).trim() : null;
				if (documentNumber != null
						&& documentNumber.startsWith(GSTConstants.SPE_CHAR)) {
					documentNumber = documentNumber.substring(1);
				}
				LOGGER.debug(
						"This is added to verify Exponential issue,"
								+ " Doc Key is {} and Doc No is {}",
						key, documentNumber);
				String docDate = (obj[25] != null
						&& !obj[25].toString().trim().isEmpty())
								? String.valueOf(obj[25]).trim() : null;
				LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
				String finYear = GenUtil.getFinYear(localDocDate);

				String origDocNo = (obj[26] != null
						&& !obj[26].toString().trim().isEmpty())
								? String.valueOf(obj[26]) : null;
				if (origDocNo != null
						&& origDocNo.startsWith(GSTConstants.SPE_CHAR)) {
					origDocNo = origDocNo.substring(1);
				}

				String origDocDate = (obj[27] != null
						&& !obj[27].toString().trim().isEmpty())
								? String.valueOf(obj[27]).trim() : null;
				LocalDate localOrigDocDate = DateUtil
						.parseObjToDate(origDocDate);

				String crDrPreGst = (obj[28] != null
						&& !obj[28].toString().trim().isEmpty())
								? String.valueOf(obj[28]).trim() : null;

				// Line Items
				Integer lineNo = 1;
				if (obj[29] != null && !obj[29].toString().trim().isEmpty()) {
					String lineNoStr = (String.valueOf(obj[29])).trim();
					lineNo = Integer.valueOf(lineNoStr);
				}

				String sgstin = (obj[30] != null
						&& !obj[30].toString().trim().isEmpty())
								? String.valueOf(obj[30]).trim() : null;

				String custSupType = (obj[31] != null
						&& !obj[31].toString().trim().isEmpty())
								? String.valueOf(obj[31]).trim() : null;

				String differentialFlag = (obj[32] != null
						&& !obj[32].toString().trim().isEmpty())
								? String.valueOf(obj[32]).trim() : null;

				String origSgstin = (obj[33] != null
						&& !obj[33].toString().trim().isEmpty())
								? String.valueOf(obj[33]).trim() : null;

				String suppName = (obj[34] != null
						&& !obj[34].toString().trim().isEmpty())
								? String.valueOf(obj[34]).trim() : null;

				String suppCode = (obj[35] != null
						&& !obj[35].toString().trim().isEmpty())
								? String.valueOf(obj[35]).trim() : null;

				String supplierAddress1 = (obj[36] != null
						&& !obj[36].toString().trim().isEmpty())
								? String.valueOf(obj[36]).trim() : null;

				String supplierAddress2 = (obj[37] != null
						&& !obj[37].toString().trim().isEmpty())
								? String.valueOf(obj[37]).trim() : null;

				String supplierAddress3 = (obj[38] != null
						&& !obj[38].toString().trim().isEmpty())
								? String.valueOf(obj[38]).trim() : null;

				String supplierAddress4 = (obj[39] != null
						&& !obj[39].toString().trim().isEmpty())
								? String.valueOf(obj[39]).trim() : null;

				String pos = (obj[40] != null
						&& !obj[40].toString().trim().isEmpty())
								? String.valueOf(obj[40]).trim() : null;
				if (pos != null && pos.startsWith(GSTConstants.SPE_CHAR)) {
					pos = pos.substring(1);
				}

				String stateApplyingCess = (obj[41] != null
						&& !obj[41].toString().trim().isEmpty())
								? String.valueOf(obj[41]).trim() : null;
				if (stateApplyingCess != null && stateApplyingCess
						.startsWith(GSTConstants.SPE_CHAR)) {
					stateApplyingCess = stateApplyingCess.substring(1);
				}

				String portCode = (obj[42] != null
						&& !obj[42].toString().trim().isEmpty())
								? String.valueOf(obj[42]).trim() : null;

				String billOfEntryNo = (obj[43] != null
						&& !obj[43].toString().trim().isEmpty())
								? String.valueOf(obj[43]).trim() : null;

				String localBillDate = (obj[44] != null
						&& !obj[44].toString().trim().isEmpty())
								? String.valueOf(obj[44]).trim() : null;
				LocalDate localBillOfEntryDate = DateUtil
						.parseObjToDate(localBillDate);

				BigDecimal cifValue = null;
				if (obj[45] != null && !obj[45].toString().trim().isEmpty()) {
					cifValue = new BigDecimal((String.valueOf(obj[45])).trim());
				}

				BigDecimal customDuty = null;
				if (obj[46] != null && !obj[46].toString().trim().isEmpty()) {
					customDuty = new BigDecimal(
							(String.valueOf(obj[46])).trim());
				}

				String hsnOrSacStr = (obj[47] != null
						&& !obj[47].toString().trim().isEmpty())
								? String.valueOf(obj[47]).trim() : null;
				if (hsnOrSacStr != null
						&& hsnOrSacStr.startsWith(GSTConstants.SPE_CHAR)) {
					hsnOrSacStr = hsnOrSacStr.substring(1);
				}
				String itemCode = (obj[48] != null
						&& !obj[48].toString().trim().isEmpty())
								? String.valueOf(obj[48]).trim() : null;

				String itemDesc = (obj[49] != null
						&& !obj[49].toString().trim().isEmpty())
								? String.valueOf(obj[49]).trim() : null;
				String itemCategory = (obj[50] != null
						&& !obj[50].toString().trim().isEmpty())
								? String.valueOf(obj[50]).trim() : null;

				String uom = (obj[51] != null
						&& !obj[51].toString().trim().isEmpty())
								? String.valueOf(obj[51]).trim() : null;

				BigDecimal qty = null;
				if (obj[52] != null && !obj[52].toString().trim().isEmpty()) {
					qty = new BigDecimal((String.valueOf(obj[52])).trim());
				}

				String section7ofIGSTFlag = (obj[53] != null
						&& !obj[53].toString().trim().isEmpty())
								? String.valueOf(obj[53]).trim() : null;

				BigDecimal taxableVal = null;
				if (obj[54] != null && !obj[54].toString().trim().isEmpty()) {
					taxableVal = new BigDecimal(
							(String.valueOf(obj[54])).trim());
				}

				BigDecimal igstRate = null;
				if (obj[55] != null && !obj[55].toString().trim().isEmpty()) {
					igstRate = new BigDecimal((String.valueOf(obj[55])).trim());

				}

				BigDecimal igstAmount = null;
				if (obj[56] != null && !obj[56].toString().trim().isEmpty()) {
					igstAmount = new BigDecimal(
							(String.valueOf(obj[56])).trim());
				}

				BigDecimal cgstRate = null;
				if (obj[57] != null && !obj[57].toString().trim().isEmpty()) {
					cgstRate = new BigDecimal((String.valueOf(obj[57])).trim());
				}
				BigDecimal cgstAmount = null;
				if (obj[58] != null && !obj[58].toString().trim().isEmpty()) {
					cgstAmount = new BigDecimal(
							(String.valueOf(obj[58])).trim());
				}
				BigDecimal sgstRate = null;
				if (obj[59] != null && !obj[59].toString().trim().isEmpty()) {
					sgstRate = new BigDecimal((String.valueOf(obj[59])).trim());
				}

				BigDecimal sgstAmount = null;
				if (obj[60] != null && !obj[60].toString().trim().isEmpty()) {
					sgstAmount = new BigDecimal(
							(String.valueOf(obj[60])).trim());
				}
				BigDecimal cessRateAdvalorem = null;
				if (obj[61] != null && !obj[61].toString().trim().isEmpty()) {
					cessRateAdvalorem = new BigDecimal(
							(String.valueOf(obj[61])).trim());
				}
				BigDecimal cessAmtAdvalorem = null;
				if (obj[62] != null && !obj[62].toString().trim().isEmpty()) {
					cessAmtAdvalorem = new BigDecimal(
							(String.valueOf(obj[62])).trim());
				}
				BigDecimal cessRateSpecific = null;
				if (obj[63] != null && !obj[63].toString().trim().isEmpty()) {
					cessRateSpecific = new BigDecimal(
							(String.valueOf(obj[63])).trim());
				}
				BigDecimal cessAmtSpecific = null;
				if (obj[64] != null && !obj[64].toString().trim().isEmpty()) {
					cessAmtSpecific = new BigDecimal(
							(String.valueOf(obj[64])).trim());
				}
				BigDecimal stateCessRate = null;
				if (obj[65] != null && !obj[65].toString().trim().isEmpty()) {
					stateCessRate = new BigDecimal(
							(String.valueOf(obj[65])).trim());
				}

				BigDecimal stateCessAmount = null;
				if (obj[66] != null && !obj[66].toString().trim().isEmpty()) {
					stateCessAmount = new BigDecimal(
							(String.valueOf(obj[66])).trim());
				}
				BigDecimal otherValue = null;
				if (obj[67] != null && !obj[67].toString().trim().isEmpty()) {
					otherValue = new BigDecimal(
							(String.valueOf(obj[67])).trim());
				}
				BigDecimal invoiceValue = null;
				if (obj[68] != null && !obj[68].toString().trim().isEmpty()) {
					invoiceValue = new BigDecimal(
							(String.valueOf(obj[68])).trim());
				}
				String claimRefundFlag = (obj[69] != null
						&& !obj[69].toString().trim().isEmpty())
								? String.valueOf(obj[69]).trim() : null;

				String autoPopulateToRefundFlag = (obj[70] != null
						&& !obj[70].toString().trim().isEmpty())
								? String.valueOf(obj[70]).trim() : null;

				String adjustementReferenceNo = (obj[71] != null
						&& !obj[71].toString().trim().isEmpty())
								? String.valueOf(obj[71]).trim() : null;

				String adjustmentDate = (obj[72] != null
						&& !obj[72].toString().trim().isEmpty())
								? String.valueOf(obj[72]).trim() : null;
				LocalDate localAdjustementReferenceDate = DateUtil
						.parseObjToDate(adjustmentDate);

				BigDecimal taxableValueAdjusted = null;
				if (obj[73] != null && !obj[73].toString().trim().isEmpty()) {
					taxableValueAdjusted = new BigDecimal(
							(String.valueOf(obj[73])).trim());
				}
				BigDecimal integratedTaxAmountAdjusted = null;
				if (obj[74] != null && !obj[74].toString().trim().isEmpty()) {
					integratedTaxAmountAdjusted = new BigDecimal(
							(String.valueOf(obj[74])).trim());
				}
				BigDecimal centralTaxAmountAdjusted = null;
				if (obj[75] != null && !obj[75].toString().trim().isEmpty()) {
					centralTaxAmountAdjusted = new BigDecimal(
							(String.valueOf(obj[75])).trim());
				}
				BigDecimal stateUTTaxAmountAdjusted = null;
				if (obj[76] != null && !obj[76].toString().trim().isEmpty()) {
					stateUTTaxAmountAdjusted = new BigDecimal(
							(String.valueOf(obj[76])).trim());
				}
				BigDecimal advaloremCessAmountAdjusted = null;
				if (obj[77] != null && !obj[77].toString().trim().isEmpty()) {
					advaloremCessAmountAdjusted = new BigDecimal(
							(String.valueOf(obj[77])).trim());
				}
				BigDecimal specificCessAmountAdjusted = null;
				if (obj[78] != null && !obj[78].toString().trim().isEmpty()) {
					specificCessAmountAdjusted = new BigDecimal(
							(String.valueOf(obj[78])).trim());
				}
				BigDecimal stateCessAmountAdjusted = null;
				if (obj[79] != null && !obj[79].toString().trim().isEmpty()) {
					stateCessAmountAdjusted = new BigDecimal(
							(String.valueOf(obj[79])).trim());
				}

				String reverseChargeFlag = (obj[80] != null
						&& !obj[80].toString().trim().isEmpty())
								? String.valueOf(obj[80]).trim() : null;

				String eligibilityIndicator = (obj[81] != null
						&& !obj[81].toString().trim().isEmpty())
								? String.valueOf(obj[81]).trim() : null;

				String commonSupplyIndicator = (obj[82] != null
						&& !obj[82].toString().trim().isEmpty())
								? String.valueOf(obj[82]).trim() : null;

				BigDecimal availableIGST = null;
				if (obj[83] != null && !obj[83].toString().trim().isEmpty()) {
					availableIGST = new BigDecimal(
							(String.valueOf(obj[83])).trim());
				}
				BigDecimal availableCGST = null;
				if (obj[84] != null && !obj[84].toString().trim().isEmpty()) {
					availableCGST = new BigDecimal(
							(String.valueOf(obj[84])).trim());
				}
				BigDecimal availableSGST = null;
				if (obj[85] != null && !obj[85].toString().trim().isEmpty()) {
					availableSGST = new BigDecimal(
							(String.valueOf(obj[85])).trim());
				}
				BigDecimal availableCess = null;
				if (obj[86] != null && !obj[86].toString().trim().isEmpty()) {
					availableCess = new BigDecimal(
							(String.valueOf(obj[86])).trim());
				}
				String itcEntitlement = (obj[87] != null
						&& !obj[87].toString().trim().isEmpty())
								? String.valueOf(obj[87]).trim() : null;
				String itcReversalIdentifier = (obj[88] != null
						&& !obj[88].toString().trim().isEmpty())
								? String.valueOf(obj[88]).trim() : null;
				String reasonForCreditDebitNote = (obj[89] != null
						&& !obj[89].toString().trim().isEmpty())
								? String.valueOf(obj[89]).trim() : null;

				String purchaseVoucherNumber = (obj[90] != null
						&& !obj[90].toString().trim().isEmpty())
								? String.valueOf(obj[90]).trim() : null;

				String purchaseVochDate = (obj[91] != null
						&& !obj[91].toString().trim().isEmpty())
								? String.valueOf(obj[91]).trim() : null;
				LocalDate localPurchaseVoucherDate = DateUtil
						.parseObjToDate(purchaseVochDate);

				String postingDate = (obj[92] != null
						&& !obj[92].toString().trim().isEmpty())
								? String.valueOf(obj[92]).trim() : null;
				LocalDate localPostingDate = DateUtil
						.parseObjToDate(postingDate);

				String paymentVoucherNumber = (obj[93] != null
						&& !obj[93].toString().trim().isEmpty())
								? String.valueOf(obj[93]).trim() : null;

				String paymentDate = (obj[94] != null
						&& !obj[94].toString().trim().isEmpty())
								? String.valueOf(obj[94]).trim() : null;
				LocalDate localPaymentDate = DateUtil
						.parseObjToDate(paymentDate);

				String contractNum = (obj[95] != null
						&& !obj[95].toString().trim().isEmpty())
								? String.valueOf(obj[95]).trim() : null;

				String localConDate = (obj[96] != null
						&& !obj[96].toString().trim().isEmpty())
								? String.valueOf(obj[96]).trim() : null;
				LocalDate localContractDate = DateUtil
						.parseObjToDate(localConDate);

				BigDecimal contractVal = null;
				if (obj[97] != null && !obj[97].toString().trim().isEmpty()) {
					contractVal = new BigDecimal(
							(String.valueOf(obj[97])).trim());
				}

				String userDefinedField1 = (obj[98] != null
						&& !obj[98].toString().trim().isEmpty())
								? String.valueOf(obj[98]).trim() : null;
				String userDefinedField2 = (obj[99] != null
						&& !obj[99].toString().trim().isEmpty())
								? String.valueOf(obj[99]).trim() : null;
				String userDefinedField3 = (obj[100] != null
						&& !obj[100].toString().trim().isEmpty())
								? String.valueOf(obj[100]).trim() : null;
				String userDefinedField4 = (obj[101] != null
						&& !obj[101].toString().trim().isEmpty())
								? String.valueOf(obj[101]).trim() : null;
				String userDefinedField5 = (obj[102] != null
						&& !obj[102].toString().trim().isEmpty())
								? String.valueOf(obj[102]).trim() : null;
				String userDefinedField6 = (obj[103] != null
						&& !obj[103].toString().trim().isEmpty())
								? String.valueOf(obj[103]).trim() : null;
				String userDefinedField7 = (obj[104] != null
						&& !obj[104].toString().trim().isEmpty())
								? String.valueOf(obj[104]).trim() : null;
				String userDefinedField8 = (obj[105] != null
						&& !obj[105].toString().trim().isEmpty())
								? String.valueOf(obj[105]).trim() : null;
				String userDefinedField9 = (obj[106] != null
						&& !obj[106].toString().trim().isEmpty())
								? String.valueOf(obj[106]).trim() : null;
				String userDefinedField10 = (obj[107] != null
						&& !obj[107].toString().trim().isEmpty())
								? String.valueOf(obj[107]).trim() : null;
				String userDefinedField11 = (obj[108] != null
						&& !obj[108].toString().trim().isEmpty())
								? String.valueOf(obj[108]).trim() : null;
				String userDefinedField12 = (obj[109] != null
						&& !obj[109].toString().trim().isEmpty())
								? String.valueOf(obj[109]).trim() : null;
				String userDefinedField13 = (obj[110] != null
						&& !obj[110].toString().trim().isEmpty())
								? String.valueOf(obj[110]).trim() : null;
				String userDefinedField14 = (obj[111] != null
						&& !obj[111].toString().trim().isEmpty())
								? String.valueOf(obj[111]).trim() : null;
				String userDefinedField15 = (obj[112] != null
						&& !obj[112].toString().trim().isEmpty())
								? String.valueOf(obj[112]).trim() : null;

				String eWayBillNumber = (obj[113] != null
						&& !obj[113].toString().trim().isEmpty())
								? String.valueOf(obj[113]).trim() : null;

				String eWayBillDate = (obj[114] != null
						&& !obj[114].toString().trim().isEmpty())
								? String.valueOf(obj[114]).trim() : null;
				LocalDate localEWayBillDate = DateUtil
						.parseObjToDate(eWayBillDate);

				/**
				 * Start - Inward Document Structural Validation Error
				 * Correction Implementation
				 */
				// File ID and Id are not null only in case of Inward Doc
				// Structural Validation Error Correction
				String idStr = null;
				String fileIdStr = null;
				if (obj.length > 115) {
					idStr = (obj[115] != null
							&& !obj[115].toString().trim().isEmpty())
									? String.valueOf(obj[115]) : null;
					fileIdStr = (obj[116] != null
							&& !obj[116].toString().trim().isEmpty())
									? String.valueOf(obj[116]) : null;
				}
				/**
				 * End - Inward Document Structural Validation Error Correction
				 * Implementation
				 */

				// Line Items

				String source = fileStatus.getSource();
				if (source != null && source
						.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					document.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.SFTP
									.getDataOriginTypeCode());
				} else {
					document.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
									.getDataOriginTypeCode());
				}

				lineItem.setGlCodeTaxableValue(gLCodeTaxableValue);
				lineItem.setGlCodeIgst(gLCodeIGST);
				lineItem.setGlCodeCgst(gLCodeCGST);
				lineItem.setGlCodeSgst(gLCodeSGST);
				lineItem.setGlCodeAdvCess(gLCodeAdvaloremCess);
				lineItem.setGlCodeSpCess(gLCodeSpecificCess);
				lineItem.setGlCodeStateCess(gLCodeStateCess);
				lineItem.setSupplyType(supplyType);
				lineItem.setOrigDocNo(origDocNo);
				lineItem.setOrigDocDate(localOrigDocDate);
				lineItem.setLineNo(lineNo);
				lineItem.setCifValue(cifValue);
				lineItem.setCustomDuty(customDuty);
				lineItem.setHsnSac(hsnOrSacStr);
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
				lineItem.setStateCessRate(stateCessRate);
				lineItem.setStateCessAmount(stateCessAmount);

				lineItem.setOtherValues(otherValue);
				lineItem.setLineItemAmt(invoiceValue);
				lineItem.setAdjustmentRefNo(adjustementReferenceNo);
				lineItem.setAdjustmentRefDate(localAdjustementReferenceDate);
				lineItem.setAdjustedTaxableValue(taxableValueAdjusted);
				lineItem.setAdjustedIgstAmt(integratedTaxAmountAdjusted);
				lineItem.setAdjustedCgstAmt(centralTaxAmountAdjusted);
				lineItem.setAdjustedSgstAmt(stateUTTaxAmountAdjusted);
				lineItem.setAdjustedCessAmtAdvalorem(
						advaloremCessAmountAdjusted);
				lineItem.setAdjustedCessAmtSpecific(specificCessAmountAdjusted);
				lineItem.setAdjustedStateCessAmt(stateCessAmountAdjusted);
				lineItem.setEligibilityIndicator(eligibilityIndicator);
				lineItem.setCommonSupplyIndicator(commonSupplyIndicator);
				lineItem.setAvailableIgst(availableIGST);
				lineItem.setAvailableCgst(availableCGST);
				lineItem.setAvailableSgst(availableSGST);
				lineItem.setAvailableCess(availableCess);
				lineItem.setItcReversalIdentifier(itcReversalIdentifier);
				lineItem.setCrDrReason(reasonForCreditDebitNote);
				lineItem.setPurchaseVoucherNum(purchaseVoucherNumber);
				lineItem.setPurchaseVoucherDate(localPurchaseVoucherDate);
				lineItem.setPaymentVoucherNumber(paymentVoucherNumber);
				lineItem.setPaymentDate(localPaymentDate);
				lineItem.setContractNumber(contractNum);
				lineItem.setContractDate(localContractDate);
				lineItem.setContractValue(contractVal);

				lineItem.setProfitCentre(profitCentre);
				lineItem.setPlantCode(plant);
				lineItem.setLocation(location);

				lineItem.setUserdefinedfield1(userDefinedField1);
				lineItem.setUserdefinedfield2(userDefinedField2);
				lineItem.setUserdefinedfield3(userDefinedField3);
				lineItem.setUserDefinedField4(userDefinedField4);
				lineItem.setUserDefinedField5(userDefinedField5);
				lineItem.setUserDefinedField6(userDefinedField6);
				lineItem.setUserDefinedField7(userDefinedField7);
				lineItem.setUserDefinedField8(userDefinedField8);
				lineItem.setUserDefinedField9(userDefinedField9);
				lineItem.setUserDefinedField10(userDefinedField10);
				lineItem.setUserDefinedField11(userDefinedField11);
				lineItem.setUserDefinedField12(userDefinedField12);
				lineItem.setUserDefinedField13(userDefinedField13);
				lineItem.setUserDefinedField14(userDefinedField14);
				lineItem.setUserDefinedField15(userDefinedField15);

				if (fileStatus != null) {
					document.setAcceptanceId(fileStatus.getId());
					lineItem.setAcceptanceId(fileStatus.getId());
				} else {
					document.setAcceptanceId(0L);
					lineItem.setAcceptanceId(0L);
				}

				lineItems.add(lineItem); // Add Line Items

				/*
				 * if (fileStatus != null) {
				 * document.setAcceptanceId(fileStatus.getId()); }
				 */
				document.setUserId(userId);
				document.setSourceFileName(sourceFileName);
				document.setProfitCentre(profitCentre);
				document.setPlantCode(plant);
				document.setDivision(division);
				document.setLocation(location);
				document.setPurchaseOrganization(purchaseOrganisation);
				document.setUserAccess1(userAccess1);
				document.setUserAccess2(userAccess2);
				document.setUserAccess3(userAccess3);
				document.setUserAccess4(userAccess4);
				document.setUserAccess5(userAccess5);
				document.setUserAccess6(userAccess6);
				document.setTaxperiod(returnPeriod);
				document.setCgstin(recipientGSTIN);
				document.setDocType(documentType);
				document.setDocNo(documentNumber);
				document.setDocDate(localDocDate);
				document.setFinYear(finYear);
				document.setCrDrPreGst(crDrPreGst);
				document.setSgstin(sgstin);
				document.setCustOrSuppType(custSupType);
				document.setDiffPercent(differentialFlag);
				document.setOrigSgstin(origSgstin);
				document.setCustOrSuppName(suppName);
				document.setCustOrSuppCode(suppCode);
				document.setCustOrSuppAddress1(supplierAddress1);
				document.setCustOrSuppAddress2(supplierAddress2);
				document.setCustOrSuppAddress3(supplierAddress3);
				document.setCustOrSuppAddress4(supplierAddress4);
				document.setDataOriginTypeCode(
						GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
								.getDataOriginTypeCode());

				document.setPos(pos);
				document.setStateApplyingCess(stateApplyingCess);
				document.setPortCode(portCode);
				document.setBillOfEntryNo(billOfEntryNo);
				document.setBillOfEntryDate(localBillOfEntryDate);
				document.setSection7OfIgstFlag(section7ofIGSTFlag);
				document.setClaimRefundFlag(claimRefundFlag);
				document.setAutoPopToRefundFlag(autoPopulateToRefundFlag);
				document.setReverseCharge(reverseChargeFlag);
				document.setItcEntitlement(itcEntitlement);
				document.setPostingDate(localPostingDate);
				document.seteWayBillNo(eWayBillNumber);
				// document.seteWayBillDate(localEWayBillDate);
				document.seteWayBillDate(localEWayBillDate != null
						? localEWayBillDate.atStartOfDay() : null);
				document.setIsError(false);
				document.setIsProcessed(true);

				if (idStr != null) {
					try {
						Long id = Long.parseLong(idStr);
						document.setId(id);
					} catch (RuntimeException e) {
						throw new AppException(
								" Error occured while converting "
										+ "String id to Long id "
										+ e.getMessage());
					}
				}

				if (fileIdStr != null) {
					try {
						Long fileId = Long.parseLong(fileIdStr);
						document.setAcceptanceId(fileId);
					} catch (RuntimeException e) {
						throw new AppException(
								" Error occuured while converting "
										+ "String file id to Long  file id "
										+ e.getMessage());
					}
				}
				document.setDocKey(key);

			}
			document.setLineItems(lineItems);
			document.setCreatedBy(userName);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setCreatedDate(convertNow);
			documents.add(document);

		});
		return documents;
	}
}
