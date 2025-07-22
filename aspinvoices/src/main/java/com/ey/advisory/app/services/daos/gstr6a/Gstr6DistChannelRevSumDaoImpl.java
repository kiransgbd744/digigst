package com.ey.advisory.app.services.daos.gstr6a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeDistributionEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6DigiComputeDistributionRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

@Component("Gstr6DistChannelRevSumDaoImpl")
public class Gstr6DistChannelRevSumDaoImpl
		implements Gstr6DistChannelRevSumDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("Gstr6DigiComputeDistributionRepository")
	private Gstr6DigiComputeDistributionRepository gstr6DigiComputeDistributionRepository;

	private static final String DERIVED_RET_PERIOD_SQL = "AND (:taxPeriod IS NULL OR DERIVED_RET_PERIOD=:taxPeriod) ";

	@Autowired
	@Qualifier("BasicCommonSecParam")
	private BasicCommonSecParam basicCommonSecParam;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6DistChannelRevSumDaoImpl.class);

	@Override
	public List<Object[]> getSummaryDetails(Annexure1SummaryReqDto reqDto) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setInwardSumDataSecuritySearchParams(reqDto);

		String taxPeriodReq = req.getTaxPeriod();
		List<Object[]> objSumDetails = new ArrayList<>();
		Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		StringBuilder sqlParam = new StringBuilder();
		StringBuilder sqlAPIParam = new StringBuilder();
		if (gstinList != null && !gstinList.isEmpty()) {
			sqlParam.append("AND ISD_GSTIN IN :gstinList ");
			sqlAPIParam.append("AND GSTIN IN :gstinList ");
		}
		String sql = getQuery(sqlParam, sqlAPIParam);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 Part B: {}", sql);
		}
		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("taxPeriod", taxPeriod);
		if (gstinList != null && !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}
		objSumDetails = q.getResultList();
		return objSumDetails;
	}

	private String getQuery(StringBuilder sqlParam, StringBuilder sqlAPIParam) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ID_ASP,IGST_AS_IGST_APS,IGST_AS_SGST_APS,");
		sql.append("IGST_AS_CGST_APS,SGST_AS_SGST_APS,SGST_AS_IGST_APS,");
		sql.append("CGST_AS_CGST_APS,CGST_AS_IGST_APS,CESS_APS,");
		sql.append("T1.ELIG_INDICATOR AS ELIG_INDICATOR_GSTN,");
		sql.append("T1.DOC_TYPE AS DOC_TYPE_ASP, ISD_GSTIN AS ISD_GSTIN_ASP,");
		sql.append("T1.TAX_PERIOD AS TAX_PERIOD_ASP,ID_GSTN,");
		sql.append("IGST_AS_IGST_GSTN,IGST_AS_SGST_GSTN,");
		sql.append("IGST_AS_CGST_GSTN,SGST_AS_SGST_GSTN,");
		sql.append("SGST_AS_IGST_GSTN,CGST_AS_CGST_GSTN,");
		sql.append("CGST_AS_IGST_GSTN, CESS_GSTN ");

		sql.append("FROM( ");
		sql.append("SELECT SUM(ID) AS ID_ASP,");
		sql.append("SUM(IFNULL(IGST_AS_IGST,0)) AS IGST_AS_IGST_APS, ");
		sql.append("SUM(IFNULL(IGST_AS_SGST,0)) as IGST_AS_SGST_APS, ");
		sql.append("SUM(IFNULL(IGST_AS_CGST,0)) as IGST_AS_CGST_APS, ");
		sql.append("SUM(IFNULL(SGST_AS_SGST,0)) as SGST_AS_SGST_APS, ");
		sql.append("SUM(IFNULL(SGST_AS_IGST,0)) as SGST_AS_IGST_APS, ");
		sql.append("SUM(IFNULL(CGST_AS_CGST,0)) as CGST_AS_CGST_APS, ");
		sql.append("SUM(IFNULL(CGST_AS_IGST,0)) as CGST_AS_IGST_APS, ");
		sql.append("SUM(IFNULL(CESS_AMOUNT,0)) as CESS_APS,ELIG_INDICATOR, ");
		sql.append("DOC_TYPE,ISD_GSTIN,TAX_PERIOD ");
		sql.append("FROM ");
		sql.append("(select COUNT(ID) AS ID,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_IGST,0)) AS IGST_AS_IGST, ");
		sql.append("SUM(IFNULL(IGST_AMT_AS_SGST,0)) as IGST_AS_SGST, ");
		sql.append("SUM(IFNULL(IGST_AMT_AS_CGST,0)) as IGST_AS_CGST, ");
		sql.append("SUM(IFNULL(SGST_AMT_AS_SGST,0)) as SGST_AS_SGST, ");
		sql.append("SUM(IFNULL(SGST_AMT_AS_IGST,0)) as SGST_AS_IGST, ");
		sql.append("SUM(IFNULL(CGST_AMT_AS_CGST,0)) as CGST_AS_CGST, ");
		sql.append("SUM(IFNULL(CGST_AMT_AS_IGST,0)) as CGST_AS_IGST, ");
		sql.append("SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT, ");
		sql.append("CASE  ");
		sql.append("WHEN  ELIGIBLE_INDICATOR IN ('IS','E')THEN 'ELIGIBLE'  ");
		sql.append(
				"WHEN  ELIGIBLE_INDICATOR IN ('NO','IE')THEN 'INELIGIBLE'  ");
		sql.append("END AS ELIG_INDICATOR, ");
		sql.append("DOC_TYPE, ");
		sql.append("ISD_GSTIN,TAX_PERIOD ");
		sql.append("FROM GSTR6_ISD_DISTRIBUTION ");
		sql.append("WHERE IS_DELETE = FALSE AND SUPPLY_TYPE IS NULL ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlParam);
		sql.append(
				"GROUP BY DOC_TYPE,ELIGIBLE_INDICATOR,ISD_GSTIN,TAX_PERIOD) ");
		sql.append("GROUP BY ELIG_INDICATOR,DOC_TYPE,ISD_GSTIN,TAX_PERIOD ");
		sql.append(") T1 ");
		sql.append("FULL OUTER JOIN ");
		sql.append("( ");
		sql.append("SELECT SUM(ID) AS ID_GSTN,");
		sql.append("SUM(IFNULL(IGST_AS_IGST,0)) AS IGST_AS_IGST_GSTN, ");
		sql.append("SUM(IFNULL(IGST_AS_SGST,0)) as IGST_AS_SGST_GSTN, ");
		sql.append("SUM(IFNULL(IGST_AS_CGST,0)) as IGST_AS_CGST_GSTN,");
		sql.append("SUM(IFNULL(SGST_AS_SGST,0)) as SGST_AS_SGST_GSTN, ");
		sql.append("SUM(IFNULL(SGST_AS_IGST,0)) as SGST_AS_IGST_GSTN,");
		sql.append("SUM(IFNULL(CGST_AS_CGST,0)) as CGST_AS_CGST_GSTN, ");
		sql.append("SUM(IFNULL(CGST_AS_IGST,0)) as CGST_AS_IGST_GSTN,");
		sql.append("SUM(IFNULL(CESS_AMOUNT,0)) as CESS_GSTN, ");
		sql.append("ELIG_INDICATOR, ");
		sql.append("DOC_TYPE,GSTIN,TAX_PERIOD ");
		sql.append("FROM ");
		sql.append("(select COUNT(ID) as ID,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_IGST,0)) AS IGST_AS_IGST,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_SGST,0)) as IGST_AS_SGST,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_CGST,0)) as IGST_AS_CGST, ");
		sql.append("SUM(IFNULL(SGST_AMT_AS_SGST,0)) as SGST_AS_SGST,");
		sql.append("SUM(IFNULL(SGST_AMT_AS_IGST,0)) as SGST_AS_IGST, ");
		sql.append("SUM(IFNULL(CGST_AMT_AS_CGST,0)) as CGST_AS_CGST,");
		sql.append("SUM(IFNULL(CGST_AMT_AS_IGST,0)) as CGST_AS_IGST,");
		sql.append("SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT, ");
		sql.append("CASE when ELIGIBLE_INDICATOR ='E' Then 'ELIGIBLE' ");
		sql.append("when ELIGIBLE_INDICATOR ='IE' Then 'INELIGIBLE' ");
		sql.append("END AS ELIG_INDICATOR, ");
		sql.append("CASE WHEN ISD_DOC_TYPE IN ('ISD','ISDUR') THEN 'INV' ");
		sql.append("WHEN ISD_DOC_TYPE IN ('ISDCN','ISDCNUR') THEN 'CR' ");
		sql.append("END AS DOC_TYPE,GSTIN,TAX_PERIOD ");
		sql.append("FROM GETGSTR6_ISD_DETAILS ");
		sql.append("WHERE IS_DELETE = FALSE ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlAPIParam);
		sql.append(
				"GROUP BY ELIGIBLE_INDICATOR,ISD_DOC_TYPE,GSTIN,TAX_PERIOD ");
		sql.append("union all ");
		sql.append("select COUNT(ID) as ID,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_IGST,0)) AS IGST_AS_IGST,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_SGST,0)) as IGST_AS_SGST,");
		sql.append("SUM(IFNULL(IGST_AMT_AS_CGST,0)) as IGST_AS_CGST, ");
		sql.append("SUM(IFNULL(SGST_AMT_AS_SGST,0)) as SGST_AS_SGST,");
		sql.append("SUM(IFNULL(SGST_AMT_AS_IGST,0)) as SGST_AS_IGST, ");
		sql.append("SUM(IFNULL(CGST_AMT_AS_CGST,0)) as CGST_AS_CGST,");
		sql.append("SUM(IFNULL(CGST_AMT_AS_IGST,0)) as CGST_AS_IGST,");
		sql.append("SUM(IFNULL(CESS_AMT,0)) as CESS_AMOUNT, ");
		sql.append("CASE when ELIGIBLE_INDICATOR ='E' Then 'ELIGIBLE' ");
		sql.append("when ELIGIBLE_INDICATOR ='IE' Then 'INELIGIBLE' ");
		sql.append("END AS ELIG_INDICATOR, ");
		sql.append("CASE  ");
		sql.append("WHEN ISD_DOC_TYPE IN  ('ISDA','ISDURA') THEN 'RNV' ");
		sql.append("WHEN ISD_DOC_TYPE IN ('ISDCNA','ISDCNURA') THEN 'RCR' ");
		sql.append("END AS DOC_TYPE,GSTIN,TAX_PERIOD ");
		sql.append("FROM GETGSTR6_ISDA_DETAILS ");
		sql.append("WHERE IS_DELETE = FALSE ");
		sql.append(DERIVED_RET_PERIOD_SQL);
		sql.append(sqlAPIParam);
		sql.append(
				"GROUP BY ELIGIBLE_INDICATOR,ISD_DOC_TYPE,GSTIN,TAX_PERIOD ");
		sql.append(") ");
		sql.append("GROUP BY ELIG_INDICATOR,DOC_TYPE,GSTIN,TAX_PERIOD ");
		sql.append(") T2 ");
		sql.append("ON T2.ELIG_INDICATOR = T1.ELIG_INDICATOR AND ");
		sql.append("T2.DOC_TYPE = T1.DOC_TYPE AND  ");
		sql.append("T2.GSTIN = T1.ISD_GSTIN ");
		return sql.toString();
	}
	
	
	public List<Gstr6DigiComputeDistributionEntity> getGstinSummaryDetails(
			final Annexure1SummaryReqDto reqDto) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setInwardSumDataSecuritySearchParams(reqDto);

		String taxPeriodReq = req.getTaxPeriod();
		List<Object[]> objSumDetails = new ArrayList<>();
		Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		 
		return gstr6DigiComputeDistributionRepository.findByGstinAndTaxPeriod(gstinList.get(0), taxPeriodReq);
				
	}
}
