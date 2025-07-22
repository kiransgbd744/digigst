package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1AspVerticalHsnDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1OutwardVerticalProcessHSNDaoImpl")
public class Gstr1OutwardVerticalProcessHSNDaoImpl
		implements Gstr1OutwardVerticalProcessHSNDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1OutwardVerticalProcessHSNDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1RSReports(SearchCriteria criteria) {
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
				buildQuery.append(" AND HSN.SUPPLIER_GSTIN IN :gstinList");
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
				buildQuery.append(" AND HSN.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND HSN.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND HSN.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND ITM.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND HSN.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND HSN.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND HSN.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND HSN.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND HSN.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND HSN.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HSN.DERIVED_RET_PERIOD = :taxperiod ");
			buildQuery.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = createB2CSSavableSummaryQueryString(
				buildQuery.toString());
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
		return list.parallelStream().map(o -> convertProcessedHsn(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1AspVerticalHsnDto convertProcessedHsn(Object[] arr) {
		Gstr1AspVerticalHsnDto obj = new Gstr1AspVerticalHsnDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setHsn(arr[2] != null ? arr[2].toString() : null);
		obj.setDescription(arr[3] != null ? arr[3].toString() : null);
		obj.setUqc(arr[4] != null ? arr[4].toString() : null);
		BigDecimal bigDecimalTotalQuantity = (BigDecimal) arr[5];
		if (bigDecimalTotalQuantity != null) {
			BigDecimal total = new BigDecimal(bigDecimalTotalQuantity.doubleValue());
			obj.setTotalQuantity(total);
		}
		BigDecimal bigDecimalTaxableValue = (BigDecimal) arr[6];
		if (bigDecimalTaxableValue != null) {
			BigDecimal taxable = new BigDecimal(bigDecimalTaxableValue.doubleValue());
			obj.setTaxableValue(taxable);
		}
		BigDecimal bigDecimalIGST = (BigDecimal) arr[7];
		if (bigDecimalIGST != null) {
			BigDecimal igst = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmount(igst);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[8];
		if (bigDecimalCGST != null) {
			BigDecimal cgst = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmount(cgst);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[9];
		if (bigDecimalSGST != null) {
			BigDecimal sgst = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstutgstAmount(sgst);
		}
		BigDecimal bigDecimalCess = (BigDecimal) arr[10];
		if (bigDecimalCess != null) {
			BigDecimal cess = new BigDecimal(bigDecimalCess.longValue());
			obj.setSgstutgstAmount(cess);
		}
		/*BigDecimal bigDecimalTotalValue = (BigDecimal) arr[11];
		if (bigDecimalTotalValue != null) {
			BigDecimal total = new BigDecimal(bigDecimalTotalValue.doubleValue());
			obj.setTotalValue(total);
		}*/
		obj.setTotalValue(arr[11] != null ? arr[11].toString() : null);
		obj.setSaveStatus(arr[12] != null ? arr[12].toString() : null);
		obj.setgSTNRefID(arr[13] != null ? arr[13].toString() : null);
		obj.setgSTNRefIDTime(arr[14] != null ? arr[14].toString() : null);
		obj.setgSTNErrorcode(arr[15] != null ? arr[15].toString() : null);
		obj.setgSTNErrorDescription(
				arr[16] != null ? arr[16].toString() : null);

		return obj;
	}

	private String createB2CSSavableSummaryQueryString(String buildQuery) {

		return "SELECT HSN.SUPPLIER_GSTIN,ITM.RETURN_PERIOD,"
				+ "ITM.ITM_HSNSAC,ITM.ITM_DESCRIPTION,ITM.ITM_UQC,"
				+ "ITM.ITM_QTY,ITM.TAXABLE_VALUE,ITM.IGST_AMT AS IGST_AMT,"
				+ "ITM.CGST_AMT AS CGST_AMT, ITM.SGST_AMT AS SGST_AMT,"
				+ "(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM) "
				+ "AS CESS_AMT,(HSN.DOC_AMT) AS TOTAL_VALUE,"
				+ "(CASE WHEN HSN.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED'"
				+ " WHEN HSN.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HSN.GSTN_ERROR = TRUE THEN 'IS_ERROR' "
				+ "WHEN HSN.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HSN.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID "
				+ "AS GSTIN_REF_ID,GSTNBATCH.GSTN_RESP_DATE "
				+ "AS GSTIN_REF_ID_TIME,"
				+ "TRIM(', ' FROM IFNULL(GSTNH_ER.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(GSTNI_ER.ERROR_CODE_ASP,'')) AS GSTIN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(GSTNH_ER.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(GSTNI_ER.ERROR_DESCRIPTION_ASP,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION FROM "
				+ "ANX_OUTWARD_DOC_HEADER HSN LEFT OUTER JOIN "
				+ "TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO () GSTNH_ER "
				+ "ON HSN.ID=GSTNH_ER.DOC_HEADER_ID "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM "
				+ "ITM ON HSN.ID = ITM.DOC_HEADER_ID "
				+ "LEFT OUTER JOIN TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO () "
				+ "GSTNI_ER ON ITM.DOC_HEADER_ID= GSTNI_ER.DOC_HEADER_ID "
				+ "AND ITM.ITM_NO=GSTNI_ER.ITM_NO "
				+ "INNER JOIN FILE_STATUS FIL ON HSN.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH "
				+ "GSTNBATCH ON GSTNBATCH.ID = HSN.BATCH_ID "
				+ "WHERE IS_ERROR= FALSE AND HSN.RETURN_TYPE ='GSTR1' "
				+ "AND HSN.TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8') "
				+ buildQuery + " GROUP BY "
				+ "HSN.SUPPLIER_GSTIN, ITM.RETURN_PERIOD,"
				+ "ITM.ITM_HSNSAC,ITM.ITM_DESCRIPTION,"
				+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_AMT, ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.CESS_AMT_ADVALOREM,"
				+ "HSN.IS_SAVED_TO_GSTN,HSN.GSTN_ERROR,HSN.TAX_DOC_TYPE,"
				+ "HSN.DOC_AMT,GSTNBATCH.GSTN_SAVE_REF_ID,"
				+ "GSTNBATCH.GSTN_RESP_DATE,GSTNH_ER.ERROR_CODE_ASP,"
				+ "GSTNI_ER.ERROR_DESCRIPTION_ASP,"
				+ "GSTNH_ER.ERROR_DESCRIPTION_ASP,GSTNI_ER.ERROR_CODE_ASP"
				+ " UNION ALL " + "SELECT HSN.SUPPLIER_GSTIN,ITM.RETURN_PERIOD,"
				+ "ITM.ITM_HSNSAC,ITM.ITM_DESCRIPTION,"
				+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.TAXABLE_VALUE,"
				+ "'0' AS  IGST_AMT, '0' AS  CGST_AMT, '0' AS  SGST_AMT,"
				+ "'0' AS  CESS_AMT,ITM.TAXABLE_VALUE AS TOTAL_VALUE,"
				+ "(CASE WHEN HSN.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN HSN.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HSN.GSTN_ERROR = TRUE THEN 'IS_ERROR' "
				+ "WHEN HSN.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HSN.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,"
				+ "GSTNBATCH.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME,"
				+ "TRIM(', ' FROM IFNULL(GSTNH_ER.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(GSTNI_ER.ERROR_CODE_ASP,'')) AS GSTIN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(GSTNH_ER.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(GSTNI_ER.ERROR_DESCRIPTION_ASP,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION "
				+ "FROM ANX_OUTWARD_DOC_HEADER HSN "
				+ "LEFT OUTER JOIN TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO () "
				+ "GSTNH_ER ON HSN.ID=GSTNH_ER.DOC_HEADER_ID "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM ITM "
				+ "ON HSN.ID = ITM.DOC_HEADER_ID "
				+ "LEFT OUTER JOIN TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO () "
				+ "GSTNI_ER ON ITM.DOC_HEADER_ID= GSTNI_ER.DOC_HEADER_ID "
				+ "AND ITM.ITM_NO=GSTNI_ER.ITM_NO "
				+ "INNER JOIN FILE_STATUS FIL ON HSN.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON "
				+ "GSTNBATCH.ID = HSN.BATCH_ID WHERE IS_ERROR= FALSE "
				+ "AND HSN.RETURN_TYPE ='GSTR1' "
				+ "AND HSN.TABLE_SECTION IN ('4B') " + buildQuery + " GROUP BY "
				+ "HSN.SUPPLIER_GSTIN, ITM.RETURN_PERIOD,"
				+ "ITM.ITM_HSNSAC,ITM.ITM_DESCRIPTION,ITM.ITM_UQC,"
				+ "ITM.ITM_QTY,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.CESS_AMT_ADVALOREM,"
				+ "HSN.IS_SAVED_TO_GSTN,HSN.GSTN_ERROR,HSN.TAX_DOC_TYPE,"
				+ "HSN.DOC_AMT,GSTNBATCH.GSTN_SAVE_REF_ID,"
				+ "GSTNBATCH.GSTN_RESP_DATE,GSTNH_ER.ERROR_CODE_ASP,"
				+ "GSTNI_ER.ERROR_DESCRIPTION_ASP,"
				+ "GSTNH_ER.ERROR_DESCRIPTION_ASP,GSTNI_ER.ERROR_CODE_ASP"
				+ " UNION ALL " + "SELECT HSN.SUPPLIER_GSTIN,ITM.RETURN_PERIOD,"
				+ "ITM.ITM_HSNSAC,ITM.ITM_DESCRIPTION,ITM.ITM_UQC,"
				+ "ITM.ITM_QTY,ITM.TAXABLE_VALUE,ITM.IGST_AMT AS IGST_AMT,"
				+ "ITM.CGST_AMT AS CGST_AMT, ITM.SGST_AMT AS SGST_AMT,"
				+ "(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM) "
				+ "AS CESS_AMT,(ITM.TAXABLE_VALUE) AS TOTAL_VALUE,"
				+ "(CASE WHEN HSN.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ " WHEN HSN.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HSN.GSTN_ERROR = TRUE THEN 'IS_ERROR' "
				+ "WHEN HSN.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HSN.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
				+ "AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,"
				+ "GSTNBATCH.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME,"
				+ "TRIM(', ' FROM IFNULL(GSTNH_ER.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(GSTNI_ER.ERROR_CODE_ASP,'')) AS GSTIN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(GSTNH_ER.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(GSTNI_ER.ERROR_DESCRIPTION_ASP,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION "
				+ "FROM ANX_OUTWARD_DOC_HEADER HSN "
				+ "LEFT OUTER JOIN TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO () "
				+ "GSTNH_ER ON HSN.ID=GSTNH_ER.DOC_HEADER_ID "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM ITM "
				+ "ON HSN.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO () GSTNI_ER ON "
				+ "ITM.DOC_HEADER_ID= GSTNI_ER.DOC_HEADER_ID "
				+ "AND ITM.ITM_NO=GSTNI_ER.ITM_NO "
				+ "INNER JOIN FILE_STATUS FIL ON HSN.FILE_ID=FIL.ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH "
				+ "GSTNBATCH ON GSTNBATCH.ID = HSN.BATCH_ID "
				+ "WHERE IS_ERROR= FALSE AND HSN.RETURN_TYPE ='GSTR1' "
				+ "AND HSN.TABLE_SECTION IN ('6A') " + buildQuery + " GROUP BY "
				+ "HSN.SUPPLIER_GSTIN,ITM.RETURN_PERIOD,"
				+ "ITM.ITM_HSNSAC,ITM.ITM_DESCRIPTION,"
				+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_AMT, ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT_SPECIFIC,ITM.CESS_AMT_ADVALOREM,"
				+ "HSN.IS_SAVED_TO_GSTN,HSN.GSTN_ERROR,HSN.TAX_DOC_TYPE,"
				+ "HSN.DOC_AMT,GSTNBATCH.GSTN_SAVE_REF_ID,"
				+ "GSTNBATCH.GSTN_RESP_DATE,GSTNH_ER.ERROR_CODE_ASP,"
				+ "GSTNI_ER.ERROR_DESCRIPTION_ASP,"
				+ "GSTNH_ER.ERROR_DESCRIPTION_ASP,GSTNI_ER.ERROR_CODE_ASP";
	}
}
