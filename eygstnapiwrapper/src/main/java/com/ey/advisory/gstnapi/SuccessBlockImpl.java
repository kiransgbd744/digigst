package com.ey.advisory.gstnapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("SuccessBlockImpl")
public class SuccessBlockImpl implements SuccessBlock {

	@Autowired
	APIInvocationReqRepository aiReqRepo;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public boolean successTask(ExecResult<Boolean> execResult,
			ExecResult<Boolean> handlerResult, Map<String, Object> map) {

		APIInvocationReqEntity requestEntity = map
				.containsKey(RetryMapKeysConstants.API_INVOCATION_REQ_ENTITY)
						? (APIInvocationReqEntity) map
								.get(RetryMapKeysConstants.API_INVOCATION_REQ_ENTITY)
						: null;
		if (requestEntity == null) {
			String errMsg = String
					.format("Couldn't find the InvocReqEntity, Map is %s", map);
			LOGGER.error(errMsg);
			return false;
		}

		if (map.containsKey("fileCount")) {
			if (map.containsKey("isDataChanged")) {
				if (Boolean.TRUE.equals(
				        Boolean.getBoolean(map.get("isDataChanged").toString()))) {
					return true;
				} else
					createJobForFileCountResponse(map, requestEntity);
				return true;

			} else
				createJobForFileCountResponse(map, requestEntity);

			return true;
		}
		
		if (map.containsKey("token")) {
				createJobForTokenResponse(map, requestEntity);
			 			return true;
			 		}
		
		
		if (map.containsKey("fileCount")) {
			createJobForFileCountResponse(map, requestEntity);
			return true;
		}
		aiReqRepo.updateStatus(GstnApiWrapperConstants.SUCCESS,
				requestEntity.getId());
		return true;

	}

	private void createJobForTokenResponse(Map<String, Object> map,
			APIInvocationReqEntity requestEntity) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = GstnApiWrapperConstants.SYSTEM;
		String groupCode = TenantContext.getTenantId();
		JsonObject jsonParams = new JsonObject();
		jsonParams.add("requestId", gson.toJsonTree(requestEntity.getId()));
		jsonParams.add("requestEntity", gson.toJsonTree(requestEntity));
		jsonParams.add("tokenParams", gson.toJsonTree(map.get("tokenParams")));
		asyncJobsService.createJob(groupCode,
				GstnApiJobConstants.PROCESS_TOKEN_RESPONSE,
				jsonParams.toString(), userName, 1L, null,
				Long.valueOf(map.get("est").toString()));
	}

	private void createJobForFileCountResponse(Map<String, Object> map,
			APIInvocationReqEntity requestEntity) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = GstnApiWrapperConstants.SYSTEM;
		String groupCode = TenantContext.getTenantId();
		JsonObject jsonParams = new JsonObject();
		jsonParams.add("requestId", gson.toJsonTree(requestEntity.getId()));
		jsonParams.add("requestEntity", gson.toJsonTree(requestEntity));
		jsonParams.addProperty("fileCount", Integer.valueOf(map.get("fileCount").toString()));
		asyncJobsService.createJob(groupCode,
				GstnApiJobConstants.PROCESS_FILE_RESPONSE,
				jsonParams.toString(), userName, 1L, null, 1L);
	}
}
