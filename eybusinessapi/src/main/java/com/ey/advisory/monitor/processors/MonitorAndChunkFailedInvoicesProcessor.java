package com.ey.advisory.monitor.processors;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.AutoDraftAttributeConfig;
import com.ey.advisory.app.data.entities.client.BCAPIPushCtrlEntity;
import com.ey.advisory.app.data.entities.master.IdTokenGrpMapEntity;
import com.ey.advisory.app.data.repositories.client.BCAPIPushCtrlRepository;
import com.ey.advisory.app.data.repositories.master.IdTokenGrpMapRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.MonitorCommonUtility;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorAndChunkFailedInvoicesProcessor")
public class MonitorAndChunkFailedInvoicesProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private MonitorCommonUtility monCommUtility;

	@Autowired
	private BCAPIPushCtrlRepository bcAPIPushCtrlRepo;

	@Autowired
	private IdTokenGrpMapRepository idtokenGrpMapRepo;

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		JsonParser parser = new JsonParser();
		try {

			List<AutoDraftAttributeConfig> activeCombList = monCommUtility
					.isEligibleForAutoDrafting();
			if (activeCombList == null) {
				return;
			}
			Pageable pageReq = PageRequest.of(0, 100, Direction.ASC, "id");

			List<BCAPIPushCtrlEntity> failedEntity = bcAPIPushCtrlRepo
					.findByPushStatus(BusinessCriticalConstants.FAILED,
							pageReq);
			for (BCAPIPushCtrlEntity dtls : failedEntity) {
				LOGGER.debug(
						"About to check the status for batch id {} for groupCode {}",
						dtls.getBatchId(), TenantContext.getTenantId());
				String sapRespPayload = callSAPAPI(dtls.getBatchId(),
						TenantContext.getTenantId());
				JsonObject sapRespObj = (JsonObject) parser
						.parse(sapRespPayload);
				String statusMsg = sapRespObj.getAsJsonObject().get("status")
						.getAsString();
				LOGGER.debug("Status Recv {} ", statusMsg);
				if (APIConstants.E.equalsIgnoreCase(statusMsg)) {
					updateBatchStatus("MarkedRty", sapRespPayload,
							dtls.getBatchId(), true);
					logAdvRepo.resetAutoDraftBatch(dtls.getBatchId());
				} else {
					updateBatchStatus(BusinessCriticalConstants.SUCCESS,
							sapRespPayload, dtls.getBatchId(), false);
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured in periodic job of BcAPI";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	private String callSAPAPI(String payloadId, String groupCode)
			throws Exception {

		IdTokenGrpMapEntity idtokenGrpEntity = idtokenGrpMapRepo
				.findByGroupCode(groupCode);
		String idToken = idtokenGrpEntity.getIdToken();
		Map<String, Config> configMap = configManager.getConfigs("BCAPI",
				"auto.drafting", "DEFAULT");
		String uri = configMap != null
				&& configMap.get("auto.drafting.manageStatus") != null
						? configMap.get("auto.drafting.manageStatus").getValue()
						: null;

		Triplet<Integer, Integer, Integer> connTimeOut = monCommUtility
				.getTimeOutDtls();

		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(connTimeOut.getValue0())
				.setConnectionRequestTimeout(connTimeOut.getValue2())
				.setSocketTimeout(connTimeOut.getValue1()).build();

		HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.setHeader("idtoken", idToken);
		httpGet.setHeader("payloadId", payloadId);
		httpGet.setConfig(config);
		HttpResponse resp = httpClient.execute(httpGet);
		String apiResponse = EntityUtils.toString(resp.getEntity());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Response Received from Cloud for BatchId {} is {}",
					payloadId, apiResponse);
		}
		return apiResponse;
	}

	private void updateBatchStatus(String status, String msg, String batchId,
			boolean isRetry) {
		try {
			bcAPIPushCtrlRepo.updateBatchModfnStatus(status, msg, batchId,
					isRetry);
		} catch (Exception e) {
			String errMsg = "Exception occured while updating the status ";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}
}
