package com.ey.advisory.app.gstr2b;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2BDupCheckProcCallHandler")
public class Gstr2BDupCheckProcCallHandler {

	@Autowired
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private final List<String> DUP_CHECK_PROC_LIST = ImmutableList
			.copyOf(Arrays.asList("USP_GETGSTR2B_B2B_DUP_CHK",
					"USP_GETGSTR2B_B2BA_DUP_CHK", "USP_GETGSTR2B_CDNR_DUP_CHK",
					"USP_GETGSTR2B_CDNRA_DUP_CHK", "USP_GETGSTR2B_ECOM_DUP_CHK",
					"USP_GETGSTR2B_ECOMA_DUP_CHK"));

	public String callDupCheckProcs(String gstin, String taxPeriod,
			Long invocationId) {

		try {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" inside the proc call for gstin - {} , taxPeriod {} , invocationId {} ",
						gstin, taxPeriod, invocationId);
			}
			for (String procedureName : DUP_CHECK_PROC_LIST) {

				long dbLoadStTime = System.currentTimeMillis();

				StoredProcedureQuery storedProcedure = entityManager
						.createStoredProcedureQuery(procedureName);

				// Set parameters
				storedProcedure.registerStoredProcedureParameter(
						"IP_RECIPIENT_GSTIN", String.class, ParameterMode.IN);

				storedProcedure.registerStoredProcedureParameter(
						"IP_RETURN_PERIOD", String.class, ParameterMode.IN);

				storedProcedure.registerStoredProcedureParameter("IP_BATCH_ID",
						Long.class, ParameterMode.IN);

				storedProcedure.setParameter("IP_RECIPIENT_GSTIN", gstin);
				storedProcedure.setParameter("IP_RETURN_PERIOD", taxPeriod);
				storedProcedure.setParameter("IP_BATCH_ID", invocationId);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Dup check procName - {} ", procedureName);
				}

				String response = (String) storedProcedure.getSingleResult();

				long dbLoadEndTime = System.currentTimeMillis();
				long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							" Dup check procName - {} executed with response as - {} within time difference of - {}  ",
							procedureName, response, dbLoadTimeDiff);
				}

				if ("SUCCESS".equalsIgnoreCase(response)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" inside success block ");
					}
					continue;
				} else {
					LOGGER.error(
							" Dup check procName - {} executed with response as - {} ",
							procedureName, response);
					return null;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

			String msg = "Gstr2BDupCheckProcCallHandler :: Error while Calling the procs "
					+ " ";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
		return "SUCCESS";
	}

}
