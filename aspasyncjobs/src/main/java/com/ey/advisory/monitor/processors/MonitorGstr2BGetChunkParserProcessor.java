package com.ey.advisory.monitor.processors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.MonitorGstnGetStatusEntity;
import com.ey.advisory.app.data.repositories.client.MonitorGstnGetStatusRepository;
import com.ey.advisory.app.gstr2b.Gstr2bMonitorControlService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Saif.S
 *
 */
@Slf4j
@Component("MonitorGstr2BGetChunkParserProcessor")
public class MonitorGstr2BGetChunkParserProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("MonitorGstnGetStatusRepository")
	private MonitorGstnGetStatusRepository monitorGstnGetStatusRepo;

	@Autowired
	@Qualifier("Gstr2bMonitorControlServiceImpl")
	private Gstr2bMonitorControlService controlService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGstr2BGetChunkParserProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}
			List<MonitorGstnGetStatusEntity> monitorEntities = monitorGstnGetStatusRepo
					.findByReturnTypeAndIsBatchUpdatedFalse(
							APIConstants.GSTR2B);

			if (monitorEntities.isEmpty()) {
				LOGGER.warn(
						"There are no records in MONITOR 2B STATUS"
								+ " where isBatcherUpdated is false for group {}",
						group.getGroupCode());
				return;
			}
			controlService.monitorControlEntries(monitorEntities);
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
