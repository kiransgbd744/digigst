package com.ey.advisory.app.services.recon.jobs.anx2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository
@Transactional
public class Anx2ReconciliationPrImpl implements Anx2ReconciliationPr {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2ReconciliationPrImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void callPrProc(String jsonReq, String groupCode) {
		String proc = APIConstants.PROC_PR;
		try {
			LOGGER.debug("Pr recon Proc {} call started.", proc);
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			StoredProcedureQuery st = entityManager
					.createStoredProcedureQuery(proc);
			st.execute();
		} catch (Exception e) {
			LOGGER.error("Error While Executing Procedure {}", e);
		}
	}

}
