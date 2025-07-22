package com.ey.advisory.app.data.services.gstr6;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr6.Gstr6ComputeDigiConfigStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeDigiConfigStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant Shukla
 *
 */
@Slf4j
@Component("Gstr6DigiGstProcessedComputeServiceImpl")
public class Gstr6DigiGstProcessedComputeServiceImpl {

	@Autowired
	@Qualifier("Gstr6ComputeDigiConfigStatusRepository")
	Gstr6ComputeDigiConfigStatusRepository digiConfigStatusRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static final String SUCCESS = "SUCCESS";

	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr6DigiGstProcessedComputeServiceImpl :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		String response = null;

		try {
			Gstr6ComputeDigiConfigStatusEntity entity = digiConfigStatusRepo
					.findByConfigId(configId);
			String taxPeriod = entity.getTaxPeriod();
			String gstin = entity.getGstin();

			digiConfigStatusRepo.updateStatus(configId, "INPROGRESS", null);

			StoredProcedureQuery storedProc = procCall(gstin,
					Long.valueOf(taxPeriod));

			response = (String) storedProc.getSingleResult();

			if (!SUCCESS.equalsIgnoreCase(response)) {

				String msg = String.format("Config Id is '%s', Response "
						+ "from GSTR6 SP %s did not " + "return success,"
						+ " Hence updating to Failed", configId.toString(),
						"USP_GSTR6_COMPUTE");
				LOGGER.error(msg);

				throw new AppException(msg);
			}
			digiConfigStatusRepo.updateStatus(configId, SUCCESS, null);
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response " + "from GSTR6 SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), "USP_GSTR6_COMPUTE");
			LOGGER.error(msg, e);

			digiConfigStatusRepo.updateStatus(configId, "FAILED",
					e.getMessage());

			throw new AppException(e);

		}
	}

	private StoredProcedureQuery procCall(String gstin, Long taxPeriod) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_GSTR6_COMPUTE");

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("About to execute GSTR6 SP with gstin :%s", gstin);
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("P_RET_PERIOD", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_RET_PERIOD", taxPeriod);

		return storedProc;
	}
}
