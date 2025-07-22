/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Config180DaysComputeEntity;
import com.ey.advisory.app.data.repositories.client.Config180DaysComputeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.days.revarsal180.ITCReversal180DaysComputeReportsImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ITCReversal180DaysComputeProcessor")
public class ITCReversal180DaysComputeProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Config180DaysComputeRepository")
	Config180DaysComputeRepository configRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ITCReversal180DaysComputeReportsImpl")
	ITCReversal180DaysComputeReportsImpl reportGenerateService;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin ITCReversal180DaysComputeProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		Long computeId = null;
		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			computeId = json.get("configId").getAsLong();

			Config180DaysComputeEntity entity = configRepo
					.findByComputeId(computeId);

			String toDate = null;
			String fromDate = null;
			

			String fromAccDate = entity.getFromAccDate();
			String toAccDate = entity.getToAccDate();
			Integer fromTaxPeriod = entity.getFromTaxPeriod();
			Integer toTaxPeriod = entity.getToTaxPeriod();
			String toDocDate = entity.getToDocDate();
			String fromDocDate = entity.getFromDocDate();
			String criteria = "docDate";
			/*boolean isDocDate = true;
			boolean isTaxPeriod = true;
*/
			if (fromAccDate != null && toAccDate != null) {
				
				toDate = toAccDate.toString();
				fromDate = fromAccDate.toString();
				criteria = "accVoucherDate";
			} else if(fromDocDate != null && toDocDate != null){
				
				toDate = toDocDate;
				fromDate = fromDocDate;
				criteria = "docDate";
			}

			if (fromTaxPeriod != null && toTaxPeriod != null) {
				
				toDate = toTaxPeriod.toString();
				fromDate = fromTaxPeriod.toString();
				criteria = "taxPeriod";

			}
			
			
			StoredProcedureQuery storedProc = proccall(computeId, toDate,
					fromDate, criteria, "USP_180_DAYS_LOADING_COMPUTE");

			String response = (String) storedProc.getSingleResult();

			LOGGER.debug("USP_180_DAYS_LOADING_COMPUTE {}" + " :: " + response);

			if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {
				configRepo.updateComputeStatus("Failed", computeId);
				String msg = String
						.format("ITC Reversal 180days Compute Faild while"
								+ " loading Data:: computeId %d ", computeId);
				LOGGER.error(msg);
				throw new AppException(msg);

			}

			storedProc = proccall(computeId, toDate, fromDate, criteria,
					"USP_180_DAYS_REVERSAL_COMPUTE");

			response = (String) storedProc.getSingleResult();

			LOGGER.debug("USP_180_DAYS_REVERSAL_COMPUTE {}" + " :: " + response);

			if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {
				configRepo.updateComputeStatus("Failed", computeId);
				String msg = String.format(
						"ITC Reversal 180days Compute Faild while"
								+ " Reclaim Compute :: computeId %d ",
						computeId);
				LOGGER.error(msg);
				throw new AppException(msg);

			}

			configRepo.updateComputeStatus("Computed", computeId);

			try {
				reportGenerateService.generateReport(computeId);
			} catch (Exception e) {
				configRepo.updateComputeStatus("REPORT GENERATION FAILED",
						computeId);

				String msg = String.format("Error occured while generating "
						+ "ITC Reversal 180days Compute Report computeId  :: "
						+ "%s", computeId);
				LOGGER.error(msg, e);
				throw new AppException(msg, e);

			}

		} catch (Exception ex) {
			configRepo.updateComputeStatus("Compute Failed", computeId);
			String msg = String
					.format("ITC Reversal 180days Compute Faild while"
							+ " Reclaim Compute :: computeId %d ", computeId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	/**
	 * @param computeId
	 * @param gstin
	 * @param toDate
	 * @param fromDate
	 * @param isDocDate
	 */
	private StoredProcedureQuery proccall(Long computeId, String toDate,
			String fromDate, String criteria,

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

		storedProc.registerStoredProcedureParameter("P_CRITERIA", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_CRITERIA", criteria);

		/*
		 * storedProc.registerStoredProcedureParameter("P_FROM_TAX_PERIOD",
		 * Integer.class, ParameterMode.IN);
		 * 
		 * storedProc.setParameter("P_FROM_TAX_PERIOD", fromTaxPeriod);
		 * 
		 * storedProc.registerStoredProcedureParameter("P_TO_TAX_PERIOD",
		 * Integer.class, ParameterMode.IN);
		 * 
		 * storedProc.setParameter("P_TO_TAX_PERIOD", ToTaxPeriod);
		 * 
		 * storedProc.registerStoredProcedureParameter("P_IS_TAX_PERIOD",
		 * Boolean.class, ParameterMode.IN);
		 * 
		 * storedProc.setParameter("P_IS_TAX_PERIOD", isTaxPeriod);
		 */

		return storedProc;
	}

}
