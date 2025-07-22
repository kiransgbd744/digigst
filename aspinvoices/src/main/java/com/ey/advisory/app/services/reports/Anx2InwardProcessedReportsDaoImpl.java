package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx2InwardErrorRecordsDto;
import com.ey.advisory.app.data.views.client.Anx2InwardProcessedRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.Anx2InwardErrorRequestDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx2InwardProcessedReportsDaoImpl")
public class Anx2InwardProcessedReportsDaoImpl 
implements Anx2InwardProcessedReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	StringBuffer buildQuery = new StringBuffer();

	public List<Anx2InwardProcessedRecordsDto> getProcessedReports(
			SearchCriteria criteria) {

		Anx2InwardErrorRequestDto request = (Anx2InwardErrorRequestDto) criteria;
		String taxperiod = request.getTaxPeriod();
		List<String> docType = request.getDocType();
		List<String> recordtype = request.getRecordType();
		
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
						" AND HDR.PURCHASE_ORGANIZATION IN :purchaseList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND ITM.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
		}
		if (recordtype != null && !recordtype.isEmpty()) {
			buildQuery.append(" AND HDR.AN_TAX_DOC_TYPE IN :recordType ");
		}

		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_TYPE IN :docType ");
		}

		String queryStr = createAnx2InwardProcessedRecQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriod());
			q.setParameter("taxperiod", derivedRetPeriodFrom);
		}
		if (recordtype != null && !recordtype.isEmpty()) {
			q.setParameter("recordType", recordtype);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
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

		List<Object[]> list = q.getResultList();
		List<Anx2InwardProcessedRecordsDto> retList = list.parallelStream()
				.map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2InwardProcessedRecordsDto convertProcessed(Object[] arr) {
		Anx2InwardProcessedRecordsDto obj = new Anx2InwardProcessedRecordsDto();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnType(arr[2] != null ? arr[2].toString() : null);
		obj.setDataCategory(arr[3] != null ? arr[3].toString() : null);
		obj.setTableNumber(arr[4] != null ? arr[4].toString() : null);
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
		obj.setReturnPeriod(arr[20] != null ? arr[20].toString() : null);
		obj.setSupplierGSTIN(arr[21] != null ? arr[21].toString() : null);
		obj.setDocumentType(arr[22] != null ? arr[22].toString() : null);
		obj.setSupplyType(arr[23] != null ? arr[23].toString() : null);
		obj.setDocumentNumber(arr[24] != null ? arr[24].toString() : null);
		obj.setDocumentDate(arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalDocumentDate(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[27] != null ? arr[27].toString() : null);
		obj.setCRDRPreGST(arr[28] != null ? arr[28].toString() : null);
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
		obj.setPOS(arr[39] != null ? arr[39].toString() : null);
		obj.setStateApplyingCess(arr[40] != null ? arr[40].toString() : null);
		obj.setPortCode(arr[41] != null ? arr[41].toString() : null);
		obj.setSection7ofIGSTFlag(arr[42] != null ? arr[42].toString() : null);
		obj.setInvoiceValue(arr[43] != null ? arr[43].toString() : null);
		obj.setReverseChargeFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setPostingDate(arr[45] != null ? arr[45].toString() : null);
		obj.setITCReversalIdentifier(
				arr[46] != null ? arr[46].toString() : null);
		obj.setClaimRefundFlag(arr[47] != null ? arr[47].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[48] != null ? arr[48].toString() : null);
		obj.setEWayBillNumber(arr[49] != null ? arr[49].toString() : null);
		obj.setEWayBillDate(arr[50] != null ? arr[50].toString() : null);

		// docheaderid
		obj.setGLCodeTaxableValue(arr[52] != null ? arr[52].toString() : null);
		obj.setGLCodeIGST(arr[53] != null ? arr[53].toString() : null);
		obj.setGLCodeCGST(arr[54] != null ? arr[54].toString() : null);
		obj.setGLCodeSGST(arr[55] != null ? arr[55].toString() : null);
		obj.setGLCodeAdvaloremCess(arr[56] != null ? arr[56].toString() : null);
		obj.setGLCodeSpecificCess(arr[57] != null ? arr[57].toString() : null);
		obj.setGLCodeStateCess(arr[58] != null ? arr[58].toString() : null);
		obj.setLineNumber(arr[59] != null ? arr[59].toString() : null);
		obj.setHSNorSAC(arr[60] != null ? arr[60].toString() : null);
		obj.setItemCode(arr[61] != null ? arr[61].toString() : null);
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
		obj.setTaxableValue(arr[89] != null ? arr[89].toString() : null);
		obj.setIntegratedTaxRate(arr[90] != null ? arr[90].toString() : null);
		obj.setIntegratedTaxAmount(arr[91] != null ? arr[91].toString() : null);
		obj.setCentralTaxRate(arr[92] != null ? arr[92].toString() : null);
		obj.setPaymentVoucherNumber(
				arr[93] != null ? arr[93].toString() : null);
		obj.setContractNumber(arr[94] != null ? arr[94].toString() : null);
		obj.setContractDate(arr[95] != null ? arr[95].toString() : null);
		obj.setContractValue(arr[96] != null ? arr[96].toString() : null);
		obj.setCentralTaxAmount(arr[97] != null ? arr[97].toString() : null);
		obj.setStateUTTaxRate(arr[98] != null ? arr[98].toString() : null);
		obj.setStateUTTaxAmount(arr[99] != null ? arr[99].toString() : null);
		obj.setSpecificCessRate(arr[100] != null ? arr[100].toString() : null);
		obj.setSpecificCessAmount(
				arr[101] != null ? arr[101].toString() : null);
		obj.setAdvaloremCessRate(arr[102] != null ? arr[102].toString() : null);
		obj.setAdvaloremCessAmount(
				arr[103] != null ? arr[103].toString() : null);
		obj.setStateCessRate(arr[104] != null ? arr[104].toString() : null);
		obj.setStateCessAmount(arr[105] != null ? arr[105].toString() : null);
		obj.setTaxableValueAdjusted(
				arr[106] != null ? arr[106].toString() : null);
		obj.setAvailableIGST(arr[107] != null ? arr[107].toString() : null);
		obj.setAvailableCGST(arr[108] != null ? arr[108].toString() : null);
		obj.setAvailableSGST(arr[109] != null ? arr[109].toString() : null);
		obj.setAvailableCess(arr[110] != null ? arr[110].toString() : null);
		obj.setCIFValue(arr[111] != null ? arr[111].toString() : null);
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
		obj.setASPInformationID(arr[119] != null ? arr[119].toString() : null);
		obj.setASPInformationDescription(
				arr[120] != null ? arr[120].toString() : null);
		//
		//
		//
		obj.setEligibilityIndicator(
				arr[124] != null ? arr[124].toString() : null);
		obj.setUploadSource(arr[125] != null ? arr[125].toString() : null);
		obj.setSourceID(arr[126] != null ? arr[126].toString() : null);
		obj.setFileName(arr[127] != null ? arr[127].toString() : null);
		obj.setASPDateTime(arr[128] != null ? arr[128].toString() : null);
		obj.setData(arr[129] != null ? arr[129].toString() : null);
		obj.setUploadPeriod(arr[130] != null ? arr[130].toString() : null);
		obj.setCreditAvailedPeriod(
				arr[131] != null ? arr[131].toString() : null);
		obj.setSaveStatus(arr[132] != null ? arr[132].toString() : null);
		obj.setGSTNErrorCode(arr[133] != null ? arr[133].toString() : null);
		obj.setGSTNErrorDescription(
				arr[134] != null ? arr[134].toString() : null);
		obj.setGSTNRefID(arr[135] != null ? arr[135].toString() : null);
		obj.setGSTNRefIDTime(arr[136] != null ? arr[136].toString() : null);
		obj.setReconReport(arr[137] != null ? arr[137].toString() : null);
		obj.setReconResponse(arr[138] != null ? arr[138].toString() : null);

		return obj;
	}

	private String createAnx2InwardProcessedRecQueryString(String buildQuery) {
		return "SELECT * FROM (SELECT HDR.ID AS HID,HDR.USER_ID,"
				+ "HDR.AN_RETURN_TYPE AS RETURN_TYPE,"
				+ "HDR.AN_TAX_DOC_TYPE AS DATA_CATEGORY,"
				+ "HDR.AN_TABLE_SECTION AS TABLE_NUMBER,"
				+ "HDR.SOURCE_FILENAME,"
				+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,HDR.DIVISION,"
				+ "ITM.LOCATION,HDR.PURCHASE_ORGANIZATION,"
				+ "HDR.BILL_OF_ENTRY,HDR.BILL_OF_ENTRY_DATE,"
				+ "HDR.ITC_ENTITLEMENT,HDR.USERACCESS1,HDR.USERACCESS2,"
				+ "HDR.USERACCESS3,HDR.USERACCESS4,HDR.USERACCESS5,"
				+ "HDR.USERACCESS6,HDR.RETURN_PERIOD,HDR.SUPPLIER_GSTIN,"
				+ "HDR.DOC_TYPE,HDR.SUPPLY_TYPE,HDR.DOC_NUM,"
				+ "HDR.DOC_DATE,HDR.ORIGINAL_DOC_DATE,"
				+ "HDR.ORIGINAL_DOC_NUM,HDR.CRDR_PRE_GST,HDR.CUST_GSTIN,"
				+ "HDR.CUST_SUPP_TYPE,HDR.DIFF_PERCENT,"
				+ "HDR.ORIG_SUPPLIER_GSTIN,HDR.CUST_SUPP_NAME,"
				+ "HDR.CUST_SUPP_CODE,HDR.CUST_SUPP_ADDRESS1,"
				+ "HDR.CUST_SUPP_ADDRESS2,HDR.CUST_SUPP_ADDRESS3,"
				+ "HDR.CUST_SUPP_ADDRESS4,HDR.POS,HDR.STATE_APPLYING_CESS,"
				+ "HDR.SHIP_PORT_CODE,HDR.SECTION7_OF_IGST_FLAG,"
				+ "HDR.DOC_AMT AS INV_VALUE,HDR.REVERSE_CHARGE,"
				+ "HDR.POSTING_DATE,HDR.ITC_REVERSAL_IDENTIFER,"
				+ "HDR.CLAIM_REFUND_FLAG,HDR.AUTOPOPULATE_TO_REFUND,"
				+ "HDR.EWAY_BILL_NUM,HDR.EWAY_BILL_DATE,"
				+ "ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,"
				+ "ITM.GLCODE_IGST,ITM.GLCODE_CGST,ITM.GLCODE_SGST,"
				+ "ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS,"
				+ "ITM.GLCODE_STATE_CESS,ITM.ITM_NO,ITM.ITM_HSNSAC,"
				+ "ITM.PRODUCT_CODE,ITM.COMMON_SUP_INDICATOR,"
				+ "ITM.PURCHASE_VOUCHER_DATE,ITM.PURCHASE_VOUCHER_NUM,"
				+ "ITM.PAYMENT_DATE,ITM.ITM_DESCRIPTION,ITM.ITM_TYPE,"
				+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.CRDR_REASON,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
				+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
				+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
				+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
				+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
				+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
				+ "ITM.USERDEFINED_FIELD15,ITM.OTHER_VALUES,"
				+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_RATE,ITM.IGST_AMT,ITM.CGST_RATE,"
				+ "ITM.PAYMENT_VOUCHER_NUM,ITM.CONTRACT_NUMBER,"
				+ "ITM.CONTRACT_DATE,ITM.CONTRACT_VALUE,ITM.CGST_AMT,"
				+ "ITM.SGST_RATE,ITM.SGST_AMT,ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.CESS_RATE_ADVALOREM,"
				+ "ITM.CESS_AMT_ADVALOREM,ITM.STATECESS_RATE,"
				+ "ITM.STATECESS_AMT,ITM.ADJ_TAXABLE_VALUE,"
				+ "ITM.AVAILABLE_IGST,ITM.AVAILABLE_CGST,"
				+ "ITM.AVAILABLE_SGST,ITM.AVAILABLE_CESS,ITM.CIF_VALUE,"
				+ "ITM.CUSTOM_DUTY,ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,"
				+ "ITM.ADJ_SGST_AMT,ITM. ADJ_CESS_AMT_ADVALOREM,"
				+ "ITM.ADJ_CESS_AMT_SPECIFIC,ITM.ADJ_STATECESS_AMT,"
				+ "IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'') "
				+ "AS INFO_ERROR_CODE_ASP,"
				+ "IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'') "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_NUM) AS ITM_ORIGINAL_DOC_NUM,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE) AS ITM_ORIGINAL_DOC_DATE,"
				+ "TO_CHAR(ITM.LINE_ITEM_AMT) AS ITM_INV_VALUE,"
				+ "TO_CHAR(ITM.ELIGIBILITY_INDICATOR) "
				+ "AS ITM_ELIGIBILITY_INDICATOR,CASE WHEN "
				+ "DATAORIGINTYPECODE IN ('A','AI') THEN 'API' "
				+ "WHEN DATAORIGINTYPECODE IN ('E','EI') "
				+ "THEN 'WEBUPLOAD' END AS UPLOAD_SOURCE,"
				+ "HDR.CREATED_BY AS SOURCE_ID," 
				+ "FIL.FILE_NAME AS FILE_NAME,"
				+ "HDR.CREATED_ON AS ASP_DATE_TIME,'CURRENT_PERIOD' "
				+ "AS DATA,TO_CHAR(HDR.ORIGINAL_DOC_DATE,'MMYYYY') "
				+ "AS UPLOADED_PERIOD,'NOT AVAILABLE' "
				+ "AS CREDIT_AVAILED_PERIOD,"
				+ "case when HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HDR.GSTN_ERROR = TRUE THEN 'IS_ERROR'"
				+ "WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS SAVE_STATUS,"
				+ "IFNULL(G_ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(G_ERRI.ERROR_CODE_ASP,'') AS GSTIN_ERROR_CODE_ASP,"
                + "IFNULL(G_ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
                + "IFNULL(G_ERRI.ERROR_DESCRIPTION_ASP,'') "
                + "AS GSTIN_ERROR_DESCRIPTION_ASP,G_ERRH.ID AS GSTIN_REF_ID,"
                + "G_ERRH.CREATED_ON AS GSTIN_REF_ID_TIME "  
				+ "FROM ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON "
				+ "HDR.ID=ERRH.DOC_HEADER_ID INNER JOIN "
				+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "LEFT OUTER JOIN TF_INWARD_ITEM_ERROR_INFO () ERRI ON "
				+ "ITM.DOC_HEADER_ID= ERRI.DOC_HEADER_ID AND "
				+ "ITM.ITM_NO=ERRI.ITM_NO LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_GSTIN_ERROR_INFO () G_ERRH ON "
				+ "HDR.ID=G_ERRH .DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_INWARD_ITEM_ERROR_INFO () G_ERRI ON "
				+ "ITM.DOC_HEADER_ID=G_ERRI.DOC_HEADER_ID "
				+ "AND ITM.ITM_NO=G_ERRI.ITM_NO "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE " + buildQuery
				+ "AND AN_RETURN_TYPE = 'ANX2' ) INWD LEFT OUTER JOIN"
				+ "(SELECT  BUCKET_TYPE,USER_RESPONSE,PR_DOC_DATE,"
				+ "PR_SUPPLIER_GSTIN,PR_RECIPIENT_GSTIN,PR_DOC_TYPE,"
				+ "PR_DOC_NUM  FROM  LINK_A2_PR where IS_ACTIVE = TRUE) "
				+ "LKT ON LKT.PR_DOC_DATE= INWD.DOC_DATE "
				+ "AND LKT.PR_SUPPLIER_GSTIN= INWD.SUPPLIER_GSTIN AND "
				+ "LKT.PR_RECIPIENT_GSTIN = INWD.CUST_GSTIN AND "
				+ "LKT.PR_DOC_TYPE= INWD.DOC_TYPE AND "
				+ "LKT.PR_DOC_NUM= INWD.DOC_NUM " 
				+ "ORDER BY DOC_NUM,ITM_NO";

	}

}
