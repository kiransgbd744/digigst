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

import com.ey.advisory.app.data.views.client.Anx1inwardapiErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx1InwardApiErrorReportsDaoImpl")
public class Anx1InwardApiErrorReportsDaoImpl
		implements Anx1InwardApiErrorReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Anx1inwardapiErrorRecordsDto> getApiErrorReports(
			SearchCriteria criteria) {

		Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;

		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		LocalDate docFromDate = request.getDocDateFrom();
		LocalDate docToDate = request.getDocDateTo();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		String status = request.getStatus();

		String dataType = request.getDataType();

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
				buildQuery.append(" AND HDR.CUST_GSTIN IN :gstinList");
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
				buildQuery.append(" AND ITM.DIVISION IN :divisionList");
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

		if (receiveFromDate != null && receiveToDate != null) {
			buildQuery.append(" AND HDR.RECEIVED_DATE BETWEEN ");
			buildQuery.append(":receiveFromDate AND :receiveToDate");
		}
		if (docFromDate != null && docToDate != null) {
			buildQuery.append(" AND HDR.DOC_DATE BETWEEN :docFromDate "
					+ "AND :docToDate");
		}
		if (returnFrom != null && returnTo != null) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":returnFrom AND :returnTo");
			buildQuery.append(" AND ITM.DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":returnFrom AND :returnTo");
		}
		if (status.equalsIgnoreCase(DownloadReportsConstant.ERRORACTIVE)) {
			buildQuery.append(" AND IS_DELETE = FALSE");
		}
		if (status.equalsIgnoreCase(DownloadReportsConstant.ERRORINACTIVE)) {
			buildQuery.append(" AND IS_DELETE = TRUE");
		}

		String queryStr = createApiErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("returnFrom", derivedRetPeriodFrom);
			q.setParameter("returnTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
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
				q.setParameter("salesList", purchaseList);
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

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1inwardapiErrorRecordsDto convertError(Object[] arr) {
		Anx1inwardapiErrorRecordsDto obj = new Anx1inwardapiErrorRecordsDto();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setSourceFileName(arr[2] != null ? arr[2].toString() : null);
		obj.setProfitCentre(arr[3] != null ? arr[3].toString() : null);
		obj.setPlant(arr[4] != null ? arr[4].toString() : null);
		obj.setDivision(arr[5] != null ? arr[5].toString() : null);
		obj.setLocation(arr[6] != null ? arr[6].toString() : null);
		obj.setPurchaseOrganisation(arr[7] != null ? arr[7].toString() : null);
		obj.setBillOfEntry(arr[8] != null ? arr[8].toString() : null);
		obj.setBillOfEntryDate(arr[9] != null ? arr[9].toString() : null);
		if (arr[9] != null) {
			String strdate = arr[9].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBillOfEntryDate(newDate);
		} else {
			obj.setBillOfEntryDate(null);
		}
		obj.setITCEntitlement(arr[10] != null ? arr[10].toString() : null);
		obj.setUserAccess1(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess2(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess3(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess4(arr[14] != null ? arr[14].toString() : null);
		obj.setUserAccess5(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess6(arr[16] != null ? arr[16].toString() : null);
		obj.setReturnPeriod(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplierGSTIN(arr[18] != null ? arr[18].toString() : null);
		obj.setDocumentType(arr[19] != null ? arr[19].toString() : null);
		obj.setSupplyType(arr[120] != null ? arr[120].toString() : null);
		obj.setDocumentNumber(arr[21] != null ? arr[21].toString() : null);
		if (arr[22] != null) {
			String strdate = arr[22].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		if (arr[122] != null) {
			String strdate = arr[122].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setOriginalDocumentDate(newDate);
		} else {
			obj.setOriginalDocumentDate(null);
		}
		obj.setOriginalDocumentNumber(
				arr[121] != null ? arr[121].toString() : null);
		obj.setCRDRPreGST(arr[25] != null ? arr[25].toString() : null);
		obj.setRecipientGSTIN(arr[26] != null ? arr[26].toString() : null);
		obj.setSupplierType(arr[120] != null ? arr[120].toString() : null);
		obj.setDifferentialFlag(arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[29] != null ? arr[29].toString() : null);
		obj.setSupplierName(arr[30] != null ? arr[30].toString() : null);
		obj.setSupplierCode(arr[31] != null ? arr[31].toString() : null);
		obj.setSupplierAddress1(arr[32] != null ? arr[32].toString() : null);
		obj.setSupplierAddress2(arr[33] != null ? arr[33].toString() : null);
		obj.setSupplierAddress3(arr[34] != null ? arr[34].toString() : null);
		obj.setSupplierAddress4(arr[35] != null ? arr[35].toString() : null);
		obj.setPOS(arr[36] != null ? arr[36].toString() : null);
		obj.setStateApplyingCess(arr[37] != null ? arr[37].toString() : null);
		obj.setPortCode(arr[38] != null ? arr[38].toString() : null);
		obj.setSection7ofIGSTFlag(arr[39] != null ? arr[39].toString() : null);
		obj.setInvoiceValue(arr[123] != null ? arr[123].toString() : null);
		obj.setReverseChargeFlag(arr[41] != null ? arr[41].toString() : null);
		if (arr[42] != null) {
			String strdate = arr[42].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPostingDate(newDate);
		} else {
			obj.setPostingDate(null);
		}
		obj.setITCReversalIdentifier(
				arr[43] != null ? arr[43].toString() : null);
		obj.setClaimRefundFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[45] != null ? arr[45].toString() : null);
		obj.setEWayBillNumber(arr[46] != null ? arr[46].toString() : null);
		if (arr[47] != null) {
			String strdate = arr[47].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setEWayBillDate(newDate);
		} else {
			obj.setEWayBillDate(null);
		}
		obj.setGLCodeTaxableValue(arr[49] != null ? arr[49].toString() : null);
		obj.setGLCodeIGST(arr[50] != null ? arr[50].toString() : null);
		obj.setGLCodeCGST(arr[51] != null ? arr[51].toString() : null);
		obj.setGLCodeSGST(arr[52] != null ? arr[52].toString() : null);
		obj.setGLCodeAdvaloremCess(arr[53] != null ? arr[53].toString() : null);
		obj.setGLCodeSpecificCess(arr[54] != null ? arr[54].toString() : null);
		obj.setGLCodeStateCess(arr[55] != null ? arr[55].toString() : null);
		obj.setLineNumber(arr[56] != null ? arr[56].toString() : null);
		obj.setHSNorSAC(arr[57] != null ? arr[57].toString() : null);
		obj.setItemCode(arr[58] != null ? arr[58].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[59] != null ? arr[59].toString() : null);
		if (arr[60] != null) {
			String strdate = arr[60].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPurchaseVoucherDate(newDate);
		} else {
			obj.setPurchaseVoucherDate(null);
		}
		obj.setPurchaseVoucherNumber(
				arr[61] != null ? arr[61].toString() : null);
		if (arr[62] != null) {
			String strdate = arr[62].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDate(newDate);
		} else {
			obj.setPaymentDate(null);
		}
		obj.setItemDescription(arr[63] != null ? arr[63].toString() : null);
		obj.setCategoryOfItem(arr[64] != null ? arr[64].toString() : null);
		obj.setUnitOfMeasurement(arr[65] != null ? arr[65].toString() : null);
		obj.setQuantity(arr[66] != null ? arr[66].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[67] != null ? arr[67].toString() : null);
		obj.setUserDefinedField1(arr[68] != null ? arr[68].toString() : null);
		obj.setUserDefinedField2(arr[69] != null ? arr[69].toString() : null);
		obj.setUserDefinedField3(arr[70] != null ? arr[70].toString() : null);
		obj.setUserDefinedField4(arr[71] != null ? arr[71].toString() : null);
		obj.setUserDefinedField5(arr[72] != null ? arr[72].toString() : null);
		obj.setUserDefinedField6(arr[73] != null ? arr[73].toString() : null);
		obj.setUserDefinedField7(arr[74] != null ? arr[74].toString() : null);
		obj.setUserDefinedField8(arr[75] != null ? arr[75].toString() : null);
		obj.setUserDefinedField9(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefinedField10(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefinedField11(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefinedField12(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefinedField13(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefinedField14(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefinedField15(arr[82] != null ? arr[82].toString() : null);
		obj.setOtherValue(arr[83] != null ? arr[83].toString() : null);
		obj.setAdjustmentReferenceNo(
				arr[84] != null ? arr[84].toString() : null);
		if (arr[85] != null) {
			String strdate = arr[85].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAdjustmentReferenceDate(newDate);
		} else {
			obj.setAdjustmentReferenceDate(null);
		}
		obj.setTaxableValue(arr[86] != null ? arr[86].toString() : null);
		obj.setIntegratedTaxRate(arr[87] != null ? arr[87].toString() : null);
		obj.setIntegratedTaxAmount(arr[88] != null ? arr[88].toString() : null);
		obj.setCentralTaxRate(arr[89] != null ? arr[89].toString() : null);
		obj.setPaymentVoucherNumber(
				arr[90] != null ? arr[90].toString() : null);
		obj.setContractNumber(arr[91] != null ? arr[91].toString() : null);
		if (arr[92] != null) {
			String strdate = arr[92].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setContractDate(newDate);
		} else {
			obj.setContractDate(null);
		}
		obj.setContractValue(arr[93] != null ? arr[93].toString() : null);
		obj.setCentralTaxAmount(arr[94] != null ? arr[94].toString() : null);
		obj.setStateUTTaxRate(arr[95] != null ? arr[95].toString() : null);
		obj.setStateUTTaxAmount(arr[96] != null ? arr[96].toString() : null);
		obj.setSpecificCessRate(arr[97] != null ? arr[97].toString() : null);
		obj.setSpecificCessAmount(arr[98] != null ? arr[98].toString() : null);
		obj.setAdvaloremCessRate(arr[99] != null ? arr[99].toString() : null);
		obj.setAdvaloremCessAmount(
				arr[100] != null ? arr[100].toString() : null);
		obj.setStateCessRate(arr[101] != null ? arr[101].toString() : null);
		obj.setStateCessAmount(arr[102] != null ? arr[102].toString() : null);
		obj.setTaxableValueAdjusted(
				arr[103] != null ? arr[103].toString() : null);
		obj.setAvailableIGST(arr[104] != null ? arr[104].toString() : null);
		obj.setAvailableCGST(arr[105] != null ? arr[105].toString() : null);
		obj.setAvailableSGST(arr[106] != null ? arr[106].toString() : null);
		obj.setAvailableCess(arr[107] != null ? arr[107].toString() : null);
		obj.setCIFValue(arr[108] != null ? arr[108].toString() : null);
		obj.setCustomDuty(arr[109] != null ? arr[109].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(
				arr[110] != null ? arr[110].toString() : null);
		obj.setCentralTaxAmountAdjusted(
				arr[111] != null ? arr[111].toString() : null);
		obj.setStateUTTaxAmountAdjusted(
				arr[112] != null ? arr[112].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(
				arr[113] != null ? arr[113].toString() : null);
		obj.setSpecificCessAmountAdjusted(
				arr[114] != null ? arr[114].toString() : null);
		obj.setStateCessAmountAdjusted(
				arr[115] != null ? arr[115].toString() : null);
		obj.setASPInformationID(arr[116] != null ? arr[116].toString() : null);
		obj.setASPInformationDescription(
				arr[117] != null ? arr[117].toString() : null);
		obj.setASPErrorCode(arr[118] != null ? arr[118].toString() : null);
		obj.setASPErrorDescription(
				arr[119] != null ? arr[119].toString() : null);
		obj.setEligibilityIndicator(
				arr[124] != null ? arr[124].toString() : null);

		return obj;
	}

	private String createApiErrorQueryString(String buildQuery) {
		return "SELECT HDR.ID," + "ITM.USER_ID, "
				+ "ITM.SOURCE_FILENAME,ITM.PROFIT_CENTRE,ITM.PLANT_CODE,"
				+ "ITM.DIVISION,ITM.LOCATION,ITM.PURCHASE_ORGANIZATION,"
				+ "ITM.BILL_OF_ENTRY,ITM.BILL_OF_ENTRY_DATE,ITM.ITC_ENTITLEMENT,"
				+ "ITM.USERACCESS1,ITM.USERACCESS2,ITM.USERACCESS3,"
				+ "ITM.USERACCESS4,ITM.USERACCESS5,ITM.USERACCESS6,"
				+ "ITM.RETURN_PERIOD,HDR.SUPPLIER_GSTIN,HDR.DOC_TYPE,"
				+ "HDR.SUPPLY_TYPE,HDR.DOC_NUM,ITM.DOC_DATE,HDR.ORIGINAL_DOC_DATE,"
				+ "HDR.ORIGINAL_DOC_NUM,ITM.CRDR_PRE_GST,HDR.CUST_GSTIN,"
				+ "ITM.CUST_SUPP_TYPE,ITM.DIFF_PERCENT,ITM.ORIG_SUPPLIER_GSTIN,"
				+ "ITM.CUST_SUPP_NAME,ITM.CUST_SUPP_CODE,ITM.CUST_SUPP_ADDRESS1,"
				+ "ITM.CUST_SUPP_ADDRESS2,ITM.CUST_SUPP_ADDRESS3,"
				+ "ITM.CUST_SUPP_ADDRESS4,ITM.POS,ITM.STATE_APPLYING_CESS,"
				+ "ITM.SHIP_PORT_CODE,ITM.SECTION7_OF_IGST_FLAG,"
				+ "HDR.DOC_AMT AS INV_VALUE,ITM.REVERSE_CHARGE,ITM.POSTING_DATE,"
				+ "HDR.ITC_REVERSAL_IDENTIFER,ITM.CLAIM_REFUND_FLAG,"
				+ "ITM.AUTOPOPULATE_TO_REFUND,"
				+ "ITM.EWAY_BILL_NUM,ITM.EWAY_BILL_DATE,ITM.DOC_HEADER_ID,"
				+ "ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,ITM.GLCODE_CGST,"
				+ "ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS,"
				+ "ITM.GLCODE_STATE_CESS,ITM.ITM_NO,ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,"
				+ "ITM.COMMON_SUP_INDICATOR,ITM.PURCHASE_VOUCHER_DATE,"
				+ "ITM.PURCHASE_VOUCHER_NUM,ITM.PAYMENT_DATE,ITM.ITM_DESCRIPTION,"
				+ "ITM.ITM_TYPE,ITM.ITM_UQC,ITM.ITM_QTY,ITM.CRDR_REASON,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3,"
				+ "ITM.USERDEFINED_FIELD4,ITM.USERDEFINED_FIELD5,"
				+ "ITM.USERDEFINED_FIELD6,"
				+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
				+ "ITM.USERDEFINED_FIELD9,"
				+ "ITM.USERDEFINED_FIELD10,ITM.USERDEFINED_FIELD11,"
				+ "ITM.USERDEFINED_FIELD12,ITM.USERDEFINED_FIELD13,"
				+ "ITM.USERDEFINED_FIELD14,ITM.USERDEFINED_FIELD15,ITM.OTHER_VALUES,"
				+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,ITM.IGST_RATE,"
				+ "ITM.IGST_AMT,ITM.CGST_RATE,ITM.PAYMENT_VOUCHER_NUM,"
				+ "ITM.CONTRACT_NUMBER,ITM.CONTRACT_DATE,ITM.CONTRACT_VALUE,"
				+ "ITM.CGST_AMT,ITM.SGST_RATE,ITM.SGST_AMT,ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM,"
				+ "ITM.STATECESS_RATE,ITM.STATECESS_AMT,ITM.ADJ_TAXABLE_VALUE,"
				+ "ITM.AVAILABLE_IGST,ITM.AVAILABLE_CGST,ITM.AVAILABLE_SGST,"
				+ "ITM.AVAILABLE_CESS,ITM.CIF_VALUE,ITM.CUSTOM_DUTY,ITM.ADJ_IGST_AMT,"
				+ "ITM.ADJ_CGST_AMT,ITM.ADJ_SGST_AMT,ITM. ADJ_CESS_AMT_ADVALOREM,"
				+ "ITM.ADJ_CESS_AMT_SPECIFIC,ITM.ADJ_STATECESS_AMT,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP,"
				+ "TO_CHAR(ITM.SUPPLY_TYPE) AS ITM_SUPPLY_TYPE,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_NUM) AS ITM_ORIGINAL_DOC_NUM,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE) AS ITM_ORIGINAL_DOC_DATE,"
				+ "TO_CHAR(ITM.LINE_ITEM_AMT) AS ITM_INV_VALUE,"
				+ "TO_CHAR(ITM.ELIGIBILITY_INDICATOR) "
				+ "AS ITM_ELIGIBILITY_INDICATOR "
				+ "FROM ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID=ERRH.DOC_HEADER_ID "
				+ "INNER JOIN ANX_INWARD_DOC_ITEM  ITM ON HDR.ID=ITM.DOC_HEADER_ID"
				+ " LEFT OUTER JOIN TF_INWARD_ITEM_ERROR_INFO () ERRI ON "
				+ "ITM.DOC_HEADER_ID=ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITEM_INDEX,'-1' ) = IFNULL(ERRI.ITEM_INDEX,'-1' ) AND "
				+ "IFNULL(ITM.ITM_NO,0) = IFNULL(ERRI.ITM_NO,0) "
				+ "WHERE IS_ERROR=TRUE AND "
				+ "DATAORIGINTYPECODE IN ('A','AI') " + buildQuery
				+ " ORDER BY DOC_NUM,ITM_NO ";

	}

}
