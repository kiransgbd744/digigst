package com.ey.advisory.app.services.jobs.gstr6a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.google.gson.Gson;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Gstr6aCommonUtility")
public class Gstr6aCommonUtility {

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	public void post6ARevIntgJob(Gstr6aGetInvoicesReqDto dto) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		String gstin = dto.getGstin();
		GSTNDetailEntity gstinInfo = gstinDetailRepo
				.findByGstinAndIsDeleteFalse(gstin);
		Long gstinId = gstinInfo.getId();
		Long scenarioId = scenarioMasterRepo
				.findSceIdOnScenarioName(JobConstants.NEW_GSTR2A_GET_REV_INTG);
		
		List<ErpScenarioPermissionEntity> scenarioPermisionList = erpScenPermissionRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(
						scenarioId, gstinId);
		if (!scenarioPermisionList.isEmpty()) {
			// Code to generate trigger/ create async job for
			// ERP
			// reverse integration.
			for (ErpScenarioPermissionEntity scenarioPermision : scenarioPermisionList) {
				Get2ARevIntReqDto erpReqDto = new Get2ARevIntReqDto();
				erpReqDto.setGstin(dto.getGstin());
				erpReqDto.setRetPeriod(dto.getReturnPeriod());
				erpReqDto.setSection(dto.getType());
				erpReqDto.setBatchId(dto.getBatchId());
				erpReqDto.setDestinationName(scenarioPermision.getDestName());
				erpReqDto.setScenarioName(JobConstants.NEW_GSTR2A_GET_REV_INTG);
				erpReqDto.setErpId(scenarioPermision.getErpId());
				String erpReqJson = gson.toJson(erpReqDto);
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTR6A_GET_REV_INTG, erpReqJson,
						JobConstants.SYSTEM, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
			}
		}

	}

}
