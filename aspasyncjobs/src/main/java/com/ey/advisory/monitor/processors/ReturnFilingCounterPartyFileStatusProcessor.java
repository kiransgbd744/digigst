/**
 * 
 */
package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.service.vendor.compliance.ReturnFilingCounterPartyFileStatusHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Component("ReturnFilingCounterPartyFileStatusProcessor")
public class ReturnFilingCounterPartyFileStatusProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private ReturnFilingCounterPartyFileStatusHandler handler;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		String groupcode = group.getGroupCode();
		LOGGER.debug(
				"About to Fetch Vendor Return Filling Status for group {}",
				groupcode);
		try {
			handler.loadAndPersistFillingStatus();
			LOGGER.debug(
					"Fetched and Persisted Vendor Return Filling Status for group {}",
					groupcode);
		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error("Exception while Fetching Vendor Filling status", ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

}
