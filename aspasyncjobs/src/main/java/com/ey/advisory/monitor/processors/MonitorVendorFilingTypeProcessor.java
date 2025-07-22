package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.VendorGstinFilingTypeService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Component("MonitorVendorFilingTypeProcessor")
@Slf4j
public class MonitorVendorFilingTypeProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("VendorGstinFilingTypeServiceImpl")
	private VendorGstinFilingTypeService filingTypeService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			String groupCode = group.getGroupCode();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Executing MonitorVendorFilingTypeProcessor"
								+ " job" + ".executeForGroup()"
								+ " method for group: '%s'", groupCode);
				LOGGER.debug(logMsg);
			}
			
			filingTypeService.stampVendorFilingType();
		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

}
