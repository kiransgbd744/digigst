package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
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
@Component("GSTR1Vs3BInitiateReconProcessor")
public class GSTR1Vs3BInitiateReconProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier(value = "Gstr1Vs3bConfigRepository")
	private Gstr1Vs3bConfigRepository gstr1Vs3bConfigRepo;
	
	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier(value = "Gstr1Vs3bStatusRepository")
	private Gstr1Vs3bStatusRepository gstr1Vs3bStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin GSTR1Vs3BInitiateReconProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		String userName = message.getUserName();

		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();
		JsonObject reqJson = json.get("reqDto").getAsJsonObject();

		Gstr1VsGstr3bProcessSummaryReqDto reqDto = gson.fromJson(reqJson,
				Gstr1VsGstr3bProcessSummaryReqDto.class);
		try {

			List<String> gstinsDetails = gstr1Vs3bStatusRepo
					.getAllGstinsByConfigId(configId);

			String procName = "COMPUTE_GSTR1_VS_3B";
			String response = null;

			for (String gstin : gstinsDetails) {

				StoredProcedureQuery storedProc = entityManager
						.createStoredProcedureQuery(procName);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"About to execute 1vs3BRecon SP with "
									+ "ConfigId :%s and gstin :%s",
							configId.toString(), gstin);
					LOGGER.debug(msg);
				}

				storedProc.registerStoredProcedureParameter("RECON_REPORT_CONFIG_ID",
						Long.class, ParameterMode.IN);
				storedProc.setParameter("RECON_REPORT_CONFIG_ID", configId);

				storedProc.registerStoredProcedureParameter("GSTIN",
						String.class, ParameterMode.IN);
				storedProc.setParameter("GSTIN", gstin);

				response = (String) storedProc.getSingleResult();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Config ID is '%s', Executed 1vs3BRecon SP, "
									+ "Response %s from COMPUTE_GSTR1_VS_3B "
									+ "storeProc: %s, gsting :%s, groupCode :%s",
							configId.toString(), response, procName, gstin,
							groupCode);
					LOGGER.debug(msg);
				}

				if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String msg = String.format(
							"Config Id is '%s', gstin :%s Response "
									+ "from COMPUTE_GSTR1_VS_3B %s "
									+ "did not return success,"
									+ " Hence updating to Failed",
							configId.toString(), gstin, procName);
					LOGGER.debug(msg);

					gstr1Vs3bConfigRepo.updateGstr1Vs3BReconStatus(
							ReconStatusConstants.RECON_FAILED,
							EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							configId,null);
					gstr1Vs3bStatusRepo.updateGstinStatus(
							ReconStatusConstants.RECON_FAILED, configId, gstin);
					throw new AppException(msg);

				} else {
					gstr1Vs3bStatusRepo.updateGstinStatus(
							ReconStatusConstants.RECON_COMPLETED, configId,
							gstin);
					continue;
				}
				
				
			}
			gstr1Vs3bConfigRepo.updateGstr1Vs3BReconStatus(
					ReconStatusConstants.RECON_COMPLETED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId,null);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config ID is '%s', calling report generation service, "
								+ " GSTR1_VS_3B for groupCode %s",
								configId.toString(),groupCode);
				LOGGER.debug(msg);
			}
			
			//toDo - report generation service
			
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);
			jsonParams.add("reqDto", gson.toJsonTree(reqDto));
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_VS_3b_REPORT, jsonParams.toString(),
					userName, 1L, null, null);

		}catch (Exception ex) {
			  String msg = String.format("Config Id is '%s', Exception occured during initiate recon for gstr1Vs3B",
			  configId.toString()); 
			  
			 LOGGER.debug(msg);
			  
			 LOGGER.error(msg, ex);
			
			 gstr1Vs3bConfigRepo.updateGstr1Vs3BReconStatus(
					ReconStatusConstants.RECON_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId,null);

			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}

	}

}
