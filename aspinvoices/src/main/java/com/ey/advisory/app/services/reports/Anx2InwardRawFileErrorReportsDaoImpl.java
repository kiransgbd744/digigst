
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx2InwardErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx2InwardErrorRequestDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Anx2InwardRawFileErrorReportsDaoImpl")
public class Anx2InwardRawFileErrorReportsDaoImpl
		implements Anx2InwardRawFileErrorReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	StringBuffer buildQuery = new StringBuffer();

	@Override
	public List<Anx2InwardErrorRecordsDto> getErrorReports(
			SearchCriteria criteria) {

		Anx2InwardErrorRequestDto request = (Anx2InwardErrorRequestDto) criteria;
		String taxperiod = request.getTaxPeriod();
		List<String> docType = request.getDocType();
		List<String> recordtype = request.getRecordType();
		
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

		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
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
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
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


		String queryStr = createErrorQueryString(buildQuery.toString());
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
		List<Anx2InwardErrorRecordsDto> retList = list.parallelStream()
				.map(o -> convertError(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2InwardErrorRecordsDto convertError(Object[] arr) {
		Anx2InwardErrorRecordsDto obj = new Anx2InwardErrorRecordsDto();

		obj.setUserID(arr[1] != null ? arr[1].toString() : null);
		obj.setSourceFileName(arr[2] != null ? arr[2].toString() : null);
		obj.setProfitCentre(arr[3] != null ? arr[3].toString() : null);
		obj.setPlant(arr[4] != null ? arr[4].toString() : null);
		obj.setDivision(arr[5] != null ? arr[5].toString() : null);
		obj.setLocation(arr[6] != null ? arr[6].toString() : null);
		obj.setBillOfEntry(arr[7] != null ? arr[7].toString() : null);
		obj.setITCEntitlement(arr[8] != null ? arr[8].toString() : null);
		obj.setBillOfEntryDate(arr[9] != null ? arr[9].toString() : null);
		obj.setPurchaseOrganisation(
				arr[10] != null ? arr[10].toString() : null);
		obj.setUserAccess1(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess2(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess3(arr[13] != null ? arr[13].toString() : null);
		obj.setUserAccess4(arr[14] != null ? arr[14].toString() : null);
		obj.setUserAccess5(arr[15] != null ? arr[15].toString() : null);
		obj.setUserAccess6(arr[16] != null ? arr[16].toString() : null);
		obj.setITCReversalIdentifier(
				arr[17] != null ? arr[17].toString() : null);
		obj.setReturnPeriod(arr[18] != null ? arr[18].toString() : null);
		obj.setSupplierGSTIN(arr[19] != null ? arr[19].toString() : null);
		obj.setPostingDate(arr[20] != null ? arr[20].toString() : null);
		obj.setDocumentType(arr[21] != null ? arr[21].toString() : null);
		obj.setSupplyType(arr[22] != null ? arr[22].toString() : null);
		obj.setDocumentNumber(arr[23] != null ? arr[23].toString() : null);
		obj.setDocumentDate(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalDocumentDate(arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalDocumentNumber(arr[26] != null ? arr[26].toString() : null);
		obj.setCRDRPreGST(arr[27] != null ? arr[27].toString() : null);
		obj.setRecipientGSTIN(arr[28] != null ? arr[28].toString() : null);
		obj.setSupplierType(arr[29] != null ? arr[29].toString() : null);
		obj.setDifferentialFlag(arr[30] != null ? arr[30].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[31] != null ? arr[31].toString() : null);
		obj.setSupplierName(arr[32] != null ? arr[32].toString() : null);
		obj.setSupplierCode(arr[33] != null ? arr[33].toString() : null);
		obj.setSupplierAddress1(arr[34] != null ? arr[34].toString() : null);
		obj.setSupplierAddress2(arr[35] != null ? arr[35].toString() : null);
		obj.setSupplierAddress3(arr[36] != null ? arr[36].toString() : null);
		obj.setSupplierAddress4(arr[37] != null ? arr[37].toString() : null);
		obj.setPOS(arr[38] != null ? arr[38].toString() : null);
		obj.setStateApplyingCess(arr[39] != null ? arr[39].toString() : null);
		obj.setPortCode(arr[40] != null ? arr[40].toString() : null);
		obj.setSection7ofIGSTFlag(arr[41] != null ? arr[41].toString() : null);
		obj.setInvoiceValue(arr[42] != null ? arr[42].toString() : null);
		obj.setReverseChargeFlag(arr[43] != null ? arr[43].toString() : null);
		obj.setClaimRefundFlag(arr[44] != null ? arr[44].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[45] != null ? arr[45].toString() : null);
		obj.setEWayBillNumber(arr[46] != null ? arr[46].toString() : null);
		obj.setEWayBillDate(arr[47] != null ? arr[47].toString() : null);
		//fileid
		//id
		// docheaderid
		obj.setGLCodeTaxableValue(arr[51] != null ? arr[51].toString() : null);
		obj.setGLCodeIGST(arr[52] != null ? arr[52].toString() : null);
		obj.setGLCodeCGST(arr[53] != null ? arr[53].toString() : null);
		obj.setGLCodeSGST(arr[54] != null ? arr[54].toString() : null);
		obj.setGLCodeAdvaloremCess(arr[55] != null ? arr[55].toString() : null);
		obj.setGLCodeSpecificCess(arr[56] != null ? arr[56].toString() : null);
		obj.setGLCodeStateCess(arr[57] != null ? arr[57].toString() : null);
		obj.setLineNumber(arr[58] != null ? arr[58].toString() : null);
		obj.setHSNorSAC(arr[59] != null ? arr[59].toString() : null);
		obj.setItemCode(arr[60] != null ? arr[60].toString() : null);
		obj.setItemDescription(arr[61] != null ? arr[61].toString() : null);
		obj.setCategoryOfItem(arr[62] != null ? arr[62].toString() : null);
		obj.setUnitOfMeasurement(arr[63] != null ? arr[63].toString() : null);
		obj.setQuantity(arr[64] != null ? arr[64].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[65] != null ? arr[65].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[66] != null ? arr[66].toString() : null);
		obj.setPurchaseVoucherDate(arr[67] != null ? arr[67].toString() : null);
		obj.setPurchaseVoucherNumber(
				arr[68] != null ? arr[68].toString() : null);
		obj.setPaymentDate(arr[69] != null ? arr[69].toString() : null);
		obj.setPaymentVoucherNumber(
				arr[70] != null ? arr[70].toString() : null);
		obj.setContractDate(arr[71] != null ? arr[71].toString() : null);
		obj.setContractValue(arr[72] != null ? arr[72].toString() : null);
		obj.setAvailableCGST(arr[73] != null ? arr[73].toString() : null);
		obj.setAvailableSGST(arr[74] != null ? arr[74].toString() : null);
		obj.setCIFValue(arr[75] != null ? arr[75].toString() : null);
		obj.setCustomDuty(arr[76] != null ? arr[76].toString() : null);
		obj.setUserDefinedField1(arr[77] != null ? arr[77].toString() : null);
		obj.setUserDefinedField2(arr[78] != null ? arr[78].toString() : null);
		obj.setUserDefinedField3(arr[79] != null ? arr[79].toString() : null);
		obj.setUserDefinedField4(arr[80] != null ? arr[80].toString() : null);
		obj.setUserDefinedField5(arr[81] != null ? arr[81].toString() : null);
		obj.setUserDefinedField6(arr[82] != null ? arr[82].toString() : null);
		obj.setUserDefinedField7(arr[83] != null ? arr[83].toString() : null);
		obj.setUserDefinedField8(arr[84] != null ? arr[84].toString() : null);
		obj.setUserDefinedField9(arr[85] != null ? arr[85].toString() : null);
		obj.setUserDefinedField10(arr[86] != null ? arr[86].toString() : null);
		obj.setUserDefinedField11(arr[87] != null ? arr[87].toString() : null);
		obj.setUserDefinedField12(arr[88] != null ? arr[88].toString() : null);
		obj.setUserDefinedField13(arr[89] != null ? arr[89].toString() : null);
		obj.setUserDefinedField14(arr[90] != null ? arr[90].toString() : null);
		obj.setUserDefinedField15(arr[91] != null ? arr[91].toString() : null);
		obj.setOtherValue(arr[92] != null ? arr[92].toString() : null);
		obj.setAdjustmentReferenceNo(
				arr[93] != null ? arr[93].toString() : null);
		obj.setAdjustmentReferenceDate(
				arr[94] != null ? arr[94].toString() : null);
		obj.setTaxableValue(arr[95] != null ? arr[95].toString() : null);
		obj.setIntegratedTaxRate(arr[96] != null ? arr[96].toString() : null);
		obj.setIntegratedTaxAmount(arr[97] != null ? arr[97].toString() : null);
		obj.setCentralTaxRate(arr[98] != null ? arr[98].toString() : null);
		obj.setCentralTaxAmount(arr[99] != null ? arr[99].toString() : null);
		obj.setStateUTTaxRate(arr[100] != null ? arr[100].toString() : null);
		obj.setStateUTTaxAmount(arr[101] != null ? arr[101].toString() : null);
		obj.setSpecificCessRate(arr[102] != null ? arr[102].toString() : null);
		obj.setSpecificCessAmount(
				arr[103] != null ? arr[103].toString() : null);
		obj.setContractNumber(arr[104] != null ? arr[104].toString() : null);
		obj.setAdvaloremCessRate(arr[105] != null ? arr[105].toString() : null);
		obj.setAvailableIGST(arr[106] != null ? arr[106].toString() : null);
		obj.setAdvaloremCessAmount(
				arr[107] != null ? arr[107].toString() : null);
		obj.setStateCessRate(arr[108] != null ? arr[108].toString() : null);
		obj.setAvailableCess(arr[109] != null ? arr[109].toString() : null);
		obj.setStateCessAmount(arr[110] != null ? arr[110].toString() : null);
		obj.setTaxableValueAdjusted(
				arr[111] != null ? arr[111].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(
				arr[112] != null ? arr[112].toString() : null);
		obj.setCentralTaxAmountAdjusted(
				arr[113] != null ? arr[113].toString() : null);
		obj.setStateUTTaxAmountAdjusted(
				arr[114] != null ? arr[114].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(
				arr[115] != null ? arr[115].toString() : null);
		obj.setSpecificCessAmountAdjusted(
				arr[116] != null ? arr[116].toString() : null);
		obj.setStateCessAmountAdjusted(
				arr[117] != null ? arr[117].toString() : null);
		obj.setASPErrorCode(arr[118] != null ? arr[118].toString() : null);
		obj.setASPErrorDescription(
				arr[119] != null ? arr[119].toString() : null);
		obj.setASPInformationID(arr[120] != null ? arr[120].toString() : null);
		obj.setASPInformationDescription(
				arr[121] != null ? arr[121].toString() : null);
		obj.setEligibilityIndicator(
				arr[126] != null ? arr[126].toString() : null);
		obj.setUploadSource(arr[127] != null ? arr[127].toString() : null);
		obj.setSourceID(arr[128] != null ? arr[128].toString() : null);
		obj.setFileName(arr[129] != null ? arr[129].toString() : null);
		obj.setAspdatetime(arr[130] != null ? arr[130].toString() : null);

		return obj;
	}

	private String createErrorQueryString(String buildQuery){
		
		return "SELECT * FROM ((SELECT HDR.ID AS HID,"
				+ "TO_CHAR(HDR.USER_ID) AS USER_ID,"
				+ "TO_CHAR(HDR.SOURCE_FILENAME) AS SOURCE_FILENAME,"
				+ "TO_CHAR(ITM.PROFIT_CENTRE) AS PROFIT_CENTRE,"
				+ "TO_CHAR(ITM.PLANT_CODE) AS PLANT_CODE,"
				+ "TO_CHAR(HDR.DIVISION)  AS DIVISION ,"
				+ "TO_CHAR(ITM.LOCATION) AS LOCATION,"
				+ "TO_CHAR(HDR.BILL_OF_ENTRY) AS BILL_OF_ENTRY,"
				+ "TO_CHAR(HDR.ITC_ENTITLEMENT) AS ITC_ENTITLEMENT,"
				+ "TO_CHAR(HDR.BILL_OF_ENTRY_DATE) AS BILL_OF_ENTRY_DATE,"
				+ "TO_CHAR(HDR.PURCHASE_ORGANIZATION) AS PURCHASE_ORGANIZATION,"
				+ "TO_CHAR(HDR.USERACCESS1) AS USERACCESS1,"
				+ "TO_CHAR(HDR.USERACCESS2) AS USERACCESS2,"
				+ "TO_CHAR(HDR.USERACCESS3) AS USERACCESS3,"
				+ "TO_CHAR(HDR.USERACCESS4) AS USERACCESS4,"
				+ "TO_CHAR(HDR.USERACCESS5) AS USERACCESS5,"
				+ "TO_CHAR(HDR.USERACCESS6) AS USERACCESS6,"
				+ "TO_CHAR(HDR.ITC_REVERSAL_IDENTIFER) AS ITC_REVERSAL_IDENTIFER,"
				+ "TO_CHAR(HDR.RETURN_PERIOD) AS RETURN_PERIOD,"
				+ "TO_CHAR(HDR.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,"
				+ "TO_CHAR(HDR.POSTING_DATE) AS POSTING_DATE,"
				+ "TO_CHAR(HDR.DOC_TYPE) AS DOC_TYPE,"
				+ "TO_CHAR(HDR.SUPPLY_TYPE) AS SUPPLY_TYPE,"
				+ "TO_CHAR(HDR.DOC_NUM) AS DOC_NUM, "
				+ "TO_CHAR(HDR.DOC_DATE) AS DOC_DATE,"
				+ "TO_CHAR(HDR.ORIGINAL_DOC_DATE) AS ORIGINAL_DOC_DATE,"
				+ "TO_CHAR(HDR.ORIGINAL_DOC_NUM) AS ORIGINAL_DOC_NUM,"
				+ "TO_CHAR(HDR.CRDR_PRE_GST) AS CRDR_PRE_GST,"
				+ "TO_CHAR(HDR.CUST_GSTIN) AS CUST_GSTIN,"
				+ "TO_CHAR(HDR.CUST_SUPP_TYPE) AS CUST_SUPP_TYPE,"
				+ "TO_CHAR(HDR.DIFF_PERCENT) AS DIFF_PERCENT,"
				+ "TO_CHAR(HDR.ORIG_SUPPLIER_GSTIN) AS ORIG_SUPPLIER_GSTIN,"
				+ "TO_CHAR(HDR.CUST_SUPP_NAME) AS CUST_SUPP_NAME,"
				+ "TO_CHAR(HDR.CUST_SUPP_CODE) AS CUST_SUPP_CODE,"
				+ "TO_CHAR(HDR.CUST_SUPP_ADDRESS1) AS CUST_SUPP_ADDRESS1,"
				+ "TO_CHAR(HDR.CUST_SUPP_ADDRESS2) AS CUST_SUPP_ADDRESS2,"
				+ "TO_CHAR(HDR.CUST_SUPP_ADDRESS3) AS CUST_SUPP_ADDRESS3,"
				+ "TO_CHAR(HDR.CUST_SUPP_ADDRESS4) AS CUST_SUPP_ADDRESS4,"
				+ "TO_CHAR(HDR.POS) AS POS, "
				+ "TO_CHAR(HDR.STATE_APPLYING_CESS) AS STATE_APPLYING_CESS, "
				+ "TO_CHAR(HDR.SHIP_PORT_CODE) AS SHIP_PORT_CODE,"
				+ "TO_CHAR(HDR.SECTION7_OF_IGST_FLAG) AS SECTION7_OF_IGST_FLAG,"
				+ "TO_CHAR(HDR.DOC_AMT) AS INV_VALUE,"
				+ "TO_CHAR(HDR.REVERSE_CHARGE) AS REVERSE_CHARGE,"
				+ "TO_CHAR(HDR.CLAIM_REFUND_FLAG) AS CLAIM_REFUND_FLAG,"
				+ "TO_CHAR(HDR.AUTOPOPULATE_TO_REFUND) AS AUTOPOPULATE_TO_REFUND,"
				+ "TO_CHAR(HDR.EWAY_BILL_NUM) AS EWAY_BILL_NUM,"
				+ "TO_CHAR(HDR.EWAY_BILL_DATE) AS EWAY_BILL_DATE, HDR.FILE_ID,"
				+ "FIL.ID, ITM.DOC_HEADER_ID, TO_CHAR(ITM.GLCODE_TAXABLEVALUE) "
				+ "AS GLCODE_TAXABLEVALUE, TO_CHAR(ITM.GLCODE_IGST) AS GLCODE_IGST,"
				+ "TO_CHAR(ITM.GLCODE_CGST) AS GLCODE_CGST, TO_CHAR(ITM.GLCODE_SGST) "
				+ "AS GLCODE_SGST, TO_CHAR(ITM.GLCODE_ADV_CESS) AS GLCODE_ADV_CESS, "
				+ "TO_CHAR(ITM.GLCODE_SP_CESS) AS GLCODE_SP_CESS,"
				+ "TO_CHAR(ITM.GLCODE_STATE_CESS) AS GLCODE_STATE_CESS,"
				+ "TO_CHAR(ITM.ITM_NO) AS ITM_NO , "
				+ "TO_CHAR(ITM.ITM_HSNSAC) AS ITM_HSNSAC,"
				+ "TO_CHAR(ITM.PRODUCT_CODE) AS PRODUCT_CODE,"
				+ "TO_CHAR(ITM.ITM_DESCRIPTION) AS ITM_DESCRIPTION,"
				+ "TO_CHAR(ITM.ITM_TYPE) AS ITM_TYPE,"
				+ "TO_CHAR(ITM.ITM_UQC) AS ITM_UQC,"
				+ "TO_CHAR(IFNULL(ITM.ITM_QTY,'0.000')) AS ITM_QTY,"
				+ "TO_CHAR(ITM.CRDR_REASON) AS CRDR_REASON,"
				+ "TO_CHAR(ITM.COMMON_SUP_INDICATOR) AS COMMON_SUP_INDICATOR,"
				+ "TO_CHAR(ITM.PURCHASE_VOUCHER_DATE) AS PURCHASE_VOUCHER_DATE,"
				+ "TO_CHAR(ITM.PURCHASE_VOUCHER_NUM) AS PURCHASE_VOUCHER_NUM,"
				+ "TO_CHAR(ITM.PAYMENT_DATE) AS PAYMENT_DATE,"
				+ "TO_CHAR(ITM.PAYMENT_VOUCHER_NUM) AS PAYMENT_VOUCHER_NUM,"
				+ "TO_CHAR(ITM.CONTRACT_DATE) AS CONTRACT_DATE,"
				+ "TO_CHAR(IFNULL(ITM.CONTRACT_VALUE,'0.00')) "
    			+ "AS CONTRACT_VALUE,"
    			+ "TO_CHAR(IFNULL(ITM.AVAILABLE_CGST,'0.00')) "
				+ "AS AVAILABLE_CGST,"
				+ "TO_CHAR(IFNULL(ITM.AVAILABLE_SGST,'0.00')) "
				+ "AS AVAILABLE_SGST,"
				+ "TO_CHAR(IFNULL(ITM.CIF_VALUE,'0.00')) AS CIF_VALUE,"
				+ "TO_CHAR(IFNULL(ITM.CUSTOM_DUTY,'0.00')) "
				+ "AS CUSTOM_DUTY,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD1) AS USERDEFINED_FIELD1,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD2) AS USERDEFINED_FIELD2,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD3) AS USERDEFINED_FIELD3,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD4) AS USERDEFINED_FIELD4,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD5) AS USERDEFINED_FIELD5,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD6) AS USERDEFINED_FIELD6,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD7) AS USERDEFINED_FIELD7,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD8) AS USERDEFINED_FIELD8,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD9) AS USERDEFINED_FIELD9,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD10) AS USERDEFINED_FIELD10,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD11) AS USERDEFINED_FIELD11,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD12) AS USERDEFINED_FIELD12,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD13) AS USERDEFINED_FIELD13,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD14) AS USERDEFINED_FIELD14,"
				+ "TO_CHAR(ITM.USERDEFINED_FIELD15) AS USERDEFINED_FIELD15,"
				+ "TO_CHAR(IFNULL(ITM.OTHER_VALUES,'0.00')) AS OTHER_VALUES," 
				+ "TO_CHAR(ITM.ADJ_REF_NO) AS ADJ_REF_NO,"
				+ "TO_CHAR(ITM.ADJ_REF_DATE) AS ADJ_REF_DATE,"
				+ "TO_CHAR(ITM.TAXABLE_VALUE) AS TAXABLE_VALUE,"
				+ "TO_CHAR(ITM.IGST_RATE) AS IGST_RATE, "
				+ "TO_CHAR(ITM.IGST_AMT) AS IGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.CGST_RATE,'0.00')) AS CGST_RATE,"
				+ "TO_CHAR(IFNULL(ITM.CGST_AMT,'0.00')) AS CGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.SGST_RATE,'0.00')) AS SGST_RATE,"
				+ "TO_CHAR(IFNULL(ITM.SGST_AMT,'0.00')) AS SGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.CESS_RATE_SPECIFIC,'0.00')) "
				+ "AS CESS_RATE_SPECIFIC,"
				+ "TO_CHAR(IFNULL(ITM.CESS_AMT_SPECIFIC,'0.00')) "
				+ "AS CESS_AMT_SPECIFIC,"
				+ "TO_CHAR(ITM.CONTRACT_NUMBER) AS CONTRACT_NUMBER,"
				+ "TO_CHAR(IFNULL(ITM.CESS_RATE_ADVALOREM,'0.00')) "
				+ "AS CESS_RATE_ADVALOREM,"
				+ "TO_CHAR(IFNULL(ITM.AVAILABLE_IGST,'0.00')) "
				+ "AS AVAILABLE_IGST,"
				+ "TO_CHAR(IFNULL(ITM.CESS_AMT_ADVALOREM,'0.00')) "
				+ "AS CESS_AMT_ADVALOREM,"
				+ "TO_CHAR(IFNULL(ITM.STATECESS_RATE,'0.00')) "
				+ "AS STATECESS_RATE,"
				+ "TO_CHAR(IFNULL(ITM.AVAILABLE_CESS,'0.00')) "
				+ "AS AVAILABLE_CESS,"
				+ "TO_CHAR(IFNULL(ITM.STATECESS_AMT,'0.00')) "
				+ "AS STATECESS_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_TAXABLE_VALUE,'0.00')) "
				+ "AS ADJ_TAXABLE_VALUE,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_IGST_AMT,'0.00')) AS ADJ_IGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_CGST_AMT,'0.00')) AS ADJ_CGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_SGST_AMT,'0.00')) AS ADJ_SGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_CESS_AMT_ADVALOREM,'0.00')) "
				+ "AS ADJ_CESS_AMT_ADVALOREM,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_CESS_AMT_SPECIFIC,'0.00')) "
				+ "AS ADJ_CESS_AMT_SPECIFIC,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_STATECESS_AMT,'0.00')) "
				+ "AS ADJ_STATECESS_AMT,"
				+ "TRIM(',' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
				+ "TRIM(',' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) "
				+ "AS ERROR_DESCRIPTION_ASP,"
				+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "TO_CHAR(ITM.SUPPLY_TYPE) AS ITM_SUPPLY_TYPE,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_NUM) AS ITM_ORIGINAL_DOC_NUM,"
				+ "TO_CHAR(ITM.ORIGINAL_DOC_DATE) AS ITM_ORIGINAL_DOC_DATE,"
				+ "TO_CHAR(IFNULL(ITM.LINE_ITEM_AMT,'0.00')) AS ITM_INV_VALUE,"
				+ "TO_CHAR(ITM.ELIGIBILITY_INDICATOR) "
				+ "AS ITM_ELIGIBILITY_INDICATOR, "
				+ "CASE WHEN DATAORIGINTYPECODE IN ('A','AI') " 
				+ "THEN 'API' "
				+ "WHEN DATAORIGINTYPECODE IN ('E','EI') THEN 'WEBUPLOAD' "
				+ "END AS UPLOAD_SOURCE," 
				+ "HDR.CREATED_BY AS SOURCE_ID,"
				+ "FIL.FILE_NAME AS FILE_NAME,"
				+ "HDR.CREATED_ON AS ASP_DATE_TIME" + " FROM "
				+ "ANX_INWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID="
				+ "ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_DOC_ITEM ITM ON "
				+ "HDR.ID=ITM.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_INWARD_ITEM_ERROR_INFO () ERRI ON "
				+ "ITM.DOC_HEADER_ID=ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITEM_INDEX,'-1') = IFNULL(ERRI.ITEM_INDEX,'-1') "
				+ "INNER JOIN FILE_STATUS FIL ON "
				+ "HDR.FILE_ID=FIL.ID WHERE IS_ERROR=TRUE AND "
				+ "(AN_RETURN_TYPE = 'ANX2' OR AN_RETURN_TYPE IS NULL) "
				+ "AND IS_DELETE = FALSE "
				+ "AND (ITM.DOC_HEADER_ID IN (SELECT DOC_HEADER_ID "
				+ "FROM TF_INWARD_ITEM_ERROR_INFO() " 
				+ "WHERE VAL_TYPE='BV')"
				+ "OR ITM.DOC_HEADER_ID IN (SELECT DOC_HEADER_ID "
				+ "FROM TF_INWARD_HEADER_ERROR_INFO() WHERE VAL_TYPE='BV')) "
				+ buildQuery + ")" + " UNION ALL "
				+ "(SELECT HDR.ID AS HID,HDR.USER_ID,HDR.SOURCE_FILENAME,"
				+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,HDR.DIVISION,ITM.LOCATION,"
				+ "HDR.BILL_OF_ENTRY,HDR.ITC_ENTITLEMENT,"
				+ "HDR.BILL_OF_ENTRY_DATE,HDR.PURCHASE_ORGANIZATION,"
				+ "HDR.USERACCESS1,HDR.USERACCESS2,HDR.USERACCESS3,"
				+ "HDR.USERACCESS4,HDR.USERACCESS5,HDR.USERACCESS6,"
				+ "HDR.ITC_REVERSAL_IDENTIFER,HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN,HDR.POSTING_DATE,HDR.DOC_TYPE,"
				+ "HDR.SUPPLY_TYPE,HDR.DOC_NUM,HDR.DOC_DATE,"
				+ "HDR.ORIGINAL_DOC_DATE,HDR.ORIGINAL_DOC_NUM,HDR.CRDR_PRE_GST,"
				+ "HDR.CUST_GSTIN,HDR.CUST_SUPP_TYPE,HDR.DIFF_PERCENT,"
				+ "HDR.ORIG_SUPPLIER_GSTIN,HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_CODE,"
				+ "HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2,"
				+ "HDR.CUST_SUPP_ADDRESS3,HDR.CUST_SUPP_ADDRESS4,HDR.POS, "
				+ "HDR.STATE_APPLYING_CESS,HDR.SHIP_PORT_CODE,"
				+ "HDR.SECTION7_OF_IGST_FLAG,HDR.DOC_AMT,HDR.REVERSE_CHARGE,"
				+ "HDR.CLAIM_REFUND_FLAG,HDR.AUTOPOPULATE_TO_REFUND,"
				+ "HDR.EWAY_BILL_NUM,HDR.EWAY_BILL_DATE, HDR.FILE_ID,FIL.ID,"
				+ "ITM.DOC_HEADER_ID,ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,"
				+ "ITM.GLCODE_CGST, ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS, "
				+ "ITM.GLCODE_SP_CESS,ITM.GLCODE_STATE_CESS,ITM.ITM_NO, "
				+ "ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,ITM.ITM_DESCRIPTION,"
				+ "ITM.ITM_TYPE,ITM.ITM_UQC,"
				+ "TO_CHAR(IFNULL(ITM.ITM_QTY,'0.000')) AS ITM_QTY,"
				+ "ITM.CRDR_REASON,"
				+ "ITM.COMMON_SUP_INDICATOR,ITM.PURCHASE_VOUCHER_DATE,"
				+ "ITM.PURCHASE_VOUCHER_NUM,ITM.PAYMENT_DATE,"
				+ "ITM.PAYMENT_VOUCHER_NUM,ITM.CONTRACT_DATE,"
				+ "TO_CHAR(IFNULL(ITM.CONTRACT_VALUE,'0.00')) "
    			+ "AS CONTRACT_VALUE,"
    			+ "TO_CHAR(IFNULL(ITM.AVAILABLE_CGST,'0.00')) "
				+ "AS AVAILABLE_CGST,"
				+ "TO_CHAR(IFNULL(ITM.AVAILABLE_SGST,'0.00')) "
				+ "AS AVAILABLE_SGST,"
				+ "TO_CHAR(IFNULL(ITM.CIF_VALUE,'0.00')) AS CIF_VALUE,"
				+ "TO_CHAR(IFNULL(ITM.CUSTOM_DUTY,'0.00')) "
				+ "AS CUSTOM_DUTY,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
				+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
				+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
				+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
				+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
				+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
				+ "ITM.USERDEFINED_FIELD15,"
				+ "TO_CHAR(IFNULL(ITM.OTHER_VALUES,'0.00')) AS OTHER_VALUES," 
				+ "ITM.ADJ_REF_NO,"
				+ "ITM.ADJ_REF_DATE,ITM.TAXABLE_VALUE,ITM.IGST_RATE,"
				+ "ITM.IGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.CGST_RATE,'0.00')) AS CGST_RATE,"
				+ "TO_CHAR(IFNULL(ITM.CGST_AMT,'0.00')) AS CGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.SGST_RATE,'0.00')) AS SGST_RATE,"
				+ "TO_CHAR(IFNULL(ITM.SGST_AMT,'0.00')) AS SGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.CESS_RATE_SPECIFIC,'0.00')) "
				+ "AS CESS_RATE_SPECIFIC,"
				+ "TO_CHAR(IFNULL(ITM.CESS_AMT_SPECIFIC,'0.00')) "
				+ "AS CESS_AMT_SPECIFIC,"
				+ "ITM.CONTRACT_NUMBER,"
				+ "TO_CHAR(IFNULL(ITM.CESS_RATE_ADVALOREM,'0.00')) "
				+ "AS CESS_RATE_ADVALOREM,"
				+ "TO_CHAR(IFNULL(ITM.AVAILABLE_IGST,'0.00')) "
				+ "AS AVAILABLE_IGST,"
				+ "TO_CHAR(IFNULL(ITM.CESS_AMT_ADVALOREM,'0.00')) "
				+ "AS CESS_AMT_ADVALOREM,"
				+ "TO_CHAR(IFNULL(ITM.STATECESS_RATE,'0.00')) "
				+ "AS STATECESS_RATE,"
				+ "TO_CHAR(IFNULL(ITM.AVAILABLE_CESS,'0.00')) "
				+ "AS AVAILABLE_CESS,"
				+ "TO_CHAR(IFNULL(ITM.STATECESS_AMT,'0.00')) "
				+ "AS STATECESS_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_TAXABLE_VALUE,'0.00')) "
				+ "AS ADJ_TAXABLE_VALUE,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_IGST_AMT,'0.00')) AS ADJ_IGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_CGST_AMT,'0.00')) AS ADJ_CGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_SGST_AMT,'0.00')) AS ADJ_SGST_AMT,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_CESS_AMT_ADVALOREM,'0.00')) "
				+ "AS ADJ_CESS_AMT_ADVALOREM,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_CESS_AMT_SPECIFIC,'0.00')) "
				+ "AS ADJ_CESS_AMT_SPECIFIC,"
				+ "TO_CHAR(IFNULL(ITM.ADJ_STATECESS_AMT,'0.00')) "
				+ "AS ADJ_STATECESS_AMT,"
				+ "TRIM(',' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP,"
				+ "TRIM(',' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) "
				+ "AS ERROR_DESCRIPTION_ASP,"
				+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
				+ "TRIM(',' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,"
				+ "ITM.SUPPLY_TYPE,"
				+ "ITM.ORIGINAL_DOC_NUM," 
				+ "ITM.ORIGINAL_DOC_DATE,"
				+ "ITM.LINE_ITEM_AMT," 
				+ "ITM.ELIGIBILITY_INDICATOR, "
				+ "CASE WHEN DATAORIGINTYPECODE IN ('A','AI') " 
				+ "THEN 'API' "
				+ "WHEN DATAORIGINTYPECODE IN ('E','EI') "
				+ "THEN 'WEBUPLOAD' END AS UPLOAD_SOURCE,"
				+ "HDR.CREATED_BY AS SOURCE_ID," 
				+ "FIL.FILE_NAME AS FILE_NAME,"
				+ "HDR.CREATED_ON AS ASP_DATE_TIME" 
				+ " FROM "
				+ "ANX_INWARD_ERROR_HEADER HDR LEFT OUTER JOIN "
				+ "TF_INWARD_HEADER_ERROR_INFO () ERRH ON HDR.ID="
				+ "ERRH.DOC_HEADER_ID INNER JOIN ANX_INWARD_ERROR_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_INWARD_ITEM_ERROR_INFO () ERRI ON ITM.DOC_HEADER_ID="
				+ "ERRI.DOC_HEADER_ID AND "
				+ "IFNULL(ITM.ITM_NO,0) = IFNULL(ERRI.ITM_NO,0) "
				+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ "WHERE IS_ERROR='true' "
				+ "AND (AN_RETURN_TYPE = 'ANX2' OR AN_RETURN_TYPE IS NULL) "
				+ "AND (ITM.DOC_HEADER_ID IN "
				+ "(SELECT DOC_HEADER_ID FROM TF_INWARD_ITEM_ERROR_INFO() "
				+ "WHERE VAL_TYPE='SV') OR ITM.DOC_HEADER_ID IN "
				+ "(SELECT DOC_HEADER_ID FROM TF_INWARD_HEADER_ERROR_INFO() "
				+ "WHERE VAL_TYPE='SV')) " + buildQuery + ")) " 
				+ " ORDER BY DOC_NUM,ITM_NO";

	}

}
