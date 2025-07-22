package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorItemEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Anx2RawFileToInwardTransDocErrorConvertion")
public class Anx2RawFileToInwardTransDocErrorConvertion {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2RawFileToInwardTransDocErrorConvertion.class);

	public List<Anx2InwardErrorHeaderEntity> convertAnx2RawFileToInWardTransError(
			Map<String, List<Object[]>> errDocMapObj,
			Gstr1FileStatusEntity fileStatus, String userName) {
		LOGGER.error("Anx2RawFileToInwardTransDocErrorConvertion "
				+ "convertPRFileToInWardTransError Begining");

		List<Anx2InwardErrorHeaderEntity> documents = new ArrayList<>();

		errDocMapObj.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			Anx2InwardErrorHeaderEntity document = new Anx2InwardErrorHeaderEntity();
			List<Anx2InwardErrorItemEntity> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				// TO-DO Structural Validations
				Anx2InwardErrorItemEntity lineItem = new Anx2InwardErrorItemEntity();
				String userId = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]) : null;
				String sourceFileName = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]) : null;
				String profitCentre = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]) : null;
				String plant = (obj[3] != null
						&& !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]) : null;
				String division = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]) : null;
				String location = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]) : null;
				String purchaseOrganisation = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]) : null;
				String userAccess1 = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]) : null;
				String userAccess2 = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]) : null;
				String userAccess3 = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]) : null;
				String userAccess4 = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]) : null;
				String userAccess5 = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]) : null;
				String userAccess6 = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]) : null;
				String gLCodeTaxableValue = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]) : null;

				String gLCodeIGST = (obj[14] != null
						&& !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]) : null;
				String glCodeCGST = (obj[15] != null
						&& !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]) : null;
				String glCodeSGST = (obj[16] != null
						&& !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]) : null;

				String gLCodeAdvaloremCess = (obj[17] != null
						&& !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]) : null;
				String gLCodeSpecificCess = (obj[18] != null
						&& !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]) : null;
				String gLCodeStateCess = (obj[19] != null
						&& !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]) : null;

				String returnPeriod = (obj[20] != null
						&& !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]) : null;
				Integer derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(returnPeriod);
				if (returnPeriod != null
						&& returnPeriod.startsWith(GSTConstants.SPE_CHAR)) {
					returnPeriod = returnPeriod.substring(1);
				}
				String recipientGSTIN = (obj[21] != null
						&& !obj[21].toString().trim().isEmpty())
								? String.valueOf(obj[21]) : null;
				String documentType = (obj[22] != null
						&& !obj[22].toString().trim().isEmpty())
								? String.valueOf(obj[22]) : null;
				String supplyType = (obj[23] != null
						&& !obj[23].toString().trim().isEmpty())
								? String.valueOf(obj[23]) : null;

				String documentNumber = (obj[24] != null
						&& !obj[24].toString().trim().isEmpty())
								? String.valueOf(obj[24]) : null;

				if (documentNumber != null
						&& documentNumber.startsWith(GSTConstants.SPE_CHAR)) {
					documentNumber = documentNumber.substring(1);
				}
				String docDate = (obj[25] != null
						&& !obj[25].toString().trim().isEmpty())
								? String.valueOf(obj[25]) : null;
				LocalDate localDocDate = DateUtil.parseObjToDate(docDate);

				String origDocNo = (obj[26] != null
						&& !obj[26].toString().trim().isEmpty())
								? String.valueOf(obj[26]) : null;
				if (origDocNo != null
						&& origDocNo.startsWith(GSTConstants.SPE_CHAR)) {
					origDocNo = origDocNo.substring(1);
				}
				String origDocDate = (obj[27] != null
						&& !obj[27].toString().trim().isEmpty())
								? String.valueOf(obj[27]) : null;
				LocalDate orgDocDateLocal = DateUtil
						.parseObjToDate(origDocDate);

				String crDrPreGst = (obj[28] != null
						&& !obj[28].toString().trim().isEmpty())
								? String.valueOf(obj[28]) : null;
				String lineNo = (obj[29] != null
						&& !obj[29].toString().trim().isEmpty())
								? (obj[29].toString()) : null;
				String sgstin = (obj[30] != null
						&& !obj[30].toString().trim().isEmpty())
								? String.valueOf(obj[30]) : null;
				String custSupType = (obj[31] != null
						&& !obj[31].toString().trim().isEmpty())
								? String.valueOf(obj[31]) : null;
				String differentialFlag = (obj[32] != null
						&& !obj[32].toString().trim().isEmpty())
								? String.valueOf(obj[32]) : null;
				String origSgstin = (obj[33] != null
						&& !obj[33].toString().trim().isEmpty())
								? String.valueOf(obj[33]) : null;
				String suppName = (obj[34] != null
						&& !obj[34].toString().trim().isEmpty())
								? String.valueOf(obj[34]) : null;
				String suppCode = (obj[35] != null
						&& !obj[35].toString().trim().isEmpty())
								? String.valueOf(obj[35]) : null;
				String supplierAddress1 = (obj[36] != null
						&& !obj[36].toString().trim().isEmpty())
								? String.valueOf(obj[36]) : null;
				String supplierAddress2 = (obj[37] != null
						&& !obj[37].toString().trim().isEmpty())
								? String.valueOf(obj[37]) : null;
				String supplierAddress3 = (obj[38] != null
						&& !obj[38].toString().trim().isEmpty())
								? String.valueOf(obj[38]) : null;
				String supplierAddress4 = (obj[39] != null
						&& !obj[39].toString().trim().isEmpty())
								? String.valueOf(obj[39]) : null;

				String pos = (obj[40] != null
						&& !obj[40].toString().trim().isEmpty())
								? String.valueOf(obj[40]) : null;
				if (pos != null && pos.startsWith(GSTConstants.SPE_CHAR)) {
					pos = pos.substring(1);
				}
				String stateApplyingCess = (obj[41] != null
						&& !obj[41].toString().trim().isEmpty())
								? String.valueOf(obj[41]) : null;
				if (stateApplyingCess != null && stateApplyingCess
						.startsWith(GSTConstants.SPE_CHAR)) {
					stateApplyingCess = stateApplyingCess.substring(1);
				}
				String portCode = (obj[42] != null
						&& !obj[42].toString().trim().isEmpty())
								? String.valueOf(obj[42]) : null;
				String billOfEntry = (obj[43] != null
						&& !obj[43].toString().trim().isEmpty())
								? String.valueOf(obj[43]) : null;
				String billOfEntryDate = (obj[44] != null
						&& !obj[44].toString().trim().isEmpty())
								? String.valueOf(obj[44]) : null;
				LocalDate localBillOfEntryDate = DateUtil
						.parseObjToDate(billOfEntryDate);

				String cifValue = (obj[45] != null
						&& !obj[45].toString().trim().isEmpty())
								? String.valueOf(obj[45]) : null;
				String customDuty = (obj[46] != null
						&& !obj[46].toString().trim().isEmpty())
								? String.valueOf(obj[46]) : null;
				String hsnOrSacStr = (obj[47] != null
						&& !obj[47].toString().trim().isEmpty())
								? String.valueOf(obj[47]) : null;
				if (hsnOrSacStr != null
						&& hsnOrSacStr.startsWith(GSTConstants.SPE_CHAR)) {
					hsnOrSacStr = hsnOrSacStr.substring(1);
				}
				String productCode = (obj[48] != null
						&& !obj[48].toString().trim().isEmpty())
								? String.valueOf(obj[48]) : null;
				String itemDesc = (obj[49] != null
						&& !obj[49].toString().trim().isEmpty())
								? String.valueOf(obj[49]) : null;
				String itemCategory = (obj[50] != null
						&& !obj[50].toString().trim().isEmpty())
								? String.valueOf(obj[50]) : null;
				String uom = (obj[51] != null
						&& !obj[51].toString().trim().isEmpty())
								? String.valueOf(obj[51]) : null;
				String qty = (obj[52] != null
						&& !obj[52].toString().trim().isEmpty())
								? String.valueOf(obj[52]) : null;

				String section7ofIGSTFlag = (obj[53] != null
						&& !obj[53].toString().trim().isEmpty())
								? String.valueOf(obj[53]) : null;

				String taxableVal = (obj[54] != null
						&& !obj[54].toString().trim().isEmpty())
								? String.valueOf(obj[54]) : null;
				String igstRate = (obj[55] != null
						&& !obj[55].toString().trim().isEmpty())
								? String.valueOf(obj[55]) : null;

				String igstAmount = (obj[56] != null
						&& !obj[56].toString().trim().isEmpty())
								? String.valueOf(obj[56]) : null;

				String cgstRate = (obj[57] != null
						&& !obj[57].toString().trim().isEmpty())
								? String.valueOf(obj[57]) : null;
				String cgstAmount = (obj[58] != null
						&& !obj[58].toString().trim().isEmpty())
								? String.valueOf(obj[58]) : null;
				String sgstRate = (obj[59] != null
						&& !obj[59].toString().trim().isEmpty())
								? String.valueOf(obj[59]) : null;
				String sgstAmount = (obj[60] != null
						&& !obj[60].toString().trim().isEmpty())
								? String.valueOf(obj[60]) : null;
				String cessRateAdvalorem = (obj[61] != null
						&& !obj[61].toString().trim().isEmpty())
								? String.valueOf(obj[61]) : null;
				String cessAmtAdvalorem = (obj[62] != null
						&& !obj[62].toString().trim().isEmpty())
								? String.valueOf(obj[62]) : null;
				String cessRateSpecific = (obj[63] != null
						&& !obj[63].toString().trim().isEmpty())
								? String.valueOf(obj[63]) : null;
				String cessAmountSpecific = (obj[64] != null
						&& !obj[64].toString().trim().isEmpty())
								? String.valueOf(obj[64]) : null;
				String stateCessRate = (obj[65] != null
						&& !obj[65].toString().trim().isEmpty())
								? String.valueOf(obj[65]) : null;
				String stateCessAmount = (obj[66] != null
						&& !obj[66].toString().trim().isEmpty())
								? String.valueOf(obj[66]) : null;
				String otherValue = (obj[67] != null
						&& !obj[67].toString().trim().isEmpty())
								? String.valueOf(obj[67]) : null;

				String invoiceValue = (obj[68] != null
						&& !obj[68].toString().trim().isEmpty())
								? String.valueOf(obj[68]) : null;

				String claimRefundFlag = (obj[69] != null
						&& !obj[69].toString().trim().isEmpty())
								? String.valueOf(obj[69]) : null;
				String autoPopulateToRefundFlag = (obj[70] != null
						&& !obj[70].toString().trim().isEmpty())
								? String.valueOf(obj[70]) : null;

				String adjustementReferenceNo = null;
				if (obj[71] != null && !obj[71].toString().trim().isEmpty()) {
					if (obj[71] instanceof BigDecimal) {
						BigDecimal decimal = (BigDecimal) obj[71];
						adjustementReferenceNo = String
								.valueOf(decimal.longValue());
					} else if (obj[71] instanceof String) {
						adjustementReferenceNo = (String) obj[71];
					} else if (obj[71] instanceof BigInteger) {
						adjustementReferenceNo = (String
								.valueOf(GenUtil.getBigInteger(obj[71])));
					} else if (obj[71] instanceof Number) {
						adjustementReferenceNo = String
								.valueOf(((Number) obj[71]).longValue());
					}
				}
				String adjustementReferenceDate = (obj[72] != null
						&& !obj[72].toString().trim().isEmpty())
								? String.valueOf(obj[72]) : null;
				LocalDate localAdjustementReferenceDate = DateUtil
						.parseObjToDate(adjustementReferenceDate);

				String taxableValueAdjusted = (obj[73] != null
						&& !obj[73].toString().trim().isEmpty())
								? String.valueOf(obj[73]) : null;

				String integratedTaxAmountAdjusted = (obj[74] != null
						&& !obj[74].toString().trim().isEmpty())
								? String.valueOf(obj[74]) : null;

				String centralTaxAmountAdjusted = (obj[75] != null
						&& !obj[75].toString().trim().isEmpty())
								? String.valueOf(obj[75]) : null;

				String stateUTTaxAmountAdjusted = (obj[76] != null
						&& !obj[76].toString().trim().isEmpty())
								? String.valueOf(obj[76]) : null;

				String advaloremCessAmountAdjusted = (obj[77] != null
						&& !obj[77].toString().trim().isEmpty())
								? String.valueOf(obj[77]) : null;

				String specificCessAmountAdjusted = (obj[78] != null
						&& !obj[78].toString().trim().isEmpty())
								? String.valueOf(obj[78]) : null;
				String stateCessAmountAdjusted = (obj[79] != null
						&& !obj[79].toString().trim().isEmpty())
								? String.valueOf(obj[79]) : null;
				String reverseChargeFlag = (obj[80] != null
						&& !obj[80].toString().trim().isEmpty())
								? String.valueOf(obj[80]) : null;
				String eligibilityIndicator = (obj[81] != null
						&& !obj[81].toString().trim().isEmpty())
								? String.valueOf(obj[81]) : null;

				String commonSupplyIndicator = (obj[82] != null
						&& !obj[82].toString().trim().isEmpty())
								? String.valueOf(obj[82]) : null;
				String availableIGST = (obj[83] != null
						&& !obj[83].toString().trim().isEmpty())
								? String.valueOf(obj[83]) : null;
				String availableCGST = (obj[84] != null
						&& !obj[84].toString().trim().isEmpty())
								? String.valueOf(obj[84]) : null;

				String availableSGST = (obj[85] != null
						&& !obj[85].toString().trim().isEmpty())
								? String.valueOf(obj[85]) : null;
				String availableCess = (obj[86] != null
						&& !obj[86].toString().trim().isEmpty())
								? String.valueOf(obj[86]) : null;
				String itcEntitlement = (obj[87] != null
						&& !obj[87].toString().trim().isEmpty())
								? String.valueOf(obj[87]) : null;
				String itcReversalIdentifier = (obj[88] != null
						&& !obj[88].toString().trim().isEmpty())
								? String.valueOf(obj[88]) : null;
				String reasonForCreditDebitNote = (obj[89] != null
						&& !obj[89].toString().trim().isEmpty())
								? String.valueOf(obj[89]) : null;
				String purchaseVoucherNumber = (obj[90] != null
						&& !obj[90].toString().trim().isEmpty())
								? String.valueOf(obj[90]) : null;
				String purchaseVoucherDate = (obj[91] != null
						&& !obj[91].toString().trim().isEmpty())
								? String.valueOf(obj[91]) : null;
				LocalDate localPurchaseVoucherDate = DateUtil
						.parseObjToDate(purchaseVoucherDate);
				String postingDate = (obj[92] != null
						&& !obj[92].toString().trim().isEmpty())
								? String.valueOf(obj[92]) : null;
				LocalDate localPostingDate = DateUtil
						.parseObjToDate(postingDate);

				String paymentVoucherNumber = (obj[93] != null
						&& !obj[93].toString().trim().isEmpty())
								? String.valueOf(obj[93]) : null;
				String paymentDate = (obj[94] != null
						&& !obj[94].toString().trim().isEmpty())
								? String.valueOf(obj[94]) : null;
				LocalDate localPaymentDate = DateUtil
						.parseObjToDate(paymentDate);

				String contractNum = (obj[95] != null
						&& !obj[95].toString().trim().isEmpty())
								? String.valueOf(obj[95]) : null;
				String contractDate = (obj[96] != null
						&& !obj[96].toString().trim().isEmpty())
								? String.valueOf(obj[96]) : null;
				LocalDate localContractDate = DateUtil
						.parseObjToDate(contractDate);

				String contractVal = (obj[97] != null
						&& !obj[97].toString().trim().isEmpty())
								? String.valueOf(obj[97]) : null;
				String userDefinedField1 = (obj[98] != null
						&& !obj[98].toString().trim().isEmpty())
								? String.valueOf(obj[98]) : null;
				String userDefinedField2 = (obj[99] != null
						&& !obj[99].toString().trim().isEmpty())
								? String.valueOf(obj[99]) : null;
				String userDefinedField3 = (obj[100] != null
						&& !obj[100].toString().trim().isEmpty())
								? String.valueOf(obj[100]) : null;
				String userDefinedField4 = (obj[101] != null
						&& !obj[101].toString().trim().isEmpty())
								? String.valueOf(obj[101]) : null;
				String userDefinedField5 = (obj[102] != null
						&& !obj[102].toString().trim().isEmpty())
								? String.valueOf(obj[102]) : null;
				String userDefinedField6 = (obj[103] != null
						&& !obj[103].toString().trim().isEmpty())
								? String.valueOf(obj[103]) : null;
				String userDefinedField7 = (obj[104] != null
						&& !obj[104].toString().trim().isEmpty())
								? String.valueOf(obj[104]) : null;
				String userDefinedField8 = (obj[105] != null
						&& !obj[105].toString().trim().isEmpty())
								? String.valueOf(obj[105]) : null;
				String userDefinedField9 = (obj[106] != null
						&& !obj[106].toString().trim().isEmpty())
								? String.valueOf(obj[106]) : null;

				String userDefinedField10 = (obj[107] != null
						&& !obj[107].toString().trim().isEmpty())
								? String.valueOf(obj[107]) : null;

				String userDefinedField11 = (obj[108] != null
						&& !obj[108].toString().trim().isEmpty())
								? String.valueOf(obj[108]) : null;

				String userDefinedField12 = (obj[109] != null
						&& !obj[109].toString().trim().isEmpty())
								? String.valueOf(obj[109]) : null;

				String userDefinedField13 = (obj[110] != null
						&& !obj[110].toString().trim().isEmpty())
								? String.valueOf(obj[110]) : null;

				String userDefinedField14 = (obj[111] != null
						&& !obj[111].toString().trim().isEmpty())
								? String.valueOf(obj[111]) : null;

				String userDefinedField15 = (obj[112] != null
						&& !obj[112].toString().trim().isEmpty())
								? String.valueOf(obj[112]) : null;

				String eWayBillNumber = (obj[113] != null
						&& !obj[113].toString().trim().isEmpty())
								? String.valueOf(obj[113]) : null;
				String eWayBillDate = (obj[114] != null
						&& !obj[114].toString().trim().isEmpty())
								? String.valueOf(obj[114]) : null;
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
					idStr = (obj[115] != null && !obj[115].toString().isEmpty())
							? String.valueOf(obj[115]) : null;
					fileIdStr = (obj[116] != null
							&& !obj[116].toString().isEmpty())
									? String.valueOf(obj[116]) : null;
				}
				/**
				 * End - Inward Document Structural Validation Error Correction
				 * Implementation
				 */

				// Line Items

				if (gLCodeTaxableValue != null) {
					lineItem.setGlCodeTaxableValue(gLCodeTaxableValue);
				}
				if (gLCodeIGST != null) {
					lineItem.setGlCodeIgst(gLCodeIGST);
				}

				lineItem.setGlCodeCgst(glCodeCGST);
				lineItem.setGlCodeSgst(glCodeSGST);
				lineItem.setGlCodeAdvCess(gLCodeAdvaloremCess);
				lineItem.setGlCodeSpCess(gLCodeSpecificCess);
				lineItem.setGlCodeStateCess(gLCodeStateCess);
				lineItem.setSupplyType(supplyType);
				lineItem.setOrigDocNo(origDocNo);
				if (orgDocDateLocal != null) {
					lineItem.setOrigDocDate(String.valueOf(orgDocDateLocal));
				} else {
					lineItem.setOrigDocDate(origDocDate);
				}

				lineItem.setLineNo(lineNo);

				lineItem.setCifValue(cifValue);

				lineItem.setCustomDuty(customDuty);

				lineItem.setHsnSac(hsnOrSacStr);
				lineItem.setItemCode(productCode);
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

				lineItem.setCessAmountSpecific(cessAmountSpecific);

				lineItem.setStateCessRate(stateCessRate);

				lineItem.setStateCessAmount(stateCessAmount);

				lineItem.setOtherValues(otherValue);

				lineItem.setItemAmount(invoiceValue);

				lineItem.setAdjustmentRefNo(adjustementReferenceNo);
				if (localAdjustementReferenceDate != null) {
					lineItem.setAdjustmentRefDate(
							String.valueOf(localAdjustementReferenceDate));
				} else {
					lineItem.setAdjustmentRefDate(adjustementReferenceDate);
				}

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
				if (localPurchaseVoucherDate != null) {
					lineItem.setPurchaseVoucherDate(
							String.valueOf(localPurchaseVoucherDate));
				} else {
					lineItem.setPurchaseVoucherDate(purchaseVoucherDate);
				}

				lineItem.setPaymentVoucherNumber(paymentVoucherNumber);
				if (localPaymentDate != null) {
					lineItem.setPaymentVoucherDate(
							String.valueOf(localPaymentDate));
				} else {
					lineItem.setPaymentVoucherDate(paymentDate);
				}

				lineItem.setContractNumber(contractNum);
				if (localContractDate != null) {
					lineItem.setContractDate(String.valueOf(localContractDate));
				} else {
					lineItem.setContractDate(contractDate);
				}

				lineItem.setContractValue(contractVal);

				lineItem.setProfitCentre(profitCentre);
				lineItem.setPlantCode(plant);
				lineItem.setLocation(location);

				lineItem.setUserDefinedField1(userDefinedField1);
				lineItem.setUserDefinedField2(userDefinedField2);
				lineItem.setUserDefinedField3(userDefinedField3);
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

				lineItem.setUserId(userId);
				lineItem.setSourceFileName(sourceFileName);
				lineItem.setDivision(division);
				lineItem.setPurchaseOrganization(purchaseOrganisation);
				lineItem.setUserAccess1(userAccess1);
				lineItem.setUserAccess2(userAccess2);
				lineItem.setUserAccess3(userAccess3);
				lineItem.setUserAccess4(userAccess4);
				lineItem.setUserAccess5(userAccess5);
				lineItem.setUserAccess6(userAccess6);
				lineItem.setTaxperiod(returnPeriod);

				if (localDocDate != null) {
					lineItem.setDocDate(String.valueOf(localDocDate));
				} else {
					lineItem.setDocDate(docDate);
				}

				lineItem.setCrDrPreGst(crDrPreGst);
				lineItem.setSupplierLegalName(custSupType);
				lineItem.setDiffPercent(differentialFlag);
				lineItem.setOrigSgstin(origSgstin);
				lineItem.setSupplierLegalName(suppName);
				lineItem.setCustOrSuppCode(suppCode);
				lineItem.setCustOrSuppAddress1(supplierAddress1);
				lineItem.setCustOrSuppAddress2(supplierAddress2);
				lineItem.setCustOrSuppAddress3(supplierAddress3);
				lineItem.setCustOrSuppAddress4(supplierAddress4);
				lineItem.setPos(pos);
				lineItem.setStateApplyingCess(stateApplyingCess);
				lineItem.setPortCode(portCode);
				lineItem.setBillOfEntryNo(billOfEntry);

				if (localBillOfEntryDate != null) {
					lineItem.setBillOfEntryDate(
							String.valueOf(localBillOfEntryDate));
				} else {
					lineItem.setBillOfEntryDate(billOfEntryDate);
				}

				lineItem.setSection7OfIgstFlag(section7ofIGSTFlag);
				lineItem.setClaimRefundFlag(claimRefundFlag);
				lineItem.setAutoPopToRefundFlag(autoPopulateToRefundFlag);
				lineItem.setReverseCharge(reverseChargeFlag);
				lineItem.setItcEntitlement(itcEntitlement);
				lineItem.setPostingDate(postingDate);
				if (localEWayBillDate != null) {
					lineItem.setEWayBillDate(String.valueOf(localEWayBillDate));
				} else {
					lineItem.setEWayBillDate(eWayBillDate);
				}

				if (fileStatus != null) {
					document.setAcceptanceId(fileStatus.getId());
					lineItem.setAcceptanceId(fileStatus.getId());
				} else {
					document.setAcceptanceId(Long.valueOf(802989));
					lineItem.setAcceptanceId(Long.valueOf(802989));
				}
				/* headers into item */

				lineItem.setUserId(userId);
				lineItem.setSourceFileName(sourceFileName);
				lineItem.setProfitCentre(profitCentre);
				lineItem.setPlantCode(plant);
				lineItem.setDivision(division);
				lineItem.setLocation(location);
				lineItem.setPurchaseOrganization(purchaseOrganisation);
				lineItem.setUserAccess1(userAccess1);
				lineItem.setUserAccess2(userAccess2);
				lineItem.setUserAccess3(userAccess3);
				lineItem.setUserAccess4(userAccess4);
				lineItem.setUserAccess5(userAccess5);
				lineItem.setUserAccess6(userAccess6);
				lineItem.setTaxperiod(returnPeriod);
				lineItem.setCgstin(recipientGSTIN);
				lineItem.setDocType(documentType);
				lineItem.setDataOriginTypeCode(
						GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
								.getDataOriginTypeCode());
				lineItem.setDocNo(documentNumber);
				if (localDocDate != null) {
					lineItem.setDocDate(String.valueOf(localDocDate));
				} else {
					lineItem.setDocDate(docDate);
				}

				lineItem.setCrDrPreGst(crDrPreGst);
				lineItem.setSgstin(sgstin);
				lineItem.setSupplyType(supplyType);
				lineItem.setDiffPercent(differentialFlag);
				lineItem.setOrigSgstin(origSgstin);
				lineItem.setSupplierType(custSupType);
				lineItem.setSupplierLegalName(suppName);
				lineItem.setCustOrSuppCode(suppCode);
				lineItem.setCustOrSuppAddress1(supplierAddress1);
				lineItem.setCustOrSuppAddress2(supplierAddress2);
				lineItem.setCustOrSuppAddress3(supplierAddress3);
				lineItem.setCustOrSuppAddress4(supplierAddress4);
				lineItem.setPos(pos);
				lineItem.setStateApplyingCess(stateApplyingCess);
				lineItem.setPortCode(portCode);
				lineItem.setBillOfEntryNo(billOfEntry);
				if (derivedRetPeriod != null) {
					lineItem.setDerivedTaxperiod(derivedRetPeriod);
				} else {
					lineItem.setDerivedTaxperiod(999999);
				}
				if (localBillOfEntryDate != null) {
					lineItem.setBillOfEntryDate(
							String.valueOf(localBillOfEntryDate));
				} else {
					lineItem.setBillOfEntryDate(billOfEntryDate);
				}

				lineItem.setSection7OfIgstFlag(section7ofIGSTFlag);

				lineItem.setClaimRefundFlag(claimRefundFlag);

				lineItem.setAutoPopToRefundFlag(autoPopulateToRefundFlag);

				lineItem.setReverseCharge(reverseChargeFlag);
				lineItem.setItcEntitlement(itcEntitlement);
				if (localPostingDate != null) {
					lineItem.setPostingDate(String.valueOf(localPostingDate));
				} else {
					lineItem.setPostingDate(postingDate);
				}

				lineItem.setEWayBillNo(eWayBillNumber);
				if (localEWayBillDate != null) {
					lineItem.setEWayBillDate(String.valueOf(localEWayBillDate));
				} else {
					lineItem.setEWayBillDate(eWayBillDate);
				}

				lineItem.setIsError("true");
				lineItem.setIsProcessed("false");
				lineItem.setIsDeleted("false");

				if (idStr != null) {
					try {
						Long id = Long.parseLong(idStr);
						lineItem.setId(id);
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
						lineItem.setAcceptanceId(fileId);
					} catch (RuntimeException e) {
						throw new AppException(
								" Error occuured while converting "
										+ "String file id to Long  file id "
										+ e.getMessage());
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
				}
				int maxlength = 99;
				if (key.length() > maxlength) {
					lineItem.setDocKey(key.substring(0, maxlength));
				} else {
					lineItem.setDocKey(key);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
					LOGGER.debug("Anx2RawFileToInwardTransDocErrConvertion "
							+ "convertAnx2RawFileToInWardTransError With "
							+ "in for end");
				}
				// lineItem.setLineItems(lineItems);
				lineItem.setCreatedBy(userName);
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				// lineItem.setCreatedDate(convertNow);

				/* Headers into Item */
				lineItems.add(lineItem); // Add Line Items

				/*
				 * if (fileStatus != null) {
				 * document.setAcceptanceId(fileStatus.getId()); }
				 */
				if (derivedRetPeriod != null) {
					lineItem.setDerivedTaxperiod(derivedRetPeriod);
				} else {
					lineItem.setDerivedTaxperiod(999999);
				}
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
				document.setDataOriginTypeCode(
						GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
								.getDataOriginTypeCode());
				document.setDocNo(documentNumber);
				if (localDocDate != null) {
					document.setDocDate(String.valueOf(localDocDate));
				} else {
					document.setDocDate(docDate);
				}

				document.setCrDrPreGst(crDrPreGst);
				document.setSgstin(sgstin);
				document.setSupplyType(supplyType);
				document.setDiffPercent(differentialFlag);
				document.setOrigSgstin(origSgstin);
				document.setSupplierType(custSupType);
				document.setSupplierLegalName(suppName);
				document.setCustOrSuppCode(suppCode);
				document.setCustOrSuppAddress1(supplierAddress1);
				document.setCustOrSuppAddress2(supplierAddress2);
				document.setCustOrSuppAddress3(supplierAddress3);
				document.setCustOrSuppAddress4(supplierAddress4);
				document.setPos(pos);
				document.setStateApplyingCess(stateApplyingCess);
				document.setPortCode(portCode);
				document.setBillOfEntryNo(billOfEntry);
				if (derivedRetPeriod != null) {
					document.setDerivedTaxperiod(derivedRetPeriod);
				} else {
					document.setDerivedTaxperiod(999999);
				}
				if (localBillOfEntryDate != null) {
					document.setBillOfEntryDate(
							String.valueOf(localBillOfEntryDate));
				} else {
					document.setBillOfEntryDate(billOfEntryDate);
				}

				document.setSection7OfIgstFlag(section7ofIGSTFlag);

				document.setClaimRefundFlag(claimRefundFlag);

				document.setAutoPopToRefundFlag(autoPopulateToRefundFlag);

				document.setReverseCharge(reverseChargeFlag);
				document.setItcEntitlement(itcEntitlement);
				if (localPostingDate != null) {
					document.setPostingDate(String.valueOf(localPostingDate));
				} else {
					document.setPostingDate(postingDate);
				}

				document.setEWayBillNo(eWayBillNumber);
				if (localEWayBillDate != null) {
					document.setEWayBillDate(String.valueOf(localEWayBillDate));
				} else {
					document.setEWayBillDate(eWayBillDate);
				}

				document.setIsError("true");
				document.setIsProcessed("false");
				document.setIsDeleted("false");

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
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
				}
				// int maxlength = 99;
				if (key.length() > maxlength) {
					document.setDocKey(key.substring(0, maxlength));
				} else {
					document.setDocKey(key);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
					LOGGER.debug("Anx2RawFileToInwardTransDocErrConvertion "
							+ "convertAnx2RawFileToInWardTransError With "
							+ "in for end");
				}
			}
			document.setLineItems(lineItems);
			document.setCreatedBy(userName);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setCreatedDate(convertNow);
			documents.add(document);
			document.getLineItems().forEach(item -> {
				item.setDocument(document);
			});
		});
		return documents;

	}

}
