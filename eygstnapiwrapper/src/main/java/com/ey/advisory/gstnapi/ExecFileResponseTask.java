/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.javatuples.Pair;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.ProcessingStatus;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sai K Pakanati
 *
 */
@Getter
@Slf4j
public class ExecFileResponseTask implements Runnable {

	private ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap;
	private CountDownLatch latch;
	private APIParams apiParams;
	private Long requestId;
	private String fileNum;
	private String groupCode;

	public ExecFileResponseTask(String fileNum,
			ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap,
			CountDownLatch latch, APIParams apiParams, Long requestId,
			String groupCode) {
		super();
		this.tasksMap = tasksMap;
		this.latch = latch;
		this.apiParams = apiParams;
		this.requestId = requestId;
		this.fileNum = fileNum;
		this.groupCode = groupCode;
	}

	@Override
	public void run() {
		TenantContext.setTenantId(groupCode);
		try {
			APIParams clonedParams = (APIParams) GenUtil.deepCopy(apiParams);

			if (APIIdentifiers.GSTR8A_GETDETAILS
					.equalsIgnoreCase(clonedParams.getApiIdentifier())) {
				clonedParams.addApiParam(new APIParam("docid", fileNum));
			} else {
				clonedParams.addApiParam(new APIParam("file_num", fileNum));
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to Execute ExecFileResponseTask for APIParams {}",
						clonedParams);
			APIExecutor apiExecutor = StaticContextHolder
					.getBean("DefaultGSTNAPIExecutor", APIExecutor.class);
			APIResponse apiResponse = apiExecutor.execute(clonedParams, null);
			if (apiResponse.isSuccess()) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Received Successful response from GSTN "
									+ "& About to save the Response for APIParams - {}",
							apiParams);
				saveResponse(apiResponse.getResponse());
				tasksMap.put(fileNum,
						new Pair<>(ProcessingStatus.COMPLETED, null));
			} else {
				String errMsg = String.format(
						"Received Error eResponse from GSTN, Response is %s",
						apiResponse.getErrors().toString());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

		} catch (Exception ex) {
			// Set the status of the job as failed in the concurrent hash map.
			// Capture the exception for later logging.
			tasksMap.put(fileNum, new Pair<>(ProcessingStatus.FAILED, ex));
			String msg = String.format(
					"MultiTenantTaskProcessor for "
							+ "processing message  for url: '%s' failed!!",
					apiParams.toString());
			LOGGER.error(msg, ex);

		} finally {
			latch.countDown();
			TenantContext.clearTenant();
		}

	}

	private void saveResponse(String decryptedResp) {
		try {
			APIResponseRepository respRepo = StaticContextHolder.getBean(
					"APIResponseRepository", APIResponseRepository.class);
			Clob responseClob = new javax.sql.rowset.serial.SerialClob(
					decryptedResp.toCharArray());
			APIResponseEntity responseEntity = new APIResponseEntity(requestId,
					responseClob, GstnApiWrapperConstants.SUCCESS,
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now(),
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now());
			respRepo.save(responseEntity);
		} catch (Exception e) {
			String msg = "Exception while persisting File Response Data";
			LOGGER.error(msg, e);
		}

	}

}
