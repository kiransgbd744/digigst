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
@Component("Gstr1Outward2FioriDashboardDaoImpl")
public class Gstr1Outward2FioriDashboardDaoImpl
		implements Gstr1Outward2FioriDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getAllHeadersData(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			supplierGstins.removeIf("All"::equals);
			retunPeriods.removeIf("All"::equals);
			
					//String queryString = 
							String queryString = "" +
									"WITH B2B AS ( " +
									"    SELECT COUNT(DISTINCT CUST_GSTIN) AS B2B_CNT " +
									"    FROM ( " +
									"        SELECT DISTINCT CUST_GSTIN " +
									"        FROM TBL_DASHBOARD_HSN_RATE_ORG " +
									"        WHERE CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " +
									"        AND SUPPLIER_GSTIN IN :supplierGstins " +
									"        AND CUST_GSTIN IS NOT NULL AND CUST_GSTIN <> 'URP' " +
									"        AND TAX_DOC_TYPE IN ('B2B','B2BA','CDNR','CDNRA') " +
									"        UNION ALL " +
									"        SELECT DISTINCT CUST_GSTIN " +
									"        FROM TBL_DASHBOARD_HSN_RATE_AMD " +
									"        WHERE CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " +
									"        AND SUPPLIER_GSTIN IN :supplierGstins " +
									"        AND CUST_GSTIN IS NOT NULL AND CUST_GSTIN <> 'URP' " +
									"        AND TAX_DOC_TYPE IN ('B2B','B2BA','CDNR','CDNRA') " +
									"    ) " +
									") " +
									"SELECT " +
									"    ABS(SUM(INV_CNT)), " +
									"    ABS(SUM(CDN_CNT)), " +
									"    ABS(SUM(DBT_CNT)), " +
									"    ABS(SUM(SLF_CNT)), " +
									"    ABS(SUM(DLC_CNT)), " +
									"    (SELECT ABS(SUM(B2B_CNT)) FROM B2B) AS B2B_CNT, " +
									"    ABS(SUM(TOT_TURNOVER)), " +
									"    ABS(SUM(TOT_TAX)), " +
									"    MAX(REFRESHED_ON) AS REFRESHED_ON " +
									"FROM ( " +
									"    (SELECT " +
									"        ABS(SUM(IFNULL(C.INV_CNT,0))) AS INV_CNT, " +
									"        ABS(SUM(IFNULL(C.CDN_CNT,0))) AS CDN_CNT, " +
									"        ABS(SUM(IFNULL(C.DBT_CNT,0))) AS DBT_CNT, " +
									"        ABS(SUM(IFNULL(C.DLC_CNT,0))) AS DLC_CNT, " +
									"        ABS(SUM(IFNULL(C.SLF_CNT,0))) AS SLF_CNT, " +
									"        GSTIN, " +
									"        RETURN_PERIOD " +
									"    FROM TBL_GSTR1_OUTWARD_DASHBOARD_CNT_PG2 C " +
									"    WHERE " +
									"        C.RETURN_PERIOD IN :retunPeriods " +
									"        AND C.GSTIN IN :supplierGstins " +
									"    GROUP BY GSTIN,RETURN_PERIOD " +
									"    ) C " +
									"    INNER JOIN " +
									"    (SELECT " +
									"        ABS(SUM(IFNULL(ORG_TAXABLE_VALUE,0))) AS TOT_TURNOVER, " +
									"        ABS(SUM(IFNULL(ORG_TOTAL_TAX,0))) AS TOT_TAX, " +
									"        MAX(REFRESHED_ON) AS REFRESHED_ON, " +
									"        GSTIN, RETURN_PERIOD " +
									"    FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " +
									"    GROUP BY GSTIN, RETURN_PERIOD " +
									"    ) S " +
									"    ON C.GSTIN = S.GSTIN " +
									"    AND C.RETURN_PERIOD = S.RETURN_PERIOD " +
									") ";


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to get Header data "
								+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
								+ " ,retunPeriods :%s and supplierGstins :%s",
						fy, retunPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to get Header data "
							+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
							+ " ,retunPeriods :%s and supplierGstins :%s",
					fy, retunPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getPsdVsErrData(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			//getPsdVsErrData--api
			/*//as part of --98221 commenting out old query--20/2/2024
			String queryString = "SELECT ABS(SUM(IFNULL(PSD_CNT,0))) AS PSD_CNT"
					+ " , ABS(SUM(IFNULL(ERR_CNT,0))) AS ERR_CNT"
					+ " FROM TBL_SAC_GSTR1_OUTWARD_SUPPLIES WHERE "
					+ " FI_YEAR = :fy AND RETURN_PERIOD IN :retunPeriods"
					+ " AND GSTIN IN :supplierGstins";*/
			
			supplierGstins.removeIf("All"::equals);
			retunPeriods.removeIf("All"::equals);
			
			String queryString = "SELECT "
				    + "ABS(SUM(IFNULL(PSD_CNT,0))) AS PSD_CNT, "
				    + "ABS(SUM(IFNULL(ERR_CNT,0))) AS ERR_CNT "
				    + "FROM TBL_GSTR1_OUTWARD_DASHBOARD_CNT_PG2 "
				    + "WHERE "
				   // + "FI_YEAR = :fy AND "
				    + " CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods "
				    + "AND GSTIN IN :supplierGstins";


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getPsdVsErrData "
								+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
								+ " ,retunPeriods :%s and supplierGstins :%s",
						fy, retunPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getPsdVsErrData "
							+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
							+ " ,retunPeriods :%s and supplierGstins :%s",
					fy, retunPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getRevenueComparativeAnalysis(String fy,
			String valueFlag, List<String> supplierGstins,
			List<String> retunPeriods) throws AppException {
		try {
			supplierGstins.removeIf("All"::equals);
			retunPeriods.removeIf("All"::equals);
			String queryString = getRevenueComparativeAnalysisQuery(valueFlag);

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getRevenueComparativeAnalysis "
								+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
								+ " ,retunPeriods :%s and supplierGstins :%s",
						fy, retunPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getRevenueComparativeAnalysis"
							+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
							+ " ,retunPeriods :%s and supplierGstins :%s",
					fy, retunPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}

	}

	private String getRevenueComparativeAnalysisQuery(String valueFlag) {
		String queryString = "";
		
		//getCompAnalysisData--api
		
		if (valueFlag.equalsIgnoreCase("INVOICE_VALUE")) {
		    queryString = "SELECT GSTIN, (SUM(INVOICE_VALUE)) AS INVOICE_VALUE " 
		      + "FROM ( " 
		      + "      SELECT GSTIN, TRANSACTION_TYPE, (SUM(ORG_INVOICE_VALUE)) AS INVOICE_VALUE, " 
		      + "             (SUM(ORG_TAXABLE_VALUE)) AS TAXABLE_VALUE, (SUM(ORG_IGST_AMT)) AS IGST_AMT, " 
		      + "             (SUM(ORG_CGST_AMT)) AS CGST_AMT, (SUM(ORG_SGST_AMT)) AS SGST_AMT, " 
		      + "             (SUM (ORG_CESS)) AS CESS_AMT FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " 
		      + "      WHERE TRANSACTION_TYPE IS NOT NULL AND " 
		      + "      TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
		      + "      AND LENGTH(TRANSACTION_TYPE) > 2 " 
		    //  + "      AND FI_YEAR = :fy"
		      + " AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + "      AND GSTIN IN :supplierGstins " 
		      + "      GROUP BY GSTIN, TRANSACTION_TYPE " 
		      
		      + "      UNION ALL " 
		      
		      + "      SELECT GSTIN, TRANSACTION_TYPE, (SUM(DIFF_INVOICE_VALUE)) AS INVOICE_VALUE, " 
		      + "             (SUM(DIFF_TAXABLE_VALUE)) AS TAXABLE_VALUE, (SUM(DIFF_IGST_AMT)) AS IGST_AMT, " 
		      + "             (SUM(DIFF_CGST_AMT)) AS CGST_AMT, (SUM(DIFF_SGST_AMT)) AS SGST_AMT, " 
		      + "             (SUM (DIFF_CESS)) AS CESS_AMT FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " 
		      + "      WHERE TRANSACTION_TYPE IS NOT NULL AND " 
		      + "      TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
		      + "      AND LENGTH(TRANSACTION_TYPE) > 2 " 
		    /*  + "      AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " 
		      + "            THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " 
		      + "            ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " */
		      + "      AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + "      AND GSTIN IN :supplierGstins " 
		      + "      GROUP BY GSTIN, TRANSACTION_TYPE " 
		      + "	   ) GROUP BY GSTIN "; 

		} else if(valueFlag.equalsIgnoreCase("TAXABLE_VALUE")) {
		    queryString = " SELECT GSTIN, (SUM(TAXABLE_VALUE)) AS TAXABLE_VALUE " 
		      + " FROM ( " 
		      + "		SELECT GSTIN, TRANSACTION_TYPE, (SUM(ORG_INVOICE_VALUE)) AS INVOICE_VALUE, " 
		      + "		(SUM(ORG_TAXABLE_VALUE)) AS TAXABLE_VALUE, (SUM(ORG_IGST_AMT)) AS IGST_AMT, " 
		      + "		(SUM(ORG_CGST_AMT)) AS CGST_AMT, (SUM(ORG_SGST_AMT)) AS SGST_AMT, " 
		      + "		(SUM(ORG_CESS)) AS CESS_AMT FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " 
		      + "		WHERE TRANSACTION_TYPE IS NOT NULL AND " 
		      + "		TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
		      + "		AND LENGTH(TRANSACTION_TYPE) > 2 " 
		     // + "		AND FI_YEAR = :fy "
		      + " AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + "		AND GSTIN IN :supplierGstins GROUP BY GSTIN, TRANSACTION_TYPE " 

		      + "       UNION ALL " 

		      + "       SELECT GSTIN, TRANSACTION_TYPE, (SUM(DIFF_INVOICE_VALUE)) AS INVOICE_VALUE, " 
		      + "		(SUM(DIFF_TAXABLE_VALUE)) AS TAXABLE_VALUE, (SUM(DIFF_IGST_AMT)) AS IGST_AMT, " 
		      + "		(SUM(DIFF_CGST_AMT)) AS CGST_AMT, (SUM(DIFF_SGST_AMT)) AS SGST_AMT, " 
		      + "		(SUM(DIFF_CESS)) AS CESS_AMT FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " 
		      + "		WHERE TRANSACTION_TYPE IS NOT NULL AND " 
		      + "		TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
		      + "		AND LENGTH(TRANSACTION_TYPE) > 2 " 
		     /* + "		AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " 
		      + "            THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " 
		      + "            ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END) = :fy " */
		      + "		AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + "		AND GSTIN IN :supplierGstins GROUP BY GSTIN, TRANSACTION_TYPE " 
		      + " ) GROUP BY GSTIN "; 

		} else if (valueFlag.equalsIgnoreCase("TAX_AMOUNT")) {
		    queryString = " SELECT GSTIN, (SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0))) AS TOTAL_TAX " 
		      + " FROM ( " 
		      + " SELECT GSTIN, TRANSACTION_TYPE, (SUM(ORG_INVOICE_VALUE)) AS INVOICE_VALUE, " 
		      + " (SUM(ORG_TAXABLE_VALUE)) AS TAXABLE_VALUE, (SUM(ORG_IGST_AMT)) AS IGST_AMT, " 
		      + " (SUM(ORG_CGST_AMT)) AS CGST_AMT, (SUM(ORG_SGST_AMT)) AS SGST_AMT, " 
		      + " (SUM(ORG_CESS)) AS CESS_AMT FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " 
		      + " WHERE TRANSACTION_TYPE IS NOT NULL AND " 
		      + " TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
		      + " AND LENGTH(TRANSACTION_TYPE) > 2 " 
		     // + " AND FI_YEAR = :fy"
		      + " AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + " AND GSTIN IN :supplierGstins GROUP BY GSTIN, TRANSACTION_TYPE " 
		      
		      + " UNION ALL " 
		      
		      + " SELECT GSTIN, TRANSACTION_TYPE, (SUM(DIFF_INVOICE_VALUE)) AS INVOICE_VALUE, " 
		      + " (SUM(DIFF_TAXABLE_VALUE)) AS TAXABLE_VALUE, (SUM(DIFF_IGST_AMT)) AS IGST_AMT, " 
		      + " (SUM(DIFF_CGST_AMT)) AS CGST_AMT, (SUM(DIFF_SGST_AMT)) AS SGST_AMT, " 
		      + " (SUM(DIFF_CESS)) AS CESS_AMT FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 " 
		      + " WHERE TRANSACTION_TYPE IS NOT NULL AND " 
		      + " TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
		      + " AND LENGTH(TRANSACTION_TYPE) > 2 " 
		     /* + " AND (CASE WHEN RIGHT(AMD_DERIVED_RET_PERIOD, 2) < 4 " 
		      + "            THEN CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4) - 1, '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2)) " 
		      + "            ELSE CONCAT(LEFT(AMD_DERIVED_RET_PERIOD, 4), '-' || RIGHT(LEFT(AMD_DERIVED_RET_PERIOD, 4), 2) + 1) END)= :fy " */
		      + " AND CONCAT(RIGHT(AMD_DERIVED_RET_PERIOD,2),LEFT(AMD_DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + " AND GSTIN IN :supplierGstins GROUP BY GSTIN, TRANSACTION_TYPE " 
		      + " ) GROUP BY GSTIN "; 
		      
		} else if (valueFlag.equalsIgnoreCase("B2B_CNT")) {
		    queryString = " SELECT GSTIN, SUM(B2B_CNT) AS B2B_CNT " 
		      + " FROM TBL_GSTR1_OUTWARD_DASHBOARD_CNT_PG2 WHERE " 
		     // + " FI_YEAR = :fy AND "
		      + " CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
		      + " AND GSTIN IN :supplierGstins GROUP BY GSTIN ";
		}

	

		return queryString;
	}

	@Override
	public List<Object[]> getOutwardSupplyDetails(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			supplierGstins.removeIf("All"::equals);
			retunPeriods.removeIf("All"::equals);
			
//getOutSuppDetails--api
			String queryString = " SELECT " 
				    + " GSTIN, STATE_NAME, REG_TYPE, " 
				    + " SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT, " 
				    + " SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT, " 
				    + " SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT, " 
				    + " SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT, " 
				    + " (SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0))) AS TOTAL_TAX, " 
				    + " (SUM(INVOICE_VALUE)) AS INVOICE_VALUE, " 
				    + " (SUM(TAXABLE_VALUE)) AS TAXABLE_VALUE " 
				    + " FROM ( " 
				    + "     SELECT C.GSTIN, G.REG_TYPE, MS.STATE_NAME, TRANSACTION_TYPE, " 
				    + "     (SUM(ORG_INVOICE_VALUE)) AS INVOICE_VALUE, " 
				    + "     (SUM(ORG_TAXABLE_VALUE)) AS TAXABLE_VALUE, " 
				    + "     (SUM(ORG_IGST_AMT)) AS IGST_AMT, " 
				    + "     (SUM(ORG_CGST_AMT)) AS CGST_AMT, " 
				    + "     (SUM(ORG_SGST_AMT)) AS SGST_AMT, " 
				    + "     (SUM(ORG_CESS)) AS CESS_AMT " 
				    + "     FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 C " 
				    + "     LEFT JOIN GSTIN_INFO G ON C.GSTIN = G.GSTIN " 
				    + "     LEFT JOIN MASTER_STATE MS ON G.STATE_CODE = MS.STATE_CODE " 
				    + "     WHERE TRANSACTION_TYPE IS NOT NULL " 
				    + "     AND LENGTH(TRANSACTION_TYPE) > 2 " 
				    + "     AND TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
				    + "     AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
				  //  + "     AND FI_YEAR = :fy " 
				    + "     AND C.GSTIN IN :supplierGstins " 
				    + "     GROUP BY C.GSTIN, G.REG_TYPE, MS.STATE_NAME, TRANSACTION_TYPE " 
				    + "     UNION ALL " 
				    + "     SELECT C.GSTIN, G.REG_TYPE, MS.STATE_NAME, TRANSACTION_TYPE, " 
				    + "     (SUM(DIFF_INVOICE_VALUE)) AS INVOICE_VALUE, " 
				    + "     (SUM(DIFF_TAXABLE_VALUE)) AS TAXABLE_VALUE, " 
				    + "     (SUM(DIFF_IGST_AMT)) AS IGST_AMT, " 
				    + "     (SUM(DIFF_CGST_AMT)) AS CGST_AMT, " 
				    + "     (SUM(DIFF_SGST_AMT)) AS SGST_AMT, " 
				    + "     (SUM(DIFF_CESS)) AS CESS_AMT " 
				    + "     FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 C " 
				    + "     LEFT JOIN GSTIN_INFO G ON C.GSTIN = G.GSTIN " 
				    + "     LEFT JOIN MASTER_STATE MS ON G.STATE_CODE = MS.STATE_CODE " 
				    + "     WHERE TRANSACTION_TYPE IS NOT NULL " 
				    + "     AND LENGTH(TRANSACTION_TYPE) > 2 " 
				    + "     AND TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') " 
				    + "     AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods " 
				  //  + "     AND FI_YEAR = :fy " 
				    + "     AND C.GSTIN IN :supplierGstins " 
				    + "     GROUP BY C.GSTIN, G.REG_TYPE, MS.STATE_NAME, TRANSACTION_TYPE " 
				    + " ) " 
				    + " GROUP BY GSTIN, STATE_NAME, REG_TYPE ";

			
			


			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getOutwardSupplyDetails "
								+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
								+ " ,retunPeriods :%s and supplierGstins :%s",
						fy, retunPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getOutwardSupplyDetails "
							+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
							+ " ,retunPeriods :%s and supplierGstins :%s",
					fy, retunPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<String> getSupGstinList(String supplierPan)
			throws AppException {
		try {
			String queryString = "select DISTINCT GSTIN from "
					+ " TBL_OUTWARD_DASH_NET_ITC  where GSTIN LIKE '%"
					+ supplierPan + "%' order by GSTIN asc";
			// return SupGstinList();
			Query q = entityManager.createNativeQuery(queryString);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward SUPPLIES table for given supplierPan "
						+ supplierPan + "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Outward2FioriDashboardDaoImpl.getSupGstinList");
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
						+ "outward supplies table for given fy " + fy
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Outward2FioriDashboardDaoImpl.getSupGstinList");
		}
	}

	@Override
	public List<Object[]> getTotalTurnOverAndTax(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException {
		try {
			supplierGstins.removeIf("All"::equals);
			retunPeriods.removeIf("All"::equals);

			
			String queryString = "SELECT "
				    + "(SUM(IFNULL(TAXABLE_VALUE,0) + IFNULL(IGST_AMT,0) + IFNULL(CGST_AMT,0) + IFNULL(SGST_AMT,0) + IFNULL(CESS_AMT,0))) AS TOTAL_TURNOVER, "
				    + "(SUM(IFNULL(IGST_AMT,0) + IFNULL(CGST_AMT,0) + IFNULL(SGST_AMT,0) + IFNULL(CESS_AMT,0))) AS TOTAL_TAX "
				    + "FROM ( "
				    + "    SELECT "
				    + "        GSTIN, TRANSACTION_TYPE, "
				    + "        (SUM(INVOICE_VALUE)) AS INVOICE_VALUE, "
				    + "        (SUM(TAXABLE_VALUE)) AS TAXABLE_VALUE, "
				    + "        (SUM(IGST_AMT)) AS IGST_AMT, "
				    + "        (SUM(CGST_AMT)) AS CGST_AMT, "
				    + "        (SUM(SGST_AMT)) AS SGST_AMT, "
				    + "        (SUM(CESS)) AS CESS_AMT "
				    + "    FROM TBL_GSTR1_OUTWARD_DASHBOARD_SUMMARY_PG2 "
				    + "    WHERE TRANSACTION_TYPE IS NOT NULL "
				    + "        AND LENGTH(TRANSACTION_TYPE) > 2 "
				    + "        AND TRIM(TRANSACTION_TYPE) IN ('ADVANCES','B2B(Stock transfer)','B2B(Other than stock transfer)','B2C','Exports','Others') "
				    + "        AND CONCAT(RIGHT(DERIVED_RET_PERIOD,2),LEFT(DERIVED_RET_PERIOD,4)) IN :retunPeriods "
				   // + "        AND FI_YEAR = :fy "
				    + "        AND GSTIN IN :supplierGstins "
				    + "    GROUP BY GSTIN, TRANSACTION_TYPE)";



			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", retunPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to get Total Turnover and tax "
								+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
								+ " ,retunPeriods :%s and supplierGstins :%s",
						fy, retunPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to get Total Turnover and tax "
							+ " from TBL_SAC_GSTR1_OUTWARD_SUPPLIES for fy :%s"
							+ " ,retunPeriods :%s and supplierGstins :%s",
					fy, retunPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}
	
}
