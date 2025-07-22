/**
 * This class is a job responsible for calling the gstnapi and store the 
 * response into the api response table after which it will execute the 
 * successHandler or faiuerHandler (Beans provided by caller) by passing the 
 * response to these handlers.
 */
package com.ey.advisory.gstnapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("APIInvocationProcessor")
@Slf4j
public class APIInvocationProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("RetryLoopImpl")
	private RetryLoop retryLoop;

	@Autowired
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;
	
	@Autowired
	@Qualifier("APIInvocationReqRepository")
	APIInvocationReqRepository apiInvocationReqRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Inside ApiInvocation Processor with"
						+ " request = '%s' ", jsonString);
				LOGGER.debug(msg);
			}
			retryLoop.retryLoop(GstnApiWrapperConstants.APIINVOCATIONRETRYBLOCK,
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
