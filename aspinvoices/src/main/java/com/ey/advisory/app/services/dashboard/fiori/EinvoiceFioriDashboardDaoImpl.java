package com.ey.advisory.app.services.dashboard.fiori;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@Slf4j
@Component("EinvoiceFioriDashboardDaoImpl")
public class EinvoiceFioriDashboardDaoImpl
		implements EinvoiceFioriDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<String> getSupGstinList(List<String> supplierGstins)
			throws AppException {
		try {
			String queryString = "select DISTINCT SGSTIN from ERP_REQ_AGG_SUMMARY"
					+ " where SGSTIN IN :supplierGstins"
					+ " order by SGSTIN asc";
			// return SupGstinList();
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data from ERP_REQ "
						+ "erp table for given supplierPan " + supplierGstins
						+ "and query = " + queryString);
			}
			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "EinvoiceFioriDashboardDaoImpl.getSupGstinList");
		}
	}

	@Override
	public List<Object[]> getHeaderDataDetails(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate) {
		try {
			String queryString = "SELECT SUM(TOTAL_REQ) AS TOTAL_IRNs, "
					+ " SUM(GEN_COUNT) AS GENERATED, "
					+ " SUM(CAN_COUNT) AS CANCELED, "
					+ " SUM(DUP_COUNT) AS DUPLICATE, "
					+ " SUM(ERROR_COUNT) AS ERRORED "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getHeaderDataDetails "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getHeaderDataDetails "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getAvgIrnGen(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate) {
		try {
			String queryString = "SELECT IFNULL(ROUND(SUM(GEN_COUNT)/(CASE"
					+ " WHEN DAYS_BETWEEN(:fromSummDate,:toSummDate) = 0"
					+ " THEN 1 ELSE DAYS_BETWEEN(:fromSummDate,:toSummDate)"
					+ " END)),0) AS GENERATED, "
					+ " MAX(CREATED_ON) "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getHeaderDataDetails "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> obj = q.getResultList();
			return obj;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getHeaderDataDetails "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getEinvoiceSumm(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate, Long entityId) {
		try {
			String queryString = "SELECT SUM(GEN_COUNT) AS GENERATED, "
					+ " SUM(CAN_COUNT) AS CANCELLED, "
					+ " SUM(DUP_COUNT) AS DUPLICATES, "
					+ " SUM(ERROR_COUNT) AS ERRORED "
					+ " FROM ERP_REQ_AGG_SUMMARY A "
					+ " INNER JOIN GSTIN_INFO G ON A.SGSTIN=G.GSTIN "
					+ " INNER JOIN ENTITY_INFO E ON E.ID=G.ENTITY_ID "
					+ " WHERE A.IS_ACTIVE = TRUE AND E.ID =:entityId AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			q.setParameter("entityId", entityId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getEinvoiceSumm "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s, entityId :%s and supplierGstins :%s",
						fromSummDate, toSummDate, entityId, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getEinvoiceSumm "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s, entityId :%s and supplierGstins :%s",
					fromSummDate, toSummDate, entityId, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getEinvDistribution(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate) {
		try {
			String queryString = "SELECT SUM(GEN_COUNT) AS GENERATED, "
					+ " SUM(CAN_COUNT) AS CANCELLED, "
					+ " SUM(DUP_COUNT) AS DUPLICATES, "
					+ " SUM(ERROR_COUNT) AS ERRORED "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getEinvDistribution "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getEinvDistribution "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getErrorDetails(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate) {
		try {
			String queryString = "SELECT TO_VARCHAR(YEAR(SUMMARY_DATE)) AS SMRY_DT_YR,"
					+ " TO_VARCHAR(MONTH(SUMMARY_DATE)) AS SMRY_DT_MON,"
					+ " SUM(ERROR_COUNT) AS ERRORED "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins "
					+ " GROUP BY YEAR(SUMMARY_DATE),MONTH(SUMMARY_DATE)";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getErrorDetails "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getErrorDetails "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getEinvGenTredForGenAndTotal(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {
		try {
			String queryString = "SELECT SUMMARY_DATE, "
					+ " SUM(GEN_COUNT) AS GENERATED, "
					+ " SUM(TOTAL_REQ) AS TOTAL_REQ "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins "
					+ " GROUP BY SUMMARY_DATE";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getEinvGenTredForGenAndTotal "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getEinvGenTredForGenAndTotal "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getEinvGenTredForCanDupAndErr(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {
		try {
			String queryString = "SELECT SUMMARY_DATE, "
					+ " SUM(CAN_COUNT) AS CANCELLED, "
					+ " SUM(DUP_COUNT) AS DUPLICATES, "
					+ " SUM(ERROR_COUNT) AS ERRORED "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins "
					+ " GROUP BY SUMMARY_DATE";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getEinvGenTredForCanDupAndErr "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getEinvGenTredForCanDupAndErr "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getEinvStatusTable(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate) {
		try {
			String queryString = "SELECT SUMMARY_DATE, "
					+ " SUM(TOTAL_REQ) AS TOTAL_REQ, "
					+ " SUM(GEN_COUNT) AS GENERATED, "
					+ " SUM(CAN_COUNT) AS CANCELLED, "
					+ " SUM(DUP_COUNT) AS DUPLICATES, "
					+ " SUM(ERROR_COUNT) AS ERRORED "
					+ " FROM ERP_REQ_AGG_SUMMARY "
					+ " WHERE IS_ACTIVE = TRUE AND "
					+ " SUMMARY_DATE BETWEEN :fromSummDate and :toSummDate "
					+ " AND SGSTIN IN :supplierGstins "
					+ " GROUP BY SUMMARY_DATE "
					+ " ORDER BY SUMMARY_DATE DESC";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("supplierGstins", supplierGstins);
			q.setParameter("fromSummDate", fromSummDate);
			q.setParameter("toSummDate", toSummDate);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query to getEinvStatusTable "
								+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
								+ " ,toSummDate :%s and supplierGstins :%s",
						fromSummDate, toSummDate, supplierGstins);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing query to getEinvStatusTable "
							+ " from ERP_REQ_AGG_SUMMARY for fromSummDate :%s"
							+ " ,toSummDate :%s and supplierGstins :%s",
					fromSummDate, toSummDate, supplierGstins);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

}
