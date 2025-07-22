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
@Component("Gstr1Outward3FioriDashboardDaoImpl")
public class Gstr1Outward3FioriDashboardDaoImpl
		implements Gstr1Outward3FioriDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getAllHeadersData(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException {
		try {
			String queryString = "SELECT ABS(SUM(ITC_BALANCE)) AS ITC_BAL,"
					+ " ABS(SUM(IFNULL(TOTAL_LIABILITY,0))) AS TOTAL_LIABILITY,"
					+ " ABS(SUM(IFNULL(PAID_THROUGH_ITC,0))) AS PAID_THROUGH_ITC,"
					+ " ABS(SUM(IFNULL(NET_LIABILITY,0))) AS NET_LIABILITY,"
					+ " ABS(SUM(IFNULL(NET_LIABILITY_SETOFF_IGST,0))) AS IGST,"
					+ " ABS(SUM(IFNULL(NET_LIABILITY_SETOFF_CGST,0))) AS CGST,"
					+ " ABS(SUM(IFNULL(NET_LIABILITY_SETOFF_SGST,0))) AS SGST,"
					+ " ABS(SUM(IFNULL(NET_LIABILITY_SETOFF_CESS,0))) AS CESS,"
					+ " ABS(SUM(IFNULL(INTEREST_PAYABLE,0))) AS INTEREST_PAYABLE,"
					+ " ABS(SUM(IFNULL(LATE_FEE_PAYABLE,0))) AS LATE_FEE_PAYABLE,"
					+ " MAX(REFRESHED_ON) FROM TBL_SAC_GSTR3B_SETOFF_DASHBOARD "
					+ " WHERE FI_YEAR = :fy AND TAX_PERIOD IN :taxPeriods"
					+ " AND GSTIN IN :supplierGstins";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			q.setParameter("taxPeriods", taxPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to get Header data "
								+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
								+ " ,taxPeriods :%s and supplierGstins :%s",
						fy, taxPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to get Header data "
							+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
							+ " ,taxPeriods :%s and supplierGstins :%s",
					fy, taxPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getUtilizationSummaryData(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException {
		try {
			String queryString = "SELECT TRANSACTION_TYPE_PIE,"
					+ " SUM(UTILISATION_SMR_VALUE) AS UTIL_SMRY"
					+ " FROM TBL_SAC_GSTR3B_SETOFF_DASHBOARD WHERE "
					+ " TRANSACTION_TYPE_PIE IS NOT NULL AND"
					+ " FI_YEAR = :fy AND TAX_PERIOD IN :taxPeriods"
					+ " AND GSTIN IN :supplierGstins GROUP BY"
					+ " TRANSACTION_TYPE_PIE";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			q.setParameter("taxPeriods", taxPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getUtilizationSummaryData "
								+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
								+ " ,taxPeriods :%s and supplierGstins :%s",
						fy, taxPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getUtilizationSummaryData"
							+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
							+ " ,taxPeriods :%s and supplierGstins :%s",
					fy, taxPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getGstNetLiabilityData(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException {
		try {
			String queryString = "SELECT TRANSACTION_TYPE_DONUT,"
					+ " SUM(LIABILITY_SETOFF_VALUE) AS UTIL_SMRY"
					+ " FROM TBL_SAC_GSTR3B_SETOFF_DASHBOARD WHERE "
					+ " TRANSACTION_TYPE_DONUT IS NOT NULL AND"
					+ " FI_YEAR = :fy AND TAX_PERIOD IN :taxPeriods"
					+ " AND GSTIN IN :supplierGstins GROUP BY"
					+ " TRANSACTION_TYPE_DONUT";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			q.setParameter("taxPeriods", taxPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getGstNetLiabilityData "
								+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
								+ " ,taxPeriods :%s and supplierGstins :%s",
						fy, taxPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getGstNetLiabilityData"
							+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
							+ " ,taxPeriods :%s and supplierGstins :%s",
					fy, taxPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getRevenueAndLiabCompAnalysis(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException {
		try {
			String queryString = getRevenueAndLiabCompAnalysisQuery();

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			q.setParameter("taxPeriods", taxPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getRevenueAndLiabCompAnalysis "
								+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
								+ " ,taxPeriods :%s and supplierGstins :%s",
						fy, taxPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getRevenueAndLiabCompAnalysis"
							+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
							+ " ,taxPeriods :%s and supplierGstins :%s",
					fy, taxPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private String getRevenueAndLiabCompAnalysisQuery() {
		String queryString = "SELECT GSTIN, FI_YEAR,"
				+ " SUM(LIABILITY_FORWARD_CHARGE) AS LIABILITY_FORWARD_CHARGE,"
				+ " SUM(LIABILITY_FOR_REVERSE_CHARGE) AS LIABILITY_FOR_REVERSE_CHARGE,"
				+ " SUM(INTEREST_PAYABLE) AS INTEREST_PAYABLE,"
				+ " SUM(LATE_FEE_PAYABLE) AS LATE_FEE_PAYABLE,"
				+ " ABS(SUM(PAID_THROUGH_ITC)) AS PAID_THROUGH_ITC,"
				+ " SUM(CASH_PAYABLE) AS CASH_PAYABLE,"
				+ " SUM(NET_LIABILITY) AS NET_LIABILITY,"
				+ " SUM(INVOICE_VALUE) AS INVOICE_VALUE"
				+ " FROM (SELECT GSTIN, FI_YEAR,"
				+ " SUM(LIABILITY_FORWARD_CHARGE) AS LIABILITY_FORWARD_CHARGE,"
				+ " SUM(LIABILITY_FOR_REVERSE_CHARGE) AS LIABILITY_FOR_REVERSE_CHARGE,"
				+ " SUM(INTEREST_PAYABLE) AS INTEREST_PAYABLE,"
				+ " SUM(LATE_FEE_PAYABLE) AS LATE_FEE_PAYABLE,"
				+ " ABS(SUM(PAID_THROUGH_ITC)) AS PAID_THROUGH_ITC,"
				+ " SUM(CASH_PAYABLE) AS CASH_PAYABLE,"
				+ " SUM(NET_LIABILITY) AS NET_LIABILITY,"
				+ " 0 AS INVOICE_VALUE FROM TBL_SAC_GSTR3B_SETOFF_DASHBOARD"
				+ " WHERE TRANSACTION_TYPE_DONUT IS NOT NULL AND"
				+ " FI_YEAR = :fy AND TAX_PERIOD IN :taxPeriods"
				+ " AND GSTIN IN :supplierGstins GROUP BY GSTIN, FI_YEAR"
				+ " UNION ALL "
				+ " SELECT GSTIN, FI_YEAR, 0, 0, 0, 0, 0, 0, 0, SUM(INVOICE_VALUE)"
				+ " AS INVOICE_VALUE FROM TBL_SAC_GSTR1_OUTWARD"
				+ " WHERE FI_YEAR = :fy AND RETURN_PERIOD IN :taxPeriods"
				+ " AND GSTIN IN :supplierGstins GROUP BY GSTIN, FI_YEAR) "
				+ " GROUP BY GSTIN, FI_YEAR";
		return queryString;
	}

	@Override
	public List<Object[]> getLiabilityTableData(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException {
		try {
			String queryString = getLiabilityTableDataQuery();

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			q.setParameter("taxPeriods", taxPeriods);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getLiabilityTableData "
								+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
								+ " ,taxPeriods :%s and supplierGstins :%s",
						fy, taxPeriods, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getLiabilityTableData"
							+ " from TBL_SAC_GSTR3B_SETOFF_DASHBOARD for fy :%s"
							+ " ,taxPeriods :%s and supplierGstins :%s",
					fy, taxPeriods, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private String getLiabilityTableDataQuery() {

		String queryString = "SELECT * FROM (SELECT GSTIN,"
				+ " SUM(LIABILITY_FORWARD_CHARGE) AS LIABILITY_FORWARD_CHARGE,"
				+ " SUM(LIABILITY_FOR_REVERSE_CHARGE) AS LIABILITY_FOR_REVERSE_CHARGE,"
				+ " SUM(INTEREST_PAYABLE) AS INTEREST_PAYABLE,"
				+ " SUM(LATE_FEE_PAYABLE) AS LATE_FEE_PAYABLE,"
				+ " SUM(PAID_THROUGH_ITC) AS PAID_THROUGH_ITC,"
				+ " SUM(CASH_PAYABLE) AS CASH_PAYABLE"
				+ " FROM TBL_SAC_GSTR3B_SETOFF_DASHBOARD"
				+ " WHERE TRANSACTION_TYPE_DONUT IS NOT NULL AND"
				+ " FI_YEAR = :fy AND TAX_PERIOD IN :taxPeriods"
				+ " AND GSTIN IN :supplierGstins GROUP BY GSTIN" + " UNION ALL"
				+ " SELECT 'Total' AS GSTIN, SUM(LIABILITY_FORWARD_CHARGE)"
				+ " AS LIABILITY_FORWARD_CHARGE,"
				+ " SUM(LIABILITY_FOR_REVERSE_CHARGE) AS LIABILITY_FOR_REVERSE_CHARGE,"
				+ " SUM(INTEREST_PAYABLE) AS INTEREST_PAYABLE,"
				+ " SUM(LATE_FEE_PAYABLE) AS LATE_FEE_PAYABLE,"
				+ "	SUM(PAID_THROUGH_ITC) AS PAID_THROUGH_ITC,"
				+ " SUM(CASH_PAYABLE) AS CASH_PAYABLE"
				+ " FROM TBL_SAC_GSTR3B_SETOFF_DASHBOARD"
				+ " WHERE TRANSACTION_TYPE_DONUT IS NOT NULL AND"
				+ " FI_YEAR = :fy AND TAX_PERIOD IN :taxPeriods"
				+ " AND GSTIN IN :supplierGstins)";
		return queryString;
	}

	@Override
	public List<String> getSupGstinList(String supplierPan)
			throws AppException {
		try {
			String queryString = "select DISTINCT GSTIN from "
					+ " TBL_SAC_GSTR3B_SETOFF_DASHBOARD  where GSTIN LIKE '%"
					+ supplierPan + "%' order by GSTIN asc";
			// return SupGstinList();
			Query q = entityManager.createNativeQuery(queryString);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from  "
						+ " TBL_SAC_GSTR3B_SETOFF_DASHBOARD table for given  "
						+ " supplierPan " + supplierPan + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Outward3FioriDashboardDaoImpl.getSupGstinList");
		}
	}

	@Override
	public List<String> getAllReturnPeriods(String fy) throws AppException {
		try {
			String queryString = "select DISTINCT TAX_PERIOD from "
					+ " TBL_SAC_GSTR3B_SETOFF_DASHBOARD where FI_YEAR = :fy";
			// return returnPeriodsList();
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("fy", fy);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from "
						+ "TBL_SAC_GSTR3B_SETOFF_DASHBOARD table for given fy "
						+ fy + "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr1Outward3FioriDashboardDaoImpl.getAllReturnPeriods");
		}
	}
}
