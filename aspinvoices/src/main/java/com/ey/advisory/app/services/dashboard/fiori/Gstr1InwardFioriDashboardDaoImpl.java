package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1InwardFioriDashboardDaoImpl")
public class Gstr1InwardFioriDashboardDaoImpl
		implements Gstr1InwardFioriDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<String> getRecGstinList(String recepientPan)
			throws AppException {

		try {
			String queryString = "select DISTINCT CUST_GSTIN from TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY"
					+ " where CUST_GSTIN LIKE '%" + recepientPan
					+ "%' order by CUST_GSTIN asc";
			// return RecGstinList();
			Query q = entityManager.createNativeQuery(queryString);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given RecepientPan " + recepientPan
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1InwardFioriDashboardDaoImpl.getRecGstinList");
		}
	}

	@Override
	public List<String> getAllReturnPeriods(String fy) {
		try {
			String queryString = "select DISTINCT RET_PERIOD from "
					+ "TBL_SAC_GSTR1_INWARD where FI_YEAR = :fy";
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
					+ "Gstr1InwardFioriDashboardDaoImpl.getAllReturnPeriods");
		}
	}

	@Override
	public List<Object[]> getAllGrossInwardSupp(String fy,
			List<String> recepientGstins, List<String> returnPeriods)
			throws AppException {
		try {

			String queryString = "SELECT * FROM ( " +
					"SELECT TRANSACTION_TYPE, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
					"WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 " +
				//	"AND FI_YEAR = :fy"
					 " AND RET_PERIOD IN :returnPeriods " +
					"AND CUST_GSTIN IN :recepientGstins " +
					"GROUP BY TRANSACTION_TYPE " +
					"UNION ALL " +
					"SELECT 'B2B' AS TRANSACTION_TYPE, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
					"WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 " +
					//"AND FI_YEAR = :fy"
					 " AND RET_PERIOD IN :returnPeriods " +
					"AND CUST_GSTIN IN :recepientGstins " +
					"AND TRANSACTION_TYPE IN ('B2B(Stock transfer)','B2B(Other than stock transfer)') " +
					"UNION ALL " +
					"SELECT 'CDN' AS TRANSACTION_TYPE, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
					"WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 " +
					//"AND FI_YEAR = :fy"
					 " AND RET_PERIOD IN :returnPeriods " +
					"AND CUST_GSTIN IN :recepientGstins " +
					"AND TRANSACTION_TYPE IN ('CDN(Stock transfer)','CDN(Other than stock transfer)')) " +
					"ORDER BY CASE WHEN TRANSACTION_TYPE='B2B' THEN 1 " +
					"WHEN TRANSACTION_TYPE='B2B(Stock transfer)' THEN 2 " +
					"WHEN TRANSACTION_TYPE='B2B(Other than stock transfer)' THEN 3 " +
					"WHEN TRANSACTION_TYPE='CDN' THEN 4 " +
					"WHEN TRANSACTION_TYPE='CDN(Stock transfer)' THEN 5 " +
					"WHEN TRANSACTION_TYPE='CDN(Other than stock transfer)' THEN 6 " +
					"WHEN TRANSACTION_TYPE='Imports (SEZ)' THEN 7 " +
					"WHEN TRANSACTION_TYPE='Imports (Goods-Services)' THEN 8 " +
					"WHEN TRANSACTION_TYPE='ISD' THEN 9 " +
					"WHEN TRANSACTION_TYPE='URD Supplies' THEN 10 " +
					"END ";


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
					+ "Gstr1InwardFioriDashboardDaoImpl.getAllGrossInwardSupp");
		}
	}

	@Override
	public List<Object[]> getMonthWiseTrendAnalysis(String fy, String valueFlag,
			List<String> recepGstins, List<String> returnPrds)
			throws AppException {
		try {
			String queryString = getMonthWiseTrendAnalysisQuery(fy, valueFlag,
					recepGstins, returnPrds);

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("retunPeriods", returnPrds);
			q.setParameter("recepGstins", recepGstins);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPrds"
						+ returnPrds + ", recepGstins " + recepGstins
						+ "valueFlag " + valueFlag + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1InwardFioriDashboardDaoImpl.getMonthWiseTrendAnalysis");
		}
	}

	private String getMonthWiseTrendAnalysisQuery(String fy, String valueFlag,
			List<String> recepGstins, List<String> retunPeriods) {
		String queryString = "";
		if (valueFlag.equalsIgnoreCase("INVOICE_VALUE")) {
		    queryString = "SELECT RET_PERIOD, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE " +
		            "FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE " +
		            //"FI_YEAR = :fy AND "
		            " RET_PERIOD IN :retunPeriods " +
		            "AND CUST_GSTIN IN :recepGstins AND TRANSACTION_TYPE NOT IN " +
		            "('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') " +
		            "GROUP BY RET_PERIOD " +
		            "ORDER BY CONCAT(RIGHT(RET_PERIOD,2),LEFT(RET_PERIOD,4));";
		} else if (valueFlag.equalsIgnoreCase("TAX_AMOUNT")) {
		    queryString = "SELECT RET_PERIOD, ABS(SUM(IGST_AMT))+ABS(SUM(CGST_AMT))+ABS(SUM(SGST_AMT))+ABS(SUM(CESS_AMT)) " +
		            "AS TAX_AMOUNT FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE " +
		            //"FI_YEAR = :fy AND"
		             " RET_PERIOD IN :retunPeriods " +
		            "AND CUST_GSTIN IN :recepGstins AND TRANSACTION_TYPE NOT IN " +
		            "('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') " +
		            "GROUP BY RET_PERIOD " +
		            "ORDER BY CONCAT(RIGHT(RET_PERIOD,2),LEFT(RET_PERIOD,4));";
		} else if (valueFlag.equalsIgnoreCase("CREDIT_AVAILABLE")) {
		    queryString = "SELECT RET_PERIOD, ABS(SUM(AV_IGST_AMT))+ABS(SUM(AV_CGST_AMT))+ABS(SUM(AV_SGST_AMT))+ABS(SUM(AV_CESS_AMT)) " +
		            "AS CREDIT_AVAILABLE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE " +
		          //  "FI_YEAR = :fy AND"
		            " RET_PERIOD IN :retunPeriods " +
		            "AND CUST_GSTIN IN :recepGstins AND TRANSACTION_TYPE NOT IN " +
		            "('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') " +
		            "GROUP BY RET_PERIOD " +
		            "ORDER BY CONCAT(RIGHT(RET_PERIOD,2),LEFT(RET_PERIOD,4));";
		} else if (valueFlag.equalsIgnoreCase("TAXABLE_VALUE")) {
		    queryString = "SELECT RET_PERIOD, ABS(SUM(TAXABLE_VALUE)) AS TAXABLE_VALUE " +
		            "FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE " +
		            //"FI_YEAR = :fy AND"
		             " RET_PERIOD IN :retunPeriods " +
		            "AND CUST_GSTIN IN :recepGstins AND TRANSACTION_TYPE NOT IN " +
		            "('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') " +
		            "GROUP BY RET_PERIOD " +
		            "ORDER BY CONCAT(RIGHT(RET_PERIOD,2),LEFT(RET_PERIOD,4));";
		}

		return queryString;
	}

	@Override
	public List<Object[]> getTopCustomerB2BData(String fy,
			List<String> recepGstins, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT TOP 10 GSTIN, INVOICE_VALUE " +
					"FROM (SELECT GSTIN, SUM(INVOICE_VALUE) AS INVOICE_VALUE " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE CUST_GSTIN IS NOT NULL AND " +
					"LENGTH(CUST_GSTIN)>3 " +
					//+ " AND FI_YEAR = :fy  " +
					" AND RET_PERIOD IN :returnPeriods AND " +
					"GSTIN <>'URP' AND GSTIN IS NOT NULL AND " +
					"CUST_GSTIN IN :recepGstins AND TRANSACTION_TYPE NOT IN " +
					"('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') " +
					"GROUP BY GSTIN) WHERE INVOICE_VALUE>0 ORDER BY 2 DESC";


			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("returnPeriods", returnPeriods);
			q.setParameter("recepGstins", recepGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "inward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", recepGstins " + recepGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1InwardFioriDashboardDaoImpl.getTopCustomerB2BData");
		}
	}

	@Override
	public List<Object[]> getMajorGoodsProcurred(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods)
			throws AppException {
		try {
			/*String queryString = "SELECT TOP 10 ITM_HSNSAC,INVOICE_VALUE FROM ("
					+ "SELECT ITM_HSNSAC,ABS(SUM(INVOICE_VALUE))"
					+ "AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE"
					+ "ITM_HSNSAC IS NOT NULL AND LENGTH(ITM_HSNSAC)>2"
					+ "AND FI_YEAR = :fy AND RET_PERIOD IN :returnPeriods AND"
					+ "CUST_GSTIN IN :listOfRecepGstin"
					+ "AND TRANSACTION_TYPE NOT IN"
					+ "('0','Import Services - Ammendments',"
					+ "'Imports (Goods) - Ammendments',"
					+ "'Import (Goods -SEZ) - Ammendments')"
					+ "GROUP BY ITM_HSNSAC  )ORDER BY 2 DESC";*/
			
			String queryString = "SELECT TOP 10 ITM_HSNSAC, INVOICE_VALUE FROM ("
					+
					"SELECT ITM_HSNSAC, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY "
					+
					"WHERE ITM_HSNSAC IS NOT NULL AND LENGTH(ITM_HSNSAC) > 2 " +
					//"AND FI_YEAR = :fy"
					 " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin "
					+
					"AND TRANSACTION_TYPE NOT IN ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') "
					+
					"GROUP BY ITM_HSNSAC) ORDER BY 2 DESC";


			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
			q.setParameter("listOfRecepGstin", listOfRecepGstin);
			q.setParameter("returnPeriods", returnPeriods);
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
					+ "Gstr1InwardFioriDashboardDaoImpl.getMajorGoodsProcurred");
		}
	}

	@Override
	public List<Object[]> getTaxRateWiseDistribution(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods)
			throws AppException {
		try {
/*			String queryString = "SELECT TAX_RATE,ABS(SUM(INVOICE_VALUE))"
					+ "AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE"
					+ "TAX_RATE IS NOT NULL  AND LENGTH(TAX_RATE)>1 AND"
					+ "FI_YEAR = :fy AND RET_PERIOD IN :returnPeriods AND"
					+ "CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE NOT IN"
					+ "('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')"
					+ "GROUP BY TAX_RATE";*/

			
			String queryString = "SELECT TAX_RATE, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY "
					+
					"WHERE TAX_RATE IS NOT NULL AND LENGTH(TAX_RATE) > 1 " +
					//"AND FI_YEAR = :fy"
					 " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin "
					+
					"AND TRANSACTION_TYPE NOT IN ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments') "
					+
					"GROUP BY TAX_RATE";

			Query q = entityManager.createNativeQuery(queryString);
			//q.setParameter("fy", fy);
			q.setParameter("listOfRecepGstin", listOfRecepGstin);
			q.setParameter("returnPeriods", returnPeriods);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from SAC GStr1 "
						+ "outward table for given fy " + fy + ", returnPeriods"
						+ returnPeriods + ", listOfRecepGstin "
						+ listOfRecepGstin + "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1InwardFioriDashboardDaoImpl.getTaxRateWiseDistribution");
		}
	}

	@Override
	public List<Object[]> getTaxInwardData(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods)
			throws AppException {
		try {
			

			String queryString = "SELECT * FROM ( " +
					"SELECT TRANSACTION_TYPE , ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND TRANSACTION_TYPE NOT IN ('B2B(Stock transfer)','B2B(Other than stock transfer)','CDN(Stock transfer)','CDN(Other than stock transfer)') AND LENGTH(TRANSACTION_TYPE)>2"
					//+ " AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin GROUP BY TRANSACTION_TYPE " +
					"UNION ALL " +
					"SELECT 'B2B' AS TRANSACTION_TYPE, ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 "
					//+ "AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE IN ('B2B(Stock transfer)','B2B(Other than stock transfer)') " +
					"UNION ALL " +
					"SELECT 'B2B(Stock transfer)' AS TRANSACTION_TYPE, ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 "
					//+ "AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE='B2B(Stock transfer)' " +
					"UNION ALL " +
					"SELECT 'B2B(Other than stock transfer)' AS TRANSACTION_TYPE, ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 "
					//+ " AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE='B2B(Other than stock transfer)' " +
					"UNION ALL " +
					"SELECT 'CDN' AS TRANSACTION_TYPE, ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 "
					//+ "AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE IN ('CDN(Stock transfer)','CDN(Other than stock transfer)') " +
					"UNION ALL " +
					"SELECT 'CDN(Stock transfer)' AS TRANSACTION_TYPE, ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 "
					//+ "AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE='CDN(Stock transfer)' " +
					"UNION ALL " +
					"SELECT 'CDN(Other than stock transfer)' AS TRANSACTION_TYPE, ABS(SUM( TAXABLE_VALUE))AS TAXABLE_VALUE, ABS(SUM(IGST_AMT)) AS IGST_AMT, ABS(SUM(CGST_AMT)) AS CGST_AMT, ABS(SUM(SGST_AMT)) AS SGST_AMT, ABS(SUM(CESS_AMT)) AS CESS_AMT, ABS(SUM(INVOICE_VALUE)) AS INVOICE_VALUE, ABS(SUM(AV_IGST_AMT)) AS IGST_CREDIT, ABS(SUM(AV_CGST_AMT)) AS CGST_CREDIT, ABS(SUM(AV_SGST_AMT))AS SGST_CREDIT, ABS(SUM(AV_CESS_AMT)) AS CESS_CREDIT " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY WHERE TRANSACTION_TYPE IS NOT NULL AND LENGTH(TRANSACTION_TYPE)>2 "
					//+ "AND FI_YEAR = :fy"
					+ " AND RET_PERIOD IN :returnPeriods AND CUST_GSTIN IN :listOfRecepGstin AND TRANSACTION_TYPE='CDN(Other than stock transfer)' " +
					") ORDER BY CASE WHEN TRANSACTION_TYPE='B2B' THEN 1 " +
					"WHEN TRANSACTION_TYPE='B2B(Stock transfer)' THEN 2 " +
					"WHEN TRANSACTION_TYPE='B2B(Other than stock transfer)' THEN 3 " +
					"WHEN TRANSACTION_TYPE='CDN' THEN 4 " +
					"WHEN TRANSACTION_TYPE='CDN(Stock transfer)' THEN 5 " +
					"WHEN TRANSACTION_TYPE='CDN(Other than stock transfer)' THEN 6 " +
					"WHEN TRANSACTION_TYPE='Imports (SEZ)' THEN 7 " +
					"WHEN TRANSACTION_TYPE='Imports (Goods-Services)' THEN 8 " +
					"WHEN TRANSACTION_TYPE='ISD' THEN 9 " +
					"WHEN TRANSACTION_TYPE='URD Supplies' THEN 10 END ";

			Query q = entityManager.createNativeQuery(queryString);
		//	q.setParameter("fy", fy);
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
					+ "Gstr1InwardFioriDashboardDaoImpl.getTaxInwardData");
		}
	}

	@Override
	public TotalItcDetailsDto getTotalItcDetails(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {

		TotalItcDetailsDto dto = new TotalItcDetailsDto();

		StringBuilder buildQuery = new StringBuilder();

		buildQuery.append(" CUST_GSTIN IN :gstinList");

		// buildQuery.append(" AND FI_YEAR = :fy");

		buildQuery.append(" AND RET_PERIOD IN :retPeriod");

		// Total ITC
		String query = createQueryTotalITC(buildQuery.toString());

		Query queryForTurnOver = entityManager.createNativeQuery(query);

		queryForTurnOver.setParameter("gstinList", listOfRecepGstin);

		// queryForTurnOver.setParameter("fy", fy);

		queryForTurnOver.setParameter("retPeriod", listOfReturnPrds);

		Object listTotalITC = queryForTurnOver.getSingleResult();

		if (listTotalITC != null) {
			if (listTotalITC instanceof BigDecimal) {
				dto.setTotalItc((BigDecimal) listTotalITC);
			}
		}

		// Input Goods
		String query1 = createQueryInputsGoods(buildQuery.toString());

		Query queryForInputsGoods = entityManager.createNativeQuery(query1);

		queryForInputsGoods.setParameter("gstinList", listOfRecepGstin);

		// queryForInputsGoods.setParameter("fy", fy);

		queryForInputsGoods.setParameter("retPeriod", listOfReturnPrds);

		Object listInputGoods = queryForInputsGoods.getSingleResult();

		if (listInputGoods != null) {
			if (listInputGoods instanceof BigDecimal) {
				dto.setInputGoods((BigDecimal) listInputGoods);
			}
		}

		// Input Services
		String query2 = createQueryInputsServices(buildQuery.toString());

		Query queryForInputsServices = entityManager.createNativeQuery(query2);

		queryForInputsServices.setParameter("gstinList", listOfRecepGstin);

		// queryForInputsServices.setParameter("fy", fy);

		queryForInputsServices.setParameter("retPeriod", listOfReturnPrds);

		Object listInputServices = queryForInputsServices.getSingleResult();

		if (listInputServices != null) {
			if (listInputServices instanceof BigDecimal) {
				dto.setInputServices((BigDecimal) listInputServices);
			}
		}

		// Capital Goods
		String query3 = createQueryCapitalGoods(buildQuery.toString());

		Query queryForCapitalGoods = entityManager.createNativeQuery(query3);

		queryForCapitalGoods.setParameter("gstinList", listOfRecepGstin);

		// queryForCapitalGoods.setParameter("fy", fy);

		queryForCapitalGoods.setParameter("retPeriod", listOfReturnPrds);

		Object listCapitalGoods = queryForCapitalGoods.getSingleResult();

		if (listCapitalGoods != null) {
			if (listCapitalGoods instanceof BigDecimal) {
				dto.setCapitalGoods((BigDecimal) listCapitalGoods);
			}
		}

		// InEligibility
		String query4 = createInEligibility(buildQuery.toString());

		Query queryForInEligibility = entityManager.createNativeQuery(query4);

		queryForInEligibility.setParameter("gstinList", listOfRecepGstin);

		// queryForInEligibility.setParameter("fy", fy);

		queryForInEligibility.setParameter("retPeriod", listOfReturnPrds);

		Object listInEligibility = queryForInEligibility.getSingleResult();

		if (listInEligibility != null) {
			if (listInEligibility instanceof BigDecimal) {
				dto.setInEligibility((BigDecimal) listInEligibility);
			}
		}

		return dto;
	}

	private String createInEligibility(String query) {

	/*	String queryStr = "SELECT ABS(SUM(IFNULL(INELIGIBLE,0))) AS INELIGIBLE " +
				"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
				"WHERE " + query +
				"AND TRANSACTION_TYPE NOT IN ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')";
*/
		
		String queryStr = "SELECT ABS(SUM(IFNULL(INELIGIBLE,0))) AS INELIGIBLE "
				+
				"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
				"WHERE " + query +
				" AND TRANSACTION_TYPE NOT IN " +
				"('0','Import Services - Amendments','Imports (Goods) - Amendments','Import (Goods -SEZ) - Amendments')";

		return queryStr;
	}

	// getTotalITCDetails
	private String createQueryTotalITC(String query) {

		/*String queryStr = "SELECT ABS(SUM(AV_IGST_AMT))+ABS(SUM(AV_CGST_AMT))+"
				+ "ABS(SUM(AV_SGST_AMT))+ABS(SUM(AV_CESS_AMT)) AS TOTAL_ITC "
				+ "FROM TBL_SAC_GSTR1_INWARD " + "WHERE " + query
				+ " AND TRANSACTION_TYPE NOT IN "
				+ " ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')";
		*/
		String queryStr = "SELECT ABS(SUM(AV_IGST_AMT))+ABS(SUM(AV_CGST_AMT))+"
				+ "ABS(SUM(AV_SGST_AMT))+ABS(SUM(AV_CESS_AMT)) AS TOTAL_ITC "
				+ "FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " + "WHERE " + query
				+ " AND TRANSACTION_TYPE NOT IN "
				+ " ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')";

		return queryStr;
	}

	// createQueryInputsGoods
	private String createQueryInputsGoods(String query) {

		
	/*	  String queryStr =
		  "SELECT ABS(SUM(IFNULL(TAX_AVAILABLE,0))) AS INPUT_GOODS " +
		  "FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " + "WHERE  " + query +
		  " AND ELIGIBILITY_INDICATOR = 'IG'" + " AND TRANSACTION_TYPE NOT IN "
		  +
		  " ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')"
		  ;
		 */
		  String queryStr = "SELECT ABS(SUM(IFNULL(TAX_AVAILABLE,0))) AS INPUT_GOODS "
					+
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
					"WHERE " + query +
					" AND ELIGIBILITY_INDICATOR = 'IG' " +
					"AND TRANSACTION_TYPE NOT IN " +
					"('0','Import Services - Amendments','Imports (Goods) - Amendments','Import (Goods -SEZ) - Amendments')";

		

		return queryStr;
	}

	// for input services
	private String createQueryInputsServices(String query) {

	
/*
		String queryStr = "SELECT ABS(SUM(IFNULL(TAX_AVAILABLE,0))) AS INPUT_SERVICES " +
				"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
				"WHERE " + query +
				"AND ELIGIBILITY_INDICATOR = 'IS' " +
				"AND TRANSACTION_TYPE NOT IN ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')";
*/
		String queryStr = "SELECT ABS(SUM(IFNULL(TAX_AVAILABLE,0))) AS INPUT_SERVICES "
				+
				"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
				"WHERE " + query +
				" AND ELIGIBILITY_INDICATOR = 'IS' " +
				"AND TRANSACTION_TYPE NOT IN " +
				"('0','Import Services - Amendments','Imports (Goods) - Amendments','Import (Goods -SEZ) - Amendments')";
		
		

		return queryStr;
	}

	private String createQueryCapitalGoods(String query) {

	/*	String queryStr = "SELECT ABS(SUM(IFNULL(TAX_AVAILABLE,0))) AS CAPITAL_GOODS " +
				"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
				"WHERE " + query +
				"AND ELIGIBILITY_INDICATOR = 'CG' " +
				"AND TRANSACTION_TYPE NOT IN ('0','Import Services - Ammendments','Imports (Goods) - Ammendments','Import (Goods -SEZ) - Ammendments')";
*/
		String queryStr = "SELECT ABS(SUM(IFNULL(TAX_AVAILABLE,0))) AS CAPITAL_GOODS "
				+
				"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
				"WHERE " + query +
				" AND ELIGIBILITY_INDICATOR = 'CG' " +
				"AND TRANSACTION_TYPE NOT IN " +
				"('0','Import Services - Amendments','Imports (Goods) - Amendments','Import (Goods -SEZ) - Amendments')";
		
		return queryStr;
	}

	@Override
	public List<String> getLastUpdatedOn(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods)
			throws AppException {
		try {
			String queryString = "SELECT MAX(REFRESHED_ON) AS LAST_UPDATED_ON " +
					"FROM TBL_INWARD_DASHBOARD_PR_DATA_PG1_SMRY " +
					"WHERE "
					//+ "FI_YEAR = :fy AND"
					+ " RET_PERIOD IN :returnPeriods AND " +
					"CUST_GSTIN IN :listOfRecepGstin";


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
					+ "Gstr1InwardFioriDashboardDaoImpl.getLastUpdatedOn");
		}
	}

}
