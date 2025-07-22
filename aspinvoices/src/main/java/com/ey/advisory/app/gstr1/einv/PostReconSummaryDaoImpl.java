package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Component;
import com.ey.advisory.common.AppException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("PostReconSummaryDaoImpl")
public class PostReconSummaryDaoImpl implements PostReconSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> getPostReconSummaryData(List<String> recipientGstins,
			Integer taxPeriod) {
		String rGstins = "";
		try {
			if (recipientGstins != null && !recipientGstins.isEmpty()) {
				rGstins = String.join(",", recipientGstins);
			} else {
				rGstins = "";
			}

			StoredProcedureQuery storedProc = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Inside PostReconSummaryDaoImpl."
						+ "Invoking USP_AUTO_2APR_RECON_SUMMARY Stored Proc");
				LOGGER.debug(msg);
			}
			String procName = "USP_EINV_RECON_SUMMARY";
			storedProc = entityManager.createStoredProcedureQuery(procName);

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_GSTIN_LIST", rGstins);

			storedProc.registerStoredProcedureParameter("P_RET_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("P_RET_PERIOD", taxPeriod);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();
			return list;
		} catch (Exception ee) {
			String msg = String.format("Error while Executing Stored Proc to "
					+ " getAutoReconSummaryData for recipientGstins :%s"
					+ " , TaxPeriod :%s ", recipientGstins, taxPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

}
