package com.ey.advisory.app.gstr3b;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.services.credit.reversal.CreditReversalProcessDaoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * 
 * @author Sasidhar reddy
 *
 */
@Repository("Gstr1vs3bProcProcessDaoImpl")
public class Gstr1vs3bProcProcessDaoImpl {
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditReversalProcessDaoImpl.class);

	public int proceCallForComputeReversal(final String gstin,
			final Integer derivedRetPerFrom, final Integer derivedRetPerTo) {
		int count = 0;
		try {
			StoredProcedureQuery storedProcQuery = entityManager
					.createStoredProcedureQuery("COMPUTE_GSTR1_VS_3B");
			storedProcQuery.registerStoredProcedureParameter("GSTIN",
					String.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter(
					"FROM_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
			storedProcQuery.registerStoredProcedureParameter(
					"TO_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
			storedProcQuery.setParameter("FROM_DERIVED_RET_PERIOD",
					derivedRetPerFrom);
			storedProcQuery.setParameter("TO_DERIVED_RET_PERIOD",
					derivedRetPerTo);
			storedProcQuery.setParameter("GSTIN", gstin);
			storedProcQuery.execute();
			count = 1 + count;

		} catch (Exception e) {

			LOGGER.debug("Exception Occured:", e);
		}
		return count;
	}

}
