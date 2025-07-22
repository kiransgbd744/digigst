/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ReconResultUpdateReportTypeDaoImpl")
public class ReconResultUpdateReportTypeDaoImpl
		implements ReconResultUpdateReportTypeDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public int updateReconReportType(BigInteger a2ReconLinkId,
			BigInteger prReconLinkId) {

		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_12_FORCE_MATCH");

			storedProc.registerStoredProcedureParameter("P_A2_RECON_LINK_ID",
					BigInteger.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("P_PR_RECON_LINK_ID",
					BigInteger.class, ParameterMode.IN);

			storedProc.setParameter("P_A2_RECON_LINK_ID", a2ReconLinkId);
			storedProc.setParameter("P_PR_RECON_LINK_ID", prReconLinkId);
			
	
			int count = storedProc.getUpdateCount();

			return count;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while Invoking Store proc");
			throw new AppException(
					"Unexpected error : Error while Invoking "
					+ "Store proc execution.");
		}

	}

}
