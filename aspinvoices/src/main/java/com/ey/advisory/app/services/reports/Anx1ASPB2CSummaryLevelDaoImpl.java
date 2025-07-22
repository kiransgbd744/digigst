/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1AspB2cSumlevelDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Anx1ASPB2CSummaryLevelDaoImpl")
public class Anx1ASPB2CSummaryLevelDaoImpl
		implements Anx1ASPB2CSavableSummaryDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ASPB2CSummaryLevelDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1B2CSavableReports(SearchCriteria criteria) {
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
				buildQuery.append(" AND HI.SUPPLIER_GSTIN IN :gstinList");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTER IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND PLANT IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND SALES_ORG IN :salesList");

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
				buildQuery.append(" AND USER_ACCESS1 IN :ud1List");

			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS2 IN :ud2List");

			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS3 IN :ud3List");

			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS4 IN :ud4List");

			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND USER_ACCESS6 IN :ud6List");

			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HI.DERIVED_RET_PERIOD = :taxperiod ");

		}

		String queryStr = createB2CSavableSummaryQueryString(
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
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && !salesList.isEmpty()
					&& salesList.size() > 0) {
				q.setParameter("salesList", salesList);
			}
		}

		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && !distList.isEmpty()
					&& distList.size() > 0) {
				q.setParameter("distList", distList);
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
		return list.parallelStream().map(o -> convertB2CSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private Anx1AspB2cSumlevelDto convertB2CSummary(Object[] arr) {
		Anx1AspB2cSumlevelDto obj = new Anx1AspB2cSumlevelDto();

		obj.setReturnType(arr[0] != null ? arr[0].toString() : null);
		obj.setSgstin(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setDiffPercentage(arr[3] != null ? arr[3].toString() : null);
		obj.setSec7ofIGSTFlag(arr[4] != null ? arr[4].toString() : null);
		obj.setAutoPopltToRefund(arr[5] != null ? arr[5].toString() : null);
		obj.setPos(arr[6] != null ? arr[6].toString() : null);
		obj.setHsnOrSac(arr[7] != null ? arr[7].toString() : null);
		obj.setUom(arr[8] != null ? arr[8].toString() : null);
		obj.setQuantity(arr[9] != null ? arr[9].toString() : null);
		obj.setRate(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxableValue(arr[11] != null ? arr[11].toString() : null);
		obj.setIntegratedTaxAmount(arr[12] != null ? arr[12].toString() : null);
		obj.setCentralTaxAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setStateUTTaxAmount(arr[14] != null ? arr[14].toString() : null);
		obj.setCessAmount(arr[15] != null ? arr[15].toString() : null);
		obj.setTotalValue(arr[16] != null ? arr[16].toString() : null);
		obj.setStateAplyCess(arr[17] != null ? arr[17].toString() : null);
		obj.setStateCessRate(arr[18] != null ? arr[18].toString() : null);
		obj.setStateCessAmount(arr[19] != null ? arr[19].toString() : null);
		obj.setTcsFlag(arr[20] != null ? arr[20].toString() : null);
		obj.seteComGSTIN(arr[21] != null ? arr[21].toString() : null);
		obj.seteComValOfSuppliesMade(
				arr[22] != null ? arr[22].toString() : null);
		obj.seteComValOfSuppliesReturned(
				arr[23] != null ? arr[23].toString() : null);
		obj.seteComNetValOfSupplies(
				arr[24] != null ? arr[24].toString() : null);
		obj.settCSAmount(arr[25] != null ? arr[25].toString() : null);
		obj.setProfitCentre(arr[26] != null ? arr[26].toString() : null);
		obj.setPlant(arr[27] != null ? arr[27].toString() : null);
		obj.setDivision(arr[28] != null ? arr[28].toString() : null);
		obj.setLocation(arr[29] != null ? arr[29].toString() : null);
		obj.setSalesOrganisation(arr[30] != null ? arr[30].toString() : null);
		obj.setDistributionChannel(arr[31] != null ? arr[31].toString() : null);
		obj.setUserAccess1(arr[32] != null ? arr[32].toString() : null);
		obj.setUserAccess2(arr[33] != null ? arr[33].toString() : null);
		obj.setUserAccess3(arr[34] != null ? arr[34].toString() : null);
		obj.setUserAccess4(arr[35] != null ? arr[35].toString() : null);
		obj.setUserAccess5(arr[36] != null ? arr[36].toString() : null);
		obj.setUserAccess6(arr[37] != null ? arr[37].toString() : null);
		obj.setUserdefinedfield1(arr[38] != null ? arr[38].toString() : null);
		obj.setUserdefinedfield2(arr[39] != null ? arr[39].toString() : null);
		obj.setUserdefinedfield3(arr[40] != null ? arr[40].toString() : null);
		obj.setAspInformationCode(arr[41] != null ? arr[41].toString() : null);
		obj.setAspInformationDesc(arr[42] != null ? arr[42].toString() : null);
		obj.setSaveStatus(arr[43] != null ? arr[43].toString() : null);
		obj.setGstnRefID(arr[44] != null ? arr[44].toString() : null);
		obj.setGstnRefIDTime(arr[45] != null ? arr[45].toString() : null);
		obj.setGstnErrorCode(arr[46] != null ? arr[46].toString() : null);
		obj.setGstnErrorDescription(
				arr[47] != null ? arr[47].toString() : null);
		// upload source
		obj.setSourceID(arr[49] != null ? arr[49].toString() : null);
		obj.setFileName(arr[50] != null ? arr[50].toString() : null);
		obj.setAspDateTime(arr[51] != null ? arr[51].toString() : null);

		return obj;
	}

	private String createB2CSavableSummaryQueryString(String buildQuery) {

		return "SELECT HI.RETURN_TYPE,HI.SUPPLIER_GSTIN,"
				+ "HI.RETURN_PERIOD,DIFF_PERCENT,SEC7_OF_IGST_FLAG,"
				+ "AUTOPOPULATE_TO_REFUND,POS,HSNORSAC,UOM,QUANTITY,TAX_RATE,"
				+ "TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "TOTAL_VALUE, STATE_APPLYING_CESS,STATE_CESS_RATE,STATE_CESS_AMT,"
				+ "TCS_FLAG,ECOM_GSTIN,ECOM_VAL_SUPMADE,ECOM_VAL_SUPRET,"
				+ "ECOM_NETVAL_SUP,TCS_AMT,PROFIT_CENTER,PLANT,DIVISION,LOCATION,"
				+ "SALES_ORG,DISTRIBUTION_CHANNEL,USER_ACCESS1,USER_ACCESS2,"
				+ "USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,"
				+ "USER_DEFINED1, USER_DEFINED2,USER_DEFINED3,"
				+ "INFO_ERROR_CODE_ASP INFO_CODE_ASP,"
				+ "INFO_ERROR_DESCRIPTION_ASP INFO_DESCRIPTION_ASP,"
				+ "CASE WHEN HI.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN HI.IS_SAVED_TO_GSTN = FALSE AND HI.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN HI.IS_SAVED_TO_GSTN = FALSE "
				+ "AND HI.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS "
				+ "SAVE_STATUS, BT.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,BT.CREATED_ON "
				+ "AS GSTIN_REF_ID_TIME, '' AS ERROR_CODE_ASP,'' "
				+ "AS ERROR_DESCRIPTION_ASP,FIL.SOURCE AS UPLOAD_SOURCE,"
				+ "FIL.CREATED_BY AS SOURCE_ID,FIL.FILE_NAME,HI.CREATED_ON "
				+ "AS ASP_DATE_TIME FROM ANX_PROCESSED_B2C HI "
				+ "LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR ON "
				+ "HI.AS_ENTERED_ID = ERR.COMMON_ID AND HI.FILE_ID = ERR.FILE_ID "
				+ "AND ERR.INV_KEY = HI.B2C_INVKEY LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HI.FILE_ID=FIL.ID AND "
				+ "ERR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH "
				+ "BT ON HI.BATCH_ID = BT.ID AND HI.FILE_ID = BT.ID "
				+ "AND HI.DERIVED_RET_PERIOD = BT.DERIVED_RET_PERIOD "
				+ "WHERE HI.IS_DELETE = FALSE " + buildQuery;
	}
}
