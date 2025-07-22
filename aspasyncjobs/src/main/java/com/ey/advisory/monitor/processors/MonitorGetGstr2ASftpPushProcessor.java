package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.services.jobs.erp.GetGstr2ASftpFailedFilePushHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MonitorGetGstr2ASftpPushProcessor")
public class MonitorGetGstr2ASftpPushProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private GetGstr2ASftpFailedFilePushHandler sftpHandler;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			Map<String, Config> configMap = configManager
					.getConfigs("SFTP_PUSH", "get.gstr2A.sftp.groupCodes");
			String groupCodesOptedSFTPpush = configMap
					.get("get.gstr2A.sftp.groupCodes") != null
							? configMap.get("get.gstr2A.sftp.groupCodes")
									.getValue()
							: null;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("groupcodes ofted for SFTP push {}",
						groupCodesOptedSFTPpush);
			}
			List<String> groupCodeList = new ArrayList<>();
			if (groupCodesOptedSFTPpush != null)

			{
				groupCodeList = new ArrayList<String>(
						Arrays.asList(groupCodesOptedSFTPpush.split(",")));

			}

			if (groupCodeList.contains(TenantContext.getTenantId())) {
				checkForSFTPReverseIntegration();
			} else {
				LOGGER.error(
						"The group code {} is not configured for GetGstr2A SFTP push ",
						TenantContext.getTenantId());
			}

		} catch (Exception ex) {
			LOGGER.error("Exception occured in monitor getGstr2A sftp {} ", ex);
			throw new AppException(ex);

		}

	}

	private void checkForSFTPReverseIntegration() {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside SFTP job push block");
		}

		String groupCode = TenantContext.getTenantId();

		Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
				JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("scenario id {}", scenarioId.toString());
		}
		if (scenarioId == null) {

			LOGGER.error(
					"Scenario {} is not configured for group {},"
							+ "Hence reverse SFTP Push job is not posted",
					JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP,
					TenantContext.getTenantId());
			return;
		}

		ErpEventsScenarioPermissionEntity scenarioPermision = erpEventsScenPermissionRepo
				.findByScenarioIdAndIsDeleteFalse(scenarioId);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("scenario permission {}", scenarioPermision);
		}

		if (scenarioPermision == null) {

			LOGGER.error("SFTP permission {} is not configured for group {},"
					+ "Hence SFTP reverse push job is " + "not posted for {}",
					JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP,
					TenantContext.getTenantId());
			return;
		}

		/*
		 * Push the failed erpbatchIds file only if the Reverse integration is
		 * on-boarded - Client level
		 */

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Submitting SFTP Push Job for groupCode :{}", groupCode);
			LOGGER.debug(msg);
		}

		sftpHandler.handleFailedBatches(groupCode);

	}
}
