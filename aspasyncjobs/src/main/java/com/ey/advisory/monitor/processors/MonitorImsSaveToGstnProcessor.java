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
import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.app.service.ims.ImsSaveJobQueueEntity;
import com.ey.advisory.app.service.ims.ImsSaveToGstnJobHandler;
import com.ey.advisory.app.service.ims.ImsSaveToGstnReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("MonitorImsSaveToGstnProcessor")
public class MonitorImsSaveToGstnProcessor extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Autowired
	private ImsSaveJobQueueRepository queueRepo;

	@Autowired
	private ImsSaveToGstnJobHandler jobHandler;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	final List<String> statusList = ImmutableList.of("In Queue", "InProgress","RefId Generated");

	@Override
	public void executeForGroup(Group group, Message message, AppExecContext ctx) {
		try {

			List<ImsSaveJobQueueEntity> entities = queueRepo.findAllActiveData(statusList);
			Set<String> allGstins = new HashSet<String>();

			Map<String, List<ImsSaveJobQueueEntity>> map = new HashMap<>();
			for (ImsSaveJobQueueEntity entity : entities) {
				allGstins.add(entity.getGstin());
				map.computeIfAbsent(entity.getGstin(), k -> new ArrayList<>()).add(entity);
			}

			List<String> gstins = getAllActiveGstnList(new ArrayList<>(allGstins));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("activeGstins {} ", gstins);
			}

			for (String gstin : gstins) {
				List<ImsSaveJobQueueEntity> queueEntities = map.get(gstin);
				Long count = queueEntities.stream().
						filter(o -> (o.getStatus().equalsIgnoreCase("InProgress") ||
								o.getStatus().equalsIgnoreCase("RefId Generated"))).count();
				if (count > 0) {
					continue;
				} else {
					ImsSaveJobQueueEntity queue = queueEntities.get(0);
					ImsSaveToGstnReqDto dto = new ImsSaveToGstnReqDto();
					List<String> gstinList = new ArrayList<>();
					gstinList.add(gstin);
					List<String> tableTypes = new ArrayList<>();
					String tableType = queue.getSection();
					tableTypes.add(tableType);
					dto.setGstins(gstinList);
					dto.setTableType(tableTypes);
					String action = queue.getAction();
					String groupCode = TenantContext.getTenantId();
					jobHandler.createJobs(gstin, dto, groupCode, tableType, action);
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

	private List<String> getAllActiveGstnList(List<String> uniqueActiveGstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for Ims save");
		}

		List<String> activeGstins = new ArrayList<>();
		List<String> inActiveGstins = new ArrayList<>();
		if (!uniqueActiveGstins.isEmpty()) {
			for (String gstin : uniqueActiveGstins) {
				String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
				if (authStatus.equals("A")) {
					activeGstins.add(gstin);
				} else {
					inActiveGstins.add(gstin);
				}
			}
		}
		return activeGstins;
	}
}
