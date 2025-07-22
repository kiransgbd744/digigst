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

import com.ey.advisory.app.data.views.client.Anx1AspEcomTotSumDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Anx1ASPEcomSummaryLevelDaoImpl")
public class Anx1ASPEcomSummaryLevelDaoImpl implements Anx1ASPEcomSavableDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ASPEcomSummaryLevelDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1EcomSavableReports(SearchCriteria criteria) {
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
				buildQuery.append(" AND HI.PROFIT_CENTER IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND HI.PLANT IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND HI.SALES_ORG IN :salesList");

			}
		}

		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND HI.DISTRIBUTION_CHANNEL IN :distList");
			}
		}

		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND HI.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND HI.USER_ACCESS1 IN :ud1List");

			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND HI.USER_ACCESS2 IN :ud2List");

			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND HI.USER_ACCESS3 IN :ud3List");

			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND HI.USER_ACCESS4 IN :ud4List");

			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND HI.USER_ACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND HI.USER_ACCESS6 IN :ud6List");

			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HI.DERIVED_RET_PERIOD = :taxperiod ");

		}

		String queryStr = createB2CSavableSummaryLevelQueryString(
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
		return list.parallelStream().map(o -> convertEcomSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private Anx1AspEcomTotSumDto convertEcomSummary(Object[] arr) {
		Anx1AspEcomTotSumDto obj = new Anx1AspEcomTotSumDto();

		obj.setReturnType(arr[0] != null ? arr[0].toString() : null);
		obj.setSgstin(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.seteComGSTIN(arr[3] != null ? arr[3].toString() : null);
		obj.setValOfSuppliesMade(arr[4] != null ? arr[4].toString() : null);
		obj.setValOfSuppliesReturned(arr[5] != null ? arr[5].toString() : null);
		obj.setNetValOfSupplies(arr[6] != null ? arr[6].toString() : null);
		obj.setIntegratedTaxAmount(arr[7] != null ? arr[7].toString() : null);
		obj.setCentralTaxAmount(arr[8] != null ? arr[8].toString() : null);
		obj.setStateUTTaxAmount(arr[9] != null ? arr[9].toString() : null);
		obj.setCessAmount(arr[10] != null ? arr[10].toString() : null);
		obj.setProfitCentre(arr[11] != null ? arr[11].toString() : null);
		obj.setPlant(arr[12] != null ? arr[12].toString() : null);
		obj.setDivision(arr[13] != null ? arr[13].toString() : null);
		obj.setLocation(arr[14] != null ? arr[14].toString() : null);
		obj.setSalesOrganisation(arr[15] != null ? arr[15].toString() : null);
		obj.setDistributionChannel(arr[16] != null ? arr[16].toString() : null);
		obj.setUserAccess1(arr[17] != null ? arr[17].toString() : null);
		obj.setUserAccess2(arr[18] != null ? arr[18].toString() : null);
		obj.setUserAccess3(arr[19] != null ? arr[19].toString() : null);
		obj.setUserAccess4(arr[20] != null ? arr[20].toString() : null);
		obj.setUserAccess5(arr[21] != null ? arr[21].toString() : null);
		obj.setUserAccess6(arr[22] != null ? arr[22].toString() : null);
		obj.setUserdefinedfield1(arr[23] != null ? arr[23].toString() : null);
		obj.setUserdefinedfield2(arr[24] != null ? arr[24].toString() : null);
		obj.setUserdefinedfield3(arr[25] != null ? arr[25].toString() : null);
		obj.setAspInformationCode(arr[26] != null ? arr[26].toString() : null);
		obj.setAspInformationDesc(arr[27] != null ? arr[27].toString() : null);
		obj.setSaveStatus(arr[28] != null ? arr[28].toString() : null);
		obj.setGstnRefID(arr[29] != null ? arr[29].toString() : null);
		obj.setGstnRefIDTime(arr[30] != null ? arr[30].toString() : null);
		obj.setGstnErrorCode(arr[34] != null ? arr[34].toString() : null);
		obj.setGstnErrorDescription(
				arr[35] != null ? arr[35].toString() : null);
		obj.setSourceID(arr[31] != null ? arr[31].toString() : null);
		obj.setFileName(arr[32] != null ? arr[32].toString() : null);
		obj.setAspDateTime(arr[33] != null ? arr[33].toString() : null);
		return obj;
	}

	private String createB2CSavableSummaryLevelQueryString(String buildQuery) {

		return "SELECT HI.RETURN_TYPE,HI.SUPPLIER_GSTIN,HI.RETURN_PERIOD,"
				+ "HI.ECOM_GSTIN, HI.ECOM_VAL_SUPMADE,HI.ECOM_VAL_SUPRET,"
				+ "HI.ECOM_NETVAL_SUP,HI.IGST_AMT,CGST_AMT,HI.SGST_AMT,"
				+ "HI.CESS_AMT, HI.PROFIT_CENTER,HI.PLANT,DIVISION,"
				+ "HI.LOCATION, HI.SALES_ORG,HI.DISTRIBUTION_CHANNEL,"
				+ "HI.USER_ACCESS1,HI.USER_ACCESS2,HI.USER_ACCESS3,"
				+ "HI.USER_ACCESS4, HI.USER_ACCESS5,HI.USER_ACCESS6,"
				+ "HI.USER_DEFINED1,HI.USER_DEFINED2,HI.USER_DEFINED3,"
				+ "ERR.INFO_ERROR_CODE_ASP AS INFO_CODE_ASP,"
				+ "ERR.INFO_ERROR_DESCRIPTION_ASP AS INFO_DESCRIPTION_ASP,"
				+ "CASE WHEN HI.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN HI.IS_SAVED_TO_GSTN = FALSE AND HI.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN HI.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HI.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS SAVE_STATUS,"
				+ "BT.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, BT.GSTN_RESP_DATE "
				+ "AS GSTIN_REF_ID_TIME, HI.CREATED_BY AS SOURCE_ID,"
				+ "FIL.FILE_NAME AS FILE_NAME, HI.CREATED_ON AS ASP_DATE_TIME,"
				+ " '' AS ERROR_CODE_ASP,'' AS ERROR_DESCRIPTION_ASP "
				+ "FROM ANX_PROCESSED_TABLE4 HI LEFT OUTER JOIN "
				+ "TF_GSTR1_ERROR_INFO () ERR ON HI.AS_ENTERED_ID= ERR.COMMON_ID "
				+ "AND HI.FILE_ID=ERR.FILE_ID AND "
				+ "ERR.INV_KEY = HI.TABLE4_INVKEY LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH BT "
				+ "ON HI.BATCH_ID = BT.ID AND HI.FILE_ID = BT.ID "
				+ "AND HI.DERIVED_RET_PERIOD = BT.DERIVED_RET_PERIOD "
				+ "WHERE HI.IS_DELETE = FALSE " + buildQuery;
	}
}
