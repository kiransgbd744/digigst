package com.ey.advisory.app.data.services.gstr9;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeDigiConfigStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeDigiProcedureEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeDigiConfigStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeDigiProcedureRepository;
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
@Component("Gstr9DigiGstProcessedComputeServiceImpl")
public class Gstr9DigiGstProcessedComputeServiceImpl {

	@Autowired
	@Qualifier("Gstr9ComputeDigiConfigStatusRepository")
	Gstr9ComputeDigiConfigStatusRepository digiConfigStatusRepo;

	@Autowired
	@Qualifier("Gstr9ComputeDigiProcedureRepository")
	Gstr9ComputeDigiProcedureRepository digiProcedureRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static final String SUCCESS = "SUCCESS";

	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr9DigiGstProcessedComputeServiceImpl :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		Map<Integer, String> procMap = new TreeMap<>();
		String procName = null;
		String response = null;

		try {
			List<Gstr9ComputeDigiProcedureEntity> allProcList = digiProcedureRepo
					.findAll();
			Gstr9ComputeDigiConfigStatusEntity entity = digiConfigStatusRepo
					.findByConfigId(configId);
			Integer fy = entity.getFy();		
			Integer fyStart = Integer.parseInt(fy.toString().substring(0, 4) + "04");
			Integer fyEnd = Integer.parseInt("20" + fy.toString().substring(4, 6) + "03");
			
			String gstin = entity.getGstin();

			digiConfigStatusRepo.updateStatus(configId, "INPROGRESS", null);
			procMap = allProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			for (Integer k : procMap.keySet()) {
				procName = procMap.get(k);
				StoredProcedureQuery storedProc = procCall(gstin, configId,
						procName, Long.valueOf(fyStart), Long.valueOf(fyEnd));

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(procName + " :: " + response);

				if (!SUCCESS.equalsIgnoreCase(response)) {

					String msg = String.format("Config Id is '%s', Response "
							+ "from GSTR9 SP %s did not " + "return success,"
							+ " Hence updating to Failed", configId.toString(),
							procName);
					LOGGER.error(msg);

					throw new AppException(msg);
				}
			}
			digiConfigStatusRepo.updateStatus(configId, SUCCESS, null);
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response " + "from GSTR9 SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), procName);
			LOGGER.error(msg, e);

			digiConfigStatusRepo.updateStatus(configId, "FAILED",
					e.getMessage());

			throw new AppException(e);

		}
	}

	private StoredProcedureQuery procCall(String gstin, Long configId,
			String procName, Long retStart, Long retEnd) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute GSTR9 SP with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_GSTR9_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_GSTR9_CONFIG_ID", configId);

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("P_FROM_RET_PERIOD",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_FROM_RET_PERIOD", retStart);

		storedProc.registerStoredProcedureParameter("P_TO_PERIOD_END",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_TO_PERIOD_END", retEnd);

		return storedProc;
	}	
}
