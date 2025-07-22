package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.AnxOutwardTransDocLineItemError;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Umesha M
 *
 */
@Service("SRFileToOutwardTransDocErrConvertion")
public class SRFileToOutwardTransDocErrConvertion {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SRFileToOutwardTransDocErrConvertion.class);

	/**
	 * 
	 * @param errDocMapObj
	 * @param documentKeyBuilder
	 * @param fileStatus
	 * @return
	 */
	public List<Anx1OutWardErrHeader> convertSRFileToOutWardTransError(
			Map<String, List<Object[]>> errDocMapObj,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Begining");
		List<Anx1OutWardErrHeader> errorHeaders = new ArrayList<>();
		try {
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				Anx1OutWardErrHeader errorDocument = new Anx1OutWardErrHeader();
				List<AnxOutwardTransDocLineItemError> lineItems = new ArrayList<>();
				for (Object[] obj : objs) {
					AnxOutwardTransDocLineItemError lineItem = new AnxOutwardTransDocLineItemError();
					String userId = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]) : null;
					String sourceFileName = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]) : null;
					String profitCentre = (obj[2] != null
							&& !obj[2].toString().trim().isEmpty())
									? String.valueOf(obj[2]) : null;
					String plantCode = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]) : null;
					String division = (obj[4] != null
							&& !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]) : null;
					String location = (obj[5] != null
							&& !obj[5].toString().trim().isEmpty())
									? String.valueOf(obj[5]) : null;
					String salesOrg = (obj[6] != null
							&& !obj[6].toString().trim().isEmpty())
									? String.valueOf(obj[6]) : null;
					String distChannel = (obj[7] != null
							&& !obj[7].toString().trim().isEmpty())
									? String.valueOf(obj[7]) : null;
					String userAccess1 = (obj[8] != null
							&& !obj[8].toString().trim().isEmpty())
									? String.valueOf(obj[8]) : null;
					String userAccess2 = (obj[9] != null
							&& !obj[9].toString().trim().isEmpty())
									? String.valueOf(obj[9]) : null;
					String userAccess3 = (obj[10] != null
							&& !obj[10].toString().trim().isEmpty())
									? String.valueOf(obj[10]) : null;
					String userAccess4 = (obj[11] != null
							&& !obj[11].toString().trim().isEmpty())
									? String.valueOf(obj[11]) : null;
					String userAccess5 = (obj[12] != null
							&& !obj[12].toString().trim().isEmpty())
									? String.valueOf(obj[12]) : null;
					String userAccess6 = (obj[13] != null
							&& !obj[13].toString().trim().isEmpty())
									? String.valueOf(obj[13]) : null;

					String glCodeTaxable = (obj[14] != null
							&& !obj[14].toString().trim().isEmpty())
									? String.valueOf(obj[14]) : null;
					String glCodeIgst = (obj[15] != null
							&& !obj[15].toString().trim().isEmpty())
									? String.valueOf(obj[15]) : null;
					String glCodeCgst = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]) : null;
					String glCodeSgst = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]) : null;
					String glAdvaloremCess = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]) : null;
					String glAdvaloremSpecificCess = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]) : null;
					String glStateCess = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]) : null;

					String returnPeriod = (obj[21] != null
							&& !obj[21].toString().trim().isEmpty())
									? String.valueOf(obj[21]) : null;
					String sgstin = (obj[22] != null
							&& !obj[22].toString().trim().isEmpty())
									? String.valueOf(obj[22]) : null;
					String docType = (obj[23] != null
							&& !obj[23].toString().trim().isEmpty())
									? String.valueOf(obj[23]) : null;
					String supplyType = (obj[24] != null
							&& !obj[24].toString().trim().isEmpty())
									? String.valueOf(obj[24]) : null;

					String docNo = (obj[25] != null
							&& !obj[25].toString().trim().isEmpty())
									? String.valueOf(obj[25]) : null;

					if (docNo != null && !docNo.trim().isEmpty()) {
						if (isDecimal(docNo.trim()) || docNo
								.matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")) {
							BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
							docNoDecimalFormat = new BigDecimal(docNo);
							Long supplierPhoneLong = docNoDecimalFormat
									.longValue();
							docNo = String.valueOf(supplierPhoneLong);
						}
					}
					String docDate = (obj[26] != null
							&& !obj[26].toString().trim().isEmpty())
									? String.valueOf(obj[26]) : null;
					LocalDate localDocDate = DateUtil.parseObjToDate(docDate);

					String finYear = GenUtil.getFinYear(localDocDate);
					String origDocType = (obj[27] != null
							&& !obj[27].toString().trim().isEmpty())
									? String.valueOf(obj[27]) : null;
					String origDocNo = (obj[28] != null
							&& !obj[28].toString().trim().isEmpty())
									? String.valueOf(obj[28]) : null;

					String orgDocDate = (obj[29] != null
							&& !obj[29].toString().trim().isEmpty())
									? String.valueOf(obj[29]) : null;
					LocalDate orgDocDateLocal = DateUtil
							.parseObjToDate(orgDocDate);
					String crDrPreGst = (obj[30] != null
							&& !obj[30].toString().trim().isEmpty())
									? String.valueOf(obj[30]) : null;
					String lineNo = (obj[31] != null
							&& !obj[31].toString().trim().isEmpty())
									? String.valueOf(obj[31]) : null;
					String cgstin = (obj[32] != null
							&& !obj[32].toString().trim().isEmpty())
									? String.valueOf(obj[32]) : null;
					String custType = (obj[33] != null
							&& !obj[33].toString().trim().isEmpty())
									? String.valueOf(obj[33]) : null;
					String diffPercFlag = (obj[34] != null
							&& !obj[34].toString().trim().isEmpty())
									? String.valueOf(obj[34]) : null;
					String orgCustGstin = (obj[35] != null
							&& !obj[35].toString().trim().isEmpty())
									? String.valueOf(obj[35]) : null;
					String custName = (obj[36] != null
							&& !obj[36].toString().trim().isEmpty())
									? String.valueOf(obj[36]) : null;
					String custCode = (obj[37] != null
							&& !obj[37].toString().trim().isEmpty())
									? String.valueOf(obj[37]) : null;
					String custAdd1 = (obj[38] != null
							&& !obj[38].toString().trim().isEmpty())
									? String.valueOf(obj[38]) : null;
					String custAdd2 = (obj[39] != null
							&& !obj[39].toString().trim().isEmpty())
									? String.valueOf(obj[39]) : null;
					String custAdd3 = (obj[40] != null
							&& !obj[40].toString().trim().isEmpty())
									? String.valueOf(obj[40]) : null;
					String custAdd4 = (obj[41] != null
							&& !obj[41].toString().trim().isEmpty())
									? String.valueOf(obj[41]) : null;
					String billToState = (obj[42] != null
							&& !obj[42].toString().trim().isEmpty())
									? String.valueOf(obj[42]) : null;
					String shipToState = (obj[43] != null
							&& !obj[43].toString().trim().isEmpty())
									? String.valueOf(obj[43]) : null;
					String pos = (obj[44] != null
							&& !obj[44].toString().trim().isEmpty())
									? String.valueOf(obj[44]) : null;
					String stateApplyCess = (obj[45] != null
							&& !obj[45].toString().trim().isEmpty())
									? String.valueOf(obj[45]) : null;
					String portCode = (obj[46] != null
							&& !obj[46].toString().trim().isEmpty())
									? String.valueOf(obj[46]) : null;
					String shippingBillNo = (obj[47] != null
							&& !obj[47].toString().trim().isEmpty())
									? String.valueOf(obj[47]) : null;
					String shipBillDate = (obj[48] != null
							&& !obj[48].toString().trim().isEmpty())
									? String.valueOf(obj[48]) : null;
					LocalDate localShipBillDate = DateUtil
							.parseObjToDate(shipBillDate);

					String fob = (obj[49] != null
							&& !obj[49].toString().trim().isEmpty())
									? String.valueOf(obj[49]) : null;
					String exportDuty = (obj[50] != null
							&& !obj[50].toString().trim().isEmpty())
									? String.valueOf(obj[50]) : null;
					String hSNorSAC = (obj[51] != null
							&& !obj[51].toString().trim().isEmpty())
									? String.valueOf(obj[51]) : null;
					String productCode = (obj[52] != null
							&& !obj[52].toString().trim().isEmpty())
									? String.valueOf(obj[52]) : null;
					String productDesc = (obj[53] != null
							&& !obj[53].toString().trim().isEmpty())
									? String.valueOf(obj[53]) : null;
					String productCategory = (obj[54] != null
							&& !obj[54].toString().trim().isEmpty())
									? String.valueOf(obj[54]) : null;
					String uom = (obj[55] != null
							&& !obj[55].toString().trim().isEmpty())
									? String.valueOf(obj[55]) : null;
					String qty = (obj[56] != null
							&& !obj[56].toString().trim().isEmpty())
									? String.valueOf(obj[56]) : null;

					String sec7OfIGSTFlag = (obj[57] != null
							&& !obj[57].toString().trim().isEmpty())
									? String.valueOf(obj[57]) : null;

					String taxableVal = (obj[58] != null
							&& !obj[58].toString().trim().isEmpty())
									? String.valueOf(obj[58]) : null;
					String igstRate = (obj[59] != null
							&& !obj[59].toString().trim().isEmpty())
									? String.valueOf(obj[59]) : null;

					String igstAmount = (obj[60] != null
							&& !obj[60].toString().trim().isEmpty())
									? String.valueOf(obj[60]) : null;

					String cgstRate = (obj[61] != null
							&& !obj[61].toString().trim().isEmpty())
									? String.valueOf(obj[61]) : null;
					String cgstAmount = (obj[62] != null
							&& !obj[62].toString().trim().isEmpty())
									? String.valueOf(obj[62]) : null;
					String sgstRate = (obj[63] != null
							&& !obj[63].toString().trim().isEmpty())
									? String.valueOf(obj[63]) : null;
					String sgstAmount = (obj[64] != null
							&& !obj[64].toString().trim().isEmpty())
									? String.valueOf(obj[64]) : null;
					String cessRateAdvalorem = (obj[65] != null
							&& !obj[65].toString().trim().isEmpty())
									? String.valueOf(obj[65]) : null;
					String cessAmtAdvalorem = (obj[66] != null
							&& !obj[66].toString().trim().isEmpty())
									? String.valueOf(obj[66]) : null;
					String cessRateSpecific = (obj[67] != null
							&& !obj[67].toString().trim().isEmpty())
									? String.valueOf(obj[67]) : null;
					String cessAmountSpecific = (obj[68] != null
							&& !obj[68].toString().trim().isEmpty())
									? String.valueOf(obj[68]) : null;
					String cessRateState = (obj[69] != null
							&& !obj[69].toString().trim().isEmpty())
									? String.valueOf(obj[69]) : null;
					String cessAmountState = (obj[70] != null
							&& !obj[70].toString().trim().isEmpty())
									? String.valueOf(obj[70]) : null;
					String otherValue = (obj[71] != null
							&& !obj[71].toString().trim().isEmpty())
									? String.valueOf(obj[71]) : null;

					String invoiceValue = (obj[72] != null
							&& !obj[72].toString().trim().isEmpty())
									? String.valueOf(obj[72]) : null;
					String adjustRefNo = null;
					if (obj[73] != null
							&& !obj[73].toString().trim().isEmpty()) {
						if (obj[73] instanceof BigDecimal) {
							BigDecimal decimal = (BigDecimal) obj[73];
							adjustRefNo = String.valueOf(decimal.longValue());
						} else if (obj[73] instanceof String) {
							adjustRefNo = (String) obj[73];
						} else if (obj[73] instanceof BigInteger) {
							adjustRefNo = (String
									.valueOf(GenUtil.getBigInteger(obj[73])));
						} else if (obj[73] instanceof Number) {
							adjustRefNo = String
									.valueOf(((Number) obj[73]).longValue());
						}
					}
					String adjestementDate = (obj[74] != null
							&& !obj[74].toString().trim().isEmpty())
									? String.valueOf(obj[74]) : null;
					LocalDate localAdjestementDate = DateUtil
							.parseObjToDate(String.valueOf(adjestementDate));
					String adjustedTaxValue = (obj[75] != null
							&& !obj[75].toString().trim().isEmpty())
									? String.valueOf(obj[75]) : null;
					String adjustedIgstAmt = (obj[76] != null
							&& !obj[76].toString().trim().isEmpty())
									? String.valueOf(obj[76]) : null;

					String adjustedCgstAmt = (obj[77] != null
							&& !obj[77].toString().trim().isEmpty())
									? String.valueOf(obj[77]) : null;

					String adjustedSgstAmt = (obj[78] != null
							&& !obj[78].toString().trim().isEmpty())
									? String.valueOf(obj[78]) : null;

					String adjustedAdvaCessAmt = (obj[79] != null
							&& !obj[79].toString().trim().isEmpty())
									? String.valueOf(obj[79]) : null;

					String adjustedSpecificCessAmt = (obj[80] != null
							&& !obj[80].toString().trim().isEmpty())
									? String.valueOf(obj[80]) : null;

					String adjustedStateCessAmt = (obj[81] != null
							&& !obj[81].toString().trim().isEmpty())
									? String.valueOf(obj[81]) : null;

					String reverseChargeFlag = (obj[82] != null
							&& !obj[82].toString().trim().isEmpty())
									? String.valueOf(obj[82]) : null;
					String tcsFlag = (obj[83] != null
							&& !obj[83].toString().trim().isEmpty())
									? String.valueOf(obj[83]) : null;
					String egstin = (obj[84] != null
							&& !obj[84].toString().trim().isEmpty())
									? String.valueOf(obj[84]) : null;
					String tcsAmount = (obj[85] != null
							&& !obj[85].toString().trim().isEmpty())
									? String.valueOf(obj[85]) : null;

					String itcFlag = (obj[86] != null
							&& !obj[86].toString().trim().isEmpty())
									? String.valueOf(obj[86]) : null;
					String claimRefundFlag = (obj[87] != null
							&& !obj[87].toString().trim().isEmpty())
									? String.valueOf(obj[87]) : null;
					String autoPopulateToRef = (obj[88] != null
							&& !obj[88].toString().trim().isEmpty())
									? String.valueOf(obj[88]) : null;
					String reasonCreDebNote = (obj[89] != null
							&& !obj[89].toString().trim().isEmpty())
									? String.valueOf(obj[89]) : null;
					String accVochNum = (obj[90] != null
							&& !obj[90].toString().trim().isEmpty())
									? String.valueOf(obj[90]) : null;
					String accVouchNumDate = (obj[91] != null
							&& !obj[91].toString().trim().isEmpty())
									? String.valueOf(obj[91]) : null;
					LocalDate localaccVouchNumDate = DateUtil
							.parseObjToDate(accVouchNumDate);
					String userDefinedField1 = (obj[92] != null
							&& !obj[92].toString().trim().isEmpty())
									? String.valueOf(obj[92]) : null;
					String userDefinedField2 = (obj[93] != null
							&& !obj[93].toString().trim().isEmpty())
									? String.valueOf(obj[93]) : null;
					String userDefinedField3 = (obj[94] != null
							&& !obj[94].toString().trim().isEmpty())
									? String.valueOf(obj[94]) : null;
					String userDefinedField4 = (obj[95] != null
							&& !obj[95].toString().trim().isEmpty())
									? String.valueOf(obj[95]) : null;
					String userDefinedField5 = (obj[96] != null
							&& !obj[96].toString().trim().isEmpty())
									? String.valueOf(obj[96]) : null;
					String userDefinedField6 = (obj[97] != null
							&& !obj[97].toString().trim().isEmpty())
									? String.valueOf(obj[97]) : null;
					String userDefinedField7 = (obj[98] != null
							&& !obj[98].toString().trim().isEmpty())
									? String.valueOf(obj[98]) : null;
					String userDefinedField8 = (obj[99] != null
							&& !obj[99].toString().trim().isEmpty())
									? String.valueOf(obj[99]) : null;
					String userDefinedField9 = (obj[100] != null
							&& !obj[100].toString().trim().isEmpty())
									? String.valueOf(obj[100]) : null;
					String userDefinedField10 = (obj[101] != null
							&& !obj[101].toString().trim().isEmpty())
									? String.valueOf(obj[101]) : null;
					String userDefinedField11 = (obj[102] != null
							&& !obj[102].toString().trim().isEmpty())
									? String.valueOf(obj[102]) : null;
					String userDefinedField12 = (obj[103] != null
							&& !obj[103].toString().trim().isEmpty())
									? String.valueOf(obj[103]) : null;
					String userDefinedField13 = (obj[104] != null
							&& !obj[104].toString().trim().isEmpty())
									? String.valueOf(obj[104]) : null;
					String userDefinedField14 = (obj[105] != null
							&& !obj[105].toString().trim().isEmpty())
									? String.valueOf(obj[105]) : null;
					String userDefinedField15 = (obj[106] != null
							&& !obj[106].toString().trim().isEmpty())
									? String.valueOf(obj[106]) : null;

					String eWayBillNo = (obj[107] != null
							&& !obj[107].toString().trim().isEmpty())
									? String.valueOf(obj[107]) : null;
					String eWayBillDate = (obj[108] != null
							&& !obj[108].toString().trim().isEmpty())
									? String.valueOf(obj[108]) : null;
					LocalDate localEWayBillDate = DateUtil
							.parseObjToDate(eWayBillDate);

					/**
					 * Start - Outward Document Structural Validation Error
					 * Correction Implementation
					 */
					// File ID and Id are not null only in case of Outward Doc
					// Structural Validation Error Correction
					String idStr = null;
					String fileIdStr = null;
					if (obj.length > 109) {
						idStr = (obj[109] != null
								&& !obj[109].toString().isEmpty())
										? String.valueOf(obj[109]) : null;
						fileIdStr = (obj[110] != null
								&& !obj[110].toString().isEmpty())
										? String.valueOf(obj[110]) : null;
					}
					/**
					 * End - Outward Document Structural Validation Error
					 * Correction Implementation
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
					lineItem.setGlCodeSgst(glCodeSgst);
					lineItem.setGlCodeCgst(glCodeCgst);
					lineItem.setGlCodeIgst(glCodeIgst);
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
					lineItem.setAdjustedCessAmtSpecific(
							adjustedSpecificCessAmt);
					lineItem.setAdjustedCgstAmt(adjustedCgstAmt);
					lineItem.setAdjustedTaxableValue(adjustedTaxValue);
					lineItem.setAdjustedIgstAmt(adjustedIgstAmt);
					lineItem.setAdjustedSgstAmt(adjustedSgstAmt);
					lineItem.setAdjustmentRefNo(adjustRefNo);
					if (localAdjestementDate != null) {
						lineItem.setAdjustmentRefDate(
								String.valueOf(localAdjestementDate));
					} else {
						lineItem.setAdjustmentRefDate(adjestementDate);
					}

					lineItem.setOtherValues(otherValue);
					lineItem.setStateCessRate(cessRateState);
					lineItem.setStateCessAmount(cessAmountState);
					lineItem.setAdjustedStateCessAmt(adjustedStateCessAmt);
					lineItem.setTcsAmount(tcsAmount);
					if(hSNorSAC != null &&
							hSNorSAC.startsWith(GSTConstants.SPE_CHAR)){
						lineItem.setHsnSac(hSNorSAC.substring(1));
					}
					else{
						lineItem.setHsnSac(hSNorSAC);
					}
					lineItem.setLineItemAmt(invoiceValue);
					lineItem.setItcFlag(itcFlag);
					lineItem.setFob(fob);
					lineItem.setExportDuty(exportDuty);
					lineItem.setCrDrReason(reasonCreDebNote);
					lineItem.setOrigDocNo(origDocNo);
					if (orgDocDateLocal != null) {
						lineItem.setOrigDocDate(
								String.valueOf(orgDocDateLocal));
					} else {
						lineItem.setOrigDocDate(orgDocDate);
					}
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

					lineItem.setUserId(userId);
					lineItem.setSourceFileName(sourceFileName);
					lineItem.setDivision(division);
					lineItem.setSalesOrgnization(salesOrg);
					lineItem.setDistributionChannel(distChannel);
					lineItem.setUserAccess1(userAccess1);
					lineItem.setUserAccess2(userAccess2);
					lineItem.setUserAccess3(userAccess3);
					lineItem.setUserAccess4(userAccess4);
					lineItem.setUserAccess5(userAccess5);
					lineItem.setUserAccess6(userAccess6);
					if(returnPeriod != null &&
							returnPeriod.startsWith(GSTConstants.SPE_CHAR)){
						lineItem.setTaxperiod(returnPeriod.substring(1));
					}
					else{
						lineItem.setTaxperiod(returnPeriod);
					}
					
					if (localDocDate != null) {
						lineItem.setDocDate(String.valueOf(localDocDate));
					} else {
						lineItem.setDocDate(docDate);
					}
					lineItem.setOrigDocType(origDocType);
					lineItem.setIsCrDrPreGst(crDrPreGst);
					lineItem.setCgstin(cgstin);
					lineItem.setCustOrSuppType(custType);
					lineItem.setDiffPercent(diffPercFlag);
					lineItem.setOrigCgstin(cgstin);
					lineItem.setCustOrSuppAddress1(custAdd1);
					lineItem.setCustOrSuppAddress2(custAdd2);
					//lineItem.setCustOrSuppAddress3(custAdd3);
					lineItem.setCustOrSuppAddress4(custAdd4);
					if(billToState != null &&
							billToState.startsWith(GSTConstants.SPE_CHAR)){
						lineItem.setBillToState(billToState.substring(1));
					}
					else{
					lineItem.setBillToState(billToState);
					}
					if(shipToState != null && 
							shipToState.startsWith(GSTConstants.SPE_CHAR)){
						lineItem.setShipToState(shipToState.substring(1));
					}
					else{
						lineItem.setShipToState(shipToState);
					}
					
					if(pos != null && 
							pos.startsWith(GSTConstants.SPE_CHAR)){
						lineItem.setPos(pos.substring(1));
					}
					else{
						lineItem.setPos(pos);
					}
					
					lineItem.setStateApplyingCess(stateApplyCess);
					lineItem.setPortCode(portCode);
					lineItem.setShippingBillNo(shippingBillNo);
					if (localShipBillDate != null) {
						lineItem.setShippingBillDate(
								String.valueOf(localShipBillDate));
					} else {
						lineItem.setShippingBillDate(shipBillDate);
					}
					lineItem.setSection7OfIgstFlag(sec7OfIGSTFlag);
					lineItem.setReverseCharge(reverseChargeFlag);
					lineItem.setTcsFlag(tcsFlag);
					lineItem.setEgstin(egstin);
					lineItem.setClaimRefundFlag(claimRefundFlag);
					lineItem.setAutoPopToRefundFlag(autoPopulateToRef);
					Integer derivedRetPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					if (derivedRetPeriod != null) {
						lineItem.setDerivedTaxperiod(derivedRetPeriod);
					} else {
						lineItem.setDerivedTaxperiod(999999);
					}
					lineItem.setAccountingVoucherNumber(accVochNum);
					if (localaccVouchNumDate != null) {
						lineItem.setAccountingVoucherDate(
								String.valueOf(localaccVouchNumDate));
					} else {
						lineItem.setAccountingVoucherDate(accVouchNumDate);
					}
					lineItem.setEWayBillNo(eWayBillNo);
					if (localEWayBillDate != null) {
						lineItem.setEWayBillDate(
								String.valueOf(localEWayBillDate));
					} else {
						lineItem.setEWayBillDate(eWayBillDate);
					}
					

					if (fileStatus != null) {
						errorDocument.setAcceptanceId(fileStatus.getId());
						lineItem.setAcceptanceId(fileStatus.getId());
					} else {
						errorDocument.setAcceptanceId(0L);
						lineItem.setAcceptanceId(0L);
					}
					lineItems.add(lineItem); // Add Line Items
					errorDocument.setDiffPercent(diffPercFlag);
					errorDocument.setClaimRefundFlag(claimRefundFlag);
					errorDocument.setReverseCharge(reverseChargeFlag);
					errorDocument.setUserId(userId);

					errorDocument.setStateApplyingCess(stateApplyCess);

					errorDocument.setSourceFileName(sourceFileName);
					errorDocument.setSalesOrgnization(salesOrg);
					errorDocument.setLocation(location);
					errorDocument.setDistributionChannel(distChannel);
					errorDocument.setOrigCgstin(orgCustGstin);
					errorDocument.setSection7OfIgstFlag(sec7OfIGSTFlag);
					errorDocument.setDivision(division);
					errorDocument.setProfitCentre(profitCentre);
					errorDocument.setPlantCode(plantCode);
					if(returnPeriod != null &&
							returnPeriod.startsWith(GSTConstants.SPE_CHAR)){
						errorDocument.setTaxperiod(returnPeriod.substring(1));
					}
					else{
						errorDocument.setTaxperiod(returnPeriod);
					}
					
					
					if (derivedRetPeriod != null) {
						errorDocument.setDerivedTaxperiod(derivedRetPeriod);
					} else {
						errorDocument.setDerivedTaxperiod(999999);
					}
					errorDocument.setSgstin(sgstin);
					errorDocument.setEgstin(egstin);

					errorDocument.setIsCrDrPreGst(crDrPreGst);
					errorDocument.setUserAccess1(userAccess1);
					errorDocument.setUserAccess2(userAccess2);
					errorDocument.setUserAccess3(userAccess3);
					errorDocument.setUserAccess4(userAccess4);
					errorDocument.setUserAccess5(userAccess5);
					errorDocument.setUserAccess6(userAccess6);

					errorDocument.setAccountingVoucherNumber(accVochNum);
					if (localaccVouchNumDate != null) {
						errorDocument.setAccountingVoucherDate(
								String.valueOf(localaccVouchNumDate));
					} else {
						errorDocument.setAccountingVoucherDate(accVouchNumDate);
					}
					errorDocument.setDocType(docType);
					if(docNo != null && 
							docNo.startsWith(GSTConstants.SPE_CHAR)){
						errorDocument.setDocNo(docNo.substring(1));
					}
					else{
						errorDocument.setDocNo(docNo);
					}
					
					if (localDocDate != null) {
						errorDocument.setDocDate(String.valueOf(localDocDate));
					} else {
						errorDocument.setDocDate(docDate);
					}
					errorDocument.setOrigDocType(origDocType);

					errorDocument.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
									.getDataOriginTypeCode());
					errorDocument.setCreatedBy(userName);
					errorDocument.setCgstin(cgstin);
					errorDocument.setCustOrSuppName(custName);
					errorDocument.setCustOrSuppType(custType);
					errorDocument.setCustOrSuppCode(custCode);
					errorDocument.setCustOrSuppAddress1(custAdd1);
					errorDocument.setCustOrSuppAddress2(custAdd2);
					errorDocument.setCustOrSuppAddress3(custAdd3);
					errorDocument.setCustOrSuppAddress4(custAdd4);
					if(billToState != null && 
							billToState.startsWith(GSTConstants.SPE_CHAR)){
						errorDocument.setBillToState(billToState.substring(1));
					}
					else{
						errorDocument.setBillToState(billToState);
					}
					if(shipToState != null && 
							shipToState.startsWith(GSTConstants.SPE_CHAR)){
						errorDocument.setShipToState(shipToState.substring(1));
					}
					else{
						errorDocument.setShipToState(shipToState);
					}
					errorDocument.setAutoPopToRefundFlag(autoPopulateToRef);

					if(pos != null && 
							pos.startsWith(GSTConstants.SPE_CHAR)){
						errorDocument.setPos(pos.substring(1));
					}
					else{
						errorDocument.setPos(pos);
					}
					
					errorDocument.setPortCode(portCode);
					errorDocument.setShippingBillNo(shippingBillNo);
					if (localShipBillDate != null) {
						errorDocument.setShippingBillDate(
								String.valueOf(localShipBillDate));
					} else {
						errorDocument.setShippingBillDate(shipBillDate);
					}
					errorDocument.setEWayBillNo(eWayBillNo);
					if (localEWayBillDate != null) {
						errorDocument.setEWayBillDate(
								String.valueOf(localEWayBillDate));
					} else {
						errorDocument.setEWayBillDate(eWayBillDate);
					}

					errorDocument.setTcsFlag(tcsFlag);
					errorDocument.setEgstin(egstin);

					errorDocument.setIsError("true");
					errorDocument.setIsProcessed("false");
					errorDocument.setIsDeleted("false");
					errorDocument.setFinYear(finYear);

					if (idStr != null) {
						try {
							Long id = Long.parseLong(idStr);
							errorDocument.setId(id);
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
							errorDocument.setAcceptanceId(fileId);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occuured while converting "
											+ "String file id to Long  file id "
											+ e.getMessage());
						}
					}

					int maxlength = 99;
					if (key.length() > maxlength) {
						errorDocument.setDocKey(key.substring(0, maxlength));
					} else {
						errorDocument.setDocKey(key);
					}
				}
				errorDocument.setLineItems(lineItems);
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				errorDocument.setCreatedDate(convertNow);
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
}
