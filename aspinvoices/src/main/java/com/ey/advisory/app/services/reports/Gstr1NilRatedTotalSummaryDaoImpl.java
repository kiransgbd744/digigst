/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
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

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.views.client.Gstr1AspVerticalNilDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1NilRatedTotalSummaryDaoImpl")
public class Gstr1NilRatedTotalSummaryDaoImpl
		implements Gstr1OutwardVerticalProcessNilDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1NilRatedTotalSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Override
	public List<Object> getGstr1RSReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		List<String> tableType = request.getTableType();
		List<String> docType = request.getDocType();
		LocalDate docFromDate = request.getDocDateFrom();
		LocalDate docToDate = request.getDocDateTo();
		List<String> einvGenerated = request
				.geteInvGenerated();
		List<String> ewbGenerated = request
				.geteWbGenerated();
		
		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}

		String ewbResp = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbResp = ewbGenerated.get(0);
		}


		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		
		String GSTIN = null;

		
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

				
			}
		}

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();
		StringBuilder buildheader1 = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildQuery1.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildheader1.append(" WHERE SUPPLIER_GSTIN IN :gstinList");
				buildHeader.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			buildQuery1.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			buildheader1.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			buildHeader.append(" AND NEN.DERIVED_RET_PERIOD = :taxperiod ");
			
			/*buildQuery.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");*/
		}
		
		if (docFromDate != null && docToDate != null) {
			buildQuery.append(
					" AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}

		if (ewbResp != null && ewbResp.equalsIgnoreCase("YES")) {
			buildQuery.append(" AND EWB_NO_RESP IS NOT NULL ");
		}
		if (ewbResp != null && ewbResp.equalsIgnoreCase("NO")) {
			buildQuery.append(" AND EWB_NO_RESP IS NULL ");
			
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			buildQuery.append(" AND IRN_RESPONSE IS NOT NULL ");
			
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			buildQuery.append(" AND IRN_RESPONSE IS NULL ");
		}
		
		if (tableType != null && tableType.size() > 0) {
			buildQuery.append(" AND TAX_DOC_TYPE IN (:tableType) ");
		}

		if (docType != null && docType.size() > 0) {
			buildQuery.append(" AND DOC_TYPE IN (:docType) ");
			// nillQueryBuilder.append(" AND DOC_TYPE IN (:docType) ");
		}

		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType(); 

		String queryStr = createNilRatedTotalSummQueryString(
				buildQuery.toString(),buildheader1.toString(),buildHeader.toString(),buildQuery1.toString(),multiSupplyTypeAns);
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
		
		if (tableType != null && tableType.size() > 0
				&& !tableType.contains("")) {
			q.setParameter("tableType", tableType);
		}
		if (docType != null && docType.size() > 0
				&& !docType.contains("")) {
			q.setParameter("docType", docType);
		}
		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		

		
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessedNil(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1AspVerticalNilDto convertProcessedNil(Object[] arr) {
		Gstr1AspVerticalNilDto obj = new Gstr1AspVerticalNilDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setNilAmt(arr[4] != null ? arr[4].toString() : null);
		obj.setNonGstSupAmt(arr[5] != null ? arr[5].toString() : null);
		obj.setExemptedAmt(arr[6] != null ? arr[6].toString() : null);
		obj.setUnilAmt(arr[7] != null ? arr[7].toString() : null);
		obj.setUnonGstSupAmt(arr[8] != null ? arr[8].toString() : null);
		obj.setUexemptedAmt(arr[9] != null ? arr[9].toString() : null);
		obj.setSaveStatus(arr[10] != null ? arr[10].toString() : null);
		obj.setgSTNRefID(arr[11] != null ? arr[11].toString() : null);
		obj.setgSTNRefIDTime(arr[12] != null ? arr[12].toString() : null);
		obj.setgSTNErrorcode(arr[13] != null ? arr[13].toString() : null);
		obj.setgSTNErrorDescription(
				arr[14] != null ? arr[14].toString() : null);
		obj.setSlno(
				arr[15] != null ? arr[15].toString() : null);

		return obj;
	}

	private static String createNilRatedTotalSummQueryString(String buildQuery, String buildheader1, String buildHeader, String buildQuery1, String multiSupplyTypeAns) {

	
			return "SELECT SUPPLIER_GSTIN "
					+ "	,RETURN_PERIOD "
					+ "	,DERIVED_RET_PERIOD "
					+ "	,SUPPLY_TYPE "
					+ "	,SUM(ASP_NIL_AMT) ASP_NIL_AMT "
					+ "	,SUM(ASP_NON_GST_SUPPLIES) ASP_NON_GST_SUPPLIES "
					+ "	,SUM(ASP_EXMPTED_AMT) ASP_EXMPTED_AMT "
					+ "	,SUM(UI_NIL_AMT) UI_NIL_AMT "
					+ "	,SUM(UI_NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES "
					+ "	,SUM(UI_EXMPTED_AMT) UI_EXMPTED_AMT "
					+ "	,MAX(SAVE_STATUS) SAVE_STATUS "
					+ "	,MAX(GSTIN_REF_ID) GSTIN_REF_ID "
					+ "	,MAX(GSTIN_REF_ID_TIME) GSTIN_REF_ID_TIME "
					+ "	,MAX(GSTIN_ERROR_CODE) GSTIN_ERROR_CODE "
					+ "	,MAX(GSTIN_ERROR_DESCRIPTION_ASP) GSTIN_ERROR_DESCRIPTION_ASP "
					+ "	,ROW_NUMBER() OVER ( "
					+ "		ORDER BY SUPPLIER_GSTIN "
					+ "		) SNO "
					+ "FROM ( "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,DERIVED_RET_PERIOD "
					+ "		,SUPPLY_TYPE "
					+ "		,ASP_NIL_AMT "
					+ "		,ASP_NON_GST_SUPPLIES "
					+ "		,ASP_EXMPTED_AMT "
					+ "		,0 UI_NIL_AMT "
					+ "		,0 UI_NON_GST_SUPPLIES "
					+ "		,0 UI_EXMPTED_AMT "
					+ "		,'' SAVE_STATUS "
					+ "		,'' GSTIN_REF_ID "
					+ "		,'' GSTIN_REF_ID_TIME "
					+ "		,'' GSTIN_ERROR_CODE "
					+ "		,'' GSTIN_ERROR_DESCRIPTION_ASP "
					+ "	FROM ( "
					+ "		SELECT SUPPLY_TYPE "
					+ "			,DOC_KEY "
					+ "			,IFNULL(SUM(NIL_RATED_SUPPLIES), 0) ASP_NIL_AMT "
					+ "			,IFNULL(SUM(NON_GST_SUPPLIES), 0) ASP_NON_GST_SUPPLIES "
					+ "			,IFNULL(SUM(EXMPTED_SUPPLIES), 0) ASP_EXMPTED_AMT "
					+ "			,RETURN_PERIOD "
					+ "			,DERIVED_RET_PERIOD "
					+ "			,SUPPLIER_GSTIN "
					+ "			,DESCRIPTION_KEY "
					+ "		FROM (SELECT ( "
					+ "					CASE "
					+ "						WHEN ITM.ITM_TABLE_SECTION = '8A' "
					+ "							THEN 'INTER-B2B' "
					+ "						WHEN ITM.ITM_TABLE_SECTION = '8B' "
					+ "							THEN 'INTRA-B2B' "
					+ "						WHEN ITM.ITM_TABLE_SECTION = '8C' "
					+ "							THEN 'INTER-B2C' "
					+ "						WHEN ITM.ITM_TABLE_SECTION = '8D' "
					+ "							THEN 'INTRA-B2C' "
					+ "						END "
					+ "					) SUPPLY_TYPE "
					+ "				,( "
					+ "					SUPPLIER_GSTIN || '|' || HDR.RETURN_PERIOD || '|' || ITM.ITM_TABLE_SECTION "
					+ "					) AS DOC_KEY "
					+ "				,IFNULL(SUM(CASE "
					+ "							WHEN DOC_TYPE IN ( "
					+ "									'INV' "
					+ "									,'DR' "
					+ "									,'BOS', 'RDR', 'RNV' "
					+ "									) "
					+ "								AND ITM.SUPPLY_TYPE = 'NIL' "
					+ "								THEN  ITM.TAXABLE_VALUE "
					+ "							END), 0) - IFNULL(SUM(CASE "
					+ "							WHEN DOC_TYPE IN ('CR' ,'RCR') "
					+ "								AND ITM.SUPPLY_TYPE = 'NIL' "
					+ "								THEN  ITM.TAXABLE_VALUE "
					+ "							END), 0) AS NIL_RATED_SUPPLIES "
					+ "				,IFNULL(SUM(CASE "
					+ "							WHEN DOC_TYPE IN ( "
					+ "									'INV' "
					+ "									,'DR' "
					+ "									,'BOS', 'RDR', 'RNV' "
					+ "									) "
					+ "								AND  ITM.SUPPLY_TYPE "
					+ "									IN ( "
					+ "									'NON' "
					+ "									,'SCH3' "
					+ "									) "
					+ "								THEN  ITM.TAXABLE_VALUE "
					+ "							END), 0) - IFNULL(SUM(CASE "
					+ "							WHEN DOC_TYPE IN ('CR' ,'RCR') "
					+ "								AND  ITM.SUPPLY_TYPE "
					+ "								 IN ( "
					+ "									'NON' "
					+ "									,'SCH3' "
					+ "									) "
					+ "								THEN ITM.TAXABLE_VALUE "
					+ "							END), 0) AS NON_GST_SUPPLIES "
					+ "				,IFNULL(SUM(CASE "
					+ "							WHEN DOC_TYPE IN ( "
					+ "									'INV' "
					+ "									,'DR' "
					+ "									,'BOS', 'RDR', 'RNV' "
					+ "									) "
					+ "								AND  ITM.SUPPLY_TYPE = 'EXT' "
					+ "								THEN  ITM.TAXABLE_VALUE "
					+ "							END), 0) - IFNULL(SUM(CASE "
					+ "							WHEN DOC_TYPE IN ('CR' ,'RCR') "
					+ "								AND  ITM.SUPPLY_TYPE = 'EXT' "
					+ "								THEN ITM.TAXABLE_VALUE "
					+ "							END), 0) AS EXMPTED_SUPPLIES "
					+ "				,HDR.RETURN_PERIOD "
					+ "				,HDR.DERIVED_RET_PERIOD "
					+ "				,SUPPLIER_GSTIN "
					+ "				, ITM.ITM_TABLE_SECTION AS DESCRIPTION_KEY "
					+ "			FROM ANX_OUTWARD_DOC_HEADER HDR "
					+ "			INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "			WHERE ASP_INVOICE_STATUS = 2 "
					+ "				AND COMPLIANCE_APPLICABLE = TRUE "
					+ "				AND IS_DELETE = FALSE "
					+ "				AND RETURN_TYPE = 'GSTR1' "
					+ "				AND ITM.ITM_TABLE_SECTION "
					+ "					 IN ( "
					+ "					'8A' "
					+ "					,'8B' "
					+ "					,'8C' "
					+ "					,'8D' "
					+ "					) "
					+ "			GROUP BY HDR.RETURN_PERIOD "
					+ "				,HDR.DERIVED_RET_PERIOD "
					+ "				,SUPPLIER_GSTIN "
					+ "				,ITM.ITM_TABLE_SECTION "
					+ "			 "
					+ "			UNION ALL "
					+ "			 "
					+ "			SELECT ( "
					+ "					CASE "
					+ "						WHEN TABLE_SECTION = '8A' "
					+ "							THEN 'INTER-B2B' "
					+ "						WHEN TABLE_SECTION = '8B' "
					+ "							THEN 'INTRA-B2B' "
					+ "						WHEN TABLE_SECTION = '8C' "
					+ "							THEN 'INTER-B2C' "
					+ "						WHEN TABLE_SECTION = '8D' "
					+ "							THEN 'INTRA-B2C' "
					+ "						END "
					+ "					) SUPPLY_TYPE "
					+ "				,(SUPPLIER_GSTIN || '|' || RETURN_PERIOD || '|' || TABLE_SECTION) AS DOC_KEY "
					+ "				,CASE "
					+ "					WHEN SUPPLY_TYPE = 'NIL' "
					+ "						THEN IFNULL(TAXABLE_VALUE, 0) "
					+ "					END AS NIL_RATED_SUPPLIES "
					+ "				,CASE "
					+ "					WHEN SUPPLY_TYPE IN ('NON') "
					+ "						THEN IFNULL(TAXABLE_VALUE, 0) "
					+ "					END AS NON_GST_SUPPLIES "
					+ "				,CASE "
					+ "					WHEN SUPPLY_TYPE = 'EXT' "
					+ "						THEN IFNULL(TAXABLE_VALUE, 0) "
					+ "					END AS EXMPTED_SUPPLIES "
					+ "				,RETURN_PERIOD "
					+ "				,DERIVED_RET_PERIOD "
					+ "				,SUPPLIER_GSTIN "
					+ "				,TABLE_SECTION AS DESCRIPTION_KEY "
					+ "			FROM GSTR1_SUMMARY_NILEXTNON "
					+ "			WHERE IS_DELETE = FALSE "
					+ "			) "
					+ buildheader1
					+ "		GROUP BY SUPPLY_TYPE "
					+ "			,SUPPLIER_GSTIN "
					+ "			,DERIVED_RET_PERIOD "
					+ "			,DESCRIPTION_KEY "
					+ "			,RETURN_PERIOD "
					+ "			,DOC_KEY "
					+ "		) "
					+ "	 "
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,DERIVED_RET_PERIOD "
					+ "		,SUPPLY_TYPE "
					+ "		,0 ASP_NIL_AMT "
					+ "		,0 ASP_NON_GST_SUPPLIES "
					+ "		,0 ASP_EXMPTED_AMT "
					+ "		,UI_NIL_AMT "
					+ "		,UI_NON_GST_SUPPLIES "
					+ "		,UI_EXMPTED_AMT "
					+ "		,SAVE_STATUS "
					+ "		,GSTIN_REF_ID "
					+ "		,GSTIN_REF_ID_TIME "
					+ "		,GSTIN_ERROR_CODE "
					+ "		,GSTIN_ERROR_DESCRIPTION_ASP "
					+ "	FROM ( "
					+ "		SELECT SUPPLIER_GSTIN "
					+ "			,RETURN_PERIOD "
					+ "			,DERIVED_RET_PERIOD "
					+ "			,SUPPLY_TYPE "
					+ "			,SUM(NIL_AMT) UI_NIL_AMT "
					+ "			,SUM(NON_GST_SUPPLIES_AMT) UI_NON_GST_SUPPLIES "
					+ "			,SUM(EXMPTED_AMT) UI_EXMPTED_AMT "
					+ "			,SAVE_STATUS "
					+ "			,GSTIN_REF_ID "
					+ "			,GSTIN_REF_ID_TIME "
					+ "			,GSTIN_ERROR_CODE "
					+ "			,GSTIN_ERROR_DESCRIPTION_ASP "
					+ "		FROM ( "
					+ "			SELECT NEN.SUPPLIER_GSTIN "
					+ "				,NEN.RETURN_PERIOD "
					+ "				,NEN.DERIVED_RET_PERIOD "
					+ "				,( "
					+ "					CASE "
					+ "						WHEN DESCRIPTION_KEY = '8A' "
					+ "							THEN 'INTER-B2B' "
					+ "						WHEN DESCRIPTION_KEY = '8B' "
					+ "							THEN 'INTRA-B2B' "
					+ "						WHEN DESCRIPTION_KEY = '8C' "
					+ "							THEN 'INTER-B2C' "
					+ "						WHEN DESCRIPTION_KEY = '8D' "
					+ "							THEN 'INTRA-B2C' "
					+ "						END "
					+ "					) AS SUPPLY_TYPE "
					+ "				,IFNULL(SUM(NIL_RATED_SUPPLIES), 0) NIL_AMT "
					+ "				,IFNULL(SUM(NON_GST_SUPPLIES), 0) NON_GST_SUPPLIES_AMT "
					+ "				,IFNULL(SUM(EXMPTED_SUPPLIES), 0) EXMPTED_AMT "
					+ "				,( "
					+ "					CASE "
					+ "						WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "							AND NEN.IS_DELETE = FALSE "
					+ "							THEN 'SAVED' "
					+ "						ELSE 'NOT_SAVED' "
					+ "						END "
					+ "					) SAVE_STATUS "
					+ "				,( "
					+ "					CASE "
					+ "						WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "							AND NEN.IS_DELETE = FALSE "
					+ "							THEN GSTNBATCH.GSTN_SAVE_REF_ID "
					+ "						ELSE NULL "
					+ "						END "
					+ "					) GSTIN_REF_ID "
					+ "				,( "
					+ "					CASE "
					+ "						WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "							AND NEN.IS_DELETE = FALSE "
					+ "							THEN GSTNBATCH.BATCH_DATE "
					+ "						ELSE NULL "
					+ "						END "
					+ "					) GSTIN_REF_ID_TIME "
					+ "				,( "
					+ "					CASE "
					+ "						WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "							AND NEN.IS_DELETE = FALSE "
					+ "							THEN IFNULL(GSTNBATCH.ERROR_CODE, '') "
					+ "						ELSE NULL "
					+ "						END "
					+ "					) GSTIN_ERROR_CODE "
					+ "				,( "
					+ "					CASE "
					+ "						WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "							AND NEN.IS_DELETE = FALSE "
					+ "							THEN IFNULL(GSTNBATCH.ERROR_DESC, '') "
					+ "						ELSE NULL "
					+ "						END "
					+ "					) GSTIN_ERROR_DESCRIPTION_ASP "
					+ "			FROM GSTR1_USERINPUT_NILEXTNON NEN "
					+ "			LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = NEN.BATCH_ID "
					+ "			WHERE NEN.IS_DELETE = FALSE "
					+ buildHeader
					+ "			GROUP BY NEN.SUPPLIER_GSTIN "
					+ "				,NEN.RETURN_PERIOD "
					+ "				,NEN.DERIVED_RET_PERIOD "
					+ "				,NEN.DESCRIPTION_KEY "
					+ "				,IS_SAVED_TO_GSTN "
					+ "				,NEN.IS_DELETE "
					+ "				,GSTN_SAVE_REF_ID "
					+ "				,BATCH_DATE "
					+ "				,ERROR_CODE "
					+ "				,ERROR_DESC "
					+ "			) "
					+ buildheader1
					+ "		GROUP BY SUPPLIER_GSTIN "
					+ "			,RETURN_PERIOD "
					+ "			,DERIVED_RET_PERIOD "
					+ "			,SUPPLY_TYPE "
					+ "			,SAVE_STATUS "
					+ "			,GSTIN_REF_ID "
					+ "			,GSTIN_REF_ID_TIME "
					+ "			,GSTIN_ERROR_CODE "
					+ "			,GSTIN_ERROR_DESCRIPTION_ASP "
					+ "		) "
					+ "	) "
					+ buildheader1
					+ "GROUP BY SUPPLIER_GSTIN "
					+ "	,RETURN_PERIOD "
					+ "	,DERIVED_RET_PERIOD "
					+ "	,SUPPLY_TYPE";
		
	
	}
	public static void main(String[] args) {
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();
		StringBuilder buildheader1 = new StringBuilder();
		StringBuilder buildHeader = new StringBuilder();
		String multiSupplyTypeAns = "A"; 

				buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildQuery1.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildheader1.append(" WHERE SUPPLIER_GSTIN IN :gstinList");
				buildHeader.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList");
	
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			buildQuery1.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			buildheader1.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
			buildHeader.append(" AND NEN.DERIVED_RET_PERIOD = :taxperiod ");
			
		
		System.out.println(createNilRatedTotalSummQueryString(
				buildQuery.toString(),buildheader1.toString(),buildHeader.toString(),buildQuery1.toString(),multiSupplyTypeAns));
	}
}
