package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1EinvPreReconSummaryDaoImpl")
public class Gstr1EinvPreReconSummaryDaoImpl
		implements Gstr1EinvPreReconSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getPreReconSummary(List<String> gstins,
			Integer returnPeriod) {
		String rGstins = "";
		try {
			if (gstins != null && !gstins.isEmpty()) {
				rGstins = String.join(",", gstins);
			} else {
				rGstins = "";
			}

			StoredProcedureQuery storedProc = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside Gstr1EinvPreReconSummaryDaoImpl."
								+ "getPreReconSummary():: "
								+ "Invoking USP_EINV_RECON_PRE_SUMMARY Stored Proc");
				LOGGER.debug(msg);
			}
			storedProc = entityManager
					.createStoredProcedureQuery("USP_EINV_RECON_PRE_SUMMARY");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_GSTIN_LIST", rGstins);

			storedProc.registerStoredProcedureParameter("P_RET_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("P_RET_PERIOD", returnPeriod);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();
			return list;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing Stored Proc to get PreReconSummary"
							+ " for Gstins :%s and taxPeriod :%s",
					rGstins, returnPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

}
