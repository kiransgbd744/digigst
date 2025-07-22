package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;

import java.math.BigDecimal;
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
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("SRFileToOutwardTransDocConvertion")
public class SRFileToOutwardTransDocConvertion {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SRFileToOutwardTransDocConvertion.class);

	public List<OutwardTransDocument> convertSRFileToOutwardTransDoc(
			Map<String, List<Object[]>> documentMap,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<OutwardTransDocument> documents = new ArrayList<>();

		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			OutwardTransDocument document = new OutwardTransDocument();
			List<OutwardTransDocLineItem> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				// TO-DO Structural Validations
				OutwardTransDocLineItem lineItem = new OutwardTransDocLineItem();
				String userId = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
				String sourceFileName = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				String profitCentre = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				String plantCode = (obj[3] != null
						&& !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
				String division = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				String location = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
				String salesOrg = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]) : null;
				String distChannel = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;
				String userAccess1 = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				String userAccess2 = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				String userAccess3 = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				String userAccess4 = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;
				String userAccess5 = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;
				String userAccess6 = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;

				String glCodeTaxable = (obj[14] != null
						&& !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;
				String glCodeIgst = (obj[15] != null
						&& !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]).trim() : null;
				String glCodeCgst = (obj[16] != null
						&& !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;
				String glCodeSgst = (obj[17] != null
						&& !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]).trim() : null;
				String glAdvaloremCess = (obj[18] != null
						&& !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;
				String glAdvaloremSpecificCess = (obj[19] != null
						&& !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;
				String glStateCess = (obj[20] != null
						&& !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;

				String returnPeriod = (obj[21] != null
						&& !obj[21].toString().trim().isEmpty())
								? String.valueOf(obj[21]).trim() : null;
				String sgstin = (obj[22] != null
						&& !obj[22].toString().trim().isEmpty())
								? String.valueOf(obj[22]).trim() : null;
				String docType = (obj[23] != null
						&& !obj[23].toString().trim().isEmpty())
								? String.valueOf(obj[23]).trim() : null;
				String supplyType = (obj[24] != null
						&& !obj[24].toString().trim().isEmpty())
								? String.valueOf(obj[24]).trim() : null;

				String docNo = (obj[25] != null
						&& !obj[25].toString().trim().isEmpty())
								? String.valueOf(obj[25]).trim() : null;
				if (docNo != null && !docNo.trim().isEmpty()) {
					if (isDecimal(docNo.trim()) || docNo
							.matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")) {
						BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
						docNoDecimalFormat = new BigDecimal(docNo);
						Long supplierPhoneLong = docNoDecimalFormat.longValue();
						docNo = String.valueOf(supplierPhoneLong);
					}
				}

				String docDate = (obj[26] != null
						&& !obj[26].toString().trim().isEmpty())
								? String.valueOf(obj[26]).trim() : null;
				LocalDate localDocDate = DateUtil.parseObjToDate(docDate);

				String finYear = GenUtil.getFinYear(localDocDate);
				String origDocType = null;
				if ((obj[27] != null && !obj[27].toString().trim().isEmpty())) {
					origDocType = String.valueOf(obj[27]).trim();
				}

				String predingInvNo = (obj[28] != null
						&& !obj[28].toString().trim().isEmpty())
								? String.valueOf(obj[28]).trim() : null;
				String predingInvDate = (obj[29] != null
						&& !obj[29].toString().trim().isEmpty())
								? String.valueOf(obj[29]).trim() : null;

				LocalDate localPrecDocDate = DateUtil
						.parseObjToDate(predingInvDate);

				String crDrPreGst = (obj[30] != null
						&& !obj[30].toString().trim().isEmpty())
								? String.valueOf(obj[30]).trim()
								: GSTConstants.N;

				// Line Items
				String lineNo =(obj[31] != null
						&& !obj[31].toString().trim().isEmpty())
						? String.valueOf(obj[31]).trim() : null;
				/*if (obj[31] != null && !obj[31].toString().trim().isEmpty()) {
					String lineNoStr = (String.valueOf(obj[31])).trim();
					lineNo = Integer.valueOf(lineNoStr);
				}*/

				String cgstin = (obj[32] != null
						&& !obj[32].toString().trim().isEmpty())
								? String.valueOf(obj[32]).trim() : null;
				String custType = (obj[33] != null
						&& !obj[33].toString().trim().isEmpty())
								? String.valueOf(obj[33]).trim() : null;
				String diffPercFlag = GSTConstants.N;
				if (obj[34] != null && !obj[34].toString().trim().isEmpty()) {
					diffPercFlag = String.valueOf(obj[34]).trim();
				}
				String orgCustGstin = (obj[35] != null
						&& !obj[35].toString().trim().isEmpty())
								? String.valueOf(obj[35]).trim() : null;
				String custName = (obj[36] != null
						&& !obj[36].toString().trim().isEmpty())
								? String.valueOf(obj[36]).trim() : null;
				String custCode = (obj[37] != null
						&& !obj[37].toString().trim().isEmpty())
								? String.valueOf(obj[37]).trim() : null;
				String custAdd1 = (obj[38] != null
						&& !obj[38].toString().trim().isEmpty())
								? String.valueOf(obj[38]).trim() : null;
				String custAdd2 = (obj[39] != null
						&& !obj[39].toString().trim().isEmpty())
								? String.valueOf(obj[39]).trim() : null;
				String custAdd3 = (obj[40] != null
						&& !obj[40].toString().trim().isEmpty())
								? String.valueOf(obj[40]).trim() : null;
				String custAdd4 = (obj[41] != null
						&& !obj[41].toString().trim().isEmpty())
								? String.valueOf(obj[41]).trim() : null;
				String billToState = (obj[42] != null
						&& !obj[42].toString().trim().isEmpty())
								? String.valueOf(obj[42]).trim() : null;
				String shipToState = (obj[43] != null
						&& !obj[43].toString().trim().isEmpty())
								? String.valueOf(obj[43]).trim() : null;
				String pos = (obj[44] != null
						&& !obj[44].toString().trim().isEmpty())
								? String.valueOf(obj[44]).trim() : null;
				String stateApplyCess = (obj[45] != null
						&& !obj[45].toString().trim().isEmpty())
								? String.valueOf(obj[45]).trim() : null;
				String portCode = (obj[46] != null
						&& !obj[46].toString().trim().isEmpty())
								? String.valueOf(obj[46]).trim() : null;
				String shippingBillNo = (obj[47] != null
						&& !obj[47].toString().trim().isEmpty())
								? String.valueOf(obj[47]).trim() : null;
				String shippingBillDate = (obj[48] != null
						&& !obj[48].toString().trim().isEmpty())
								? String.valueOf(obj[48]).trim() : null;
				LocalDate localShipBillDate = DateUtil
						.parseObjToDate(shippingBillDate);

				BigDecimal fob = null;
				if (obj[49] != null && !obj[49].toString().trim().isEmpty()) {
					String fobStr = String.valueOf(obj[49]).trim();
					fob = new BigDecimal(fobStr);
				}
				BigDecimal exportDuty = null;
				if (obj[50] != null && !obj[50].toString().trim().isEmpty()) {
					String exportDutyStr = String.valueOf(obj[50]).trim();
					exportDuty = new BigDecimal(exportDutyStr);
				}
				String hSNorSAC = (obj[51] != null
						&& !obj[51].toString().trim().isEmpty())
								? String.valueOf(obj[51]).trim() : null;
				String productCode = (obj[52] != null
						&& !obj[52].toString().trim().isEmpty())
								? String.valueOf(obj[52]).trim() : null;
				String productDesc = (obj[53] != null
						&& !obj[53].toString().trim().isEmpty())
								? String.valueOf(obj[53]).trim() : null;
				String productCategory = (obj[54] != null
						&& !obj[54].toString().trim().isEmpty())
								? String.valueOf(obj[54]).trim() : null;
				String uom = (obj[55] != null
						&& !obj[55].toString().trim().isEmpty())
								? String.valueOf(obj[55]).trim() : null;

				BigDecimal qty = null;
				if (obj[56] != null && !obj[56].toString().trim().isEmpty()) {
					qty = new BigDecimal(String.valueOf(obj[56]).trim());
				}
				String sec7OfIGSTFlag = (obj[57] != null
						&& !obj[57].toString().trim().isEmpty())
								? String.valueOf(obj[57]).trim()
								: GSTConstants.N;

				BigDecimal taxableVal = null;
				if (obj[58] != null && !obj[58].toString().trim().isEmpty()) {
					taxableVal = new BigDecimal(String.valueOf(obj[58]).trim());
				}

				BigDecimal igstRate = null;
				if (obj[59] != null && !obj[59].toString().trim().isEmpty()) {
					igstRate = new BigDecimal(String.valueOf(obj[59]).trim());
				}

				BigDecimal igstAmount = null;
				if (obj[60] != null && !obj[60].toString().trim().isEmpty()) {
					igstAmount = new BigDecimal(String.valueOf(obj[60]).trim());
				}

				BigDecimal cgstRate = null;
				if (obj[61] != null && !obj[61].toString().trim().isEmpty()) {
					cgstRate = new BigDecimal(String.valueOf(obj[61]).trim());
				}

				BigDecimal cgstAmount = null;
				if (obj[62] != null && !obj[62].toString().trim().isEmpty()) {
					cgstAmount = new BigDecimal(String.valueOf(obj[62]).trim());
				}

				BigDecimal sgstRate = null;
				if (obj[63] != null && !obj[63].toString().trim().isEmpty()) {
					sgstRate = new BigDecimal(String.valueOf(obj[63]).trim());
				}

				BigDecimal sgstAmount = null;
				if (obj[64] != null && !obj[64].toString().trim().isEmpty()) {
					sgstAmount = new BigDecimal(String.valueOf(obj[64]).trim());
				}

				BigDecimal cessRateAdvalorem = null;
				if (obj[65] != null && !obj[65].toString().trim().isEmpty()) {
					cessRateAdvalorem = new BigDecimal(
							String.valueOf(obj[65]).trim());
				}

				BigDecimal cessAmtAdvalorem = null;
				if (obj[66] != null && !obj[66].toString().trim().isEmpty()) {
					cessAmtAdvalorem = new BigDecimal(
							String.valueOf(obj[66]).trim());
				}

				BigDecimal cessRateSpecific = null;
				if (obj[67] != null && !obj[67].toString().trim().isEmpty()) {
					cessRateSpecific = new BigDecimal(
							String.valueOf(obj[67]).trim());
				}

				BigDecimal cessAmountSpecific = null;
				if (obj[68] != null && !obj[68].toString().trim().isEmpty()) {
					cessAmountSpecific = new BigDecimal(
							String.valueOf(obj[68]).trim());
				}

				BigDecimal cessRateState = null;
				if (obj[69] != null && !obj[69].toString().trim().isEmpty()) {
					String cessRateStateStr = String.valueOf(obj[69]).trim();
					cessRateState = new BigDecimal(cessRateStateStr);
				}

				BigDecimal cessAmountState = null;
				if (obj[70] != null && !obj[70].toString().trim().isEmpty()) {
					cessAmountState = new BigDecimal(
							String.valueOf(obj[70]).trim());
				}

				BigDecimal otherValue = null;
				if (obj[71] != null && !obj[71].toString().trim().isEmpty()) {
					otherValue = new BigDecimal(String.valueOf(obj[71]).trim());
				}
				BigDecimal invoiceValue = null;
				if (obj[72] != null && !obj[72].toString().trim().isEmpty()) {
					invoiceValue = new BigDecimal(
							String.valueOf(obj[72]).trim());
				}

				String adjustRefNo = (obj[73] != null
						&& !obj[73].toString().trim().isEmpty())
								? String.valueOf(obj[73]).trim() : null;
				String adjestementDate = (obj[74] != null
						&& !obj[74].toString().trim().isEmpty())
								? String.valueOf(obj[74]).trim() : null;
				LocalDate adjestementLocalDate = DateUtil
						.parseObjToDate(adjestementDate);

				BigDecimal adjustedTaxValue = null;
				if (obj[75] != null && !obj[75].toString().trim().isEmpty()) {
					adjustedTaxValue = new BigDecimal(
							String.valueOf(obj[75]).trim());
				}
				BigDecimal adjustedIgstAmt = null;
				if (obj[76] != null && !obj[76].toString().trim().isEmpty()) {
					adjustedIgstAmt = new BigDecimal(
							String.valueOf(obj[76]).trim());
				}
				BigDecimal adjustedCgstAmt = null;
				if (obj[77] != null && !obj[77].toString().trim().isEmpty()) {
					String adjustedCgstAmtStr = String.valueOf(obj[77]).trim();
					adjustedCgstAmt = new BigDecimal(adjustedCgstAmtStr);
				}
				BigDecimal adjustedSgstAmt = null;
				if (obj[78] != null && !obj[78].toString().trim().isEmpty()) {
					adjustedSgstAmt = new BigDecimal(
							String.valueOf(obj[78]).trim());
				}
				BigDecimal adjustedAdvaCessAmt = null;
				if (obj[79] != null && !obj[79].toString().trim().isEmpty()) {
					adjustedAdvaCessAmt = new BigDecimal(
							String.valueOf(obj[79]).trim());
				}
				BigDecimal adjustedSpecificCessAmt = null;
				if (obj[80] != null && !obj[80].toString().trim().isEmpty()) {
					adjustedSpecificCessAmt = new BigDecimal(
							String.valueOf(obj[80]).trim());
				}
				BigDecimal adjustedStateCessAmt = null;
				if (obj[81] != null && !obj[81].toString().trim().isEmpty()) {
					adjustedStateCessAmt = new BigDecimal(
							String.valueOf(obj[81]).trim());
				}
				String reverseChargeFlag = (obj[82] != null
						&& !obj[82].toString().trim().isEmpty())
								? String.valueOf(obj[82]).trim() : null;

				String isTcs = GSTConstants.N;
				if (obj[83] != null && !obj[83].toString().trim().isEmpty()) {
					isTcs = String.valueOf(obj[83]).trim();
				}
				String egstin = (obj[84] != null
						&& !obj[84].toString().trim().isEmpty())
								? String.valueOf(obj[84]).trim() : null;
				BigDecimal tcsAmount = null;
				if (obj[85] != null && !obj[85].toString().trim().isEmpty()) {
					tcsAmount = new BigDecimal(String.valueOf(obj[85]).trim());
				}
				String itcFlag = (obj[86] != null
						&& !obj[86].toString().trim().isEmpty())
								? String.valueOf(obj[86]).trim() : null;

				String claimRefundFlag = (obj[87] != null
						&& !obj[87].toString().trim().isEmpty())
								? String.valueOf(obj[87]).trim()
								: GSTConstants.N;

				String autoPopulateToRef = (obj[88] != null
						&& !obj[88].toString().trim().isEmpty())
								? String.valueOf(obj[88]).trim()
								: GSTConstants.N;

				String reasonCreDebNote = (obj[89] != null
						&& !obj[89].toString().trim().isEmpty())
								? String.valueOf(obj[89]).trim() : null;
				String accVochNum = (obj[90] != null
						&& !obj[90].toString().trim().isEmpty())
								? String.valueOf(obj[90]).trim() : null;
				String accVochDate = (obj[91] != null
						&& !obj[91].toString().trim().isEmpty())
								? String.valueOf(obj[91]).trim() : null;
				LocalDate accVouchLocalDate = DateUtil
						.parseObjToDate(accVochDate);

				String userDefinedField1 = (obj[92] != null
						&& !obj[92].toString().trim().isEmpty())
								? String.valueOf(obj[92]).trim() : null;
				String userDefinedField2 = (obj[93] != null
						&& !obj[93].toString().trim().isEmpty())
								? String.valueOf(obj[93]).trim() : null;
				String userDefinedField3 = (obj[94] != null
						&& !obj[94].toString().trim().isEmpty())
								? String.valueOf(obj[94]).trim() : null;
				String userDefinedField4 = (obj[95] != null
						&& !obj[95].toString().trim().isEmpty())
								? String.valueOf(obj[95]).trim() : null;
				String userDefinedField5 = (obj[96] != null
						&& !obj[96].toString().trim().isEmpty())
								? String.valueOf(obj[96]).trim() : null;
				String userDefinedField6 = (obj[97] != null
						&& !obj[97].toString().trim().isEmpty())
								? String.valueOf(obj[97]).trim() : null;
				String userDefinedField7 = (obj[98] != null
						&& !obj[98].toString().trim().isEmpty())
								? String.valueOf(obj[98]).trim() : null;
				String userDefinedField8 = (obj[99] != null
						&& !obj[99].toString().trim().isEmpty())
								? String.valueOf(obj[99]).trim() : null;
				String userDefinedField9 = (obj[100] != null
						&& !obj[100].toString().trim().isEmpty())
								? String.valueOf(obj[100]).trim() : null;
				String userDefinedField10 = (obj[101] != null
						&& !obj[101].toString().trim().isEmpty())
								? String.valueOf(obj[101]).trim() : null;
				String userDefinedField11 = (obj[102] != null
						&& !obj[102].toString().trim().isEmpty())
								? String.valueOf(obj[102]).trim() : null;
				String userDefinedField12 = (obj[103] != null
						&& !obj[103].toString().trim().isEmpty())
								? String.valueOf(obj[103]).trim() : null;
				String userDefinedField13 = (obj[104] != null
						&& !obj[104].toString().trim().isEmpty())
								? String.valueOf(obj[104]).trim() : null;
				String userDefinedField14 = (obj[105] != null
						&& !obj[105].toString().trim().isEmpty())
								? String.valueOf(obj[105]).trim() : null;
				String userDefinedField15 = (obj[106] != null
						&& !obj[106].toString().trim().isEmpty())
								? String.valueOf(obj[106]).trim() : null;

				String eWayBillNo = (obj[107] != null
						&& !obj[107].toString().trim().isEmpty())
								? String.valueOf(obj[107]).trim() : null;

				String eWayBillDate = (obj[108] != null
						&& !obj[108].toString().trim().isEmpty())
								? String.valueOf(obj[108]).trim() : null;
				LocalDate eWayBillLocalDate = DateUtil
						.parseObjToDate(eWayBillDate);

				/**
				 * Start - Outward Document Structural Validation Error
				 * Correction Implementation
				 */
				// File ID and Id are not null only in case of Inward Doc
				// Structural Validation Error Correction
				String idStr = null;
				String fileIdStr = null;
				if (obj.length > 109) {
					fileIdStr = (obj[110] != null
							&& !obj[110].toString().trim().isEmpty())
									? String.valueOf(obj[110]) : null;
					idStr = (obj[111] != null
							&& !obj[111].toString().trim().isEmpty())
									? String.valueOf(obj[111]) : null;
				}
				/**
				 * End - Outward Document Structural Validation Error Correction
				 * Implementation
				 */

				lineItem.setLineNo(lineNo);
				lineItem.setSupplyType(supplyType);
				lineItem.setItemCode(productCode);
				lineItem.setItemDescription(productDesc);
				lineItem.setItemCategory(productCategory);
				lineItem.setUom(uom);
				lineItem.setQty(qty);
				lineItem.setTaxableValue(taxableVal);
				lineItem.setGlCodeTaxableValue(glCodeTaxable);
				lineItem.setGlCodeIgst(glCodeIgst);
				lineItem.setGlCodeCgst(glCodeCgst);
				lineItem.setGlCodeSgst(glCodeSgst);
				lineItem.setGlCodeAdvCess(glAdvaloremCess);
				lineItem.setGlCodeSpCess(glAdvaloremSpecificCess);
				lineItem.setGlCodeStateCess(glStateCess);
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

				lineItem.setAdjustedCessAmtAdvalorem(adjustedAdvaCessAmt);
				lineItem.setAdjustedCessAmtSpecific(adjustedSpecificCessAmt);
				lineItem.setAdjustedCgstAmt(adjustedCgstAmt);
				lineItem.setAdjustedIgstAmt(adjustedIgstAmt);
				lineItem.setAdjustedSgstAmt(adjustedSgstAmt);
				lineItem.setAdjustmentRefNo(adjustRefNo);
				lineItem.setAdjustmentRefDate(adjestementLocalDate);
				lineItem.setOtherValues(otherValue);
				lineItem.setStateCessRate(cessRateState);
				lineItem.setStateCessAmount(cessAmountState);
				lineItem.setAdjustedStateCessAmt(adjustedStateCessAmt);
				lineItem.setAdjustedTaxableValue(adjustedTaxValue);
				lineItem.setTcsAmount(tcsAmount);
				if(hSNorSAC != null &&
						hSNorSAC.startsWith(GSTConstants.SPE_CHAR)){
					lineItem.setHsnSac(hSNorSAC.substring(1));
				}
				else{
					lineItem.setHsnSac(hSNorSAC);
				}
				lineItem.setLineItemAmt(invoiceValue);

				lineItem.setCrDrReason(reasonCreDebNote);
				lineItem.setPreceedingInvoiceNumber(predingInvNo);
				lineItem.setPreceedingInvoiceDate(localPrecDocDate);

				lineItem.setFob(fob);
				lineItem.setExportDuty(exportDuty);

				lineItem.setProfitCentre(profitCentre);
				lineItem.setPlantCode(plantCode);
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
				lineItem.setItcFlag(itcFlag);
				if (fileStatus != null) {
					document.setAcceptanceId(fileStatus.getId());
					lineItem.setAcceptanceId(fileStatus.getId());
				} else {
					document.setAcceptanceId(0L);
					lineItem.setAcceptanceId(0L);
				}
				lineItems.add(lineItem); // Add Line Items
				document.setReverseCharge(reverseChargeFlag);
				document.setClaimRefundFlag(claimRefundFlag);
				document.setDiffPercent(diffPercFlag);
				document.setUserId(userId);
				document.setStateApplyingCess(stateApplyCess);

				document.setSourceFileName(sourceFileName);
				document.setSalesOrgnization(salesOrg);
				document.setLocation(location);
				document.setDistributionChannel(distChannel);
				document.setOrigCgstin(orgCustGstin);
				document.setSection7OfIgstFlag(sec7OfIGSTFlag);
				document.setDivision(division);
				document.setProfitCentre(profitCentre);
				document.setPlantCode(plantCode);
				if(returnPeriod != null &&
						returnPeriod.startsWith(GSTConstants.SPE_CHAR)){
					document.setTaxperiod(returnPeriod.substring(1));
				}
				else{
					document.setTaxperiod(returnPeriod);
				}
				
				document.setSgstin(sgstin);
				document.setEgstin(egstin);

				document.setCrDrPreGst(crDrPreGst);
				document.setUserAccess1(userAccess1);
				document.setUserAccess2(userAccess2);
				document.setUserAccess3(userAccess3);
				document.setUserAccess4(userAccess4);
				document.setUserAccess5(userAccess5);
				document.setUserAccess6(userAccess6);

				document.setAccountingVoucherNumber(accVochNum);
				document.setAccountingVoucherDate(accVouchLocalDate);

				document.setDocType(docType);
				if(docNo != null && 
						docNo.startsWith(GSTConstants.SPE_CHAR)){
					document.setDocNo(docNo.substring(1));
				}
				else{
					document.setDocNo(docNo);
				}
				document.setDocDate(localDocDate);
				document.setOrigDocType(origDocType);

				document.setCgstin(cgstin);
				document.setCustOrSuppName(custName);
				document.setCustOrSuppType(custType);
				document.setCustOrSuppCode(custCode);
				document.setCustOrSuppAddress1(custAdd1);
				document.setCustOrSuppAddress2(custAdd2);
				document.setCustOrSuppAddress3(custAdd3);
				document.setCustOrSuppAddress4(custAdd4);
				document.setDataOriginTypeCode(
						GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
								.getDataOriginTypeCode());

				if(billToState != null && 
						billToState.startsWith(GSTConstants.SPE_CHAR)){
					document.setBillToState(billToState.substring(1));
				}
				else{
					document.setBillToState(billToState);
				}
				if(shipToState != null && 
						shipToState.startsWith(GSTConstants.SPE_CHAR)){
					document.setShipToState(shipToState.substring(1));
				}
				else{
					document.setShipToState(shipToState);
				}
				if(pos != null &&
						pos.startsWith(GSTConstants.SPE_CHAR)){
					document.setPos(pos.substring(1));
				}
				else{
					document.setPos(pos);
				}
				document.setPortCode(portCode);
				document.setShippingBillNo(shippingBillNo);
				document.setShippingBillDate(localShipBillDate);

				document.seteWayBillNo(eWayBillNo);
				//document.seteWayBillDate(eWayBillLocalDate);
				document.seteWayBillDate(eWayBillLocalDate != null ? 
						eWayBillLocalDate.atStartOfDay() : null);

				document.setTcsFlag(isTcs);

				document.setEgstin(egstin);

				document.setAutoPopToRefundFlag(autoPopulateToRef);
				document.setDocKey(key);
				document.setFinYear(finYear);
				document.setPreceedingInvoiceNumber(predingInvNo);
				document.setPreceedingInvoiceDate(localPrecDocDate);
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
			}
			document.setLineItems(lineItems);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setCreatedDate(convertNow);
			document.setCreatedBy(userName);
			documents.add(document);

		});
		return documents;
	}
}
