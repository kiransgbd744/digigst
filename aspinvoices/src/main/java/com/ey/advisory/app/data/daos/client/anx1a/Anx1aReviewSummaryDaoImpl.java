package com.ey.advisory.app.data.daos.client.anx1a;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

@Component("Anx1aReviewSummaryDaoImpl")
public class Anx1aReviewSummaryDaoImpl implements Anx1aReviewSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Object[]> getOutwardAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto) {
		List<String> conditions = buildQueryWithCriteria(anx1aSummaryReqDto);
		String queryStr = createOutwardQueryString(conditions);
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, anx1aSummaryReqDto);
		List<Object[]> outwardList = q.getResultList();
		return outwardList;
	}

	@Override
	public List<Object[]> getInwardAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto) {
		List<String> conditions = buildQueryWithCriteria(anx1aSummaryReqDto);
		String queryStr = createInwardQueryString(conditions);
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, anx1aSummaryReqDto);
		List<Object[]> inwardList = q.getResultList();
		return inwardList;
	}

	@Override
	public List<Object[]> getSuppliesAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto) {
		List<String> conditions = buildQueryWithCriteria(anx1aSummaryReqDto);
		String queryStr = createSuppliesQueryString(conditions);
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, anx1aSummaryReqDto);
		List<Object[]> suppliesList = q.getResultList();
		return suppliesList;
	}

	private List<String> buildQueryWithCriteria(
			Annexure1SummaryReqDto anx1aSummaryReqDto) {
		String taxPeriod = anx1aSummaryReqDto.getTaxPeriod();

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(anx1aSummaryReqDto);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
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
					gstin = key;
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
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
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

		StringBuffer Build = new StringBuffer();
		StringBuffer outEmptyBuild = new StringBuffer();
		StringBuffer inEmptyBuild = new StringBuffer();
		StringBuffer build = new StringBuffer();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				Build.append(" AND  SUPPLIER_GSTIN IN (:gstinList) ");
				outEmptyBuild.append(" AND SUPPLIER_GSTIN IN (:gstinList) ");
				build.append(" AND  CUST_GSTIN IN (:gstinList) ");
				inEmptyBuild.append(" AND CUST_GSTIN IN (:gstinList) ");
			}
		}
		if (taxPeriod != null && !taxPeriod.equals("")) {
			Build.append("  AND  DERIVED_RET_PERIOD IN (:taxPeriod) ");
			outEmptyBuild.append("  AND DERIVED_RET_PERIOD IN (:taxPeriod) ");
			build.append("  AND  DERIVED_RET_PERIOD IN (:taxPeriod) ");
			inEmptyBuild.append("  AND DERIVED_RET_PERIOD IN (:taxPeriod) ");
		}

		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				Build.append(" AND  PROFIT_CENTRE IN (:pcList) ");
				outEmptyBuild.append(" AND  PROFIT_CENTER IN (:pcList) ");
				build.append(" AND  PROFIT_CENTRE IN (:pcList) ");
				inEmptyBuild.append(" AND  PROFIT_CENTER IN (:pcList) ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				Build.append(" AND  PLANT_CODE IN (:plantList) ");
				outEmptyBuild.append(" AND  PLANT IN (:plantList) ");
				build.append(" AND  PLANT_CODE IN (:plantList) ");
				inEmptyBuild.append(" AND  PLANT IN (:plantList) ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				Build.append(" AND  DIVISION IN (:divisionList) ");
				outEmptyBuild.append(" AND DIVISION IN (:divisionList) ");
				build.append(" AND  DIVISION IN (:divisionList) ");
				inEmptyBuild.append(" AND DIVISION IN (:divisionList) ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				Build.append(" AND  LOCATION IN (:locationList) ");
				outEmptyBuild.append(" AND  LOCATION IN (:locationList) ");
				build.append(" AND  LOCATION IN (:locationList) ");
				inEmptyBuild.append(" AND  LOCATION IN (:locationList) ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				Build.append(" AND  SALES_ORGANIZATION IN (:salesList) ");
				outEmptyBuild.append(" AND SALES_ORG IN (:salesList) ");
				build.append(" AND  SALES_ORGANIZATION IN (:salesList) ");
				inEmptyBuild.append(" AND SALES_ORG IN (:salesList) ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				Build.append(" AND  PURCHASE_ORGANIZATION IN (:purcList) ");
				outEmptyBuild.append(" AND PURCHASE_ORG IN (:purcList) ");
				build.append(" AND  PURCHASE_ORGANIZATION IN (:purcList) ");
				inEmptyBuild.append(" AND PURCHASE_ORG IN (:purcList) ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				Build.append(" AND  DISTRIBUTION_CHANNEL IN (:distList) ");
				outEmptyBuild
						.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
				build.append(" AND  DISTRIBUTION_CHANNEL IN (:distList) ");
				inEmptyBuild
						.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				Build.append(" AND  USERACCESS1 IN (:ud1List) ");
				outEmptyBuild.append(" AND USER_ACCESS1 IN (:ud1List) ");
				build.append(" AND  USERACCESS1 IN (:ud1List) ");
				inEmptyBuild.append(" AND USERACCESS1 IN (:ud1List) ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				Build.append(" AND  USERACCESS2 IN (:ud2List) ");
				outEmptyBuild.append(" AND USER_ACCESS2 IN (:ud2List) ");
				build.append(" AND  USERACCESS2 IN (:ud2List) ");
				inEmptyBuild.append(" AND USERACCESS2 IN (:ud2List) ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				Build.append(" AND  USERACCESS3 IN (:ud3List) ");
				outEmptyBuild.append(" AND USER_ACCESS3 IN (:ud3List) ");
				build.append(" AND  USERACCESS3 IN (:ud3List) ");
				inEmptyBuild.append(" AND USERACCESS3 IN (:ud3List) ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				Build.append(" AND  USERACCESS4 IN (:ud4List) ");
				outEmptyBuild.append(" AND USER_ACCESS4 IN (:ud4List) ");
				build.append(" AND  USERACCESS4 IN (:ud4List) ");
				inEmptyBuild.append(" AND USERACCESS4 IN (:ud4List) ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				Build.append(" AND  USERACCESS5 IN (:ud5List) ");
				outEmptyBuild.append(" AND USER_ACCESS5 IN (:ud5List) ");
				build.append(" AND  USERACCESS5 IN (:ud5List) ");
				inEmptyBuild.append(" AND USERACCESS5 IN (:ud5List) ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				Build.append(" AND  USERACCESS6 IN (:ud6List) ");
				outEmptyBuild.append(" AND USER_ACCESS6 IN (:ud6List) ");
				build.append(" AND  USERACCESS6 IN (:ud6List) ");
				inEmptyBuild.append(" AND USERACCESS6 IN (:ud6List) ");
			}
		}
		List<String> conditions = Arrays.asList(Build.toString(),
				outEmptyBuild.toString(), build.toString(),
				inEmptyBuild.toString());
		return conditions;
	}

	private void setParamtersForQuery(Query q,
			Annexure1SummaryReqDto anx1aSummaryReqDto) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(anx1aSummaryReqDto);

		String taxPeriod = anx1aSummaryReqDto.getTaxPeriod();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		String profitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase("GSTIN")) {
					gstin = key;
					gstinList = dataSecAttrs.get("GSTIN");
				}
				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
					pcList = dataSecAttrs.get("PC");
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
					plantList = dataSecAttrs.get("Plant");
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
					divisionList = dataSecAttrs.get("D");
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
					locationList = dataSecAttrs.get("L");
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
					salesList = dataSecAttrs.get("SO");
				}
				if (key.equalsIgnoreCase("PO")) {
					purchase = key;
					purcList = dataSecAttrs.get("PO");
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
					distList = dataSecAttrs.get("DC");
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
					ud1List = dataSecAttrs.get("UD1");
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
					ud2List = dataSecAttrs.get("UD2");
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
					ud3List = dataSecAttrs.get("UD3");
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
					ud4List = dataSecAttrs.get("UD4");
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
					ud5List = dataSecAttrs.get("UD5");
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
					ud6List = dataSecAttrs.get("UD6");
				}
			}
		}

		if (taxPeriod != null && !taxPeriod.equals("")) {
			q.setParameter("taxPeriod",
					GenUtil.convertTaxPeriodToInt(taxPeriod));
		}
		if (gstin != null && !gstin.isEmpty() && !gstinList.isEmpty()
				&& gstinList.size() > 0) {
			q.setParameter("gstinList", gstinList);
		}
		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			q.setParameter("pcList", pcList);
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			q.setParameter("plantList", plantList);
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			q.setParameter("divisionList", divisionList);
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			q.setParameter("locationList", locationList);
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			q.setParameter("salesList", salesList);
		}
		if (purchase != null && !purchase.isEmpty() && purcList != null
				&& purcList.size() > 0) {
			q.setParameter("purcList", purcList);
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			q.setParameter("distList", distList);
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			q.setParameter("ud1List", ud1List);
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			q.setParameter("ud2List", ud2List);
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			q.setParameter("ud3List", ud3List);
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			q.setParameter("ud4List", ud4List);
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			q.setParameter("ud5List", ud5List);
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			q.setParameter("ud6List", ud6List);
		}
	}

	private String createOutwardQueryString(List<String> conditions) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT SUM(RECORD_COUNT) AS RECORD_COUNT,DOC_TYPE, "
				+ "TABLE_SECTION,SUM(INVOICE_VALUE) AS INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,SUM(TOTAL_TAX) AS "
				+ "TOTAL_TAX,SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS "
				+ "CGST_AMT,SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT) AS CESS_AMT "
				+ "FROM (SELECT COUNT(DISTINCT DOC_KEY) AS RECORD_COUNT,"
				+ "DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,"
				+ "IFNULL(SUM( DOC_AMT),0) AS INVOICE_VALUE,"
				+ "IFNULL(SUM( TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "(IFNULL(SUM( IGST_AMT),0)+IFNULL(SUM( CGST_AMT),0)+"
				+ "IFNULL(SUM( SGST_AMT),0)+IFNULL(SUM( CESS_AMT_SPECIFIC),0)"
				+ "+IFNULL(SUM( CESS_AMT_ADVALOREM),0)) AS TOTAL_TAX,"
				+ "IFNULL(SUM( IGST_AMT),0) AS IGST_AMT,IFNULL(SUM( CGST_AMT),0) "
				+ "AS CGST_AMT,IFNULL(SUM( SGST_AMT),0) AS SGST_AMT,"
				+ " (IFNULL(SUM( CESS_AMT_SPECIFIC),0)+IFNULL"
				+ "(SUM( CESS_AMT_ADVALOREM),0)) AS CESS_AMT"
				+ " FROM ANX_OUTWARD_DOC_HEADER  WHERE "
				+ "IS_PROCESSED= TRUE AND IS_DELETE=FALSE AND  AN_RETURN_TYPE"
				+ "='ANX1A' AND AN_TABLE_SECTION IN ('3A','3B','3C') ");
		if (!conditions.isEmpty() && !conditions.get(0).equals("")) {
			buffer.append(conditions.get(0));
		}
		buffer.append(" GROUP BY  "
				+ "DOC_TYPE,AN_TABLE_SECTION UNION ALL SELECT '0' AS RECORD_COUNT,"
				+ " 'RNV' AS DOC_TYPE,'3A' AS TABLE_SECTION,(IFNULL(SUM(TAXABLE_VALUE),0)"
				+ "+IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+IFNULL(SUM(SGST_AMT),0)"
				+ "+IFNULL(SUM(CESS_AMT),0)) AS INVOICE_VALUE,SUM(TAXABLE_VALUE)"
				+ " AS TAXABLE_VALUE,(IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)"
				+ "+IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,IFNULL(SUM(CGST_AMT),0) "
				+ "AS CGST_AMT,IFNULL(SUM(SGST_AMT),0) AS SGST_AMT, (IFNULL(SUM"
				+ "(CESS_AMT),0)) AS CESS_AMT FROM ANX_PROCESSED_B2C"
				+ " WHERE IS_DELETE=FALSE AND RETURN_TYPE='ANX1A' AND "
				+ "IS_AMENDMENT=TRUE");
		if (!conditions.isEmpty() && !conditions.get(1).equals("")) {
			buffer.append(conditions.get(1));
		}
		buffer.append(") GROUP BY DOC_TYPE, TABLE_SECTION");
		return buffer.toString();
	}

	private String createInwardQueryString(List<String> conditions) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT SUM(RECORD_COUNT) AS RECORD_COUNT,DOC_TYPE, "
				+ "TABLE_SECTION,SUM(INVOICE_VALUE) AS INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,SUM(TOTAL_TAX) AS TOTAL_TAX"
				+ ",SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,"
				+ "SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT) AS CESS_AMT FROM"
				+ " (SELECT COUNT(DISTINCT DOC_KEY) AS RECORD_COUNT,DOC_TYPE,"
				+ "AN_TABLE_SECTION AS TABLE_SECTION,IFNULL(SUM( DOC_AMT),0)"
				+ " AS INVOICE_VALUE,IFNULL(SUM( TAXABLE_VALUE),0) AS"
				+ " TAXABLE_VALUE,(IFNULL(SUM( IGST_AMT),0)+IFNULL(SUM"
				+ "( CGST_AMT),0)+IFNULL(SUM( SGST_AMT),0)+IFNULL(SUM"
				+ "( CESS_AMT_SPECIFIC),0)+IFNULL(SUM( CESS_AMT_ADVALOREM)"
				+ ",0)) AS TOTAL_TAX,IFNULL(SUM( IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM( CGST_AMT),0) AS CGST_AMT,IFNULL(SUM( SGST_AMT)"
				+ ",0) AS SGST_AMT, (IFNULL(SUM( CESS_AMT_SPECIFIC),0)+IFNULL"
				+ "(SUM( CESS_AMT_ADVALOREM),0)) AS CESS_AMT FROM "
				+ "ANX_INWARD_DOC_HEADER  WHERE "
				+ "IS_PROCESSED = TRUE AND  IS_DELETE =FALSE AND "
				+ " AN_RETURN_TYPE='ANX1A' AND AN_TABLE_SECTION IN "
				+ "('3H','3I','3J','3K')");
		if (!conditions.isEmpty() && !conditions.get(2).equals("")) {
			buffer.append(conditions.get(2));
		}
		buffer.append(" GROUP BY  DOC_TYPE,AN_TABLE_SECTION, DERIVED_RET_PERIOD"
				+ " UNION ALL "
				+ "SELECT 0 AS RECORD_COUNT,(CASE WHEN TRAN_FLAG='RC'"
				+ " THEN 'RSLF'       WHEN TRAN_FLAG='IMPS' THEN 'RSLF' END) AS"
				+ "  DOC_TYPE ,(CASE WHEN TRAN_FLAG='RC' THEN '3H' WHEN "
				+ "TRAN_FLAG='IMPS' THEN '3I' END) AS  TABLE_SECTION ,"
				+ "(IFNULL(SUM(TAXABLE_VALUE),0)+IFNULL(SUM(IGST_AMT),0)+"
				+ "IFNULL(SUM(CGST_AMT),0)+IFNULL(SUM(SGST_AMT),0)+IFNULL"
				+ "(SUM(CESS_AMT),0)) AS INVOICE_VALUE,IFNULL(SUM(TAXABLE_VALUE)"
				+ ",0) AS TAXABLE_VALUE,     (IFNULL(SUM(IGST_AMT),0)+IFNULL"
				+ "(SUM(CGST_AMT),0)+IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),"
				+ "0)) AS TOTAL_TAX,IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,IFNULL"
				+ "(SUM(CGST_AMT),0) AS CGST_AMT,IFNULL(SUM(SGST_AMT),0) AS "
				+ "SGST_AMT, IFNULL(SUM(CESS_AMT),0) AS CESS_AMT FROM "
				+ "ANX_PROCESSED_3H_3I  WHERE  IS_DELETE =FALSE AND "
				+ "IS_AMENDMENT= TRUE AND RETURN_TYPE='ANX1A' ");
		if (!conditions.isEmpty() && !conditions.get(3).equals("")) {
			buffer.append(conditions.get(3));
		}
		buffer.append(
				"GROUP BY " + "TRAN_FLAG ) GROUP BY DOC_TYPE, TABLE_SECTION ");
		return buffer.toString();
	}

	private String createSuppliesQueryString(List<String> conditions) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT  TABLE_SECTION , SUM ( SUPPLIES_MADE ) AS "
				+ "SUPPLIES_MADE,SUM ( SUPPLIES_RETURN ) AS SUPPLIES_RETURN,"
				+ "SUM ( NET_SUPPLIES ) AS NET_SUPPLIES,SUM ( IGST_AMT ) AS"
				+ " IGST,SUM ( CGST_AMT ) AS CGST,SUM ( SGST_AMT ) AS SGST,SUM "
				+ "( CESS_AMT ) AS CESS,SUM( TAX_PAYABLE ) AS TAX_PAYABLE FROM"
				+ " ((SELECT 'TABLE-4' AS  TABLE_SECTION ,SUM(CASE WHEN "
				+ "  DOC_TYPE ='CR' THEN 0 ELSE   TAXABLE_VALUE  END)"
				+ " AS  SUPPLIES_MADE ,SUM(CASE WHEN   DOC_TYPE ='CR' "
				+ "THEN   TAXABLE_VALUE  ELSE 0 END) AS  SUPPLIES_RETURN ,"
				+ "SUM(CASE WHEN   DOC_TYPE ='CR' THEN IFNULL((  "
				+ "TAXABLE_VALUE ),0)*-1 ELSE IFNULL((  TAXABLE_VALUE ),0) "
				+ "END) AS  NET_SUPPLIES , SUM(CASE WHEN   DOC_TYPE ='CR' "
				+ "THEN IFNULL((  IGST_AMT ),0)*-1 ELSE IFNULL((  IGST_AMT )"
				+ ",0) END) AS  IGST_AMT ,SUM(CASE WHEN   DOC_TYPE ='CR' THEN"
				+ " IFNULL((  CGST_AMT ),0)*-1 ELSE IFNULL((  CGST_AMT ),0) "
				+ "END) AS  CGST_AMT , SUM(CASE WHEN   DOC_TYPE ='CR' THEN "
				+ "IFNULL((  SGST_AMT ),0)*-1 ELSE IFNULL((  SGST_AMT ),0)"
				+ " END) AS  SGST_AMT ,(SUM(CASE WHEN   DOC_TYPE ='CR' THEN "
				+ "IFNULL((  CESS_AMT_SPECIFIC ),0)*-1 ELSE IFNULL(( "
				+ " CESS_AMT_SPECIFIC ),0) END)+SUM(CASE WHEN   DOC_TYPE ='CR' "
				+ "THEN IFNULL((  CESS_AMT_ADVALOREM ),0)*-1 ELSE IFNULL(( "
				+ " CESS_AMT_ADVALOREM ),0) END)) AS  CESS_AMT ,(SUM(CASE WHEN"
				+ "   DOC_TYPE ='CR' THEN IFNULL((  IGST_AMT ),0)*-1 ELSE "
				+ "IFNULL((  IGST_AMT ),0) END)+SUM(CASE WHEN   DOC_TYPE"
				+ " ='CR' THEN IFNULL((  CGST_AMT ),0)*-1 ELSE IFNULL((  "
				+ "CGST_AMT ),0) END)+SUM(CASE WHEN   DOC_TYPE ='CR' THEN "
				+ "IFNULL((  SGST_AMT ),0)*-1 ELSE IFNULL((  SGST_AMT ),0)"
				+ " END)+SUM(CASE WHEN   DOC_TYPE ='CR' THEN IFNULL((  "
				+ "CESS_AMT_SPECIFIC ),0)*-1 ELSE IFNULL((  CESS_AMT_SPECIFIC ),0"
				+ ") END)+SUM(CASE WHEN   DOC_TYPE ='CR' THEN IFNULL(( "
				+ " CESS_AMT_ADVALOREM ),0)*-1 ELSE IFNULL((  CESS_AMT_ADVALOREM "
				+ "),0) END)) AS  TAX_PAYABLE  FROM ANX_OUTWARD_DOC_HEADER "
				+ "  WHERE  IS_PROCESSED = TRUE AND"
				+ " IS_DELETE =FALSE AND  AN_RETURN_TYPE='ANX1A' AND  "
				+ "AN_TABLE_SECTION  IN ('3A','3B','3C','3D','3E','3F','3G') "
				+ "AND   TCS_FLAG  = 'Y' ");
		if (!conditions.isEmpty() && !conditions.get(0).equals("")) {
			buffer.append(conditions.get(0));
		}
		buffer.append(" ) UNION ALL(SELECT 'TABLE-4' AS  "
				+ "TABLE_SECTION ,SUM( ECOM_VAL_SUPMADE ) AS  SUPPLIES_MADE ,"
				+ "SUM( ECOM_VAL_SUPRET ) AS  SUPPLIES_RETURN ,SUM( ECOM_NETVAL_SUP"
				+ " ) AS  NET_SUPPLIES , SUM( IGST_AMT ) AS  IGST_AMT ,SUM( CGST_AMT )"
				+ " AS  CGST_AMT , SUM( SGST_AMT ) AS  SGST_AMT , "
				+ "SUM( CESS_AMT ) AS  CESS_AMT , SUM( IGST_AMT + CGST_AMT + "
				+ "SGST_AMT + CESS_AMT ) AS TAX_PAYABLE FROM ANX_PROCESSED_TABLE4"
				+ "  WHERE  IS_DELETE  = FALSE AND IS_AMENDMENT=TRUE AND "
				+ "RETURN_TYPE='ANX1A' ");
		if (!conditions.isEmpty() && !conditions.get(1).equals("")) {
			buffer.append(conditions.get(1));
		}
		buffer.append(") UNION (SELECT 'TABLE-4' AS  "
				+ "TABLE_SECTION ,SUM( ECOM_VAL_SUPMADE ) AS  SUPPLIES_MADE ,"
				+ "SUM( ECOM_VAL_SUPRET ) AS  SUPPLIES_RETURN ,SUM( "
				+ "ECOM_NETVAL_SUP ) AS  NET_SUPPLIES , SUM( IGST_AMT ) AS "
				+ " IGST_AMT ,SUM( CGST_AMT ) AS  CGST_AMT , SUM( SGST_AMT ) AS"
				+ "  SGST_AMT , SUM( CESS_AMT ) AS  CESS_AMT ,SUM( IGST_AMT +"
				+ " CGST_AMT + SGST_AMT + CESS_AMT ) AS  TAX_PAYABLE FROM  "
				+ "ANX_PROCESSED_B2C   WHERE  IS_DELETE  = FALSE AND"
				+ " IS_AMENDMENT=TRUE AND RETURN_TYPE='ANX1A' ");
		if (!conditions.isEmpty() && !conditions.get(1).equals("")) {
			buffer.append(conditions.get(1));
		}
		buffer.append(" ) ) GROUP BY  " + "TABLE_SECTION ; ");
		return buffer.toString();
	}

}
