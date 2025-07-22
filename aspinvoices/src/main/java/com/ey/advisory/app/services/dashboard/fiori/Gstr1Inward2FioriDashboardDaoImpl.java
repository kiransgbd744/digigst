package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1Inward2FioriDashboardDaoImpl")
public class Gstr1Inward2FioriDashboardDaoImpl
		implements Gstr1Inward2FioriDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getAllHeadersData(String fy, List<String> recepientGstins,
			List<String> returnPeriods) throws AppException {
		try {
			
			
			String queryString = "SELECT SUM(NO_OF_SUPPLIERS_PR) AS NO_OF_SUPPLIERS_PR,"
					+ "					        SUM(NO_OF_SUPPLIERS_2A) AS NO_OF_SUPPLIERS_2A,"
					+ "					        SUM(NO_OF_SUPPLIERS_2B) AS NO_OF_SUPPLIERS_2B,"
					+ "					        SUM(PR_TOTAL_TAX) AS PR_TOTAL_TAX,"
					+ "					        SUM(A2_TOTAL_TAX) AS A2_TOTAL_TAX,"
					+ "					        SUM(B2_TOTAL_TAX) AS B2_TOTAL_TAX"
					+ "  FROM("
					+ "              SELECT COUNT( DISTINCT SGSTIN ) AS NO_OF_SUPPLIERS_PR,"
					+ "					  0 AS NO_OF_SUPPLIERS_2A,"
					+ "					  0 AS NO_OF_SUPPLIERS_2B,"
					+ "					  0 AS PR_TOTAL_TAX,"
					+ "					  0 AS A2_TOTAL_TAX,"
					+ "					  0 AS B2_TOTAL_TAX"
					+ "					  FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY WHERE "
					//+ "FI_YEAR = :fy AND "
					+ "					   TAX_PERIOD IN :returnPeriods   "
					+ "					  AND RGSTIN IN :recepientGstins AND TRANSACTION_TYPE  IN "
					+ "					  ('PR') AND DOC_TYPE<>'TOTAL' AND LENGTH(SGSTIN)>3"

					+ "					  UNION ALL"
					+ "					  SELECT 0 AS NO_OF_SUPPLIERS_PR,"
					+ "					  0 AS NO_OF_SUPPLIERS_2A,"
					+ "					  0 AS NO_OF_SUPPLIERS_2B,"
					+ "					  ABS(SUM(IFNULL(PR_TOTAL_TAX,0))) AS PR_TOTAL_TAX,"
					+ "					  0 AS A2_TOTAL_TAX,"
					+ "					  0 AS B2_TOTAL_TAX"
					+ "					  FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY WHERE "
				//	+ "FI_YEAR = :fy AND "
					+ "					   TAX_PERIOD IN :returnPeriods   "
					+ "					  AND RGSTIN IN :recepientGstins AND TRANSACTION_TYPE  IN "
					+ "					  ('PR') AND DOC_TYPE<>'TOTAL'"
					+ "					  UNION ALL"
					+ "					   SELECT 0 AS NO_OF_SUPPLIERS_PR,"
					+ "                             COUNT( DISTINCT SGSTIN ) AS NO_OF_SUPPLIERS_2A,"
					+ "					         0 AS NO_OF_SUPPLIERS_2B,"
					+ "					         0 AS PR_TOTAL_TAX,"
					+ "					  0 AS A2_TOTAL_TAX,"
					+ "					   0 AS B2_TOTAL_TAX"
					+ "					  FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY WHERE "
					//+ "FI_YEAR = :fy AND"
					+ "					   TAX_PERIOD IN :returnPeriods   "
					+ "					  AND RGSTIN IN :recepientGstins AND TRANSACTION_TYPE  IN "
					+ "					  ('2A') AND DOC_TYPE<>'TOTAL' "
					+ "					  AND  LENGTH(SGSTIN)>3"
					+ "					 UNION ALL"
					+ "					  SELECT 0 AS NO_OF_SUPPLIERS_PR,"
					+ "					 0 AS NO_OF_SUPPLIERS_2A,"
					+ "					  0 AS NO_OF_SUPPLIERS_2B,"
					+ "					   0 AS PR_TOTAL_TAX,"
					+ "					  ABS(SUM(IFNULL(A2_TOTAL_TAX,0))) AS A2_TOTAL_TAX,"
					+ "					   0 AS B2_TOTAL_TAX"
					+ "					  FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY WHERE "
					//+ "FI_YEAR = :fy AND "
					+ "					   TAX_PERIOD IN :returnPeriods   "
					+ "					  AND RGSTIN IN :recepientGstins AND TRANSACTION_TYPE  IN "
					+ "					  ('2A') AND DOC_TYPE<>'TOTAL'"

					+ "					  UNION ALL"
					+ "					   SELECT  0 AS NO_OF_SUPPLIERS_PR,"
					+ "					          0 AS NO_OF_SUPPLIERS_2A,"
					+ "					  COUNT( DISTINCT SGSTIN ) AS NO_OF_SUPPLIERS_2B,"
					+ "					  0 AS PR_TOTAL_TAX,"
					+ "					  0 AS A2_TOTAL_TAX,"
					+ "					 0 AS B2_TOTAL_TAX"
					+ "					  FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY WHERE "
					//+ "FI_YEAR = :fy AND "
					+ "					   TAX_PERIOD IN :returnPeriods   "
					+ "					  AND RGSTIN IN :recepientGstins AND TRANSACTION_TYPE  IN "
					+ "					  ('2B') AND DOC_TYPE<>'TOTAL'"
					+ "					  AND  LENGTH(SGSTIN)>3"

					+ "					  UNION ALL"
					+ "					  SELECT  0 AS NO_OF_SUPPLIERS_PR,"
					+ "					          0 AS NO_OF_SUPPLIERS_2A,"
					+ "					  0 AS NO_OF_SUPPLIERS_2B,"
					+ "					  0 AS PR_TOTAL_TAX,"
					+ "					  0 AS A2_TOTAL_TAX,"
					+ "					  ABS(SUM(IFNULL(B2_TOTAL_TAX,0))) AS B2_TOTAL_TAX"
					+ "					  FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY WHERE "
					//+ "FI_YEAR = :fy AND"
					+ "					   TAX_PERIOD IN :returnPeriods   "
					+ "					  AND RGSTIN IN :recepientGstins AND TRANSACTION_TYPE  IN "
					+ "					  ('2B') AND DOC_TYPE<>'TOTAL'"
					+ "					 )";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepientGstins", recepientGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods "
						+ returnPeriods + ", recepientGstins " + recepientGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getAllHeadersData");
		}
	}

	@Override
	public List<Object[]> getprVsGstr2aRecords(String fy,
			List<String> recepGstins, List<String> returnPeriods)
			throws AppException {
		try {
			/*String queryString = "SELECT TOP 5 * FROM(SELECT SGSTIN,ABS(SUM(PR_TOTAL_TAX)) "
					+ " AS PR_TOTAL_TAX,ABS(SUM(A2_TOTAL_TAX)) AS A2_TOTAL_TAX "
					+ " FROM TBL_SAC_GSTR1_INWARD_RECON "
					+ " WHERE SGSTIN IS NOT NULL AND LENGTH(SGSTIN)>3 AND "
					+ " TRANSACTION_TYPE = 'Recon 2A' AND DOC_TYPE<>'TOTAL' "
					+ " AND FI_YEAR = :fy AND TAX_PERIOD IN :returnPeriods AND "
					+ " RGSTIN IN :recepGstins GROUP BY SGSTIN) "
					+ " ORDER BY (IFNULL(PR_TOTAL_TAX,0)+IFNULL(A2_TOTAL_TAX,0)) DESC";*/
			
			String queryString =  "SELECT TOP 5 * FROM(SELECT SGSTIN,ABS(SUM(PR_TOTAL_TAX)) "
			+ "AS PR_TOTAL_TAX,ABS(SUM(A2_TOTAL_TAX)) AS A2_TOTAL_TAX "
			+ "FROM TBL_INWARD_DASHBOARD_2A_2B_PR_RECON_DATA "
			+ "WHERE SGSTIN IS NOT NULL AND LENGTH(SGSTIN)>3 AND "
			+ "TRANSACTION_TYPE = 'Recon 2A' AND DOC_TYPE<>'TOTAL' "
			//+ "AND "
			//+ "FI_YEAR = :fy"
			+ " AND TAX_PERIOD IN :returnPeriods "
			+ "AND RGSTIN IN :recepGstins GROUP BY SGSTIN) "
			+ "ORDER BY (IFNULL(PR_TOTAL_TAX,0)+IFNULL(A2_TOTAL_TAX,0)) DESC";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepGstins", recepGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy
						+ ", listOfReturnPrds " + returnPeriods
						+ ", recepGstins " + recepGstins + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getprVsGstr2aRecords");
		}
	}

	@Override
	public List<Object[]> getprVsGstr2bRecords(String fy,
			List<String> recepGstins, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT TOP 5 * FROM(" +
					"SELECT SGSTIN,ABS(SUM(PR_TOTAL_TAX)) AS PR_TOTAL_TAX,ABS(SUM(B2_TOTAL_TAX)) AS B2_TOTAL_TAX " +
					"FROM TBL_INWARD_DASHBOARD_2A_2B_PR_RECON_DATA " +
					"WHERE SGSTIN IS NOT NULL AND LENGTH(SGSTIN)>3 AND " +
					"TRANSACTION_TYPE = 'Recon 2B' AND DOC_TYPE<>'TOTAL' " +
					//"AND FI_YEAR = :fy"
					 " AND TAX_PERIOD IN :returnPeriods AND " +
					"RGSTIN IN :recepGstins GROUP BY SGSTIN) " +
					"ORDER BY (IFNULL(PR_TOTAL_TAX,0)+IFNULL(B2_TOTAL_TAX,0)) DESC";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepGstins", recepGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy
						+ ", listOfReturnPrds " + returnPeriods
						+ ", recepGstins " + recepGstins + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getprVsGstr2bRecords");
		}
	}

	@Override
	public List<Object[]> getPurchaseRegisterVsGstr2b(String fy,
			List<String> recepGstins, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT REPORT_TYPE,ABS(SUM(IFNULL(PR_TOTAL_TAX,0))) AS PR_TOTAL_TAX,ABS(SUM(IFNULL(B2_TOTAL_TAX,0))) AS B2_TOTAL_TAX " +
					"FROM TBL_INWARD_DASHBOARD_2A_2B_PR_RECON_DATA WHERE REPORT_TYPE IS NOT NULL AND " +
					"TRANSACTION_TYPE = 'Recon 2B' AND DOC_TYPE<>'TOTAL' " +
					//"AND FI_YEAR = :fy"
					 " AND TAX_PERIOD IN :returnPeriods AND " +
					"RGSTIN IN :recepGstins GROUP BY REPORT_TYPE " +
					"ORDER BY (CASE WHEN REPORT_TYPE='Exact' THEN 1 " +
					"WHEN REPORT_TYPE='Mismatch' THEN 2 " +
					"WHEN REPORT_TYPE='Potential' THEN 3 " +
					"WHEN REPORT_TYPE='Addition in PR' THEN 4 " +
					"WHEN REPORT_TYPE='Addition in 2B' THEN 5 " +
					"WHEN REPORT_TYPE='Force Match' THEN 6 " +
					"WHEN REPORT_TYPE='Logical Match' THEN 7 END)";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepGstins", recepGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy
						+ ", listOfReturnPrds " + returnPeriods
						+ ", recepGstins " + recepGstins + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getPurchaseRegisterVsGstr2b");
		}
	}

	@Override
	public List<Object[]> get2aVs2bVsPrSummary(String fy,
			List<String> recepientGstins, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT TRANSACTION_TYPE,ABS(SUM(IFNULL(PR_TOTAL_TAX,0))) "
					+ " AS PR_TOTAL_TAX,ABS(SUM(IFNULL(A2_TOTAL_TAX,0))) AS "
					+ " A2_TOTAL_TAX,ABS(SUM(IFNULL(B2_TOTAL_TAX,0))) AS "
					+ " B2_TOTAL_TAX,ABS(SUM(IFNULL(PR_TAXABLE_VALUE,0))) AS "
					+ " PR_TAXABLE_VALUE,ABS(SUM(IFNULL(A2_TAXABLE_VALUE,0))) AS "
					+ " A2_TAXABLE_VALUE,ABS(SUM(IFNULL(B2_TAXABLE_VALUE,0))) AS "
					+ " B2_TAXABLE_VALUE FROM TBL_SAC_GSTR1_INWARD_RECON "
					+ " WHERE "
					//+ "FI_YEAR = :fy AND "
					+ " TAX_PERIOD IN :returnPeriods "
					+ " AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepientGstins", recepientGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", supplierGstins " + recepientGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1InwardFioriDashboardDaoImpl.get2aVs2bVsPrSummary");
		}
	}

	@Override
	public List<Object[]> getPr2a2bData(String fy,
			List<String> recepientGstins, List<String> returnPeriods)
			throws AppException {
		try {
			
			
			String queryString ="SELECT DOC_TYPE,"
					+ "					SUM(PR_TAXABLE_VALUE) AS PR_TAXABLE_VALUE,"
					+ "					SUM(PR_TOTAL_TAX)     AS PR_TOTAL_TAX,"
					+ "					SUM(PR_INVOICE_VAL)   AS PR_INVOICE_VAL,"
					+ "					SUM(A2_TAXABLE_VALUE) AS A2_TAXABLE_VALUE,"
					+ "					SUM(A2_TOTAL_TAX)     AS A2_TOTAL_TAX,"
					+ "					SUM(A2_INVOICE_VAL)   AS A2_INVOICE_VAL,"
					+ "					SUM(B2_TAXABLE_VALUE) AS B2_TAXABLE_VALUE,"
					+ "					SUM(B2_TOTAL_TAX)     AS B2_TOTAL_TAX,"
					+ "					SUM(B2_INVOICE_VAL)   AS B2_INVOICE_VAL"
					+ "					FROM("
					+ "					SELECT DOC_TYPE,"
					+ "					SUM(IFNULL(PR_TAXABLE_VALUE,0)) AS PR_TAXABLE_VALUE,"
					+ "					SUM(IFNULL(PR_TOTAL_TAX,0)) AS PR_TOTAL_TAX,"
					+ "					SUM(IFNULL(PR_TAXABLE_VALUE,0))+SUM(IFNULL(PR_TOTAL_TAX,0)) AS PR_INVOICE_VAL,"
					+ "					0 AS A2_TAXABLE_VALUE,"
					+ "					0 AS A2_TOTAL_TAX,"
					+ "					0 AS A2_INVOICE_VAL,"
					+ "					0 AS B2_TAXABLE_VALUE,"
					+ "					0 AS B2_TOTAL_TAX,"
					+ "					0 AS B2_INVOICE_VAL"
					+ "					FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY"
					+ "					WHERE TRANSACTION_TYPE IN ('PR') AND "
				//	+ "					FI_YEAR = :fy AND"
					+ " TAX_PERIOD IN :returnPeriods   AND"
					+ "					RGSTIN IN :recepientGstins"
					+ "					AND DOC_TYPE IN ('B2B','Credit Notes','Debit Notes','ISD','Imports (Goods)','Imports (Services)','Imports (Goods-SEZ)','Unregistered Supplies')"
					+ "					GROUP BY DOC_TYPE"
					+ "					UNION ALL"
					+ "					SELECT DOC_TYPE,"
					+ "					0 AS PR_TAXABLE_VALUE,"
					+ "					0 AS PR_TOTAL_TAX,"
					+ "					0 AS PR_INVOICE_VAL,"
					+ "					SUM(IFNULL(A2_TAXABLE_VALUE,0)) AS A2_TAXABLE_VALUE,"
					+ "					SUM(IFNULL(A2_TOTAL_TAX,0)) AS A2_TOTAL_TAX,"
					+ "				    SUM(IFNULL(A2_TAXABLE_VALUE,0))+SUM(IFNULL(A2_TOTAL_TAX,0)) AS A2_INVOICE_VAL,"
					+ "					0 AS B2_TAXABLE_VALUE,"
					+ "					0 AS B2_TOTAL_TAX,"
					+ "					0 AS B2_INVOICE_VAL"
					+ "					FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY WHERE TRANSACTION_TYPE"
					+ "					IN ('2A') AND"
					//+ "					FI_YEAR = :fy AND"
					+ "  TAX_PERIOD IN :returnPeriods   AND"
					+ "					RGSTIN IN :recepientGstins"
					+ "					AND DOC_TYPE IN ('B2B','Credit Notes','Debit Notes','ISD','Imports (Goods)','Imports (Services)','Imports (Goods-SEZ)','Unregistered Supplies')"
					+ "					GROUP BY DOC_TYPE"
					+ "					UNION ALL"
					+ "					SELECT DOC_TYPE,"
					+ "					0 AS PR_TAXABLE_VALUE,"
					+ "					0 AS PR_TOTAL_TAX,"
					+ "					0 AS PR_INVOICE_VAL,"
					+ "					0 AS A2_TAXABLE_VALUE,"
					+ "					0 AS A2_TOTAL_TAX,"
					+ "					0 AS A2_INVOICE_VAL,"
					+ "					SUM(IFNULL(B2_TAXABLE_VALUE,0)) AS B2_TAXABLE_VALUE,"
					+ "					SUM(IFNULL(B2_TOTAL_TAX,0)) AS B2_TOTAL_TAX,"
					+ "					SUM(IFNULL(B2_TAXABLE_VALUE,0))+SUM(IFNULL(B2_TOTAL_TAX,0))"
					+ "					AS B2_INVOICE_VAL"
					+ "					FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY WHERE TRANSACTION_TYPE"
					+ "					IN ('2B') AND"
					//+ "					FI_YEAR = :fy AND"
					+ " TAX_PERIOD IN :returnPeriods   AND"
					+ "					RGSTIN IN :recepientGstins"
					+ "					AND DOC_TYPE IN ('B2B','Credit Notes','Debit Notes','ISD','Imports (Goods)','Imports (Services)','Imports (Goods-SEZ)','Unregistered Supplies')"
					+ "					GROUP BY DOC_TYPE"
					+ "					UNION ALL"
					+ "					SELECT 'TOTAL' AS DOC_TYPE,"
					+ "					SUM(PR_TAXABLE_VALUE) AS PR_TAXABLE_VALUE,"
					+ "					SUM(PR_TOTAL_TAX)     AS PR_TOTAL_TAX,"
					+ "					SUM(PR_INVOICE_VAL)   AS PR_INVOICE_VAL,"
					+ "					SUM(A2_TAXABLE_VALUE) AS A2_TAXABLE_VALUE,"
					+ "					SUM(A2_TOTAL_TAX)     AS A2_TOTAL_TAX,"
					+ "					SUM(A2_INVOICE_VAL)   AS A2_INVOICE_VAL,"
					+ "					SUM(B2_TAXABLE_VALUE) AS B2_TAXABLE_VALUE,"
					+ "					SUM(B2_TOTAL_TAX)     AS B2_TOTAL_TAX,"
					+ "					SUM(B2_INVOICE_VAL)   AS B2_INVOICE_VAL"
					+ "					FROM("
					+ "					SELECT DOC_TYPE,"
					+ "					SUM(IFNULL(PR_TAXABLE_VALUE,0)) AS PR_TAXABLE_VALUE,"
					+ "					SUM(IFNULL(PR_TOTAL_TAX,0)) AS PR_TOTAL_TAX,"
					+ "					SUM(IFNULL(PR_TAXABLE_VALUE,0))+SUM(IFNULL(PR_TOTAL_TAX,0)) AS PR_INVOICE_VAL,"
					+ "					0 AS A2_TAXABLE_VALUE,"
					+ "					0 AS A2_TOTAL_TAX,"
					+ "					0 AS A2_INVOICE_VAL,"
					+ "					0 AS B2_TAXABLE_VALUE,"
					+ "					0 AS B2_TOTAL_TAX,"
					+ "					0 AS B2_INVOICE_VAL"
					+ "					FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY"
					+ "					WHERE TRANSACTION_TYPE IN ('PR') AND"
					//+ "					FI_YEAR = :fy AND"
					+ " TAX_PERIOD IN :returnPeriods   AND"
					+ "					RGSTIN IN :recepientGstins"
					+ "					AND DOC_TYPE IN ('B2B','Credit Notes','Debit Notes','ISD','Imports (Goods)','Imports (Services)','Imports (Goods-SEZ)','Unregistered Supplies')"
					+ "					GROUP BY DOC_TYPE"
					+ "					UNION ALL"
					+ "					SELECT DOC_TYPE,"
					+ "					0 AS PR_TAXABLE_VALUE,"
					+ "					0 AS PR_TOTAL_TAX,"
					+ "					0 AS PR_INVOICE_VAL,"
					+ "					SUM(IFNULL(A2_TAXABLE_VALUE,0)) AS A2_TAXABLE_VALUE,"
					+ "					SUM(IFNULL(A2_TOTAL_TAX,0)) AS A2_TOTAL_TAX,"
					+ "					SUM(IFNULL(A2_TAXABLE_VALUE,0))+SUM(IFNULL(A2_TOTAL_TAX,0)) AS A2_INVOICE_VAL,"
					+ "					0 AS B2_TAXABLE_VALUE,"
					+ "					0 AS B2_TOTAL_TAX,"
					+ "					0 AS B2_INVOICE_VAL"
					+ "					FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY WHERE TRANSACTION_TYPE"
					+ "					IN ('2A') AND"
					//+ "					FI_YEAR = :fy AND"
					+ " TAX_PERIOD IN :returnPeriods   AND"
					+ "					RGSTIN IN :recepientGstins"
					+ "					AND DOC_TYPE IN ('B2B','Credit Notes','Debit Notes','ISD','Imports (Goods)','Imports (Services)','Imports (Goods-SEZ)','Unregistered Supplies')"
					+ "					GROUP BY DOC_TYPE"
					+ "					UNION ALL"
					+ "					SELECT DOC_TYPE,"
					+ "					0 AS PR_TAXABLE_VALUE,"
					+ "					0 AS PR_TOTAL_TAX,"
					+ "					0 AS PR_INVOICE_VAL,"
					+ "					0 AS A2_TAXABLE_VALUE,"
					+ "					0 AS A2_TOTAL_TAX,"
					+ "					0 AS A2_INVOICE_VAL,"
					+ "					SUM(IFNULL(B2_TAXABLE_VALUE,0)) AS B2_TAXABLE_VALUE,"
					+ "					SUM(IFNULL(B2_TOTAL_TAX,0)) AS B2_TOTAL_TAX,"
					+ "					SUM(IFNULL(B2_TAXABLE_VALUE,0))+SUM(IFNULL(B2_TOTAL_TAX,0))"
					+ "					AS B2_INVOICE_VAL"
					+ "					FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY WHERE TRANSACTION_TYPE"
					+ "					IN ('2B') AND"
					//+ "					FI_YEAR = :fy AND"
					+ " TAX_PERIOD IN :returnPeriods   AND"
					+ "					RGSTIN IN :recepientGstins"
					+ "					AND DOC_TYPE IN ('B2B','Credit Notes','Debit Notes','ISD','Imports (Goods)','Imports (Services)','Imports (Goods-SEZ)','Unregistered Supplies')"
					+ "					GROUP BY DOC_TYPE"
					+ "					)"
					+ "					) GROUP BY DOC_TYPE"
					+ "					ORDER BY (CASE WHEN DOC_TYPE='B2B' THEN 1"
					+ "					WHEN DOC_TYPE='Credit Notes' THEN 2"
					+ "					WHEN DOC_TYPE='Debit Notes' THEN 3"
					+ "					WHEN DOC_TYPE='ISD' THEN 4"
					+ "					WHEN DOC_TYPE='Imports (Goods)' THEN 5"
					+ "					WHEN DOC_TYPE='Imports (Services)' THEN 6"
					+ "					WHEN DOC_TYPE='Imports (Goods-SEZ)' THEN 7"
					+ "					WHEN DOC_TYPE='Unregistered Supplies' THEN 8"
					+ "					WHEN DOC_TYPE='TOTAL' THEN 9 END)";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepientGstins", recepientGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", listOfRecepGstin "
						+ recepientGstins + "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getPr2a2bData");
		}
	}

	@Override
	public List<Object[]> getLastRefereshedOn(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT MAX(CASE WHEN TRANSACTION_TYPE='Recon 2A' THEN REFRESHED_ON END) "
					+ " AS LAST_REFRESHED_ON_2A, "
					+ " MAX(CASE WHEN TRANSACTION_TYPE='Recon 2B' THEN REFRESHED_ON END) "
					+ " AS LAST_REFRESHED_ON_2B "
					+ " FROM TBL_SAC_GSTR1_INWARD_RECON WHERE "
					//+ " FI_YEAR = :fy AND"
					+ " TAX_PERIOD IN :returnPeriods AND "
					+ " RGSTIN IN :listOfRecepGstin";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("listOfRecepGstin", listOfRecepGstin);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", listOfRecepGstin "
						+ listOfRecepGstin + "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getLastRefereshedOn");
		}
	}

	@Override
	public List<Object[]> getPurchaseRegisterVsGstr2a(String fy,
			List<String> recepGstins, List<String> returnPeriods)
			throws AppException {
		try {
			//backup-10/5/2024
		/*	String queryString = "SELECT REPORT_TYPE,ABS(SUM(IFNULL(PR_TOTAL_TAX,0))) "
					+ " AS PR_TOTAL_TAX,ABS(SUM(IFNULL(A2_TOTAL_TAX,0))) AS A2_TOTAL_TAX "
					+ " FROM TBL_SAC_GSTR1_INWARD_RECON WHERE REPORT_TYPE IS NOT NULL AND "
					+ " TRANSACTION_TYPE = 'Recon 2A' AND DOC_TYPE<>'TOTAL' "
					+ " AND FI_YEAR = :fy AND TAX_PERIOD IN :returnPeriods AND "
					+ " RGSTIN IN :recepGstins GROUP BY REPORT_TYPE "
					+ " ORDER BY (CASE WHEN REPORT_TYPE='Exact Match' THEN 1 "
					+ " WHEN REPORT_TYPE='Mismatch' THEN 2 "
					+ " WHEN REPORT_TYPE='Pot Match (I & II)' THEN 3 "
					+ "	WHEN REPORT_TYPE='Addition in PR' THEN 4 "
					+ "	WHEN REPORT_TYPE='Addition in 2A' THEN 5 "
					+ " WHEN REPORT_TYPE='Force Match' THEN 6 "
					+ " WHEN REPORT_TYPE='Logical Match' THEN 7 END)";*/
			String queryString = "SELECT REPORT_TYPE,ABS(SUM(IFNULL(PR_TOTAL_TAX,0))) "
					+ "AS PR_TOTAL_TAX,ABS(SUM(IFNULL(A2_TOTAL_TAX,0))) AS A2_TOTAL_TAX "
					+ "FROM TBL_INWARD_DASHBOARD_2A_2B_PR_RECON_DATA WHERE REPORT_TYPE IS NOT NULL AND "
					+ "TRANSACTION_TYPE = 'Recon 2A' AND DOC_TYPE<>'TOTAL' "
					//+ "AND FI_YEAR = :fy"
					+ " AND TAX_PERIOD IN :returnPeriods "
					+ "AND RGSTIN IN :recepGstins GROUP BY REPORT_TYPE "
					+ "ORDER BY (CASE WHEN REPORT_TYPE='Exact' THEN 1 "
					+ "WHEN REPORT_TYPE='Mismatch' THEN 2 "
					+ "WHEN REPORT_TYPE='Potential' THEN 3 "
					+ "WHEN REPORT_TYPE='Addition in PR' THEN 4 "
					+ "WHEN REPORT_TYPE='Addition in 2B' THEN 5 "
					+ "WHEN REPORT_TYPE='Force Match' THEN 6 "
					+ "WHEN REPORT_TYPE='Logical Match' THEN 7 END) ";

			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepGstins", recepGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy
						+ ", listOfReturnPrds " + returnPeriods
						+ ", recepGstins " + recepGstins + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getPurchaseRegisterVsGstr2a");
		}
	}

	@Override
	public List<Object[]> get2aVs2bVsPrSummarySuppliers(String fy,
			List<String> recepientGstins, List<String> returnPeriods)
			throws AppException {
		try {
			
			String queryString = "SELECT TRANSACTION_TYPE,COUNT(DISTINCT SGSTIN) "
					+ "					  AS B2_SUP_CNT FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY "
					+ "					  WHERE TRANSACTION_TYPE IN ('PR') AND "
					+ "					  LENGTH(SGSTIN)>3 "
				//	+ "AND FI_YEAR = :fy  "
					+ "					  AND TAX_PERIOD IN  :returnPeriods "
					+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE"
					+ "					  UNION ALL"
					+ "					   SELECT TRANSACTION_TYPE,COUNT(DISTINCT SGSTIN) "
					+ "					  AS B2_SUP_CNT FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY "
					+ "					  WHERE TRANSACTION_TYPE IN ('2A') AND "
					+ "					  LENGTH(SGSTIN)>3 "
					//+ "AND FI_YEAR = :fy  "
					+ "					  AND TAX_PERIOD IN  :returnPeriods "
					+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE"
					+ "					  UNION ALL"
					+ "					   SELECT TRANSACTION_TYPE,COUNT(DISTINCT SGSTIN) "
					+ "					  AS B2_SUP_CNT FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY "
					+ "					  WHERE TRANSACTION_TYPE IN ('2B') AND "
					+ "					  LENGTH(SGSTIN)>3 "
					//+ "AND FI_YEAR = :fy  "
					+ "					  AND TAX_PERIOD IN  :returnPeriods "
					+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE";


			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepientGstins", recepientGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", supplierGstins " + recepientGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.get2aVs2bVsPrSummarySuppliers");
		}
	}

	@Override
	public List<Object[]> get2aVs2bVsPrSummaryTaxable(String fy,
			List<String> recepientGstins, List<String> returnPeriods)
			throws AppException {
		try {
			
			String queryString = "SELECT TRANSACTION_TYPE,"
					+ "					  ABS(SUM(IFNULL(PR_TAXABLE_VALUE,0))) AS TAXABLE_VALUE "
					+ "					  FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY "
					+ "					  WHERE  DOC_TYPE<>'TOTAL' "
					//+ "					  AND FI_YEAR = :fy"
					+ " AND TAX_PERIOD IN :returnPeriods"
					+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE"
					+ "					  UNION ALL"
					+ "					  SELECT TRANSACTION_TYPE,"
					+ "					  ABS(SUM(IFNULL(A2_TAXABLE_VALUE,0))) AS TAXABLE_VALUE "
					+ "					  FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY "
					+ "					  WHERE  DOC_TYPE<>'TOTAL' "
					//+ "					  AND FI_YEAR = :fy"
					+ " AND TAX_PERIOD IN :returnPeriods"
					+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE"
					+ "					  UNION ALL"
					+ "					  SELECT TRANSACTION_TYPE,"
					+ "					  ABS(SUM(IFNULL(B2_TAXABLE_VALUE,0))) AS TAXABLE_VALUE "
					+ "					  FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY "
					+ "					  WHERE  DOC_TYPE<>'TOTAL' "
					//+ "					  AND FI_YEAR = :fy"
					+ " AND TAX_PERIOD IN :returnPeriods"
					+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE";


			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepientGstins", recepientGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", supplierGstins " + recepientGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.get2aVs2bVsPrSummaryTaxable");
		}
	}

	@Override
	public List<Object[]> get2aVs2bVsPrSummaryTotalTax(String fy,
			List<String> recepientGstins, List<String> returnPeriods)
			throws AppException {
		try {
				
				    String queryString = "SELECT TRANSACTION_TYPE,"
				    		+ "					  ABS(SUM(IFNULL(PR_TOTAL_TAX,0))) AS TOTAL_TAX "
				    		+ "					  FROM TBL_INWARD_DASHBOARD_PR_DATA_SMRY "
				    		+ "					  WHERE  DOC_TYPE<>'TOTAL' "
				    		//+ "					  AND FI_YEAR = :fy"
				    		+ " AND TAX_PERIOD IN :returnPeriods"
				    		+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE"
				    		+ "					  UNION ALL"
				    		+ "					    SELECT TRANSACTION_TYPE,"
				    		+ "					  ABS(SUM(IFNULL(A2_TOTAL_TAX,0))) AS TOTAL_TAX "
				    		+ "					  FROM TBL_INWARD_DASHBOARD_2A_DATA_SMRY "
				    		+ "					  WHERE  DOC_TYPE<>'TOTAL' "
				    		//+ "					  AND FI_YEAR = :fy"
				    		+ " AND TAX_PERIOD IN :returnPeriods"
				    		+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE"
				    		+ "					  UNION ALL"
				    		+ "					  SELECT TRANSACTION_TYPE,"
				    		+ "					  ABS(SUM(IFNULL(b2_TOTAL_TAX,0))) AS TOTAL_TAX "
				    		+ "					  FROM TBL_INWARD_DASHBOARD_2B_DATA_SMRY "
				    		+ "					  WHERE  DOC_TYPE<>'TOTAL' "
				    		//+ "					  AND FI_YEAR = :fy"
				    		+ " AND TAX_PERIOD IN :returnPeriods"
				    		+ "					  AND RGSTIN IN :recepientGstins GROUP BY TRANSACTION_TYPE";


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepientGstins", recepientGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", supplierGstins " + recepientGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.get2aVs2bVsPrSummaryTotalTax");
		}
	}

	@Override
	public List<String> getReconLastUpdatedOn(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT MAX(REFRESHED_ON) AS RECON_LAST_UPDATED_ON "
					+ " FROM TBL_INWARD_DASHBOARD_2A_2B_PR_RECON_DATA "
					+ " WHERE TRANSACTION_TYPE IN ('Recon 2A','Recon 2B') AND "
					//+ " FI_YEAR = :fy AND"
					+ "  TAX_PERIOD IN :returnPeriods AND "
					+ " RGSTIN IN :listOfRecepGstin";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("listOfRecepGstin", listOfRecepGstin);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", listOfRecepGstin "
						+ listOfRecepGstin + "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Inward2FioriDashboardDaoImpl.getReconLastUpdatedOn");
		}
	}
	

}
