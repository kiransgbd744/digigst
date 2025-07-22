package com.ey.advisory.app.data.repositories.client;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
@Component("NilAndHsnProcedureCallRepositoryImpl")
public class NilAndHsnProcedureCallRepositoryImpl
		implements NilAndHsnProcedureCallRepository {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void getNilNonProc(String sgstin, Integer intDevPeriod) {

		String proc = GSTConstants.GSTR1_POPUP_ASPUI_NILEXTNON;
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside saveGstr1NilNonExtProcCall method with args {},{}",
						sgstin, intDevPeriod);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} Procedure Call started", proc);
			}
			StoredProcedureQuery st = entityManager
					.createStoredProcedureQuery(proc);
			// set parameters
			st.registerStoredProcedureParameter(paramGstin, String.class,
					ParameterMode.IN);
			st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
					ParameterMode.IN);

			st.setParameter(paramGstin, sgstin);
			st.setParameter(paramDervdPeriod, intDevPeriod);
			st.execute();
		} catch (Exception e) {
			LOGGER.debug("Inside Procedure getting exception", e);
			throw new AppException();

		}
	}

	@Override
	public void getHsnProc(String sgstin, Integer intDevPeriod) {

		String proc = GSTConstants.GSTR1_POPUP_ASPUI_HSNSAC;
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside saveGstr1HsnProcCall method with args {},{}",
						sgstin, intDevPeriod);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} Procedure Call started", proc);
			}
			StoredProcedureQuery st = entityManager
					.createStoredProcedureQuery(proc);
			// set parameters
			st.registerStoredProcedureParameter(paramGstin, String.class,
					ParameterMode.IN);
			st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
					ParameterMode.IN);

			st.setParameter(paramGstin, sgstin);
			st.setParameter(paramDervdPeriod, intDevPeriod);
			st.execute();
		} catch (Exception e) {
			LOGGER.debug("Inside Procedure getting exception", e);
			throw new AppException();

		}
	}

}
