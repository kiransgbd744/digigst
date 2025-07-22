package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("MonitorGstnBatchDtlsProcessor")
public class MonitorGstnBatchDtlsProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"MonitorGstnBatchDtlsProcessor periodic job  execution "
								+ "started for groupcode {} ",
						group.getGroupCode()));
			}

			Map<String, Config> configMap = configManager
					.getConfigs("GSTNRESET", "gstn.resetbatch.size", "DEFAULT");

			String resetSize = configMap != null
					&& configMap.get("gstn.resetbatch.size") != null
							? configMap.get("gstn.resetbatch.size").getValue()
							: String.valueOf(9);

			List<GetAnx1BatchEntity> batchDtlsList = batchRepo
					.findbatchestobeFailed(
							Arrays.asList(APIConstants.INITIATED,
									APIConstants.INPROGRESS),
							Long.valueOf(resetSize));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batches to be Failed {} ", batchDtlsList.size());
			}

			if (!batchDtlsList.isEmpty()) {
				List<Long> batchIds = batchDtlsList.stream().map(a -> a.getId())
						.collect(Collectors.toList());

				List<List<Long>> batchIdChunks = Lists.partition(batchIds, 500);

				for (List<Long> batchIdChunk : batchIdChunks) {
					batchRepo.updateBatchStatus(
							"Failed by System bcoz get call is taking more than 9 hrs to complete.",
							APIConstants.FAILED, batchIdChunk);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"MonitorGstnBatchDtlsProcessor periodic job  execution "
								+ "completed for groupcode {} ",
						group.getGroupCode()));
			}
		} catch (Exception e) {
			LOGGER.error("Error while monitoring the get call status ", e);
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}
