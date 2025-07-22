/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpScenarioMasterEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.SftpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.SftpScenarioPermissionRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("RevIntegrationScenarioTriggerTaskHandler")
@Slf4j
public class RevIntegrationScenarioTriggerTaskHandler {

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ErpScenarioMasterRepository masterRepo;

	@Autowired
	private GSTNDetailRepository gstnRepo;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	@Qualifier("SftpScenarioPermissionRepository")
	private SftpScenarioPermissionRepository sftpScenPermissionRepo;

	public void triggerCronEligibleJobs(String groupcode, Message message) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside Scenario Trigger with groupcode {}",
					groupcode);
		}
		try {
			TenantContext.setTenantId(groupcode);
			List<ErpScenarioPermissionEntity> erpScenarioPermissions = erpScenPermissionRepo
					.findByGroupcodeAndIsDelete(groupcode, false);
			List<AsyncExecJob> jobs = new ArrayList<>();
			List<ErpScenarioPermissionEntity> eligibleScenarios = new ArrayList<>();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Active Scenarios are {}", erpScenarioPermissions);
			}
			for (int scenarioCount = 0; scenarioCount < erpScenarioPermissions
					.size(); scenarioCount++) {
				ErpScenarioPermissionEntity scenario = erpScenarioPermissions
						.get(scenarioCount);
				String cron = scenario.getJobFrequency();
				LocalDateTime lastJobStartDate = scenario.getJobstartDate();
				if (cron != null && !cron.trim().isEmpty()
						&& CronExpression.isValidExpression(cron)) {
					LocalDateTime nextScheduledTime = getNextScheduledTime(cron,
							lastJobStartDate);
					if (LocalDateTime.now().compareTo(nextScheduledTime) > 0) {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Eligible Scenario to run {}",
									scenario);
						}

						Long id = scenario.getGstinId();
						Optional<GSTNDetailEntity> gstnEntity = gstnRepo
								.findById(id);
						if (gstnEntity == null) {
							LOGGER.error(
									"No active Gstin Found in GSTIN_INFO with the gstin id {} ",
									id);
							continue;
						}
						String gstin = gstnEntity.get().getGstin();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("scenario Gstin is {}", gstin);
						}
						RevIntegrationScenarioTriggerDto dto = new RevIntegrationScenarioTriggerDto();
						dto.setDestinationName(scenario.getDestName());
						dto.setGroupcode(groupcode);
						dto.setEntityId(scenario.getEntityId());
						dto.setScenarioId(scenario.getScenarioId());
						dto.setErpId(scenario.getErpId());
						dto.setGstin(gstin);
						String json = gson.toJson(dto,
								RevIntegrationScenarioTriggerDto.class);
						ErpScenarioMasterEntity scenarioMaster = masterRepo
								.findByIdAndIsDelete(scenario.getScenarioId(),
										false);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("scenario Master {} ", scenarioMaster);
						}
						if (scenarioMaster != null
								&& scenarioMaster.getScenarioName() != null
								&& !scenarioMaster.getScenarioName()
										.isEmpty()) {
							AsyncExecJob job = createEntity(groupcode,
									scenarioMaster.getScenarioName(), json,
									JobConstants.SYSTEM, JobConstants.PRIORITY,
									message.getId(),
									JobConstants.SCHEDULE_AFTER_IN_MINS);
							jobs.add(job);
							scenario.setJobstartDate(LocalDateTime.now());
							eligibleScenarios.add(scenario);
							// tempararly saving in a loop instead we can
							// saveAll.
							// asyncJobsService.createJob(job);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("{} Scenario is not eligible to run.",
									scenario);
						}
					}
				}
			}
			if (!jobs.isEmpty()) {
				// TenantContext.setTenantId(groupcode);
				// not working so saved in the above for loop itself
				asyncJobsService.createJobs(jobs);
				erpScenPermissionRepo.saveAll(eligibleScenarios);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new AppException(e.getMessage(), e);
		}

		SftpScenario(groupcode, message);
	}

	private AsyncExecJob createEntity(String groupCode, String jobCategory,
			String jsonParam, String userName, Long priority, Long parentJobId,
			Long scheduleAfterInMins) {

		Date curTime = new Date();
		AsyncExecJob job = new AsyncExecJob();
		job.setGroupCode(groupCode);
		job.setJobCategory(jobCategory);
		job.setStatus(JobStatusConstants.SUBMITTED);
		job.setMessage(jsonParam);
		job.setJobPriority(priority);
		job.setUserName(userName);
		job.setParentId(parentJobId);
		job.setCreatedDate(curTime);
		job.setUpdatedDate(curTime);
		if (scheduleAfterInMins != null && scheduleAfterInMins > 0) {
			job.setScheduled(true);
			LocalDateTime schedTime = LocalDateTime.now()
					.plusMinutes(scheduleAfterInMins);
			Date asyncTime = java.sql.Timestamp.valueOf(schedTime);
			job.setScheduledTime(asyncTime);
		}

		return job;
	}

	private LocalDateTime getNextScheduledTime(String cron,
			LocalDateTime lastJobstartDate) {
		CronExpression expression = CronExpression.parse(cron);
		return expression.next(LocalDateTime.now());
	}

	private void SftpScenario(String groupcode, Message message) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing SftpScenario  with groupcode {}", groupcode);
				LOGGER.debug(logMsg);
			}
			TenantContext.setTenantId(groupcode);

			List<SftpScenarioPermissionEntity> sftpScenarios = sftpScenPermissionRepo
					.findByIsDelete(false);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Active sftpScenarios are {}", sftpScenarios);
			}
			for (int scenarioCount = 0; scenarioCount < sftpScenarios
					.size(); scenarioCount++) {
				SftpScenarioPermissionEntity sftpScenario = sftpScenarios
						.get(scenarioCount);

				String cron = sftpScenario.getJobFrequency();
				LocalDateTime jobstartDate = sftpScenario.getJobstartDate();
				if (cron != null && !cron.trim().isEmpty()
						&& CronExpression.isValidExpression(cron)) {
					LocalDateTime nextScheduledTime = getNextScheduledTime(cron,
							jobstartDate);
					if (LocalDateTime.now().compareTo(nextScheduledTime) > 0) {

						ErpScenarioMasterEntity scenarioMaster = masterRepo
								.findByIdAndIsDelete(sftpScenario.getScenarioId(),
										false);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("scenario Master {} ", scenarioMaster);
						}
						
						if (scenarioMaster == null
								|| scenarioMaster.getScenarioName() == null
								|| scenarioMaster.getScenarioName().isEmpty()) {
							LOGGER.error(
									"No Entry in the Scenario master table with id {}",
									sftpScenario.getScenarioId());
							break;
							
						}
						
						Long erpId = sftpScenario.getErpId();
						List<Long> filesIds = null;
						if (LOGGER.isDebugEnabled()) {
							String logMsg = String.format(
									"Fetching fileIds from fileStatus for sftpResponse");
							LOGGER.debug(logMsg);
						}
						filesIds = gstr1FileStatusRepository.getIdBySource();

						if (LOGGER.isDebugEnabled()) {
							String logMsg = String
									.format("Fetched fileIds for sftpResponse");
							LOGGER.debug(logMsg);
						}
						if (filesIds != null && !filesIds.isEmpty()) {

							filesIds.forEach(fileId -> {
								if (LOGGER.isDebugEnabled()) {
									String logMsg = String.format(
											"OutwardSftpfileResponse Job is created for each fileId:'%s'",
											fileId);
									LOGGER.debug(logMsg);
								}
								postOutwardSftpfileResponseJob(fileId,
										groupcode, scenarioMaster.getId(),scenarioMaster.getScenarioName(), erpId, message);
								sftpScenario.setJobstartDate(LocalDateTime.now());
								sftpScenPermissionRepo.save(sftpScenario);
							});
						} else {
							if (LOGGER.isDebugEnabled()) {
								String logMsg = String.format(
										"There are no eligible fileId's for OutwardSftpfileResponse Job");
								LOGGER.debug(logMsg);
							}
						}
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
			throw new AppException(ex.getMessage(), ex);
		}
	}

	private void postOutwardSftpfileResponseJob(Long fileId, String groupCode,
			Long scenarioId, String scenarioName, Long erpId, Message message) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Posting OutwardSftpfileResponse Job job for fileId and groupCode",
					fileId, groupCode);
			LOGGER.debug(msg);
		}
		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("groupCode", groupCode);
		jsonParams.addProperty("scenarioId", scenarioId);
		jsonParams.addProperty("erpId", erpId);
		jsonParams.addProperty("fileId", fileId);
		jsonParams.addProperty("scenarioName",
				scenarioName);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Created JobParams JSON for fileId",
					fileId);
			LOGGER.debug(msg);
		}
		// Post a job to the job table.
		AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
				scenarioName, jsonParams.toString(),
				JobConstants.SYSTEM, JobConstants.PRIORITY,
				message.getId(),
				JobConstants.SCHEDULE_AFTER_IN_MINS);

		gstr1FileStatusRepository.updateChildCreatedFlagToTrue(fileId);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Created new OutwardSftpfileResponse Job with Job Id = '%d' "
							+ "for -> jobparams: '%s',",
					execJob.getJobId(), jsonParams.toString());
			LOGGER.debug(msg);
		}
	}

}
