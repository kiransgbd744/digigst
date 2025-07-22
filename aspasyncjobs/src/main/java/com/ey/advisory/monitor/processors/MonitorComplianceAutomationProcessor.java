package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beust.jcommander.internal.Lists;
import com.ey.advisory.admin.data.entities.client.ComplianceAutomationEntity;
import com.ey.advisory.admin.data.entities.client.ComplianceAutomationLoggerEntity;
import com.ey.advisory.admin.data.repositories.client.ComplianceAutomationLogRepository;
import com.ey.advisory.admin.data.repositories.client.ComplianceAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.services.noncomplaintvendor.ComplaintClientCommunicationServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Slf4j
@Service("MonitorComplianceAutomationProcessor")
public class MonitorComplianceAutomationProcessor
		extends DefaultMultiTenantTaskProcessor {

	private static final String SUBMITTED = "SUBMITTED";
	private static final String IN_PROGRESS = "InProgress";
	private static final String COMPLETED = "Completed";

	@Autowired
	private ComplianceAutomationRepository complianceAutomationRepository;

	@Autowired
	private ComplaintClientCommunicationServiceImpl clientCommunicationServiceImpl;

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty clientFilingStatusRepositoty;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	@Qualifier("ComplianceAutomationLogRepository")
	private ComplianceAutomationLogRepository complianceAutomationLogRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Executing Monitoring"
							+ " MonitorComplianceAutomationProcessor"
							+ ".executeForGroup()  method for group: '%s'",
					group.getGroupCode());
			LOGGER.debug(logMsg);
		}

		createCompliancePollingTasks(message, group.getGroupCode());

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Completed one cycle of periodic Monitoring"
							+ " job for ComplianceAutomation group '%s'",
					group.getGroupCode());
			LOGGER.debug(logMsg);
		}

	}

	private ComplianceAutomationLoggerEntity buildLogMessage(String returnType,
			String gstin, String logMsg, String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		ComplianceAutomationLoggerEntity loggerEntity = new ComplianceAutomationLoggerEntity();
		loggerEntity.setReturnType(returnType);
		loggerEntity.setGstin(gstin);
		loggerEntity.setLog(logMsg);
		loggerEntity.setType(type);
		loggerEntity.setCreatedOn(now);
		loggerEntity.setCreatedBy("SYSTEM");
		loggerEntity.setModifiedBy("SYSTEM");
		loggerEntity.setModifiedOn(now);
		return loggerEntity;
	}

	@Transactional(value = "clientTransactionManager")
	public void createCompliancePollingTasks(Message message,
			String groupCode) {
		List<ComplianceAutomationLoggerEntity> logList = Lists.newLinkedList();

		Optional<ClientFilingStatusEntity> clientFillingStatusEntity = Optional
				.empty();

		// List<ClientFilingStatusEntity> clientFillingStatusEntity =
		// Lists.newLinkedList();

		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Fetching ComplianceAutomationEntity "
								+ "but ComplianceAutomation yet to happen");
				LOGGER.debug(logMsg);
				// logList.add(buildLogMessage(null, null, logMsg));
			}
			/*String userName = SecurityContext.getUser()
					.getUserPrincipalName();*/
			String logMsg1 = "Fetching ComplianceAutomationEntity";
			logList.add(buildLogMessage(null, null, logMsg1, "INF"));
			TenantContext.setTenantId(groupCode);

			List<ComplianceAutomationEntity> complianceEntities = complianceAutomationRepository
					.findByIsDeleteFalseOrderByEntityIdDesc();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Fetched ComplianceAutomationEntity list"
								+ " for which Polling job will be created");
				LOGGER.debug(logMsg);

			}
			String logMsg2 = "ComplianceAutomationEntity fetched";
			logList.add(buildLogMessage(null, null, logMsg2, "INF"));

			if (CollectionUtils.isNotEmpty(complianceEntities)) {

				for (ComplianceAutomationEntity entity : complianceEntities) {

					String returnType = entity.getReturnType();
					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"Compliance AUTO GET call initiated for the Entity is %s",
								entity);
						LOGGER.debug(logMsg);

					}
					String logMsg3 = "Compliance AUTO GET call initiated";
					logList.add(
							buildLogMessage(returnType, null, logMsg3, "INF"));

					String cronExpression = entity.getCronExpression();

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"cronExpression: %s, returnType id: %s, lastPostedDate: %s",
								cronExpression, entity.getReturnType(),
								entity.getLastPostedDate());
						LOGGER.debug(logMsg);

					}

					if (cronExpression != null && CronExpression.isValidExpression(cronExpression)) {

						LocalDateTime lastPostedDate = entity
								.getLastPostedDate();
						if (lastPostedDate == null) {
							lastPostedDate = LocalDateTime.now().minusDays(1);
						}
						LocalDateTime nextScheduledTime = getNextScheduledTime(
								cronExpression, lastPostedDate);

						if (LocalDateTime.now()
								.compareTo(nextScheduledTime) > 0) {
							if (LOGGER.isDebugEnabled()) {
								String logMsg = String.format(
										"Daily ComplianceAutomation Job frequency is matched to execute for combination is %s",
										entity);
								LOGGER.debug(logMsg);
								logList.add(buildLogMessage(returnType, null,
										logMsg, "INF"));
							}
							String logMsg4 = "Job frequency is matched";
							logList.add(buildLogMessage(returnType, null,
									logMsg4, "INF"));
							
							
							

							// Fetch all the GSTIN's for the entity
							List<String> gstinIds = Lists.newArrayList();
							gstinIds.addAll(gstnDetailRepo
									.findgstinByEntityId(entity.getEntityId()));

							for (String gstinStr : gstinIds) {
								String gstin = gstinStr.toString();

								ClientFilingStatusEntity clientFillingStatusEntity1 = clientFilingStatusRepositoty
										.findByFinancialYearAndGstinAndReturnType(
												getCurrentFinancialYear(),
												gstin, entity.getReturnType());
								if (clientFillingStatusEntity1 == null) {
									clientFillingStatusEntity1 = new ClientFilingStatusEntity(
											entity.getReturnType(), gstin,
											getCurrentFinancialYear(),
											SUBMITTED, "SYSTEM", "SYSTEM");
									clientFillingStatusEntity1 = clientFilingStatusRepositoty
											.save(clientFillingStatusEntity1);
								}

								else {
									LocalDateTime now = LocalDateTime.now();
									clientFilingStatusRepositoty
											.updatRecordById(message.getId(),
													now);

								}
								PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
										PublicApiConstants.MON_COMP_HISTORY_RET);

								clientCommunicationServiceImpl
										.persistGstnApiForSelectedFinancialYear(
												getCurrentFinancialYear(),
												clientFillingStatusEntity1
														.getGstin());
								clientFillingStatusEntity1
										.setStatus("COMPLETED");
								clientFillingStatusEntity1
										.setModifiedOn(LocalDateTime.now());
								clientFilingStatusRepositoty
										.save(clientFillingStatusEntity1);

								JsonObject jsonParams = new JsonObject();
								jsonParams.addProperty("gstin",
										clientFillingStatusEntity1.getGstin());
								jsonParams.addProperty("section",
										clientFillingStatusEntity1
												.getReturnType());
								jsonParams.addProperty("finYear",
										clientFillingStatusEntity1
												.getFinancialYear());
								jsonParams.addProperty("scenarioName",
										JobConstants.COMP_HISTORY_REV);

								asyncJobsService.createJob(groupCode,
										JobConstants.COMP_HISTORY_REV,
										jsonParams.toString(), "SYSTEM",
										JobConstants.PRIORITY, message.getId(),
										JobConstants.SCHEDULE_AFTER_IN_MINS);

								if (LOGGER.isDebugEnabled()) {
									String logMsg = String.format(
											"Daily ComplianceAutomation Job is created for combination is %s",
											entity);
									LOGGER.debug(logMsg);

								}
								String logMsg5 = "ComplianceAutomation Job is created";
								logList.add(buildLogMessage(returnType, gstin,
										logMsg5, "INF"));

								updateJobStatus(entity, message, logList);
							}
						} else {
							if (LOGGER.isDebugEnabled()) {
								String logMsg = String.format(
										"Daily ComplianceAutomation Job frequency is not matched to execute for combination is %s",
										entity);
								LOGGER.debug(logMsg);

							}
							String logMsg6 = "ComplianceAutomation Job is not matched";
							logList.add(buildLogMessage(returnType, null,
									logMsg6, "ERR"));
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							String logMsg = String.format(
									"Cron expression is not matching %s",
									entity);
							LOGGER.debug(logMsg);

						}

					}

				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format("%s",
							"There are no eligible ComplianceAutomationEntity for Polling");
					LOGGER.debug(logMsg);

				}
				String logMsg7 = "There are no eligible ComplianceAutomationEntity for Polling";
				logList.add(buildLogMessage(null, null, logMsg7, "ERR"));
			}
		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			String logMsg = String.format(
					"Exception while processing the compliance automation filing request: %s",
					ex);
			LOGGER.debug(logMsg);

			if (clientFillingStatusEntity.isPresent()) {
				ClientFilingStatusEntity entity = clientFillingStatusEntity
						.get();
				entity.setStatus("Failed");
				entity.setModifiedOn(LocalDateTime.now());
				clientFilingStatusRepositoty.save(entity);

			}
			String logMsg8 = "Exception while processing the compliance automation filing request";
			logList.add(buildLogMessage(null, null, logMsg8, "ERR"));
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		} finally {
			if (!CollectionUtils.isEmpty(logList)) {
				complianceAutomationLogRepository.saveAll(logList);
			}
		}
	}

	private void updateJobStatus(ComplianceAutomationEntity entity,
			Message message, List<ComplianceAutomationLoggerEntity> logList) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		entity.setJobId(message.getId());
		entity.setLastPostedDate(now);
		complianceAutomationRepository.save(entity);
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Daily ComplianceAutomation Job is successfully completed and updated the status of the job for returnType is %s,jobPostedDate is %s",
					entity.getReturnType(), entity.getLastPostedDate());
			LOGGER.debug(logMsg);
			logList.add(buildLogMessage(entity.getReturnType(), null, logMsg,
					"INF"));
		}
		String logMsg9 = "ComplianceAutomation Job is successfully completed";
		logList.add(
				buildLogMessage(entity.getReturnType(), null, logMsg9, "INF"));
	}

	private static LocalDateTime getNextScheduledTime(String cron,
			LocalDateTime lastJobstartDate) {

		CronExpression cronExpression = CronExpression.parse(cron);
		return cronExpression.next(lastJobstartDate);

	}

	private static String getCurrentFinancialYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String financialYear;
		if (month < 4) {
			financialYear = (year - 1) + "-"
					+ String.valueOf(year).substring(2, 4);
		} else {
			financialYear = (year) + "-"
					+ String.valueOf(year + 1).substring(2, 4);

		}
		return financialYear;
	}

}
