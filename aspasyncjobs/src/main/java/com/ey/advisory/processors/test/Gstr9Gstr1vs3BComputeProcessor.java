package com.ey.advisory.processors.test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeConfigStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeProcedureEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9FinYearFilingCallEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeConfigStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeProcedureRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeReadyStatusFYRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9FinYearFilingCallRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@Slf4j
@Component("Gstr9Gstr1vs3BComputeProcessor")
public class Gstr9Gstr1vs3BComputeProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr9ComputeConfigStatusRepository")
	Gstr9ComputeConfigStatusRepository configStatusRepo;

	@Autowired
	@Qualifier("Gstr9ComputeProcedureRepository")
	Gstr9ComputeProcedureRepository procedureRepo;

	@Autowired
	@Qualifier("Gstr9ComputeReadyStatusFYRepository")
	Gstr9ComputeReadyStatusFYRepository readyStatusRepo;

	@Autowired
	@Qualifier("Gstr9FinYearFilingCallRepository")
	Gstr9FinYearFilingCallRepository finYearFilingcalRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static final String SUCCESS = "SUCCESS";

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr9Gstr1vs3BComputeProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();
		boolean isAmdReqFlag = json.get("isAmdReq").getAsBoolean();

		Map<Integer, String> procMap = new TreeMap<>();
		String procName = null;
		String response = null;

		try {
			List<Gstr9ComputeProcedureEntity> allProcList = procedureRepo
					.findAll();
			Gstr9ComputeConfigStatusEntity entity = configStatusRepo
					.findByConfigId(configId);
			Integer fy = entity.getFy();
			Gstr9FinYearFilingCallEntity fyFilingEntity = finYearFilingcalRepo
					.findByFy(fy);
			String gstin = entity.getGstin();
			Integer fyStart = fyFilingEntity.getFyStart();
			Integer fyEnd = fyFilingEntity.getFyEnd();
			Integer lateFiledFyStart = fyFilingEntity.getLateFiledFyStart();
			Integer lateFiledFyEnd = fyFilingEntity.getLateFiledFyEnd();

			configStatusRepo.updateStatus(configId, "INPROGRESS", null);
			procMap = allProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			for (Integer k : procMap.keySet()) {
				procName = procMap.get(k);
				if (!isAmdReqFlag && (procName
						.equalsIgnoreCase("USP_GSTR9_COMPUTE_7_SEC_14")
						|| procName.equalsIgnoreCase(
								"USP_GSTR9_COMPUTE_9_SEC_19"))) {
					continue;
					
				} else if (isAmdReqFlag && (procName
						.equalsIgnoreCase("USP_GSTR9_COMPUTE_7_SEC_14")
						|| procName.equalsIgnoreCase(
								"USP_GSTR9_COMPUTE_9_SEC_19"))) {
					fyStart = lateFiledFyStart;
					fyEnd = lateFiledFyEnd;
				}
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
			configStatusRepo.updateStatus(configId, SUCCESS, null);
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response " + "from GSTR9 SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), procName);
			LOGGER.error(msg, e);

			configStatusRepo.updateStatus(configId, "FAILED", e.getMessage());

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
