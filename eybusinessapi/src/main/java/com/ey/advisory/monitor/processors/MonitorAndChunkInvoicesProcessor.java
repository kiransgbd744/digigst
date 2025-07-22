package com.ey.advisory.monitor.processors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.AutoDraftAttributeConfig;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.MonitorCommonUtility;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorAndChunkInvoicesProcessor")
public class MonitorAndChunkInvoicesProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	@Autowired
	private MonitorCommonUtility monCommUtility;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	final List<String> apiType = ImmutableList
			.of(BusinessCriticalConstants.GENEINV_V3);

	final List<String> cnlapiType = ImmutableList
			.of(BusinessCriticalConstants.CANEINV_V3);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			List<AutoDraftAttributeConfig> activeCombList = monCommUtility
					.isEligibleForAutoDrafting();
			if (activeCombList == null) {
				return;
			}
			Map<String, Config> configMap = configManager.getConfigs("BCAPI",
					"auto.drafting", TenantContext.getTenantId());

			String invChunkSize = configMap != null
					&& configMap.get("auto.drafting.chunkSize.geneinv") != null
							? configMap.get("auto.drafting.chunkSize.geneinv")
									.getValue()
							: String.valueOf(100);

			Integer invChunkSizeInt = Integer.valueOf(invChunkSize) / 2;

			for (int i = 0; i < activeCombList.size(); i++) {

				String companyCode = activeCombList.get(i).getCompanyCode();
				String sourceId = activeCombList.get(i).getSourceId();

				LOGGER.debug("CompanyCode {} and SourceId {}", companyCode,
						sourceId);
				Pageable pageReq = PageRequest.of(0, invChunkSizeInt,
						Direction.ASC, "id");

				Pageable einvPageReq = null;

				List<ERPRequestLogEntity> cancelReqPayload = logAdvRepo
						.findByApiTypeInAndIsDuplicateFalseAndNicRawRespTimestampIsNotNullAndBatchIdIsNullAndCompanyCodeAndSourceId(
								cnlapiType, companyCode, sourceId, pageReq);

				int cancelSize = cancelReqPayload.size();
				if (cancelSize == invChunkSizeInt) {

					einvPageReq = PageRequest.of(0, invChunkSizeInt,
							Direction.ASC, "id");
				} else {
					invChunkSizeInt = Integer.valueOf(invChunkSize)
							- cancelSize;
					einvPageReq = PageRequest.of(0, invChunkSizeInt,
							Direction.ASC, "id");
				}

				List<ERPRequestLogEntity> getSuccessReqPayloads = logAdvRepo
						.findByApiTypeInAndIsDuplicateFalseAndNicStatusTrueAndBatchIdIsNullAndCompanyCodeAndSourceId(
								apiType, companyCode, sourceId, einvPageReq);

				if (getSuccessReqPayloads.isEmpty()
						&& cancelReqPayload.isEmpty()) {
					String msg = String.format(
							"No Invoices are available to Push to Cloud for Group-%s",
							group.getGroupCode());
					LOGGER.debug(msg);
					continue;
				} else {
					getSuccessReqPayloads.addAll(cancelReqPayload);
				}

				String batchId = monCommUtility.createAndPresistBatch(
						getSuccessReqPayloads, getSuccessReqPayloads.size());
				List<Long> idList = getSuccessReqPayloads.stream()
						.map(o -> o.getId()).collect(Collectors.toList());
				logAdvRepo.updateBatchIds(batchId, idList);
				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("batchId", batchId);
				jobParams.addProperty("companyCode", companyCode);
				jobParams.addProperty("sourceId", sourceId);
				jobParams.addProperty("apiIdentifier", "GEN");
				asyncJobsService.createJob(group.getGroupCode(),
						"PushInvoicesToCloud", jobParams.toString(), "SYSTEM",
						1L, null, null);
			}
		} catch (Exception ex) {
			String msg = "Exception occured in periodic job of BcAPI";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

}
