package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1AspConsolidatedErrorDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Anx1ConsolidatedErrorDaoImpl")
public class Anx1ConsolidatedErrorDaoImpl
		implements Anx1ConsolidatedErrorReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1AspInwardProcessedUploadedDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1ConsolidatedErrorDaoReport(
			SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
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

		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
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
		return list.parallelStream().map(o -> convertErrorProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1AspConsolidatedErrorDto convertErrorProcessed(Object[] arr) {
		Anx1AspConsolidatedErrorDto obj = new Anx1AspConsolidatedErrorDto();

		obj.setUserID(arr[0] != null ? arr[0].toString() : null);
		obj.setSourceId(arr[1] != null ? arr[1].toString() : null);
		obj.setProfitCentre(arr[2] != null ? arr[2].toString() : null);
		obj.setPlant(arr[3] != null ? arr[3].toString() : null);
		obj.setDivision(arr[4] != null ? arr[4].toString() : null);
		obj.setLocation(arr[5] != null ? arr[5].toString() : null);
		obj.setSalesOrganisation(arr[6] != null ? arr[6].toString() : null);
		obj.setDistributionChannel(arr[7] != null ? arr[7].toString() : null);
		obj.setUserAccess1(arr[8] != null ? arr[8].toString() : null);
		obj.setUserAccess2(arr[9] != null ? arr[9].toString() : null);
		obj.setUserAccess3(arr[10] != null ? arr[10].toString() : null);
		obj.setUserAccess4(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess5(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess6(arr[13] != null ? arr[13].toString() : null);
		obj.setgLCodeTaxableValue(arr[14] != null ? arr[14].toString() : null);
		obj.setgLCodeIGST(arr[15] != null ? arr[15].toString() : null);
		obj.setgLCodeCGST(arr[16] != null ? arr[16].toString() : null);
		obj.setgLCodeSGST(arr[17] != null ? arr[17].toString() : null);
		obj.setgLCodeAdvlormCess(arr[18] != null ? arr[18].toString() : null);
		obj.setgLCodeSpecificCess(arr[19] != null ? arr[19].toString() : null);
		obj.setgLCodeStateCess(arr[20] != null ? arr[20].toString() : null);
		obj.setReturnPeriod(arr[21] != null ? arr[21].toString() : null);
		obj.setSgstin(arr[22] != null ? arr[22].toString() : null);
		obj.setDocType(arr[23] != null ? arr[23].toString() : null);
		obj.setSupplyType(arr[24] != null ? arr[24].toString() : null);
		obj.setDocNum(arr[25] != null ? arr[25].toString() : null);
		obj.setDocDate(arr[26] != null ? arr[26].toString() : null);
		obj.setOrgDocType(arr[27] != null ? arr[27].toString() : null);
		obj.setOrgDocNum(arr[28] != null ? arr[28].toString() : null);
		obj.setOrgDocDate(arr[29] != null ? arr[29].toString() : null);
		obj.setCrdrPreGST(arr[30] != null ? arr[30].toString() : null);
		obj.setItemNumber(arr[31] != null ? arr[31].toString() : null);
		obj.setRecpGSTIN(arr[32] != null ? arr[32].toString() : null);
		obj.setRecpType(arr[33] != null ? arr[33].toString() : null);
		obj.setDiffPerFlag(arr[34] != null ? arr[34].toString() : null);
		obj.setOrgRecpGSTIN(arr[35] != null ? arr[35].toString() : null);
		obj.setRecpName(arr[36] != null ? arr[36].toString() : null);
		obj.setRecpCode(arr[37] != null ? arr[37].toString() : null);
		obj.setRecpAdd1(arr[38] != null ? arr[38].toString() : null);
		obj.setRecpAdd2(arr[39] != null ? arr[39].toString() : null);
		obj.setRecpAdd3(arr[40] != null ? arr[40].toString() : null);
		obj.setRecpAdd4(arr[41] != null ? arr[41].toString() : null);
		obj.setBillToState(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToState(arr[43] != null ? arr[43].toString() : null);
		obj.setPos(arr[44] != null ? arr[44].toString() : null);
		obj.setStateAplyCess(arr[45] != null ? arr[45].toString() : null);
		obj.setPortCode(arr[46] != null ? arr[46].toString() : null);
		obj.setShippingBillNum(arr[47] != null ? arr[47].toString() : null);
		obj.setShippingBillDate(arr[48] != null ? arr[48].toString() : null);
		obj.setFob(arr[49] != null ? arr[49].toString() : null);
		obj.setExportDuty(arr[50] != null ? arr[50].toString() : null);
		obj.setHsnOrSac(arr[51] != null ? arr[51].toString() : null);
		obj.setProductCode(arr[52] != null ? arr[52].toString() : null);
		obj.setProductDesc(arr[53] != null ? arr[53].toString() : null);
		obj.setCategoryOfProduct(arr[54] != null ? arr[54].toString() : null);
		obj.setUom(arr[55] != null ? arr[55].toString() : null);
		obj.setQuantity(arr[56] != null ? arr[56].toString() : null);
		obj.setSec7ofIGSTFlag(arr[57] != null ? arr[57].toString() : null);
		obj.setTaxableValue(arr[58] != null ? arr[58].toString() : null);
		obj.setIntegTaxRate(arr[59] != null ? arr[59].toString() : null);
		obj.setIntegTaxAmt(arr[60] != null ? arr[60].toString() : null);
		obj.setCentralTaxRate(arr[61] != null ? arr[61].toString() : null);
		obj.setCentralTaxAmt(arr[62] != null ? arr[62].toString() : null);
		obj.setStateUTTaxRate(arr[63] != null ? arr[63].toString() : null);
		obj.setStateUTTaxAmt(arr[64] != null ? arr[64].toString() : null);
		obj.setAdvmCessRate(arr[65] != null ? arr[65].toString() : null);
		obj.setAdvmCessAmt(arr[66] != null ? arr[66].toString() : null);
		obj.setSpecfcCessRate(arr[67] != null ? arr[67].toString() : null);
		obj.setSpecfcCessAmt(arr[68] != null ? arr[68].toString() : null);
		obj.setStateCessRate(arr[69] != null ? arr[69].toString() : null);
		obj.setStateCessAmt(arr[70] != null ? arr[70].toString() : null);
		obj.setOtherValue(arr[71] != null ? arr[71].toString() : null);
		obj.setInvoiceValue(arr[72] != null ? arr[72].toString() : null);
		obj.setAdjRefNo(arr[73] != null ? arr[73].toString() : null);
		obj.setAdjRefDate(arr[74] != null ? arr[74].toString() : null);
		obj.setTaxableValueAdj(arr[75] != null ? arr[75].toString() : null);
		obj.setIntegdTaxAmtAdj(arr[76] != null ? arr[76].toString() : null);
		obj.setCentralTaxAmtAdj(arr[77] != null ? arr[77].toString() : null);
		obj.setStateUTTaxAmtAdj(arr[78] != null ? arr[78].toString() : null);
		obj.setAdvlmCessAmtAdj(arr[79] != null ? arr[79].toString() : null);
		obj.setSpecfCessAmtAdj(arr[80] != null ? arr[80].toString() : null);
		obj.setStateCessAmtAdj(arr[81] != null ? arr[81].toString() : null);
		obj.setReverseChargeFlag(arr[82] != null ? arr[82].toString() : null);
		obj.setTcsFlag(arr[83] != null ? arr[83].toString() : null);
		obj.seteComGSTIN(arr[84] != null ? arr[84].toString() : null);
		obj.setTcsAmt(arr[85] != null ? arr[85].toString() : null);
		obj.setItcFlag(arr[86] != null ? arr[86].toString() : null);
		obj.setClaimRefndFlag(arr[87] != null ? arr[87].toString() : null);
		obj.setAutoPopltToRefund(arr[88] != null ? arr[88].toString() : null);
		obj.setReasonForCrDbNote(arr[89] != null ? arr[89].toString() : null);
		obj.setAccVoucherNum(arr[90] != null ? arr[90].toString() : null);
		obj.setAccVoucherDate(arr[91] != null ? arr[91].toString() : null);
		obj.setUserDefField1(arr[92] != null ? arr[92].toString() : null);
		obj.setUserDefField2(arr[93] != null ? arr[93].toString() : null);
		obj.setUserDefField3(arr[94] != null ? arr[94].toString() : null);
		obj.setUserDefField4(arr[95] != null ? arr[95].toString() : null);
		obj.setUserDefField5(arr[96] != null ? arr[96].toString() : null);
		obj.setUserDefField6(arr[97] != null ? arr[97].toString() : null);
		obj.setUserDefField7(arr[98] != null ? arr[98].toString() : null);
		obj.setUserDefField8(arr[99] != null ? arr[99].toString() : null);
		obj.setUserDefField9(arr[100] != null ? arr[100].toString() : null);
		obj.setUserDefField10(arr[101] != null ? arr[101].toString() : null);
		obj.setUserDefField11(arr[102] != null ? arr[102].toString() : null);
		obj.setUserDefField12(arr[103] != null ? arr[103].toString() : null);
		obj.setUserDefField13(arr[104] != null ? arr[104].toString() : null);
		obj.setUserDefField14(arr[105] != null ? arr[105].toString() : null);
		obj.setUserDefField15(arr[106] != null ? arr[106].toString() : null);
		obj.setEwaybillNum(arr[107] != null ? arr[107].toString() : null);
		obj.setEwaybillDate(arr[108] != null ? arr[108].toString() : null);
		obj.setAspErrorCode(arr[109] != null ? arr[109].toString() : null);
		obj.setAspErrorDesc(arr[110] != null ? arr[110].toString() : null);
		obj.setAspInfoId(arr[111] != null ? arr[111].toString() : null);
		obj.setAspInfoDesc(arr[112] != null ? arr[112].toString() : null);
		obj.setUploadSource(arr[113] != null ? arr[113].toString() : null);
		obj.setSourceId(arr[114] != null ? arr[114].toString() : null);
		obj.setFileName(arr[115] != null ? arr[115].toString() : null);
		obj.setAspDateTime(arr[116] != null ? arr[116].toString() : null);

		return obj;
	}

	private String createErrorQueryString(String buildQuery) {

		return "SELECT TO_CHAR(HDR.USER_ID) AS USER_ID, "
				+ "TO_CHAR(HDR.SOURCE_FILENAME) AS SOURCE_FILENAME,"
				+ " TO_CHAR(ITM.PROFIT_CENTRE) AS PROFIT_CENTRE,"
				+ " TO_CHAR(ITM.PLANT_CODE) AS PLANT_CODE, "
				+ "TO_CHAR(HDR.DIVISION) AS DIVISION, TO_CHAR(ITM.LOCATION)"
				+ " AS LOCATION, TO_CHAR(HDR.SALES_ORGANIZATION) AS "
				+ "SALES_ORGANIZATION, TO_CHAR(HDR.DISTRIBUTION_CHANNEL)"
				+ " AS DISTRIBUTION_CHANNEL, TO_CHAR(HDR.USERACCESS1) AS "
				+ "USERACCESS1, TO_CHAR(HDR.USERACCESS2) AS USERACCESS2, "
				+ "TO_CHAR(HDR.USERACCESS3) AS USERACCESS3, TO_CHAR(HDR.USERACCESS4)"
				+ " AS USERACCESS4, TO_CHAR(HDR.USERACCESS5) AS USERACCESS5,"
				+ " TO_CHAR(HDR.USERACCESS6) AS USERACCESS6, "
				+ "TO_CHAR(ITM.GLCODE_TAXABLEVALUE) AS GLCODE_TAXABLEVALUE,"
				+ " TO_CHAR(ITM.GLCODE_IGST) AS GLCODE_IGST, "
				+ "TO_CHAR(ITM.GLCODE_CGST) AS GLCODE_CGST,"
				+ " TO_CHAR(ITM.GLCODE_SGST) AS GLCODE_SGST, "
				+ "TO_CHAR(ITM.GLCODE_ADV_CESS) AS GLCODE_ADV_CESS, "
				+ "TO_CHAR(ITM.GLCODE_SP_CESS) AS GLCODE_SP_CESS, "
				+ "TO_CHAR(ITM.GLCODE_STATE_CESS) AS GLCODE_STATE_CESS, "
				+ "TO_CHAR(HDR.RETURN_PERIOD) AS RETURN_PERIOD ,"
				+ " TO_CHAR(HDR.SUPPLIER_GSTIN) AS SUPPLIER_GSTIN,"
				+ "TO_CHAR(HDR.DOC_TYPE) AS DOC_TYPE, TO_CHAR(HDR.SUPPLY_TYPE)"
				+ " AS SUPPLY_TYPE, TO_CHAR(HDR.DOC_NUM) AS DOC_NUM, "
				+ "TO_CHAR(HDR.DOC_DATE) AS DOC_DATE, "
				+ "TO_CHAR(HDR.ORIGINAL_DOC_TYPE) AS ORIGINAL_DOC_TYPE,"
				+ " TO_CHAR(HDR.ORIGINAL_DOC_NUM) AS ORIGINAL_DOC_NUM,"
				+ " TO_CHAR(HDR.ORIGINAL_DOC_DATE) AS ORIGINAL_DOC_DATE, "
				+ "TO_CHAR(HDR.CRDR_PRE_GST) AS CRDR_PRE_GST,"
				+ " TO_CHAR(ITM.ITM_NO) AS ITM_NO , TO_CHAR(HDR.CUST_GSTIN)"
				+ " AS CUST_GSTIN, TO_CHAR(HDR.CUST_SUPP_TYPE) "
				+ "AS CUST_SUPP_TYPE, TO_CHAR(HDR.DIFF_PERCENT) "
				+ "AS DIFF_PERCENT, TO_CHAR(HDR.ORIGINAL_CUST_GSTIN)"
				+ " AS ORIGINAL_CUST_GSTIN, TO_CHAR(HDR.CUST_SUPP_NAME)"
				+ " AS CUST_SUPP_NAME, TO_CHAR(HDR.CUST_SUPP_CODE) AS "
				+ "CUST_SUPP_CODE, TO_CHAR(HDR.CUST_SUPP_ADDRESS1) AS "
				+ "CUST_SUPP_ADDRESS1, TO_CHAR(HDR.CUST_SUPP_ADDRESS2) "
				+ "AS CUST_SUPP_ADDRESS2, TO_CHAR(HDR.CUST_SUPP_ADDRESS3)"
				+ " AS CUST_SUPP_ADDRESS3, TO_CHAR(HDR.CUST_SUPP_ADDRESS4) "
				+ "AS CUST_SUPP_ADDRESS4, TO_CHAR(HDR.BILL_TO_STATE) AS "
				+ "BILL_TO_STATE, TO_CHAR(HDR.SHIP_TO_STATE) AS SHIP_TO_STATE, "
				+ "TO_CHAR(HDR.POS) AS POS, TO_CHAR(HDR.STATE_APPLYING_CESS)"
				+ " AS STATE_APPLYING_CESS, TO_CHAR(HDR.SHIP_PORT_CODE) AS"
				+ " SHIP_PORT_CODE, TO_CHAR(HDR.SHIP_BILL_NUM) AS SHIP_BILL_NUM, "
				+ "TO_CHAR(HDR.SHIP_BILL_DATE) AS SHIP_BILL_DATE,"
				+ " TO_CHAR(ITM.FOB) AS FOB,TO_CHAR(ITM.EXPORT_DUTY) AS"
				+ " EXPORT_DUTY, TO_CHAR(ITM.ITM_HSNSAC) AS ITM_HSNSAC, "
				+ "TO_CHAR(ITM.PRODUCT_CODE) AS PRODUCT_CODE,"
				+ " TO_CHAR(ITM.ITM_DESCRIPTION) AS ITM_DESCRIPTION, "
				+ "'0'AS CategoryOfProduct, TO_CHAR(ITM.ITM_UQC) AS ITM_UQC, "
				+ "TO_CHAR(ITM.ITM_QTY) AS ITM_QTY,"
				+ " TO_CHAR(HDR.SECTION7_OF_IGST_FLAG) AS SECTION7_OF_IGST_FLAG,"
				+ " TO_CHAR(ITM.TAXABLE_VALUE) AS TAXABLE_VALUE, "
				+ "TO_CHAR(ITM.IGST_RATE) AS IGST_RATE, TO_CHAR(ITM.IGST_AMT) "
				+ "AS IGST_AMT, TO_CHAR(ITM.CGST_RATE) AS CGST_RATE,"
				+ " TO_CHAR(ITM.CGST_AMT) AS CGST_AMT, TO_CHAR(ITM.SGST_RATE) "
				+ "AS SGST_RATE, TO_CHAR(ITM.SGST_AMT) AS SGST_AMT, "
				+ "TO_CHAR(ITM.CESS_RATE_ADVALOREM) AS CESS_RATE_ADVALOREM,"
				+ " TO_CHAR(ITM.CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM, "
				+ "TO_CHAR(ITM.CESS_RATE_SPECIFIC) AS CESS_RATE_SPECIFIC, "
				+ "TO_CHAR(ITM.CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC,"
				+ " TO_CHAR(ITM.STATECESS_RATE) AS STATECESS_RATE, "
				+ "TO_CHAR(ITM.STATECESS_AMT) AS STATECESS_AMT,"
				+ " TO_CHAR(ITM.OTHER_VALUES) AS OTHER_VALUES,"
				+ " TO_CHAR(HDR.DOC_AMT) AS INV_VALUE, ITM.ADJ_REF_NO, "
				+ "TO_CHAR(ITM.ADJ_REF_DATE) AS ADJ_REF_DATE, "
				+ "TO_CHAR(ITM.ADJ_TAXABLE_VALUE) AS ADJ_TAXABLE_VALUE, "
				+ "TO_CHAR(ITM.ADJ_IGST_AMT) AS ADJ_IGST_AMT,"
				+ " TO_CHAR(ITM.ADJ_CGST_AMT) AS ADJ_CGST_AMT,"
				+ " TO_CHAR(ITM.ADJ_SGST_AMT) AS ADJ_SGST_AMT, "
				+ "TO_CHAR(ITM. ADJ_CESS_AMT_ADVALOREM) AS ADJ_CESS_AMT_ADVALOREM, "
				+ "TO_CHAR(ITM.ADJ_CESS_AMT_SPECIFIC) AS ADJ_CESS_AMT_SPECIFIC,"
				+ " TO_CHAR(ITM.ADJ_STATECESS_AMT) AS ADJ_STATECESS_AMT,"
				+ " TO_CHAR(HDR.REVERSE_CHARGE) AS REVERSE_CHARGE, "
				+ "TO_CHAR(HDR.TCS_FLAG) AS TCS_FLAG, TO_CHAR(HDR.ECOM_GSTIN)"
				+ " AS ECOM_GSTIN, TO_CHAR(ITM.TCS_AMT) AS TCS_AMT, "
				+ "TO_CHAR(ITM.ITC_FLAG) AS ITC_FLAG, TO_CHAR(HDR.CLAIM_REFUND_FLAG)"
				+ " AS CLAIM_REFUND_FLAG, TO_CHAR(HDR.AUTOPOPULATE_TO_REFUND)"
				+ " AS AUTOPOPULATE_TO_REFUND, TO_CHAR(ITM.CRDR_REASON) AS"
				+ " CRDR_REASON, TO_CHAR(HDR.ACCOUNTING_VOUCHER_NUM) AS"
				+ " ACCOUNTING_VOUCHER_NUM, TO_CHAR(HDR.ACCOUNTING_VOUCHER_DATE)"
				+ " AS ACCOUNTING_VOUCHER_DATE, TO_CHAR(ITM.USERDEFINED_FIELD1) "
				+ "AS USERDEFINED_FIELD1, TO_CHAR(ITM.USERDEFINED_FIELD2) AS "
				+ "USERDEFINED_FIELD2, TO_CHAR(ITM.USERDEFINED_FIELD3) AS"
				+ " USERDEFINED_FIELD3, TO_CHAR(ITM.USERDEFINED_FIELD4) AS "
				+ "USERDEFINED_FIELD4, TO_CHAR(ITM.USERDEFINED_FIELD5) AS "
				+ "USERDEFINED_FIELD5, TO_CHAR(ITM.USERDEFINED_FIELD6) AS "
				+ "USERDEFINED_FIELD6, TO_CHAR(ITM.USERDEFINED_FIELD7) AS"
				+ " USERDEFINED_FIELD7, TO_CHAR(ITM.USERDEFINED_FIELD8) AS "
				+ "USERDEFINED_FIELD8, TO_CHAR(ITM.USERDEFINED_FIELD9) AS "
				+ "USERDEFINED_FIELD9, TO_CHAR(ITM.USERDEFINED_FIELD10) AS "
				+ "USERDEFINED_FIELD10, TO_CHAR(ITM.USERDEFINED_FIELD11) AS "
				+ "USERDEFINED_FIELD11, TO_CHAR(ITM.USERDEFINED_FIELD12) AS "
				+ "USERDEFINED_FIELD12, TO_CHAR(ITM.USERDEFINED_FIELD13) AS "
				+ "USERDEFINED_FIELD13, TO_CHAR(ITM.USERDEFINED_FIELD14) AS"
				+ " USERDEFINED_FIELD14, TO_CHAR(ITM.USERDEFINED_FIELD15) AS "
				+ "USERDEFINED_FIELD15, TO_CHAR(HDR.EWAY_BILL_NUM) AS "
				+ "EWAY_BILL_NUM, TO_CHAR(HDR.EWAY_BILL_DATE) AS EWAY_BILL_DATE,"
				+ " IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| IFNULL"
				+ "(ERRI.ERROR_CODE_ASP,'') AS ERROR_CODE_ASP, IFNULL"
				+ "(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| IFNULL"
				+ "(ERRI.ERROR_DESCRIPTION_ASP,'') AS ERROR_DESCRIPTION_ASP,"
				+ " IFNULL(ERRI.INFO_ERROR_CODE_ASP,'' ) AS INFO_ERROR_CODE_ASP,"
				+ " IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'') AS "
				+ "INFO_ERROR_DESCRIPTION_ASP, CASE WHEN "
				+ "HDR.DATAORIGINTYPECODE = 'A' OR HDR.DATAORIGINTYPECODE"
				+ " = 'AI' THEN 'API' WHEN HDR.DATAORIGINTYPECODE = 'E'"
				+ " OR HDR.DATAORIGINTYPECODE = 'EI' THEN 'WEB UPLOAD' END "
				+ "UPLOAD_SOURCE, HDR.CREATED_BY SOURCE_ID,FIL.FILE_NAME,"
				+ "HDR.CREATED_ON ASP_DATE_TIME "
				+ "FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ buildQuery
				+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID  "
				+ " LEFT OUTER JOIN TF_OUTWARD_HEADER_ERROR_INFO() "
				+ "ERRH ON HDR.ID = ERRH.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON ITM.DOC_HEADER_ID = "
				+ "ERRI.DOC_HEADER_ID AND IFNULL(ITM.ITEM_INDEX,'-1') = "
				+ "IFNULL(ERRI.ITEM_INDEX,'-1') WHERE HDR.IS_ERROR = TRUE AND"
				+ " ITM.DOC_HEADER_ID IN (SELECT DOC_HEADER_ID FROM "
				+ "TF_OUTWARD_ITEM_ERROR_INFO() ) OR ITM.DOC_HEADER_ID IN "
				+ "(SELECT DOC_HEADER_ID FROM TF_OUTWARD_HEADER_ERROR_INFO() )"
				+ "UNION ALL SELECT HDR.USER_ID,"
				+ "HDR.SOURCE_FILENAME,ITM.PROFIT_CENTRE,ITM.PLANT_CODE,"
				+ " HDR.DIVISION,ITM.LOCATION,HDR.SALES_ORGANIZATION,"
				+ "HDR.DISTRIBUTION_CHANNEL, HDR.USERACCESS1, HDR.USERACCESS2,"
				+ "HDR.USERACCESS3,HDR.USERACCESS4,HDR.USERACCESS5, "
				+ "HDR.USERACCESS6,ITM.GLCODE_TAXABLEVALUE, ITM.GLCODE_IGST,"
				+ "ITM.GLCODE_CGST,ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,"
				+ "ITM.GLCODE_SP_CESS, ITM.GLCODE_STATE_CESS,HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN,HDR.DOC_TYPE,HDR.SUPPLY_TYPE, "
				+ "HDR.DOC_NUM,HDR.DOC_DATE,HDR.ORIGINAL_DOC_TYPE,"
				+ "HDR.ORIGINAL_DOC_NUM,HDR.ORIGINAL_DOC_DATE, HDR.CRDR_PRE_GST,"
				+ "ITM.ITM_NO,HDR.CUST_GSTIN,HDR.CUST_SUPP_TYPE,HDR.DIFF_PERCENT,"
				+ "HDR.ORIGINAL_CUST_GSTIN, HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_CODE,"
				+ "HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2, "
				+ "HDR.CUST_SUPP_ADDRESS3,HDR.CUST_SUPP_ADDRESS4,HDR.BILL_TO_STATE,"
				+ "HDR.SHIP_TO_STATE, HDR.POS,HDR.STATE_APPLYING_CESS,"
				+ "HDR.SHIP_PORT_CODE,HDR.SHIP_BILL_NUM,HDR.SHIP_BILL_DATE,"
				+ " ITM.FOB,ITM.EXPORT_DUTY,ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,"
				+ "ITM.ITM_DESCRIPTION, '0' AS CATEGORY_OF_PRODUCT,ITM.ITM_UQC,"
				+ "ITM.ITM_QTY,HDR.SECTION7_OF_IGST_FLAG, ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_RATE,ITM.IGST_AMT,ITM.CGST_RATE, ITM.CGST_AMT,"
				+ "ITM.SGST_RATE,ITM.SGST_AMT,ITM.CESS_RATE_ADVALOREM,"
				+ "ITM.CESS_AMT_ADVALOREM, ITM.CESS_RATE_SPECIFIC,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.STATECESS_RATE,ITM.STATECESS_AMT, "
				+ "ITM.OTHER_VALUES,HDR.DOC_AMT AS INV_VALUE, "
				+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE, ITM.ADJ_TAXABLE_VALUE,"
				+ "ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,ITM.ADJ_SGST_AMT,"
				+ "ITM.ADJ_CESS_AMT_ADVALOREM, ITM.ADJ_CESS_AMT_SPECIFIC,"
				+ "ITM.ADJ_STATECESS_AMT, HDR.DOC_AMT,HDR.REVERSE_CHARGE,"
				+ "HDR.TCS_FLAG,HDR.ECOM_GSTIN,ITM.TCS_AMT,ITM.ITC_FLAG, "
				+ "HDR.CLAIM_REFUND_FLAG,HDR.AUTOPOPULATE_TO_REFUND,"
				+ "HDR.ACCOUNTING_VOUCHER_NUM, HDR.ACCOUNTING_VOUCHER_DATE,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4, "
				+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
				+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
				+ " ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
				+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12, "
				+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
				+ "ITM.USERDEFINED_FIELD15, HDR.EWAY_BILL_NUM,HDR.EWAY_BILL_DATE,"
				+ " IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_ASP,'') AS ERROR_CODE_ASP,"
				+ " IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','||"
				+ " IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'') AS ERROR_DESCRIPTION_ASP,"
				+ " IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'' ) AS INFO_ERROR_CODE_ASP,"
				+ " IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'') AS "
				+ "INFO_ERROR_DESCRIPTION_ASP,CASE WHEN HDR.DATAORIGINTYPECODE = "
				+ "'A' OR HDR.DATAORIGINTYPECODE = 'AI' THEN 'API'"
				+ " WHEN HDR.DATAORIGINTYPECODE = 'E' OR HDR.DATAORIGINTYPECODE = "
				+ "'EI' THEN 'WEB UPLOAD' END UPLOAD_SOURCE, HDR.CREATED_BY "
				+ "SOURCE_ID,FIL.FILE_NAME,HDR.CREATED_ON ASP_DATE_TIME"
				+ "  FROM ANX_OUTWARD_ERR_HEADER HDR INNER JOIN "
				+ "ANX_OUTWARD_ERR_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID"
				+ buildQuery
				+ " INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+ " LEFT OUTER JOIN TF_OUTWARD_HEADER_ERROR_INFO ()"
				+ " ERRH ON HDR.ID = ERRH.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_OUTWARD_ITEM_ERROR_INFO() ERRI ON ITM.DOC_HEADER_ID = "
				+ "ERRI.DOC_HEADER_ID AND IFNULL(ITM.ITEM_INDEX,'-1') = "
				+ "IFNULL(ERRI.ITEM_INDEX,'-1') WHERE HDR.IS_ERROR = 'true' "
				+ "AND ITM.DOC_HEADER_ID IN (SELECT DOC_HEADER_ID FROM "
				+ "TF_OUTWARD_ITEM_ERROR_INFO() ) OR ITM.DOC_HEADER_ID IN "
				+ "(SELECT DOC_HEADER_ID FROM TF_OUTWARD_HEADER_ERROR_INFO() )";

	}

}
