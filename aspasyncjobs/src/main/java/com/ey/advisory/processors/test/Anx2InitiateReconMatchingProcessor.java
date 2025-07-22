package com.ey.advisory.processors.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.anx2.initiaterecon.InitiateReconFetchReportDetails;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconConfigRepository;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("Anx2InitiateReconMatchingProcessor")
public class Anx2InitiateReconMatchingProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ReconConfigRepository")
	ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("InitiateReconFetchReportDetailsImpl")
	InitiateReconFetchReportDetails initiateReconFetchReportDetails;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Anx2InitiateReconMatchingProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();
		StoredProcedureQuery storedProc = entityManager
				.createNamedStoredProcedureQuery("initiateReconcile");

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon SP with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		try {

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			String response = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Config ID is '%s', Executed Recon SP, "
								+"Response from RECON_MASTER "
								+"storeProc: %s", configId.toString(),response);
				LOGGER.debug(msg);
			}

			if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Config Id is '%s', Response "
							+ "from RECON_MASTER SP did not return success,"
							+ " Hence updating to Failed", configId.toString());
					LOGGER.debug(msg);
				}

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_FAILED, null, new Date(),
						configId);

				return;
			}
			
			initiateReconFetchReportDetails.getReconReportData(configId);

		} catch (Exception ex) {

			LOGGER.error(
					"Exception occured during initiate recon ", ex);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					new Date(), configId);

			return;

		}

	}

}
