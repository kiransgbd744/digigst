package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.BasicCommonSecParam;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("ProcessSbmitPorcDaoImpl")
public class ProcessSbmitPorcDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	public int getProcubmitProcCall(List<String> gstinList,
			int derivedRetPeriodFrom, int derivedRetPeriodTo) {

		// String proc = GSTConstants.GSTR1_PS_RS_SUBREP;
		String proc = "USP_GSTR1_SUBMITTED_PS_TRANS";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramDervdPeriodFrom = "IP_DERIVED_RET_PERIOD_FROM";
		String paramDervdPeriodTo = "IP_DERIVED_RET_PERIOD_TO";

		int count =0;
		for (String gstin : gstinList) {
 
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside saveGstr1HsnProcCall method with args {},{}",
							gstin, derivedRetPeriodFrom, derivedRetPeriodTo);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{} Procedure Call started", proc);
				}
				StoredProcedureQuery st = entityManager
						.createStoredProcedureQuery(proc);
				// set parameters
				st.registerStoredProcedureParameter(paramGstin, String.class,
						ParameterMode.IN);
				st.registerStoredProcedureParameter(paramDervdPeriodFrom,
						Integer.class, ParameterMode.IN);
				st.registerStoredProcedureParameter(paramDervdPeriodTo,
						Integer.class, ParameterMode.IN);

				st.setParameter(paramGstin, gstin);
				st.setParameter(paramDervdPeriodFrom, derivedRetPeriodFrom);
				st.setParameter(paramDervdPeriodTo, derivedRetPeriodTo);
				st.execute();
				count = 1 + count;
			} catch (Exception e) {
				LOGGER.error("Inside Procedure getting exception {}-->", e);
			

			}

		}
		return count;
	}

}

