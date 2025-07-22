package com.ey.advisory.processors.test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconFailedProcedureEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconFailedProcedureRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.Gstr2aAutoInitiateReconPanLevelService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("Gstr2aAutoInitiateReconPanLevelProcessor")
public class Gstr2aAutoInitiateReconPanLevelProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2aAutoInitiateReconPanLevelServiceImpl")
	private Gstr2aAutoInitiateReconPanLevelService autoInitiateReconService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;
	
	@Autowired
	@Qualifier("Gstr2ReconFailedProcedureRepository")
	Gstr2ReconFailedProcedureRepository procRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long entityId = json.get("entityId").getAsLong();
		Long configId = json.get("configId").getAsLong();
		String retType = json.get("returnType").getAsString();

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Gstr2aAutoInitiateReconPanLevelProcessor with :%s"
								+ "entityId :%s and configId :%s",
						entityId, configId);
				LOGGER.debug(msg);
			}

			reconConfigRepo.updateReconConfigStatus("PAN_RECON_JOB_POSTED",
					configId);

			autoInitiateReconService.initiateAutoReconPanLevel(entityId,
					configId, retType);

			JsonObject erpReportParams = new JsonObject();
			erpReportParams.addProperty("configId", configId);
			erpReportParams.addProperty("entityId", entityId);

			asyncJobsService.createJob(message.getGroupCode(),
					JobConstants.Gstr2A_AUTO_RECON_ERP_REPORT,
					erpReportParams.toString(), "SYSTEM", 50L, null, null);

			Gstr2ReconAddlReportsEntity setEntity = setEntity(configId,
					"ERP_Report");

			addlReportRepo.save(setEntity);

			reconConfigRepo.updateReconConfigStatus("ERP_JOB_POSTED", configId);

		} catch (Exception ee) {
			String msg = "Exception occurred while auto initiating pan level recon after"
					+ " gstin level recon Get Gstr2a";
			LOGGER.error(msg, ee);
			callFailedProc("", configId);

			throw new AppException(msg, ee);
		}
	}

	private Gstr2ReconAddlReportsEntity setEntity(Long configId,
			String reportName) {

		Gstr2ReconAddlReportsEntity entity = new Gstr2ReconAddlReportsEntity();
		entity.setConfigId(configId);
		entity.setIsDownloadable(false);
		entity.setReportType(reportName);
		entity.setReportTypeId(23);

		return entity;
	}

	private void callFailedProc(String gstin, Long configId) {

		String procName = null;
		String response = null;
		try {
			List<Gstr2ReconFailedProcedureEntity> procList = procRepo.findProcedure();
	
			Map<Integer, String> procMap = new TreeMap<>();

			procMap = procList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config Id is '%s', Procs going to "
								+ "Execute  are %s ",
						configId.toString(), procMap.toString());
				LOGGER.error(msg);
			}
			for (Integer k : procMap.keySet())

			{
				procName = procMap.get(k);

				StoredProcedureQuery storedProc = procCall(configId, gstin,
						procName);

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(procName + " :: " + response);

				if (!ReconStatusConstants.SUCCESS
						.equalsIgnoreCase(response)) {

					String msg = String.format(
							"Config Id is '%s', Response "
									+ "from RECON_FAILED PROC %s did not "
									+ "return success,"
									+ " Hence updating to Failed",
							configId.toString(), procName);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config Id is '%s', After For loop "
								+ "Post proc Execution %s ",
						configId.toString(), procMap.toString());
				LOGGER.error(msg);
			}
			reconConfigRepo.updateReconConfigStatus("RECON_FAILED",
					configId);

		} catch (Exception ex) {
			LOGGER.error(
					" Failed in failure proc at gstin level with config id {} and with exception {} ",
					configId, ex);

			reconConfigRepo.updateReconConfigStatus("RECON_HALT", configId);
			failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(configId),
					"AUTO_RECON",String.format(" RECON_HALT FOUND AT PAN LEVEL"));
		}

	}
	
	private StoredProcedureQuery procCall(Long configId, String gstin,
			String procName) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon Failed Proc with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

		return storedProc;
	}

}
