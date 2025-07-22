package com.ey.advisory.app.gstr3b;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.repositories.client.Gstr2bVs3bStatusRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr2bvs3bProcProcessDaoImpl")
public class Gstr2bvs3bProcProcessDaoImpl {
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier(value = "Gstr2bVs3bStatusRepository")
	private Gstr2bVs3bStatusRepository gstr2bVs3bStatusRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bvs3bProcProcessDaoImpl.class);

	public void proceCallForComputeReversal(final Long batchId,
			final String gstin, final Integer taxPeriodFrom,
			final Integer taxPeriodTo) {
		try {
			StoredProcedureQuery storedProcQuery = entityManager
					.createStoredProcedureQuery("USP_2BVS3B_COMPUTE");

			storedProcQuery.registerStoredProcedureParameter("P_BATCH_ID",
					Long.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter("P_GSTIN",
					String.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter("FROM_TAX_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter("TO_TAX_PERIOD",
					Integer.class, ParameterMode.IN);

			storedProcQuery.setParameter("P_BATCH_ID", batchId);
			storedProcQuery.setParameter("P_GSTIN", gstin);
			storedProcQuery.setParameter("FROM_TAX_PERIOD", taxPeriodFrom);
			storedProcQuery.setParameter("TO_TAX_PERIOD", taxPeriodTo);

			storedProcQuery.execute();
			LOGGER.error("Proc Executed successfully");
			LOGGER.error("proc Executed successfully for {} and from {} to {}",
					gstin, taxPeriodFrom, taxPeriodTo);

		} catch (Exception e) {
			LOGGER.debug("Exception Occured:", e);
			gstr2bVs3bStatusRepository.gstr2bvs3bUpdateFailedStatus(batchId);
		}
	}
	
	
	
}
