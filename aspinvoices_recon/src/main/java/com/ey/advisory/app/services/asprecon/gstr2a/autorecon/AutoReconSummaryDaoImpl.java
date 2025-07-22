package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("AutoReconSummaryDaoImpl")
public class AutoReconSummaryDaoImpl implements AutoReconSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getAutoReconSummaryData(List<String> recipientGstins,
			boolean isDateRange, Integer fromTaxPeriodPR, Integer toTaxPeriodPR,
			Integer fromTaxPeriod2A, Integer toTaxPeriod2A,
			LocalDate fromReconDate, LocalDate toReconDate, String criteria) {
		String rGstins = "";
		try {
			if (recipientGstins != null && !recipientGstins.isEmpty()) {
				rGstins = String.join(",", recipientGstins);
			} else {
				rGstins = "";
			}

			StoredProcedureQuery storedProc = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside AutoReconSummaryDaoImpl.getAutoReconSummaryData():: "
								+ "Invoking USP_AUTO_2APR_RECON_SUMMARY Stored Proc");
				LOGGER.debug(msg);
			}
			storedProc = entityManager
					.createStoredProcedureQuery("USP_AUTO_2APR_RECON_SUMMARY");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_GSTIN_LIST", rGstins);

			storedProc.registerStoredProcedureParameter("IS_DATE_RANGE",
					Boolean.class, ParameterMode.IN);
			storedProc.setParameter("IS_DATE_RANGE", isDateRange);

			storedProc.registerStoredProcedureParameter("FROM_TAX_PRD_PR",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("FROM_TAX_PRD_PR", fromTaxPeriodPR);

			storedProc.registerStoredProcedureParameter("TO_TAX_PRD_PR",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("TO_TAX_PRD_PR", toTaxPeriodPR);

			storedProc.registerStoredProcedureParameter("FROM_TAX_PRD_2A",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("FROM_TAX_PRD_2A", fromTaxPeriod2A);

			storedProc.registerStoredProcedureParameter("TO_TAX_PRD_2A",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("TO_TAX_PRD_2A", toTaxPeriod2A);

			storedProc.registerStoredProcedureParameter("FROM_RECON_DT",
					LocalDate.class, ParameterMode.IN);
			storedProc.setParameter("FROM_RECON_DT", fromReconDate);

			storedProc.registerStoredProcedureParameter("TO_RECON_DT",
					LocalDate.class, ParameterMode.IN);
			storedProc.setParameter("TO_RECON_DT", toReconDate);
			
			storedProc.registerStoredProcedureParameter("P_CRITERIA",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_CRITERIA", criteria);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();
			return list;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing Stored Proc to "
							+ " getAutoReconSummaryData for recipientGstins :%s"
							+ " , fromTaxPeriodPR :%s, toTaxPeriodPR :%s "
							+ " , fromTaxPeriod2A :%s ,toTaxPeriod2A :%s "
							+ " , fromReconDate :%s and toReconDate :%s "
							+ " ,isDateRange :%s ",
					recipientGstins, fromTaxPeriodPR, toTaxPeriodPR,
					fromTaxPeriod2A, toTaxPeriod2A, fromReconDate, toReconDate,
					isDateRange);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getIncrementalDataSummary(
			List<String> recipientGstins) {
		String rGstins = "";
		try {
			if (recipientGstins != null && !recipientGstins.isEmpty()) {
				rGstins = String.join(",", recipientGstins);
			} else {
				rGstins = "";
			}

			StoredProcedureQuery storedProc = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside AutoReconSummaryDaoImpl.getIncrementalDataSummary():: "
								+ "Invoking USP_AUTO_2APR_PRE_RECON_SUMMARY Stored Proc");
				LOGGER.debug(msg);
			}
			storedProc = entityManager.createStoredProcedureQuery(
					"USP_AUTO_2APR_PRE_RECON_SUMMARY");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_GSTIN_LIST", rGstins);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();
			return list;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing Stored Proc to get "
							+ " IncrementalDataSummary for recipientGstins :%s",
					recipientGstins);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Object[]> getAutoReconGstinsData(Long entityId) {
		try {
			String queryString = createQueryString();

			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"Query created for getAutoReconGstinsData "
								+ "on entityId selected  By User : %s",
						queryString);
				LOGGER.debug(str);
			}

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			return list;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing query to get "
							+ " Auto recon gstin data for entityId :%s",
					entityId);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private String createQueryString() {
		String query = "SELECT STATE_NAME, REG_TYPE, GSTIN, STATUS, STATUS_DATE "
				+ " FROM (SELECT MS.STATE_NAME, REG_TYPE , GI.GSTIN, CASE "
				+ " WHEN RC.STATUS = 'RECON_COMPLETED' OR "
				+ " RC.STATUS = 'REPORT_GENERATED' THEN 'SUCCESS' "
				+ " WHEN RC.STATUS = 'RECON_INITIATED' THEN 'IN PROGRESS' "
				+ " WHEN RC.STATUS = 'NO_DATA_FOUND' OR "
				+ " RC.STATUS = 'REPORT_GENERATION_FAILED' THEN 'FAILED' "
				+ " ELSE 'NOT INITIATED'" + " END  AS STATUS, "
				+ " RC.COMPLETED_ON AS STATUS_DATE, "
				+ " ROW_NUMBER() OVER( PARTITION BY GI.GSTIN "
				+ " ORDER BY RC.RECON_REPORT_CONFIG_ID DESC, "
				+ " RC.COMPLETED_ON DESC) AS RNK "
				+ " FROM            GSTIN_INFO GI "
				+ " INNER JOIN      ENTITY_INFO EI "
				+ "  ON              EI.ID = GI.ENTITY_ID "
				+ " INNER JOIN      MASTER_STATE MS "
				+ " ON              MS.STATE_CODE = GI.STATE_CODE "
				+ " LEFT OUTER JOIN TBL_RECON_REPORT_GSTIN_DETAILS GD "
				+ " ON              GD.GSTIN = GI.GSTIN "
				+ " LEFT OUTER JOIN TBL_RECON_REPORT_CONFIG RC "
				+ " ON              GD.RECON_REPORT_CONFIG_ID = RC.RECON_REPORT_CONFIG_ID "
				+ " AND RC.RECON_TYPE ='AUTO_2APR' "
				+ " WHERE           EI.ID =:entityId "
				+ " AND             GI.IS_DELETE = FALSE "
				+ " AND             EI.IS_DELETE = FALSE)A "
				+ " WHERE    RNK = 1 " + "ORDER BY GSTIN;";
		return query;
	}

	@Override
	public Object getAutoReconUpdatedOn(Long entityId) {
		try {
			String queryString = "SELECT MAX(CREATED_DATE)"
					+ " FROM TBL_RECON_REPORT_CONFIG"
					+ " WHERE ENTITY_ID = :entityId AND"
					+ " RECON_TYPE = 'AUTO_2APR'";

			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"Query created for getAutoReconUpdatedOn "
								+ "on entityId selected  By User : %s",
						queryString);
				LOGGER.debug(str);
			}

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);

			Object obj = q.getSingleResult();
			return obj;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing query to get "
							+ " Auto recon Summary UpdatedOn for entityId :%s",
					entityId);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	@Override
	public Object getIncrementalDataUpdatedOn(Long entityId) {
		try {
			String queryString = "SELECT IFNULL(MAX(GD.COMPLETED_ON),"
					+ " '2020-04-01') AS FROM_DATE, NOW() AS TO_DATE"
					+ " FROM TBL_RECON_REPORT_GSTIN_DETAILS GD"
					+ " INNER JOIN TBL_RECON_REPORT_CONFIG C ON "
					+ " C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID "
					+ " AND C.RECON_TYPE IN ('AUTO_2APR','AP_M_2APR')"
					+ " AND GD.STATUS='RECON_COMPLETED'"
					+ " WHERE C.ENTITY_ID=:entityId";

			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"Query created for getIncrementalDataUpdatedOn "
								+ "on entityId selected  By User : %s",
						queryString);
				LOGGER.debug(str);
			}

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);

			Object obj = q.getSingleResult();
			return obj;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing query to get "
							+ " Incremental data updatedOn for entityId :%s",
					entityId);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}
}
