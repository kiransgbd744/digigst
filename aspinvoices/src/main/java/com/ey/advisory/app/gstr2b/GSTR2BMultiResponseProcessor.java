package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.ControlGstnGetStatusEntity;
import com.ey.advisory.app.data.entities.client.MonitorGstnGetStatusEntity;
import com.ey.advisory.app.data.repositories.client.ControlGstnGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.MonitorGstnGetStatusRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("GSTR2BMultiResponseProcessor")
public class GSTR2BMultiResponseProcessor
		implements Gstr2BGetJsonResponseProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("MonitorGstnGetStatusRepository")
	private MonitorGstnGetStatusRepository monitorGstnGetStatusRepo;

	@Autowired
	@Qualifier("ControlGstnGetStatusRepository")
	private ControlGstnGetStatusRepository controlGstnGetStatusRepo;

	@Override
	public void processJsonResponse(String gstin, String taxPeriod,
			Long invocationId, List<Long> reqIds, boolean isAuto) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Long monitorId = createMonitorEntry(gstin, taxPeriod, invocationId,
				isAuto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created Entry in Monitor table with id {}",
					monitorId);
		}
		List<List<Long>> resultIdSets = Lists.partition(reqIds, 10);
		for (List<Long> chunkIds : resultIdSets) {
			Long controlId = createControlEntry(monitorId);

			JsonObject jsonParams = new JsonObject();
			jsonParams.add("jsonIds", gson.toJsonTree(chunkIds));
			jsonParams.addProperty("controlId", controlId);
			jsonParams.addProperty("invId", invocationId);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR2B_GET_CHUNKS, jsonParams.toString(),
					"SYSTEM", 50L, null, null);
		}
	}

	private Long createMonitorEntry(String gstin, String taxPeriod,
			Long invocationId, boolean isAuto) {
		MonitorGstnGetStatusEntity monitorEntity = new MonitorGstnGetStatusEntity();
		monitorEntity.setInvocationId(invocationId);
		monitorEntity.setBatchUpdated(false);
		monitorEntity.setGstin(gstin);
		monitorEntity.setTaxPeriod(taxPeriod);
		monitorEntity.setReturnType(APIConstants.GSTR2B);
		monitorEntity.setSection(APIConstants.GSTR2B);
		monitorEntity.setUpdatedOn(LocalDateTime.now());
		monitorEntity.setCreatedOn(LocalDateTime.now());
		monitorEntity.setAutoRequest(isAuto);
		monitorEntity = monitorGstnGetStatusRepo.save(monitorEntity);
		return monitorEntity.getMonitorId();
	}

	private Long createControlEntry(Long monitorId) {
		ControlGstnGetStatusEntity controlEntity = new ControlGstnGetStatusEntity();
		controlEntity.setMonitorId(monitorId);
		controlEntity.setJobStatus(APIConstants.INITIATED);
		controlEntity.setCreatedOn(LocalDateTime.now());
		controlEntity.setUpdatedOn(LocalDateTime.now());
		controlEntity = controlGstnGetStatusRepo.save(controlEntity);
		return controlEntity.getId();
	}

}
