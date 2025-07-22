package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aAmdhistHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingAmdhistHeaderEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aAmdhistRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingAmdhistRepository;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr2aGetAmdhistSuccessHandler")
@Slf4j
public class Gstr2aGetAmdhistSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr2aAmdhistDataParserImpl")
	private Gstr2aAmdhistDataParser gstr2aAmdhistDataParser;

	@Autowired
	private GetGstr2aAmdhistRepository getGstr2aAmdhistRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private GetGstr2aStagingAmdhistRepository getGstr2aStagingAmdhistRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();

			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			String apiSection = dto.getApiSection();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr2aGetAmdhistSuccessHandler.class",
						batchId);
			}
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (APIConstants.GSTR2A.equalsIgnoreCase(apiSection)) {
				/*
				 * boolean isFDeltaGetData = false; GSTNDetailEntity gstinInfo =
				 * gstnRepo .findByGstinAndIsDeleteFalse(dto.getGstin());
				 * EntityConfigPrmtEntity entityConfig = entityConfigRepo
				 * .findByGroupCodeAndEntityIdAndparamkryId( dto.getGroupcode(),
				 * gstinInfo.getEntityId(), "I25"); String paramValue =
				 * entityConfig!= null ? entityConfig.getParamValue() : "A"; if
				 * ("B".equals(paramValue)) { isFDeltaGetData = true; }
				 */
				boolean isFDeltaGetData = true;
				if (!isFDeltaGetData) {
					handleUserGetGstr2a(resultIds, dto, batchId);
				} else {
					handleStagingGetGstr2a(resultIds, dto, batchId);

					// Proc call to insert the Delta data into original tables
					docRepository.getGstr2aProcCall(dto.getGstin(),
							dto.getReturnPeriod(), dto.getType(), batchId,
							dto.getFromTime() != null ? true : false);
					String gstin = dto.getGstin();
					GSTNDetailEntity gstinInfo = gstinDetailRepo
							.findByGstinAndIsDeleteFalse(gstin);
					Long gstinId = gstinInfo.getId();
					Long scenarioId = scenarioMasterRepo
							.findSceIdOnScenarioName(
									JobConstants.NEW_GSTR2A_GET_REV_INTG);
					// Assuming it as Event based job
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
							erpReqDto.setBatchId(batchId);
							erpReqDto.setDestinationName(
									scenarioPermision.getDestName());
							erpReqDto.setErpId(scenarioPermision.getErpId());
							String erpReqJson = gson.toJson(erpReqDto);
							AsyncExecJob job = asyncJobsService.createJob(
									TenantContext.getTenantId(),
									JobConstants.NEW_GSTR2A_GET_REV_INTG,
									erpReqJson, JobConstants.SYSTEM,
									JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);
						}
					}
				}
			} else {
				// handleErpGetGstr2a(resultIds, dto, batchId);
			}
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			throw new APIException(e.getLocalizedMessage());
		}
	}

	private void handleUserGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		Long userRequestId = dto.getUserRequestId();
		String parentSection = dto.getParentSection().toUpperCase();
		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.AMDHIST.equalsIgnoreCase(dto.getType())) {
				GetAnx1BatchEntity parentBatch = batchRepo
						.findByUserRequestIdAndTypeAndIsDeleteFalse(
								userRequestId, parentSection);
				getGstr2aAmdhistRepository
						.softlyDeleteByBatchIdNotInAndParentSection(
								parentBatch.getId(), parentSection, now);
			}
		}

		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			if (APIConstants.AMDHIST.equalsIgnoreCase(dto.getType())) {

				List<GetGstr2aStagingAmdhistHeaderEntity> amdEntities = gstr2aAmdhistDataParser
						.parseAmdData(dto, apiResp, dto.getType(), batchId,
								now);
				if(amdEntities == null) {
					return;
				}
				Type listType = new TypeToken<List<GetGstr2aAmdhistHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aAmdhistHeaderEntity> postDtoList = modelmapper
						.map(amdEntities, listType);
				postDtoList.forEach(postDto -> {
					postDto.setDeltaInStatus(APIConstants.N);
				});
				if (!amdEntities.isEmpty()) {
					getGstr2aAmdhistRepository.saveAll(postDtoList);
				}
			}

		});

	}

	private void handleStagingGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		Long userRequestId = dto.getUserRequestId();
		String parentSection = dto.getParentSection().toUpperCase();
		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.AMDHIST.equalsIgnoreCase(dto.getType())) {

				GetAnx1BatchEntity parentBatch = batchRepo
						.findByUserRequestIdAndTypeAndIsDeleteFalse(
								userRequestId, parentSection);
				getGstr2aStagingAmdhistRepository
						.softlyDeleteByBatchIdNotInAndParentSection(
								parentBatch.getId(), parentSection, now);
			}
		}

		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			if (APIConstants.AMDHIST.equalsIgnoreCase(dto.getType())) {

				List<GetGstr2aStagingAmdhistHeaderEntity> amdEntities = gstr2aAmdhistDataParser
						.parseAmdData(dto, apiResp, dto.getType(), batchId,
								now);
				if (!amdEntities.isEmpty()) {
					getGstr2aStagingAmdhistRepository.saveAll(amdEntities);
				}
			}

		});
	}
}
