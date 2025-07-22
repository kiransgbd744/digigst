package com.ey.advisory.app.services.reports.gstr1a;

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

import com.ey.advisory.app.data.views.client.NilNonProcessedRecordsDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Gstr1AAspNilRatedSummaryUploadDaoImpl")
public class Gstr1AAspNilRatedSummaryUploadDaoImpl
		implements Gstr1AOutwardVerticalProcessNilDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AAspNilRatedSummaryUploadDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1RSReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
		// String taxDocType = request.getTaxDocType();

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
				buildQuery.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		/*
		 * if (ProfitCenter != null && !ProfitCenter.isEmpty()) { if (pcList !=
		 * null && pcList.size() > 0) {
		 * buildQuery.append(" AND NEN.PROFIT_CENTRE IN :pcList"); } } if (plant
		 * != null && !plant.isEmpty()) { if (plantList != null &&
		 * plantList.size() > 0) {
		 * buildQuery.append(" AND NEN.PLANT_CODE IN :plantList"); } } if (sales
		 * != null && !sales.isEmpty()) { if (salesList != null &&
		 * salesList.size() > 0) {
		 * buildQuery.append(" AND NEN.SALES_ORGANIZATION IN :salesList"); } }
		 * if (distChannel != null && !distChannel.isEmpty()) { if (distList !=
		 * null && distList.size() > 0) {
		 * buildQuery.append(" AND NEN.DISTRIBUTION_CHANNEL IN :distList"); } }
		 * if (division != null && !division.isEmpty()) { if (divisionList !=
		 * null && divisionList.size() > 0) {
		 * buildQuery.append(" AND NEN.DIVISION IN :divisionList"); } } if
		 * (location != null && !location.isEmpty()) { if (locationList != null
		 * && locationList.size() > 0) {
		 * buildQuery.append(" AND NEN.LOCATION IN :locationList"); } } if (ud1
		 * != null && !ud1.isEmpty()) { if (ud1List != null && ud1List.size() >
		 * 0) { buildQuery.append(" AND NEN.USERACCESS1 IN :ud1List"); } } if
		 * (ud2 != null && !ud2.isEmpty()) { if (ud2List != null &&
		 * ud2List.size() > 0) {
		 * buildQuery.append(" AND NEN.USERACCESS2 IN :ud2List"); } } if (ud3 !=
		 * null && !ud3.isEmpty()) { if (ud3List != null && ud3List.size() > 0)
		 * { buildQuery.append(" AND NEN.USERACCESS3 IN :ud3List"); } } if (ud4
		 * != null && !ud4.isEmpty()) { if (ud4List != null && ud4List.size() >
		 * 0) { buildQuery.append(" AND NEN.USERACCESS4 IN :ud4List"); } } if
		 * (ud5 != null && !ud5.isEmpty()) { if (ud5List != null &&
		 * ud5List.size() > 0) {
		 * buildQuery.append(" AND NEN.USERACCESS5 IN :ud5List"); } } if (ud6 !=
		 * null && !ud6.isEmpty()) { if (ud6List != null && ud6List.size() > 0)
		 * { buildQuery.append(" AND NEN.USERACCESS6 IN :ud6List"); } }
		 */
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildQuery.append(" AND NEN.DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":taxPeriodFrom AND :taxPeriodTo");

		}

		String queryStr = createNilRatedTotalSummQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodTo());
			q.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}
		/*
		 * if (ProfitCenter != null && !ProfitCenter.isEmpty()) { if (pcList !=
		 * null && !pcList.isEmpty() && pcList.size() > 0) {
		 * q.setParameter("pcList", pcList); } } if (plant != null &&
		 * !plant.isEmpty()) { if (plantList != null && !plantList.isEmpty() &&
		 * plantList.size() > 0) { q.setParameter("plantList", plantList); } }
		 * if (sales != null && !sales.isEmpty()) { if (salesList != null &&
		 * !salesList.isEmpty() && salesList.size() > 0) {
		 * q.setParameter("salesList", salesList); } } if (division != null &&
		 * !division.isEmpty()) { if (divisionList != null &&
		 * !divisionList.isEmpty() && divisionList.size() > 0) {
		 * q.setParameter("divisionList", divisionList); } } if (location !=
		 * null && !location.isEmpty()) { if (locationList != null &&
		 * !locationList.isEmpty() && locationList.size() > 0) {
		 * q.setParameter("locationList", locationList); } } if (distChannel !=
		 * null && !distChannel.isEmpty()) { if (distList != null &&
		 * !distList.isEmpty() && distList.size() > 0) {
		 * q.setParameter("distList", distList); } } if (ud1 != null &&
		 * !ud1.isEmpty()) { if (ud1List != null && !ud1List.isEmpty() &&
		 * ud1List.size() > 0) { q.setParameter("ud1List", ud1List); } } if (ud2
		 * != null && !ud2.isEmpty()) { if (ud2List != null &&
		 * !ud2List.isEmpty() && ud2List.size() > 0) { q.setParameter("ud2List",
		 * ud2List); } } if (ud3 != null && !ud3.isEmpty()) { if (ud3List !=
		 * null && !ud3List.isEmpty() && ud3List.size() > 0) {
		 * q.setParameter("ud3List", ud3List); } } if (ud4 != null &&
		 * !ud4.isEmpty()) { if (ud4List != null && !ud4List.isEmpty() &&
		 * ud4List.size() > 0) { q.setParameter("ud4List", ud4List); } } if (ud5
		 * != null && !ud5.isEmpty()) { if (ud5List != null &&
		 * !ud5List.isEmpty() && ud5List.size() > 0) { q.setParameter("ud5List",
		 * ud5List); } } if (ud6 != null && !ud6.isEmpty()) { if (ud6List !=
		 * null && !ud6List.isEmpty() && ud6List.size() > 0) {
		 * q.setParameter("ud6List", ud6List); } }
		 */
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessedNil(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private NilNonProcessedRecordsDto convertProcessedNil(Object[] arr) {
		NilNonProcessedRecordsDto obj = new NilNonProcessedRecordsDto();

		obj.setgSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setHsn(arr[2] != null ? arr[2].toString() : null);
		obj.setDescription(arr[3] != null ? arr[3].toString() : null);
		obj.setUqc(arr[4] != null ? arr[4].toString() : null);
		obj.setQuantity(arr[5] != null ? arr[5].toString() : null);
		obj.setnILInterStateRegistered(
				arr[6] != null ? arr[6].toString() : null);
		obj.setnILIntraStateRegistered(
				arr[7] != null ? arr[7].toString() : null);
		obj.setnILInterStateUnRegistered(
				arr[8] != null ? arr[8].toString() : null);
		obj.setnILIntraStateUnRegistered(
				arr[9] != null ? arr[9].toString() : null);
		obj.setExtInterStateRegistered(
				arr[10] != null ? arr[10].toString() : null);
		obj.seteXTIntraStateRegistered(
				arr[11] != null ? arr[11].toString() : null);
		obj.seteXTInterStateUnRegistered(
				arr[12] != null ? arr[12].toString() : null);
		obj.seteXTIntraStateUnRegistered(
				arr[13] != null ? arr[13].toString() : null);
		obj.setnONInterStateRegistered(
				arr[14] != null ? arr[14].toString() : null);
		obj.setnONIntraStateRegistered(
				arr[15] != null ? arr[15].toString() : null);
		obj.setnONInterStateUnRegistered(
				arr[16] != null ? arr[16].toString() : null);
		obj.setnONIntraStateUnRegistered(
				arr[17] != null ? arr[17].toString() : null);
		obj.setaSPinformationCode(arr[18] != null ? arr[18].toString() : null);
		obj.setaSPinformationDescription(
				arr[19] != null ? arr[19].toString() : null);
		/*
		 * obj.setGSTR1Table(arr[20] != null ? arr[20].toString() : null);
		 * obj.setGSTR3BTable(arr[21] != null ? arr[21].toString() : null);
		 */
		obj.setSaveStatus(arr[20] != null ? arr[20].toString() : null);
		obj.setgSTNRefID(arr[21] != null ? arr[21].toString() : null);
		obj.setgSTNRefIDTime(arr[22] != null ? arr[22].toString() : null);
		obj.setgSTNErrorCode(arr[23] != null ? arr[23].toString() : null);
		obj.setgSTNErrorDescription(
				arr[24] != null ? arr[24].toString() : null);
		obj.setSourceID(arr[25] != null ? arr[25].toString() : null);
		obj.setFileName(arr[26] != null ? arr[26].toString() : null);
		obj.setaSPDateTime(arr[27] != null ? arr[27].toString() : null);

		return obj;
	}

	private String createNilRatedTotalSummQueryString(String buildQuery) {

		return "SELECT NEN.SUPPLIER_GSTIN, NEN.RETURN_PERIOD,"
				+ "ITM_HSNSAC HSNSAC, ITM_DESCRIPTION DESCRIPTION,"
				+ "ITM_UQC UQC,ITM_QTY QTY, NIL_INTERSTATE_REG,"
				+ "NIL_INTRASTATE_REG,NIL_INTERSTATE_UNREG,NIL_INTRASTATE_UNREG, "
				+ "EXT_INTERSTATE_REG,EXT_INTRASTATE_REG ,EXT_INTERSTATE_UNREG ,"
				+ "EXT_INTRASTATE_UNREG, NON_INTERSTATE_REG,"
				+ "NON_INTRASTATE_REG,NON_INTERSTATE_UNREG,NON_INTRASTATE_UNREG, "
				+ "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
				+ "AS INFO_ERROR_CODE_ASP, "
				+ "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP, (CASE "
				+ "WHEN NEN.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN NEN.IS_SAVED_TO_GSTN = FALSE AND NEN.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN NEN.IS_SAVED_TO_GSTN = FALSE "
				+ "AND NEN.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
				+ " '' AS GSTN_ERROR_CODE,'' AS GSTN_ERROR_DESCRIPTION, "
				+ "FIL.CREATED_BY AS SOURCE_ID,FIL.FILE_NAME AS FILE_NAME, "
				+ "NEN.CREATED_ON AS ASP_DATE_TIME FROM "
				+ "GSTR1A_PROCESSED_NILEXTNON NEN LEFT OUTER JOIN "
				+ " (SELECT COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE ,"
				+ "STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP ,"
				+ "STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') "
				+ "INFO_ERROR_DESCRIPTION_ASP FROM ( SELECT COMMON_ID,VAL_TYPE,"
				+ "FILE_ID,INV_KEY,TABLE_TYPE, CASE WHEN ERROR_TYPE='INFO' "
				+ "THEN ERROR_CODE END AS INFO_ERROR_CODE_ASP, CASE "
				+ "WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END "
				+ "AS INFO_ERROR_DESCRIPTION_ASP FROM ANX_VERTICAL_ERROR_1A) "
				+ " GROUP BY COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE) GSTRERR ON "
				+ "NEN.AS_ENTERED_ID= GSTRERR.COMMON_ID "
				+ "AND NEN.FILE_ID=GSTRERR.FILE_ID "
				+ "AND GSTRERR.TABLE_TYPE='NILNONEXMPT' LEFT OUTER JOIN "
				+ "FILE_STATUS FIL  ON (NEN.FILE_ID=FIL.ID OR GSTRERR.FILE_ID=FIL.ID)  "
				+ " LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = NEN.BATCH_ID "
				+ "WHERE NEN.IS_DELETE=FALSE " + buildQuery;
	}
}
