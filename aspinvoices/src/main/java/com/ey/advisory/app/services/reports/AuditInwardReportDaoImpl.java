package com.ey.advisory.app.services.reports;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.views.client.GSTR2ProcessedInwarddto;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 */
@Component("AuditInwardReportDaoImpl")
public class AuditInwardReportDaoImpl implements AuditOutwardReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuditInwardReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy, HH:mm:ss");

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	private static final List<String> DATA_ORIGION_TYPE_API = ImmutableList
			.of("A");

	@Override
	public List<Object> getAudOutwardReports(SearchCriteria criteria) {
		AuditTrailReportsReqDto request = (AuditTrailReportsReqDto) criteria;

		String docnum = request.getDocNum();
		// String taxperiod = request.getReturnPeriod();
		String gstin = request.getGstin();
		String docdate = request.getDocDate().toString();
		String doctype = request.getDocType();
		String docKey = request.getDocKey();
		String supplierGstin = request.getSgstin();
		String supplierLegalName = request.getLegalName();

		StringBuilder buildHeader = new StringBuilder();

		if (docnum != null && !docnum.isEmpty()) {
			buildHeader.append(" HDR.DOC_NUM = :docnum");
		}

		/*
		 * if (taxperiod != null) {
		 * buildHeader.append(" AND HDR.DERIVED_RET_PERIOD  = :taxperiod "); }
		 */
		if (gstin != null && !gstin.isEmpty()) {
			buildHeader.append(" AND HDR.CUST_GSTIN  = :gstin ");
		}

		if (docdate != null && !docdate.isEmpty()) {
			buildHeader.append(" AND HDR.DOC_DATE  = :docdate ");
		}

		if (doctype != null && !doctype.isEmpty()) {
			buildHeader.append(" AND HDR.DOC_TYPE  = :doctype ");
		}

		if (docKey != null && !docKey.isEmpty()) {
			buildHeader.append(" AND HDR.DOC_KEY  = :docKey ");
		}

		if (supplierGstin != null && !supplierGstin.isEmpty()) {
			buildHeader.append(
					" AND (CASE WHEN HDR.SUPPLIER_GSTIN IS NULL THEN  HDR.CUST_SUPP_NAME ELSE HDR.SUPPLIER_GSTIN END)= :supplierGstin ");
		} else if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			buildHeader.append(
					" AND (CASE WHEN HDR.SUPPLIER_GSTIN IS NULL THEN  HDR.CUST_SUPP_NAME ELSE HDR.SUPPLIER_GSTIN END)= :supplierLegalName ");
		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (docnum != null && !docnum.isEmpty()) {
			q.setParameter("docnum", docnum);
		}

		if (gstin != null && !gstin.isEmpty()) {
			q.setParameter("gstin", gstin);
		}

		if (docdate != null && !docdate.isEmpty()) {
			q.setParameter("docdate", docdate);
		}

		if (doctype != null && !doctype.isEmpty()) {
			q.setParameter("doctype", doctype);
		}

		if (docKey != null && !docKey.isEmpty()) {
			q.setParameter("docKey", docKey);
		}

		if (supplierGstin != null && !supplierGstin.isEmpty()) {
			q.setParameter("supplierGstin", supplierGstin);
		} else if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			q.setParameter("supplierLegalName", supplierLegalName);
		}
		/*
		 * if (taxperiod != null) { int derivedRetPeriod = GenUtil
		 * .convertTaxPeriodToInt(request.getReturnPeriod());
		 * q.setParameter("taxperiod", derivedRetPeriod); }
		 */
		List<Object[]> list = q.getResultList();
		int i = list.size();
		List<Object> list1 = new ArrayList<>();
		for (Object[] arr : list) {
			Object obj = convertProcessed(arr, i);
			i--;
			list1.add(obj);
		}

		return list1;
	}

	private GSTR2ProcessedInwarddto convertProcessed(Object[] arr, int i) {
		GSTR2ProcessedInwarddto obj = new GSTR2ProcessedInwarddto();

		obj.setIrn(arr[0] != null ? arr[0].toString() : null);
		obj.setIrnDate(arr[1] != null ? arr[1].toString() : null);
		obj.setTaxScheme(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setDocCategory(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentDate(arr[7] != null ? arr[7].toString() : null);
		obj.setReverseChargeFlag(arr[8] != null ? arr[8].toString() : null);
		obj.setSupplierGSTIN(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierTradeName(arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierLegalName(arr[11] != null ? arr[11].toString() : null);
		obj.setSupplierAddress1(arr[12] != null ? arr[12].toString() : null);
		obj.setSupplierAddress2(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierLocation(arr[14] != null ? arr[14].toString() : null);
		obj.setSupplierPincode(arr[15] != null ? arr[15].toString() : null);
		obj.setSupplierStateCode(
				arr[16] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[16].toString()) : null);
		obj.setSupplierPhone(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplierEmail(arr[18] != null ? arr[18].toString() : null);
		obj.setCustomerGSTIN(arr[19] != null ? arr[19].toString() : null);
		obj.setCustomerTradeName(arr[20] != null ? arr[20].toString() : null);
		obj.setCustomerLegalName(arr[21] != null ? arr[21].toString() : null);
		obj.setCustomerAddress1(arr[22] != null ? arr[22].toString() : null);
		obj.setCustomerAddress2(arr[23] != null ? arr[23].toString() : null);
		obj.setCustomerLocation(arr[24] != null ? arr[24].toString() : null);
		obj.setCustomerPincode(arr[25] != null ? arr[25].toString() : null);
		obj.setCustomerStateCode(
				arr[26] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[26].toString()) : null);
		obj.setBillingPOS(arr[27] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[27].toString()) : null);
		obj.setCustomerPhone(arr[28] != null ? arr[28].toString() : null);
		obj.setCustomerEmail(arr[29] != null ? arr[29].toString() : null);
		obj.setDispatcherGSTIN(arr[30] != null ? arr[30].toString() : null);
		obj.setDispatcherTradeName(arr[31] != null ? arr[31].toString() : null);
		obj.setDispatcherAddress1(arr[32] != null ? arr[32].toString() : null);
		obj.setDispatcherAddress2(arr[33] != null ? arr[33].toString() : null);
		obj.setDispatcherLocation(arr[34] != null ? arr[34].toString() : null);
		obj.setDispatcherPincode(arr[35] != null ? arr[35].toString() : null);
		obj.setDispatcherStateCode(
				arr[36] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[36].toString()) : null);
		obj.setShipToGSTIN(arr[37] != null ? arr[37].toString() : null);
		obj.setShipToTradeName(arr[38] != null ? arr[38].toString() : null);
		obj.setShipToLegalName(arr[39] != null ? arr[39].toString() : null);
		obj.setShipToAddress1(arr[40] != null ? arr[40].toString() : null);
		obj.setShipToAddress2(arr[41] != null ? arr[41].toString() : null);
		obj.setShipToLocation(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToPincode(arr[43] != null ? arr[43].toString() : null);
		obj.setShipToStateCode(
				arr[44] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[44].toString()) : null);
		obj.setItemSerialNumber(arr[45] != null ? arr[45].toString() : null);
		obj.setProductSerialNumber(arr[46] != null ? arr[46].toString() : null);
		obj.setProductName(arr[47] != null ? arr[47].toString() : null);
		obj.setProductDescription(arr[48] != null ? arr[48].toString() : null);
		obj.setIsService(arr[49] != null ? arr[49].toString() : null);
		obj.setHsn(arr[50] != null ? arr[50].toString() : null);
		obj.setBarcode(arr[51] != null ? arr[51].toString() : null);
		obj.setBatchName(arr[52] != null ? arr[52].toString() : null);
		obj.setBatchExpiryDate(arr[53] != null ? arr[53].toString() : null);
		obj.setWarrantyDate(arr[54] != null ? arr[54].toString() : null);
		obj.setOrderlineReference(arr[55] != null ? arr[55].toString() : null);
		obj.setAttributeName(arr[56] != null ? arr[56].toString() : null);
		obj.setAttributeValue(arr[57] != null ? arr[57].toString() : null);
		obj.setOriginCountry(arr[58] != null ? arr[58].toString() : null);
		obj.setuQC(arr[59] != null ? arr[59].toString() : null);
		obj.setQuantity(arr[60] != null ? arr[60].toString() : null);
		obj.setFreeQuantity(arr[61] != null ? arr[61].toString() : null);
		obj.setUnitPrice(arr[62] != null ? arr[62].toString() : null);
		obj.setItemAmount(arr[63] != null ? arr[63].toString() : null);
		obj.setItemDiscount(arr[64] != null ? arr[64].toString() : null);
		obj.setPreTaxAmount(arr[65] != null ? arr[65].toString() : null);
		obj.setItemAssessableAmount(
				arr[66] != null ? arr[66].toString() : null);
		obj.setiGSTRate(arr[67] != null ? arr[67].toString() : null);
		obj.setiGSTAmount(arr[68] != null ? arr[68].toString() : null);
		obj.setcGSTRate(arr[69] != null ? arr[69].toString() : null);
		obj.setcGSTAmount(arr[70] != null ? arr[70].toString() : null);
		obj.setsGSTRate(arr[71] != null ? arr[71].toString() : null);
		obj.setsGSTAmount(arr[72] != null ? arr[72].toString() : null);
		obj.setCessAdvaloremRate(arr[73] != null ? arr[73].toString() : null);
		obj.setCessAdvaloremAmount(arr[74] != null ? arr[74].toString() : null);
		obj.setCessSpecificRate(arr[75] != null ? arr[75].toString() : null);
		obj.setCessSpecificAmount(arr[76] != null ? arr[76].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[77] != null ? arr[77].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[78] != null ? arr[78].toString() : null);
		obj.setStateCessSpecificRate(
				arr[79] != null ? arr[79].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[80] != null ? arr[80].toString() : null);
		obj.setItemOtherCharges(arr[81] != null ? arr[81].toString() : null);
		obj.setTotalItemAmount(arr[82] != null ? arr[82].toString() : null);
		obj.setInvoiceOtherCharges(arr[83] != null ? arr[83].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[84] != null ? arr[84].toString() : null);
		obj.setInvoiceIGSTAmount(arr[85] != null ? arr[85].toString() : null);
		obj.setInvoiceCGSTAmount(arr[86] != null ? arr[86].toString() : null);
		obj.setInvoiceSGSTAmount(arr[87] != null ? arr[87].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[88] != null ? arr[88].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[89] != null ? arr[89].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[90] != null ? arr[90].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[91] != null ? arr[91].toString() : null);
		obj.setInvoiceValue(arr[92] != null ? arr[92].toString() : null);
		obj.setRoundOff(arr[93] != null ? arr[93].toString() : null);
		obj.setTotalInvoiceValue(arr[94] != null ? arr[94].toString() : null);
		obj.setEligibilityIndicator(
				arr[95] != null ? arr[95].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[96] != null ? arr[96].toString() : null);
		obj.setAvailableIgst(arr[97] != null ? arr[97].toString() : null);
		obj.setAvailableCgst(arr[98] != null ? arr[98].toString() : null);
		obj.setAvailableSgst(arr[99] != null ? arr[99].toString() : null);
		obj.setAvailableCess(arr[100] != null ? arr[100].toString() : null);
		obj.setiTCEntitlement(arr[101] != null ? arr[101].toString() : null);
		obj.setiTCReversalIdentifier(
				arr[102] != null ? arr[102].toString() : null);
		obj.settCSFlagIncomeTax(arr[103] != null ? arr[103].toString() : null);
		obj.settCSRateIncomeTax(arr[104] != null ? arr[104].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[105] != null ? arr[105].toString() : null);
		obj.setCurrencyCode(arr[106] != null ? arr[106].toString() : null);
		obj.setCountryCode(arr[107] != null ? arr[107].toString() : null);
		obj.setInvoiceValueFC(arr[108] != null ? arr[108].toString() : null);
		obj.setPortCode(arr[109] != null ? arr[109].toString() : null);
		obj.setBillofEntry(arr[110] != null ? arr[110].toString() : null);
		obj.setBillofEntryDate(arr[111] != null ? arr[111].toString() : null);
		obj.setInvoiceRemarks(arr[112] != null ? arr[112].toString() : null);
		obj.setInvoicePeriodStartDate(
				arr[113] != null ? arr[113].toString() : null);
		obj.setInvoicePeriodEndDate(
				arr[114] != null ? arr[114].toString() : null);
		obj.setPreceedingInvoiceNumber(
				arr[115] != null ? arr[115].toString() : null);
		obj.setPreceedingInvoiceDate(
				arr[116] != null ? arr[116].toString() : null);
		obj.setOtherReference(arr[117] != null ? arr[117].toString() : null);
		obj.setReceiptAdviceReference(
				arr[118] != null ? arr[118].toString() : null);
		obj.setReceiptAdviceDate(arr[119] != null ? arr[119].toString() : null);
		obj.setTenderReference(arr[120] != null ? arr[120].toString() : null);
		obj.setContractReference(arr[121] != null ? arr[121].toString() : null);
		obj.setExternalReference(arr[122] != null ? arr[122].toString() : null);
		obj.setProjectReference(arr[123] != null ? arr[123].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[124] != null ? arr[124].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[125] != null ? arr[125].toString() : null);
		obj.setPayeeName(arr[126] != null ? arr[126].toString() : null);
		obj.setModeOfPayment(arr[127] != null ? arr[127].toString() : null);
		obj.setBranchOrIFSCCode(arr[128] != null ? arr[128].toString() : null);
		obj.setPaymentTerms(arr[129] != null ? arr[129].toString() : null);
		obj.setPaymentInstruction(
				arr[130] != null ? arr[130].toString() : null);
		obj.setCreditTransfer(arr[131] != null ? arr[131].toString() : null);
		obj.setDirectDebit(arr[132] != null ? arr[132].toString() : null);
		obj.setCreditDays(arr[133] != null ? arr[133].toString() : null);
		obj.setPaidAmount(arr[134] != null ? arr[134].toString() : null);
		obj.setBalanceAmount(arr[135] != null ? arr[135].toString() : null);
		obj.setPaymentDueDate(arr[136] != null ? arr[136].toString() : null);
		obj.setAccountDetail(arr[137] != null ? arr[137].toString() : null);
		obj.setEcomGSTIN(arr[138] != null ? arr[138].toString() : null);
		obj.setSupportingDocURL(arr[139] != null ? arr[139].toString() : null);
		obj.setSupportingDocument(
				arr[140] != null ? arr[140].toString() : null);
		obj.setAdditionalInformation(
				arr[141] != null ? arr[141].toString() : null);
		obj.setTransactionType(arr[142] != null ? arr[142].toString() : null);
		obj.setSubSupplyType(arr[143] != null ? arr[143].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[144] != null ? arr[144].toString() : null);
		obj.setTransporterID(arr[145] != null ? arr[145].toString() : null);
		obj.setTransporterName(arr[146] != null ? arr[146].toString() : null);
		obj.setTransportMode(arr[147] != null ? arr[147].toString() : null);
		obj.setTransportDocNo(arr[148] != null ? arr[148].toString() : null);
		obj.setTransportDocDate(arr[149] != null ? arr[149].toString() : null);
		obj.setDistance(arr[150] != null ? arr[150].toString() : null);
		obj.setVehicleNo(arr[151] != null ? arr[151].toString() : null);
		obj.setVehicleType(arr[152] != null ? arr[152].toString() : null);
		obj.setReturnPeriod(
				arr[153] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[153].toString()) : null);
		obj.setOriginalDocumentType(
				arr[154] != null ? arr[154].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[155] != null ? arr[155].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[156] != null ? arr[156].toString() : null);
		obj.setSec7ofIGSTFlag(arr[157] != null ? arr[157].toString() : null);
		obj.setClaimRefndFlag(arr[158] != null ? arr[158].toString() : null);
		obj.setAutoPopltToRefund(arr[159] != null ? arr[159].toString() : null);
		obj.setcRDRPreGST(arr[160] != null ? arr[160].toString() : null);
		obj.setSupplierType(arr[161] != null ? arr[161].toString() : null);
		obj.setSupplierCode(arr[162] != null ? arr[162].toString() : null);
		obj.setProductCode(arr[163] != null ? arr[163].toString() : null);
		obj.setCategoryOfProduct(arr[164] != null ? arr[164].toString() : null);
		obj.setStateApplyingCess(arr[165] != null ? arr[165].toString() : null);
		obj.setCif(arr[166] != null ? arr[166].toString() : null);
		obj.setCustomDuty(arr[167] != null ? arr[167].toString() : null);
		obj.setExchangeRate(arr[168] != null ? arr[168].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[169] != null ? arr[169].toString() : null);
		obj.settCSFlagGST(arr[170] != null ? arr[170].toString() : null);
		obj.settCSIGSTAmount(arr[171] != null ? arr[171].toString() : null);
		obj.settCSCGSTAmount(arr[172] != null ? arr[172].toString() : null);
		obj.settCSSGSTAmount(arr[173] != null ? arr[173].toString() : null);
		obj.settDSFlagGST(arr[174] != null ? arr[174].toString() : null);
		obj.settDSIGSTAmount(arr[175] != null ? arr[175].toString() : null);
		obj.settDSCGSTAmount(arr[176] != null ? arr[176].toString() : null);
		obj.settDSSGSTAmount(arr[177] != null ? arr[177].toString() : null);
		// obj.setUserID(arr[178] != null ? arr[178].toString() : null);
		obj.setCompanyCode(arr[179] != null ? arr[179].toString() : null);
		obj.setSourceIdentifier(arr[180] != null ? arr[180].toString() : null);
		obj.setSourceFileName(arr[181] != null ? arr[181].toString() : null);
		obj.setPlantCode(arr[182] != null ? arr[182].toString() : null);
		obj.setDivision(arr[183] != null ? arr[183].toString() : null);
		obj.setSubDivision(arr[184] != null ? arr[184].toString() : null);
		obj.setLocation(arr[185] != null ? arr[185].toString() : null);
		obj.setPurchaseOrganisation(
				arr[186] != null ? arr[186].toString() : null);
		obj.setProfitCentre1(arr[187] != null ? arr[187].toString() : null);
		obj.setProfitCentre2(arr[188] != null ? arr[188].toString() : null);
		obj.setProfitCentre3(arr[189] != null ? arr[189].toString() : null);
		obj.setProfitCentre4(arr[190] != null ? arr[190].toString() : null);
		obj.setProfitCentre5(arr[191] != null ? arr[191].toString() : null);
		obj.setProfitCentre6(arr[192] != null ? arr[192].toString() : null);
		obj.setProfitCentre7(arr[193] != null ? arr[193].toString() : null);
		obj.setProfitCentre8(arr[194] != null ? arr[194].toString() : null);
		obj.setGlAssessableValue(arr[195] != null ? arr[195].toString() : null);
		obj.setGlIGST(arr[196] != null ? arr[196].toString() : null);
		obj.setGlCGST(arr[197] != null ? arr[197].toString() : null);
		obj.setGlSGST(arr[198] != null ? arr[198].toString() : null);
		obj.setGlAdvaloremCess(arr[199] != null ? arr[199].toString() : null);
		obj.setGlSpecificCess(arr[200] != null ? arr[200].toString() : null);
		obj.setgLStateCessAdvalorem(
				arr[201] != null ? arr[201].toString() : null);
		obj.setgLStateCessSpecific(
				arr[202] != null ? arr[202].toString() : null);
		obj.setGlPostingDate(arr[203] != null ? arr[203].toString() : null);
		obj.setPurchaseOrderValue(
				arr[204] != null ? arr[204].toString() : null);
		obj.seteWBNumber(arr[205] != null ? arr[205].toString() : null);
		obj.seteWBDate(arr[206] != null ? arr[206].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[207] != null ? arr[207].toString() : null);
		obj.setAccountingVoucherDate(
				arr[208] != null ? arr[208].toString() : null);
		obj.setDocumentReferenceNumber(
				arr[209] != null ? arr[209].toString() : null);
		obj.setUserDefField1(arr[210] != null ? arr[210].toString() : null);
		obj.setUserDefField2(arr[211] != null ? arr[211].toString() : null);
		obj.setUserDefField3(arr[212] != null ? arr[212].toString() : null);
		obj.setUserDefField4(arr[213] != null ? arr[213].toString() : null);
		obj.setUserDefField5(arr[214] != null ? arr[214].toString() : null);
		obj.setUserDefField6(arr[215] != null ? arr[215].toString() : null);
		obj.setUserDefField7(arr[216] != null ? arr[216].toString() : null);
		obj.setUserDefField8(arr[217] != null ? arr[217].toString() : null);
		obj.setUserDefField9(arr[218] != null ? arr[218].toString() : null);
		obj.setUserDefField10(arr[219] != null ? arr[219].toString() : null);
		obj.setUserDefField11(arr[220] != null ? arr[220].toString() : null);
		obj.setUserDefField12(arr[221] != null ? arr[221].toString() : null);
		obj.setUserDefField13(arr[222] != null ? arr[222].toString() : null);
		obj.setUserDefField14(arr[223] != null ? arr[223].toString() : null);
		obj.setUserDefField15(arr[224] != null ? arr[224].toString() : null);
		obj.setUserDefField16(arr[225] != null ? arr[225].toString() : null);
		obj.setUserDefField17(arr[226] != null ? arr[226].toString() : null);
		obj.setUserDefField18(arr[227] != null ? arr[227].toString() : null);
		obj.setUserDefField19(arr[228] != null ? arr[228].toString() : null);
		obj.setUserDefField20(arr[229] != null ? arr[229].toString() : null);
		obj.setUserDefField21(arr[230] != null ? arr[230].toString() : null);
		obj.setUserDefField22(arr[231] != null ? arr[231].toString() : null);
		obj.setUserDefField23(arr[232] != null ? arr[232].toString() : null);
		obj.setUserDefField24(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField25(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField26(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField27(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField28(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField29(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField30(arr[239] != null ? arr[239].toString() : null);
		String errDesc = null;
		String errCode = (arr[240] != null ? arr[240].toString() : null);

		if (!Strings.isNullOrEmpty(errCode)) {
			String oldReturntPeriod = deriveOldReturnPeriod(arr,
					obj.getDocumentNumber());
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);

			Map<String, String> dynamicMap = new HashMap<>();
			if (!Strings.isNullOrEmpty(oldReturntPeriod)) {
				dynamicMap.put("ER1277", oldReturntPeriod);
			}
			errDesc = ErrorMasterUtil.findDynamicErrorInfoByErrorCodes(
					errCodeList, "INWARD", dynamicMap);
		}

		/*
		 * errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "INWARD"); }
		 */
		obj.setDigigstErrorDesc(errDesc);
		String infoCode = (arr[242] != null) ? arr[242].toString() : null;
		String infoDesc = null;
		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "INWARD");
		}
		obj.setDigigstInformationDesc(infoDesc);
		obj.setReturnType(arr[244] != null ? arr[244].toString() : null);
		obj.setDataCategory(arr[245] != null ? arr[245].toString() : null);
		obj.setTableNumber(arr[246] != null ? arr[246].toString() : null);
		obj.setRecordStatus(arr[247] != null ? arr[247].toString() : null);
		obj.setInvoiceValueDigigst(
				arr[248] != null ? arr[248].toString() : null);
		obj.setSource(arr[249] != null ? arr[249].toString() : null);
		obj.setSourceID(arr[250] != null ? arr[250].toString() : null);
		obj.setFileName(arr[251] != null ? arr[251].toString() : null);

		if (arr[252] != null) {
			Timestamp timeStamp = (Timestamp) arr[252];
			LocalDateTime localDT = timeStamp.toLocalDateTime();
			LocalDateTime convertref = EYDateUtil.toISTDateTimeFromUTC(localDT);
			obj.setDigiGSTDateTime(convertref.toString());
		} else {
			obj.setDigiGSTDateTime(null);
		}
		// obj.setProcessFreq(String.valueOf(i));
		if (arr[254] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[254];

			LocalDateTime processdate = timeStamp1.toLocalDateTime();
			String dateTime = formatter1
					.format(EYDateUtil.toISTDateTimeFromUTC(processdate))
					.toString();

			obj.setProcessDateTime(dateTime);
		} else {
			obj.setProcessDateTime(null);
		}

		String createdBy = arr[250] != null ? arr[250].toString() : null;

		// obj.setUserID(arr[178] != null ? arr[178].toString() : null);
		String userId = arr[178] != null ? arr[178].toString() : null;

		String dataOriginTypeCode = arr[256] != null ? arr[256].toString()
				: null;
		List<String> entity1 = repo.findEmailByUser(createdBy);

		if (DATA_ORIGION_TYPE_API
				.contains(trimAndConvToUpperCase(dataOriginTypeCode))) {
			obj.setUserIDN(userId);
		} else {

			if (entity1 != null && !entity1.isEmpty()) {
				String user = entity1.get(0);
				String usernameEmail = "";
				usernameEmail = createdBy + "(" + user + ")";
				obj.setUserIDN(usernameEmail);
			} else {
				obj.setUserIDN(createdBy);
			}
		}
		// obj.setProcessFreq(String.valueOf(i));
		obj.setProcessFreq(arr[255] != null ? arr[255].toString() : null);

		return obj;
	}

	private String createApiProcessedQueryString(String buildHeader) {

		return " SELECT TO_CHAR(HDR.IRN), TO_CHAR(HDR.IRN_DATE,'DD-MM-YYYY') IRN_DATE, "
				+ "TO_CHAR(TAX_SCHEME), TO_CHAR(HDR.SUPPLY_TYPE), TO_CHAR(ITM.DOC_CATEGORY), "
				+ "TO_CHAR(HDR.DOC_TYPE), TO_CHAR(HDR.DOC_NUM), TO_CHAR(HDR.DOC_DATE), "
				+ "TO_CHAR(ITM.REVERSE_CHARGE), TO_CHAR(HDR.SUPPLIER_GSTIN), "
				+ "TO_CHAR(ITM.SUPP_TRADE_NAME), TO_CHAR(ITM.CUST_SUPP_NAME), "
				+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS1), TO_CHAR(ITM.CUST_SUPP_ADDRESS2), "
				+ "TO_CHAR(ITM.CUST_SUPP_ADDRESS3), TO_CHAR(ITM.CUST_SUPP_ADDRESS4), "
				+ "TO_CHAR(ITM.SUPP_STATE_CODE), TO_CHAR(ITM.SUPP_PHONE), "
				+ "TO_CHAR(ITM.SUPP_EMAIL), TO_CHAR(HDR.CUST_GSTIN), "
				+ "TO_CHAR(ITM.CUST_TRADE_NAME), TO_CHAR(ITM.CUST_LEGAL_NAME), "
				+ "TO_CHAR(ITM.CUST_BUILDING_NUM ), TO_CHAR(ITM.CUST_BUILDING_NAME), "
				+ "TO_CHAR(ITM.CUST_LOCATION), TO_CHAR(ITM.CUST_PINCODE), "
				+ "TO_CHAR(ITM.BILL_TO_STATE), TO_CHAR(ITM.POS), "
				+ "TO_CHAR(ITM.CUST_PHONE), TO_CHAR(ITM.CUST_EMAIL), "
				+ "TO_CHAR(HDR.DISPATCHER_GSTIN), TO_CHAR(ITM.DISPATCHER_TRADE_NAME), "
				+ "TO_CHAR(DISPATCHER_BUILDING_NUM), TO_CHAR(DISPATCHER_BUILDING_NAME), "
				+ "TO_CHAR(DISPATCHER_LOCATION), TO_CHAR(DISPATCHER_PINCODE), "
				+ "TO_CHAR(DISPATCHER_STATE_CODE), TO_CHAR(HDR.SHIP_TO_GSTIN), "
				+ "TO_CHAR(SHIP_TO_TRADE_NAME), TO_CHAR(SHIP_TO_LEGAL_NAME), "
				+ "TO_CHAR(SHIP_TO_BUILDING_NUM), TO_CHAR(SHIP_TO_BUILDING_NAME), "
				+ "TO_CHAR(SHIP_TO_LOCATION), TO_CHAR(SHIP_TO_PINCODE), "
				+ "TO_CHAR(SHIP_TO_STATE), TO_CHAR(ITM_NO), TO_CHAR(SERIAL_NUM2), "
				+ "TO_CHAR(PRODUCT_NAME), TO_CHAR(ITM_DESCRIPTION), "
				+ "TO_CHAR(IS_SERVICE), TO_CHAR(ITM_HSNSAC), TO_CHAR(BAR_CODE), "
				+ "TO_CHAR(BATCH_NAME_OR_NUM), TO_CHAR(BATCH_EXPIRY_DATE), "
				+ "TO_CHAR(WARRANTY_DATE), TO_CHAR(ORDER_ITEM_REFERENCE), "
				+ "TO_CHAR(ATTRIBUTE_NAME), TO_CHAR(ATTRIBUTE_VALUE), "
				+ "TO_CHAR(ORIGIN_COUNTRY), TO_CHAR(ITM_UQC), TO_CHAR(ITM_QTY), "
				+ "TO_CHAR(FREE_QTY), TO_CHAR(UNIT_PRICE), TO_CHAR(ITEM_AMT_UP_QTY), "
				+ "TO_CHAR(ITEM_DISCOUNT), TO_CHAR(PRE_TAX_AMOUNT), TO_CHAR(ITM.TAXABLE_VALUE), "
				+ "TO_CHAR(ITM.IGST_RATE), TO_CHAR(ITM.IGST_AMT), TO_CHAR(ITM.CGST_RATE), "
				+ "TO_CHAR(ITM.CGST_AMT), TO_CHAR(ITM.SGST_RATE), TO_CHAR(ITM.SGST_AMT), "
				+ "TO_CHAR(ITM.CESS_RATE_ADVALOREM), TO_CHAR(ITM.CESS_AMT_ADVALOREM), "
				+ "TO_CHAR(ITM.CESS_RATE_SPECIFIC), TO_CHAR(ITM.CESS_AMT_SPECIFIC), "
				+ "TO_CHAR(ITM.STATECESS_RATE), TO_CHAR(ITM.STATECESS_AMT), "
				+ "TO_CHAR(ITM.STATE_CESS_SPECIFIC_RATE), "
				+ "TO_CHAR(ITM.STATE_CESS_SPECIFIC_AMOUNT), "
				+ "TO_CHAR(ITM.OTHER_VALUES), TO_CHAR(TOT_ITEM_AMT), "
				+ "TO_CHAR(ITM.INV_OTHER_CHARGES), "
				+ "TO_CHAR(ITM.INV_ASSESSABLE_AMT), TO_CHAR(ITM.INV_IGST_AMT), "
				+ "TO_CHAR(ITM.INV_CGST_AMT), "
				+ "TO_CHAR(ITM.INV_SGST_AMT), TO_CHAR(ITM.INV_CESS_ADVLRM_AMT), "
				+ "TO_CHAR(ITM.INV_CESS_SPECIFIC_AMT), TO_CHAR(ITM.INV_STATE_CESS_AMT), "
				+ "TO_CHAR(ITM.INV_STATE_CESS_SPECIFIC_AMOUNT), TO_CHAR(ITM.LINE_ITEM_AMT), "
				+ "TO_CHAR(ITM.ROUND_OFF), TO_CHAR(ITM.TOT_INV_VAL_WORDS), "
				+ "TO_CHAR(ELIGIBILITY_INDICATOR),"
				+ " TO_CHAR(COMMON_SUP_INDICATOR), TO_CHAR(ITM.AVAILABLE_IGST), "
				+ "TO_CHAR(ITM.AVAILABLE_CGST), "
				+ "TO_CHAR(ITM.AVAILABLE_SGST), TO_CHAR(ITM.AVAILABLE_CESS), "
				+ "TO_CHAR(ITM.ITC_ENTITLEMENT), "
				+ "TO_CHAR(ITM.ITC_REVERSAL_IDENTIFER), TO_CHAR(ITM.TCS_FLAG_INCOME_TAX), "
				+ "TO_CHAR(TCS_RATE_INCOME_TAX), "
				+ "TO_CHAR(TCS_AMOUNT_INCOME_TAX), TO_CHAR(FOREIGN_CURRENCY), "
				+ "TO_CHAR(COUNTRY_CODE), "
				+ "TO_CHAR(INV_VAL_FC), TO_CHAR(ITM.SHIP_PORT_CODE), "
				+ "TO_CHAR(ITM.BILL_OF_ENTRY), "
				+ "TO_CHAR(ITM.BILL_OF_ENTRY_DATE), TO_CHAR(INV_REMARKS), "
				+ "TO_CHAR(INV_PERIOD_START_DATE), "
				+ "TO_CHAR(INV_PERIOD_END_DATE), TO_CHAR(ITM.ORIGINAL_DOC_NUM), "
				+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE), "
				+ "TO_CHAR(ITM.INV_REFERENCE), TO_CHAR(RECEIPT_ADVICE_REFERENCE), "
				+ "TO_CHAR(RECEIPT_ADVICE_DATE), TO_CHAR(TENDER_REFERENCE), "
				+ "TO_CHAR(CONTRACT_REFERENCE), "
				+ "TO_CHAR(EXTERNAL_REFERENCE), TO_CHAR(PROJECT_REFERENCE), "
				+ "TO_CHAR(CONTRACT_NUMBER), "
				+ "TO_CHAR(CONTRACT_DATE), TO_CHAR(PAYEE_NAME), "
				+ "TO_CHAR(MODE_OF_PAYMENT), "
				+ "TO_CHAR(BRANCH_IFSC_CODE), TO_CHAR(PAYMENT_TERMS), "
				+ "TO_CHAR(PAYMENT_INSTRUCTION), "
				+ "TO_CHAR(CR_TRANSFER), TO_CHAR(DB_DIRECT), TO_CHAR(CR_DAYS), TO_CHAR(PAID_AMT), "
				+ "TO_CHAR(BAL_AMT), TO_CHAR(ITM.PAYMENT_DUE_DATE), TO_CHAR(ACCOUNT_DETAIL), "
				+ "TO_CHAR(ITM.ECOM_GSTIN), TO_CHAR(SUPPORTING_DOC_URL), TO_CHAR(SUPPORTING_DOC), "
				+ "TO_CHAR(ADDITIONAL_INFORMATION), TO_CHAR(TRANS_TYPE), "
				+ "TO_CHAR(SUB_SUPP_TYPE), TO_CHAR(OTHER_SUPP_TYPE_DESC), "
				+ "TO_CHAR(TRANSPORTER_ID), TO_CHAR(TRANSPORTER_NAME), TO_CHAR(TRANSPORT_MODE), "
				+ "TO_CHAR(TRANSPORT_DOC_NUM), TO_CHAR(TRANSPORT_DOC_DATE), TO_CHAR(DISTANCE), "
				+ "TO_CHAR(VEHICLE_NUM), TO_CHAR(VEHICLE_TYPE), TO_CHAR(HDR.RETURN_PERIOD), "
				+ "TO_CHAR(ITM.ORIGINAL_DOC_TYPE), TO_CHAR(ITM.ORIG_SUPPLIER_GSTIN), "
				+ "TO_CHAR(ITM.DIFF_PERCENT), TO_CHAR(ITM.SECTION7_OF_IGST_FLAG), "
				+ "TO_CHAR(ITM.CLAIM_REFUND_FLAG), TO_CHAR(ITM.AUTOPOPULATE_TO_REFUND), "
				+ "TO_CHAR(ITM.CRDR_PRE_GST), TO_CHAR(ITM.CUST_SUPP_TYPE), "
				+ "TO_CHAR(ITM.CUST_SUPP_CODE), TO_CHAR(ITM.PRODUCT_CODE), "
				+ "TO_CHAR(ITM.ITM_TYPE), TO_CHAR(ITM.STATE_APPLYING_CESS), TO_CHAR(ITM.CIF_VALUE), "
				+ "TO_CHAR(CUSTOM_DUTY), TO_CHAR(EXCHANGE_RATE), TO_CHAR(CRDR_REASON), "
				+ "TO_CHAR(ITM.TCS_FLAG), TO_CHAR(ITM.TCS_IGST_AMT), TO_CHAR(ITM.TCS_CGST_AMT), "
				+ "TO_CHAR(ITM.TCS_SGST_AMT), TO_CHAR(ITM.TDS_FLAG), TO_CHAR(ITM.TDS_IGST_AMT), "
				+ "TO_CHAR(ITM.TDS_CGST_AMT), TO_CHAR(ITM.TDS_SGST_AMT), TO_CHAR(ITM.USER_ID), "
				+ "TO_CHAR(ITM.COMPANY_CODE), TO_CHAR(ITM.SOURCE_IDENTIFIER), "
				+ "TO_CHAR(ITM.SOURCE_FILENAME), "
				+ "TO_CHAR(ITM.PLANT_CODE), TO_CHAR(ITM.DIVISION), TO_CHAR(ITM.SUB_DIVISION), "
				+ "TO_CHAR(ITM.LOCATION), TO_CHAR(ITM.PURCHASE_ORGANIZATION), "
				+ "TO_CHAR(ITM.PROFIT_CENTRE), "
				+ "TO_CHAR(ITM.PROFIT_CENTRE2), TO_CHAR(ITM.USERACCESS1), "
				+ "TO_CHAR(ITM.USERACCESS2), "
				+ "TO_CHAR(ITM.USERACCESS3), TO_CHAR(ITM.USERACCESS4), "
				+ "TO_CHAR(ITM.USERACCESS5), "
				+ "TO_CHAR(ITM.USERACCESS6), TO_CHAR(GLCODE_TAXABLEVALUE), TO_CHAR(GLCODE_IGST), "
				+ "TO_CHAR(GLCODE_CGST), TO_CHAR(GLCODE_SGST), TO_CHAR(GLCODE_ADV_CESS), "
				+ "TO_CHAR(GLCODE_SP_CESS), TO_CHAR(GLCODE_STATE_CESS), "
				+ "TO_CHAR(GL_STATE_CESS_SPECIFIC), "
				+ "TO_CHAR(ITM.POSTING_DATE), TO_CHAR(CONTRACT_VALUE), TO_CHAR(ITM.EWAY_BILL_NUM), "
				+ "TO_CHAR(ITM.EWAY_BILL_DATE), TO_CHAR(ITM.PURCHASE_VOUCHER_NUM), "
				+ "TO_CHAR(ITM.PURCHASE_VOUCHER_DATE), TO_CHAR(ITM.DOCUMENT_REFERENCE_NUMBER), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD1), TO_CHAR(ITM.USERDEFINED_FIELD2), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD3), TO_CHAR(ITM.USERDEFINED_FIELD4), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD5), TO_CHAR(ITM.USERDEFINED_FIELD6), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD7), TO_CHAR(ITM.USERDEFINED_FIELD8), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD9), TO_CHAR(ITM.USERDEFINED_FIELD10), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD11), TO_CHAR(ITM.USERDEFINED_FIELD12), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD13), TO_CHAR(ITM.USERDEFINED_FIELD14), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD15), TO_CHAR(ITM.USERDEFINED_FIELD16), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD17), TO_CHAR(ITM.USERDEFINED_FIELD18), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD19), TO_CHAR(ITM.USERDEFINED_FIELD20), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD21), TO_CHAR(ITM.USERDEFINED_FIELD22), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD23), TO_CHAR(ITM.USERDEFINED_FIELD24), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD25), TO_CHAR(ITM.USERDEFINED_FIELD26), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD27), TO_CHAR(ITM.USERDEFINED_FIELD28), "
				+ "TO_CHAR(ITM.USERDEFINED_FIELD29), TO_CHAR(ITM.USERDEFINED_FIELD30), "
				+ "TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'') ||','|| IFNULL(ITM.ERROR_CODES,'')) "
				+ "AS ERROR_CODE_ASP, NULL AS ERROR_DESCRIPTION_ASP, "
				+ "TRIM(',' FROM IFNULL(HDR.INFORMATION_CODES,'')||','||IFNULL(ITM.INFORMATION_CODES,'')) "
				+ "AS INFO_ERROR_CODE_ASP, NULL AS INFO_ERROR_DESCRIPTION_ASP, "
				+ "HDR.RETURN_TYPE AS RETURN_TYPE, HDR.TAX_DOC_TYPE AS DATA_CATEGORY, "
				+ "HDR.TABLE_SECTION AS TABLE_NUMBER, CASE WHEN HDR.IS_DELETE = TRUE THEN 'INACTIVE' "
				+ "WHEN HDR.IS_DELETE = FALSE THEN 'ACTIVE' END AS RECORD_STATUS, "
				+ "HDR.DOC_AMT AS INVOICE_VALUE_DIGIGST,CASE WHEN DATAORIGINTYPECODE"
				+ " IN('E') THEN 'Web Upload' WHEN DATAORIGINTYPECODE IN('A')"
				+ " THEN 'API Push' WHEN DATAORIGINTYPECODE IN('S') THEN 'SFTP'"
				+ " WHEN DATAORIGINTYPECODE IN('C') THEN 'CSV' WHEN DATAORIGINTYPECODE"
				+ " IN('EI','AI','CI','SI') THEN 'Invoice Management' END"
				+ " PROCESSING_SOURCE,HDR.CREATED_BY AS SOURCE_ID, "
				+ "FIL.FILE_NAME AS FILE_NAME, HDR.CREATED_ON AS ASP_DATE_TIME,HDR.ID,"
				+ "HDR.CREATED_ON,DENSE_RANK() OVER ( PARTITION BY HDR.DOC_NUM,DOC_KEY"
				+ " ORDER BY ITM.DOC_HEADER_ID ASC ) "
				+ "AS PROCESSING_FREQUENCY,DATAORIGINTYPECODE, OLD_RETURN_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER "
				+ "HDR INNER JOIN ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID = FIL.ID "
				+ "WHERE " + buildHeader + " ORDER BY HDR.CREATED_ON DESC";

	}

	private static String deriveOldReturnPeriod(Object[] arr, String docNum) {
		try {
			return (arr[257] != null ? arr[257].toString() : null);
		} catch (Exception e) {
			LOGGER.debug("error for docNum:{}", docNum, e);
		}
		return null;

	}
}
