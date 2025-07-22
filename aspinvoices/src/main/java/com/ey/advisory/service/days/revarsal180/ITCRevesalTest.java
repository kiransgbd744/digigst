/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Config180DaysComputeEntity;
import com.ey.advisory.app.data.repositories.client.Config180DaysComputeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ReconStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author QD194RK
 *
 */
@Slf4j
@Component("ITCRevesalTest")
public class ITCRevesalTest {
	
	@Autowired
	@Qualifier("Config180DaysComputeRepository")
	Config180DaysComputeRepository configRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("ITCReversal180DaysComputeReportsImpl")
	ITCReversal180DaysComputeReportsImpl reportGenerateService;
	
	public String callProcs(Long computeId){

	try {
		
		Config180DaysComputeEntity entity = configRepo
				.findByComputeId(computeId);

		String toDate = null;
		String fromDate = null;

		String fromAccDate = entity.getFromAccDate();
		String toAccDate = entity.getToAccDate();

		boolean isDocDate = true;

		if (fromAccDate != null && toAccDate != null) {

			isDocDate = false;
			toDate = toAccDate.toString();
			fromDate = fromAccDate.toString();
		}

		String fromDocDate = entity.getFromDocDate();
		String toDocDate = entity.getToDocDate();

		toDate = toDocDate;
		fromDate = fromDocDate;

		StoredProcedureQuery storedProc = proccall(computeId,  toDate,
				fromDate, isDocDate, "USP_180_DAYS_LOADING_COMPUTE");

		String response = (String) storedProc.getSingleResult();

		LOGGER.error("USP_180_DAYS_LOADING_COMPUTE" + " :: " + response);

		if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {
			configRepo.updateComputeStatus("Failed", computeId);
			String msg = String
					.format("ITC Reversal 180days Compute Faild while"
							+ " loading Data:: computeId %d ", computeId);
			throw new AppException(msg);

		}

		storedProc = proccall(computeId, toDate, fromDate, isDocDate,
				"USP_180_DAYS_REVERSAL_COMPUTE");

		response = (String) storedProc.getSingleResult();

		LOGGER.error("USP_180_DAYS_REVERSAL_COMPUTE" + " :: " + response);

		if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {
			configRepo.updateComputeStatus("Failed", computeId);
			String msg = String.format(
					"ITC Reversal 180days Compute Faild while"
							+ " Reclaim Compute :: computeId %d ",
					computeId);
			throw new AppException(msg);

		}

		configRepo.updateComputeStatus("Computed", computeId);
		
		reportGenerateService.generateReport(computeId);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	return "success";
}


/**
 * @param computeId
 * @param gstin
 * @param toDate
 * @param fromDate
 * @param isDocDate
 */
private StoredProcedureQuery proccall(Long computeId,
		String toDate, String fromDate, boolean isDocDate,
		String procName) {
	StoredProcedureQuery storedProc = entityManager
			.createStoredProcedureQuery(procName);

	storedProc.registerStoredProcedureParameter("P_COMPUTE_ID", Long.class,
			ParameterMode.IN);

	storedProc.setParameter("P_COMPUTE_ID", computeId);

	storedProc.registerStoredProcedureParameter("P_FROM_DATE", String.class,
			ParameterMode.IN);

	storedProc.setParameter("P_FROM_DATE", fromDate);

	storedProc.registerStoredProcedureParameter("P_TO_DATE", String.class,
			ParameterMode.IN);

	storedProc.setParameter("P_TO_DATE", toDate);

	storedProc.registerStoredProcedureParameter("P_IS_DOC_DATE",
			Boolean.class, ParameterMode.IN);

	storedProc.setParameter("P_IS_DOC_DATE", isDocDate);

	return storedProc;
}

}

