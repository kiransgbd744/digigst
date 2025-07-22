/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ReversalInwardDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ItcInwardReportDaoImpl")
public class ItcInwardReportDaoImpl implements ItcReversalInwardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Object> getItcReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		// String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String purchase = null;
		String division = null;
		String location = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String GSTIN = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> purchaseList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}

		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" WHERE HDR.CUST_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND ITM.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND ITM.PLANT_CODE IN :plantList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				buildQuery.append(
						" AND ITM.PURCHASE_ORGANIZATION IN :purchaseList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND ITM.HDIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND ITM.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND ITM.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND ITM.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND ITM.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND ITM.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND ITM.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND ITM.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
			buildQuery.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = createInwardApiProcessedRecQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
				q.setParameter("pcList", pcList);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && !plantList.isEmpty()
					&& plantList.size() > 0) {
				q.setParameter("plantList", plantList);
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && !purchaseList.isEmpty()
					&& purchaseList.size() > 0) {
				q.setParameter("purchaseList", purchaseList);
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && !divisionList.isEmpty()
					&& divisionList.size() > 0) {
				q.setParameter("divisionList", divisionList);
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && !locationList.isEmpty()
					&& locationList.size() > 0) {
				q.setParameter("locationList", locationList);
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && !ud1List.isEmpty() && ud1List.size() > 0) {
				q.setParameter("ud1List", ud1List);
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && !ud2List.isEmpty() && ud2List.size() > 0) {
				q.setParameter("ud2List", ud2List);
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && !ud3List.isEmpty() && ud3List.size() > 0) {
				q.setParameter("ud3List", ud3List);
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && !ud4List.isEmpty() && ud4List.size() > 0) {
				q.setParameter("ud4List", ud4List);
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && !ud5List.isEmpty() && ud5List.size() > 0) {
				q.setParameter("ud5List", ud5List);
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && !ud6List.isEmpty() && ud6List.size() > 0) {
				q.setParameter("ud6List", ud6List);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream()
				.map(o -> convertApiInwardProcessedRecords(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ReversalInwardDto convertApiInwardProcessedRecords(Object[] arr) {
		ReversalInwardDto obj = new ReversalInwardDto();

		obj.setCategory(arr[0] != null ? arr[0].toString() : null);
		obj.setIrn(arr[1] != null ? arr[1].toString() : null);
		obj.setIrnDate(arr[2] != null ? arr[2].toString() : null);
		obj.setTaxScheme(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplyType(arr[4] != null ? arr[4].toString() : null);
		obj.setDocCategory(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setDocumentDate(arr[8] != null ? arr[8].toString() : null);
		obj.setReverseChargeFlag(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierTradeName(arr[11] != null ? arr[11].toString() : null);
		obj.setSupplierLegalName(arr[12] != null ? arr[12].toString() : null);
		obj.setSupplierAddress1(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierAddress2(arr[14] != null ? arr[14].toString() : null);
		obj.setSupplierLocation(arr[15] != null ? arr[15].toString() : null);
		obj.setSupplierPincode(arr[16] != null ? arr[16].toString() : null);
		obj.setSupplierStateCode(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplierPhone(arr[18] != null ? arr[18].toString() : null);
		obj.setSupplierEmail(arr[19] != null ? arr[19].toString() : null);
		obj.setCustomerGSTIN(arr[20] != null ? arr[20].toString() : null);
		obj.setCustomerTradeName(arr[21] != null ? arr[21].toString() : null);
		obj.setCustomerLegalName(arr[22] != null ? arr[22].toString() : null);
		obj.setCustomerAddress1(arr[23] != null ? arr[23].toString() : null);
		obj.setCustomerAddress2(arr[24] != null ? arr[24].toString() : null);
		obj.setCustomerLocation(arr[25] != null ? arr[25].toString() : null);
		obj.setCustomerPincode(arr[26] != null ? arr[26].toString() : null);
		obj.setCustomerStateCode(arr[27] != null ? arr[27].toString() : null);
		obj.setBillingPOS(arr[28] != null ? arr[28].toString() : null);
		obj.setCustomerPhone(arr[29] != null ? arr[29].toString() : null);
		obj.setCustomerEmail(arr[30] != null ? arr[30].toString() : null);
		obj.setDispatcherGSTIN(arr[31] != null ? arr[31].toString() : null);
		obj.setDispatcherTradeName(arr[32] != null ? arr[32].toString() : null);
		obj.setDispatcherAddress1(arr[33] != null ? arr[33].toString() : null);
		obj.setDispatcherAddress2(arr[34] != null ? arr[34].toString() : null);
		obj.setDispatcherLocation(arr[35] != null ? arr[35].toString() : null);
		obj.setDispatcherPincode(arr[36] != null ? arr[36].toString() : null);
		obj.setDispatcherStateCode(arr[37] != null ? arr[37].toString() : null);
		obj.setShipToGSTIN(arr[38] != null ? arr[38].toString() : null);
		obj.setShipToTradeName(arr[39] != null ? arr[39].toString() : null);
		obj.setShipToLegalName(arr[40] != null ? arr[40].toString() : null);
		obj.setShipToAddress1(arr[41] != null ? arr[41].toString() : null);
		obj.setShipToAddress2(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToLocation(arr[43] != null ? arr[43].toString() : null);
		obj.setShipToPincode(arr[44] != null ? arr[44].toString() : null);
		obj.setShipToStateCode(arr[45] != null ? arr[45].toString() : null);
		obj.setItemSerialNumber(arr[46] != null ? arr[46].toString() : null);
		obj.setProductSerialNumber(arr[47] != null ? arr[47].toString() : null);
		obj.setProductName(arr[48] != null ? arr[48].toString() : null);
		obj.setProductDescription(arr[49] != null ? arr[49].toString() : null);
		obj.setIsService(arr[50] != null ? arr[50].toString() : null);
		obj.setHsn(arr[51] != null ? arr[51].toString() : null);
		obj.setBarcode(arr[52] != null ? arr[52].toString() : null);
		obj.setBatchName(arr[53] != null ? arr[53].toString() : null);
		obj.setBatchExpiryDate(arr[54] != null ? arr[54].toString() : null);
		obj.setWarrantyDate(arr[55] != null ? arr[55].toString() : null);
		obj.setOrderlineReference(arr[56] != null ? arr[56].toString() : null);
		obj.setAttributeName(arr[57] != null ? arr[57].toString() : null);
		obj.setAttributeValue(arr[58] != null ? arr[58].toString() : null);
		obj.setOriginCountry(arr[59] != null ? arr[59].toString() : null);
		obj.setuQC(arr[60] != null ? arr[60].toString() : null);
		obj.setQuantity(arr[61] != null ? arr[61].toString() : null);
		obj.setFreeQuantity(arr[62] != null ? arr[62].toString() : null);
		obj.setUnitPrice(arr[63] != null ? arr[63].toString() : null);
		obj.setItemAmount(arr[64] != null ? arr[64].toString() : null);
		obj.setItemDiscount(arr[65] != null ? arr[65].toString() : null);
		obj.setPreTaxAmount(arr[66] != null ? arr[66].toString() : null);
		obj.setItemAssessableAmount(
				arr[67] != null ? arr[67].toString() : null);
		obj.setiGSTRate(arr[68] != null ? arr[68].toString() : null);
		obj.setiGSTAmount(arr[69] != null ? arr[69].toString() : null);
		obj.setcGSTRate(arr[70] != null ? arr[70].toString() : null);
		obj.setcGSTAmount(arr[71] != null ? arr[71].toString() : null);
		obj.setsGSTRate(arr[72] != null ? arr[72].toString() : null);
		obj.setsGSTAmount(arr[73] != null ? arr[73].toString() : null);
		obj.setCessAdvaloremRate(arr[74] != null ? arr[74].toString() : null);
		obj.setCessAdvaloremAmount(arr[75] != null ? arr[75].toString() : null);
		obj.setCessSpecificRate(arr[76] != null ? arr[76].toString() : null);
		obj.setCessSpecificAmount(arr[77] != null ? arr[77].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[78] != null ? arr[78].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[79] != null ? arr[79].toString() : null);
		obj.setStateCessSpecificRate(
				arr[80] != null ? arr[80].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[81] != null ? arr[81].toString() : null);
		obj.setItemOtherCharges(arr[82] != null ? arr[82].toString() : null);
		obj.setTotalItemAmount(arr[83] != null ? arr[83].toString() : null);
		obj.setInvoiceOtherCharges(arr[84] != null ? arr[84].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[85] != null ? arr[85].toString() : null);
		obj.setInvoiceIGSTAmount(arr[86] != null ? arr[86].toString() : null);
		obj.setInvoiceCGSTAmount(arr[87] != null ? arr[87].toString() : null);
		obj.setInvoiceSGSTAmount(arr[88] != null ? arr[88].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[89] != null ? arr[89].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[90] != null ? arr[90].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[91] != null ? arr[91].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[92] != null ? arr[92].toString() : null);
		obj.setInvoiceValue(arr[93] != null ? arr[93].toString() : null);
		obj.setRoundOff(arr[94] != null ? arr[94].toString() : null);
		obj.setTotalInvoiceValue(arr[95] != null ? arr[95].toString() : null);
		obj.setEligibilityIndicator(
				arr[96] != null ? arr[96].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[97] != null ? arr[97].toString() : null);
		obj.setAvailableIgst(arr[98] != null ? arr[98].toString() : null);
		obj.setAvailableCgst(arr[99] != null ? arr[99].toString() : null);
		obj.setAvailableSgst(arr[100] != null ? arr[100].toString() : null);
		obj.setAvailableCess(arr[101] != null ? arr[101].toString() : null);
		obj.setiTCEntitlement(arr[102] != null ? arr[102].toString() : null);
		obj.setiTCReversalIdentifier(
				arr[103] != null ? arr[103].toString() : null);
		obj.settCSFlagIncomeTax(arr[104] != null ? arr[104].toString() : null);
		obj.settCSRateIncomeTax(arr[105] != null ? arr[105].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[106] != null ? arr[106].toString() : null);
		obj.setCurrencyCode(arr[107] != null ? arr[107].toString() : null);
		obj.setCountryCode(arr[108] != null ? arr[108].toString() : null);
		obj.setInvoiceValueFC(arr[109] != null ? arr[109].toString() : null);
		obj.setPortCode(arr[110] != null ? arr[110].toString() : null);
		obj.setBillofEntry(arr[111] != null ? arr[111].toString() : null);
		obj.setBillofEntryDate(arr[112] != null ? arr[112].toString() : null);
		obj.setInvoiceRemarks(arr[113] != null ? arr[113].toString() : null);
		obj.setInvoicePeriodStartDate(
				arr[114] != null ? arr[114].toString() : null);
		obj.setInvoicePeriodEndDate(
				arr[115] != null ? arr[115].toString() : null);
		obj.setPreceedingInvoiceNumber(
				arr[116] != null ? arr[116].toString() : null);
		obj.setPreceedingInvoiceDate(
				arr[117] != null ? arr[117].toString() : null);
		obj.setOtherReference(arr[118] != null ? arr[118].toString() : null);
		obj.setReceiptAdviceReference(
				arr[119] != null ? arr[119].toString() : null);
		obj.setReceiptAdviceDate(arr[120] != null ? arr[120].toString() : null);
		obj.setTenderReference(arr[121] != null ? arr[121].toString() : null);
		obj.setContractReference(arr[122] != null ? arr[122].toString() : null);
		obj.setExternalReference(arr[123] != null ? arr[123].toString() : null);
		obj.setProjectReference(arr[124] != null ? arr[124].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[125] != null ? arr[125].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[126] != null ? arr[126].toString() : null);
		obj.setPayeeName(arr[127] != null ? arr[127].toString() : null);
		obj.setModeOfPayment(arr[128] != null ? arr[128].toString() : null);
		obj.setBranchOrIFSCCode(arr[129] != null ? arr[129].toString() : null);
		obj.setPaymentTerms(arr[130] != null ? arr[130].toString() : null);
		obj.setPaymentInstruction(
				arr[131] != null ? arr[131].toString() : null);
		obj.setCreditTransfer(arr[132] != null ? arr[132].toString() : null);
		obj.setDirectDebit(arr[133] != null ? arr[133].toString() : null);
		obj.setCreditDays(arr[134] != null ? arr[134].toString() : null);
		obj.setPaidAmount(arr[135] != null ? arr[135].toString() : null);
		obj.setBalanceAmount(arr[136] != null ? arr[136].toString() : null);
		obj.setPaymentDueDate(arr[137] != null ? arr[137].toString() : null);
		obj.setAccountDetail(arr[138] != null ? arr[138].toString() : null);
		obj.setEcomGSTIN(arr[139] != null ? arr[139].toString() : null);
		obj.setSupportingDocURL(arr[140] != null ? arr[140].toString() : null);
		obj.setSupportingDocument(
				arr[141] != null ? arr[141].toString() : null);
		obj.setAdditionalInformation(
				arr[142] != null ? arr[142].toString() : null);
		obj.setTransactionType(arr[143] != null ? arr[143].toString() : null);
		obj.setSubSupplyType(arr[144] != null ? arr[144].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[145] != null ? arr[145].toString() : null);
		obj.setTransporterID(arr[146] != null ? arr[146].toString() : null);
		obj.setTransporterName(arr[147] != null ? arr[147].toString() : null);
		obj.setTransportMode(arr[148] != null ? arr[148].toString() : null);
		obj.setTransportDocNo(arr[149] != null ? arr[149].toString() : null);
		obj.setTransportDocDate(arr[150] != null ? arr[150].toString() : null);
		obj.setDistance(arr[151] != null ? arr[151].toString() : null);
		obj.setVehicleNo(arr[152] != null ? arr[152].toString() : null);
		obj.setVehicleType(arr[153] != null ? arr[153].toString() : null);
		obj.setReturnPeriod(
				arr[154] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[154].toString()) : null);
		obj.setOriginalDocumentType(
				arr[155] != null ? arr[155].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[156] != null ? arr[156].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[157] != null ? arr[157].toString() : null);
		obj.setSec7ofIGSTFlag(arr[158] != null ? arr[158].toString() : null);
		obj.setClaimRefndFlag(arr[159] != null ? arr[159].toString() : null);
		obj.setAutoPopltToRefund(arr[160] != null ? arr[160].toString() : null);
		obj.setcRDRPreGST(arr[161] != null ? arr[161].toString() : null);
		obj.setSupplierType(arr[162] != null ? arr[162].toString() : null);
		obj.setSupplierCode(arr[163] != null ? arr[163].toString() : null);
		obj.setProductCode(arr[164] != null ? arr[164].toString() : null);
		obj.setCategoryOfProduct(arr[165] != null ? arr[165].toString() : null);
		obj.setStateApplyingCess(arr[166] != null ? arr[166].toString() : null);
		obj.setCif(arr[167] != null ? arr[167].toString() : null);
		obj.setCustomDuty(arr[168] != null ? arr[168].toString() : null);
		obj.setExchangeRate(arr[169] != null ? arr[169].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[170] != null ? arr[170].toString() : null);
		obj.settCSFlagGST(arr[171] != null ? arr[171].toString() : null);
		obj.settCSIGSTAmount(arr[172] != null ? arr[172].toString() : null);
		obj.settCSCGSTAmount(arr[173] != null ? arr[173].toString() : null);
		obj.settCSSGSTAmount(arr[174] != null ? arr[174].toString() : null);
		obj.settDSFlagGST(arr[175] != null ? arr[175].toString() : null);
		obj.settDSIGSTAmount(arr[176] != null ? arr[176].toString() : null);
		obj.settDSCGSTAmount(arr[177] != null ? arr[177].toString() : null);
		obj.settDSSGSTAmount(arr[178] != null ? arr[178].toString() : null);
		obj.setUserID(arr[179] != null ? arr[179].toString() : null);
		obj.setCompanyCode(arr[180] != null ? arr[180].toString() : null);
		obj.setSourceIdentifier(arr[181] != null ? arr[181].toString() : null);
		obj.setSourceFileName(arr[182] != null ? arr[182].toString() : null);
		obj.setPlantCode(arr[183] != null ? arr[183].toString() : null);
		obj.setDivision(arr[184] != null ? arr[184].toString() : null);
		obj.setSubDivision(arr[185] != null ? arr[185].toString() : null);
		obj.setLocation(arr[186] != null ? arr[186].toString() : null);
		obj.setPurchaseOrganisation(
				arr[187] != null ? arr[187].toString() : null);
		obj.setProfitCentre1(arr[188] != null ? arr[188].toString() : null);
		obj.setProfitCentre2(arr[189] != null ? arr[189].toString() : null);
		obj.setProfitCentre3(arr[190] != null ? arr[190].toString() : null);
		obj.setProfitCentre4(arr[191] != null ? arr[191].toString() : null);
		obj.setProfitCentre5(arr[192] != null ? arr[192].toString() : null);
		obj.setProfitCentre6(arr[193] != null ? arr[193].toString() : null);
		obj.setProfitCentre7(arr[194] != null ? arr[194].toString() : null);
		obj.setProfitCentre8(arr[195] != null ? arr[195].toString() : null);
		obj.setGlAssessableValue(arr[196] != null ? arr[196].toString() : null);
		obj.setGlIGST(arr[197] != null ? arr[197].toString() : null);
		obj.setGlCGST(arr[198] != null ? arr[198].toString() : null);
		obj.setGlSGST(arr[199] != null ? arr[199].toString() : null);
		obj.setGlAdvaloremCess(arr[200] != null ? arr[200].toString() : null);
		obj.setGlSpecificCess(arr[201] != null ? arr[201].toString() : null);
		obj.setgLStateCessAdvalorem(
				arr[202] != null ? arr[202].toString() : null);
		obj.setgLStateCessSpecific(
				arr[203] != null ? arr[203].toString() : null);
		obj.setGlPostingDate(arr[204] != null ? arr[204].toString() : null);
		obj.setPurchaseOrderValue(
				arr[205] != null ? arr[205].toString() : null);
		obj.seteWBNumber(arr[206] != null ? arr[206].toString() : null);
		obj.seteWBDate(arr[207] != null ? arr[207].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[208] != null ? arr[208].toString() : null);
		obj.setAccountingVoucherDate(
				arr[209] != null ? arr[209].toString() : null);
		obj.setDocumentReferenceNumber(
				arr[210] != null ? arr[210].toString() : null);
		obj.setUserDefField1(arr[211] != null ? arr[211].toString() : null);
		obj.setUserDefField2(arr[212] != null ? arr[212].toString() : null);
		obj.setUserDefField3(arr[213] != null ? arr[213].toString() : null);
		obj.setUserDefField4(arr[214] != null ? arr[214].toString() : null);
		obj.setUserDefField5(arr[215] != null ? arr[215].toString() : null);
		obj.setUserDefField6(arr[216] != null ? arr[216].toString() : null);
		obj.setUserDefField7(arr[217] != null ? arr[217].toString() : null);
		obj.setUserDefField8(arr[218] != null ? arr[218].toString() : null);
		obj.setUserDefField9(arr[219] != null ? arr[219].toString() : null);
		obj.setUserDefField10(arr[220] != null ? arr[220].toString() : null);
		obj.setUserDefField11(arr[221] != null ? arr[221].toString() : null);
		obj.setUserDefField12(arr[222] != null ? arr[222].toString() : null);
		obj.setUserDefField13(arr[223] != null ? arr[223].toString() : null);
		obj.setUserDefField14(arr[224] != null ? arr[224].toString() : null);
		obj.setUserDefField15(arr[225] != null ? arr[225].toString() : null);
		obj.setUserDefField16(arr[226] != null ? arr[226].toString() : null);
		obj.setUserDefField17(arr[227] != null ? arr[227].toString() : null);
		obj.setUserDefField18(arr[228] != null ? arr[228].toString() : null);
		obj.setUserDefField19(arr[229] != null ? arr[229].toString() : null);
		obj.setUserDefField20(arr[230] != null ? arr[230].toString() : null);
		obj.setUserDefField21(arr[231] != null ? arr[231].toString() : null);
		obj.setUserDefField22(arr[232] != null ? arr[232].toString() : null);
		obj.setUserDefField23(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField24(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField25(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField26(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField27(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField28(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField29(arr[239] != null ? arr[239].toString() : null);
		obj.setUserDefField30(arr[240] != null ? arr[240].toString() : null);
		obj.setItcAvailableIGST(arr[241] != null ? arr[241].toString() : null);
		obj.setItcAvailableCGST(arr[242] != null ? arr[242].toString() : null);
		obj.setItcAvailableSGST(arr[243] != null ? arr[243].toString() : null);
		obj.setItcAvailableCess(arr[244] != null ? arr[244].toString() : null);

		BigDecimal bigDecimalRatio1IGST = (BigDecimal) arr[245];
		if (bigDecimalRatio1IGST != null) {
			obj.setRatio1IGST(
					bigDecimalRatio1IGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio1CGST = (BigDecimal) arr[246];
		if (bigDecimalRatio1CGST != null) {
			obj.setRatio1CGST(
					bigDecimalRatio1CGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio1SGST = (BigDecimal) arr[247];
		if (bigDecimalRatio1SGST != null) {
			obj.setRatio1SGST(
					bigDecimalRatio1SGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalRatio1CESS = (BigDecimal) arr[248];
		if (bigDecimalRatio1CESS != null) {
			obj.setRatio1Cess(
					bigDecimalRatio1CESS.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2IGST = (BigDecimal) arr[249];
		if (bigDecimalRatio2IGST != null) {
			obj.setRatio2IGST(
					bigDecimalRatio2IGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2CGST = (BigDecimal) arr[250];
		if (bigDecimalRatio2CGST != null) {
			obj.setRatio2CGST(
					bigDecimalRatio2CGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2SGST = (BigDecimal) arr[251];
		if (bigDecimalRatio2SGST != null) {
			obj.setRatio2SGST(
					bigDecimalRatio2SGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio2CESS = (BigDecimal) arr[252];
		if (bigDecimalRatio2CESS != null) {
			obj.setRatio2Cess(
					bigDecimalRatio2CESS.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio3IGST = (BigDecimal) arr[253];
		if (bigDecimalRatio3IGST != null) {
			obj.setRatio3IGST(
					bigDecimalRatio3IGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio3CGST = (BigDecimal) arr[254];
		if (bigDecimalRatio3CGST != null) {
			obj.setRatio3CGST(
					bigDecimalRatio3CGST.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		BigDecimal bigDecimalRatio3SGST = (BigDecimal) arr[255];
		if (bigDecimalRatio3SGST != null) {
			obj.setRatio3SGST(
					bigDecimalRatio3SGST.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		BigDecimal bigDecimalRatio3CESS = (BigDecimal) arr[256];
		if (bigDecimalRatio3CESS != null) {
			obj.setRatio3Cess(
					bigDecimalRatio3CESS.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		return obj;
	}

	private String createInwardApiProcessedRecQueryString(String buildQuery) {
		return "select CASE WHEN ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER = 'T1' THEN 'T1: Used other than business' "
				+ "WHEN ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND ITM.ITC_REVERSAL_IDENTIFER = 'T2' "
				+ "THEN 'T2: Used exempt supplies ' WHEN ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND ITM.ITC_REVERSAL_IDENTIFER = 'T3' "
				+ "THEN 'T3: Credit 17 (5)' WHEN ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND ITM.ITC_REVERSAL_IDENTIFER = 'T4' "
				+ "THEN 'T4: Used exclusively for business' "
				+ "WHEN ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL THEN 'Common Credit - C2' end as CATEGORY, "
				+ "HDR.IRN,TO_CHAR(HDR.IRN_DATE,'DD-MM-YYYY') IRN_DATE,"
				+ "TAX_SCHEME,HDR.SUPPLY_TYPE,ITM.DOC_CATEGORY,HDR.DOC_TYPE,"
				+ "HDR.DOC_NUM,HDR.DOC_DATE,ITM.REVERSE_CHARGE,HDR.SUPPLIER_GSTIN,"
				+ "ITM.SUPP_TRADE_NAME,ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_ADDRESS1,"
				+ "ITM.CUST_SUPP_ADDRESS2,ITM.CUST_SUPP_ADDRESS3,ITM.CUST_SUPP_ADDRESS4,"
				+ "ITM.SUPP_STATE_CODE,ITM.SUPP_PHONE,ITM.SUPP_EMAIL,HDR.CUST_GSTIN,"
				+ "ITM.CUST_TRADE_NAME,ITM.CUST_LEGAL_NAME,ITM.CUST_BUILDING_NUM ,"
				+ "ITM.CUST_BUILDING_NAME,ITM.CUST_LOCATION,ITM.CUST_PINCODE,"
				+ "ITM.BILL_TO_STATE,ITM.POS,ITM.CUST_PHONE,ITM.CUST_EMAIL,"
				+ "HDR.DISPATCHER_GSTIN,ITM.DISPATCHER_TRADE_NAME,DISPATCHER_BUILDING_NUM,"
				+ "DISPATCHER_BUILDING_NAME,DISPATCHER_LOCATION,DISPATCHER_PINCODE,"
				+ "DISPATCHER_STATE_CODE,HDR.SHIP_TO_GSTIN,SHIP_TO_TRADE_NAME,"
				+ "SHIP_TO_LEGAL_NAME,SHIP_TO_BUILDING_NUM,SHIP_TO_BUILDING_NAME,"
				+ "SHIP_TO_LOCATION,SHIP_TO_PINCODE,SHIP_TO_STATE,ITM_NO,SERIAL_NUM2,"
				+ "PRODUCT_NAME,ITM_DESCRIPTION,IS_SERVICE,ITM_HSNSAC,BAR_CODE,"
				+ "BATCH_NAME_OR_NUM,BATCH_EXPIRY_DATE,WARRANTY_DATE,"
				+ "ORDER_ITEM_REFERENCE,ATTRIBUTE_NAME,ATTRIBUTE_VALUE,"
				+ "ORIGIN_COUNTRY,ITM_UQC,ITM_QTY,FREE_QTY,UNIT_PRICE,ITEM_AMT_UP_QTY,"
				+ "ITEM_DISCOUNT,PRE_TAX_AMOUNT,ITM.TAXABLE_VALUE,ITM.IGST_RATE,"
				+ "ITM.IGST_AMT,ITM.CGST_RATE,ITM.CGST_AMT,ITM.SGST_RATE,ITM.SGST_AMT,"
				+ "ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM,ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.STATECESS_RATE,ITM.STATECESS_AMT,"
				+ "ITM.STATE_CESS_SPECIFIC_RATE,ITM.STATE_CESS_SPECIFIC_AMOUNT,"
				+ "ITM.OTHER_VALUES,TOT_ITEM_AMT,ITM.INV_OTHER_CHARGES,"
				+ "ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT,ITM.INV_CGST_AMT,"
				+ "ITM.INV_SGST_AMT,ITM.INV_CESS_ADVLRM_AMT,ITM.INV_CESS_SPECIFIC_AMT,"
				+ "ITM.INV_STATE_CESS_AMT,ITM.INV_STATE_CESS_SPECIFIC_AMOUNT,"
				+ "ITM.LINE_ITEM_AMT,ITM.ROUND_OFF,ITM.TOT_INV_VAL_WORDS,"
				+ "ELIGIBILITY_INDICATOR,COMMON_SUP_INDICATOR,"
				+ "ITM.AVAILABLE_IGST,ITM.AVAILABLE_CGST,ITM.AVAILABLE_SGST,"
				+ "ITM.AVAILABLE_CESS,ITM.ITC_ENTITLEMENT,ITM.ITC_REVERSAL_IDENTIFER,"
				+ "ITM.TCS_FLAG_INCOME_TAX,TCS_RATE_INCOME_TAX,TCS_AMOUNT_INCOME_TAX,"
				+ "FOREIGN_CURRENCY,COUNTRY_CODE,INV_VAL_FC,ITM.SHIP_PORT_CODE,"
				+ "ITM.BILL_OF_ENTRY,ITM.BILL_OF_ENTRY_DATE,INV_REMARKS,INV_PERIOD_START_DATE,"
				+ "INV_PERIOD_END_DATE,ITM.ORIGINAL_DOC_NUM,ITM.ORIGINAL_DOC_DATE,"
				+ "ITM.INV_REFERENCE,RECEIPT_ADVICE_REFERENCE,RECEIPT_ADVICE_DATE,"
				+ "TENDER_REFERENCE,CONTRACT_REFERENCE,EXTERNAL_REFERENCE,"
				+ "PROJECT_REFERENCE,CONTRACT_NUMBER,CONTRACT_DATE,PAYEE_NAME,"
				+ "MODE_OF_PAYMENT,BRANCH_IFSC_CODE,PAYMENT_TERMS,PAYMENT_INSTRUCTION,"
				+ "CR_TRANSFER,DB_DIRECT,CR_DAYS,PAID_AMT,BAL_AMT,"
				+ "ITM.PAYMENT_DUE_DATE,ACCOUNT_DETAIL,ITM.ECOM_GSTIN,"
				+ "SUPPORTING_DOC_URL,SUPPORTING_DOC,ADDITIONAL_INFORMATION,"
				+ "TRANS_TYPE,SUB_SUPP_TYPE,OTHER_SUPP_TYPE_DESC,TRANSPORTER_ID,"
				+ "TRANSPORTER_NAME,TRANSPORT_MODE,TRANSPORT_DOC_NUM,TRANSPORT_DOC_DATE,"
				+ "DISTANCE,VEHICLE_NUM,VEHICLE_TYPE,HDR.RETURN_PERIOD,"
				+ "ITM.ORIGINAL_DOC_TYPE,ITM.ORIG_SUPPLIER_GSTIN,ITM.DIFF_PERCENT,"
				+ "ITM.SECTION7_OF_IGST_FLAG,ITM.CLAIM_REFUND_FLAG,"
				+ "ITM.AUTOPOPULATE_TO_REFUND,ITM.CRDR_PRE_GST,ITM.CUST_SUPP_TYPE,"
				+ "ITM.CUST_SUPP_CODE,ITM.PRODUCT_CODE,ITM.ITM_TYPE,"
				+ "ITM.STATE_APPLYING_CESS,ITM.CIF_VALUE,CUSTOM_DUTY,"
				+ "EXCHANGE_RATE,CRDR_REASON,ITM.TCS_FLAG,ITM.TCS_IGST_AMT,"
				+ "ITM.TCS_CGST_AMT,ITM.TCS_SGST_AMT,ITM.TDS_FLAG,ITM.TDS_IGST_AMT,"
				+ "ITM.TDS_CGST_AMT,ITM.TDS_SGST_AMT,ITM.USER_ID,ITM.COMPANY_CODE,"
				+ "ITM.SOURCE_IDENTIFIER,ITM.SOURCE_FILENAME,ITM.PLANT_CODE,"
				+ "ITM.DIVISION,ITM.SUB_DIVISION,ITM.LOCATION,ITM.PURCHASE_ORGANIZATION,"
				+ "ITM.PROFIT_CENTRE,ITM.PROFIT_CENTRE2,ITM.USERACCESS1,"
				+ "ITM.USERACCESS2,ITM.USERACCESS3,ITM.USERACCESS4,ITM.USERACCESS5,"
				+ "ITM.USERACCESS6,GLCODE_TAXABLEVALUE,GLCODE_IGST,GLCODE_CGST,"
				+ "GLCODE_SGST,GLCODE_ADV_CESS,GLCODE_SP_CESS,GLCODE_STATE_CESS,"
				+ "GL_STATE_CESS_SPECIFIC,ITM.POSTING_DATE,CONTRACT_VALUE,"
				+ "ITM.EWAY_BILL_NUM,ITM.EWAY_BILL_DATE,ITM.PURCHASE_VOUCHER_NUM,"
				+ "ITM.PURCHASE_VOUCHER_DATE,ITM.DOCUMENT_REFERENCE_NUMBER,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
				+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,ITM.USERDEFINED_FIELD7,"
				+ "ITM.USERDEFINED_FIELD8,ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
				+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,ITM.USERDEFINED_FIELD13,"
				+ "ITM.USERDEFINED_FIELD14,ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16,"
				+ "ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18,ITM.USERDEFINED_FIELD19,"
				+ "ITM.USERDEFINED_FIELD20,ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22,"
				+ "ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24,"
				+ "ITM.USERDEFINED_FIELD25,ITM.USERDEFINED_FIELD26,"
				+ "ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28,"
				+ "ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30,"
				+ "CASE WHEN ECP.ANSWER in ('A','C') THEN "
				+ "ITM.AVAILABLE_IGST WHEN ECP.ANSWER = 'B' "
				+ "THEN ITM.IGST_AMT END AS AVAILABLE_IGST_ITC , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "THEN ITM.AVAILABLE_CGST WHEN ECP.ANSWER = 'B' "
				+ "THEN ITM.CGST_AMT END AS AVAILABLE_CGST_ITC , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "THEN ITM.AVAILABLE_SGST WHEN ECP.ANSWER = 'B' "
				+ "THEN ITM.SGST_AMT END AS AVAILABLE_SGST_ITC , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "THEN ITM.AVAILABLE_CESS WHEN ECP.ANSWER = 'B' "
				+ "THEN IFNULL(ITM.CESS_AMT_ADVALOREM,0) "
				+ " + IFNULL(ITM.CESS_AMT_SPECIFIC,0) END "
				+ "AS AVAILABLE_CESS_ITC , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_IGST * G42.RATIO_1)/100 "
				+ "WHEN ECP.ANSWER = 'B' "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.IGST_AMT * G42.RATIO_1) /100 else null "
				+ "END AS AVAILABLE_IGST_RATIO_1 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_CGST * G42.RATIO_1)/100 "
				+ "WHEN ECP.ANSWER = 'B' "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.CGST_AMT * G42.RATIO_1) /100 else null "
				+ "END AS AVAILABLE_CGST_RATIO_1 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_SGST * G42.RATIO_1)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.SGST_AMT * G42.RATIO_1) /100 else "
				+ "null END AS AVAILABLE_SGST_RATIO_1 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_CESS * G42.RATIO_1)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN ( IFNULL(ITM.CESS_AMT_ADVALOREM,0) "
				+ " + IFNULL(ITM.CESS_AMT_SPECIFIC,0) * G42.RATIO_1)/100 "
				+ "else null END AS AVAILABLE_CESS_RATIO_1 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_IGST * G42.RATIO_2)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.IGST_AMT * G42.RATIO_2) /100 "
				+ "else null END AS AVAILABLE_IGST_RATIO_2 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_CGST * G42.RATIO_2)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.CGST_AMT * G42.RATIO_2) /100 else null "
				+ "END AS AVAILABLE_CGST_RATIO_2 , CASE "
				+ "WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_SGST * G42.RATIO_2)/100 "
				+ "WHEN ECP.ANSWER = 'B' and ITM.ELIGIBILITY_INDICATOR "
				+ "in('IS','IG') AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.SGST_AMT * G42.RATIO_2) /100 else null END "
				+ "AS AVAILABLE_SGST_RATIO_2 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_CESS * G42.RATIO_2)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN ( IFNULL(ITM.CESS_AMT_ADVALOREM,0) "
				+ " + IFNULL(ITM.CESS_AMT_SPECIFIC,0) * G42.RATIO_2)/100 "
				+ "else null END AS AVAILABLE_CESS_RATIO_2 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_IGST * G42.RATIO_3)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.IGST_AMT * G42.RATIO_3) /100 else null "
				+ "END AS AVAILABLE_IGST_RATIO_3 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_CGST * G42.RATIO_3)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.CGST_AMT * G42.RATIO_3) /100 else null "
				+ "END AS AVAILABLE_CGST_RATIO_3 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_SGST * G42.RATIO_3)/100 "
				+ "WHEN ECP.ANSWER = 'B' "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.SGST_AMT * G42.RATIO_3) /100 else null "
				+ "END AS AVAILABLE_SGST_RATIO_3 , "
				+ "CASE WHEN ECP.ANSWER in ('A','C') "
				+ "and ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' "
				+ "AND ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN (ITM.AVAILABLE_CESS * G42.RATIO_3)/100 "
				+ "WHEN ECP.ANSWER = 'B' and "
				+ "ITM.ELIGIBILITY_INDICATOR in('IS','IG') "
				+ "AND ITM.COMMON_SUP_INDICATOR = 'Y' AND "
				+ "ITM.ITC_REVERSAL_IDENTIFER IS NULL "
				+ "THEN ( IFNULL(ITM.CESS_AMT_ADVALOREM,0) "
				+ " + IFNULL(ITM.CESS_AMT_SPECIFIC,0) * G42.RATIO_3)/100 "
				+ "else null END AS AVAILABLE_CESS_RATIO_3 "
				+ " FROM ANX_INWARD_DOC_HEADER "
				+ "HDR INNER JOIN ANX_INWARD_DOC_ITEM ITM "
				+ "ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "AND HDR.IS_DELETE = FALSE AND HDR.IS_PROCESSED = TRUE "
				+ "AND HDR.RETURN_TYPE = 'GSTR2' INNER JOIN "
				+ "GSTR3B_RULE42_COMPUTE G42 ON HDR.CUST_GSTIN = G42.GSTIN "
				+ "AND G42.IS_ACTIVE = TRUE "
				+ "AND G42.DERIVED_RET_PERIOD = HDR.DERIVED_RET_PERIOD "
				+ "AND SECTION_NAME IN ('ITC Reversal Ratio') INNER JOIN "
				+ "GSTIN_INFO GIF ON GIF.GSTIN = HDR.CUST_GSTIN INNER JOIN "
				+ "ENTITY_CONFG_PRMTR ECP ON GIF.ENTITY_ID = ECP.ENTITY_ID "
				+ "AND ECP. IS_DELETE = FALSE AND QUESTION_CODE = 'I15' "
				+ buildQuery;

	}
}
