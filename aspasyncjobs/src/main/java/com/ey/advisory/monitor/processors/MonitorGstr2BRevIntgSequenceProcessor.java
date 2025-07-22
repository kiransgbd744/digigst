package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.Get2BErpConfigRequestEntity;
import com.ey.advisory.app.data.repositories.client.Get2BErpConfigRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("MonitorGstr2BRevIntgSequenceProcessor")
public class MonitorGstr2BRevIntgSequenceProcessor extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Autowired
	private Get2BErpConfigRequestRepository erpConfigRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	final List<String> statusList = ImmutableList.of("JOB_POSTED", APIConstants.INPROGRESS,APIConstants.INITIATED);

	final List<String> initiatedStatusList = ImmutableList.of(APIConstants.INITIATED);

	@Override
	public void executeForGroup(Group group, Message message, AppExecContext ctx) {
		try {

			List<Get2BErpConfigRequestEntity> entities = erpConfigRepo.findAllActiveData(statusList);
			Set<String> allGstinTaxPeriods = new HashSet<String>();

			Map<String, List<Get2BErpConfigRequestEntity>> map = new HashMap<>();
			for (Get2BErpConfigRequestEntity entity : entities) {
				allGstinTaxPeriods.add(entity.getGstinList()+ "|" + entity.getReturnPeriodList());
				map.computeIfAbsent(entity.getGstinList()+ "|" + entity.getReturnPeriodList(), k -> new ArrayList<>()).add(entity);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("activeGstins {} ", allGstinTaxPeriods);
			}

			for (String gstinTaxPeriod : allGstinTaxPeriods) {
				List<Get2BErpConfigRequestEntity> configEntities = map.get(gstinTaxPeriod);
				Long count = configEntities.stream().
						filter(o -> (o.getStatus().equalsIgnoreCase(APIConstants.INPROGRESS) ||
								o.getStatus().equalsIgnoreCase("JOB_POSTED"))).count();
				if (count > 0) {
					continue;
				} else {
					Get2BErpConfigRequestEntity config = erpConfigRepo.
							findTop1ByStatusInAndReturnPeriodListAndGstinListOrderByRequestIdAsc(initiatedStatusList,
									configEntities.get(0).getReturnPeriodList(),configEntities.get(0).getGstinList());
					String erpReqJson = config.getReqPayload();
					
					erpConfigRepo.updateStatusByConfigId(config.getInvocationId(),
							APIConstants.INPROGRESS);
					
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.GSTR2B_GET_REV_INTG, erpReqJson,
							JobConstants.SYSTEM, JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);
					
				}
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error("Unexpected error while executing Ims Automation Job {} ", ex);
			throw new AppException(ex, ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}
