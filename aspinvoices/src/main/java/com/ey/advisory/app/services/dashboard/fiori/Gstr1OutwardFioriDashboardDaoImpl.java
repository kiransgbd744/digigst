package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("Gstr1OutwardFioriDashboardDaoImpl")
public class Gstr1OutwardFioriDashboardDaoImpl
		implements Gstr1OutwardFioriDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<String> getSupGstinList(String supplierPan)
			throws AppException {
		try {
			String queryString = "select DISTINCT GSTIN from TBL_OUTWARD_DASH_NET_ITC"
					+ " where GSTIN LIKE '%" + supplierPan
					+ "%' order by GSTIN asc";
			// return SupGstinList();
			Query q = entityManager.createNativeQuery(queryString);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given supplierPan " + supplierPan
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getSupGstinList");
		}
	}

	@Override
	public List<String> getAllReturnPeriods(String fy) throws AppException {
		try {
			String queryString = "select DISTINCT RETURN_PERIOD from "
					+ " TBL_OUTWARD_DASH_NET_ITC where FI_YEAR = :fy";
			// return returnPeriodsList();
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getSupGstinList");
		}
	}

	@Override
	public List<Object[]> getAllGrossOutwardSupp(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			String queryString = 
				    "SELECT TRANSACTION_TYPE, SUM(INVOICE_VALUE) AS INVOICE_VALUE " +
				    "FROM ( " +
				    "  SELECT TRANSACTION_TYPE, ORG_INVOICE_VALUE AS INVOICE_VALUE, ORG_TAXABLE_VALUE AS TAXABLE_VALUE, " +
				    "  ORG_IGST AS IGST_AMT, ORG_CGST AS CGST_AMT, ORG_SGST AS SGST_AMT, ORG_CESS AS CESS " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', " +
				    "  'B2C', 'Exports', 'Others') " +
				   /* "  AND (CASE WHEN RIGHT(DERIVED_RET_PERIOD, 2) < 4 " +
				    "  THEN CONCAT(LEFT(DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2)) " +
				    "  ELSE CONCAT(LEFT(DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "  AND CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) IN :retunPeriods " +
				    "  AND GSTIN IN :supplierGstins " +
				    "  UNION ALL " +
				    "  SELECT TRANSACTION_TYPE, REV_INVOICE_VALUE, REV_TAXABLE_VALUE, REV_IGST, REV_CGST, REV_SGST, REV_CESS " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', " +
				    "  'B2C', 'Exports', 'Others') " +
				   /* "  AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
				    "  THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "  ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "  AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) IN :retunPeriods " +
				    "  AND GSTIN IN :supplierGstins " +
				    "  UNION ALL " +
				    "  SELECT 'B2B' AS TRANSACTION_TYPE, ORG_INVOICE_VALUE, ORG_TAXABLE_VALUE, ORG_IGST, ORG_CGST, ORG_SGST, ORG_CESS " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('B2B(Stock transfer)', 'B2B(Other than stock transfer)') " +
				    /*"  AND (CASE WHEN RIGHT(DERIVED_RET_PERIOD, 2) < 4 " +
				    "  THEN CONCAT(LEFT(DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2)) " +
				    "  ELSE CONCAT(LEFT(DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "  AND CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) IN :retunPeriods " +
				    "  AND GSTIN IN :supplierGstins " +
				    "  UNION ALL " +
				    "  SELECT 'B2B' AS TRANSACTION_TYPE, REV_INVOICE_VALUE, REV_TAXABLE_VALUE, REV_IGST, REV_CGST, REV_SGST, REV_CESS " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('B2B(Stock transfer)', 'B2B(Other than stock transfer)') " +
				   /* "  AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
				    "  THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "  ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "  AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) IN :retunPeriods " +
				    "  AND GSTIN IN :supplierGstins " +
				    ") " +
				    "GROUP BY TRANSACTION_TYPE " +
				    "HAVING SUM(INVOICE_VALUE) >= 0";

			
	
			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ retunPeriods + ", supplierGstins " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getAllGrossOutwardSupp");
		}
	}

	@Override
	public List<Object[]> getMonthWiseTrendAnalysis(String fy, String valueFlag,
			List<String> supplierGstins, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = getMonthWiseTrendAnalysisQuery(fy, valueFlag,
					supplierGstins, returnPeriods);

			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("supplierGstins", supplierGstins);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", supplierGstins " + supplierGstins
						+ "valueFlag " + valueFlag + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getMonthWiseTrendAnalysis");
		}
	}

	private String getMonthWiseTrendAnalysisQuery(String fy, String valueFlag,
			List<String> supplierGstins, List<String> retunPeriods) {
		String queryString = "";
		if (valueFlag.equalsIgnoreCase("INVOICE_VALUE")) {
			
			queryString = "SELECT RETURN_PERIOD, " +
				    "       SUM(INVOICE_VALUE) AS INVOICE_VALUE " +
				    "FROM ( " +
				    "  SELECT CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) AS RETURN_PERIOD, " +
				    "         SUM(ORG_INVOICE_VALUE) AS INVOICE_VALUE " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', 'B2C', 'Exports', 'Others') " +
				   /* "    AND (CASE WHEN RIGHT(DERIVED_RET_PERIOD, 2) < 4 " +
				    "             THEN CONCAT(LEFT(DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2)) " +
				    "             ELSE CONCAT(LEFT(DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "    AND CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "  GROUP BY CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) " +
				    "  UNION ALL " +
				    "  SELECT CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) AS RETURN_PERIOD, " +
				    "         SUM(REV_INVOICE_VALUE) AS INVOICE_VALUE " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', 'B2C', 'Exports', 'Others') " +
				  /*  "    AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
				    "             THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "             ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "    AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "  GROUP BY CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) " +
				    ") " +
				    "GROUP BY RETURN_PERIOD";

			
			
		} else if (valueFlag.equalsIgnoreCase("TAXABLE_VALUE")) {
			
			queryString = "SELECT RETURN_PERIOD, " +
    "       SUM(TAXABLE_VALUE) AS TAXABLE_VALUE " +
    "FROM ( " +
    "  SELECT CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) AS RETURN_PERIOD, " +
    "         SUM(ORG_TAXABLE_VALUE) AS TAXABLE_VALUE " +
    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', 'B2C', 'Exports', 'Others') " +
   /* "    AND (CASE WHEN RIGHT(DERIVED_RET_PERIOD, 2) < 4 " +
    "             THEN CONCAT(LEFT(DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2)) " +
    "             ELSE CONCAT(LEFT(DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
    "    AND CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
    "    AND GSTIN IN :supplierGstins " +
    "  GROUP BY CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) " +
    "  UNION ALL " +
    "  SELECT CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) AS RETURN_PERIOD, " +
    "         SUM(REV_TAXABLE_VALUE) AS TAXABLE_VALUE " +
    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', 'B2C', 'Exports', 'Others') " +
   /* "    AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
    "             THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
    "             ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
    "    AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
    "    AND GSTIN IN :supplierGstins " +
    "  GROUP BY CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) " +
    ") " +
    "GROUP BY RETURN_PERIOD;";

			
		} else if (valueFlag.equalsIgnoreCase("TAX_AMOUNT")) {
			/*queryString = "SELECT RETURN_PERIOD, SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+ IFNULL( "
					+ " SGST_AMT,0)+IFNULL(CESS,0)) AS TOTAL_TAX FROM ( SELECT CONCAT(RIGHT( "
					+ " DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) AS RETURN_PERIOD,SUM(ORG_IGST) "
					+ " AS IGST_AMT,SUM(ORG_CGST) AS CGST_AMT,SUM(ORG_SGST) AS SGST_AMT,SUM(ORG_CESS) AS CESS "
					+ " FROM TBL_OUTWARD_DASH_TAX_LIABILITIES WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', "
					+ " 'B2B(Stock transfer)','B2B(Other than stock transfer)', 'B2C','Exports','Others') "
					+ " AND ( CASE WHEN RIGHT(DERIVED_RET_PERIOD,2) < 4 THEN CONCAT(LEFT(DERIVED_RET_PERIOD "
					+ " ,4)-1,'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2)) ELSE CONCAT(LEFT(DERIVED_RET_PERIOD "
					+ " ,4),'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2)+1) END) = :fy AND CONCAT(RIGHT( "
					+ " DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :returnPeriods AND GSTIN IN "
					+ " :supplierGstins GROUP BY CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) "
					+ " UNION ALL "
					+ " SELECT CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) "
					+ " AS RETURN_PERIOD, SUM(CASE WHEN CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT( "
					+ " DERIVED_RET_PERIOD,4)) NOT IN :returnPeriods THEN AMD_IGST ELSE REV_IGST END), "
					+ " SUM(CASE WHEN CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) "
					+ " NOT IN :returnPeriods THEN AMD_CGST ELSE REV_CGST END), SUM(CASE WHEN CONCAT( "
					+ " RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) NOT IN :returnPeriods "
					+ " THEN AMD_SGST ELSE REV_SGST END), SUM(CASE WHEN CONCAT(RIGHT(DERIVED_RET_PERIOD "
					+ " ,2),LEFT(DERIVED_RET_PERIOD,4)) NOT IN :returnPeriods THEN AMD_CESS ELSE REV_CESS "
					+ " END) FROM TBL_OUTWARD_DASH_TAX_LIABILITIES WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', "
					+ " 'B2B(Stock transfer)','B2B(Other than stock transfer)', 'B2C','Exports','Others') "
					+ " AND ( CASE WHEN RIGHT(DERIVED_RET_PERIOD,2) < 4 THEN CONCAT(LEFT(DERIVED_RET_PERIOD "
					+ " ,4)-1,'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2)) ELSE CONCAT(LEFT(DERIVED_RET_PERIOD "
					+ " ,4),'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2)+1) END) = :fy AND CONCAT(RIGHT( "
					+ " AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) IN :returnPeriods "
					+ " AND GSTIN IN :supplierGstins GROUP BY CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2), "
					+ " LEFT(AMD_DERIVED_RET_PERIOD,4))) GROUP BY RETURN_PERIOD";*/
			
			queryString = "SELECT RETURN_PERIOD, " +
				    "       SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + IFNULL(SGST_AMT, 0) + IFNULL(CESS, 0)) AS TOTAL_TAX " +
				    "FROM ( " +
				    "  SELECT CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) AS RETURN_PERIOD, " +
				    "         SUM(ORG_IGST) AS IGST_AMT, " +
				    "         SUM(ORG_CGST) AS CGST_AMT, " +
				    "         SUM(ORG_SGST) AS SGST_AMT, " +
				    "         SUM(ORG_CESS) AS CESS " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', 'B2C', 'Exports', 'Others') " +
				/*    "    AND (CASE WHEN RIGHT(DERIVED_RET_PERIOD, 2) < 4 " +
				    "             THEN CONCAT(LEFT(DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2)) " +
				    "             ELSE CONCAT(LEFT(DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "    AND CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "  GROUP BY CONCAT(RIGHT(DERIVED_RET_PERIOD, 2), LEFT(DERIVED_RET_PERIOD, 4)) " +
				    "  UNION ALL " +
				    "  SELECT CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) AS RETURN_PERIOD, " +
				    "         SUM(REV_IGST) AS IGST_AMT, " +
				    "         SUM(REV_CGST) AS CGST_AMT, " +
				    "         SUM(REV_SGST) AS SGST_AMT, " +
				    "         SUM(REV_CESS) AS CESS " +
				    "  FROM TBL_OUTWARD_DASH_TAX_LIABILITIES " +
				    "  WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES', 'B2B(Stock transfer)', 'B2B(Other than stock transfer)', 'B2C', 'Exports', 'Others') " +
				  /*  "    AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
				    "             THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "             ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "    AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "  GROUP BY CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) " +
				    ") " +
				    "GROUP BY RETURN_PERIOD";

		}
		return queryString;
	}

	@Override
	public List<Object[]> getTopCustomerB2BData(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			String queryString = "WITH CUST_DATA AS ( "
				    + "SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, "
				    + "SUM(IFNULL(ORG_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, "
				   // + "SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0) "
				   // + "+ (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(ORG_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE "
				    + " SUM(IFNULL(ORG_INVOICE_VALUE,0)) AS TOTAL_VALUE "
				    + "FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE "
				    + "WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 "
				    + "AND TRANSACTION_TYPE IN ('B2B', 'CDNR', 'B2BA', 'CDNRA') "
				//    + "AND FI_YEAR = :fy "
				    + "AND ORG_DERIVED_RET_PERIOD IN (:returnPeriods) "
				    + "AND GSTIN IN (:supplierGstins) "
				    + "GROUP BY CUST_GSTIN, ITM_HSNSAC "
				    + "UNION ALL "
				    + "SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, "
				    + "SUM(IFNULL(DIFF_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, "
				    //+ "SUM(IFNULL(DIFF_TAXABLE_VALUE, 0) + IFNULL(DIFF_IGST_AMT, 0) + IFNULL(DIFF_CGST_AMT, 0) + IFNULL(DIFF_SGST_AMT, 0) + IFNULL(DIFF_CESS, 0) "
				    //+ "+ (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(DIFF_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE "
				    + " SUM(IFNULL(DIFF_INVOICE_VALUE,0)) AS TOTAL_VALUE "
				    + "FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE "
				    + "WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 "
				    + "AND TRANSACTION_TYPE IN ('B2B', 'CDNR', 'B2BA', 'CDNRA') "
				   /* + "AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) -1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) "
				    + "ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy "*/
				    + "AND AMD_DERIVED_RET_PERIOD IN (:returnPeriods) "
				    + "AND GSTIN IN (:supplierGstins) "
				    + "GROUP BY CUST_GSTIN, ITM_HSNSAC, AMD_DERIVED_RET_PERIOD, AMD_TAXABLE_VALUE, DIFF_TAXABLE_VALUE "
				    + "), "
				    + "TOP_CUST AS ( "
				    + "SELECT TOP 10 CUST_GSTIN, SUM(TOTAL_VALUE) AS TOTAL_VALUE "
				    + "FROM CUST_DATA "
				    + "GROUP BY CUST_GSTIN "
				    + "ORDER BY 2 DESC "
				    + ") "
				    + "SELECT CUST_GSTIN, TOTAL_VALUE, NULL AS HSNSAC, TOTAL_VALUE AS GSTIN_TOTAL, "
				    + "NULL AS RN, NULL AS TAXABLE_VALUE, NULL AS TOTAL_TAXABLE "
				    + "FROM TOP_CUST ORDER BY 2 DESC ";

			
			/*String queryString = "SELECT CUST_GSTIN, TOTAL_VLUE AS TOTAL_VALUE, HSNSAC, TOTAL_VLUE as GSTIN_TOTAL, RN, TAXABLE_VALUE AS TAXABLE_VALUE, TAXABLE_VALUE as TOTAL_TAXABLE " +
				    " FROM ( " +
				    "    SELECT CUST_GSTIN, sum(TOTAL_VALUE) AS TOTAL_VLUE, HSNSAC, sum(TAXABLE_VALUE) AS TAXABLE_VALUE, " +
				    "    DENSE_RANK() OVER (ORDER BY CUST_GSTIN) RN " +
				    "    FROM ( " +
				    "        SELECT CUST_GSTIN, TOTAL_VALUE, HSNSAC, TAXABLE_VALUE " +
				    "        FROM ( " +
				    "            SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, SUM(IFNULL(ORG_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, " +
				    "            SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0) + " +
				    "            (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(ORG_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE " +
				    "            FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "            WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 " +
				    "            AND TRANSACTION_TYPE IN ('B2B', 'CDNR','B2BA','CDNRA') " +
				    "            AND FI_YEAR = :fy " +
				    "            AND ORG_DERIVED_RET_PERIOD IN (:returnPeriods) " +
				    "            AND GSTIN IN (:supplierGstins) " +
				    "            GROUP BY CUST_GSTIN, ITM_HSNSAC " +
				    "            UNION ALL " +
				    "            SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, SUM(IFNULL(DIFF_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, " +
				    "            SUM(IFNULL(DIFF_TAXABLE_VALUE, 0) + IFNULL(DIFF_IGST_AMT, 0) + IFNULL(DIFF_CGST_AMT, 0) + IFNULL(DIFF_SGST_AMT, 0) + IFNULL(DIFF_CESS, 0) + " +
				    "            (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(DIFF_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE " +
				    "            FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "            WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 " +
				    "            AND TRANSACTION_TYPE IN ('B2B', 'CDNR','B2BA','CDNRA') " +
				    "            AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "                 ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +
				    "            AND AMD_DERIVED_RET_PERIOD IN (:returnPeriods) " +
				    "            AND GSTIN IN (:supplierGstins) " +
				    "            GROUP BY CUST_GSTIN, ITM_HSNSAC, AMD_DERIVED_RET_PERIOD, AMD_TAXABLE_VALUE, DIFF_TAXABLE_VALUE " +
				    "        ) " +
				    "        WHERE TOTAL_VALUE > 0 " +
				    "    ) " +
				    "    GROUP BY CUST_GSTIN, HSNSAC " +
				    ") " +
				    "WHERE RN <= 10";*/


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ retunPeriods + ", supplierGstins " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getTopCustomerB2BData");
		}
	}

	@Override
	public List<Object[]> getMajorTaxPayingProducts(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			
			
			String queryString =  "SELECT TOP 10 ITM_HSNSAC, "
				    + " SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + "
				    + "IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0)) AS TOTAL_VALUE "
				    + " FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE "
				    + " WHERE ITM_HSNSAC IS NOT NULL"
				    + " AND TRANSACTION_TYPE NOT IN ('AT','TXP') "
				  //  + "   AND FI_YEAR = :fy "
				    + "   AND ORG_DERIVED_RET_PERIOD IN :retunPeriods "
				    + "   AND GSTIN IN :supplierGstins "
				    + " GROUP BY ITM_HSNSAC "
				    + " HAVING SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + "
				    + "IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0)) >= 0 "
				    + " ORDER BY 2 DESC ";



			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ retunPeriods + ", supplierGstins " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getMajorTaxPayingProdtucts");
		}
	}

	@Override
	public List<Object[]> getTaxRateWiseDistribution(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {

			
		String	queryString = "SELECT IFNULL(TAX_RATE, 0) AS TAX_RATE, " +
				    "       ABS(SUM(TOTAL_VALUE)) AS TOTAL_VALUE " +
				    "FROM ( " +
				    "  SELECT IFNULL(TAX_RATE, 0) AS TAX_RATE, " +
				    "         SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0)) AS TOTAL_VALUE " +
				    "  FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "  WHERE " +
				  //  + "FI_YEAR = :fy AND " + 
				    "     ORG_DERIVED_RET_PERIOD IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "    AND TRANSACTION_TYPE NOT IN ('B2CCDNUR', 'B2CCDNUR-B2CL', 'B2CCDNUR-EXPORTS', 'Others', 'NILEXTNON', 'NON_UI_ITM_HSNSACNA', 'UI_ITM_HSNSACNA') " +
				    "  GROUP BY IFNULL(TAX_RATE, 0) " +
				    "  UNION ALL " +
				    "  SELECT 0 AS TAX_RATE, " +
				    "         SUM(IFNULL(ORG_INVOICE_VALUE, 0)) AS TOTAL_VALUE " +
				    "  FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "  WHERE UPPER(TRANSACTION_TYPE) = 'OTHERS' " +
				 //   "    AND FI_YEAR = :fy " +
				    "    AND ORG_DERIVED_RET_PERIOD IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "  UNION ALL " +
				    "  SELECT IFNULL(TAX_RATE, 0) AS TAX_RATE, " +
				    "         SUM(IFNULL(DIFF_TAXABLE_VALUE, 0) + IFNULL(DIFF_IGST_AMT, 0) + IFNULL(DIFF_CGST_AMT, 0) + IFNULL(DIFF_SGST_AMT, 0) + IFNULL(DIFF_CESS, 0)) AS TOTAL_VALUE " +
				    "  FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "  WHERE " +
				  /*  + "(CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
				    "              THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "              ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy AND " +*/
				    "     AMD_DERIVED_RET_PERIOD IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "    AND TRANSACTION_TYPE NOT IN ('B2CCDNUR', 'B2CCDNUR-B2CL', 'B2CCDNUR-EXPORTS', 'Others', 'NILEXTNON', 'NON_UI_ITM_HSNSACNA', 'UI_ITM_HSNSACNA') " +
				    "  GROUP BY IFNULL(TAX_RATE, 0), AMD_DERIVED_RET_PERIOD " +
				    "  UNION ALL " +
				    "  SELECT 0 AS TAX_RATE, " +
				    "         SUM(IFNULL(DIFF_INVOICE_VALUE, 0)) AS TOTAL_VALUE " +
				    "  FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "  WHERE UPPER(TRANSACTION_TYPE) = 'OTHERS' " +
				  /*  "    AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
				    "              THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "              ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +*/
				    "    AND AMD_DERIVED_RET_PERIOD IN :returnPeriods " +
				    "    AND GSTIN IN :supplierGstins " +
				    "  GROUP BY AMD_DERIVED_RET_PERIOD " +
				    ") " +
				    "GROUP BY IFNULL(TAX_RATE, 0) " +
				    "ORDER BY 2 DESC";


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ retunPeriods + ", supplierGstins " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getTaxRateWiseDistribution");
		}
	}

	@Override
	public List<Object[]> getTotalLiabilityDetails(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			String queryString = "SELECT SUM(IFNULL(TOTAL_LIABILITY,0)"
					+ "-IFNULL(NET_ITC_AVAILABLE,0)) AS NET_LIABILITY,"
					+ " SUM(IFNULL(NET_ITC_AVAILABLE,0)) AS NET_ITC_AVAILABLE,"
					+ " SUM(IFNULL(TOTAL_LIABILITY,0)) AS TOTAL_LIABILITY, "
					+ " TO_VARCHAR(MAX(REFRESHED_ON)) FROM TBL_OUTWARD_DASH_NET_ITC WHERE "
					//+ " FI_YEAR = :fy AND "
					+ " GSTIN IN :supplierGstins AND "
					+ " DERIVED_RET_PERIOD IN :retunPeriods";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ retunPeriods + ", supplierGstins " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getTotalLiabilityDetails");
		}
	}

	@Override
	public List<Object[]> getTaxLiabilityDetails(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			
			String queryString = "SELECT TRANSACTION_TYPE,"
					+ " SUM(INVOICE_VALUE) AS INVOICE_VALUE," 
					+ " SUM(TAXABLE_VALUE) As TAXABLE_VALUE," 
					+ " SUM(IGST_AMT) AS IGST_AMT," 
					+ " SUM(CGST_AMT) AS CGST_AMT," 
					+ " SUM(SGST_AMT) AS SGST_AMT," 
					+ " SUM(CESS) AS CESS_AMT"
					+ " FROM(" 
					+ " SELECT TRANSACTION_TYPE,ORG_INVOICE_VALUE AS INVOICE_VALUE,ORG_TAXABLE_VALUE AS TAXABLE_VALUE,ORG_IGST AS IGST_AMT,ORG_CGST AS"
					+ " CGST_AMT,ORG_SGST AS SGST_AMT,ORG_CESS AS CESS"
					+ " FROM TBL_OUTWARD_DASH_TAX_LIABILITIES" 
					+ " WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)', 'B2C','Exports','Others')" 
					/*+ " AND ( CASE WHEN RIGHT(DERIVED_RET_PERIOD,2) < 4"
					   + " THEN CONCAT(LEFT(DERIVED_RET_PERIOD,4)-1,'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2))" 
					   + " ELSE CONCAT(LEFT(DERIVED_RET_PERIOD,4),'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2)+1)" 
					  + " END) = :fy" */
					+ " AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods"
					+ " AND GSTIN IN :supplierGstins"
					+ " UNION ALL"
					+ " SELECT TRANSACTION_TYPE, REV_INVOICE_VALUE,"
					+ " REV_TAXABLE_VALUE ,"
					+ "  REV_IGST ,"
					+ "  REV_CGST ,"
					+ " REV_SGST ,"
					+ "  REV_CESS "
					+ " FROM TBL_OUTWARD_DASH_TAX_LIABILITIES" 
					+ " WHERE TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)', 'B2C',"
					+ " 'Exports','Others')" 
					/*+ " AND ( CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD,2) < 4 "
					+ "THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD,4)-1,'-'||RIGHT(LEFT(AMD_DERIVED_RET_PERIOD,4),2)) "
					+ "ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD,4),'-'||RIGHT(LEFT(AMD_DERIVED_RET_PERIOD,4),2)+1) END) = :fy" */
					+ " AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) IN :retunPeriods"
					+ " AND GSTIN IN :supplierGstins"
					+ " UNION ALL"
					+ " SELECT 'B2B' AS TRANSACTION_TYPE,ORG_INVOICE_VALUE,ORG_TAXABLE_VALUE,ORG_IGST,ORG_CGST,ORG_SGST,ORG_CESS"
					+ " FROM TBL_OUTWARD_DASH_TAX_LIABILITIES" 
					+ " WHERE TRIM(TRANSACTION_TYPE) IN ('B2B(Stock transfer)','B2B(Other than stock transfer)' )"
					/*+ " AND ( CASE WHEN RIGHT(DERIVED_RET_PERIOD,2) < 4"
					   + " THEN CONCAT(LEFT(DERIVED_RET_PERIOD,4)-1,'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2))" 
					   + " ELSE CONCAT(LEFT(DERIVED_RET_PERIOD,4),'-'||RIGHT(LEFT(DERIVED_RET_PERIOD,4),2)+1)" 
					  + " END) = :fy" */
					+ " AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods"
					+ " AND GSTIN IN :supplierGstins"
					+ " UNION ALL"
					+ " SELECT 'B2B' AS TRANSACTION_TYPE,"
					+ "  REV_INVOICE_VALUE ,"
					+ "  REV_TAXABLE_VALUE ,"
					+ "  REV_IGST ,"
					+ "  REV_CGST ,"
					+ "  REV_SGST ,"
					+ "  REV_CESS "
					+ " FROM TBL_OUTWARD_DASH_TAX_LIABILITIES"
					+ " WHERE TRIM(TRANSACTION_TYPE) IN ('B2B(Stock transfer)','B2B(Other than stock transfer)' )"
					/*+ " AND ( CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD,2) < 4 "
					+ "THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD,4)-1,'-'||RIGHT(LEFT(AMD_DERIVED_RET_PERIOD,4),2)) "
					+ "ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD,4),'-'||RIGHT(LEFT(AMD_DERIVED_RET_PERIOD,4),2)+1) END) = :fy" */
					+ " AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) IN :retunPeriods"
					+ " AND GSTIN IN :supplierGstins"
					  + " )"
					+ " GROUP BY TRANSACTION_TYPE";
	

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ retunPeriods + ", supplierGstins " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getTaxLiabilityDetails");
		}
	}

	public List<Object[]> getTopb2bAfterToggle(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {

		try {
			
			String query = "WITH CUST_DATA AS ( "
				    + "SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, SUM(IFNULL(ORG_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, "
				    //+ "SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0) "
				   // + "+ (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(ORG_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE "
				    + " SUM(IFNULL(ORG_INVOICE_VALUE,0)) AS TOTAL_VALUE "
				    + "FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE "
				    + "WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 "
				    + "AND TRANSACTION_TYPE IN ('B2B', 'CDNR','B2BA','CDNRA') "
				  //  + "AND FI_YEAR = :fy "
				    + "AND ORG_DERIVED_RET_PERIOD IN (:returnPeriods) "
				    + "AND GSTIN IN (:supplierGstins) "
				    + "GROUP BY CUST_GSTIN, ITM_HSNSAC "
				    + "UNION ALL "
				    + "SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, SUM(IFNULL(DIFF_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, "
				   // + "SUM(IFNULL(DIFF_TAXABLE_VALUE, 0) + IFNULL(DIFF_IGST_AMT, 0) + IFNULL(DIFF_CGST_AMT, 0) + IFNULL(DIFF_SGST_AMT, 0) + IFNULL(DIFF_CESS, 0) "
				   // + "+ (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(DIFF_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE "
				   	+ " SUM(IFNULL(DIFF_INVOICE_VALUE,0)) AS TOTAL_VALUE "
				    + "FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE "
				    + "WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 "
				    + "AND TRANSACTION_TYPE IN ('B2B', 'CDNR','B2BA','CDNRA') "
				  /*  + "AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) "
				    + "ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy "*/
				    + "AND AMD_DERIVED_RET_PERIOD IN (:returnPeriods) "
				    + "AND GSTIN IN (:supplierGstins) "
				    + "GROUP BY CUST_GSTIN, ITM_HSNSAC, AMD_DERIVED_RET_PERIOD, AMD_TAXABLE_VALUE, DIFF_TAXABLE_VALUE "
				    + "), "
				    + "TOP_CUST AS ( "
				    + "SELECT TOP 10 CUST_GSTIN, SUM(TOTAL_VALUE) AS TOTAL_VALUE, "
				    + "DENSE_RANK() OVER (ORDER BY SUM(TOTAL_VALUE) DESC) RNK "
				    + "FROM CUST_DATA "
				    + "GROUP BY CUST_GSTIN "
				    + "ORDER BY 2 DESC "
				    + ") "
				    + "SELECT CD.CUST_GSTIN, SUM(CD.TOTAL_VALUE) AS TOTAL_VALUE, CD.HSNSAC, "
				    + "SUM(CD.TOTAL_VALUE) AS GSTIN_TOTAL, TC.RNK AS RN, SUM(CD.TAXABLE_VALUE) AS TAXABLE_VALUE, "
				    + "SUM(CD.TAXABLE_VALUE) AS TOTAL_TAXABLE "
				    + "FROM CUST_DATA CD "
				    + "INNER JOIN TOP_CUST TC ON TC.CUST_GSTIN = CD.CUST_GSTIN "
				    + "GROUP BY CD.CUST_GSTIN, CD.HSNSAC, TC.RNK "
				    + "ORDER BY TC.RNK";

			/*String query = "SELECT CUST_GSTIN, TOTAL_VLUE AS TOTAL_VALUE, HSNSAC, TOTAL_VLUE as GSTIN_TOTAL, RN, TAXABLE_VALUE AS TAXABLE_VALUE, TAXABLE_VALUE as TOTAL_TAXABLE " +
				    "FROM ( " +
				    "    SELECT CUST_GSTIN, sum(TOTAL_VALUE) AS TOTAL_VLUE, HSNSAC, sum(TAXABLE_VALUE) AS TAXABLE_VALUE, " +
				    "    DENSE_RANK() OVER (ORDER BY CUST_GSTIN) RN " +
				    "    FROM ( " +
				    "        SELECT CUST_GSTIN, TOTAL_VALUE, HSNSAC, TAXABLE_VALUE " +
				    "        FROM ( " +
				    "            SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, SUM(IFNULL(ORG_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, " +
				    "            SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0) + " +
				    "            (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(ORG_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE " +
				    "            FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "            WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 " +
				    "            AND TRANSACTION_TYPE IN ('B2B', 'CDNR','B2BA','CDNRA') " +
				    "            AND FI_YEAR = :fy " +
				    "            AND ORG_DERIVED_RET_PERIOD IN (:returnPeriods) " +
				    "            AND GSTIN IN (:supplierGstins) " +
				    "            GROUP BY CUST_GSTIN, ITM_HSNSAC " +
				    "            UNION ALL " +
				    "            SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, SUM(IFNULL(DIFF_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, " +
				    "            SUM(IFNULL(DIFF_TAXABLE_VALUE, 0) + IFNULL(DIFF_IGST_AMT, 0) + IFNULL(DIFF_CGST_AMT, 0) + IFNULL(DIFF_SGST_AMT, 0) + IFNULL(DIFF_CESS, 0) + " +
				    "            (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(DIFF_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE " +
				    "            FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
				    "            WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 " +
				    "            AND TRANSACTION_TYPE IN ('B2B', 'CDNR','B2BA','CDNRA') " +
				    "            AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
				    "                 ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +
				    "            AND AMD_DERIVED_RET_PERIOD IN (:returnPeriods) " +
				    "            AND GSTIN IN (:supplierGstins) " +
				    "            GROUP BY CUST_GSTIN, ITM_HSNSAC, AMD_DERIVED_RET_PERIOD, AMD_TAXABLE_VALUE, DIFF_TAXABLE_VALUE " +
				    "        ) " +
				    "        WHERE TOTAL_VALUE > 0 " +
				    "    ) " +
				    "    GROUP BY CUST_GSTIN, HSNSAC " +
				    ") " +
				    "WHERE RN <= 10";*/



			Query q = entityManager.createNativeQuery(query);
			//q.setParameter("fy", fy);
			q.setParameter("returnPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getTaxLiabilityDetails");
		}

	}

	public List<Object[]> getMajorTaxpayData(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {

			String queryString = "WITH CTE AS "
				    + "(SELECT ITM_HSNSAC, "
				    + "        TAX_RATE, "
				    + "        TOTAL_VALUE, "
				    + "        HSN_TOTALTAX, "
				    + "        DENSE_RANK()OVER(ORDER BY HSN_TOTALTAX DESC) AS DRN, RN "
				    + " FROM ( SELECT ITM_HSNSAC, "
				    + "               TAX_RATE, "
				    + "               TOTAL_VALUE, "
				    + "               SUM(TOTAL_VALUE)OVER(PARTITION BY ITM_HSNSAC) AS HSN_TOTALTAX, "
				    + "               ROW_NUMBER()OVER(PARTITION BY ITM_HSNSAC) RN "
				    + "        FROM (SELECT ITM_HSNSAC, "
				    + "                     IFNULL(TAX_RATE, 0) AS TAX_RATE, "
				    + "                     SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0)"
				    + " + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0)) AS TOTAL_VALUE "
				    + "              FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE "
				    + "              WHERE ITM_HSNSAC IS NOT NULL "
				    + " AND TRANSACTION_TYPE NOT IN ('AT','TXP') "
				  //  + "              AND FI_YEAR = :fy "
				    + "              AND ORG_DERIVED_RET_PERIOD IN :retunPeriods "
				    + "              AND GSTIN IN :supplierGstins "
				    + "              GROUP BY ITM_HSNSAC, IFNULL(TAX_RATE, 0) "
				    + "              HAVING SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + "
				    + "IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0)) >= 0 "
				    + "             ) "
				    + "      ) "
				    + " )SELECT ITM_HSNSAC, TAX_RATE, TOTAL_VALUE, HSN_TOTALTAX, DRN, RN FROM CTE WHERE "
				    + "DRN <= 10 ORDER BY DRN ";


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1OutwardFioriDashboardDaoImpl.getTaxLiabilityDetails");
		}

	}
	
	public static void main(String[] args) {
		String queryString = "SELECT CUST_GSTIN, TOTAL_VALUE, HSNSAC, GSTIN_TOTAL, RN, TAXABLE_VALUE, TOTAL_TAXABLE " +
			    "FROM ( " +
			    "  SELECT CUST_GSTIN, TOTAL_VALUE, HSNSAC, GSTIN_TOTAL, TAXABLE_VALUE, TOTAL_TAXABLE, " +
			    "         DENSE_RANK() OVER(ORDER BY GSTIN_TOTAL DESC, CUST_GSTIN) RN " +
			    "  FROM ( " +
			    "    SELECT CUST_GSTIN, TOTAL_VALUE, HSNSAC, GSTIN_TOTAL, TAXABLE_VALUE, " +
			    "           SUM(TAXABLE_VALUE) OVER(PARTITION BY CUST_GSTIN) AS TOTAL_TAXABLE " +
			    "    FROM ( " +
			    "      SELECT CUST_GSTIN, TOTAL_VALUE, HSNSAC, " +
			    "             SUM(TOTAL_VALUE) OVER(PARTITION BY CUST_GSTIN) AS GSTIN_TOTAL, TAXABLE_VALUE " +
			    "      FROM ( " +
			    "        SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, " +
			    "               SUM(IFNULL(ORG_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, " +
			    "               SUM(IFNULL(ORG_TAXABLE_VALUE, 0) + IFNULL(ORG_IGST_AMT, 0) + IFNULL(ORG_CGST_AMT, 0) + IFNULL(ORG_SGST_AMT, 0) + IFNULL(ORG_CESS, 0) + " +
			    "                   (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(ORG_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE " +
			    "        FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
			    "        WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 " +
			    "          AND TRANSACTION_TYPE IN ('B2B', 'CDNR') " +
			    "          AND FI_YEAR = :fy " +
			    "          AND ORG_DERIVED_RET_PERIOD IN :returnPeriods " +
			    "          AND GSTIN IN :supplierGstins " +
			    "        GROUP BY CUST_GSTIN, ITM_HSNSAC " +
			    "        UNION ALL " +
			    "        SELECT CUST_GSTIN, ITM_HSNSAC AS HSNSAC, " +
			    "               SUM(IFNULL(DIFF_TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, " +
			    "               SUM(IFNULL(DIFF_TAXABLE_VALUE, 0) + IFNULL(DIFF_IGST_AMT, 0) + IFNULL(DIFF_CGST_AMT, 0) + IFNULL(DIFF_SGST_AMT, 0) + IFNULL(DIFF_CESS, 0) + " +
			    "                   (CASE WHEN UPPER(TRANSACTION_TYPE) = 'OTHERS' THEN IFNULL(DIFF_INVOICE_VALUE, 0) ELSE 0 END)) AS TOTAL_VALUE " +
			    "        FROM TBL_OUTWARD_DASH_HSNSAC_TAXRATE " +
			    "        WHERE CUST_GSTIN IS NOT NULL AND LENGTH(CUST_GSTIN) > 3 " +
			    "          AND TRANSACTION_TYPE IN ('B2B', 'CDNR') " +
			    "          AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " +
			    "                   THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " +
			    "                   ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " +
			    "          AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD, 2), LEFT(AMD_DERIVED_RET_PERIOD, 4)) IN :returnPeriods " +
			    "          AND GSTIN IN :supplierGstins " +
			    "        GROUP BY CUST_GSTIN, ITM_HSNSAC, AMD_DERIVED_RET_PERIOD, AMD_TAXABLE_VALUE, DIFF_TAXABLE_VALUE " +
			    "      ) " +
			    "    ) " +
			    "    WHERE TOTAL_VALUE > 0 " +
			    "  ) " +
			    ") " +
			    "WHERE RN <= 10";
		
		System.out.println("query is " +queryString);
	}
	
	

}
