package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.ControlGstnGetIrnStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.MonitorGstnGetIrnStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ControlGstnGetIrnStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.MonitorGstnGetIrnStatusRepository;
import com.ey.advisory.app.inward.einvoice.InwardGetIrnListDataParser;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("GetIrnMultiResponseProcessor")
public class GetIrnMultiResponseProcessor
		implements GetIrnJsonResponseProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("MonitorGstnGetIrnStatusRepository")
	private MonitorGstnGetIrnStatusRepository monitorGstnGetStatusRepo;

	@Autowired
	@Qualifier("ControlGstnGetIrnStatusRepository")
	private ControlGstnGetIrnStatusRepository controlGstnGetStatusRepo;
	
	@Autowired
	@Qualifier("InwardGetIrnJsonDataParserImpl")
	InwardGetIrnListDataParser inwardGetIrnJsonDataParser;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	
	@Override
	public void processJsonResponse(String gstin, String taxPeriod,
			Long batchId, List<Long> reqIds, boolean isAuto, String section, Gstr1GetInvoicesReqDto dto) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		Long monitorId = createMonitorEntry(gstin, taxPeriod, batchId,
				isAuto, section);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created Entry in Monitor table with id {} for batchid {} and type {} ",
					monitorId, batchId, section);
		}
		List<List<Long>> resultIdSets = Lists.partition(reqIds, 10);
		for (List<Long> chunkIds : resultIdSets) {
			Long controlId = createControlEntry(monitorId);

			JsonObject jsonParams = new JsonObject();
			jsonParams.add("jsonIds", gson.toJsonTree(chunkIds));
			jsonParams.addProperty("controlId", controlId);
			jsonParams.addProperty("batchId", batchId);
			jsonParams.addProperty("section", section);
			jsonParams.addProperty("dto", gson.toJson(dto));	
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GETIRN_GET_CHUNKS, jsonParams.toString(),
					"SYSTEM", 50L, null, null);
		}
	}
	

	private Long createMonitorEntry(String gstin, String taxPeriod,
			Long batchId, boolean isAuto, String section) {
		MonitorGstnGetIrnStatusEntity monitorEntity = new MonitorGstnGetIrnStatusEntity();
		monitorEntity.setInvocationId(batchId);
		monitorEntity.setBatchUpdated(false);
		monitorEntity.setGstin(gstin);
		monitorEntity.setTaxPeriod(taxPeriod);
		monitorEntity.setReturnType(APIConstants.GET_IRN_LIST);
		monitorEntity.setSection(section);
		monitorEntity.setUpdatedOn(LocalDateTime.now());
		monitorEntity.setCreatedOn(LocalDateTime.now());
		monitorEntity.setAutoRequest(isAuto);
		monitorEntity = monitorGstnGetStatusRepo.save(monitorEntity);
		return monitorEntity.getMonitorId();
	}

	private Long createControlEntry(Long monitorId) {
		ControlGstnGetIrnStatusEntity controlEntity = new ControlGstnGetIrnStatusEntity();
		controlEntity.setMonitorId(monitorId);
		controlEntity.setJobStatus(APIConstants.INITIATED);
		controlEntity.setCreatedOn(LocalDateTime.now());
		controlEntity.setUpdatedOn(LocalDateTime.now());
		controlEntity = controlGstnGetStatusRepo.save(controlEntity);
		return controlEntity.getId();
	}

}
