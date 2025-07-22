package com.ey.advisory.app.dashboard.homeOld;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("DashboardHOProcCallServiceImpl")
public class DashboardHOProcCallServiceImpl
		implements DashboardHOProcCallService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@Override
	public void dashboardProcCall(String derivedRetPeriod, Long batchId,
			Long entityId) {

		boolean procComp = callProcCompliance(batchId, entityId,
				derivedRetPeriod);

		boolean procGstr1 = callProcOutwardSupply(batchId, entityId,
				derivedRetPeriod);

		if (procComp && procGstr1) {
			ldRepo.softlyDeleteBatchIds(derivedRetPeriod, entityId);
			ldRepo.updateSuccessBatchStatus(batchId, "COMPLETED",
					LocalDateTime.now());

		} else {
			ldRepo.updateBatchStatus(batchId, "FAILED", LocalDateTime.now());
			throw new AppException();
		}

	}

	private boolean callProcCompliance(Long batchId, Long entityId,
			String derivedTaxPeriod) {
		boolean response = true;
		try {
			StoredProcedureQuery returnComplianceProc = entityManager
					.createStoredProcedureQuery(
							"USP_LD_RETURN_COMPLIANCE_STATUS");
			if (batchId != null) {
				returnComplianceProc.registerStoredProcedureParameter(
						"P_BATCH_ID", Long.class, ParameterMode.IN);

				returnComplianceProc.setParameter("P_BATCH_ID", batchId);

			}
			returnComplianceProc.registerStoredProcedureParameter("ENTITYID",
					Long.class, ParameterMode.IN);

			returnComplianceProc.registerStoredProcedureParameter("TAXPERIOD",
					String.class, ParameterMode.IN);

			returnComplianceProc.setParameter("ENTITYID", entityId);

			returnComplianceProc.setParameter("TAXPERIOD", derivedTaxPeriod);

			returnComplianceProc.getSingleResult();
		} catch (Exception ex) {
			LOGGER.error("USP_LD_RETURN_COMPLIANCE_STATUS proc Failed {}  ",
					ex);
			response = false;
		}
		return response;

	}

	private boolean callProcOutwardSupply(Long batchId, Long entityId,
			String derivedTaxPeriod) {
		boolean response = true;
		try {
			StoredProcedureQuery returnComplianceProc = entityManager
					.createStoredProcedureQuery(
							"USP_LD_OUTWARD_SUPPLY_GSTR1_VS_GSTR3B");
			if (batchId != null) {
				returnComplianceProc.registerStoredProcedureParameter(
						"P_BATCH_ID", Long.class, ParameterMode.IN);

				returnComplianceProc.setParameter("P_BATCH_ID", batchId);

			}
			returnComplianceProc.registerStoredProcedureParameter("ENTITYID",
					Long.class, ParameterMode.IN);

			returnComplianceProc.registerStoredProcedureParameter("TAXPERIOD",
					String.class, ParameterMode.IN);

			returnComplianceProc.setParameter("ENTITYID", entityId);

			returnComplianceProc.setParameter("TAXPERIOD", derivedTaxPeriod);

			returnComplianceProc.getSingleResult();
		} catch (Exception ex) {
			LOGGER.error(
					"USP_LD_OUTWARD_SUPPLY_GSTR1_VS_GSTR3B proc Failed {} ",
					ex);
			response = false;
		}
		return response;

	}

}
