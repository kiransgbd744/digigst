package com.ey.advisory.app.services.daos.gstr6a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6DigiComputeRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr6ReviewSummaryDaoImpl")
public class Gstr6ReviewSummaryDaoImpl implements Gstr6ReviewSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String DERIVED_RET_PERIOD_SQL = " AND (:taxPeriod IS NULL OR HDR.DERIVED_RET_PERIOD=:taxPeriod) ";

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("Gstr6DigiComputeRepository")
	private Gstr6DigiComputeRepository gstr6DigiComputeRepository;
	
	@Autowired
	@Qualifier("BasicCommonSecParam")
	private BasicCommonSecParam basicCommonSecParam;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ReviewSummaryDaoImpl.class);

	public List<Object[]> getSummaryDetails(
			final Annexure1SummaryReqDto reqDto) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setInwardSumDataSecuritySearchParams(reqDto);

		String taxPeriodReq = req.getTaxPeriod();
		List<Object[]> objSumDetails = new ArrayList<>();
		Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		gstinList = gstnDetailRepository.getGstinRegTypeISD(gstinList);

		StringBuilder sqlParam = new StringBuilder();
		StringBuilder sqlAPIParam = new StringBuilder();
		StringBuilder sqlDistribuParam = new StringBuilder();
		cGstnSql(sqlParam, sqlAPIParam, sqlDistribuParam, gstinList);
		String sql = getQuery(sqlParam.toString(), sqlAPIParam.toString(),
				sqlDistribuParam.toString());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 Part A Query: {}", sql);
		}
		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("taxPeriod", taxPeriod);
		gstnParameter(q, gstinList);
		objSumDetails = q.getResultList();
		return objSumDetails;

	}

	private void gstnParameter(Query q, List<String> gstinList) {
		if (gstinList != null && !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}
	}

	private void cGstnSql(StringBuilder sqlParam, StringBuilder sqlAPIParam,
			StringBuilder sqlDistribuParam, List<String> gstinList) {
		if (gstinList != null && !gstinList.isEmpty()) {
			sqlParam.append("AND HDR.CUST_GSTIN IN :gstinList ");
			sqlAPIParam.append("AND GSTIN IN :gstinList ");
			sqlDistribuParam.append("AND ISD_GSTIN IN :gstinList ");
		}
	}

	private String getQuery(String sqlParam, String sqlAPIParam,
			String sqlDistribuParam) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT SUM(IFNULL(ID_CNT,0)) as ID_CNT_ASP,");
		sql.append(" SUM(IFNULL(INVOICE_VALUE_ASP,0)) AS INVOICE_VALUEASP, ");
		sql.append(" SUM(IFNULL(TAXABLE_VALUE_ASP,0)) AS TAXABLE_VALUEASP, ");
		sql.append(" SUM(IFNULL(TOTAL_TAX_ASP,0)) AS TOTAL_TAXASP, ");
		sql.append(" SUM(IFNULL(IGST_AMOUNT_ASP,0)) AS IGST_AMOUNTASP, ");
		sql.append(" SUM(IFNULL(CGST_AMOUNT_ASP,0)) AS CGST_AMOUNTASP, ");
		sql.append(" SUM(IFNULL(SGST_AMOUNT_ASP,0)) AS SGST_AMOUNTASP, ");
		sql.append(" SUM(IFNULL(CESS_AMOUNT_ASP,0)) AS CESS_AMOUNTASP, ");
		sql.append(" IFNULL(ELIG_IND_ASP,ELIG_IND_GSTIN) as ELIG_INDICATOR, ");
		sql.append(
				" IFNULL(TAX_DOC_TYPE_ASP,TAX_DOC_TYPE_GSTIN) AS TAX_DOCUMENT_TYPE, ");
		sql.append(
				" IFNULL(CUST_GSTIN_ASP,GSTIN) AS CUST_GSTIN_ASP,IFNULL(TAX_PERIOD_ASP,TAX_PERIOD_GSTIN) AS TAX_PERIOD_ASP,  ");
		sql.append(" SUM(IFNULL(RECORD_COUNT_GSTIN,0)) as RECORD_COUNT_GST, ");
		sql.append(
				" SUM(IFNULL(INVOICE_VALUE_GSTIN,0)) AS INVOICE_VALUE_GST, ");
		sql.append(
				" SUM(IFNULL(TAXABLE_VALUE_GSTIN,0)) AS TAXABLE_VALUE_GST, ");
		sql.append(" sum(IFNULL(TOTAL_TAX_GSTIN,0)) AS TOTAL_TAX_GST, ");
		sql.append(" SUM(IFNULL(IGST_AMOUNT_GSTIN,0)) AS IGST_AMOUNT_GST, ");
		sql.append(" SUM(IFNULL(CGST_AMOUNT_GSTIN,0))  AS CGST_AMOUNT_GST, ");
		sql.append(" SUM(IFNULL(SGST_AMOUNT_GSTIN,0)) AS SGST_AMOUNT_GST, ");
		sql.append(" SUM(IFNULL(CESS_AMOUNT_GSTIN,0)) AS CESS_AMOUNT_GST,  ");
		sql.append(
				"IFNULL(ELIG_IND_GSTIN,ELIG_IND_ASP) AS ELIG_IND_GSTIN,IFNULL(TAX_DOC_TYPE_GSTIN,TAX_DOC_TYPE_ASP) AS TAX_DOC_TYPE_GSTIN  ");
		sql.append(" FROM  ");
		sql.append(" ((  ");
		sql.append(" select SUM(IFNULL(ID_COUNT,0)) as ID_CNT, ");
		sql.append(" SUM(INVOICE_VALUE) AS INVOICE_VALUE_ASP, ");
		sql.append(" SUM(TAXABLE_VALUE) AS TAXABLE_VALUE_ASP, ");
		sql.append(" SUM(IFNULL(TOTAL_TAX,0)) AS TOTAL_TAX_ASP, ");
		sql.append(" SUM(IFNULL(IGST_AMOUNT,0)) AS IGST_AMOUNT_ASP, ");
		sql.append(" SUM(IFNULL(CGST_AMOUNT,0)) AS CGST_AMOUNT_ASP, ");
		sql.append(" SUM(IFNULL(SGST_AMOUNT,0)) AS SGST_AMOUNT_ASP, ");
		sql.append(" SUM(IFNULL(CESS_AMOUNT,0)) AS CESS_AMOUNT_ASP, ");
		sql.append(" ELIG_IND AS ELIG_IND_ASP, ");
		sql.append(" TAX_DOC_TYPE AS TAX_DOC_TYPE_ASP, ");
		sql.append(
				" CUST_GSTIN AS CUST_GSTIN_ASP,RETURN_PERIOD AS TAX_PERIOD_ASP  ");
		sql.append(" FROM  ");
		sql.append(" (  ");
		sql.append(" SELECT 0 AS ID_COUNT, ");
		sql.append(" NULL AS INVOICE_VALUE, ");
		sql.append(" NULL AS TAXABLE_VALUE, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.ELIGIBLE_TAX_PAYABLE,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') ");
		sql.append(
				" THEN IFNULL(ITM.ELIGIBLE_TAX_PAYABLE,0) END),0) AS TOTAL_TAX, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.ELIGIBLE_IGST_AMT,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') ");
		sql.append(
				" THEN IFNULL(ITM.ELIGIBLE_IGST_AMT,0) END),0) AS IGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.ELIGIBLE_CGST_AMT,0) END),0) -  ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR')  ");
		sql.append(
				" THEN IFNULL(ITM.ELIGIBLE_CGST_AMT,0)END),0)  AS CGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.ELIGIBLE_SGST_AMT,0) END),0) -  ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') ");
		sql.append(
				" THEN IFNULL(ITM.ELIGIBLE_SGST_AMT,0)END),0)  AS SGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.ELIGIBLE_CESS_AMT,0) END),0) -  ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR')  ");
		sql.append(
				" THEN IFNULL(ITM.ELIGIBLE_CESS_AMT,0) END),0) AS CESS_AMOUNT, ");
		sql.append(" 'IS_ELG' AS ELIG_IND, ");
		sql.append(" HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD ");
		sql.append(" FROM ANX_INWARD_DOC_HEADER HDR  ");
		sql.append(" INNER JOIN   ");
		sql.append(
				" ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND ");
		sql.append(" HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");
		sql.append(" INNER JOIN   ");
		sql.append(" GSTIN_INFO GIF  ");
		sql.append(" ON HDR.CUST_GSTIN = GIF.GSTIN INNER JOIN  ");
		sql.append(" ENTITY_CONFG_PRMTR ECP ON  ");
		sql.append(" GIF.ENTITY_ID = ECP.ENTITY_ID  ");
		sql.append(" AND ECP.IS_DELETE = FALSE   ");
		sql.append(" AND QUESTION_CODE = 'I15'    ");
		sql.append(
				" WHERE RETURN_TYPE = 'GSTR6' AND HDR.SUPPLY_TYPE <> 'CAN' ");
		sql.append(" AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE   ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlParam);
		sql.append(" GROUP BY ELIGIBILITY_INDICATOR,HDR.TAX_DOC_TYPE,   ");
		sql.append(" HDR.CUST_GSTIN,HDR.RETURN_PERIOD   ");
		sql.append(" UNION ALL  ");
		sql.append("  SELECT 0 AS ID_COUNT, ");
		sql.append(" NULL AS INVOICE_VALUE, ");
		sql.append(" NULL AS TAXABLE_VALUE, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.IN_ELIGIBLE_TAX_PAYABLE,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') ");
		sql.append(
				" THEN IFNULL(ITM.IN_ELIGIBLE_TAX_PAYABLE,0) END),0) AS TOTAL_TAX, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.IN_ELIGIBLE_IGST_AMT,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') ");
		sql.append(
				" THEN IFNULL(ITM.IN_ELIGIBLE_IGST_AMT,0) END),0) AS IGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR')  ");
		sql.append(" THEN IFNULL(ITM.IN_ELIGIBLE_CGST_AMT,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR')  ");
		sql.append(
				" THEN IFNULL(ITM.IN_ELIGIBLE_CGST_AMT,0)END),0)  AS CGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.IN_ELIGIBLE_SGST_AMT,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR')  ");
		sql.append(
				" THEN IFNULL(ITM.IN_ELIGIBLE_SGST_AMT,0)END),0)  AS SGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(ITM.IN_ELIGIBLE_CESS_AMT,0) END),0) -  ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR')  ");
		sql.append(
				" THEN IFNULL(ITM.IN_ELIGIBLE_CESS_AMT,0) END),0) AS CESS_AMOUNT, ");
		sql.append(" 'NOT_ELG' AS ELIG_IND,  ");
		sql.append(" HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD  ");
		sql.append(" FROM ANX_INWARD_DOC_HEADER HDR ");
		sql.append(" INNER JOIN  ");
		sql.append(
				" ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND ");
		sql.append(" HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		sql.append(" INNER JOIN  ");
		sql.append(" GSTIN_INFO GIF  ");
		sql.append(" ON HDR.CUST_GSTIN = GIF.GSTIN INNER JOIN ");
		sql.append(" ENTITY_CONFG_PRMTR ECP ON  ");
		sql.append(" GIF.ENTITY_ID = ECP.ENTITY_ID  ");
		sql.append(" AND ECP.IS_DELETE = FALSE  ");
		sql.append(" AND QUESTION_CODE = 'I15'   ");
		sql.append(
				" WHERE RETURN_TYPE = 'GSTR6' AND HDR.SUPPLY_TYPE <> 'CAN' ");
		sql.append(" AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE  ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlParam);
		sql.append(" GROUP BY ELIGIBILITY_INDICATOR,HDR.TAX_DOC_TYPE,");
		sql.append(" HDR.CUST_GSTIN,HDR.RETURN_PERIOD ");
		sql.append(" UNION  ALL ");
		sql.append(" SELECT COUNT( HDR.ID) AS ID_COUNT, ");
		sql.append(" IFNULL(SUM(CASE WHEN HDR.DOC_TYPE  IN ");
		sql.append(
				" ('INV','DR','RNV','RDR')THEN IFNULL(HDR.DOC_AMT,0) END),0) - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(HDR.DOC_AMT,0) END ),0) ");
		sql.append(" AS INVOICE_VALUE, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN HDR.TAXABLE_VALUE END),0) - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN HDR.TAXABLE_VALUE END),0) AS TAXABLE_VALUE, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','RDR') THEN IFNULL(HDR.TAX_PAYABLE,0) END),0) - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(HDR.TAX_PAYABLE,0) END),0) AS TOTAL_TAX, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN IFNULL(HDR.IGST_AMT,0) END),0) - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN IFNULL(HDR.IGST_AMT,0) END),0) AS IGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN IFNULL(HDR.CGST_AMT,0) END),0) - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN IFNULL(HDR.CGST_AMT,0)END),0)  AS CGST_AMOUNT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN IFNULL(HDR.SGST_AMT,0) END),0) - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN IFNULL(HDR.SGST_AMT,0)END),0)  AS SGST_AMOUNT, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN IFNULL(HDR.CESS_AMT_SPECIFIC,0) END),0)  - ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN IFNULL(HDR.CESS_AMT_SPECIFIC,0) END),0)) +  ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN IFNULL(HDR.CESS_AMT_ADVALOREM,0) END),0) -  ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE  IN ('CR','RCR') THEN IFNULL(HDR.CESS_AMT_ADVALOREM,0) END),0)) AS CESS_AMOUNT, ");
		sql.append(
				" 'TOTAL' ELIG_IND,HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD ");
		sql.append(" FROM ANX_INWARD_DOC_HEADER HDR  ");
		sql.append(
				" WHERE RETURN_TYPE = 'GSTR6'   AND HDR.SUPPLY_TYPE <> 'CAN' ");
		sql.append(" AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlParam);
		sql.append(
				" GROUP BY HDR.TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD  ");
		sql.append(" UNION ALL  ");
		sql.append(
				" select COUNT(ID) AS ID_COUNT ,0 as INVOICE_VALUE, 0 AS  TAXABLE_VALUE, ");
		sql.append(
				" SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(IGST_AMT_AS_SGST,0)+ ");
		sql.append(" IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(SGST_AMT_AS_SGST,0)+ ");
		sql.append(
				" IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(CGST_AMT_AS_IGST,0)+ ");
		sql.append(" IFNULL(CESS_AMT,0)) as TOTAL_TAX, ");
		sql.append(
				" SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+ ");
		sql.append(" IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMOUNT, ");
		sql.append(
				" SUM(IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(IGST_AMT_AS_CGST,0)) as CGST_AMOUNT, ");
		sql.append(
				" SUM(IFNULL(SGST_AMT_AS_SGST,0)+ IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT, ");
		sql.append(" SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT, ");
		sql.append(" CASE ");
		sql.append(" WHEN  ELIGIBLE_INDICATOR IN ('IS','E')THEN 'IS_ELG'  ");
		sql.append(" WHEN  ELIGIBLE_INDICATOR IN ('NO','IE')THEN 'NOT_ELG'  ");
		sql.append(" END AS ELIG_IND,  ");
		sql.append(" 'ITC_CROSS' AS TAX_DOC_TYPE,  ");
		sql.append(" ISD_GSTIN AS CUST_GSTIN,TAX_PERIOD AS RETURN_PERIOD  ");
		sql.append(" FROM GSTR6_ISD_DISTRIBUTION  ");
		sql.append(
				" WHERE IS_DELETE = FALSE AND SUPPLY_TYPE is NULL AND DOC_TYPE = 'INV'  ");
		sql.append(
				"AND  (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ");
		sql.append(sqlDistribuParam);
		sql.append(
				" GROUP BY DOC_TYPE,ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD  ");
		sql.append(" UNION  ALL ");
		sql.append(" SELECT COUNT( HDR.ID) AS ID_COUNT, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.DOC_AMT,0) END),0) ");
		sql.append(
				"  - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.DOC_AMT,0) END),0)) ");
		sql.append("  + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') ");
		sql.append(
				"  THEN IFNULL(HDR.DOC_AMT,0) - IFNULL(HDR.PRECEEDING_INVOICE_VALUE,0) END),0) ");
		sql.append("  - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				"  THEN IFNULL(HDR.DOC_AMT,0) - IFNULL(HDR.PRECEEDING_INVOICE_VALUE,0)END),0))  AS INVOICE_VALUE, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.TAXABLE_VALUE,0) END),0) ");
		sql.append(
				"  - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.TAXABLE_VALUE,0) END),0)) ");
		sql.append("  + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR') ");
		sql.append(
				"  THEN IFNULL(HDR.TAXABLE_VALUE,0) - IFNULL(HDR.PRECEEDING_TAXABLE_VALUE,0) END),0) ");
		sql.append("  - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				"  THEN IFNULL(HDR.TAXABLE_VALUE,0) - IFNULL(HDR.PRECEEDING_TAXABLE_VALUE,0) END),0)) AS TAXABLE_VALUE, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.TAX_PAYABLE,0) END),0) ");
		sql.append(
				" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.TAX_PAYABLE,0) END),0)) ");
		sql.append(" + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR')  ");
		sql.append(
				" THEN IFNULL(HDR.TAX_PAYABLE,0) - IFNULL(HDR.PRECEEDING_TOTAL_TAX,0) END),0) ");
		sql.append(" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				" THEN IFNULL(HDR.TAX_PAYABLE,0) - IFNULL(HDR.PRECEEDING_TOTAL_TAX,0) END),0)) AS TOTAL_TAX, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.IGST_AMT,0) END),0) ");
		sql.append(
				" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.IGST_AMT,0) END),0))  ");
		sql.append(" + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR')  ");
		sql.append(
				" THEN IFNULL(HDR.IGST_AMT,0) - IFNULL(HDR.PRECEEDING_IGST_AMT,0) END),0) ");
		sql.append(" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				" THEN IFNULL(HDR.IGST_AMT,0) - IFNULL(HDR.PRECEEDING_IGST_AMT,0) END),0)) AS IGST_AMOUNT, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.CGST_AMT,0) END),0) ");
		sql.append(
				"  - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.CGST_AMT,0) END),0))  ");
		sql.append(" + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR')  ");
		sql.append(
				" THEN IFNULL(HDR.CGST_AMT,0) - IFNULL(HDR.PRECEEDING_CGST_AMT,0) END),0) ");
		sql.append(" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				" THEN IFNULL(HDR.CGST_AMT,0) - IFNULL(HDR.PRECEEDING_CGST_AMT,0) END),0)) AS CGST_AMOUNT, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IFNULL(HDR.SGST_AMT,0) END),0) ");
		sql.append(
				" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(HDR.SGST_AMT,0) END),0))  ");
		sql.append(" + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR')  ");
		sql.append(
				" THEN IFNULL(HDR.SGST_AMT,0) - IFNULL(HDR.PRECEEDING_SGST_AMT,0) END),0) ");
		sql.append(" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				" THEN IFNULL(HDR.SGST_AMT,0) - IFNULL(HDR.PRECEEDING_SGST_AMT,0) END),0)) AS SGST_AMOUNT, ");
		sql.append(
				" (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN (IFNULL(HDR.CESS_AMT_SPECIFIC,0) +IFNULL(CESS_AMT_ADVALOREM,0))  END),0) ");
		sql.append(
				"  - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN (IFNULL(HDR.CESS_AMT_SPECIFIC,0) +IFNULL(CESS_AMT_ADVALOREM,0)) END),0))  ");
		sql.append(" + (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RNV','RDR')  ");
		sql.append(
				" THEN IFNULL((HDR.CESS_AMT_SPECIFIC +CESS_AMT_ADVALOREM),0) - IFNULL(HDR.PRECEEDING_CESS_AMT,0) END),0) ");
		sql.append(" - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('RCR')  ");
		sql.append(
				" THEN IFNULL((HDR.CESS_AMT_SPECIFIC +CESS_AMT_ADVALOREM),0) - IFNULL(HDR.PRECEEDING_CESS_AMT,0) END),0)) AS CESS_AMOUNT, ");
		sql.append(" 'TOTAL' ELIG_IND, ");
		sql.append(
				" 'ITC_CROSS'as TAX_DOC_TYPE,HDR.CUST_GSTIN,HDR.RETURN_PERIOD ");
		sql.append(" FROM ANX_INWARD_DOC_HEADER HDR  ");
		sql.append(
				" WHERE RETURN_TYPE = 'GSTR6' AND HDR.SUPPLY_TYPE <> 'CAN'  ");
		sql.append(" AND HDR.IS_PROCESSED=TRUE AND HDR.IS_DELETE=FALSE  ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlParam);
		sql.append("GROUP BY HDR.CUST_GSTIN,HDR.RETURN_PERIOD   ");
		sql.append(" UNION ALL ");
		sql.append(
				" select COUNT(ID) AS ID_COUNT ,0 as INVOICE_VALUE, 0 AS  TAXABLE_VALUE,  ");
		sql.append(
				"SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(IGST_AMT_AS_SGST,0)+ ");
		sql.append(
				" IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(SGST_AMT_AS_SGST,0)+  ");
		sql.append(
				"IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(CGST_AMT_AS_IGST,0)+  ");
		sql.append(" IFNULL(CESS_AMT,0)) as TOTAL_TAX,  ");
		sql.append(
				"SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+  ");
		sql.append(" IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMOUNT,  ");
		sql.append(
				"SUM(IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(IGST_AMT_AS_CGST,0)) as CGST_AMOUNT,  ");
		sql.append(
				"SUM(IFNULL(SGST_AMT_AS_SGST,0)+ IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT,  ");
		sql.append(" SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT,  ");
		sql.append(" 'TOTAL' ELIG_IND, 'ITC_CROSS'as TAX_DOC_TYPE, ");
		sql.append(" ISD_GSTIN AS CUST_GSTIN,TAX_PERIOD AS RETURN_PERIOD ");
		sql.append(" FROM GSTR6_ISD_DISTRIBUTION  ");
		sql.append(
				" WHERE IS_DELETE = FALSE AND SUPPLY_TYPE is NULL  AND DOC_TYPE = 'CR'   ");
		sql.append(
				"AND  (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ");
		sql.append(sqlDistribuParam);
		sql.append("GROUP BY ISD_GSTIN,TAX_PERIOD  ");
		sql.append("UNION  ALL  ");
		sql.append(
				"select COUNT(ID) AS ID_COUNT ,0 as INVOICE_VALUE, 0 AS  TAXABLE_VALUE,  ");
		sql.append(
				"SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(IGST_AMT_AS_SGST,0)+  ");
		sql.append(
				" IFNULL(IGST_AMT_AS_CGST,0)+ IFNULL(SGST_AMT_AS_SGST,0)+  ");
		sql.append(
				"IFNULL(SGST_AMT_AS_IGST,0)+ IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(CGST_AMT_AS_IGST,0)+  ");
		sql.append(" IFNULL(CESS_AMT,0)) as TOTAL_TAX,  ");
		sql.append(
				"SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+  ");
		sql.append(" IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMOUNT,  ");
		sql.append(
				"SUM(IFNULL(CGST_AMT_AS_CGST,0)+ IFNULL(IGST_AMT_AS_CGST,0)) as CGST_AMOUNT,  ");
		sql.append(
				"SUM(IFNULL(SGST_AMT_AS_SGST,0)+ IFNULL(IGST_AMT_AS_SGST,0)) as SGST_AMOUNT,  ");
		sql.append(" SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT,  ");
		sql.append(" CASE  ");
		sql.append(" WHEN  ELIGIBLE_INDICATOR IN ('IS','E')THEN 'IS_ELG'  ");
		sql.append(" WHEN  ELIGIBLE_INDICATOR IN ('NO','IE')THEN 'NOT_ELG'  ");
		sql.append(" END AS ELIG_IND,  ");
		sql.append(" case when DOC_TYPE = 'INV' THEN 'D_INV'  ");
		sql.append(" WHEN DOC_TYPE IN ('CR','DR') THEN 'D_CR'  ");
		sql.append(" WHEN DOC_TYPE = 'RNV' THEN 'RD_INC'  ");
		sql.append(" WHEN DOC_TYPE IN ('RCR','RDR') THEN 'RD_CR'  ");
		sql.append(" END AS TAX_DOC_TYPE,  ");
		sql.append(" ISD_GSTIN AS CUST_GSTIN,TAX_PERIOD AS RETURN_PERIOD  ");
		sql.append(" FROM GSTR6_ISD_DISTRIBUTION  ");
		sql.append(" WHERE IS_DELETE = FALSE AND SUPPLY_TYPE is NULL ");
		sql.append(
				"AND  (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ");
		sql.append(sqlDistribuParam);
		sql.append(
				"GROUP BY DOC_TYPE,ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD  ");
		sql.append(
				" ) GROUP BY ELIG_IND,TAX_DOC_TYPE,CUST_GSTIN ,RETURN_PERIOD  ");
		sql.append(" ) T1  ");
		sql.append(" FULL OUTER JOIN  ");
		sql.append(" (SELECT  SUM(RECORD_COUNT_GSTN) as RECORD_COUNT_GSTIN,  ");
		sql.append(" SUM(INVOICE_VALUE_GSTN) AS INVOICE_VALUE_GSTIN,  ");
		sql.append(" SUM(TAXABLE_VALUE_GSTN) AS TAXABLE_VALUE_GSTIN,  ");
		sql.append(" sum(IFNULL(TOTAL_TAX_GSTN,0)) AS TOTAL_TAX_GSTIN,  ");
		sql.append(" SUM(IFNULL(IGST_AMOUNT_GSTN,0)) AS IGST_AMOUNT_GSTIN,  ");
		sql.append(" SUM(IFNULL(CGST_AMOUNT_GSTN,0))  AS CGST_AMOUNT_GSTIN,  ");
		sql.append(" SUM(IFNULL(SGST_AMOUNT_GSTN,0)) AS SGST_AMOUNT_GSTIN,  ");
		sql.append(" SUM(IFNULL(CESS_AMOUNT_GSTN,0)) AS CESS_AMOUNT_GSTIN,  ");
		sql.append(
				"ELIG_IND_GSTIN,TAX_DOC_TYPE_GSTIN,GSTIN,TAX_PERIOD_GSTIN FROM  ");
		sql.append(" (SELECT  SUM(RECORD_COUNT) as RECORD_COUNT_GSTN,  ");
		sql.append(" SUM(ifnull(TOTAL_VALUE,0)) AS INVOICE_VALUE_GSTN,  ");
		sql.append(
				"SUM(IFNULL(TOTAL_TAXABLE_VALUE,0)) AS TAXABLE_VALUE_GSTN,  ");
		sql.append(" sum(IFNULL(TOTAL_IGST,0) + IFNULL(TOTAL_CGST,0) +  ");
		sql.append(
				"IFNULL(TOTAL_SGST,0) + IFNULL(TOTAL_CESS,0)) AS TOTAL_TAX_GSTN,  ");
		sql.append(" SUM(IFNULL(TOTAL_IGST,0)) AS IGST_AMOUNT_GSTN,  ");
		sql.append(" SUM(IFNULL(TOTAL_CGST,0))  AS CGST_AMOUNT_GSTN,  ");
		sql.append(" SUM(IFNULL(TOTAL_SGST,0)) AS SGST_AMOUNT_GSTN,  ");
		sql.append(" SUM(IFNULL(TOTAL_CESS,0)) AS CESS_AMOUNT_GSTN,  ");
		sql.append(
				" 'TOTAL' ELIG_IND_GSTIN,CASE WHEN TABLE_SECTION = 'B2BT' THEN 'B2B'  ");
		sql.append(" WHEN TABLE_SECTION = 'B2BAT' THEN 'B2BA'  ");
		sql.append(" WHEN TABLE_SECTION = 'CDNT' THEN 'CDN'  ");
		sql.append(
				" WHEN TABLE_SECTION = 'CDNAT' THEN 'CDNA' END AS TAX_DOC_TYPE_GSTIN,  ");
		sql.append(
				" GSTIN,TAX_PERIOD AS TAX_PERIOD_GSTIN FROM GETGSTR6_B2B_CDN_SUMMARY  ");
		sql.append(" WHERE TABLE_SECTION IN ('B2BT','B2BAT','CDNT','CDNAT')  ");
		sql.append(" AND IS_DELETE=FALSE  ");
		sql.append(
				"AND  (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ");
		sql.append(sqlAPIParam);
		sql.append(" GROUP BY TABLE_SECTION,GSTIN,TAX_PERIOD  ");
		sql.append(" union all  ");
		sql.append(" select COUNT(ID)  as RECORD_COUNT_GSTN,  ");
		sql.append(" NULL as INVOICE_VALUE_GSTN,NULL as TAXABLE_VALUE_GSTN,  ");
		sql.append(" sum(IFNULL(IGST_AMT,0) + IFNULL(CGST_AMT,0) +  ");
		sql.append(
				" IFNULL(SGST_AMT,0) + IFNULL(CESS_AMT,0)) AS TOTAL_TAX_GSTN,  ");
		sql.append(" SUM(IFNULL(IGST_AMT,0)) AS IGST_AMOUNT_GSTN,  ");
		sql.append(" SUM(IFNULL(CGST_AMT,0))  AS CGST_AMOUNT_GSTN,  ");
		sql.append(" SUM(IFNULL(SGST_AMT,0)) AS SGST_AMOUNT_GSTN,  ");
		sql.append(" SUM(IFNULL(CESS_AMT,0)) AS CESS_AMOUNT_GSTN,  ");
		sql.append(" case WHEN TOTAL_ITC = TRUE THEN 'TOTAL'  ");
		sql.append(" WHEN ELG_ITC = TRUE THEN 'IS_ELG'  ");
		sql.append(
				" WHEN INELG_ITC = TRUE   THEN 'NOT_ELG' END AS ELIG_IND_GSTIN, ");
		sql.append(" 'ITC_CROSS'  AS TAX_DOC_TYPE_GSTIN, ");
		sql.append(" GSTIN,  ");
		sql.append(" TAX_PERIOD AS TAX_PERIOD_GSTIN  ");
		sql.append(" from GETGSTR6_ITC_DETAILS  ");
		sql.append(" where IS_DELETE = FALSE AND ISD_ITC_CROSS = FALSE  ");
		sql.append(
				"AND  (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ");
		sql.append(sqlAPIParam);
		sql.append("GROUP BY TOTAL_ITC,ELG_ITC, ");
		sql.append(" INELG_ITC,GSTIN,TAX_PERIOD  ");
		sql.append(" UNION all  ");
		sql.append(" SELECT  SUM(RECORD_COUNT) as RECORD_COUNT_GSTN, ");
		sql.append(" 0 as INVOICE_VALUE_GSTN, ");
		sql.append(" 0 as TAXABLE_VALUE_GSTN, ");
		sql.append(" sum(IFNULL(TOTAL_IGST,0) + IFNULL(TOTAL_CGST,0) +  ");
		sql.append(
				"IFNULL(TOTAL_SGST,0) + IFNULL(TOTAL_CESS,0)) AS TOTAL_TAX_GSTN, ");
		sql.append(" SUM(IFNULL(TOTAL_IGST,0)) AS IGST_AMOUNT_GSTN, ");
		sql.append(" SUM(IFNULL(TOTAL_CGST,0))  AS CGST_AMOUNT_GSTN, ");
		sql.append(" SUM(IFNULL(TOTAL_SGST,0)) AS SGST_AMOUNT_GSTN, ");
		sql.append(" SUM(IFNULL(TOTAL_CESS,0)) AS CESS_AMOUNT_GSTN, ");
		sql.append(" case WHEN TABLE_SECTION IN  ");
		sql.append(" ('EISDT','EISDURT','EISDCNT','EISDCNURT','EISDAT', ");
		sql.append(" 'EISDURAT','EISDCNAT','EISDCNURAT') THEN 'IS_ELG'  ");
		sql.append(" WHEN TABLE_SECTION IN  ");
		sql.append(" ('IEISDT','IEISDURT','IEISDCNT','IEISDCNURT','IEISDAT', ");
		sql.append(
				" 'IEISDURAT','IEISDCNAT','IEISDCNURAT') THEN 'NOT_ELG' END AS ELIG_IND_GSTIN, ");
		sql.append(
				" case when TABLE_SECTION in ('EISDT','EISDURT','IEISDT','IEISDURT') THEN 'D_INV' ");
		sql.append(
				" when TABLE_SECTION in  ('EISDCNT','EISDCNURT','IEISDCNT','IEISDCNURT') THEN 'D_CR'  ");
		sql.append(
				" when TABLE_SECTION in ('EISDAT','EISDURAT','IEISDAT','IEISDURAT') THEN 'RD_INC'  ");
		sql.append(
				" when TABLE_SECTION in  ('EISDCNAT','EISDCNURAT','IEISDCNAT','IEISDCNURAT') THEN 'RD_CR'  ");
		sql.append(
				"END AS TAX_DOC_TYPE_GSTIN,GSTIN,TAX_PERIOD AS TAX_PERIOD_GSTIN ");
		sql.append(" FROM GETGSTR6_ISD_SUMMARY ");
		sql.append(" WHERE  ");
		sql.append(" IS_DELETE=FALSE  ");
		sql.append(
				"AND  (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ");
		sql.append(sqlAPIParam);
		sql.append(" GROUP BY TABLE_SECTION,GSTIN,TAX_PERIOD  ");
		sql.append(" )  ");
		sql.append(
				" GROUP BY TAX_DOC_TYPE_GSTIN,GSTIN,TAX_PERIOD_GSTIN,ELIG_IND_GSTIN ");
		sql.append(" ) T2  ");
		sql.append(" ON T1.ELIG_IND_ASP = T2.ELIG_IND_GSTIN AND  ");
		sql.append(" T1.TAX_DOC_TYPE_ASP = T2.TAX_DOC_TYPE_GSTIN  ");
		sql.append(" AND T1.CUST_GSTIN_ASP = T2.GSTIN)  ");
		sql.append(" GROUP BY ELIG_IND_ASP ,TAX_DOC_TYPE_ASP,CUST_GSTIN_ASP, ");
		sql.append(" TAX_PERIOD_ASP,ELIG_IND_GSTIN,TAX_DOC_TYPE_GSTIN, ");
		sql.append(" GSTIN,TAX_PERIOD_GSTIN  ");
		sql.append("ORDER BY TAX_DOC_TYPE_ASP ");
		return sql.toString();
	}
	
	
	public List<Gstr6DigiComputeEntity> getGstinSummaryDetails(
			final Annexure1SummaryReqDto reqDto) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setInwardSumDataSecuritySearchParams(reqDto);

		String taxPeriodReq = req.getTaxPeriod();
		List<Object[]> objSumDetails = new ArrayList<>();
		Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		gstinList = gstnDetailRepository.getGstinRegTypeISD(gstinList);
		
		return gstr6DigiComputeRepository.findByGstinAndTaxPeriod(gstinList.get(0),taxPeriodReq);
	}
	
}
