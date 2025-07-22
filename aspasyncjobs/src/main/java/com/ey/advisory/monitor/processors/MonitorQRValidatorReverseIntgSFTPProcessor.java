package com.ey.advisory.monitor.processors;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorService;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Saif.S
 *
 */
@Slf4j
@Component("MonitorQRValidatorReverseIntgSFTPProcessor")
public class MonitorQRValidatorReverseIntgSFTPProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	@Autowired
	QRResponseSummaryRepo qrRespSummRepo;

	@Autowired
	QRPDFResponseSummaryRepo qrpdfRespSummRepo;

	@Autowired
	QRPDFJSONResponseSummaryRepo qrpdfjsonRespSummRepo;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (StringUtils.isEmpty(group.getSftpUserName())
					|| StringUtils.isEmpty(group.getSftpPassword())) {
				LOGGER.error(
						"SFTP UserName/Password is not COnfigured for Group {},"
								+ " Hence not Monitoring QRValidator SFTP Folder",
						group.getGroupCode());
				return;
			}
			List<QRUploadFileStatusEntity> activeRecords = qrUploadFileStatusRepo
					.getAllReverseIntgRecords();

			if (!activeRecords.isEmpty()) {

				postJobForReverseIntegration(activeRecords, group);

			} else {
				LOGGER.error(
						"There are no active reports to be reverse"
								+ " integrated in QR Validator for group {} ",
						group.getGroupCode());
			}
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

	private void postJobForReverseIntegration(
			List<QRUploadFileStatusEntity> activeRecords, Group group) {

		JsonObject obj = new JsonObject();
		List<Long> activeIds = null;

		Map<String, List<QRUploadFileStatusEntity>> groupedMap = activeRecords
				.stream()
				.filter(record -> record.getOptionOpted() != null
						&& !record.getOptionOpted().trim().isEmpty())
				.collect(Collectors
						.groupingBy(QRUploadFileStatusEntity::getOptionOpted));

		for (Entry<String, List<QRUploadFileStatusEntity>> pair : groupedMap
				.entrySet()) {
			activeIds = pair.getValue().stream()
					.map(QRUploadFileStatusEntity::getId)
					.limit(15) // Limit the size to 15
					.collect(Collectors.toList());
			
			obj.addProperty(pair.getKey(), activeIds.toString());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MonitorQRValidatorReverseIntgSFTPProcessor Recieved FileIds to reverseInterate: {}",
					obj.toString());
		}
		asyncJobsService.createJob(group.getGroupCode(),
				JobConstants.QR_VALIDATOR_REVERSE_INTG, obj.toString(),
				"SYSTEM", 1L, null, null);
	}
}
