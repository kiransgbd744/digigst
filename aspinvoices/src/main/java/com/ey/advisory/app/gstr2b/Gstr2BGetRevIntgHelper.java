package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.data.entities.client.Get2BErpConfigRequestEntity;
import com.ey.advisory.app.data.repositories.client.Get2BErpConfigRequestRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2BMonitorTagging2ARepository;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Service("Gstr2BGetRevIntgHelper")
public class Gstr2BGetRevIntgHelper {

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	Get2BErpConfigRequestRepository get2BErpConfigRequestRepo;

	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	private Gstr2BMonitorTagging2ARepository monitorTaggingRepo;
	
	@Autowired
	@Qualifier("Gstr2BTaggingServiceImpl")
	private Gstr2BTaggingServiceImpl gstr2BTaggingServiceImpl;

	public void postRevIntgJob(Long invocationId, String gstin,
			String taxPeriod) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get 2B Json processed and going for erp check");
			}

			Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
					APIConstants.GSTR2B_TRANSACT_REV_INT);
			// Assuming it as Event based job

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario id for get 2B rev integration : {}",
						scenarioId);
			}

			String sourceId = gstinSourceInfoRepository.findByGstin(gstin);

			if (sourceId == null) {

				LOGGER.error(
						"sourceId {} is not configured for group {},"
								+ "Hence reverse integartion job is not posted for {}",
						APIConstants.GSTR2B_TRANSACT_REV_INT,
						TenantContext.getTenantId(), gstin);

				return;
			}

			Long erpId = erpInfoEntityRepository.getErpId(sourceId);

			if (erpId == null) {

				LOGGER.error(
						"erpId {} is not configured for group {},"
								+ "Hence reverse integartion job is not posted for {}",
						APIConstants.GSTR2B_TRANSACT_REV_INT,
						TenantContext.getTenantId(), gstin);

				return;
			}

			ErpEventsScenarioPermissionEntity scenarioPermision = erpEventsScenPermissionRepo
					.findByScenarioIdAndErpIdAndIsDeleteFalse(scenarioId,
							erpId);

			if (scenarioPermision != null) {
				// Code to generate trigger/ create async job for
				// ERP
				// reverse integration.

				String taxPeriodConv = String.valueOf(
						GenUtil.getReturnPeriodFromTaxPeriod(taxPeriod));
				Get2BErpConfigRequestEntity get2BErpReq = new Get2BErpConfigRequestEntity();
				get2BErpReq.setGstinList(gstin);
				get2BErpReq.setReturnPeriodList(taxPeriodConv);
				get2BErpReq.setItc("ALL");
				get2BErpReq.setCreatedBy("SYSTEM");
				LocalDateTime schedTime = LocalDateTime.now();
				get2BErpReq.setCreatedOn(schedTime);
				get2BErpReq.setInvocationId(invocationId);
				get2BErpReq.setStatus(APIConstants.INITIATED);
				
				get2BErpReq = get2BErpConfigRequestRepo
						.save(get2BErpReq);
				
				Get2BRevIntReqDto erpReqDto = new Get2BRevIntReqDto();
				erpReqDto.setGstin(gstin);
				erpReqDto.setRetPeriod(taxPeriodConv);
				erpReqDto.setScenarioId(scenarioId);
				erpReqDto.setRequestId(get2BErpReq.getRequestId());
				erpReqDto.setErpId(scenarioPermision.getErpId());
				erpReqDto.setGroupCode(TenantContext.getTenantId());
				erpReqDto.setDestinationName(scenarioPermision.getDestName());
				erpReqDto.setInvocationId(get2BErpReq.getInvocationId());
				String erpReqJson = gson.toJson(erpReqDto);
				
				get2BErpReq.setReqPayload(erpReqJson);
				get2BErpReq = get2BErpConfigRequestRepo
						.save(get2BErpReq);

				
//				asyncJobsService.createJob(TenantContext.getTenantId(),
//						JobConstants.GSTR2B_GET_REV_INTG, erpReqJson,
//						JobConstants.SYSTEM, JobConstants.PRIORITY,
//						JobConstants.PARENT_JOB_ID,
//						JobConstants.SCHEDULE_AFTER_IN_MINS);

				LOGGER.debug(
						"GSTR2b Reverse Integration JOB Posted Successfully");
			}
		} catch (Exception e) {
			LOGGER.error("Exception while posting Rev Integration Job"
					+ "Gstr2BGetRevIntgHelper", e);
		}
	}

	public void persistMonitorTaggingDtls(String gstin, String taxPeriod,
			Long invocationId) {
		gstr2BTaggingServiceImpl.callGstr2a2bMonitorProc(gstin, taxPeriod,
				invocationId, APIConstants.GSTR2B, APIConstants.GSTR2B_GET_ALL);
	}
}
