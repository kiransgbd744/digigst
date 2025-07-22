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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.views.client.GSTR1ProcessedRecordsScreenDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1ProcessedRecordsScreenDaoImpl")
public class Gstr1ProcessedRecordsScreenDaoImpl
		implements Gstr1GstnSummaryTablesDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ProcessedRecordsScreenDaoImpl.class);

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1GstnSummaryTablesReport(
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

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" DERIVED_RET_PERIOD = :taxperiod");
		}

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
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

		String queryStr = createProcessRecQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		/*
		 * String groupcode = TenantContext.getTenantId();
		 * TenantContext.setTenantId(groupcode);
		 */
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessedRecords(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GSTR1ProcessedRecordsScreenDto convertProcessedRecords(
			Object[] arr) {
		GSTR1ProcessedRecordsScreenDto obj = new GSTR1ProcessedRecordsScreenDto();

		String gstin = String
				.valueOf(arr[0] != null ? arr[0].toString() : null);
		obj.setGSTIN(gstin);
		obj.setStateCode(arr[1] != null ? arr[1].toString() : null);
		obj.setStateName(arr[2] != null ? arr[2].toString() : null);

		try {
			List<String> regName = gSTNDetailRepository
					.findRegTypeByGstin(gstin);

			if (regName != null && regName.size() > 0) {
				String regTypeName = regName.get(0);
				obj.setRegistrationType(regTypeName.toUpperCase());
			} else {
				obj.setRegistrationType("");
			}
		} catch (Exception e) {
			obj.setRegistrationType("");
			String str = "Gstin not found in Gstin info table, hence setting is empty to RegType -> "
					+ gstin + " Exception is->" + e.getMessage();
			LOGGER.error(str);
		}

		// returnperiod
		obj.setCount(arr[5] != null ? arr[5].toString() : null);
		obj.setTaxableValue(arr[6] != null ? arr[6].toString() : null);
		obj.setIgst(arr[7] != null ? arr[7].toString() : null);
		obj.setCgst(arr[8] != null ? arr[8].toString() : null);
		obj.setSgst(arr[9] != null ? arr[9].toString() : null);
		obj.setCess(arr[10] != null ? arr[10].toString() : null);
		obj.setSaveStatus(arr[11] != null ? arr[11].toString() : null);
		obj.setDateTime(arr[12] != null ? arr[12].toString() : null);

		return obj;
	}

	private String createProcessRecQueryString(String buildQuery) {

		return "SELECT SUPPLIER_GSTIN,STATE_CODE,STATE_NAME,"
				+ " '' REGISTRATION_TYPE,RETURN_PERIOD,SUM(TOT_COUNT) TOT_COUNT,"
				+ "SUPPLIES TAXABLE_VALUE,SUM(IGST) IGST,SUM(CGST) CGST,"
				+ "SUM(SGST) SGST,SUM(CESS) CESS,STATUS,MAX(MODIFIED_ON) STATUS_TIME"
				+ " FROM ((SELECT HDR.SUPPLIER_GSTIN,MT.STATE_CODE,"
				+ "MT.STATE_NAME,HDR.RETURN_PERIOD,HDR.TAX_DOC_TYPE,"
				+ "HDR.DOC_TYPE,COUNT(DOC_KEY) AS TOT_COUNT,"
				+ "SUM(ITM.TAXABLE_VALUE) AS SUPPLIES,SUM(ITM. IGST_AMT) "
				+ "AS IGST,SUM(ITM.CGST_AMT) AS CGST,SUM(ITM.SGST_AMT) "
				+ "AS SGST,SUM(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM) "
				+ "AS CESS,CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN =TRUE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0 "
				+ "AND COUNT(CASE WHEN IS_SAVED_TO_GSTN =true AND "
				+ "IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN "
				+ "IS_DELETE =FALSE THEN 1 ELSE NULL END) = COUNT(CASE "
				+ "WHEN GSTN_ERROR =TRUE AND IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'FAILED' WHEN (COUNT(CASE when "
				+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN "
				+ "1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE then 1 ELSE "
				+ "NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN "
				+ "1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS,"
				+ "HDR.MODIFIED_ON FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM ITM "
				+ "ON HDR. ID = ITM. DOC_HEADER_ID LEFT JOIN MASTER_STATE MT"
				+ " ON MT.STATE_CODE = SUBSTR(SUPPLIER_GSTIN,1,2) "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE =FALSE "
				+ "AND HDR. TAX_DOC_TYPE IN ('B2B','B2BA','B2CS','B2CSA',"
				+ " 'B2CL','B2CLA','ADV REC','ADV REC-A','ADV ADJ',"
				+ " 'ADV ADJ-A','EXPORTS','EXPORTS-A','CDNR','CDNRA',"
				+ " 'CDNUR','CDNURA','NILEXTNON') "
				+ " GROUP BY HDR.SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME, "
				+ "HDR.RETURN_PERIOD,HDR.TAX_DOC_TYPE,HDR.DOC_TYPE,HDR.MODIFIED_ON "
				+ " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,'B2CS' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,"
				+ " '0' AS TOT_COUNT,SUM( NEW_TAXABLE_VALUE ) AS SUPPLIES,"
				+ "SUM( IGST_AMT ) AS IGST,SUM( CGST_AMT ) AS CGST,"
				+ "SUM( SGST_AMT ) AS SGST,SUM( CESS_AMT ) AS CESS,"
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN =FALSE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN "
				+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT(CASE "
				+ "WHEN GSTN_ERROR =TRUE AND IS_DELETE =FALSE THEN "
				+ "1 ELSE NULL END)) THEN 'FAILED' WHEN (COUNT(CASE "
				+ "when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT "
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END "
				+ "AS STATUS,MODIFIED_ON FROM GSTR1_PROCESSED_B2CS "
				+ "LEFT JOIN MASTER_STATE MT ON "
				+ "MT.STATE_CODE = SUBSTR(SUPPLIER_GSTIN,1,2) "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT= FALSE "
				+ " GROUP BY SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,MODIFIED_ON " + " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,'B2CSA' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,"
				+ " '0' AS TOT_COUNT,SUM( NEW_TAXABLE_VALUE ) AS SUPPLIES,"
				+ "SUM( IGST_AMT ) AS IGST,SUM( CGST_AMT ) AS CGST,"
				+ "SUM( SGST_AMT ) AS SGST,SUM( CESS_AMT ) AS CESS,"
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN =FALSE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE =FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR = TRUE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL	END)) THEN 'SAVED' WHEN (COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' "
				+ "END AS STATUS,MODIFIED_ON FROM GSTR1_PROCESSED_B2CS "
				+ "LEFT JOIN MASTER_STATE MT ON "
				+ "MT.STATE_CODE = SUBSTR(SUPPLIER_GSTIN,1,2) "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT= TRUE "
				+ " GROUP BY SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,MODIFIED_ON " + " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,'ADV ADJ' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,"
				+ " '0' AS TOT_COUNT,SUM( NEW_GROSS_ADV_ADJUSTED ) AS SUPPLIES,"
				+ "SUM( IGST_AMT ) AS IGST,SUM( CGST_AMT ) AS CGST,"
				+ "SUM( SGST_AMT ) AS SGST,SUM( CESS_AMT ) AS CESS,"
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN =FALSE AND "
				+ "IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ " 'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE =FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR = TRUE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL	END)) THEN 'SAVED' WHEN (COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END "
				+ "AS STATUS,MODIFIED_ON FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
				+ "LEFT JOIN MASTER_STATE MT ON MT.STATE_CODE = SUBSTR"
				+ "(SUPPLIER_GSTIN,1,2) WHERE IS_DELETE = FALSE AND "
				+ "IS_AMENDMENT= FALSE "
				+ " GROUP BY SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME, "
				+ "RETURN_PERIOD,MODIFIED_ON " + " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,'ADV ADJ-A' AS TAX_DOC_TYPE,"
				+ " 'INV' AS DOC_TYPE,'0' AS TOT_COUNT,"
				+ "SUM( NEW_GROSS_ADV_ADJUSTED ) AS SUPPLIES,SUM( IGST_AMT ) "
				+ "AS IGST,SUM( CGST_AMT ) AS CGST,SUM( SGST_AMT ) AS SGST,"
				+ "SUM( CESS_AMT ) AS CESS,CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN =FALSE AND IS_DELETE =FALSE "
				+ "THEN 1 ELSE NULL END) = 0) THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE =FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR = TRUE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL	END)) THEN 'SAVED' WHEN (COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS,"
				+ "MODIFIED_ON FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
				+ "LEFT JOIN MASTER_STATE MT ON "
				+ "MT.STATE_CODE = SUBSTR(SUPPLIER_GSTIN,1,2) "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT= TRUE "
				+ " GROUP BY SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,MODIFIED_ON " + " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,'ADV REC' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,"
				+ " '0' AS TOT_COUNT,SUM( NEW_GROSS_ADV_RECEIVED ) AS SUPPLIES,"
				+ "SUM( IGST_AMT ) AS IGST,SUM( CGST_AMT ) AS CGST,"
				+ "SUM( SGST_AMT ) AS SGST,SUM( CESS_AMT ) AS CESS,"
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN =FALSE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ " 'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE =FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR = TRUE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE THEN "
				+ "1 ELSE NULL	END)) THEN 'SAVED' WHEN (COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' "
				+ "END AS STATUS,MODIFIED_ON FROM GSTR1_PROCESSED_ADV_RECEIVED "
				+ "LEFT JOIN MASTER_STATE MT ON "
				+ "MT.STATE_CODE = SUBSTR(SUPPLIER_GSTIN,1,2) "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT= FALSE "
				+ " GROUP BY SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,RETURN_PERIOD,MODIFIED_ON " + " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,'ADV REC-A' AS TAX_DOC_TYPE,"
				+ " 'INV' AS DOC_TYPE,'0' AS TOT_COUNT,"
				+ "SUM( NEW_GROSS_ADV_RECEIVED ) AS SUPPLIES,"
				+ "SUM( IGST_AMT ) AS IGST,SUM( CGST_AMT ) AS CGST,"
				+ "SUM( SGST_AMT ) AS SGST,SUM( CESS_AMT ) AS CESS,"
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN =FALSE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE =FALSE "
				+ "THEN 1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR = TRUE "
				+ "AND IS_DELETE =FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE "
				+ "AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT(CASE "
				+ "WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN "
				+ " 'PARTIALLY SAVED' END AS STATUS,MODIFIED_ON "
				+ "FROM GSTR1_PROCESSED_ADV_RECEIVED LEFT JOIN "
				+ "MASTER_STATE MT ON MT.STATE_CODE = SUBSTR (SUPPLIER_GSTIN,1,2) "
				+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT= TRUE AND "
				+ buildQuery
				+ " GROUP BY SUPPLIER_GSTIN,MT.STATE_CODE,MT.STATE_NAME,"
				+ "RETURN_PERIOD,MODIFIED_ON)) "
				+ " GROUP BY SUPPLIER_GSTIN,STATE_CODE,STATE_NAME,"
				+ "RETURN_PERIOD,TAX_DOC_TYPE,DOC_TYPE,SUPPLIES,STATUS "
				+ " ORDER BY SUPPLIER_GSTIN DESC ";

	}
}
