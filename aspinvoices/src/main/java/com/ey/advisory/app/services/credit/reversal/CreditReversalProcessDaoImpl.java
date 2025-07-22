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

import com.ey.advisory.common.AppException;

@Repository("CreditReversalProcessDaoImpl")
public class CreditReversalProcessDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditReversalProcessDaoImpl.class);

	public int proceCallForComputeReversal(final String gstin,
			final Integer retPeriod) {
		int count = 0;
		try {
			StoredProcedureQuery storedProcQuery = entityManager
					.createStoredProcedureQuery("GSTR3B_42_COMPUTE");
			storedProcQuery.registerStoredProcedureParameter("GSTIN",
					String.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter(
					"DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
			storedProcQuery.setParameter("DERIVED_RET_PERIOD", retPeriod);
			storedProcQuery.setParameter("GSTIN", gstin);
			storedProcQuery.execute();
			count = 1 + count;

		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
			throw new AppException(e.getMessage());
		}
		return count;
	}

	public List<Object[]> getCreditReversalProcess(final List<String> gstinList,
			final Integer derivedRetPeriod) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String sql = sqlQuery(gstinList);
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("derivedRetPeriod", derivedRetPeriod);
			if (gstinList != null && !gstinList.isEmpty()) {
				q.setParameter("gstinList", gstinList);
			}
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		return objs;
	}

	private String sqlQuery(List<String> gstinList) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT GSTIN,SUM(RATIO_01),SUM(R1_IGST_AMT),");
		sql.append("SUM(R1_CGST_AMT),SUM(R1_SGST_AMT),SUM(R1_CESS_AMT),");
		sql.append("SUM(RATIO_02),SUM(R2_IGST_AMT),SUM(R2_CGST_AMT),");
		sql.append("SUM(R2_SGST_AMT),SUM(R2_CESS_AMT),");
		sql.append("SUM(RATIO_03),SUM(R3_IGST_AMT),SUM(R3_CGST_AMT),");
		sql.append("SUM(R3_SGST_AMT),SUM(R3_CESS_AMT), ");
		sql.append("SUM(R1_TAXABLE_VALUE) AS TOTAL_TAX_01,");
		sql.append("SUM(R2_TAXABLE_VALUE) AS TOTAL_TAX_02,");
		sql.append("SUM(R3_TAXABLE_VALUE) AS TOTAL_TAX_03 ");
		sql.append("FROM( ");
		sql.append("SELECT C.GSTIN,");
		sql.append("IFNULL(CASE WHEN R.RATIO1=0 THEN NULL ELSE R.RATIO1 END,CASE WHEN substring(RATIO_1, 6, 1) > 5 THEN LEFT(RATIO_1,5)+0.01 ELSE LEFT(RATIO_1,5) END) ");
		sql.append("AS RATIO_01,");
		sql.append("IGST_AMT AS R1_IGST_AMT,");
		sql.append("CGST_AMT AS R1_CGST_AMT,SGST_AMT AS R1_SGST_AMT,");
		sql.append("CESS_AMT AS R1_CESS_AMT,");
		sql.append("NULL AS RATIO_02,NULL AS R2_IGST_AMT,NULL AS R2_CGST_AMT,");
		sql.append("NULL AS R2_SGST_AMT,NULL AS R2_CESS_AMT,");
		sql.append("NULL AS RATIO_03,NULL AS R3_IGST_AMT,");
		sql.append("NULL AS R3_CGST_AMT,NULL AS R3_SGST_AMT,");
		sql.append("NULL AS R3_CESS_AMT,");
		sql.append(
				"TAXABLE_VALUE AS R1_TAXABLE_VALUE,NULL AS R2_TAXABLE_VALUE,");
		sql.append("NULL AS R3_TAXABLE_VALUE ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE C LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN\r\n" + 
				"    AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE ");
		sql.append("AND C.SECTION_NAME IN ('ITC Reversal Ratio') WHERE IS_ACTIVE = TRUE ");
		sql.append(
				"AND SUB_SECTION_NAME IN ('Ratio_1_R42','ITC Reversal Ratio') ");
		sql.append("AND DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (gstinList != null && !gstinList.isEmpty()) {
			sql.append("AND C.GSTIN IN (:gstinList) ");
		}
		sql.append("UNION ALL ");
		sql.append("SELECT C.GSTIN,NULL AS RATIO_01,NULL AS R1_IGST_AMT,");
		sql.append(
				"NULL AS R1_CGST_AMT,NULL AS R1_SGST_AMT,NULL AS R1_CESS_AMT,");
		sql.append(
				"IFNULL(CASE WHEN R.RATIO2=0 THEN NULL ELSE R.RATIO2 END,CASE WHEN substring(RATIO_2, 6, 1) > 5 THEN LEFT(RATIO_2)+0.01 ELSE LEFT(RATIO_2,5) END) ");
		sql.append("AS RATIO_02,");

		sql.append("IGST_AMT AS R2_IGST_AMT,");
		sql.append("CGST_AMT AS R2_CGST_AMT,SGST_AMT AS R2_SGST_AMT,");
		sql.append("CESS_AMT AS R2_CESS_AMT,NULL AS RATIO_03,");
		sql.append("NULL AS R3_IGST_AMT,NULL AS R3_CGST_AMT,");
		sql.append("NULL AS R3_SGST_AMT,NULL AS R3_CESS_AMT,");
		sql.append(
				"NULL AS R1_TAXABLE_VALUE,TAXABLE_VALUE AS R2_TAXABLE_VALUE,");
		sql.append("NULL AS R3_TAXABLE_VALUE ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE C LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN\r\n" + 
				" AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE AND C.SECTION_NAME IN ('ITC Reversal Ratio') WHERE IS_ACTIVE = TRUE ");
		sql.append(
				"AND SUB_SECTION_NAME IN ('Ratio_2_R42','ITC Reversal Ratio') ");
		sql.append("AND DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (gstinList != null && !gstinList.isEmpty()) {
			sql.append("AND C.GSTIN IN (:gstinList) ");
		}
		sql.append("UNION ALL ");
		sql.append("SELECT GSTIN,NULL AS RATIO_01,NULL AS R1_IGST_AMT,");
		sql.append(
				"NULL AS R1_CGST_AMT,NULL AS R1_SGST_AMT,NULL AS R1_CESS_AMT,");
		sql.append("NULL AS RATIO_02,NULL AS R2_IGST_AMT,NULL AS R2_CGST_AMT,");
		sql.append("NULL AS R2_SGST_AMT,NULL AS R2_CESS_AMT,");
		sql.append(
				"CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ");
		sql.append("ELSE LEFT(RATIO_3,5) END AS RATIO_03, ");
		sql.append("IGST_AMT AS R3_IGST_AMT,");
		sql.append("CGST_AMT AS R3_CGST_AMT,SGST_AMT AS R3_SGST_AMT,");
		sql.append("CESS_AMT AS R3_CESS_AMT,");
		sql.append("NULL AS R1_TAXABLE_VALUE,NULL AS R2_TAXABLE_VALUE,");
		sql.append("TAXABLE_VALUE AS R3_TAXABLE_VALUE ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE ");
		sql.append(
				"AND SUB_SECTION_NAME IN ('Ratio_3_R42','ITC Reversal Ratio') \r\n" + 
				"AND SECTION_NAME IN ('Ratio 3: Reversal Amount as per Rule 42 =D1 (C2 * Ratio 3)','ITC Reversal Ratio')");
		sql.append("AND DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (gstinList != null && !gstinList.isEmpty()) {
			sql.append("AND GSTIN IN (:gstinList) ");
		}
		sql.append(") GROUP BY GSTIN ");
		return sql.toString();
	}

	public List<Object[]> getCreditReversalAndTurnvolProcess(
			final String processType, final String gstin,
			final Integer derivedRetPeriod) {
		List<Object[]> objs = new ArrayList<>();
		String sql = null;
		try {
			if ("reversel".equalsIgnoreCase(processType)) {
				sql = reversalSql();
			} else {
				sql = turnOverSql();
			}
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("derivedRetPeriod", derivedRetPeriod);
			q.setParameter("gstin", gstin);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return objs;
	}

	private String reversalSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SUB_SECTION_NAME, TAXABLE_VALUE, ");
		sql.append("IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT, ");
		sql.append("IFNULL(CASE WHEN R.RATIO1 = 0 THEN NULL ELSE R.RATIO1 END, ");
		sql.append("CASE WHEN SUBSTRING(RATIO_1, 6, 1) > 5 THEN LEFT(RATIO_1, 5) + 0.01 ELSE LEFT(RATIO_1, 5) END) AS RATIO_1, ");
		sql.append("IFNULL(CASE WHEN R.RATIO2 = 0 THEN NULL ELSE R.RATIO2 END, ");
		sql.append("CASE WHEN SUBSTRING(RATIO_2, 6, 1) > 5 THEN LEFT(RATIO_2, 5) + 0.01 ELSE LEFT(RATIO_2, 5) END) AS RATIO_2, ");
		sql.append("CASE WHEN SUBSTRING(RATIO_3, 6, 1) > 5 THEN LEFT(RATIO_3, 5) + 0.01 ELSE LEFT(RATIO_3, 5) END AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE C ");
		sql.append("LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ");
		sql.append("ON C.GSTIN = R.GSTIN AND C.TAX_PERIOD = R.TAX_PERIOD AND R.IS_DELETE = FALSE ");
		sql.append("AND C.SECTION_NAME='ITC Reversal Ratio'");
		sql.append("WHERE IS_ACTIVE = TRUE ");
		sql.append("AND SUB_SECTION_NAME IN (");
		sql.append("'Total Tax Amount', 'A', 'B', 'C', 'D_T1', 'D_T2', 'D_T3', 'D_T4', 'E', ");
		sql.append("'Ratio_1_R42', 'Ratio_2_R42', 'Ratio_3_R42', 'D2', 'Total Reversal', ");
		sql.append("'ITC Reversal Ratio', 'Total Reversal_RATIO_1', ");
		sql.append("'Total Reversal_RATIO_2', 'Total Reversal_RATIO_3') ");
		sql.append("AND DERIVED_RET_PERIOD = :derivedRetPeriod ");
		sql.append("AND C.GSTIN = :gstin");
		return sql.toString();
	}

	private String turnOverSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SUB_SECTION_NAME,TAXABLE_VALUE,");
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
		sql.append("('TAX','B2CS <>0','SEZ','DXP','EXPT','EXPWT',");
		sql.append("'DTA','EXT','NIL','B2CS = 0','Non GST /SCH3',");
		sql.append("'TOTAL_GSTIN_SUPPLIES','TTL_RCS',");
		sql.append(
				"'TAX_REVERSE_CHARGE','DTA_REVERSE_CHARGE','DXP_REVERSE_CHARGE',");
		sql.append(
				"'SEZ_REVERSE_CHARGE','ITC Reversal Ratio','T_GSTIN_SUPPLIES') ");

		sql.append(
				"AND DERIVED_RET_PERIOD=:derivedRetPeriod AND GSTIN=:gstin ");
		return sql.toString();
	}

	public List<Object[]> getCreditReverseSummary(final List<String> gstinList,
			final Integer retPeriod) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String sql = CreditReverseSummarySql(gstinList);
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("derivedRetPeriod", retPeriod);
			if (gstinList != null) {
				q.setParameter("gstinList", gstinList);
			}
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return objs;
	}

	private String CreditReverseSummarySql(List<String> gstinList) {
		StringBuilder sql = new StringBuilder();
		sql.append("select SECTION_NAME,");
		sql.append("SUM(RATIO_1) AS RATIO_01,SUM(RATIO_2) AS RATIO_02,");
		sql.append("SUM(RATIO_3) AS RATIO_03 FROM ( ");
		sql.append("select  'Reversal' as section_name,");
		sql.append("IFNULL(SUM(CASE WHEN SECTION_NAME IN ('EXT','NIL',");
		sql.append("'TOTAL_REVERSE_CHARGE_SUPPLIES','B2CS = 0') ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0)END),0) AS RATIO_1,");
		sql.append("IFNULL(SUM(CASE WHEN SECTION_NAME IN ('EXT','NIL',");
		sql.append(
				"'TOTAL_REVERSE_CHARGE_SUPPLIES','Non GST /SCH3','B2CS = 0') ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0)END),0) AS RATIO_2,");
		sql.append(" IFNULL(SUM(CASE WHEN SECTION_NAME IN ('EXT','NIL',");
		sql.append(
				"'TOTAL_REVERSE_CHARGE_SUPPLIES','Non GST /SCH3','B2CS = 0') ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0)END),0) AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE ");
		sql.append("AND SECTION_NAME IN ");
		sql.append(
				"('EXT','NIL','TOTAL_REVERSE_CHARGE_SUPPLIES','B2CS = 0','Non GST /SCH3') ");
		sql.append("AND  DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (gstinList != null && !gstinList.isEmpty()) {
			sql.append("AND GSTIN IN (:gstinList) ");
		}
		sql.append("group by SECTION_NAME ");
		sql.append("union all ");
		sql.append("select 'Turn_over' as section_name, ");
		sql.append(
				"(IFNULL(SUM(CASE WHEN SUB_SECTION_NAME = 'TOTAL_GSTIN_SUPPLIES' ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0) END),0) ");
		sql.append(
				"- IFNULL(SUM(CASE WHEN SUB_SECTION_NAME = 'Non GST /SCH3'  ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0) END),0))  as RATIO_1, ");
		sql.append("CASE WHEN SECTION_NAME IN ('TOTAL_GSTIN_SUPPLIES')  ");
		sql.append("THEN SUM(IFNULL(TAXABLE_VALUE,0)) END as RATIO_2, ");
		sql.append("CASE WHEN SECTION_NAME IN ('TOTAL_GSTIN_SUPPLIES') THEN  ");
		sql.append("SUM(IFNULL(TAXABLE_VALUE,0)) END as RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE  ");
		sql.append("AND SECTION_NAME IN  ");
		sql.append("('TOTAL_GSTIN_SUPPLIES','Non GST /SCH3')  ");
		sql.append("AND  DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (gstinList != null && !gstinList.isEmpty()) {
			sql.append("AND GSTIN IN (:gstinList) ");
		}
		sql.append("group by section_name ");
		sql.append("union all ");
		sql.append("select 'Ratios' as section_name, ");
		sql.append("IFNULL(CASE WHEN R.RATIO1=0 THEN NULL ELSE R.RATIO1 END,CASE WHEN substring(RATIO_1, 6, 1) > 5 THEN (LEFT(RATIO_1,5)+0.01) ");
		sql.append(
				"ELSE LEFT(RATIO_1,5) END) AS RATIO_1,");
		sql.append(
				"IFNULL(CASE WHEN R.RATIO2=0 THEN NULL ELSE R.RATIO2 END,CASE WHEN substring(RATIO_2, 6, 1) > 5 THEN (LEFT(RATIO_2)+0.01)  ");
		sql.append("ELSE LEFT(RATIO_2,5) END) AS RATIO_2,");
		sql.append(
				"CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ");
		sql.append("ELSE LEFT(RATIO_3,5) END AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE C LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN\r\n" + 
				"AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE WHERE IS_ACTIVE = TRUE  ");
		sql.append("AND SECTION_NAME IN  ");
		sql.append("('ITC Reversal Ratio')  ");
		sql.append("AND  DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (gstinList != null && !gstinList.isEmpty()) {
			sql.append("AND C.GSTIN IN (:gstinList) ");
		}
		sql.append(") GROUP BY SECTION_NAME ");
		return sql.toString();
	}

	public List<Object[]> getGstr3BRuleCompute(List<String> gstins,
			Integer retPeriod, String subSectionName) {
		List<Object[]> objs = new ArrayList<>();
		try {
			String sql = gstr3BQuery();
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("derivedRetPeriod", retPeriod);
			q.setParameter("gstins", gstins);
			q.setParameter("subSectionName", subSectionName);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return objs;
	}

	public String gstr3BQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TAX_PERIOD,GSTIN,'4(b)(1)' as SECTION_NAME,");
		sql.append("'4(b)(1)' AS SUB_SECTION_NAME,TAXABLE_VALUE,");
		sql.append(
				"IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,DERIVED_RET_PERIOD,BATCH_ID ");
		sql.append(" FROM GSTR3B_RULE42_COMPUTE ");
		sql.append(
				"WHERE IS_ACTIVE = TRUE AND SUB_SECTION_NAME=:subSectionName  ");
		sql.append(
				"AND GSTIN IN (:gstins) AND DERIVED_RET_PERIOD=:derivedRetPeriod ");
		return sql.toString();
	}
	
	public static void main(String[] args) {

		StringBuilder sql = new StringBuilder();
		sql.append("select SECTION_NAME,");
		sql.append("SUM(RATIO_1) AS RATIO_01,SUM(RATIO_2) AS RATIO_02,");
		sql.append("SUM(RATIO_3) AS RATIO_03 FROM ( ");
		sql.append("select  'Reversal' as section_name,");
		sql.append("IFNULL(SUM(CASE WHEN SECTION_NAME IN ('EXT','NIL',");
		sql.append("'TOTAL_REVERSE_CHARGE_SUPPLIES','B2CS = 0') ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0)END),0) AS RATIO_1,");
		sql.append("IFNULL(SUM(CASE WHEN SECTION_NAME IN ('EXT','NIL',");
		sql.append(
				"'TOTAL_REVERSE_CHARGE_SUPPLIES','Non GST /SCH3','B2CS = 0') ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0)END),0) AS RATIO_2,");
		sql.append(" IFNULL(SUM(CASE WHEN SECTION_NAME IN ('EXT','NIL',");
		sql.append(
				"'TOTAL_REVERSE_CHARGE_SUPPLIES','Non GST /SCH3','B2CS = 0') ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0)END),0) AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE ");
		sql.append("AND SECTION_NAME IN ");
		sql.append(
				"('EXT','NIL','TOTAL_REVERSE_CHARGE_SUPPLIES','B2CS = 0','Non GST /SCH3') ");
		sql.append("AND  DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (true) {
			sql.append("AND GSTIN IN (:gstinList) ");
		}
		sql.append("group by SECTION_NAME ");
		sql.append("union all ");
		sql.append("select 'Turn_over' as section_name, ");
		sql.append(
				"(IFNULL(SUM(CASE WHEN SUB_SECTION_NAME = 'TOTAL_GSTIN_SUPPLIES' ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0) END),0) ");
		sql.append(
				"- IFNULL(SUM(CASE WHEN SUB_SECTION_NAME = 'Non GST /SCH3'  ");
		sql.append("THEN IFNULL(TAXABLE_VALUE,0) END),0))  as RATIO_1, ");
		sql.append("CASE WHEN SECTION_NAME IN ('TOTAL_GSTIN_SUPPLIES')  ");
		sql.append("THEN SUM(IFNULL(TAXABLE_VALUE,0)) END as RATIO_2, ");
		sql.append("CASE WHEN SECTION_NAME IN ('TOTAL_GSTIN_SUPPLIES') THEN  ");
		sql.append("SUM(IFNULL(TAXABLE_VALUE,0)) END as RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE WHERE IS_ACTIVE = TRUE  ");
		sql.append("AND SECTION_NAME IN  ");
		sql.append("('TOTAL_GSTIN_SUPPLIES','Non GST /SCH3')  ");
		sql.append("AND  DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (true) {
			sql.append("AND GSTIN IN (:gstinList) ");
		}
		sql.append("group by section_name ");
		sql.append("union all ");
		sql.append("select 'Ratios' as section_name, ");
		sql.append("IFNULL(CASE WHEN R.RATIO1=0 THEN NULL ELSE R.RATIO1 END,CASE WHEN substring(RATIO_1, 6, 1) > 5 THEN (LEFT(RATIO_1,5)+0.01) ");
		sql.append(
				"ELSE LEFT(RATIO_1,5) END) AS RATIO_1,");
		sql.append(
				"IFNULL(CASE WHEN R.RATIO2=0 THEN NULL ELSE R.RATIO2 END,CASE WHEN substring(RATIO_2, 6, 1) > 5 THEN (LEFT(RATIO_2)+0.01)  ");
		sql.append("ELSE LEFT(RATIO_2,5) END) AS RATIO_2,");
		sql.append(
				"CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ");
		sql.append("ELSE LEFT(RATIO_3,5) END AS RATIO_3 ");
		sql.append("FROM GSTR3B_RULE42_COMPUTE C LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN\r\n" + 
				"AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE WHERE IS_ACTIVE = TRUE  ");
		sql.append("AND SECTION_NAME IN  ");
		sql.append("('ITC Reversal Ratio')  ");
		sql.append("AND  DERIVED_RET_PERIOD=:derivedRetPeriod ");
		if (true) {
			sql.append("AND C.GSTIN IN (:gstinList) ");
		}
		sql.append(") GROUP BY SECTION_NAME ");
		//return sql.toString();
	
		System.out.println(sql.toString()); 
	}

}
