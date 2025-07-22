package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.views.client.AspProcessVsSubmitSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Slf4j
@Component("AspProcessVsSubmitSummaryReportDaoImpl")
public class AspProcessVsSubmitSummaryReportDaoImpl
		implements AspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	static Integer cutoffPeriod = null;

	@Override
	public List<Object> aspProcessVsSubmitDaoReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
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

		StringBuilder buildHeader = new StringBuilder();
		StringBuilder buildHeaderHdr = new StringBuilder();
		StringBuilder buildHeaderForNil = new StringBuilder();

		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" HDR.SUPPLIER_GSTIN IN :gstinList");
				buildHeaderHdr.append(" HDR.SUPPLIER_GSTIN IN :gstinList");
				buildHeaderForNil.append(" SUPPLIER_GSTIN IN :gstinList ");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND HDR.PROFIT_CENTRE IN :pcList");
				buildHeaderHdr.append(" AND HDR.PROFIT_CENTRE IN :pcList");
				buildHeader.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND HDR.PLANT_CODE IN :plantList ");
				buildHeaderHdr.append(" AND HDR.PLANT_CODE IN :plantList ");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
				buildHeaderHdr
						.append(" AND HDR.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader
						.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
				buildHeaderHdr
						.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND HDR.DIVISION IN :divisionList");
				buildHeaderHdr.append(" AND HDR.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND HDR.LOCATION IN :locationList");
				buildHeaderHdr.append(" AND HDR.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS1 IN :ud1List");
				buildHeaderHdr.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS2 IN :ud2List");
				buildHeaderHdr.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS3 IN :ud3List");
				buildHeaderHdr.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS4 IN :ud4List");
				buildHeaderHdr.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS5 IN :ud5List");
				buildHeaderHdr.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS6 IN :ud6List");
				buildHeaderHdr.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {

			buildHeader.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");

			buildHeaderHdr.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo");
			buildHeaderForNil.append(
					" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo");

		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString(),
				buildHeaderHdr.toString(), buildHeaderForNil.toString(),
				multiSupplyTypeAns);
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
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Object arr[] : list) {
				verticalHsnList.add(convertTransactionalLevel(arr));
			}
		}
		return verticalHsnList;
	}

	private AspProcessVsSubmitSummaryDto convertTransactionalLevel(
			Object[] arr) {
		AspProcessVsSubmitSummaryDto obj = new AspProcessVsSubmitSummaryDto();

		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setTaxPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setTransType(arr[2] != null ? arr[2].toString() : null);
		obj.setProcessCount(arr[3] != null ? arr[3].toString() : null);
		obj.setProcessTaxableValue(arr[4] != null ? arr[4].toString() : null);
		obj.setProcessTotalTax(arr[5] != null ? arr[5].toString() : null);
		obj.setProcessIgst(arr[6] != null ? arr[6].toString() : null);
		obj.setProcessCgst(arr[7] != null ? arr[7].toString() : null);
		obj.setProcessSgst(arr[8] != null ? arr[8].toString() : null);
		obj.setProcessCess(arr[9] != null ? arr[9].toString() : null);
		obj.setSubmitCount(arr[10] != null ? arr[10].toString() : null);
		obj.setSubmitTaxable(arr[11] != null ? arr[11].toString() : null);
		obj.setSubmitTotal(arr[12] != null ? arr[12].toString() : null);
		obj.setSubmitIgst(arr[13] != null ? arr[13].toString() : null);
		obj.setSubmitCgst(arr[14] != null ? arr[14].toString() : null);
		obj.setSubmitSgst(arr[15] != null ? arr[15].toString() : null);
		obj.setSubmitCess(arr[16] != null ? arr[16].toString() : null);
		obj.setDerTaxPeriod(arr[17] != null ? arr[17].toString() : null);
		obj.setDiffCount(arr[18] != null ? arr[18].toString() : null);
		obj.setDiffTaxableValue(
				arr[19] != null ? addSingleQuote(arr[19].toString()) : null);
		obj.setDiffTotalTax(arr[20] != null
				? convertStringToBigDecimal(arr[20].toString()) : null);
		obj.setDiffIgst(
				arr[21] != null ? addSingleQuote(arr[21].toString()) : null);
		obj.setDiffCgst(
				arr[22] != null ? addSingleQuote(arr[22].toString()) : null);
		obj.setDiffSgst(
				arr[23] != null ? addSingleQuote(arr[23].toString()) : null);
		obj.setDiffCess(
				arr[24] != null ? addSingleQuote(arr[24].toString()) : null);
		obj.setGstnGetCalls(arr[25] != null ? arr[25].toString() : null);
		return obj;
	}

	public static BigDecimal convertStringToBigDecimal(String value)
			throws NumberFormatException {
		return new BigDecimal(value.trim());
	}

	public static String addSingleQuote(String input) {
		return "'" + input;
	}

	private static String createApiProcessedQueryString(String buildQuery,
			String buildHeaderHdr, String buildHeaderForNil,
			String multiSupplyTypeAns) {
		LOGGER.debug("buildQuery {}", buildQuery);
		LOGGER.debug("buildHeaderHdr {}", buildHeaderHdr);
		LOGGER.debug("buildHeaderForNil {}", buildHeaderForNil);

		StringBuilder build = new StringBuilder();

		build.append(" SELECT SUPPLIER_GSTIN " + "	,RETURN_PERIOD "
				+ "	,TRANSACTION_TYPE " + "	,ASP_COUNT "
				+ "	,ASP_TAXABLE_VALUE " + "	,ASP_TOTAL_TAX "
				+ "	,ASP_IGST_AMT " + "	,ASP_CGST_AMT " + "	,ASP_SGST_AMT "
				+ "	,ASP_CESS_AMT " + "	,GSTN_COUNT "
				+ "	,GSTN_TAXABLE_VALUE " + "	,GSTN_TOTAL_TAX "
				+ "	,GSTN_IGST_AMT " + "	,GSTN_CGST_AMT "
				+ "	,GSTN_SGST_AMT " + "	,GSTN_CESS_AMT "
				+ "	,DERIVED_RET_PERIOD "
				+ "	,(ASP_COUNT - GSTN_COUNT) AS DIFF_COUNT "
				+ "	,(ASP_TAXABLE_VALUE - GSTN_TAXABLE_VALUE) AS DIFF_TAXABLE_VALUE "
				+ "	,(ASP_TOTAL_TAX - GSTN_TOTAL_TAX) AS DIFF_TOTAL_TAX "
				+ "	,(ASP_IGST_AMT - GSTN_IGST_AMT) AS DIFF_IGST_AMT "
				+ "	,(ASP_CGST_AMT - GSTN_CGST_AMT) AS DIFF_CGST_AMT "
				+ "	,(ASP_SGST_AMT - GSTN_SGST_AMT) AS DIFF_SGST_AMT "
				+ "	,(ASP_CESS_AMT - GSTN_CESS_AMT) AS DIFF_CESS_AMT "
				+ "	,LASTEST_UPDATED_TIMESTAMP " + "FROM ( "
				+ "	SELECT SUPPLIER_GSTIN " + "		,RETURN_PERIOD "
				+ "		,TRANSACTION_TYPE " + "		,SUM(ASP_COUNT) ASP_COUNT "
				+ "		,SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE "
				+ "		,SUM(ASP_TOTAL_TAX) ASP_TOTAL_TAX "
				+ "		,SUM(ASP_IGST_AMT) AS ASP_IGST_AMT "
				+ "		,SUM(ASP_CGST_AMT) ASP_CGST_AMT "
				+ "		,SUM(ASP_SGST_AMT) ASP_SGST_AMT "
				+ "		,SUM(ASP_CESS_AMT) ASP_CESS_AMT "
				+ "		,SUM(GSTN_COUNT) GSTN_COUNT "
				+ "		,SUM(GSTN_TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "		,SUM(GSTN_TOTAL_TAX) GSTN_TOTAL_TAX "
				+ "		,SUM(GSTN_IGST_AMT) GSTN_IGST_AMT "
				+ "		,SUM(GSTN_CGST_AMT) GSTN_CGST_AMT "
				+ "		,SUM(GSTN_SGST_AMT) GSTN_SGST_AMT "
				+ "		,SUM(GSTN_CESS_AMT) GSTN_CESS_AMT "
				+ "		,DERIVED_RET_PERIOD " + "		,TO_CHAR(MAX(CASE "
				+ "					WHEN TO_CHAR(LASTEST_UPDATED_TIMESTAMP, 'YYYY-MM-DD') > '2016-07-01' "
				+ "						THEN ADD_SECONDS(LASTEST_UPDATED_TIMESTAMP, 19800) "
				+ "					ELSE NULL "
				+ "					END), 'YYYY-MM-DD HH24:MI:SS') AS LASTEST_UPDATED_TIMESTAMP "
				+ "	FROM ( " + "		SELECT GSTIN AS SUPPLIER_GSTIN "
				+ "			,RETURN_PERIOD " + "			,TRANSACTION_TYPE "
				+ "			,ASP_COUNT " + "			,ASP_TAXABLE_VALUE "
				+ "			,ASP_TOTAL_TAX " + "			,ASP_IGST_AMT "
				+ "			,ASP_CGST_AMT " + "			,ASP_SGST_AMT "
				+ "			,ASP_CESS_AMT " + "			,0 AS GSTN_COUNT "
				+ "			,0 AS GSTN_TAXABLE_VALUE "
				+ "			,0 AS GSTN_TOTAL_TAX "
				+ "			,0 AS GSTN_IGST_AMT "
				+ "			,0 AS GSTN_CGST_AMT "
				+ "			,0 AS GSTN_SGST_AMT "
				+ "			,0 AS GSTN_CESS_AMT "
				+ "			,DERIVED_RET_PERIOD "
				+ "			,'' LASTEST_UPDATED_TIMESTAMP " + "		FROM ( "
				+ "			SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,(CASE WHEN  ITM.ITM_TABLE_SECTION  IN ('4A','4B','6B','6C') THEN 'B2B (4, 6B, 6C)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('9A') AND  ITM.ITM_TAX_DOC_TYPE  = 'B2BA'      THEN 'B2B Amendment (9A)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('5A') AND  ITM.ITM_TAX_DOC_TYPE  = 'B2CL'      THEN 'B2CL (5)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION IN ('9A') AND ITM.ITM_TAX_DOC_TYPE  = 'B2CLA'     THEN 'B2CL Amendment (9A)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('6A') AND  ITM.ITM_TAX_DOC_TYPE  = 'EXPORTS'   THEN 'Exports (6A)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('9A') AND  ITM.ITM_TAX_DOC_TYPE  = 'EXPORTS-A' THEN 'Exports Amendment (9A)' "
				+ "				  END " + "				) AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT DOC_KEY) AS ASP_COUNT "
				+ "				,SUM(IFNULL( ITM.TAXABLE_VALUE , 0)) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.CESS_AMT_SPECIFIC, 0)) AS ASP_TOTAL_TAX "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "				,SUM(IFNULL(ITM.CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "				,SUM(IFNULL(ITM.SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "				,SUM(IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.CESS_AMT_SPECIFIC, 0)) AS ASP_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "			FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "			INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "

				+ "			WHERE ASP_INVOICE_STATUS = 2 "
				+ "				AND COMPLIANCE_APPLICABLE = TRUE "
				+ "				AND IS_DELETE = FALSE "
				+ "				AND  ITM.ITM_TAX_DOC_TYPE IN ( "
				+ "					'B2B' " + "					,'B2BA' "
				+ "					,'B2CL' " + "					,'B2CLA' "
				+ "					,'EXPORTS' "
				+ "					,'EXPORTS-A' " + "					) AND "
				+ buildQuery + "			GROUP BY  ITM.ITM_TABLE_SECTION "
				+ "				,SUPPLIER_GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				, ITM.ITM_TAX_DOC_TYPE "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,(CASE WHEN  ITM.ITM_TABLE_SECTION  IN ('9B') AND  ITM.ITM_TAX_DOC_TYPE  = 'CDNR'  THEN 'CDN Registered (9B)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('9C') AND ITM.ITM_TAX_DOC_TYPE  = 'CDNRA' THEN 'CDN Registered Amendment (9C)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('9B') AND ITM.ITM_TAX_DOC_TYPE  IN ('CDNUR','CDNUR-B2CL','CDNUR-EXPORTS') THEN 'CDN UnRegistered (9B)' "
				+ "					   WHEN  ITM.ITM_TABLE_SECTION  IN ('9C') AND  ITM.ITM_TAX_DOC_TYPE  = 'CDNURA' THEN 'CDN UnRegistered Amendment (9C)' "
				+ "				   END "
				+ "					) AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT DOC_KEY) AS ASP_COUNT "
				+ "				,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') "
				+ "								 THEN  ITM.TAXABLE_VALUE  "
				+ "							END), 0) "
				+ "				- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN  ITM.TAXABLE_VALUE  "
				+ "							 END), 0) AS ASP_TAXABLE_VALUE "
				+ "				,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') "
				+ "								 THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "							END), 0) "
				+ "				- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') "
				+ "								  THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "							END), 0) AS ASP_TOTAL_TAX "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'DR' "
				+ "									,'RDR' "
				+ "									) "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'CR' "
				+ "									,'RCR' "
				+ "									) "
				+ "								THEN  ITM.IGST_AMT "
				+ "							END), 0) AS ASP_IGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'DR' "
				+ "									,'RDR' "
				+ "									) "
				+ "								THEN  ITM.CGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'CR' "
				+ "									,'RCR' "
				+ "									) "
				+ "								THEN ITM. CGST_AMT "
				+ "							END), 0) AS ASP_CGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'DR' "
				+ "									,'RDR' "
				+ "									) "
				+ "								THEN  ITM.SGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'CR' "
				+ "									,'RCR' "
				+ "									) "
				+ "								THEN  ITM.SGST_AMT "
				+ "							END), 0) AS ASP_SGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'DR' "
				+ "									,'RDR' "
				+ "									) "
				+ "								THEN IFNULL( ITM.CESS_AMT_SPECIFIC, 0) + IFNULL( ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'CR' "
				+ "									,'RCR' "
				+ "									) "
				+ "								THEN IFNULL( ITM.CESS_AMT_SPECIFIC, 0) + IFNULL( ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) AS ASP_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "			FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "			INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "

				+ "			     WHERE ASP_INVOICE_STATUS = 2 "
				+ "				AND COMPLIANCE_APPLICABLE = TRUE "
				+ "				AND IS_DELETE = FALSE "
				+ "				AND  ITM.ITM_TAX_DOC_TYPE  IN ( "
				+ "					'CDNR' " + "					,'CDNRA' "
				+ "					,'CDNUR' " + "					,'CDNURA' "
				+ "					,'CDNUR-B2CL' "
				+ "					,'CDNUR-EXPORTS' "
				+ "					) AND " + buildQuery
				+ "			GROUP BY  ITM.ITM_TABLE_SECTION  "
				+ "				,SUPPLIER_GSTIN "
				+ "				, HDR.RETURN_PERIOD "
				+ "				, ITM.ITM_TAX_DOC_TYPE "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,'NIL NON Exempt (8)' AS TRANSACTION_TYPE "
				+ "				,0 AS ASP_COUNT "
				+ "				,SUM(IFNULL(TAXABLE_VALUE, 0)) AS ASP_TAXABLE_VALUE "
				+ "				,0 AS ASP_TOTAL_TAX "
				+ "				,0 AS ASP_IGST_AMT "
				+ "				,0 AS ASP_CGST_AMT "
				+ "				,0 AS ASP_SGST_AMT "
				+ "				,0 AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD "
				+ "			FROM GSTR1_SUBMITTED_PS_TRANS HDR "
				+ "			WHERE TAX_DOC_TYPE = 'NILEXTNON' "
				+ "				AND RECORD_TYPE = 'ASP' "
				+ "				AND TABLE_NAME IN ('ANX_OUTWARD_DOC_HEADER') AND "
				+ buildQuery + "			GROUP BY SUPPLIER_GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,'NIL NON Exempt (8)' AS TRANSACTION_TYPE "
				+ "				,0 AS ASP_COUNT "
				+ "				,SUM(IFNULL(TAXABLE_VALUE, 0)) AS ASP_TAXABLE_VALUE "
				+ "				,0 AS ASP_TOTAL_TAX "
				+ "				,0 AS ASP_IGST_AMT "
				+ "				,0 AS ASP_CGST_AMT "
				+ "				,0 AS ASP_SGST_AMT "
				+ "				,0 AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD "
				+ "			FROM GSTR1_SUBMITTED_PS_TRANS HDR "
				+ "			WHERE TAX_DOC_TYPE = 'NILEXTNON' "
				+ "				AND RECORD_TYPE = 'ASP' "
				+ "				AND TABLE_NAME IN ( "
				+ "					'GSTR1_USERINPUT_NILEXTNON' "
				+ "					,'GSTR1_SUMMARY_NILEXTNON' "
				+ "					) " + "			GROUP BY SUPPLIER_GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,TRANSACTION_TYPE "
				+ "				,SUM(ASP_COUNT) ASP_COUNT "
				+ "				,SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE "
				+ "				,SUM(ASP_TOTAL_TAX) ASP_TOTAL_TAX "
				+ "				,SUM(ASP_IGST_AMT) ASP_IGST_AMT "
				+ "				,SUM(ASP_CGST_AMT) ASP_CGST_AMT "
				+ "				,SUM(ASP_SGST_AMT) ASP_SGST_AMT "
				+ "				,SUM(ASP_CESS_AMT) ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,( " + "						CASE "
				+ "							WHEN ITM.ITM_TAX_DOC_TYPE = 'B2CS' "
				+ "								THEN 'B2CS (7)' "
				+ "							END "
				+ "						) AS TRANSACTION_TYPE "
				+ "					,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999))) ASP_COUNT "
				+ "					,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('INV') THEN  ITM.TAXABLE_VALUE  END), 0) "
				+ "					+ IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') THEN  ITM.TAXABLE_VALUE  END), 0) "
				+ "					- IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN  ITM.TAXABLE_VALUE END), 0) AS ASP_TAXABLE_VALUE "
				+ "					,IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ( "
				+ "										'INV' "
				+ "										,'DR' "
				+ "										,'RDR' "
				+ "										) "
				+ "									THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ( "
				+ "										'CR' "
				+ "										,'RCR' "
				+ "										) "
				+ "									THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "								END), 0) AS ASP_TOTAL_TAX "
				+ "					,IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('INV') "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) + IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('DR') "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('CR') "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) AS ASP_IGST_AMT "
				+ "					,IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('INV') "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) + IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('DR') "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('CR') "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) AS ASP_CGST_AMT "
				+ "					,IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('INV') "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) + IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('DR') "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('CR') "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) AS ASP_SGST_AMT "
				+ "					,IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('INV') "
				+ "									THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "								END), 0) + IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('DR') "
				+ "									THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN DOC_TYPE IN ('CR') "
				+ "									THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "								END), 0) AS ASP_CESS_AMT "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "				INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "

				+ "				WHERE ASP_INVOICE_STATUS = 2 "
				+ "					AND COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND IS_DELETE = FALSE "
				+ "					AND ITM.ITM_TAX_DOC_TYPE = 'B2CS' AND "
				+ buildHeaderHdr
				+ "				GROUP BY ITM.ITM_TAX_DOC_TYPE "
				+ "					,SUPPLIER_GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,HDR.POS "
				+ "					,HDR.ECOM_GSTIN "
				+ "					,HDR.DIFF_PERCENT "
				+ "					,ITM.TAX_RATE "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				 " + "				UNION ALL "
				+ "				 "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,'B2CS (7)' AS TRANSACTION_TYPE "
				+ "					,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) || '|' || IFNULL(NEW_ECOM_GSTIN, '') || '|' || IFNULL(NEW_RATE, 9999))) ASP_COUNT "
				+ "					,SUM(NEW_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE "
				+ "					,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX "
				+ "					,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "					,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "					,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "					,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM GSTR1_PROCESSED_B2CS HDR "
				+ "				WHERE IS_DELETE = FALSE "
				+ "					AND IS_AMENDMENT = FALSE AND " + buildQuery
				+ "				GROUP BY SUPPLIER_GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,TRAN_TYPE "
				+ "					,NEW_POS "
				+ "					,DERIVED_RET_PERIOD "
				+ "					,NEW_ECOM_GSTIN "
				+ "					,NEW_RATE " + "				 "
				+ "				UNION ALL " + "				 "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,'B2CS Amendment (10)' AS TRANSACTION_TYPE "
				+ "					,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) || '|' || IFNULL(NEW_ECOM_GSTIN, '') || '|' || IFNULL(MONTH, ''))) ASP_COUNT "
				+ "					,SUM(NEW_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE "
				+ "					,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX "
				+ "					,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "					,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "					,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "					,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM GSTR1_PROCESSED_B2CS HDR "
				+ "				WHERE IS_DELETE = FALSE "
				+ "					AND IS_AMENDMENT = true AND " + buildQuery
				+ "				GROUP BY SUPPLIER_GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,TRAN_TYPE "
				+ "					,NEW_POS "
				+ "					,DERIVED_RET_PERIOD "
				+ "					,NEW_ECOM_GSTIN "
				+ "					,MONTH " + "				) "
				+ "			GROUP BY GSTIN " + "				,RETURN_PERIOD "
				+ "				,TRANSACTION_TYPE "
				+ "				,DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,'Adv. Received (11 Part I-11A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT (ASP)) AS ASP_COUNT "
				+ "				,SUM(NEW_GROSS_ADV_RECEIVED) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX "
				+ "				,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "				,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "				,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "				,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP "
				+ "					,NEW_GROSS_ADV_RECEIVED "
				+ "					,IFNULL(IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(CESS_AMT, 0) AS CESS_AMT "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM GSTR1_PROCESSED_ADV_RECEIVED HDR "
				+ "				WHERE IS_DELETE = FALSE AND " + buildQuery
				+ "					AND IS_AMENDMENT = FALSE "
				+ "				 " + "				UNION ALL "
				+ "				 "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) ASP "
				+ "					,ITM.TAXABLE_VALUE AS NEW_GROSS_ADV_RECEIVED "
				+ "					,IFNULL(ITM.IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(ITM.CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(ITM.SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM, 0) AS CESS_AMT "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "				JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "				WHERE HDR.TAX_DOC_TYPE = 'AT' "
				+ "					AND HDR.RETURN_TYPE = 'GSTR1' "
				+ "					AND HDR.COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND HDR.ASP_INVOICE_STATUS = 2 AND "
				+ buildHeaderHdr + "					AND IS_DELETE = FALSE "
				+ "				) " + "			GROUP BY GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,'Adv. Received Amended (11 Part II-11A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT (ASP)) AS ASP_COUNT "
				+ "				,SUM(NEW_GROSS_ADV_RECEIVED) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX "
				+ "				,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "				,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "				,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "				,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP "
				+ "					,NEW_GROSS_ADV_RECEIVED "
				+ "					,IFNULL(IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(CESS_AMT, 0) AS CESS_AMT "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM GSTR1_PROCESSED_ADV_RECEIVED HDR "
				+ "				WHERE IS_DELETE = FALSE AND " + buildQuery
				+ "					AND IS_AMENDMENT = TRUE "
				+ "				 " + "				UNION ALL "
				+ "				 "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) ASP "
				+ "					,ITM.TAXABLE_VALUE AS NEW_GROSS_ADV_RECEIVED "
				+ "					,IFNULL(ITM.IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(ITM.CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(ITM.SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM, 0) AS CESS_AMT "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "				JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "				WHERE HDR.TAX_DOC_TYPE = 'ATA' "
				+ "					AND HDR.RETURN_TYPE = 'GSTR1' "
				+ "					AND HDR.COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND HDR.ASP_INVOICE_STATUS = 2 AND "
				+ buildHeaderHdr + "					AND IS_DELETE = FALSE "
				+ "				) " + "			GROUP BY GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,'Adv. Adjusted (11 Part I-11B)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT ASP) AS ASP_COUNT "
				+ "				,SUM(NEW_GROSS_ADV_ADJUSTED) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX "
				+ "				,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "				,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "				,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "				,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP "
				+ "					,NEW_GROSS_ADV_ADJUSTED "
				+ "					,IFNULL(IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(CESS_AMT, 0) AS CESS_AMT "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM GSTR1_PROCESSED_ADV_ADJUSTMENT HDR "
				+ "				WHERE IS_DELETE = FALSE AND " + buildQuery
				+ "					AND IS_AMENDMENT = FALSE "
				+ "				 " + "				UNION ALL "
				+ "				 "
				+ "				SELECT HDR.SUPPLIER_GSTIN AS GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) AS ASP "
				+ "					,ITM.TAXABLE_VALUE AS ASP_TAXABLE_VALUE "
				+ "					,IFNULL(ITM.IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(ITM.CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(ITM.SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM, 0) AS CESS_AMT "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "				JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "				WHERE HDR.TAX_DOC_TYPE = 'TXP' "
				+ "					AND HDR.RETURN_TYPE = 'GSTR1' "
				+ "					AND HDR.COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND HDR.ASP_INVOICE_STATUS = 2 AND "
				+ buildHeaderHdr + "					AND IS_DELETE = FALSE "
				+ "				) " + "			GROUP BY GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,RETURN_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,'Adv. Adjusted Amended (11 Part II-11B)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT ASP) AS ASP_COUNT "
				+ "				,SUM(NEW_GROSS_ADV_ADJUSTED) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX "
				+ "				,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT "
				+ "				,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT "
				+ "				,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT "
				+ "				,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP "
				+ "					,NEW_GROSS_ADV_ADJUSTED "
				+ "					,IFNULL(IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(CESS_AMT, 0) AS CESS_AMT "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM GSTR1_PROCESSED_ADV_ADJUSTMENT HDR "
				+ "				WHERE IS_DELETE = FALSE AND " + buildQuery
				+ "					AND IS_AMENDMENT = TRUE "
				+ "				 " + "				UNION ALL "
				+ "				 "
				+ "				SELECT HDR.SUPPLIER_GSTIN AS GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) AS ASP "
				+ "					,ITM.TAXABLE_VALUE AS ASP_TAXABLE_VALUE "
				+ "					,IFNULL(ITM.IGST_AMT, 0) AS IGST_AMT "
				+ "					,IFNULL(ITM.CGST_AMT, 0) AS CGST_AMT "
				+ "					,IFNULL(ITM.SGST_AMT, 0) AS SGST_AMT "
				+ "					,IFNULL(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM, 0) AS CESS_AMT "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "				JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "				WHERE HDR.TAX_DOC_TYPE = 'TXPA' "
				+ "					AND HDR.RETURN_TYPE = 'GSTR1' "
				+ "					AND HDR.COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND HDR.ASP_INVOICE_STATUS = 2 AND "
				+ buildHeaderHdr + "					AND IS_DELETE = FALSE "
				+ "				) " + "			GROUP BY GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,RETURN_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,TRANSACTION_TYPE "
				+ "				,SUM(ASP_COUNT) ASP_COUNT "
				+ "				,SUM(TAXABLE_VALUE) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(TOTAL_TAX) AS ASP_TOTAL_TAX "
				+ "				,SUM(IGST) AS ASP_IGST_AMT "
				+ "				,SUM(CGST) AS ASP_CGST_AMT "
				+ "				,SUM(SGST) AS ASP_SGST_AMT "
				+ "				,SUM(CESS) AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,TRANSACTION_TYPE "
				+ "					,SUM(ASP_COUNT) AS ASP_COUNT "
				+ "					,SUM(TAXABLE_VALUE) AS TAXABLE_VALUE "
				+ "					,SUM(TOTAL_TAX) AS TOTAL_TAX "
				+ "					,SUM(IGST_AMT) AS IGST "
				+ "					,SUM(CGST_AMT) AS CGST "
				+ "					,SUM(SGST_AMT) AS SGST "
				+ "					,SUM(CESS_AMT) AS CESS "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM ( "
				+ "					SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "						,RETURN_PERIOD "
				+ "						,'HSN Summary (12)' AS TRANSACTION_TYPE "
				+ "						,IFNULL(TAX_RATE, 0) "
				+ "						,COUNT(DISTINCT HSNSAC || '-' || ( "
				+ "								CASE "
				+ "									WHEN LEFT(HSNSAC, 2) = 99 "
				+ "										THEN 'NA' "
				+ "									ELSE ITM_UQC "
				+ "									END "
				+ "								)) ASP_COUNT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(TAXABLE_VALUE, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(TAXABLE_VALUE, 0) "
				+ "										END), 0) "
				+ "							) AS TAXABLE_VALUE "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(TOTAL_TAX, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(TOTAL_TAX, 0) "
				+ "										END), 0) "
				+ "							) AS TOTAL_TAX "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(IGST_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(IGST_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS IGST_AMT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(CGST_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(CGST_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS CGST_AMT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(SGST_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(SGST_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS SGST_AMT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(CESS_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(CESS_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS CESS_AMT "
				+ "						,DERIVED_RET_PERIOD "
				+ "					FROM GSTR1_SUBMITTED_PS_TRANS HDR "
				+ "					WHERE TAX_DOC_TYPE = 'HSN_DIGI' "
				+ "						AND RECORD_TYPE = 'ASP' "
				+ "						AND TABLE_NAME IN ( "
				+ "							'ANX_OUTWARD_DOC_HEADER' "
				+ "							,'GSTR1_PROCESSED_B2CS' "
				+ "							) AND " + buildQuery
				+ "					GROUP BY RETURN_PERIOD "
				+ "						,DERIVED_RET_PERIOD "
				+ "						,SUPPLIER_GSTIN "
				+ "						,IFNULL(TAX_RATE, 0) "
				+ "					) " + "				GROUP BY RETURN_PERIOD "
				+ "					,DERIVED_RET_PERIOD "
				+ "					,GSTIN "
				+ "					,TRANSACTION_TYPE " + "				 "
				+ "				UNION ALL " + "				 "
				+ "				SELECT GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,TRANSACTION_TYPE "
				+ "					,SUM(ASP_COUNT) "
				+ "					,SUM(TAXABLE_VALUE) "
				+ "					,SUM(TOTAL_TAX) "
				+ "					,SUM(IGST_AMT) "
				+ "					,SUM(CGST_AMT) "
				+ "					,SUM(SGST_AMT) "
				+ "					,SUM(CESS_AMT) "
				+ "					,DERIVED_RET_PERIOD "
				+ "				FROM ( "
				+ "					SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "						,RETURN_PERIOD "
				+ "						,'HSN Summary (12)' AS TRANSACTION_TYPE "
				+ "						,IFNULL(TAX_RATE, 0) "
				+ "						,COUNT(DISTINCT HSNSAC || '-' || ( "
				+ "								CASE "
				+ "									WHEN LEFT(HSNSAC, 2) = 99 "
				+ "										THEN 'NA' "
				+ "									ELSE ITM_UQC "
				+ "									END "
				+ "								)) ASP_COUNT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(TAXABLE_VALUE, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(TAXABLE_VALUE, 0) "
				+ "										END), 0) "
				+ "							) AS TAXABLE_VALUE "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(TOTAL_TAX, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(TOTAL_TAX, 0) "
				+ "										END), 0) "
				+ "							) AS TOTAL_TAX "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(IGST_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(IGST_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS IGST_AMT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(CGST_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(CGST_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS CGST_AMT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(SGST_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(SGST_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS SGST_AMT "
				+ "						,( "
				+ "							IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ( "
				+ "												'INV' "
				+ "												,'BOS' "
				+ "												,'DR' "
				+ "												) "
				+ "											THEN IFNULL(CESS_AMT, 0) "
				+ "										END), 0) - IFNULL(SUM(CASE "
				+ "										WHEN DOC_TYPE IN ('CR') "
				+ "											THEN IFNULL(CESS_AMT, 0) "
				+ "										END), 0) "
				+ "							) AS CESS_AMT "
				+ "						,DERIVED_RET_PERIOD "
				+ "					FROM GSTR1_SUBMITTED_PS_TRANS HDR  "
				+ "					WHERE TAX_DOC_TYPE = 'HSN_DIGI' "
				+ "						AND RECORD_TYPE = 'ASP' "
				+ "						AND TABLE_NAME IN ('GSTR1_SUMMARY_NILEXTNON') AND "
				+ buildQuery + "					GROUP BY RETURN_PERIOD "
				+ "						,DERIVED_RET_PERIOD "
				+ "						,SUPPLIER_GSTIN "
				+ "						,IFNULL(TAX_RATE, 0) "
				+ "					) " + "				GROUP BY RETURN_PERIOD "
				+ "					,DERIVED_RET_PERIOD "
				+ "					,GSTIN "
				+ "					,TRANSACTION_TYPE " + "				) "
				+ "			GROUP BY RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD " + "				,GSTIN "
				+ "				,TRANSACTION_TYPE " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT ECOM_GSTIN) AS ASP_COUNT "
				+ "				,SUM(ASP_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE "
				+ "				,SUM(ASP_TOTAL_TAX) AS ASP_TOTAL_TAX "
				+ "				,SUM(ASP_IGST_AMT) AS ASP_IGST_AMT "
				+ "				,SUM(ASP_SGST_AMT) AS ASP_SGST_AMT "
				+ "				,SUM(ASP_CGST_AMT) AS ASP_CGST_AMT "
				+ "				,SUM(ASP_CESS_AMT) AS ASP_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD " + "			FROM ( "
				+ "				SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,CASE "
				+ "						WHEN  ITM.ITM_TABLE_SECTION  IN ( "
				+ "								'4A' "
				+ "								,'5A' "
				+ "								,'7A(1)' "
				+ "								,'7B(1)' "
				+ "								,'9B' "
				+ "								,'6B' "
				+ "								,'6C' "
				+ "								) "
				+ "							AND HDR.TCS_FLAG = 'Y' "
				+ "							THEN '14(i)' "
				+ "						WHEN  ITM.ITM_TABLE_SECTION  = '14(ii)' "
				+ "							AND HDR.TCS_FLAG = 'E' "
				+ "							THEN '14(ii)' "
				+ "						END AS TRANSACTION_TYPE "
				+ "					,IFNULL(HDR.ECOM_GSTIN, '') AS ECOM_GSTIN "
				+ "					,CASE "
				+ "						WHEN DOC_TYPE IN ( "
				+ "								'C' "
				+ "								,'CR' "
				+ "								) "
				+ "							THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
				+ "						ELSE IFNULL(ITM.TAXABLE_VALUE, 0) "
				+ "						END AS ASP_TAXABLE_VALUE "
				+ "					,CASE "
				+ "						WHEN DOC_TYPE IN ( "
				+ "								'C' "
				+ "								,'CR' "
				+ "								) "
				+ "							THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0)) "
				+ "						ELSE (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0)) "
				+ "						END AS ASP_TOTAL_TAX "
				+ "					,CASE "
				+ "						WHEN DOC_TYPE IN ( "
				+ "								'C' "
				+ "								,'CR' "
				+ "								) "
				+ "							THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
				+ "						ELSE IFNULL(ITM.IGST_AMT, 0) "
				+ "						END AS ASP_IGST_AMT "
				+ "					,CASE "
				+ "						WHEN DOC_TYPE IN ( "
				+ "								'C' "
				+ "								,'CR' "
				+ "								) "
				+ "							THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
				+ "						ELSE IFNULL(ITM.CGST_AMT, 0) "
				+ "						END AS ASP_CGST_AMT "
				+ "					,CASE "
				+ "						WHEN DOC_TYPE IN ( "
				+ "								'C' "
				+ "								,'CR' "
				+ "								) "
				+ "							THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
				+ "						ELSE IFNULL(ITM.SGST_AMT, 0) "
				+ "						END AS ASP_SGST_AMT "
				+ "					,CASE "
				+ "						WHEN DOC_TYPE IN ( "
				+ "								'C' "
				+ "								,'CR' "
				+ "								) "
				+ "							THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
				+ "						ELSE (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
				+ "						END AS ASP_CESS_AMT "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "				JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "

				+ "				WHERE  ITM.ITM_TABLE_SECTION  IN ( "
				+ "						'4A' " + "						,'5A' "
				+ "						,'7A(1)' "
				+ "						,'7B(1)' "
				+ "						,'9B' " + "						,'6B' "
				+ "						,'6C' "
				+ "						,'14(ii)' " + "						) "
				+ "					AND ASP_INVOICE_STATUS = 2 "
				+ "					AND HDR.ECOM_GSTIN IS NOT NULL "
				+ "					AND HDR.TCS_FLAG IN ( "
				+ "						'Y' " + "						,'E' "
				+ "						) "
				+ "					AND HDR.RETURN_TYPE = 'GSTR1' "
				+ "					AND COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND IS_DELETE = FALSE AND " + buildHeaderHdr
				+ "				) " + "			GROUP BY RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD " + "				,GSTIN "
				+ "				,TRANSACTION_TYPE " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'15' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT CASE "
				+ "						WHEN  ITM.ITM_TABLE_SECTION  = '15(i)' "
				+ "							THEN DOC_KEY "
				+ "						WHEN  ITM.ITM_TABLE_SECTION  = '15(ii)' "
				+ "							THEN IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(HDR.POS, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
				+ "						WHEN  ITM.ITM_TABLE_SECTION  = '15(iii)' "
				+ "							THEN DOC_KEY "
				+ "						WHEN  ITM.ITM_TABLE_SECTION  = '15(iv)' "
				+ "							THEN IFNULL(HDR.POS, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
				+ "						END) ASP_COUNT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.TAXABLE_VALUE "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.TAXABLE_VALUE "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.TAXABLE_VALUE "
				+ "							END), 0) AS ASP_TAXABLE_VALUE "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'INV' "
				+ "									,'DR' "
				+ "									,'RDR' "
				+ "									) "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'CR' "
				+ "									,'RCR' "
				+ "									) "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "							END), 0) AS ASP_TOTAL_TAX "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) AS ASP_IGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.CGST_AMT "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.CGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.CGST_AMT "
				+ "							END), 0) AS ASP_CGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.SGST_AMT "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.SGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.SGST_AMT "
				+ "							END), 0) AS ASP_SGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) AS ASP_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "			FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "			JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "

				+ "			WHERE  ITM.ITM_TABLE_SECTION  IN ( "
				+ "					'15(i)' " + "					,'15(ii)' "
				+ "					,'15(iii)' "
				+ "					,'15(iv)' " + "					) "
				+ "				AND ASP_INVOICE_STATUS = 2 "
				+ "				AND COMPLIANCE_APPLICABLE = TRUE "
				+ "				AND HDR.TCS_FLAG = 'O' "
				+ "				AND HDR.RETURN_TYPE = 'GSTR1' "
				+ "				AND IS_DELETE = FALSE AND " + buildHeaderHdr
				+ "			GROUP BY HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,SUPPLIER_GSTIN "
				+ "				, ITM.ITM_TABLE_SECTION  " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT SUPPLIER_GSTIN AS GSTIN "
				+ "				,HDR.RETURN_PERIOD " + "				,CASE "
				+ "					WHEN HDR.TABLE_SECTION = '15(i)' "
				+ "						THEN '15A(i)' "
				+ "					WHEN HDR.TABLE_SECTION = '15(ii)' "
				+ "						THEN '15A(ii)' "
				+ "					END AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999))) ASP_COUNT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.TAXABLE_VALUE "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.TAXABLE_VALUE "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.TAXABLE_VALUE "
				+ "							END), 0) AS ASP_TAXABLE_VALUE "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'INV' "
				+ "									,'DR' "
				+ "									,'RDR' "
				+ "									) "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ( "
				+ "									'CR' "
				+ "									,'RCR' "
				+ "									) "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) "
				+ "							END), 0) AS ASP_TOTAL_TAX "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.IGST_AMT "
				+ "							END), 0) AS ASP_IGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.CGST_AMT "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.CGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.CGST_AMT "
				+ "							END), 0) AS ASP_CGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN ITM.SGST_AMT "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN ITM.SGST_AMT "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN ITM.SGST_AMT "
				+ "							END), 0) AS ASP_SGST_AMT "
				+ "				,IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('INV') "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) + IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('DR') "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) - IFNULL(SUM(CASE "
				+ "							WHEN DOC_TYPE IN ('CR') "
				+ "								THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "							END), 0) AS ASP_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "			FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "			JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "

				+ "			WHERE HDR.TABLE_SECTION IN ( "
				+ "					'15(i)' " + "					,'15(ii)' "
				+ "					) "
				+ "				AND HDR.PRECEEDING_INV_NUM IS NOT NULL "
				+ "				AND ASP_INVOICE_STATUS = 2 "
				+ "				AND COMPLIANCE_APPLICABLE = TRUE "
				+ "				AND IS_DELETE = FALSE AND " + buildHeaderHdr
				+ "			GROUP BY HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,SUPPLIER_GSTIN "
				+ "				,HDR.TABLE_SECTION " + "			) "
				+ "		 " + "		UNION ALL " + "		 "
				+ "		SELECT GSTIN " + "			,RETURN_PERIOD "
				+ "			,TRANSACTION_TYPE " + "			,0 AS ASP_COUNT "
				+ "			,0 AS ASP_TAXABLE_VALUE "
				+ "			,0 AS ASP_TOTAL_TAX "
				+ "			,0 AS ASP_IGST_AMT "
				+ "			,0 AS ASP_CGST_AMT "
				+ "			,0 AS ASP_SGST_AMT "
				+ "			,0 AS ASP_CESS_AMT " + "			,GSTN_COUNT "
				+ "			,GSTN_TAXABLE_VALUE "
				+ "			,GSTN_TOTAL_TAX " + "			,GSTN_IGST_AMT "
				+ "			,GSTN_CGST_AMT " + "			,GSTN_SGST_AMT "
				+ "			,GSTN_CESS_AMT "
				+ "			,DERIVED_RET_PERIOD "
				+ "			,LASTEST_UPDATED_TIMESTAMP " + "		FROM ( "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'B2B (4, 6B, 6C)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_B2B_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('B2B') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'B2B Amendment (9A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_B2BA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('B2BA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'B2CL (5)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,0 GSTN_CGST_AMT "
				+ "				,0 GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_B2CL_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_B2CL_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('B2CL') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'B2CL Amendment (9A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,0 GSTN_CGST_AMT "
				+ "				,0 GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_B2CLA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_B2CLA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('B2CLA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'Exports (6A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,0 GSTN_CGST_AMT "
				+ "				,0 GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_EXP_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_EXP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('EXP') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'Exports Amendment (9A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,0 GSTN_CGST_AMT "
				+ "				,0 GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_EXPA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_EXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('EXPA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'CDN Registered (9B)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,( " + "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) "
				+ "					) AS GSTN_TAXABLE_VALUE "
				+ "				,( " + "					SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) - SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) "
				+ "					) AS GSTN_TOTAL_TAX " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_IGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) "
				+ "					) GSTN_CGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) "
				+ "					) GSTN_SGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_CDNR_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_CDNR_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('CDNR') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'CDN Registered Amendment (9C)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,( " + "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) "
				+ "					) AS GSTN_TAXABLE_VALUE "
				+ "				,( " + "					SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) - SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) "
				+ "					) AS GSTN_TOTAL_TAX " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_IGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.CGST_AMT "
				+ "								END), 0) "
				+ "					) GSTN_CGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.SGST_AMT "
				+ "								END), 0) "
				+ "					) GSTN_SGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_CDNRA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_CDNRA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('CDNRA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'CDN UnRegistered (9B)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,( " + "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) "
				+ "					) AS GSTN_TAXABLE_VALUE "
				+ "				,( " + "					SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) - SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) "
				+ "					) AS GSTN_TOTAL_TAX " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_IGST_AMT "
				+ "				,0 AS GSTN_CGST_AMT "
				+ "				,0 AS GSTN_SGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_CDNUR_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_CDNUR_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('CDNUR') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'CDN UnRegistered Amendment (9C)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,( " + "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.TAXABLE_VALUE "
				+ "								END), 0) "
				+ "					) AS GSTN_TAXABLE_VALUE "
				+ "				,( " + "					SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) - SUM(( "
				+ "							CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) "
				+ "								END "
				+ "							)) "
				+ "					) AS GSTN_TOTAL_TAX " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.IGST_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_IGST_AMT "
				+ "				,0 AS GSTN_CGST_AMT "
				+ "				,0 AS GSTN_SGST_AMT " + "				,( "
				+ "					IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'D' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) - IFNULL(SUM(CASE "
				+ "								WHEN NOTE_TYPE = 'C' "
				+ "									THEN ITM.CESS_AMT "
				+ "								END), 0) "
				+ "					) AS GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_CDNURA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_CDNURA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('CDNURA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'B2CS (7)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_B2CS_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_B2CS_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('B2CS') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'B2CS Amendment (10)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_B2CSA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_B2CSA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('B2CSA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'NIL NON Exempt (8)' AS TRANSACTION_TYPE "
				+ "				,0 GSTN_COUNT "
				+ "				,SUM(IFNULL(NIL_AMT, 0) + IFNULL(EXPT_AMT, 0) + IFNULL(NONGST_SUP_AMT, 0)) GSTN_TAXABLE_VALUE "
				+ "				,0 GSTN_TOTAL_TAX "
				+ "				,0 GSTN_IGST_AMT "
				+ "				,0 GSTN_CGST_AMT "
				+ "				,0 GSTN_SGST_AMT "
				+ "				,0 GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_NILEXTNON HDR "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('NIL') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'Adv. Received (11 Part I-11A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.ADVREC_AMT) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_AT_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_AT_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('AT') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'Adv. Adjusted (11 Part I-11B)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.ADVADJ_AMT) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_TXP_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_TXP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('TXP') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'Adv. Received Amended (11 Part II-11A)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.ADVREC_AMT) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_ATA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_ATA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('ATA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'Adv. Adjusted Amended (11 Part II-11B)' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.ADVADJ_AMT) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_TXPA_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_TXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('TXPA') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'HSN Summary (12)' AS TRANSACTION_TYPE "
				+ "				,COUNT(HSNORSAC) GSTN_COUNT "
				+ "				,SUM(TAXABLE_VALUE) GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_HSNORSAC HDR "
				+ "			LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND BT.IS_DELETE = FALSE "
				+ "				AND BT.API_SECTION = 'GSTR1' "
				+ "				AND BT.GET_TYPE IN ('HSN') "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,CASE "
				+ "					WHEN TABLE_SECTION = '14(i)' "
				+ "						THEN '14(i)' "
				+ "					WHEN TABLE_SECTION = '14(ii)' "
				+ "						THEN '14(ii)' "
				+ "					END AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT ID) GSTN_COUNT "
				+ "				,SUM(SUPPLIER_VALUE) AS GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(CESS_AMT) GSTN_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,MAX(CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_SUPECOM_HEADER "
				+ "			WHERE IS_DELETE = FALSE "
				+ "				AND TABLE_SECTION IN ( "
				+ "					'14(i)' " + "					,'14(ii)' "
				+ "					) " + "			GROUP BY GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,TABLE_SECTION " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT GSTIN " + "				,RETURN_PERIOD "
				+ "				,CASE "
				+ "					WHEN TABLE_SECTION = '14A(i)' "
				+ "						THEN '14A(i)' "
				+ "					WHEN TABLE_SECTION = '14A(ii)' "
				+ "						THEN '14A(ii)' "
				+ "					END AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT ID) GSTN_COUNT "
				+ "				,SUM(SUPPLIER_VALUE) AS GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(CESS_AMT) GSTN_CESS_AMT "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,MAX(CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_SUPECOM_AMD_HEADER "
				+ "			WHERE IS_DELETE = FALSE "
				+ "				AND TABLE_SECTION IN ( "
				+ "					'14A(i)' " + "					,'14A(ii)' "
				+ "					) " + "			GROUP BY GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,TABLE_SECTION " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,'15' AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(HDR.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_ECOMSUP_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_ECOMSUP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND TABLE_SECTION IN ( "
				+ "					'15(i)' " + "					,'15(ii)' "
				+ "					,'15(iii)' "
				+ "					,'15(iv)' " + "					) "
				+ "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD " + "			 "
				+ "			UNION ALL " + "			 "
				+ "			SELECT HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD " + "				,CASE "
				+ "					WHEN TABLE_SECTION = '15A(i)' "
				+ "						THEN '15A(i)' "
				+ "					WHEN TABLE_SECTION = '15A(ii)' "
				+ "						THEN '15A(ii)' "
				+ "					END AS TRANSACTION_TYPE "
				+ "				,COUNT(DISTINCT HDR.ID) GSTN_COUNT "
				+ "				,SUM(ITM.TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE "
				+ "				,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX "
				+ "				,SUM(ITM.IGST_AMT) GSTN_IGST_AMT "
				+ "				,SUM(ITM.CGST_AMT) GSTN_CGST_AMT "
				+ "				,SUM(ITM.SGST_AMT) GSTN_SGST_AMT "
				+ "				,SUM(ITM.CESS_AMT) GSTN_CESS_AMT "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,MAX(HDR.CREATED_ON) LASTEST_UPDATED_TIMESTAMP "
				+ "			FROM GETGSTR1_ECOMSUP_AMD_HEADER HDR "
				+ "			INNER JOIN GETGSTR1_ECOMSUPAMD_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "				AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "			WHERE HDR.IS_DELETE = FALSE "
				+ "				AND TABLE_SECTION IN ( "
				+ "					'15A(i)' " + "					,'15A(ii)' "
				+ "					) " + "			GROUP BY HDR.GSTIN "
				+ "				,HDR.RETURN_PERIOD "
				+ "				,HDR.DERIVED_RET_PERIOD "
				+ "				,TABLE_SECTION " + "			) " + "		) "
				+ "	WHERE  " + buildHeaderForNil
				+ "	GROUP BY SUPPLIER_GSTIN " + "		,RETURN_PERIOD "
				+ "		,TRANSACTION_TYPE " + "		,DERIVED_RET_PERIOD "
				+ "	);");

		return build.toString();
	}

	public static void main(String[] args) {
		StringBuilder buildHeader = new StringBuilder();
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildHeaderHdr = new StringBuilder();
		StringBuilder buildHeaderForNil = new StringBuilder();
		if (true) {
			buildQuery.append("  SUPPLIER_GSTIN IN :gstinList");
			buildHeaderHdr.append("  HDR.SUPPLIER_GSTIN IN :gstinList");
			buildHeaderForNil.append("  SUPPLIER_GSTIN IN :gstinList");

		}
		if (true) {
			buildQuery.append(" AND DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":taxPeriodFrom AND :taxPeriodTo");

			buildHeaderHdr.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderForNil.append(
					" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");

		}

		String queryTest = "SELECT SUPPLIER_GSTIN, RETURN_PERIOD, TRANSACTION_TYPE, ASP_COUNT, ASP_TAXABLE_VALUE, ASP_TOTAL_TAX, ASP_IGST_AMT, ASP_CGST_AMT, ASP_SGST_AMT, ASP_CESS_AMT, GSTN_COUNT, GSTN_TAXABLE_VALUE, GSTN_TOTAL_TAX, GSTN_IGST_AMT, GSTN_CGST_AMT, GSTN_SGST_AMT, GSTN_CESS_AMT, DERIVED_RET_PERIOD, (ASP_COUNT-GSTN_COUNT) AS DIFF_COUNT, (ASP_TAXABLE_VALUE-GSTN_TAXABLE_VALUE) AS DIFF_TAXABLE_VALUE, (ASP_TOTAL_TAX-GSTN_TOTAL_TAX) AS DIFF_TOTAL_TAX, (ASP_IGST_AMT-GSTN_IGST_AMT) AS DIFF_IGST_AMT, (ASP_CGST_AMT-GSTN_CGST_AMT) AS DIFF_CGST_AMT, (ASP_SGST_AMT-GSTN_SGST_AMT) AS DIFF_SGST_AMT, (ASP_CESS_AMT-GSTN_CESS_AMT) AS DIFF_CESS_AMT, LASTEST_UPDATED_TIMESTAMP FROM ( SELECT SUPPLIER_GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,SUM(ASP_COUNT) ASP_COUNT ,SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE ,SUM(ASP_TOTAL_TAX) ASP_TOTAL_TAX ,SUM(ASP_IGST_AMT) AS ASP_IGST_AMT ,SUM(ASP_CGST_AMT) ASP_CGST_AMT ,SUM(ASP_SGST_AMT) ASP_SGST_AMT ,SUM(ASP_CESS_AMT) ASP_CESS_AMT ,SUM(GSTN_COUNT) GSTN_COUNT ,SUM(GSTN_TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(GSTN_TOTAL_TAX) GSTN_TOTAL_TAX ,SUM(GSTN_IGST_AMT) GSTN_IGST_AMT ,SUM(GSTN_CGST_AMT) GSTN_CGST_AMT ,SUM(GSTN_SGST_AMT) GSTN_SGST_AMT ,SUM(GSTN_CESS_AMT) GSTN_CESS_AMT ,DERIVED_RET_PERIOD ,TO_CHAR(MAX(CASE  WHEN TO_CHAR(LASTEST_UPDATED_TIMESTAMP, 'YYYY-MM-DD') > '2016-07-01' THEN ADD_SECONDS(LASTEST_UPDATED_TIMESTAMP, 19800) ELSE NULL END), 'YYYY-MM-DD HH24:MI:SS') AS LASTEST_UPDATED_TIMESTAMP FROM ( SELECT GSTIN AS SUPPLIER_GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,ASP_COUNT ,ASP_TAXABLE_VALUE ,ASP_TOTAL_TAX ,ASP_IGST_AMT ,ASP_CGST_AMT ,ASP_SGST_AMT ,ASP_CESS_AMT ,0 AS GSTN_COUNT ,0 AS GSTN_TAXABLE_VALUE ,0 AS GSTN_TOTAL_TAX ,0 AS GSTN_IGST_AMT ,0 AS GSTN_CGST_AMT ,0 AS GSTN_SGST_AMT ,0 AS GSTN_CESS_AMT ,DERIVED_RET_PERIOD ,'' LASTEST_UPDATED_TIMESTAMP FROM ( SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,( CASE  WHEN TABLE_SECTION IN ( '4A' ,'4B' ,'6B' ,'6C' ) THEN 'B2B (4, 6B, 6C)' WHEN TABLE_SECTION IN ('9A') AND TAX_DOC_TYPE = 'B2BA' THEN 'B2B Amendment (9A)' WHEN TABLE_SECTION IN ('5A') AND TAX_DOC_TYPE = 'B2CL' THEN 'B2CL (5)' WHEN TABLE_SECTION IN ('9A') AND TAX_DOC_TYPE = 'B2CLA' THEN 'B2CL Amendment (9A)' WHEN TABLE_SECTION IN ('6A') AND TAX_DOC_TYPE = 'EXPORTS' THEN 'Exports (6A)' WHEN TABLE_SECTION IN ('9A') AND TAX_DOC_TYPE = 'EXPORTS-A' THEN 'Exports Amendment (9A)' END ) AS TRANSACTION_TYPE ,COUNT(DOC_KEY) AS ASP_COUNT ,SUM(IFNULL(TAXABLE_VALUE, 0)) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) + IFNULL(CESS_AMT_SPECIFIC, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT_ADVALOREM, 0) + IFNULL(CESS_AMT_SPECIFIC, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND TAX_DOC_TYPE IN ( 'B2B' ,'B2BA' ,'B2CL' ,'B2CLA' ,'EXPORTS' ,'EXPORTS-A' ) AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' GROUP BY TABLE_SECTION ,SUPPLIER_GSTIN ,RETURN_PERIOD ,TAX_DOC_TYPE ,DERIVED_RET_PERIOD UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,( CASE  WHEN TABLE_SECTION IN ('9B') AND TAX_DOC_TYPE = 'CDNR' THEN 'CDN Registered (9B)' WHEN TABLE_SECTION IN ('9C') AND TAX_DOC_TYPE = 'CDNRA' THEN 'CDN Registered Amendment (9C)' WHEN TABLE_SECTION IN ('9B') AND TAX_DOC_TYPE IN ( 'CDNUR' ,'CDNUR-B2CL' ,'CDNUR-EXPORTS' ) THEN 'CDN UnRegistered (9B)' WHEN TABLE_SECTION IN ('9C') AND TAX_DOC_TYPE = 'CDNURA' THEN 'CDN UnRegistered Amendment (9C)' END ) AS TRANSACTION_TYPE ,COUNT(DOC_KEY) AS ASP_COUNT ,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'DR' ,'RDR' ) THEN TAXABLE_VALUE END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN TAXABLE_VALUE END), 0) AS ASP_TAXABLE_VALUE ,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'DR' ,'RDR' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) + IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) + IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) END), 0) AS ASP_TOTAL_TAX ,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'DR' ,'RDR' ) THEN IGST_AMT END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN IGST_AMT END), 0) AS ASP_IGST_AMT ,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'DR' ,'RDR' ) THEN CGST_AMT END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN CGST_AMT END), 0) AS ASP_CGST_AMT ,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'DR' ,'RDR' ) THEN SGST_AMT END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN SGST_AMT END), 0) AS ASP_SGST_AMT ,IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 'DR' ,'RDR' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN (  'CR'  ,'RCR'  ) 	THEN IFNULL(CESS_AMT_SPECIFIC, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) END), 0) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND TAX_DOC_TYPE IN ( 'CDNR' ,'CDNRA' ,'CDNUR' ,'CDNURA' ,'CDNUR-B2CL' ,'CDNUR-EXPORTS' ) AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 GROUP BY TABLE_SECTION ,SUPPLIER_GSTIN ,RETURN_PERIOD ,TAX_DOC_TYPE ,DERIVED_RET_PERIOD UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,'NIL NON Exempt (8)' AS TRANSACTION_TYPE ,0 AS ASP_COUNT ,SUM(IFNULL(TAXABLE_VALUE, 0)) AS ASP_TAXABLE_VALUE ,0 AS ASP_TOTAL_TAX ,0 AS ASP_IGST_AMT ,0 AS ASP_CGST_AMT ,0 AS ASP_SGST_AMT ,0 AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_SUBMITTED_PS_TRANS WHERE TAX_DOC_TYPE = 'NILEXTNON' AND RECORD_TYPE = 'ASP' AND TABLE_NAME IN ('ANX_OUTWARD_DOC_HEADER') AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 GROUP BY SUPPLIER_GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,'NIL NON Exempt (8)' AS TRANSACTION_TYPE ,0 AS ASP_COUNT ,SUM(IFNULL(TAXABLE_VALUE, 0)) AS ASP_TAXABLE_VALUE ,0 AS ASP_TOTAL_TAX ,0 AS ASP_IGST_AMT ,0 AS ASP_CGST_AMT ,0 AS ASP_SGST_AMT ,0 AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_SUBMITTED_PS_TRANS WHERE TAX_DOC_TYPE = 'NILEXTNON' AND RECORD_TYPE = 'ASP' AND TABLE_NAME IN ( 	'GSTR1_USERINPUT_NILEXTNON' 	,'GSTR1_SUMMARY_NILEXTNON' 	) GROUP BY SUPPLIER_GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD UNION ALL SELECT GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,SUM(ASP_COUNT) ASP_COUNT ,SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE ,SUM(ASP_TOTAL_TAX) ASP_TOTAL_TAX ,SUM(ASP_IGST_AMT) ASP_IGST_AMT ,SUM(ASP_CGST_AMT) ASP_CGST_AMT ,SUM(ASP_SGST_AMT) ASP_SGST_AMT ,SUM(ASP_CESS_AMT) ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM ( SELECT SUPPLIER_GSTIN AS GSTIN 	,HDR.RETURN_PERIOD 	,( CASE WHEN HDR.TAX_DOC_TYPE = 'B2CS' 	THEN 'B2CS (7)' END ) AS TRANSACTION_TYPE 	,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999))) ASP_COUNT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.TAXABLE_VALUE 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE 	END), 0) AS ASP_TAXABLE_VALUE 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ( 'INV' ,'DR' ,'RDR' ) THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) 	END), 0) AS ASP_TOTAL_TAX 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.IGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT 	END), 0) AS ASP_IGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.CGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.CGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT 	END), 0) AS ASP_CGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.SGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.SGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT 	END), 0) AS ASP_SGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) AS ASP_CESS_AMT 	,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID WHERE ASP_INVOICE_STATUS = 2 	AND COMPLIANCE_APPLICABLE = TRUE 	AND IS_DELETE = FALSE 	AND HDR.TAX_DOC_TYPE = 'B2CS' AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' GROUP BY HDR.TAX_DOC_TYPE ,SUPPLIER_GSTIN ,HDR.RETURN_PERIOD ,HDR.POS ,HDR.ECOM_GSTIN ,HDR.DIFF_PERCENT ,ITM.TAX_RATE ,HDR.TAX_DOC_TYPE ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,'B2CS (7)' AS TRANSACTION_TYPE ,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) || '|' || IFNULL(NEW_ECOM_GSTIN, '') || '|' || IFNULL(NEW_RATE, 9999))) ASP_COUNT ,SUM(NEW_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_PROCESSED_B2CS WHERE IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' GROUP BY SUPPLIER_GSTIN ,RETURN_PERIOD ,TRAN_TYPE ,NEW_POS ,DERIVED_RET_PERIOD ,NEW_ECOM_GSTIN ,NEW_RATE UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,'B2CS Amendment (10)' AS TRANSACTION_TYPE ,COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) || '|' || IFNULL(NEW_ECOM_GSTIN, '') || '|' || IFNULL(MONTH, ''))) ASP_COUNT ,SUM(NEW_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_PROCESSED_B2CS WHERE IS_DELETE = FALSE AND IS_AMENDMENT = true AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 GROUP BY SUPPLIER_GSTIN ,RETURN_PERIOD ,TRAN_TYPE ,NEW_POS ,DERIVED_RET_PERIOD ,NEW_ECOM_GSTIN ,MONTH ) GROUP BY GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,DERIVED_RET_PERIOD UNION ALL SELECT  GSTIN ,RETURN_PERIOD ,'Adv. Received (11 Part I-11A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT (ASP)) AS ASP_COUNT ,SUM(NEW_GROSS_ADV_RECEIVED) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM( SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP ,NEW_GROSS_ADV_RECEIVED ,IFNULL(IGST_AMT, 0) AS IGST_AMT ,IFNULL(CGST_AMT, 0) AS CGST_AMT ,IFNULL(SGST_AMT, 0) AS SGST_AMT ,IFNULL(CESS_AMT, 0) AS CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_PROCESSED_ADV_RECEIVED WHERE IS_DELETE = FALSE AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 AND IS_AMENDMENT = FALSE UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,HDR.RETURN_PERIOD ,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) ASP ,ITM.TAXABLE_VALUE AS NEW_GROSS_ADV_RECEIVED ,IFNULL(ITM.IGST_AMT,0) AS IGST_AMT ,IFNULL(ITM.CGST_AMT,0) AS CGST_AMT ,IFNULL(ITM.SGST_AMT, 0)AS SGST_AMT ,IFNULL(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT ,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TAX_DOC_TYPE='AT' AND HDR.RETURN_TYPE='GSTR1' AND HDR.COMPLIANCE_APPLICABLE=TRUE AND HDR.ASP_INVOICE_STATUS = 2 AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' AND IS_DELETE=FALSE ) GROUP BY GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD UNION ALL SELECT  GSTIN ,RETURN_PERIOD ,'Adv. Received Amended (11 Part II-11A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT (ASP)) AS ASP_COUNT ,SUM(NEW_GROSS_ADV_RECEIVED) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM( SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP ,NEW_GROSS_ADV_RECEIVED ,IFNULL(IGST_AMT, 0) AS IGST_AMT ,IFNULL(CGST_AMT, 0) AS CGST_AMT ,IFNULL(SGST_AMT, 0) AS SGST_AMT ,IFNULL(CESS_AMT, 0) AS CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_PROCESSED_ADV_RECEIVED WHERE IS_DELETE = FALSE AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 AND IS_AMENDMENT = TRUE UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,HDR.RETURN_PERIOD ,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) ASP ,ITM.TAXABLE_VALUE AS NEW_GROSS_ADV_RECEIVED ,IFNULL(ITM.IGST_AMT,0) AS IGST_AMT ,IFNULL(ITM.CGST_AMT,0) AS CGST_AMT ,IFNULL(ITM.SGST_AMT, 0)AS SGST_AMT ,IFNULL(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT ,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TAX_DOC_TYPE='ATA' AND HDR.RETURN_TYPE='GSTR1' AND HDR.COMPLIANCE_APPLICABLE=TRUE AND HDR.ASP_INVOICE_STATUS = 2 AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' AND IS_DELETE=FALSE ) GROUP BY GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD UNION ALL SELECT GSTIN ,RETURN_PERIOD ,'Adv. Adjusted (11 Part I-11B)' AS TRANSACTION_TYPE ,COUNT(DISTINCT ASP) AS ASP_COUNT ,SUM(NEW_GROSS_ADV_ADJUSTED) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM( SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP ,NEW_GROSS_ADV_ADJUSTED ,IFNULL(IGST_AMT,0) AS IGST_AMT ,IFNULL(CGST_AMT,0) AS CGST_AMT ,IFNULL(SGST_AMT,0) AS SGST_AMT ,IFNULL(CESS_AMT,0) AS CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_PROCESSED_ADV_ADJUSTMENT WHERE IS_DELETE = FALSE AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 AND IS_AMENDMENT = FALSE UNION ALL SELECT HDR.SUPPLIER_GSTIN AS GSTIN ,HDR.RETURN_PERIOD ,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) AS ASP ,ITM.TAXABLE_VALUE AS ASP_TAXABLE_VALUE ,IFNULL(ITM.IGST_AMT,0) AS IGST_AMT ,IFNULL(ITM.CGST_AMT,0) AS CGST_AMT ,IFNULL(ITM.SGST_AMT,0) AS SGST_AMT ,IFNULL(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT ,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TAX_DOC_TYPE='TXP' AND HDR.RETURN_TYPE='GSTR1' AND HDR.COMPLIANCE_APPLICABLE=TRUE AND HDR.ASP_INVOICE_STATUS = 2 AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' AND IS_DELETE=FALSE ) GROUP BY GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD ,RETURN_PERIOD UNION ALL SELECT GSTIN ,RETURN_PERIOD ,'Adv. Adjusted Amended (11 Part II-11B)' AS TRANSACTION_TYPE ,COUNT(DISTINCT ASP) AS ASP_COUNT ,SUM(NEW_GROSS_ADV_ADJUSTED) AS ASP_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS ASP_TOTAL_TAX ,SUM(IFNULL(IGST_AMT, 0)) AS ASP_IGST_AMT ,SUM(IFNULL(CGST_AMT, 0)) AS ASP_CGST_AMT ,SUM(IFNULL(SGST_AMT, 0)) AS ASP_SGST_AMT ,SUM(IFNULL(CESS_AMT, 0)) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM( SELECT SUPPLIER_GSTIN AS GSTIN ,RETURN_PERIOD ,IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999) AS ASP ,NEW_GROSS_ADV_ADJUSTED ,IFNULL(IGST_AMT,0) AS IGST_AMT ,IFNULL(CGST_AMT,0) AS CGST_AMT ,IFNULL(SGST_AMT,0) AS SGST_AMT ,IFNULL(CESS_AMT,0) AS CESS_AMT ,DERIVED_RET_PERIOD FROM GSTR1_PROCESSED_ADV_ADJUSTMENT WHERE IS_DELETE = FALSE AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 AND IS_AMENDMENT = TRUE UNION ALL SELECT HDR.SUPPLIER_GSTIN AS GSTIN ,HDR.RETURN_PERIOD ,IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) AS ASP ,ITM.TAXABLE_VALUE AS ASP_TAXABLE_VALUE ,IFNULL(ITM.IGST_AMT,0) AS IGST_AMT ,IFNULL(ITM.CGST_AMT,0) AS CGST_AMT ,IFNULL(ITM.SGST_AMT,0) AS SGST_AMT ,IFNULL(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT ,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TAX_DOC_TYPE='TXPA' AND HDR.RETURN_TYPE='GSTR1' AND HDR.COMPLIANCE_APPLICABLE=TRUE AND HDR.ASP_INVOICE_STATUS = 2 AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' AND IS_DELETE=FALSE ) GROUP BY GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD ,RETURN_PERIOD UNION ALL SELECT GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,SUM(ASP_COUNT) ASP_COUNT ,SUM(TAXABLE_VALUE) AS ASP_TAXABLE_VALUE ,SUM(TOTAL_TAX) AS ASP_TOTAL_TAX ,SUM(IGST) AS ASP_IGST_AMT ,SUM(CGST) AS ASP_CGST_AMT ,SUM(SGST) AS ASP_SGST_AMT ,SUM(CESS) AS ASP_CESS_AMT ,DERIVED_RET_PERIOD FROM ( SELECT GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,SUM(ASP_COUNT) AS ASP_COUNT ,SUM(TAXABLE_VALUE) AS TAXABLE_VALUE ,SUM(TOTAL_TAX) AS TOTAL_TAX ,SUM(IGST_AMT) AS IGST ,SUM(CGST_AMT) AS CGST ,SUM(SGST_AMT) AS SGST ,SUM(CESS_AMT) AS CESS ,DERIVED_RET_PERIOD FROM ( SELECT SUPPLIER_GSTIN AS GSTIN 	,RETURN_PERIOD 	,'HSN Summary (12)' AS TRANSACTION_TYPE 	,IFNULL(TAX_RATE, 0) 	,COUNT(DISTINCT HSNSAC || '-' || ( 	CASE  WHEN LEFT(HSNSAC, 2) = 99 THEN 'NA' ELSE ITM_UQC END 	)) ASP_COUNT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(TAXABLE_VALUE, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(TAXABLE_VALUE, 0) END), 0) ) AS TAXABLE_VALUE 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(TOTAL_TAX, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(TOTAL_TAX, 0) END), 0) ) AS TOTAL_TAX 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(IGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(IGST_AMT, 0) END), 0) ) AS IGST_AMT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(CGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(CGST_AMT, 0) END), 0) ) AS CGST_AMT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(SGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(SGST_AMT, 0) END), 0) ) AS SGST_AMT 	,( IFNULL(SUM(CASE WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(CESS_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(CESS_AMT, 0) END), 0) ) AS CESS_AMT 	,DERIVED_RET_PERIOD FROM GSTR1_SUBMITTED_PS_TRANS WHERE TAX_DOC_TYPE = 'HSN_DIGI' 	AND RECORD_TYPE = 'ASP' 	AND TABLE_NAME IN ( 'ANX_OUTWARD_DOC_HEADER' ,'GSTR1_PROCESSED_B2CS' ) AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 GROUP BY RETURN_PERIOD 	,DERIVED_RET_PERIOD 	,SUPPLIER_GSTIN 	,IFNULL(TAX_RATE, 0) ) GROUP BY RETURN_PERIOD ,DERIVED_RET_PERIOD ,GSTIN ,TRANSACTION_TYPE UNION ALL SELECT GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,SUM(ASP_COUNT) ,SUM(TAXABLE_VALUE) ,SUM(TOTAL_TAX) ,SUM(IGST_AMT) ,SUM(CGST_AMT) ,SUM(SGST_AMT) ,SUM(CESS_AMT) ,DERIVED_RET_PERIOD FROM ( SELECT SUPPLIER_GSTIN AS GSTIN 	,RETURN_PERIOD 	,'HSN Summary (12)' AS TRANSACTION_TYPE 	,IFNULL(TAX_RATE, 0) 	,COUNT(DISTINCT HSNSAC || '-' || ( 	CASE  WHEN LEFT(HSNSAC, 2) = 99 THEN 'NA' ELSE ITM_UQC END 	)) ASP_COUNT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(TAXABLE_VALUE, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(TAXABLE_VALUE, 0) END), 0) ) AS TAXABLE_VALUE 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(TOTAL_TAX, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(TOTAL_TAX, 0) END), 0) ) AS TOTAL_TAX 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(IGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(IGST_AMT, 0) END), 0) ) AS IGST_AMT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(CGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(CGST_AMT, 0) END), 0) ) AS CGST_AMT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(SGST_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(SGST_AMT, 0) END), 0) ) AS SGST_AMT 	,( IFNULL(SUM(CASE  WHEN DOC_TYPE IN ( 	'INV' 	,'BOS' 	,'DR' 	) THEN IFNULL(CESS_AMT, 0) END), 0) - IFNULL(SUM(CASE  WHEN DOC_TYPE IN ('CR') THEN IFNULL(CESS_AMT, 0) END), 0) ) AS CESS_AMT 	,DERIVED_RET_PERIOD FROM GSTR1_SUBMITTED_PS_TRANS WHERE TAX_DOC_TYPE = 'HSN_DIGI' 	AND RECORD_TYPE = 'ASP' 	AND TABLE_NAME IN ('GSTR1_SUMMARY_NILEXTNON') AND SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 GROUP BY RETURN_PERIOD 	,DERIVED_RET_PERIOD 	,SUPPLIER_GSTIN 	,IFNULL(TAX_RATE, 0) ) GROUP BY RETURN_PERIOD ,DERIVED_RET_PERIOD ,GSTIN ,TRANSACTION_TYPE ) GROUP BY RETURN_PERIOD ,DERIVED_RET_PERIOD ,GSTIN ,TRANSACTION_TYPE UNION ALL 	 SELECT GSTIN,RETURN_PERIOD,TRANSACTION_TYPE,COUNT(DISTINCT ASP_COUNT) AS ASP_COUNT,SUM(ASP_TAXABLE_VALUE) AS ASP_TAXABLE_VALUE,SUM(ASP_TOTAL_TAX) AS ASP_TOTAL_TAX,SUM(ASP_IGST_AMT) AS ASP_IGST_AMT,SUM(ASP_SGST_AMT) AS ASP_SGST_AMT,SUM(ASP_CGST_AMT) AS ASP_CGST_AMT, SUM(ASP_CESS_AMT) AS ASP_CESS_AMT,DERIVED_RET_PERIOD FROM( SELECT SUPPLIER_GSTIN AS GSTIN    ,HDR.RETURN_PERIOD        , CASE WHEN HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C') AND         HDR.TCS_FLAG ='Y' THEN '14(i)'  WHEN HDR.TABLE_SECTION='14(ii)' AND HDR.TCS_FLAG='E' THEN '14(ii)' WHEN HDR.TABLE_SECTION IN ('9A','9C') AND HDR.TCS_FLAG='Y' THEN '14A(i)'  WHEN TABLE_SECTION='14(ii)' AND HDR.PRECEEDING_INV_NUM IS NOT NULL  AND DOC_TYPE IN ('RNV','RDR','RCR') THEN '14A(ii)' END AS TRANSACTION_TYPE, (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999)) ASP_COUNT, CASE WHEN DOC_TYPE IN ('C','CR','RCR') THEN -1*IFNULL(ITM.TAXABLE_VALUE,0) ELSE IFNULL(ITM.TAXABLE_VALUE,0) END AS ASP_TAXABLE_VALUE, 	CASE WHEN DOC_TYPE IN ('C','CR','RCR') THEN -1*(IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) + IFNULL(ITM.IGST_AMT,0) + IFNULL(ITM.CGST_AMT,0) + IFNULL(ITM.SGST_AMT,0)) ELSE (IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) + IFNULL(ITM.IGST_AMT,0) + IFNULL(ITM.CGST_AMT,0) + IFNULL(ITM.SGST_AMT,0)) END AS ASP_TOTAL_TAX, 	CASE WHEN DOC_TYPE IN ('C','CR','RCR') THEN -1*IFNULL(ITM.IGST_AMT,0) ELSE IFNULL(ITM.IGST_AMT,0) END AS ASP_IGST_AMT, CASE WHEN DOC_TYPE IN ('C','CR','RCR') THEN -1*IFNULL(ITM.CGST_AMT,0) ELSE IFNULL(ITM.CGST_AMT,0) END AS ASP_CGST_AMT, CASE WHEN DOC_TYPE IN ('C','CR','RCR') THEN -1*IFNULL(ITM.SGST_AMT,0) ELSE IFNULL(ITM.SGST_AMT,0) END AS ASP_SGST_AMT, CASE WHEN DOC_TYPE IN ('C','CR','RCR') THEN -1*(IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0)) ELSE (IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0)) END AS ASP_CESS_AMT, HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TABLE_SECTION IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C', '14(ii)','9A','9C') AND ASP_INVOICE_STATUS = 2 AND HDR.ECOM_GSTIN IS NOT NULL AND HDR.TCS_FLAG IN ('Y','E') AND HDR.RETURN_TYPE='GSTR1' AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405') GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,GSTIN,TRANSACTION_TYPE UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,HDR.RETURN_PERIOD ,'15' AS TRANSACTION_TYPE, COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999))) ASP_COUNT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.TAXABLE_VALUE 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE 	END), 0) AS ASP_TAXABLE_VALUE 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ( 'INV' ,'DR' ,'RDR' ) THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) 	END), 0) AS ASP_TOTAL_TAX 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.IGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT 	END), 0) AS ASP_IGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.CGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.CGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT 	END), 0) AS ASP_CGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.SGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.SGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT 	END), 0) AS ASP_SGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) AS ASP_CESS_AMT ,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TABLE_SECTION IN ('15(i)', '15(ii)') AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.TCS_FLAG='O' AND IS_DELETE = FALSE AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' GROUP BY HDR.RETURN_PERIOD 	,HDR.DERIVED_RET_PERIOD ,SUPPLIER_GSTIN ,HDR.TABLE_SECTION	 UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN ,HDR.RETURN_PERIOD ,CASE WHEN HDR.TABLE_SECTION='15(i)' THEN '15A(i)' WHEN HDR.TABLE_SECTION='15(ii)' THEN '15A(ii)' END AS TRANSACTION_TYPE, COUNT(DISTINCT (IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999))) ASP_COUNT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.TAXABLE_VALUE 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE 	END), 0) AS ASP_TAXABLE_VALUE 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ( 'INV' ,'DR' ,'RDR' ) THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ( 'CR' ,'RCR' ) THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) + IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) 	END), 0) AS ASP_TOTAL_TAX 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.IGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT 	END), 0) AS ASP_IGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.CGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.CGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT 	END), 0) AS ASP_CGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN ITM.SGST_AMT 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN ITM.SGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT 	END), 0) AS ASP_SGST_AMT 	,IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) + IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) - IFNULL(SUM(CASE 	WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) 	END), 0) AS ASP_CESS_AMT ,HDR.DERIVED_RET_PERIOD FROM ANX_OUTWARD_DOC_HEADER HDR JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE HDR.TABLE_SECTION IN ('15(i)', '15(ii)') AND HDR.PRECEEDING_INV_NUM IS NOT NULL AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND HDR.SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND HDR.DERIVED_RET_PERIOD BETWEEN '202404' AND '202405' GROUP BY HDR.RETURN_PERIOD 	,HDR.DERIVED_RET_PERIOD 	,SUPPLIER_GSTIN 	,HDR.TABLE_SECTION) UNION ALL SELECT GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,0 AS ASP_COUNT ,0 AS ASP_TAXABLE_VALUE ,0 AS ASP_TOTAL_TAX ,0 AS ASP_IGST_AMT ,0 AS ASP_CGST_AMT ,0 AS ASP_SGST_AMT ,0 AS ASP_CESS_AMT ,GSTN_COUNT ,GSTN_TAXABLE_VALUE ,GSTN_TOTAL_TAX ,GSTN_IGST_AMT ,GSTN_CGST_AMT ,GSTN_SGST_AMT ,GSTN_CESS_AMT ,DERIVED_RET_PERIOD ,LASTEST_UPDATED_TIMESTAMP FROM ( SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'B2B (4, 6B, 6C)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_B2B_HEADER HDR INNER JOIN GETGSTR1_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('B2B') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'B2B Amendment (9A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_B2BA_HEADER HDR INNER JOIN GETGSTR1_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('B2BA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'B2CL (5)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,0 GSTN_CGST_AMT ,0 GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_B2CL_HEADER HDR INNER JOIN GETGSTR1_B2CL_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('B2CL') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'B2CL Amendment (9A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,0 GSTN_CGST_AMT ,0 GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_B2CLA_HEADER HDR INNER JOIN GETGSTR1_B2CLA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('B2CLA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'Exports (6A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,0 GSTN_CGST_AMT ,0 GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_EXP_HEADER HDR INNER JOIN GETGSTR1_EXP_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('EXP') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'Exports Amendment (9A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,0 GSTN_CGST_AMT ,0 GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_EXPA_HEADER HDR INNER JOIN GETGSTR1_EXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('EXPA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'CDN Registered (9B)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.TAXABLE_VALUE 	END), 0) ) AS GSTN_TAXABLE_VALUE ,( SUM(( CASE 	WHEN NOTE_TYPE = 'D' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) - SUM(( CASE 	WHEN NOTE_TYPE = 'C' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) ) AS GSTN_TOTAL_TAX ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.IGST_AMT 	END), 0) ) AS GSTN_IGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.CGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.CGST_AMT 	END), 0) ) GSTN_CGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.SGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.SGST_AMT 	END), 0) ) GSTN_SGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.CESS_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.CESS_AMT 	END), 0) ) AS GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_CDNR_HEADER HDR INNER JOIN GETGSTR1_CDNR_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('CDNR') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'CDN Registered Amendment (9C)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.TAXABLE_VALUE 	END), 0) ) AS GSTN_TAXABLE_VALUE ,( SUM(( CASE 	WHEN NOTE_TYPE = 'D' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) - SUM(( CASE 	WHEN NOTE_TYPE = 'C' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) ) AS GSTN_TOTAL_TAX ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.IGST_AMT 	END), 0) ) AS GSTN_IGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.CGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.CGST_AMT 	END), 0) ) GSTN_CGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.SGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.SGST_AMT 	END), 0) ) GSTN_SGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.CESS_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.CESS_AMT 	END), 0) ) AS GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_CDNRA_HEADER HDR INNER JOIN GETGSTR1_CDNRA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('CDNRA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'CDN UnRegistered (9B)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.TAXABLE_VALUE 	END), 0) ) AS GSTN_TAXABLE_VALUE ,( SUM(( CASE 	WHEN NOTE_TYPE = 'D' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) - SUM(( CASE 	WHEN NOTE_TYPE = 'C' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) ) AS GSTN_TOTAL_TAX ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.IGST_AMT 	END), 0) ) AS GSTN_IGST_AMT ,0 AS GSTN_CGST_AMT ,0 AS GSTN_SGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.CESS_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.CESS_AMT 	END), 0) ) AS GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_CDNUR_HEADER HDR INNER JOIN GETGSTR1_CDNUR_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('CDNUR') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'CDN UnRegistered Amendment (9C)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.TAXABLE_VALUE 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.TAXABLE_VALUE 	END), 0) ) AS GSTN_TAXABLE_VALUE ,( SUM(( CASE 	WHEN NOTE_TYPE = 'D' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) - SUM(( CASE 	WHEN NOTE_TYPE = 'C' THEN (IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) 	END )) ) AS GSTN_TOTAL_TAX ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.IGST_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.IGST_AMT 	END), 0) ) AS GSTN_IGST_AMT ,0 AS GSTN_CGST_AMT ,0 AS GSTN_SGST_AMT ,( IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'D' THEN ITM.CESS_AMT 	END), 0) - IFNULL(SUM(CASE 	WHEN NOTE_TYPE = 'C' THEN ITM.CESS_AMT 	END), 0) ) AS GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_CDNURA_HEADER HDR INNER JOIN GETGSTR1_CDNURA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('CDNURA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'B2CS (7)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_B2CS_HEADER HDR INNER JOIN GETGSTR1_B2CS_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('B2CS') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'B2CS Amendment (10)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_B2CSA_HEADER HDR INNER JOIN GETGSTR1_B2CSA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('B2CSA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'NIL NON Exempt (8)' AS TRANSACTION_TYPE ,0 GSTN_COUNT ,SUM(IFNULL(NIL_AMT, 0) + IFNULL(EXPT_AMT, 0) + IFNULL(NONGST_SUP_AMT, 0)) GSTN_TAXABLE_VALUE ,0 GSTN_TOTAL_TAX ,0 GSTN_IGST_AMT ,0 GSTN_CGST_AMT ,0 GSTN_SGST_AMT ,0 GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_NILEXTNON HDR LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('NIL') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'Adv. Received (11 Part I-11A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.ADVREC_AMT) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_AT_HEADER HDR INNER JOIN GETGSTR1_AT_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('AT') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'Adv. Adjusted (11 Part I-11B)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.ADVADJ_AMT) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_TXP_HEADER HDR INNER JOIN GETGSTR1_TXP_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('TXP') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'Adv. Received Amended (11 Part II-11A)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.ADVREC_AMT) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_ATA_HEADER HDR INNER JOIN GETGSTR1_ATA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('ATA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'Adv. Adjusted Amended (11 Part II-11B)' AS TRANSACTION_TYPE ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.ADVADJ_AMT) GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_TXPA_HEADER HDR INNER JOIN GETGSTR1_TXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('TXPA') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD ,'HSN Summary (12)' AS TRANSACTION_TYPE ,COUNT(HSNORSAC) GSTN_COUNT ,SUM(TAXABLE_VALUE) GSTN_TAXABLE_VALUE ,SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(IGST_AMT) GSTN_IGST_AMT ,SUM(CGST_AMT) GSTN_CGST_AMT ,SUM(SGST_AMT) GSTN_SGST_AMT ,SUM(CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(BT.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_HSNORSAC HDR LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE AND BT.API_SECTION = 'GSTR1' AND BT.GET_TYPE IN ('HSN') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT GSTIN,RETURN_PERIOD,CASE WHEN TABLE_SECTION='14(i)' THEN '14(i)'WHEN TABLE_SECTION='14(ii)' THEN '14(ii)' END AS TRANSACTION_TYPE,COUNT(DISTINCT ID) GSTN_COUNT, SUM(SUPPLIER_VALUE) AS GSTN_TAXABLE_VALUE, SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) GSTN_TOTAL_TAX , SUM(IGST_AMT) GSTN_IGST_AMT,   SUM(CGST_AMT) GSTN_CGST_AMT,   SUM(SGST_AMT) GSTN_SGST_AMT,   SUM(CESS_AMT) GSTN_CESS_AMT, DERIVED_RET_PERIOD, MAX(CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_SUPECOM_HEADER WHERE IS_DELETE = FALSE AND TABLE_SECTION IN ('14(i)','14(ii)') GROUP BY GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD ,TABLE_SECTION UNION ALL SELECT GSTIN,   RETURN_PERIOD, CASE WHEN TABLE_SECTION='14A(i)' THEN '14A(i)' WHEN TABLE_SECTION='14A(ii)' THEN '14A(ii)' END AS TRANSACTION_TYPE, COUNT(DISTINCT ID) GSTN_COUNT, SUM(SUPPLIER_VALUE) AS GSTN_TAXABLE_VALUE, SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) GSTN_TOTAL_TAX , SUM(IGST_AMT) GSTN_IGST_AMT,   SUM(CGST_AMT) GSTN_CGST_AMT,   SUM(SGST_AMT) GSTN_SGST_AMT,   SUM(CESS_AMT) GSTN_CESS_AMT, DERIVED_RET_PERIOD, MAX(CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_SUPECOM_AMD_HEADER WHERE IS_DELETE = FALSE AND TABLE_SECTION IN ('14A(i)','14A(ii)') GROUP BY GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD ,TABLE_SECTION UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD , '15' AS TRANSACTION_TYPE   ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(HDR.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_ECOMSUP_HEADER HDR INNER JOIN GETGSTR1_ECOMSUP_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD   WHERE HDR.IS_DELETE = FALSE AND TABLE_SECTION IN ('15(i)', '15(ii)', '15(iii)', '15(iv)') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD UNION ALL SELECT HDR.GSTIN ,HDR.RETURN_PERIOD   ,CASE WHEN TABLE_SECTION='15A(i)' THEN '15A(i)'   WHEN TABLE_SECTION='15A(ii)' THEN '15A(ii)' END AS TRANSACTION_TYPE   ,COUNT(DISTINCT HDR.ID) GSTN_COUNT ,SUM(ITM.TAXABLE_VALUE) AS GSTN_TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0)) GSTN_TOTAL_TAX ,SUM(ITM.IGST_AMT) GSTN_IGST_AMT ,SUM(ITM.CGST_AMT) GSTN_CGST_AMT ,SUM(ITM.SGST_AMT) GSTN_SGST_AMT ,SUM(ITM.CESS_AMT) GSTN_CESS_AMT ,HDR.DERIVED_RET_PERIOD ,MAX(HDR.CREATED_ON) LASTEST_UPDATED_TIMESTAMP FROM GETGSTR1_ECOMSUP_AMD_HEADER HDR INNER JOIN GETGSTR1_ECOMSUPAMD_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD   WHERE HDR.IS_DELETE = FALSE AND TABLE_SECTION IN ('15A(i)','15A(ii)') GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD ,HDR.DERIVED_RET_PERIOD ,TABLE_SECTION ) ) WHERE SUPPLIER_GSTIN IN('33GSPTN0482G1Z9') AND DERIVED_RET_PERIOD BETWEEN 202404 AND 202405 GROUP BY SUPPLIER_GSTIN ,RETURN_PERIOD ,TRANSACTION_TYPE ,DERIVED_RET_PERIOD );";
		;

		System.out.println(queryTest);

	}

}
