package com.ey.advisory.app.services.credit.reversal;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("CreditRevForFinancialYearWiseDaoImpl")
public class CreditRevForFinancialYearWiseDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditRevForFinancialYearWiseDaoImpl.class);

	public List<Object[]> getProcSumRevFinaYear(String gstin, int fromRetPeriod,
			int toRetPeriod) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String proceFinYearQuery = getProcesFinYearQuery();
			Query q = entityManager.createNativeQuery(proceFinYearQuery);
			q.setParameter("fromRetPeriod", fromRetPeriod);
			q.setParameter("toRetPeriod", toRetPeriod);
			q.setParameter("gstin", gstin);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		return objs;
	}

	private String getProcesFinYearQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TAXPERIOD,GSTIN,SUM(RATIO_01),SUM(R1_IGST_AMT), ");
		sql.append("SUM(R1_CGST_AMT),SUM(R1_SGST_AMT),SUM(R1_CESS_AMT), ");
		sql.append("SUM(RATIO_02),SUM(R2_IGST_AMT),SUM(R2_CGST_AMT), ");
		sql.append("SUM(R2_SGST_AMT),SUM(R2_CESS_AMT), ");
		sql.append("SUM(RATIO_03),SUM(R3_IGST_AMT),SUM(R3_CGST_AMT), ");
		sql.append("SUM(R3_SGST_AMT),SUM(R3_CESS_AMT), DERIVED_RET_PERIOD  ");
		sql.append(
				"FROM( SELECT SUBSTRING(TAX_PERIOD,0,2) AS TAXPERIOD,GSTIN, ");
		sql.append("CASE WHEN substring(RATIO_1, 6, 1) > 5 THEN  ");
		sql.append(
				"(LEFT(RATIO_1,5)+0.01) ELSE LEFT(RATIO_1,5) END AS RATIO_01, ");
		sql.append("IGST_AMT AS R1_IGST_AMT,CGST_AMT AS R1_CGST_AMT, ");
		sql.append("SGST_AMT AS R1_SGST_AMT,CESS_AMT AS R1_CESS_AMT, ");
		sql.append(
				"NULL AS RATIO_02,NULL AS R2_IGST_AMT,NULL AS R2_CGST_AMT, ");
		sql.append("NULL AS R2_SGST_AMT,NULL AS R2_CESS_AMT, ");
		sql.append("NULL AS RATIO_03,NULL AS R3_IGST_AMT, ");
		sql.append("NULL AS R3_CGST_AMT,NULL AS R3_SGST_AMT, ");
		sql.append("NULL AS R3_CESS_AMT, DERIVED_RET_PERIOD  ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE  ");
		sql.append("AND SUB_SECTION_NAME IN  ");
		sql.append("('ITC Reversal Ratio','Total Reversal_RATIO_1')  ");
		sql.append(
				"AND DERIVED_RET_PERIOD BETWEEN :fromRetPeriod AND :toRetPeriod  ");
		sql.append("AND GSTIN IN (:gstin)   ");
		sql.append("UNION ALL  ");
		sql.append("SELECT SUBSTRING(TAX_PERIOD,0,2) AS TAXPERIOD,GSTIN, ");
		sql.append("NULL AS RATIO_01,NULL AS R1_IGST_AMT, ");
		sql.append(
				"NULL AS R1_CGST_AMT,NULL AS R1_SGST_AMT,NULL AS R1_CESS_AMT, ");
		sql.append(
				"CASE WHEN substring(RATIO_2, 6, 1) > 5 THEN (LEFT(RATIO_2)+0.01) ");
		sql.append(
				"ELSE LEFT(RATIO_2,5) END AS RATIO_02,IGST_AMT AS R2_IGST_AMT, ");
		sql.append("CGST_AMT AS R2_CGST_AMT,SGST_AMT AS R2_SGST_AMT, ");
		sql.append("CESS_AMT AS R2_CESS_AMT,NULL AS RATIO_03, ");
		sql.append("NULL AS R3_IGST_AMT,NULL AS R3_CGST_AMT, ");
		sql.append(
				"NULL AS R3_SGST_AMT,NULL AS R3_CESS_AMT,DERIVED_RET_PERIOD  ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE  ");
		sql.append(
				"AND SUB_SECTION_NAME IN ('ITC Reversal Ratio','Total Reversal_RATIO_2')  ");
		sql.append(
				"AND DERIVED_RET_PERIOD BETWEEN :fromRetPeriod AND :toRetPeriod  ");
		sql.append("AND GSTIN IN (:gstin)   ");
		sql.append("UNION ALL  ");
		sql.append("SELECT SUBSTRING(TAX_PERIOD,0,2) AS TAXPERIOD,GSTIN, ");
		sql.append("NULL AS RATIO_01,NULL AS R1_IGST_AMT, ");
		sql.append(
				"NULL AS R1_CGST_AMT,NULL AS R1_SGST_AMT,NULL AS R1_CESS_AMT, ");
		sql.append(
				"NULL AS RATIO_02,NULL AS R2_IGST_AMT,NULL AS R2_CGST_AMT, ");
		sql.append("NULL AS R2_SGST_AMT,NULL AS R2_CESS_AMT, ");
		sql.append(
				"CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ");
		sql.append("ELSE LEFT(RATIO_3,5) END AS RATIO_03, ");
		sql.append("IGST_AMT AS R3_IGST_AMT, ");
		sql.append("CGST_AMT AS R3_CGST_AMT,SGST_AMT AS R3_SGST_AMT, ");
		sql.append("CESS_AMT AS R3_CESS_AMT,DERIVED_RET_PERIOD  ");
		sql.append("FROM  GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE  ");
		sql.append(
				"AND SUB_SECTION_NAME IN ('ITC Reversal Ratio','Total Reversal_RATIO_3') ");
		sql.append(
				"AND DERIVED_RET_PERIOD BETWEEN :fromRetPeriod AND :toRetPeriod  ");
		sql.append("AND GSTIN IN (:gstin)   ");
		sql.append(")  ");
		sql.append("GROUP BY GSTIN,TAXPERIOD,DERIVED_RET_PERIOD ");

		return sql.toString();

	}

	public int proceCallForFinancialYear(String gstin, int fromRetPeriod,
			int toRetPeriod) {
		int count = 0;
		try {
			StoredProcedureQuery stoProcQuery = entityManager
					.createStoredProcedureQuery("GSTR3B_42_COMPUTE_FIN");
			stoProcQuery.registerStoredProcedureParameter("GSTIN", String.class,
					ParameterMode.IN);
			stoProcQuery.registerStoredProcedureParameter(
					"FROM_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
			stoProcQuery.registerStoredProcedureParameter(
					"TO_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
			stoProcQuery.setParameter("GSTIN", gstin);
			stoProcQuery.setParameter("FROM_DERIVED_RET_PERIOD", fromRetPeriod);
			stoProcQuery.setParameter("TO_DERIVED_RET_PERIOD", toRetPeriod);
			stoProcQuery.execute();
			count = count + 1;
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		return count;
	}

	public List<Object[]> getReviewReversalFinancialYear(String gstin,
			int fromRetPeriod, int toRetPeriod) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String sql = getRevFinancialQuery();
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("fromRetPeriod", fromRetPeriod);
			q.setParameter("toRetPeriod", toRetPeriod);
			q.setParameter("gstin", gstin);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return objs;
	}

	private String getRevFinancialQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SUBSTRING(TAX_PERIOD,0,2) AS TAXPERIOD,SUB_SECTION_NAME,");
		sql.append("TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,");
		sql.append("CASE WHEN substring(RATIO_1, 6, 1) > 5 THEN ");
		sql.append(
				"(LEFT(RATIO_1,5)+0.01) ELSE LEFT(RATIO_1,5) END AS RATIO_1,");
		sql.append(
				"CASE WHEN substring(RATIO_2, 6, 1) > 5 THEN (LEFT(RATIO_2)+0.01) ");
		sql.append("ELSE LEFT(RATIO_2,5) END AS RATIO_2,");
		sql.append(
				"CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ");
		sql.append("ELSE LEFT(RATIO_3,5) END AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE ");
		sql.append("AND SUB_SECTION_NAME IN ");
		sql.append(
				"('Total Tax Amount','A','B','C','D_T1','D_T2','D_T3','D_T4','E',");
		sql.append(
				"'Ratio_1_R42','Ratio_2_R42','Ratio_3_R42','D2','Total Reversal',");
		sql.append("'ITC Reversal Ratio','Total Reversal_RATIO_1',");
		sql.append("'Total Reversal_RATIO_2','Total Reversal_RATIO_3') ");
		sql.append("AND GSTIN=:gstin ");
		sql.append(
				"AND DERIVED_RET_PERIOD BETWEEN :fromRetPeriod AND :toRetPeriod ");
		return sql.toString();

	}

	public List<Object[]> getTurnOverFinaYear(String gstin, int fromRetPeriod,
			int toRetPeriod) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String sql = getTurnOverFinaYearQuery();
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("fromRetPeriod", fromRetPeriod);
			q.setParameter("toRetPeriod", toRetPeriod);
			q.setParameter("gstin", gstin);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return objs;
	}

	private String getTurnOverFinaYearQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SUBSTRING(TAX_PERIOD,0,2) AS TAXPERIOD,SUB_SECTION_NAME,");
		sql.append(
				"TAXABLE_VALUE,CASE WHEN substring(RATIO_1, 6, 1) > 5 THEN ");
		sql.append(
				"(LEFT(RATIO_1,5)+0.01) ELSE LEFT(RATIO_1,5) END AS RATIO_1,");
		sql.append(
				"CASE WHEN substring(RATIO_2, 6, 1) > 5 THEN (LEFT(RATIO_2)+0.01) ");
		sql.append("ELSE LEFT(RATIO_2,5) END AS RATIO_2,");
		sql.append(
				"CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01)");
		sql.append("ELSE LEFT(RATIO_3,5) END AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE ");
		sql.append("AND SUB_SECTION_NAME IN ");
		sql.append("('TAX','B2CS <>0','SEZ','DXP','EXPT','EXPWT',");
		sql.append("'DTA','EXT','NIL','B2CS = 0','Non GST /SCH3',");
		sql.append("'TOTAL_GSTIN_SUPPLIES','TTL_RCS',");
		sql.append(
				"'TAX_REVERSE_CHARGE','DTA_REVERSE_CHARGE','DXP_REVERSE_CHARGE',");
		sql.append(
				"'SEZ_REVERSE_CHARGE','ITC Reversal Ratio','T_GSTIN_SUPPLIES') ");
		sql.append("AND GSTIN=:gstin AND ");
		sql.append(
				"DERIVED_RET_PERIOD BETWEEN :fromRetPeriod AND :toRetPeriod ");
		return sql.toString();
	}
}
