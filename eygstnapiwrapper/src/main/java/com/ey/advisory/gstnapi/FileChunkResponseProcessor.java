/**
 * 
 */
package com.ey.advisory.gstnapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("FileChunkResponseProcessor")
public class FileChunkResponseProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	APIExecutor apiExecutor;

	@Autowired
	APIInvocationReqRepository aiReqRepo;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Autowired
	@Qualifier("UrlListProcessorImpl")
	UrlListProcessor urlProcessor;

	@Autowired
	@Qualifier("RetryLoopImpl")
	private RetryLoop retryLoop;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Inside ApiInvocation Processor with"
						+ " request = '%s' ", jsonString);
				LOGGER.debug(msg);
			}
			retryLoop.retryLoop(
					GstnApiWrapperConstants.FILE_CHUNK_RESPONSE_RETRY_BLOCK,
					jsonString);

		} catch (Exception e) {
			String msg = String.format(
					"Error occured in InvocationReqProcessor "
							+ "for jobId = %d with message [%s]",
					message.getId(), e.getMessage());
			APILogger.logError(msg, null, e);
			throw e;

		}
	}

}
