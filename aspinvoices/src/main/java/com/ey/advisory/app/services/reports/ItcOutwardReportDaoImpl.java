/**
 * 
 */
package com.ey.advisory.app.services.reports;

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

import com.ey.advisory.app.data.views.client.ItcOutwardDto;
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

@Component("ItcOutwardReportDaoImpl")
public class ItcOutwardReportDaoImpl implements ItcReversalInwardDao {

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
		String sales = null;
		String division = null;
		String location = null;
		String distChannel = null;
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
		List<String> salesList = null;
		List<String> distList = null;
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
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
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

		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" WHERE HDR.SUPPLIER_GSTIN IN :gstinList");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND HDR.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND HDR.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND HDR.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader
						.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND HDR.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND ITM.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {

			buildHeader.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
			buildHeader.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
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
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && !salesList.isEmpty()
					&& salesList.size() > 0) {
				q.setParameter("salesList", salesList);
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
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && !distList.isEmpty()
					&& distList.size() > 0) {
				q.setParameter("distList", distList);
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

		List<Object[]> list = q.getResultList();
		return list.parallelStream()
				.map(o -> convertApiInwardProcessedRecords(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ItcOutwardDto convertApiInwardProcessedRecords(Object[] arr) {
		ItcOutwardDto obj = new ItcOutwardDto();

		obj.setCategory(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnType(arr[1] != null ? arr[1].toString() : null);
		obj.setTableNumber(arr[2] != null ? arr[2].toString() : null);
		obj.setIrn(arr[3] != null ? arr[3].toString() : null);
		obj.setIrnDate(arr[4] != null ? arr[4].toString() : null);
		obj.setTaxScheme(arr[5] != null ? arr[5].toString() : null);
		obj.setCancellationReason(arr[6] != null ? arr[6].toString() : null);
		obj.setCancellationRemarks(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplyType(arr[8] != null ? arr[8].toString() : null);
		obj.setDocCategory(arr[9] != null ? arr[9].toString() : null);
		obj.setDocumentType(arr[10] != null ? arr[10].toString() : null);
		obj.setDocumentNumber(
				arr[11] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[11].toString()) : null);
		if (arr[12] != null) {
			String strdate = arr[12].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		obj.setReverseChargeFlag(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierGSTIN(arr[14] != null ? arr[14].toString() : null);
		obj.setSupplierTradeName(arr[15] != null ? arr[15].toString() : null);
		obj.setSupplierLegalName(arr[16] != null ? arr[16].toString() : null);
		obj.setSupplierAddress1(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplierAddress2(arr[18] != null ? arr[18].toString() : null);
		obj.setSupplierLocation(arr[19] != null ? arr[19].toString() : null);
		obj.setSupplierPincode(arr[20] != null ? arr[20].toString() : null);
		obj.setSupplierStateCode(
				arr[21] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[21].toString()) : null);
		obj.setSupplierPhone(arr[22] != null ? arr[22].toString() : null);
		obj.setSupplierEmail(arr[23] != null ? arr[23].toString() : null);
		obj.setCustomerGSTIN(arr[24] != null ? arr[24].toString() : null);
		obj.setCustomerTradeName(arr[25] != null ? arr[25].toString() : null);
		obj.setCustomerLegalName(arr[26] != null ? arr[26].toString() : null);
		obj.setCustomerAddress1(arr[27] != null ? arr[27].toString() : null);
		obj.setCustomerAddress2(arr[28] != null ? arr[28].toString() : null);
		obj.setCustomerLocation(arr[29] != null ? arr[29].toString() : null);
		obj.setCustomerPincode(arr[30] != null ? arr[30].toString() : null);
		obj.setCustomerStateCode(
				arr[31] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[31].toString()) : null);
		obj.setBillingPOS(arr[32] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[32].toString()) : null);
		obj.setCustomerPhone(arr[33] != null ? arr[33].toString() : null);
		obj.setCustomerEmail(arr[34] != null ? arr[34].toString() : null);
		obj.setDispatcherGSTIN(arr[35] != null ? arr[35].toString() : null);
		obj.setDispatcherTradeName(arr[36] != null ? arr[36].toString() : null);
		obj.setDispatcherAddress1(arr[37] != null ? arr[37].toString() : null);
		obj.setDispatcherAddress2(arr[38] != null ? arr[38].toString() : null);
		obj.setDispatcherLocation(arr[39] != null ? arr[39].toString() : null);
		obj.setDispatcherPincode(arr[40] != null ? arr[40].toString() : null);
		obj.setDispatcherStateCode(
				arr[41] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[41].toString()) : null);
		obj.setShipToGSTIN(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToTradeName(arr[43] != null ? arr[43].toString() : null);
		obj.setShipToLegalName(arr[44] != null ? arr[44].toString() : null);
		obj.setShipToAddress1(arr[45] != null ? arr[45].toString() : null);
		obj.setShipToAddress2(arr[46] != null ? arr[46].toString() : null);
		obj.setShipToLocation(arr[47] != null ? arr[47].toString() : null);
		obj.setShipToPincode(arr[48] != null ? arr[48].toString() : null);

		obj.setShipToStateCode(
				arr[49] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[49].toString()) : null);

		/*
		 * obj.setShipToStateCode( arr[301] != null ?
		 * DownloadReportsConstant.CSVCHARACTER .concat(arr[301].toString()) :
		 * null);
		 */
		obj.setItemSerialNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setProductSerialNumber(arr[51] != null ? arr[51].toString() : null);
		obj.setProductName(arr[52] != null ? arr[52].toString() : null);
		obj.setProductDescription(arr[53] != null ? arr[53].toString() : null);
		obj.setIsService(arr[54] != null ? arr[54].toString() : null);
		// obj.setHsn(arr[77] != null ? arr[77].toString() : null);
		obj.setHsn(arr[55] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[55].toString()) : null);
		obj.setBarcode(arr[56] != null ? arr[56].toString() : null);
		obj.setBatchName(arr[57] != null ? arr[57].toString() : null);
		if (arr[58] != null) {
			String strdate = arr[58].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {
			obj.setBatchExpiryDate(null);
		}
		if (arr[59] != null) {
			String strdate = arr[59].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(newDate);
		} else {
			obj.setWarrantyDate(null);
		}
		obj.setOrderlineReference(arr[60] != null ? arr[60].toString() : null);
		obj.setAttributeName(arr[61] != null ? arr[61].toString() : null);
		obj.setAttributeValue(arr[62] != null ? arr[62].toString() : null);
		obj.setOriginCountry(arr[63] != null ? arr[63].toString() : null);
		obj.setuQC(arr[64] != null ? arr[64].toString() : null);
		obj.setQuantity(arr[65] != null ? arr[65].toString() : null);
		obj.setFreeQuantity(arr[66] != null ? arr[66].toString() : null);
		obj.setUnitPrice(arr[67] != null ? arr[67].toString() : null);
		obj.setItemAmount(arr[68] != null ? arr[68].toString() : null);
		obj.setItemDiscount(arr[69] != null ? arr[69].toString() : null);
		obj.setPreTaxAmount(arr[70] != null ? arr[70].toString() : null);
		obj.setItemAssessableAmount(
				arr[71] != null ? arr[71].toString() : null);
		obj.setiGSTRate(arr[72] != null ? arr[72].toString() : null);
		obj.setiGSTAmount(arr[73] != null ? arr[73].toString() : null);
		obj.setcGSTRate(arr[74] != null ? arr[74].toString() : null);
		obj.setcGSTAmount(arr[75] != null ? arr[75].toString() : null);
		obj.setsGSTRate(arr[76] != null ? arr[76].toString() : null);
		obj.setsGSTAmount(arr[77] != null ? arr[77].toString() : null);
		obj.setCessAdvaloremRate(arr[78] != null ? arr[78].toString() : null);
		obj.setCessAdvaloremAmount(arr[79] != null ? arr[79].toString() : null);
		obj.setCessSpecificRate(arr[80] != null ? arr[80].toString() : null);
		obj.setCessSpecificAmount(arr[81] != null ? arr[81].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[82] != null ? arr[82].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[83] != null ? arr[83].toString() : null);
		obj.setStateCessSpecificRate(
				arr[84] != null ? arr[84].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[85] != null ? arr[85].toString() : null);
		obj.setItemOtherCharges(arr[86] != null ? arr[86].toString() : null);
		obj.setTotalItemAmount(arr[87] != null ? arr[87].toString() : null);
		obj.setInvoiceOtherCharges(arr[88] != null ? arr[88].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[89] != null ? arr[89].toString() : null);
		obj.setInvoiceIGSTAmount(arr[90] != null ? arr[90].toString() : null);
		obj.setInvoiceCGSTAmount(arr[91] != null ? arr[91].toString() : null);
		obj.setInvoiceSGSTAmount(arr[92] != null ? arr[92].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[93] != null ? arr[93].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[94] != null ? arr[94].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[95] != null ? arr[95].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[96] != null ? arr[96].toString() : null);
		obj.setInvoiceValue(arr[97] != null ? arr[97].toString() : null);
		obj.setRoundOff(arr[98] != null ? arr[98].toString() : null);
		obj.setTotalInvoiceValue(arr[99] != null ? arr[99].toString() : null);
		obj.settCSFlagIncomeTax(arr[100] != null ? arr[100].toString() : null);
		obj.settCSRateIncomeTax(arr[101] != null ? arr[101].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[102] != null ? arr[102].toString() : null);
		obj.setCustomerPANOrAadhaar(
				arr[103] != null ? arr[103].toString() : null);
		obj.setCurrencyCode(arr[104] != null ? arr[104].toString() : null);
		obj.setCountryCode(arr[105] != null ? arr[105].toString() : null);
		obj.setInvoiceValueFC(arr[106] != null ? arr[106].toString() : null);
		obj.setPortCode(arr[107] != null ? arr[107].toString() : null);
		obj.setShippingBillNumber(
				arr[108] != null ? arr[108].toString() : null);
		if (arr[109] != null) {
			String strdate = arr[109].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {
			obj.setShippingBillDate(null);
		}
		obj.setInvoiceRemarks(arr[110] != null ? arr[110].toString() : null);
		if (arr[111] != null) {
			String strdate = arr[111].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(newDate);
		} else {
			obj.setInvoicePeriodStartDate(null);
		}
		if (arr[112] != null) {
			String strdate = arr[112].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodEndDate(newDate);
		} else {
			obj.setInvoicePeriodEndDate(null);
		}
		obj.setPreceedingInvoiceNumber(
				arr[113] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[113].toString()) : null);
		if (arr[114] != null) {
			String strdate = arr[114].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPreceedingInvoiceDate(newDate);
		} else {
			obj.setPreceedingInvoiceDate(null);
		}
		obj.setOtherReference(arr[115] != null ? arr[115].toString() : null);
		obj.setReceiptAdviceReference(
				arr[116] != null ? arr[116].toString() : null);
		obj.setReceiptAdviceDate(arr[117] != null ? arr[117].toString() : null);
		obj.setTenderReference(arr[118] != null ? arr[118].toString() : null);
		obj.setContractReference(arr[119] != null ? arr[119].toString() : null);
		obj.setExternalReference(arr[120] != null ? arr[120].toString() : null);
		obj.setProjectReference(arr[121] != null ? arr[121].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[122] != null ? arr[122].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[123] != null ? arr[123].toString() : null);
		obj.setPayeeName(arr[124] != null ? arr[124].toString() : null);
		obj.setModeOfPayment(arr[125] != null ? arr[125].toString() : null);
		obj.setBranchOrIFSCCode(arr[126] != null ? arr[126].toString() : null);
		obj.setPaymentTerms(arr[127] != null ? arr[127].toString() : null);
		obj.setPaymentInstruction(
				arr[128] != null ? arr[128].toString() : null);
		obj.setCreditTransfer(arr[129] != null ? arr[129].toString() : null);
		obj.setDirectDebit(arr[130] != null ? arr[130].toString() : null);
		obj.setCreditDays(arr[131] != null ? arr[131].toString() : null);
		obj.setPaidAmount(arr[132] != null ? arr[132].toString() : null);
		obj.setBalanceAmount(arr[133] != null ? arr[133].toString() : null);
		if (arr[134] != null) {
			String strdate = arr[134].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDueDate(newDate);
		} else {
			obj.setPaymentDueDate(null);
		}
		obj.setAccountDetail(arr[135] != null ? arr[135].toString() : null);
		obj.setEcomGSTIN(arr[136] != null ? arr[136].toString() : null);
		obj.setEcomTransactionID(arr[137] != null ? arr[137].toString() : null);
		obj.setSupportingDocURL(arr[138] != null ? arr[138].toString() : null);
		obj.setSupportingDocument(
				arr[139] != null ? arr[139].toString() : null);
		obj.setAdditionalInformation(
				arr[140] != null ? arr[140].toString() : null);
		obj.setTransactionType(arr[141] != null ? arr[141].toString() : null);
		obj.setSubSupplyType(arr[142] != null ? arr[142].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[143] != null ? arr[143].toString() : null);
		obj.setTransporterID(arr[144] != null ? arr[144].toString() : null);
		obj.setTransporterName(arr[145] != null ? arr[145].toString() : null);
		obj.setTransportMode(arr[146] != null ? arr[146].toString() : null);
		obj.setTransportDocNo(arr[147] != null ? arr[147].toString() : null);
		if (arr[148] != null) {
			String strdate = arr[148].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(newDate);
		} else {
			obj.setTransportDocDate(null);
		}
		obj.setDistance(arr[149] != null ? arr[149].toString() : null);
		obj.setVehicleNo(arr[150] != null ? arr[150].toString() : null);
		obj.setVehicleType(arr[151] != null ? arr[151].toString() : null);
		obj.setReturnPeriod(
				arr[152] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[152].toString()) : null);
		obj.setOriginalDocumentType(
				arr[153] != null ? arr[153].toString() : null);
		obj.setOriginalCustomerGSTIN(
				arr[154] != null ? arr[154].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[155] != null ? arr[155].toString() : null);
		obj.setSec7ofIGSTFlag(arr[156] != null ? arr[156].toString() : null);
		obj.setClaimRefndFlag(arr[157] != null ? arr[157].toString() : null);
		obj.setAutoPopltToRefund(arr[158] != null ? arr[158].toString() : null);
		obj.setcRDRPreGST(arr[159] != null ? arr[159].toString() : null);
		obj.setCustomerType(arr[160] != null ? arr[160].toString() : null);
		obj.setCustomerCode(arr[161] != null ? arr[161].toString() : null);
		obj.setProductCode(arr[162] != null ? arr[162].toString() : null);
		obj.setCategoryOfProduct(arr[163] != null ? arr[163].toString() : null);
		obj.setiTCFlag(arr[164] != null ? arr[164].toString() : null);
		obj.setStateApplyingCess(arr[165] != null ? arr[165].toString() : null);
		obj.setfOB(arr[166] != null ? arr[166].toString() : null);
		obj.setExportDuty(arr[167] != null ? arr[167].toString() : null);
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
		obj.setUserId(arr[178] != null ? arr[178].toString() : null);
		obj.setCompanyCode(arr[179] != null ? arr[179].toString() : null);
		obj.setSourceIdentifier(arr[180] != null ? arr[180].toString() : null);
		obj.setSourceFileName(arr[181] != null ? arr[181].toString() : null);
		obj.setPlantCode(arr[182] != null ? arr[182].toString() : null);
		obj.setDivision(arr[183] != null ? arr[183].toString() : null);
		obj.setSubDivision(arr[184] != null ? arr[184].toString() : null);
		obj.setLocation(arr[185] != null ? arr[185].toString() : null);
		obj.setSalesOrganisation(arr[186] != null ? arr[186].toString() : null);
		obj.setDistributionChannel(
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
		// obj.setGlPostingDate(arr[248] != null ? arr[248].toString() :
		// null);
		if (arr[204] != null) {
			String strdate = arr[204].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(newDate);
		} else {
			obj.setGlPostingDate(null);
		}
		obj.setSalesOrderNumber(arr[205] != null ? arr[205].toString() : null);
		obj.seteWBNumber(arr[206] != null ? arr[206].toString() : null);
		//obj.seteWBDate(arr[207] != null ? arr[207].toString() : null);
		if (arr[207] != null) {
			String strdate = arr[207].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.seteWBDate(newDate);
		} else {
			obj.seteWBDate(null);
		}
		obj.setAccountingVoucherNumber(
				arr[208] != null ? arr[208].toString() : null);
		if (arr[209] != null) {
			String strdate = arr[209].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccountingVoucherDate(newDate);
		} else {
			obj.setAccountingVoucherDate(null);
		}
		obj.setDocumentReferenceNumber(
				arr[210] != null ? arr[210].toString() : null);
		obj.setCustomerTAN(arr[211] != null ? arr[211].toString() : null);
		obj.setUserDefField1(arr[212] != null ? arr[212].toString() : null);
		obj.setUserDefField2(arr[213] != null ? arr[213].toString() : null);
		obj.setUserDefField3(arr[214] != null ? arr[214].toString() : null);
		obj.setUserDefField4(arr[215] != null ? arr[215].toString() : null);
		obj.setUserDefField5(arr[216] != null ? arr[216].toString() : null);
		obj.setUserDefField6(arr[217] != null ? arr[217].toString() : null);
		obj.setUserDefField7(arr[218] != null ? arr[218].toString() : null);
		obj.setUserDefField8(arr[219] != null ? arr[219].toString() : null);
		obj.setUserDefField9(arr[220] != null ? arr[220].toString() : null);
		obj.setUserDefField10(arr[221] != null ? arr[221].toString() : null);
		obj.setUserDefField11(arr[222] != null ? arr[222].toString() : null);
		obj.setUserDefField12(arr[223] != null ? arr[223].toString() : null);
		obj.setUserDefField13(arr[224] != null ? arr[224].toString() : null);
		obj.setUserDefField14(arr[225] != null ? arr[225].toString() : null);
		obj.setUserDefField15(arr[226] != null ? arr[226].toString() : null);
		obj.setUserDefField16(arr[227] != null ? arr[227].toString() : null);
		obj.setUserDefField17(arr[228] != null ? arr[228].toString() : null);
		obj.setUserDefField18(arr[229] != null ? arr[229].toString() : null);
		obj.setUserDefField19(arr[230] != null ? arr[230].toString() : null);
		obj.setUserDefField20(arr[231] != null ? arr[231].toString() : null);
		obj.setUserDefField21(arr[232] != null ? arr[232].toString() : null);
		obj.setUserDefField22(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField23(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField24(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField25(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField26(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField27(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField28(arr[239] != null ? arr[239].toString() : null);
		obj.setUserDefField29(arr[240] != null ? arr[240].toString() : null);
		obj.setUserDefField30(arr[241] != null ? arr[241].toString() : null);
		obj.setSupplyTypeASP(arr[242] != null ? arr[242].toString() : null);
		obj.setApproximateDistanceASP(
				arr[243] != null ? arr[243].toString() : null);
		obj.setDistanceSavedtoEWB(
				arr[244] != null ? arr[244].toString() : null);
		obj.setUserID(arr[245] != null ? arr[245].toString() : null);
		obj.setFileID(arr[246] != null ? arr[246].toString() : null);
		obj.setFileName(arr[247] != null ? arr[247].toString() : null);
		obj.setInvoiceOtherChargesASP(
				arr[248] != null ? arr[248].toString() : null);
		obj.setInvoiceAssessableAmountASP(
				arr[249] != null ? arr[249].toString() : null);
		obj.setInvoiceIGSTAmountASP(
				arr[250] != null ? arr[250].toString() : null);
		obj.setInvoiceCGSTAmountASP(
				arr[251] != null ? arr[251].toString() : null);
		obj.setInvoiceSGSTAmountASP(
				arr[252] != null ? arr[252].toString() : null);
		obj.setInvoiceCessAdvaloremAmountASP(
				arr[253] != null ? arr[253].toString() : null);
		obj.setInvoiceCessSpecificAmountASP(
				arr[254] != null ? arr[254].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmountASP(
				arr[255] != null ? arr[255].toString() : null);
		obj.setInvoiceStateCessSpecificAmountASP(
				arr[256] != null ? arr[256].toString() : null);
		obj.setInvoiceValueASP(arr[257] != null ? arr[257].toString() : null);
		obj.setIntegratedTaxAmountASP(
				arr[258] != null ? arr[258].toString() : null);
		obj.setCentralTaxAmountASP(
				arr[259] != null ? arr[259].toString() : null);
		obj.setStateUTTaxAmountASP(
				arr[260] != null ? arr[260].toString() : null);
		obj.setCessAdvaloremAmountASP(
				arr[261] != null ? arr[261].toString() : null);
		obj.setStateCessAdvaloremAmountASP(
				arr[262] != null ? arr[262].toString() : null);
		obj.setIntegratedTaxAmountRET1Impact(
				arr[263] != null ? arr[263].toString() : null);
		obj.setCentralTaxAmountRET1Impact(
				arr[264] != null ? arr[264].toString() : null);
		obj.setStateUTTaxAmountRET1Impact(
				arr[265] != null ? arr[265].toString() : null);
		obj.setCessAdvaloremAmountDifference(
				arr[266] != null ? arr[266].toString() : null);
		obj.setStateCessAdvaloremAmountDifference(
				arr[267] != null ? arr[267].toString() : null);
		obj.setRecordStatus(arr[268] != null ? arr[268].toString() : null);
		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {
		return " select CASE WHEN ITM.SUPPLY_TYPE in ('TAX','SEZWOP','SEZWP',"
				+ " 'DXP','EXPT','EXPWT','DTA') AND "
				+ "HDR.REVERSE_CHARGE <> 'Y' THEN "
				+ " 'Taxable Supplies - Forward Charge' "
				+ "WHEN ITM.SUPPLY_TYPE in ('TAX','DXP','DTA','SEZWOP','SEZWP') "
				+ "AND HDR.REVERSE_CHARGE = 'Y' THEN "
				+ " 'Taxable Supplies - Reverse Charge' "
				+ "WHEN ITM.SUPPLY_TYPE in('NIL','EXT') THEN "
				+ " 'Exempted Supplies - NIL / EXT' "
				+ "WHEN ITM.SUPPLY_TYPE in ('SCH3','NON') "
				+ "THEN 'NON GST Supplies - NON / SCH3' end AS CATEGORY, "
				+ "HDR.RETURN_TYPE,HDR.TABLE_SECTION, HDR.IRN, "
				+ "HDR.IRN_DATE, HDR.TAX_SCHEME, HDR.CANCEL_REASON, "
				+ "HDR.CANCEL_REMARKS, " + "HDR.SUPPLY_TYPE AS supply,"
				+ "HDR.DOC_CATEGORY, "
				+ "HDR.DOC_TYPE, HDR.DOC_NUM, HDR.DOC_DATE, HDR.REVERSE_CHARGE, "
				+ "HDR.SUPPLIER_GSTIN, HDR.SUPP_TRADE_NAME, HDR.SUPP_LEGAL_NAME, "
				+ "HDR.SUPP_BUILDING_NUM, HDR.CUST_SUPP_ADDRESS2 "
				+ "AS SUPPLIERADDRESS2, HDR.SUPP_LOCATION, HDR.SUPP_PINCODE, "
				+ "HDR.SUPP_STATE_CODE, HDR.SUPP_PHONE, HDR.SUPP_EMAIL, "
				+ "HDR.CUST_GSTIN, HDR.CUST_TRADE_NAME, HDR.CUST_SUPP_NAME, "
				+ "HDR.CUST_SUPP_ADDRESS1, HDR.CUST_SUPP_ADDRESS2, "
				+ "HDR.CUST_SUPP_ADDRESS4, HDR.CUST_PINCODE, HDR.BILL_TO_STATE, "
				+ "HDR.POS, HDR.CUST_PHONE, HDR.CUST_EMAIL, HDR.DISPATCHER_GSTIN, "
				+ "HDR.DISPATCHER_TRADE_NAME, HDR.DISPATCHER_BUILDING_NUM, "
				+ "HDR.DISPATCHER_BUILDING_NAME, HDR.DISPATCHER_LOCATION, "
				+ "HDR.DISPATCHER_PINCODE, HDR.DISPATCHER_STATE_CODE, HDR.SHIP_TO_GSTIN, "
				+ "HDR.SHIP_TO_TRADE_NAME, HDR.SHIP_TO_LEGAL_NAME, HDR.SHIP_TO_BUILDING_NUM, "
				+ "HDR.SHIP_TO_BUILDING_NAME, HDR.SHIP_TO_LOCATION, ITM.SHIP_TO_PINCODE, "
				+ "ITM.SHIP_TO_STATE AS SHIPTOSTATECODE, ITM.ITM_NO, ITM.SERIAL_NUM2, "
				+ "ITM.PRODUCT_NAME, ITM.ITM_DESCRIPTION, ITM.IS_SERVICE, "
				+ "ITM.ITM_HSNSAC, ITM.BAR_CODE, ITM.BATCH_NAME_OR_NUM, "
				+ "ITM.BATCH_EXPIRY_DATE, ITM.WARRANTY_DATE, ITM.ORDER_ITEM_REFERENCE, "
				+ "ITM.ATTRIBUTE_NAME, ITM.ATTRIBUTE_VALUE, ITM.ORIGIN_COUNTRY, "
				+ "ITM.ITM_UQC, ITM.ITM_QTY, ITM.FREE_QTY, ITM.UNIT_PRICE, "
				+ "ITM.ITEM_AMT_UP_QTY, ITM.ITEM_DISCOUNT, ITM.PRE_TAX_AMOUNT, "
				+ "ITM.TAXABLE_VALUE, ITM.IGST_RATE, ITM.IGST_AMT, ITM.CGST_RATE, "
				+ "ITM.CGST_AMT, ITM.SGST_RATE, ITM.SGST_AMT, "
				+ "ITM.CESS_RATE_ADVALOREM, ITM.CESS_AMT_ADVALOREM, "
				+ "ITM.CESS_RATE_SPECIFIC, ITM.CESS_AMT_SPECIFIC, "
				+ "ITM.STATECESS_RATE, ITM.STATECESS_AMT, ITM.STATE_CESS_SPECIFIC_RATE, "
				+ "ITM.STATE_CESS_SPECIFIC_AMOUNT, ITM.OTHER_VALUES, ITM.TOT_ITEM_AMT, "
				+ "ITM.INV_OTHER_CHARGES, ITM.INV_ASSESSABLE_AMT, ITM.INV_IGST_AMT, "
				+ "ITM.INV_CGST_AMT, ITM.INV_SGST_AMT, ITM.INV_CESS_ADVLRM_AMT, "
				+ "ITM.INV_CESS_SPECIFIC_AMT, ITM.INV_STATE_CESS_AMT, "
				+ "ITM.INV_STATE_CESS_SPECIFIC_AMOUNT, "
				+ "CASE WHEN HDR.DATAORIGINTYPECODE IN ('A','AI') "
				+ "THEN TO_CHAR(HDR.DOC_AMT) "
				+ "WHEN HDR.DATAORIGINTYPECODE IN ('E','EI') "
				+ "THEN TO_CHAR(ITM.LINE_ITEM_AMT) END LINE_ITEM_AMT, "
				+ "HDR.ROUND_OFF, HDR.TOT_INV_VAL_WORLDS, HDR.TCS_FLAG_INCOME_TAX, "
				+ "ITM.TCS_RATE_INCOME_TAX, ITM.TCS_AMOUNT_INCOME_TAX, "
				+ "CUSTOMER_PAN_OR_AADHAAR, HDR.FOREIGN_CURRENCY, "
				+ "HDR.COUNTRY_CODE, HDR.INV_VAL_FC, HDR.SHIP_PORT_CODE, "
				+ "HDR.SHIP_BILL_NUM, HDR.SHIP_BILL_DATE, HDR.INV_REMARKS, "
				+ "HDR.INV_PERIOD_START_DATE, HDR.INV_PERIOD_END_DATE, "
				+ "ITM.PRECEEDING_INV_NUM, ITM.PRECEEDING_INV_DATE, "
				+ "ITM.INV_REFERENCE, ITM.RECEIPT_ADVICE_REFERENCE, "
				+ "ITM.RECEIPT_ADVICE_DATE, ITM.TENDER_REFERENCE , "
				+ "ITM.CONTRACT_REFERENCE, ITM.EXTERNAL_REFERENCE, "
				+ "ITM.PROJECT_REFERENCE, ITM.CUST_PO_REF_NUM, ITM.CUST_PO_REF_DATE, "
				+ "HDR.PAYEE_NAME, HDR.MODE_OF_PAYMENT, HDR.BRANCH_IFSC_CODE, "
				+ "HDR.PAYMENT_TERMS, HDR.PAYMENT_INSTRUCTION, HDR.CR_TRANSFER, "
				+ "HDR.DB_DIRECT, HDR.CR_DAYS, ITM.PAID_AMT, ITM.BAL_AMT, "
				+ "HDR.PAYMENT_DUE_DATE, HDR.ACCOUNT_DETAIL, HDR.ECOM_GSTIN, "
				+ "HDR.ECOM_TRANS_ID, ITM.SUPPORTING_DOC_URL, ITM.SUPPORTING_DOC_BASE64, "
				+ "ITM.ADDITIONAL_INFORMATION, HDR.TRANS_TYPE, HDR.SUB_SUPP_TYPE, "
				+ "HDR.OTHER_SUPP_TYPE_DESC, HDR.TRANSPORTER_ID, HDR.TRANSPORTER_NAME, "
				+ "HDR.TRANSPORT_MODE, HDR.TRANSPORT_DOC_NUM, HDR.TRANSPORT_DOC_DATE, "
				+ "HDR.DISTANCE , HDR.VEHICLE_NUM, HDR.VEHICLE_TYPE, "
				+ "HDR.RETURN_PERIOD, HDR.ORIGINAL_DOC_TYPE, HDR.ORIGINAL_CUST_GSTIN, "
				+ "HDR.DIFF_PERCENT, HDR.SECTION7_OF_IGST_FLAG, HDR.CLAIM_REFUND_FLAG, "
				+ "HDR.AUTOPOPULATE_TO_REFUND, HDR.CRDR_PRE_GST, HDR.CUST_SUPP_TYPE, "
				+ "HDR.CUST_SUPP_CODE AS CUSTOMERCODE, ITM.PRODUCT_CODE, "
				+ "ITM.ITM_TYPE, ITM.ITC_FLAG, HDR.STATE_APPLYING_CESS, ITM.FOB , "
				+ "ITM.EXPORT_DUTY, HDR.EXCHANGE_RATE, ITM.CRDR_REASON, "
				+ "HDR.TCS_FLAG , ITM.TCS_AMT, TCS_CGST_AMT, TCS_SGST_AMT, "
				+ "HDR.TDS_FLAG, TDS_IGST_AMT, TDS_CGST_AMT, TDS_SGST_AMT, "
				+ "HDR.USER_ID AS userid, HDR.COMPANY_CODE, HDR.SOURCE_IDENTIFIER, "
				+ "HDR.SOURCE_FILENAME, ITM.PLANT_CODE, HDR.DIVISION, SUB_DIVISION, "
				+ "ITM.LOCATION, HDR.SALES_ORGANIZATION, HDR.DISTRIBUTION_CHANNEL, "
				+ "HDR.PROFIT_CENTRE, HDR.PROFIT_CENTRE2, HDR.USERACCESS1, "
				+ "HDR.USERACCESS2, HDR.USERACCESS3, HDR.USERACCESS4, "
				+ "HDR.USERACCESS5, HDR.USERACCESS6, ITM.GLCODE_TAXABLEVALUE, "
				+ "HDR.GLCODE_IGST, HDR.GLCODE_CGST, HDR.GLCODE_SGST, "
				+ "HDR.GLCODE_ADV_CESS, HDR.GLCODE_SP_CESS, HDR.GLCODE_STATE_CESS, "
				+ "HDR.GL_STATE_CESS_SPECIFIC, HDR.GL_POSTING_DATE, "
				+ "HDR.SALES_ORD_NUM, HDR.EWAY_BILL_NUM, "
				+ "TO_DATE(HDR.EWAY_BILL_DATE), HDR.ACCOUNTING_VOUCHER_NUM, "
				+ "HDR.ACCOUNTING_VOUCHER_DATE, ITM.DOCUMENT_REFERENCE_NUMBER, "
				+ "HDR.CUST_TAN, ITM.USERDEFINED_FIELD1, ITM.USERDEFINED_FIELD2, "
				+ "ITM.USERDEFINED_FIELD3, ITM.USERDEFINED_FIELD4, "
				+ "ITM.USERDEFINED_FIELD5, ITM.USERDEFINED_FIELD6, ITM.USERDEFINED_FIELD7, "
				+ "ITM.USERDEFINED_FIELD8, ITM.USERDEFINED_FIELD9, ITM.USERDEFINED_FIELD10, "
				+ "ITM.USERDEFINED_FIELD11, ITM.USERDEFINED_FIELD12, ITM.USERDEFINED_FIELD13, "
				+ "ITM.USERDEFINED_FIELD14, ITM.USERDEFINED_FIELD15, ITM.USERDEFINED_FIELD16, "
				+ "ITM.USERDEFINED_FIELD17, ITM.USERDEFINED_FIELD18, ITM.USERDEFINED_FIELD19, "
				+ "ITM.USERDEFINED_FIELD20, ITM.USERDEFINED_FIELD21, ITM.USERDEFINED_FIELD22, "
				+ "ITM.USERDEFINED_FIELD23, ITM.USERDEFINED_FIELD24, ITM.USERDEFINED_FIELD25, "
				+ "ITM.USERDEFINED_FIELD26, ITM.USERDEFINED_FIELD27, ITM.USERDEFINED_FIELD28, "
				+ "ITM.USERDEFINED_FIELD29, ITM.USERDEFINED_FIELD30, HDR.SUPPLY_TYPE as SUPPLYTYPE, "
				+ " '' AS ApproximateDistance_ASP, '' AS DistanceSavedtoEWB, "
				+ "HDR.CREATED_BY as USER_ID, HDR.FILE_ID, FIL.FILE_NAME as FILE_NAME, "
				+ "HDR.INV_OTHER_CHARGES as InvoiceOtherChargesASP, "
				+ "HDR.INV_ASSESSABLE_AMT as InvoiceAssessableAmountASP, "
				+ "HDR.INV_IGST_AMT as InvoiceIGSTAmountASP, HDR.INV_CGST_AMT "
				+ "as InvoiceCGSTAmountASP, HDR.INV_SGST_AMT as InvoiceSGSTAmountASP, "
				+ "HDR.INV_CESS_ADVLRM_AMT as InvoiceCessAdvaloremAmountASP, "
				+ "HDR.INV_CESS_SPECIFIC_AMT as InvoiceCessSpecificAmountASP, "
				+ "HDR.INV_STATE_CESS_AMT as InvoiceStateCessAdvaloremAmountASP, "
				+ "HDR.INV_STATE_CESS_SPECIFIC_AMOUNT "
				+ "as InvoiceStateCessSpecificAmountASP, HDR.DOC_AMT "
				+ "AS InvoiceValue_ASP, ITM.MEMO_VALUE_IGST, "
				+ "HDR.MEMO_VALUE_CGST, HDR.MEMO_VALUE_SGST, "
				+ "ITM.MEMO_VALUE_CESS_ADV AS CessAdvaloremAmount_ASP, "
				+ "ITM.MEMO_VALUE_STATE_CESS_ADV AS StateCessAdvaloremAmount_ASP, "
				+ "ITM.IGST_RET1_IMPACT, ITM.CGST_RET1_IMPACT, "
				+ "ITM.SGST_RET1_IMPACT, ITM.CESS_ADV_RET1_IMPACT "
				+ "AS CessAdvaloremAmount_Difference, "
				+ "ITM.STATE_CESS_ADV_RET1_IMPACT "
				+ "AS StateCessAdvaloremAmount_Difference, "
				+ " 'ACTIVE' AS RECORD_STATUS FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM "
				+ "ON HDR.ID = ITM.DOC_HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "AND HDR.ASP_INVOICE_STATUS = 2 AND HDR.IS_DELETE = FALSE "
				+ "AND HDR.RETURN_TYPE = 'GSTR1' LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID " + buildQuery;

	}
}
