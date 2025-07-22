package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr3b.dto.Gstr3BInwardReportDownloadDto;
import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr3bInwardReportDownloadConvertor")
public class Gstr3bInwardReportDownloadConvertor implements ReportConvertor {

	/*@Autowired
	@Qualifier("MasterErrorCatalogEntityRepository")
	MasterErrorCatalogEntityRepository masterErrorCatalogRepo;
	*/
	@Autowired
	@Qualifier("GSTR3BInwardReportCacheImpl")
	GSTR3BInwardReportCache cache;
	

	@Override
	public Object convert(Object[] arr, String reportType) {

		Gstr3BInwardReportDownloadDto obj = new Gstr3BInwardReportDownloadDto();
		String docType = arr[22] != null ? arr[22].toString() : null;
		obj.setSourceID(arr[0] != null ? arr[0].toString() : null);
		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setCategoryOfItem(arr[3] != null ? arr[3].toString() : null);
		obj.setInwardTableNumber(arr[4] != null ? arr[4].toString() : null);
		obj.setSourceFileName(arr[5] != null ? arr[5].toString() : null);
		obj.setProfitCentre(arr[6] != null ? arr[6].toString() : null);
		obj.setPlant(arr[7] != null ? arr[7].toString() : null);
		obj.setDivision(arr[8] != null ? arr[8].toString() : null);
		obj.setLocation(arr[9] != null ? arr[9].toString() : null);
		obj.setPurchaseOrganisation(
				arr[10] != null ? arr[10].toString() : null);
		obj.setBillOfEntry(arr[11] != null ? arr[11].toString() : null);
		obj.setBillOfEntryDate(arr[12] != null ? arr[12].toString() : null);
		obj.setITCEntitlement(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess1(arr[14] != null ? arr[14].toString() : null);
		obj.setUserAccess2(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess3(arr[16] != null ? arr[16].toString() : null);
		obj.setUserAccess4(arr[17] != null ? arr[17].toString() : null);
		obj.setUserAccess5(arr[18] != null ? arr[18].toString() : null);
		obj.setUserAccess6(arr[19] != null ? arr[19].toString() : null);
		obj.setReturnPeriod(
				(arr[20] != null) ? "`".concat(arr[20].toString()) : null);
		obj.setSupplierGSTIN(arr[21] != null ? arr[21].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setInvoiceValue(CheckForNegativeValue(arr[43]));
		} else {
			obj.setInvoiceValue(arr[43] != null ? arr[43].toString() : null);
		}
		obj.setAutoPopulateToRefund(
				arr[48] != null ? arr[48].toString() : null);
		obj.setGlTaxableValue(arr[52] != null ? arr[52].toString() : null);
		obj.setGLIGST(arr[53] != null ? arr[53].toString() : null);
		obj.setGLCGST(arr[54] != null ? arr[54].toString() : null);
		obj.setGLSGST(arr[55] != null ? arr[55].toString() : null);
		obj.setGLAdvaloremCess(arr[56] != null ? arr[56].toString() : null);
		obj.setGLSpecificCess(arr[57] != null ? arr[57].toString() : null);
		obj.setGLStateCessAdvalorem(
				arr[58] != null ? arr[58].toString() : null);
		obj.setStateApplyingCess(arr[40] != null ? arr[40].toString() : null);
		obj.setDocumentType(arr[22] != null ? arr[22].toString() : null);
		obj.setSupplyType(arr[23] != null ? arr[23].toString() : null);
		obj.setDocumentNumber(arr[24] != null ? arr[24].toString() : null);
		obj.setDocumentDate(arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalDocumentDate(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[27] != null ? arr[27].toString() : null);
		obj.setCrdrPreGST(arr[28] != null ? arr[28].toString() : null);
		obj.setRecipientGSTIN(arr[29] != null ? arr[29].toString() : null);
		obj.setSupplierType(arr[30] != null ? arr[30].toString() : null);
		obj.setDifferentialFlag(arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[32] != null ? arr[32].toString() : null);
		obj.setSupplierName(arr[33] != null ? arr[33].toString() : null);
		obj.setSupplierCode(arr[34] != null ? arr[34].toString() : null);
		obj.setSupplierAddress1(arr[35] != null ? arr[35].toString() : null);
		obj.setSupplierAddress2(arr[36] != null ? arr[36].toString() : null);
		obj.setSupplierAddress3(arr[37] != null ? arr[37].toString() : null);
		obj.setSupplierAddress4(arr[38] != null ? arr[38].toString() : null);
		obj.setPos(arr[39] != null ? arr[39].toString() : null);
		obj.setPortCode(arr[41] != null ? arr[41].toString() : null);
		obj.setSection7ofIGSTFlag(arr[42] != null ? arr[42].toString() : null);
		obj.setReverseChargeFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setPostingDate(arr[45] != null ? arr[45].toString() : null);
		obj.setITCReversalIdentifier(
				arr[46] != null ? arr[46].toString() : null);
		obj.setClaimRefundFlag(arr[47] != null ? arr[47].toString() : null);
		obj.setEWayBillNumber(arr[49] != null ? arr[49].toString() : null);
		obj.setEWayBillDate(arr[50] != null ? arr[50].toString() : null);
		obj.setItemCode(arr[59] != null ? arr[59].toString() : null);
		obj.setHsnorSac(arr[60] != null ? arr[60].toString() : null);
		obj.setProductCode(arr[61] != null ? arr[61].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[62] != null ? arr[62].toString() : null);
		obj.setPurchaseVoucherDate(arr[63] != null ? arr[63].toString() : null);
		obj.setPurchaseVoucherNumber(
				arr[64] != null ? arr[64].toString() : null);
		obj.setPaymentDate(arr[65] != null ? arr[65].toString() : null);
		obj.setItemDescription(arr[66] != null ? arr[66].toString() : null);
		obj.setCategoryOfItem(arr[67] != null ? arr[67].toString() : null);
		obj.setUnitOfMeasurement(arr[68] != null ? arr[68].toString() : null);
		obj.setQuantity(arr[69] != null ? arr[69].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[70] != null ? arr[70].toString() : null);
		obj.setUserDefinedField1(arr[71] != null ? arr[71].toString() : null);
		obj.setUserDefinedField2(arr[72] != null ? arr[72].toString() : null);
		obj.setUserDefinedField3(arr[73] != null ? arr[73].toString() : null);
		obj.setUserDefinedField4(arr[74] != null ? arr[74].toString() : null);
		obj.setUserDefinedField5(arr[75] != null ? arr[75].toString() : null);
		obj.setUserDefinedField6(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefinedField7(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefinedField8(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefinedField9(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefinedField10(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefinedField11(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefinedField12(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefinedField13(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefinedField14(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefinedField15(arr[85] != null ? arr[85].toString() : null);
		obj.setOtherValue(arr[86] != null ? arr[86].toString() : null);
		obj.setAdjustmentReferenceNo(
				arr[87] != null ? arr[87].toString() : null);
		obj.setAdjustmentReferenceDate(
				arr[88] != null ? arr[88].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setTaxableValue(CheckForNegativeValue(arr[89]));
			obj.setIntegratedTaxAmount(CheckForNegativeValue(arr[91]));
			obj.setCentralTaxAmount(CheckForNegativeValue(arr[97]));
			obj.setStateUTTaxAmount(CheckForNegativeValue(arr[99]));
			obj.setSpecificCessAmount(CheckForNegativeValue(arr[101]));
			obj.setAdvaloremCessAmount(CheckForNegativeValue(arr[103]));
			obj.setStateCessAmount(CheckForNegativeValue(arr[105]));
			obj.setTaxableValueAdjusted(CheckForNegativeValue(arr[106]));
			obj.setAvailableIGST(CheckForNegativeValue(arr[107]));
			obj.setAvailableCGST(CheckForNegativeValue(arr[108]));
			obj.setAvailableSGST(CheckForNegativeValue(arr[109]));
			obj.setAvailableCess(CheckForNegativeValue(arr[110]));
			obj.setCustomDuty(CheckForNegativeValue(arr[112]));
			obj.setIntegratedTaxAmountAdjusted(CheckForNegativeValue(arr[113]));
			obj.setCentralTaxAmountAdjusted(CheckForNegativeValue(arr[114]));
			obj.setStateUTTaxAmountAdjusted(CheckForNegativeValue(arr[115]));
			obj.setAdvaloremCessAmountAdjusted(CheckForNegativeValue(arr[116]));
			obj.setSpecificCessAmountAdjusted(CheckForNegativeValue(arr[117]));
			obj.setStateCessAmountAdjusted(CheckForNegativeValue(arr[118]));
		} else {
			obj.setTaxableValue(arr[89] != null ? arr[89].toString() : null);
			obj.setIntegratedTaxAmount(
					arr[91] != null ? arr[91].toString() : null);
			obj.setCentralTaxAmount(
					arr[97] != null ? arr[97].toString() : null);
			obj.setStateUTTaxAmount(
					arr[99] != null ? arr[99].toString() : null);
			obj.setSpecificCessAmount(
					arr[101] != null ? arr[101].toString() : null);
			obj.setAdvaloremCessAmount(
					arr[103] != null ? arr[103].toString() : null);
			obj.setStateCessAmount(
					arr[105] != null ? arr[105].toString() : null);
			obj.setTaxableValueAdjusted(
					arr[106] != null ? arr[106].toString() : null);
			obj.setAvailableIGST(arr[107] != null ? arr[107].toString() : null);
			obj.setAvailableCGST(arr[108] != null ? arr[108].toString() : null);
			obj.setAvailableSGST(arr[109] != null ? arr[109].toString() : null);
			obj.setAvailableCess(arr[110] != null ? arr[110].toString() : null);
			obj.setCustomDuty(arr[112] != null ? arr[112].toString() : null);
			obj.setIntegratedTaxAmountAdjusted(
					arr[113] != null ? arr[113].toString() : null);
			obj.setCentralTaxAmountAdjusted(
					arr[114] != null ? arr[114].toString() : null);
			obj.setStateUTTaxAmountAdjusted(
					arr[115] != null ? arr[115].toString() : null);
			obj.setAdvaloremCessAmountAdjusted(
					arr[116] != null ? arr[116].toString() : null);
			obj.setSpecificCessAmountAdjusted(
					arr[117] != null ? arr[117].toString() : null);
			obj.setStateCessAmountAdjusted(
					arr[118] != null ? arr[118].toString() : null);
		}
		obj.setIntegratedTaxRate(arr[90] != null ? arr[90].toString() : null);
		obj.setCentralTaxRate(arr[92] != null ? arr[92].toString() : null);
		obj.setPaymentVoucherNumber(
				arr[93] != null ? arr[93].toString() : null);
		obj.setContractNumber(arr[94] != null ? arr[94].toString() : null);
		obj.setContractDate(arr[95] != null ? arr[95].toString() : null);
		obj.setContractValue(arr[96] != null ? arr[96].toString() : null);
		obj.setStateUTTaxRate(arr[98] != null ? arr[98].toString() : null);
		obj.setSpecificCessRate(arr[100] != null ? arr[100].toString() : null);
		obj.setAdvaloremCessRate(arr[102] != null ? arr[102].toString() : null);
		obj.setStateCessRate(arr[104] != null ? arr[104].toString() : null);
		obj.setAspInformationID(arr[119] != null ? arr[119].toString() : null);

		if (arr[119] != null) {
			String aspInfoId = arr[119].toString();
			if (!aspInfoId.isEmpty()) {
				String descs = "";
				String[] aspInfoIdArr = aspInfoId.split(",");

				LOGGER.debug(String.format("aspInfoId %s", aspInfoId));
				for (String id : aspInfoIdArr) {
					LOGGER.error(String.format("id %s", id));

					//String desc = masterErrorCatalogRepo.findByErrorCode(id);
					String desc = cache.findAspInfoDescription(id);

					LOGGER.debug(String.format("desc %s", desc));
					if (!descs.isEmpty()) {
						descs.concat(",").concat(desc);
						LOGGER.debug(String.format("descs %s", descs));
					} else if (desc != null) {
						descs.concat(desc);
						LOGGER.debug(String.format("else if block -descs %s", descs));
					}
				}
				
				

				obj.setAspInformationDescription(descs);

			}
		}
		/*obj.setAspInformationDescription(
				arr[120] != null ? arr[120].toString() : null);*/
		obj.setOriginalDocumentNumber(
				arr[121] != null ? arr[121].toString() : null);
		obj.setOriginalDocumentDate(
				arr[122] != null ? arr[122].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setInvoiceValueASP(CheckForNegativeValue(arr[123]));
		} else {
			obj.setInvoiceValueASP(
					arr[123] != null ? arr[123].toString() : null);
		}
		obj.setEligibilityIndicator(
				arr[124] != null ? arr[124].toString() : null);
		obj.setGstr3BTableNumber(arr[125] != null ? arr[125].toString() : null);
		obj.setCreditTaxPeriod3B(
				(arr[126] != null) ? "`".concat(arr[126].toString()) : null);
		obj.setIrn(arr[127] != null ? arr[127].toString() : null);
		obj.setIrnDate(arr[128] != null ? arr[128].toString() : null);
		obj.setTaxScheme(arr[129] != null ? arr[129].toString() : null);
		obj.setDocCategory(arr[130] != null ? arr[130].toString() : null);
		obj.setSupplierTradeName(arr[131] != null ? arr[131].toString() : null);
		obj.setSupplierLegalName(arr[132] != null ? arr[132].toString() : null);
		obj.setSupplierLocation(arr[133] != null ? arr[133].toString() : null);
		obj.setSupplierPincode(arr[134] != null ? arr[134].toString() : null);
		obj.setSupplierStateCode(
				arr[135] != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[135].toString())
						: null);
		obj.setSupplierPhone(arr[136] != null ? arr[136].toString() : null);
		obj.setSupplierEmail(arr[137] != null ? arr[137].toString() : null);
		obj.setCustomerGSTIN(arr[138] != null ? arr[138].toString() : null);
		obj.setCustomerTradeName(arr[139] != null ? arr[139].toString() : null);
		obj.setCustomerLegalName(arr[140] != null ? arr[140].toString() : null);
		obj.setCustomerAddress1(arr[141] != null ? arr[141].toString() : null);
		obj.setCustomerAddress2(arr[142] != null ? arr[142].toString() : null);
		obj.setCustomerLocation(arr[143] != null ? arr[143].toString() : null);
		obj.setCustomerPincode(arr[144] != null ? arr[144].toString() : null);
		obj.setCustomerStateCode(
				arr[145] != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[145].toString())
						: null);
		obj.setBillingPOS(
				arr[146] != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[146].toString())
						: null);
		obj.setCustomerPhone(arr[147] != null ? arr[147].toString() : null);
		obj.setCustomerEmail(arr[148] != null ? arr[148].toString() : null);
		obj.setDispatcherGSTIN(arr[149] != null ? arr[149].toString() : null);
		obj.setDispatcherTradeName(
				arr[150] != null ? arr[150].toString() : null);
		obj.setDispatcherAddress1(
				arr[151] != null ? arr[151].toString() : null);
		obj.setDispatcherAddress2(
				arr[152] != null ? arr[152].toString() : null);
		obj.setDispatcherLocation(
				arr[153] != null ? arr[153].toString() : null);
		obj.setDispatcherPincode(arr[154] != null ? arr[154].toString() : null);
		obj.setDispatcherStateCode(
				arr[155] != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[155].toString())
						: null);
		obj.setShipToGSTIN(arr[156] != null ? arr[156].toString() : null);
		obj.setShipToTradeName(arr[157] != null ? arr[157].toString() : null);
		obj.setShipToLegalName(arr[158] != null ? arr[158].toString() : null);
		obj.setShipToAddress1(arr[159] != null ? arr[159].toString() : null);
		obj.setShipToAddress2(arr[160] != null ? arr[160].toString() : null);
		obj.setShipToLocation(arr[161] != null ? arr[161].toString() : null);
		obj.setShipToPincode(arr[162] != null ? arr[162].toString() : null);
		obj.setShipToStateCode(
				arr[163] != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[163].toString())
						: null);
		obj.setItemSerialNumber(arr[164] != null ? arr[164].toString() : null);
		obj.setProductSerialNumber(
				arr[165] != null ? arr[165].toString() : null);
		obj.setProductName(arr[166] != null ? arr[166].toString() : null);
		obj.setProductDescription(
				arr[167] != null ? arr[167].toString() : null);
		obj.setIsService(arr[168] != null ? arr[168].toString() : null);
		obj.setHsn(arr[169] != null ? arr[169].toString() : null);
		obj.setBarcode(arr[170] != null ? arr[170].toString() : null);
		obj.setBatchName(arr[171] != null ? arr[171].toString() : null);
		obj.setBatchExpiryDate(arr[172] != null ? arr[172].toString() : null);
		obj.setWarrantyDate(arr[173] != null ? arr[173].toString() : null);
		obj.setOrderLineReference(
				arr[174] != null ? arr[174].toString() : null);
		obj.setAttributeName(arr[175] != null ? arr[175].toString() : null);
		obj.setAttributeValue(arr[176] != null ? arr[176].toString() : null);
		obj.setOriginCountry(arr[177] != null ? arr[177].toString() : null);
		obj.setUqc(arr[178] != null ? arr[178].toString() : null);
		obj.setFreeQuantity(arr[179] != null ? arr[179].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setItemAmount(CheckForNegativeValue(arr[181]));
			obj.setItemDiscount(CheckForNegativeValue(arr[182]));
			obj.setPreTaxAmount(CheckForNegativeValue(arr[183]));
			obj.setItemAssessableAmount(CheckForNegativeValue(arr[184]));
			obj.setIGSTAmount(CheckForNegativeValue(arr[186]));
			obj.setCGSTAmount(CheckForNegativeValue(arr[188]));
			obj.setSGSTAmount(CheckForNegativeValue(arr[190]));
			obj.setStateCessAdvaloremAmount(CheckForNegativeValue(arr[192]));
			obj.setStateCessSpecificAmount(CheckForNegativeValue(arr[194]));
			obj.setItemOtherCharges(CheckForNegativeValue(arr[195]));
			obj.setInvoiceOtherCharges(CheckForNegativeValue(arr[197]));
			obj.setTotalItemAmount(CheckForNegativeValue(arr[196]));
			obj.setInvoiceAssessableAmount(CheckForNegativeValue(arr[198]));
			obj.setInvoiceIGSTAmount(CheckForNegativeValue(arr[199]));
			obj.setInvoiceCGSTAmount(CheckForNegativeValue(arr[200]));
			obj.setInvoiceSGSTAmount(CheckForNegativeValue(arr[201]));
			obj.setInvoiceCessAdvaloremAmount(CheckForNegativeValue(arr[202]));
			obj.setInvoiceCessSpecificAmount(CheckForNegativeValue(arr[203]));
			obj.setInvoiceStateCessAdvaloremAmount(
					CheckForNegativeValue(arr[204]));
			obj.setInvoiceStateCessSpecificAmount(
					CheckForNegativeValue(arr[205]));
			obj.setTcsAmountIncomeTax(CheckForNegativeValue(arr[210]));
		} else {
			obj.setItemAmount(arr[181] != null ? arr[181].toString() : null);
			obj.setItemDiscount(arr[182] != null ? arr[182].toString() : null);
			obj.setPreTaxAmount(arr[183] != null ? arr[183].toString() : null);
			obj.setItemAssessableAmount(
					arr[184] != null ? arr[184].toString() : null);
			obj.setIGSTAmount(arr[186] != null ? arr[186].toString() : null);
			obj.setCGSTAmount(arr[188] != null ? arr[188].toString() : null);
			obj.setSGSTAmount(arr[190] != null ? arr[190].toString() : null);
			obj.setStateCessAdvaloremAmount(
					arr[192] != null ? arr[192].toString() : null);
			obj.setStateCessSpecificAmount(
					arr[194] != null ? arr[194].toString() : null);
			obj.setTotalItemAmount(
					arr[196] != null ? arr[196].toString() : null);
			obj.setItemOtherCharges(
					arr[195] != null ? arr[195].toString() : null);
			obj.setInvoiceOtherCharges(
					arr[197] != null ? arr[197].toString() : null);
			obj.setInvoiceAssessableAmount(
					arr[198] != null ? arr[198].toString() : null);
			obj.setInvoiceIGSTAmount(
					arr[199] != null ? arr[199].toString() : null);
			obj.setInvoiceCGSTAmount(
					arr[200] != null ? arr[200].toString() : null);
			obj.setInvoiceSGSTAmount(
					arr[201] != null ? arr[201].toString() : null);
			obj.setInvoiceCessAdvaloremAmount(
					arr[202] != null ? arr[202].toString() : null);
			obj.setInvoiceCessSpecificAmount(
					arr[203] != null ? arr[203].toString() : null);
			obj.setInvoiceStateCessAdvaloremAmount(
					arr[204] != null ? arr[204].toString() : null);
			obj.setInvoiceStateCessSpecificAmount(
					arr[205] != null ? arr[205].toString() : null);
			obj.setTcsAmountIncomeTax(
					arr[210] != null ? arr[210].toString() : null);
		}
		obj.setUnitPrice(arr[180] != null ? arr[180].toString() : null);
		obj.setIGSTRate(arr[185] != null ? arr[185].toString() : null);
		obj.setCGSTRate(arr[187] != null ? arr[187].toString() : null);
		obj.setSGSTRate(arr[189] != null ? arr[189].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[191] != null ? arr[191].toString() : null);
		obj.setStateCessSpecificRate(
				arr[193] != null ? arr[193].toString() : null);
		obj.setRoundOff(arr[206] != null ? arr[206].toString() : null);
		obj.setTotalInvoiceValue(arr[207] != null ? arr[207].toString() : null);
		obj.setTcsFlagIncomeTax(arr[208] != null ? arr[208].toString() : null);
		obj.setTcsRateIncomeTax(arr[209] != null ? arr[209].toString() : null);
		obj.setCurrencyCode(arr[211] != null ? arr[211].toString() : null);
		obj.setCountryCode(arr[212] != null ? arr[212].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setInvoiceValueFC(CheckForNegativeValue(arr[213]));
		} else {
			obj.setInvoiceValueFC(
					arr[213] != null ? arr[213].toString() : null);
		}
		obj.setInvoiceRemarks(arr[214] != null ? arr[214].toString() : null);
		obj.setInvoicePeriodStartDate(
				(arr[215] != null) ? "`".concat(arr[215].toString()) : null);
		obj.setInvoicePeriodEndDate(
				(arr[216] != null) ? "`".concat(arr[216].toString()) : null);
		obj.setPreceedingInvoiceNumber(
				arr[217] != null ? arr[217].toString() : null);
		obj.setPreceedingInvoiceDate(
				arr[218] != null ? arr[218].toString() : null);
		obj.setOtherReference(arr[219] != null ? arr[219].toString() : null);
		obj.setReceiptAdviceReference(
				arr[220] != null ? arr[220].toString() : null);
		obj.setReceiptAdviceDate(arr[221] != null ? arr[221].toString() : null);
		obj.setTenderReference(arr[222] != null ? arr[222].toString() : null);
		obj.setContractReference(arr[223] != null ? arr[223].toString() : null);
		obj.setExternalReference(arr[224] != null ? arr[224].toString() : null);
		obj.setProjectReference(arr[225] != null ? arr[225].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[226] != null ? arr[226].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[227] != null ? arr[227].toString() : null);
		obj.setPayeeName(arr[228] != null ? arr[228].toString() : null);
		obj.setModeOfPayment(arr[229] != null ? arr[229].toString() : null);
		obj.setBranchOrIFSCCode(arr[230] != null ? arr[230].toString() : null);
		obj.setPaymentTerms(arr[231] != null ? arr[231].toString() : null);
		obj.setPaymentInstruction(
				arr[232] != null ? arr[232].toString() : null);
		obj.setPaidAmount(arr[236] != null ? arr[236].toString() : null);
		obj.setBalanceAmount(arr[237] != null ? arr[237].toString() : null);
		obj.setEcomGSTIN(arr[240] != null ? arr[240].toString() : null);
		obj.setCreditTransfer(arr[233] != null ? arr[233].toString() : null);
		obj.setDirectDebit(arr[234] != null ? arr[234].toString() : null);
		obj.setCreditDays(arr[235] != null ? arr[235].toString() : null);
		obj.setPaymentDueDate(arr[238] != null ? arr[238].toString() : null);
		obj.setAccountDetail(arr[239] != null ? arr[239].toString() : null);
		obj.setSupportingDocURL(arr[241] != null ? arr[241].toString() : null);
		obj.setSupportingDocument(
				arr[242] != null ? arr[242].toString() : null);
		obj.setAdditionalInformation(
				arr[243] != null ? arr[243].toString() : null);
		obj.setTransactionType(arr[244] != null ? arr[244].toString() : null);
		obj.setSubSupplyType(arr[245] != null ? arr[245].toString() : null);
		obj.setOtherSupplyTypeDes(
				arr[246] != null ? arr[246].toString() : null);
		obj.setTransporterID(arr[247] != null ? arr[247].toString() : null);
		obj.setTransporterName(arr[248] != null ? arr[248].toString() : null);
		obj.setTransportMode(arr[249] != null ? arr[249].toString() : null);
		obj.setTransportDocNo(arr[250] != null ? arr[250].toString() : null);
		obj.setTransportDocDate(arr[251] != null ? arr[251].toString() : null);
		obj.setDistance(arr[252] != null ? arr[252].toString() : null);
		obj.setVehicleNo(arr[253] != null ? arr[253].toString() : null);
		obj.setVehicleType(arr[254] != null ? arr[254].toString() : null);
		obj.setOriginalDocumentType(
				arr[255] != null ? arr[255].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[256] != null ? arr[256].toString() : null);
		obj.setProductCode(arr[257] != null ? arr[257].toString() : null);
		obj.setCategoryOfProduct(arr[258] != null ? arr[258].toString() : null);
		obj.setExchangeRate(arr[260] != null ? arr[260].toString() : null);
		obj.setTcsFlagGST(arr[261] != null ? arr[261].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setCif(CheckForNegativeValue(arr[259]));
			obj.setTcsIGSTAmount(CheckForNegativeValue(arr[262]));
			obj.setTcsCGSTAmount(CheckForNegativeValue(arr[263]));
			obj.setTcsSGSTAmount(CheckForNegativeValue(arr[264]));
			obj.setTdsIGSTAmount(CheckForNegativeValue(arr[266]));
			obj.setTdsCGSTAmount(CheckForNegativeValue(arr[267]));
			obj.setTdsSGSTAmount(CheckForNegativeValue(arr[268]));
		} else {
			obj.setCif(arr[259] != null ? arr[259].toString() : null);
			obj.setTcsIGSTAmount(arr[262] != null ? arr[262].toString() : null);
			obj.setTcsCGSTAmount(arr[263] != null ? arr[263].toString() : null);
			obj.setTcsSGSTAmount(arr[264] != null ? arr[264].toString() : null);
			obj.setTdsIGSTAmount(arr[266] != null ? arr[266].toString() : null);
			obj.setTdsCGSTAmount(arr[267] != null ? arr[267].toString() : null);
			obj.setTdsSGSTAmount(arr[268] != null ? arr[268].toString() : null);
		}
		obj.setTdsFlagGST(arr[265] != null ? arr[265].toString() : null);
		obj.setCompanyCode(arr[269] != null ? arr[269].toString() : null);
		obj.setSourceIdentifier(arr[270] != null ? arr[270].toString() : null);
		obj.setPlantCode(arr[271] != null ? arr[271].toString() : null);
		obj.setSubDivision(arr[272] != null ? arr[272].toString() : null);
		obj.setProfitCentre1(arr[273] != null ? arr[273].toString() : null);
		obj.setProfitCentre2(arr[274] != null ? arr[274].toString() : null);
		obj.setProfitCentre3(arr[275] != null ? arr[275].toString() : null);
		obj.setProfitCentre4(arr[276] != null ? arr[276].toString() : null);
		obj.setProfitCentre5(arr[277] != null ? arr[277].toString() : null);
		obj.setProfitCentre6(arr[278] != null ? arr[278].toString() : null);
		obj.setProfitCentre7(arr[279] != null ? arr[279].toString() : null);
		obj.setProfitCentre8(arr[280] != null ? arr[280].toString() : null);
		obj.setGLAssessableValue(arr[281] != null ? arr[281].toString() : null);
		obj.setGLStateCessSpecific(
				arr[282] != null ? arr[282].toString() : null);
		obj.setGLPostingDate(arr[283] != null ? arr[283].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ADJ"))) {
			obj.setPurchaseOrderValue(CheckForNegativeValue(arr[284]));
		} else {
			obj.setPurchaseOrderValue(
					arr[284] != null ? arr[284].toString() : null);
		}
		obj.setEwbNumber(arr[285] != null ? arr[285].toString() : null);
		obj.setEwbDate(arr[286] != null ? arr[286].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[287] != null ? arr[287].toString() : null);
		obj.setAccountingVoucherDate(
				arr[288] != null ? arr[288].toString() : null);
		obj.setDocumentReferenceNumber(
				arr[289] != null ? arr[289].toString() : null);
		obj.setUserDefinedField16(
				arr[290] != null ? arr[290].toString() : null);
		obj.setUserDefinedField17(
				arr[291] != null ? arr[291].toString() : null);
		obj.setUserDefinedField18(
				arr[292] != null ? arr[292].toString() : null);
		obj.setUserDefinedField19(
				arr[293] != null ? arr[293].toString() : null);
		obj.setUserDefinedField20(
				arr[294] != null ? arr[294].toString() : null);
		obj.setUserDefinedField21(
				arr[295] != null ? arr[295].toString() : null);
		obj.setUserDefinedField22(
				arr[296] != null ? arr[296].toString() : null);
		obj.setUserDefinedField23(
				arr[297] != null ? arr[297].toString() : null);
		obj.setUserDefinedField24(
				arr[298] != null ? arr[298].toString() : null);
		obj.setUserDefinedField25(
				arr[299] != null ? arr[299].toString() : null);
		obj.setUserDefinedField26(
				arr[300] != null ? arr[300].toString() : null);
		obj.setUserDefinedField27(
				arr[301] != null ? arr[301].toString() : null);
		obj.setUserDefinedField28(
				arr[302] != null ? arr[302].toString() : null);
		obj.setUserDefinedField29(
				arr[303] != null ? arr[303].toString() : null);
		obj.setUserDefinedField30(
				arr[304] != null ? arr[304].toString() : null);

		return obj;

	}

	private String CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null
						? (((Integer) value > 0) ? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof Long) {
				return (value != null
						? (((Long) value > 0) ? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString()
								: value.toString())
						: null);
			} else {
				if (!value.toString().isEmpty()) {
					return "-" + value.toString().replaceFirst("-", "");
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
