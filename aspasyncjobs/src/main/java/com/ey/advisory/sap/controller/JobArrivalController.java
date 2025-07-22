package com.ey.advisory.sap.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.UserLoadService;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ContextBuilder;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.UUIDContext;
import com.ey.advisory.common.async.MessageProcessorFactory;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.AsyncExecStackTrace;
import com.ey.advisory.core.async.domain.master.PeriodicExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.async.repositories.master.AsyncExecStackTraceRepository;
import com.ey.advisory.core.async.repositories.master.PeriodicExecJobRepository;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class JobArrivalController {

	@Autowired
	@Qualifier("JsonConfigMessageProcessorFactoryImpl")
	private MessageProcessorFactory messageProcessorFactory;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	private AsyncRequestLogHelper asyncReqLogHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	@Qualifier("UserLoadServiceImpl")
	private UserLoadService userLoadServiceImpl;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	@GetMapping("/status")
	public String handler() {
		return "{\"msg\": \"App is UP and RUNNING\"}";
	}

	@PostMapping(value = "/executeAsyncJob")
	public String executeAsyncJob(@RequestBody String jsonMsg) {
		Message message = JsonUtil.newGsonInstance(false).fromJson(jsonMsg,
				Message.class);
		LOGGER.debug("message {} ", message);
		TenantContext.setTenantId(message.getGroupCode());
		setThreadContext(message);
		Gstr1FileStatusEntity updateFileStatus = null;
		String fileStatus = null;
		AsyncExecJob asyncExceJob = asyncExecJobRepository
				.findByJobDetails(message.getId());

		FileArrivalMsgDto fileResponse = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);
		String fileName = fileResponse.getFileName();
		String id = fileResponse.getFileId();

		try {
			AsyncExecJobRepository jobDetailsRepository = StaticContextHolder
					.getBean(AsyncExecJobRepository.class);
			Optional<AsyncExecJob> optionalJobDetails = jobDetailsRepository
					.findById(message.getId());
			AsyncExecJob jobDetails = null;
			if (optionalJobDetails.isPresent()) {
				jobDetails = optionalJobDetails.get();
			}
			TaskProcessor processor = messageProcessorFactory
					.getMessageTaskProcessor(message);
			Optional<Gstr1FileStatusEntity> entity = null;

			if (!Strings.isNullOrEmpty(id)) {
				entity = gstr1FileStatusRepository.findById(Long.valueOf(id));
			}

			if (fileName != null) {

				if (entity!=null && entity.isPresent() && entity.get().getFileName()
						.equalsIgnoreCase(fileName)) {
					updateFileStatus = entity.get();
				} else {
					updateFileStatus = gstr1FileStatusRepository
							.getFileName(fileName);
				}
				fileStatus = asyncExceJob.getStatus();

				if (null != updateFileStatus) {
					LocalDateTime jobStartDate = LocalDateTime.ofInstant(
							asyncExceJob.getJobStartDate().toInstant(),
							ZoneId.systemDefault());
					updateFileStatus.setStartOfUploadTime(jobStartDate);
					gstr1FileStatusRepository.save(updateFileStatus);
				}
			}

			ContextBuilder contextBuilder = StaticContextHolder
					.getBean("DefaultContextBuilder", ContextBuilder.class);
			AppExecContext context = contextBuilder
					.createAppContext(message.getUserName());
			jobDetails.setStatus(JobStatusConstants.IN_PROGRESS);
			jobDetails.setUpdatedDate(new Date());

			// inprogress stage
			if (null != updateFileStatus) {
				updateFileStatus.setFileStatus(fileStatus);
				updateFileStatus.setFileStatus(JobStatusConstants.IN_PROGRESS);
				gstr1FileStatusRepository.save(updateFileStatus);
			}

			try {
				jobDetailsRepository.save(jobDetails);

			} catch (Exception ex) {
				LOGGER.error(" Exception in job details ");

				failedBatAltUtility.prepareAndTriggerAlert(
						String.valueOf(message.getId()), "MASTER_DB",
						String.format("MASTER DB NOT ACCESSABLE {} ",
								message.getJobCategory()));

			}
			asyncReqLogHelper.logJobParams(message);
			processor.execute(message, context);

			// At this point if the job status is not already marked as
			// Completed or Failed, then mark it as Complete. We assume that
			// since there was no exception in processing and the Job status
			// is not set to 'Failed', the job was successful. The TaskProcessor
			// code might have forgotten to set the 'Completed' status for the
			// job. In this case, we set the job status to 'Completed' in order
			// to avoid jobs remaining in the 'InProgress' state forever.
			String status = null;
			try {
				// Get the Job Record first, from the DB.
				Optional<AsyncExecJob> findById = jobDetailsRepository.findById(message.getId());
				if (findById.isPresent()) {
					jobDetails = findById.get();
				}
				status = jobDetails.getStatus();
				// Mark the job status as
				if (!JobStatusConstants.COMPLETED.equals(status)
						&& !JobStatusConstants.FAILED.equals(status)) {
					jobDetails.setStatus(JobStatusConstants.COMPLETED);
					Date curDate = new Date();
					jobDetails.setJobEndDate(curDate);
					jobDetails.setUpdatedDate(curDate);

					LocalDateTime jobEndDate = LocalDateTime.now();
					LocalDateTime localDateTime = LocalDateTime
							.ofInstant(curDate.toInstant(), ZoneId.systemDefault());
					LOGGER.info("Converted LocalDateTime: {}", localDateTime);
					
					if (!Strings.isNullOrEmpty(id)) {
						entity = gstr1FileStatusRepository.findById(Long.valueOf(id));
					}
					
					if (fileName != null) {
						Gstr1FileStatusEntity completeFileStatus = new 	Gstr1FileStatusEntity();
						
						if (entity!=null && entity.isPresent() && entity.get().getFileName()
								.equalsIgnoreCase(fileName)) {
							completeFileStatus = entity.get();
						} else {
							completeFileStatus = gstr1FileStatusRepository
									.getFileName(fileName);
						}
						
					/*	Gstr1FileStatusEntity completeFileStatus = gstr1FileStatusRepository
								.getFileName(fileName);*/

						if (completeFileStatus != null) {
							Integer total = (completeFileStatus
									.getTotal() != null)
											? completeFileStatus.getTotal() : 0;
							Integer processed = (completeFileStatus
									.getProcessed() != null)
											? completeFileStatus.getProcessed()
											: 0;
							Integer error = (completeFileStatus
									.getError() != null)
											? completeFileStatus.getError() : 0;
							Integer information = (completeFileStatus
									.getInformation() != null)
											? completeFileStatus
													.getInformation()
											: 0;
							completeFileStatus.setTotal(total);
							completeFileStatus.setProcessed(processed);
							completeFileStatus.setError(error);
							completeFileStatus.setInformation(information);
							if (completeFileStatus.getFileStatus().trim()
									.equalsIgnoreCase(
											JobStatusConstants.FAILED)) {
								completeFileStatus.setFileStatus(
										JobStatusConstants.FAILED);
							} else {
								completeFileStatus.setFileStatus(
										JobStatusConstants.PROCESSED);
							}
							completeFileStatus.setEndOfUploadTime(jobEndDate);
							gstr1FileStatusRepository.save(completeFileStatus);
						}
					}
					try {

						jobDetailsRepository.save(jobDetails);
					} catch (Exception ex) {
						LOGGER.error(" Exception in job details ");

						failedBatAltUtility.prepareAndTriggerAlert(
								String.valueOf(message.getId()), "MASTER_DB",
								String.format("MASTER DB NOT ACCESSABLE {} ",
										message.getJobCategory()));

					}
				}

				// Update the Periodic job on success.
				if (jobDetails.isPeriodic()) {
					updatePeriodicJobDetails(jobDetails);
				}
			} catch (Exception ex) {
				// If this exception occurs, then there's pretty much nothing
				// that we can do, other than logging it and proceeding.
				String msg = "Error occured while trying "
						+ "to mark the Job as Completed.";
				LOGGER.error(msg, ex);

			}
		} catch (Exception ex) {

			// If we already got an AppException, then use it to log the
			// message. Otherwise, create an AppException with the
			// Error Enhancement strategy of appending the message of the
			// First Non-App Exception.
			AppException aex = (ex instanceof AppException) ? (AppException) ex
					: new AppException("Error occured while executing the Job.",
							ex,
							ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

			// Any exception that reaches here should set the Job Status to
			// Failed, if it is not set already. Sometimes, a Job status
			// may be set to Success, by some other component but after
			// setting the status to success, some other exception might have
			// occurred, that resulted in the control reaching this point. In
			// such cases, this error can be ignored. Similarly, if the status
			// of the job is marked as 'Failed' by some other component, then
			// too we can ignore the exception here. Only if the job status is
			// not 'Failed' or 'Completed', then we need to set the status as
			// 'Failed' and get the exception message and set it to the Job
			// record.

			// Extract the error message from the new exception instance.
			String errMsg = aex.getMessage();

			// Get the error code from the original exception.
			String errCode = ((ex instanceof AppException)
					&& ((AppException) ex).hasErrCode())
							? String.valueOf(((AppException) ex).getErrCode())
							: null;

			// Proceed with Logging the exception first.
			LOGGER.error(errMsg, aex);

			String status = null;
			try {
				// Get the Job Record first, from the DB.
				AsyncExecJobRepository jobDetailsRepository = StaticContextHolder
						.getBean(AsyncExecJobRepository.class);
				Optional<AsyncExecJob> optionalJobDetails = jobDetailsRepository
						.findById(message.getId());
				Optional<Gstr1FileStatusEntity> entity = null;
				AsyncExecJob jobDetails = null;
				if (optionalJobDetails.isPresent()) {
					jobDetails = optionalJobDetails.get();
				}
				status = jobDetails.getStatus();
				// Mark the job status as Failed only if already the status is
				// not marked as 'Failed' or 'Complete'. Otherwise, log this
				// condition and proceed.
				if (!JobStatusConstants.COMPLETED.equals(status)
						&& !JobStatusConstants.FAILED.equals(status)) {
					jobDetails.setStatus(JobStatusConstants.FAILED);
					Date curDate = new Date();
					jobDetails.setJobEndDate(curDate);
					jobDetails.setUpdatedDate(curDate);
					jobDetails.setErrReason(errMsg);
					if (errCode != null) {
						jobDetails.setErrorCode(errCode);
					}

					LocalDateTime jobEndDate = LocalDateTime.ofInstant(
							curDate.toInstant(), ZoneId.systemDefault());
					
					if (!Strings.isNullOrEmpty(id)) {
						entity = gstr1FileStatusRepository.findById(Long.valueOf(id));
					}
					
					if (fileName != null) {
						
						Gstr1FileStatusEntity failedFileStatus = new Gstr1FileStatusEntity();
						
						if (entity!=null && entity.isPresent() && entity.get().getFileName()
								.equalsIgnoreCase(fileName)) {
							failedFileStatus = entity.get();
						} else {
							failedFileStatus = gstr1FileStatusRepository
									.getFileName(fileName);
						}
					
						
						/*Gstr1FileStatusEntity failedFileStatus = gstr1FileStatusRepository
								.getFileName(fileName);*/

						if (null != failedFileStatus) {
							Integer total = (failedFileStatus
									.getTotal() != null)
											? failedFileStatus.getTotal() : 0;
							Integer processed = (failedFileStatus
									.getProcessed() != null)
											? failedFileStatus.getTotal() : 0;
							Integer error = (failedFileStatus
									.getError() != null)
											? failedFileStatus.getError() : 0;
							Integer information = ((failedFileStatus
									.getInformation() != null))
											? failedFileStatus.getInformation()
											: 0;
							failedFileStatus.setTotal(total);
							failedFileStatus.setProcessed(processed);
							failedFileStatus.setError(error);
							failedFileStatus.setInformation(information);

							failedFileStatus
									.setFileStatus(JobStatusConstants.FAILED);
							failedFileStatus.setEndOfUploadTime(jobEndDate);
							gstr1FileStatusRepository.save(failedFileStatus);
						}
					}

					try {
						jobDetailsRepository.save(jobDetails);
					} catch (Exception ex1) {
						LOGGER.error(" Exception in job details ");

						failedBatAltUtility.prepareAndTriggerAlert(
								String.valueOf(message.getId()), "MASTER_DB",
								String.format("MASTER DB NOT ACCESSABLE {} ",
										message.getJobCategory()));
					}

				} else {
					String msg = String.format(
							"Job already marked as '%s'. "
									+ "Leaving the job status unaltered.",
							status);
					LOGGER.warn(msg);
				}

				// update the periodic job on failure.
				if (jobDetails.isPeriodic()) {
					// Load the periodic job
					// update the job_start_date and job_end_date
					updatePeriodicJobDetails(jobDetails);
				}

				// Finally save the stack trace to the Stack Trace Table
				// For easy access of the error.
				recordStackTrace(message.getId(), aex);
			} catch (Exception e) {
				// If this exception occurs, then there's pretty much nothing
				// that we can do, other than logging it and proceeding.
				String msg = "Error occured while trying "
						+ "to mark the Job as Failed.";

				LOGGER.error(msg, e);
			}
		} finally {
			reqLogHelper.saveLogEntity();
			TenantContext.clearTenant();
			LoggerIdContext.clearLoggerId();
			SecurityContext.clearTenant();
			PublicApiContext.clearPublicApiContext();
		}
		return jsonMsg;
	}

	private void updatePeriodicJobDetails(AsyncExecJob jobDetails) {
		PeriodicExecJobRepository periodicJobRepo = StaticContextHolder
				.getBean(PeriodicExecJobRepository.class);
		Optional<PeriodicExecJob> optionalPeriodicJob = periodicJobRepo
				.findById(jobDetails.getPeriodicJobId());
		optionalPeriodicJob.ifPresent(periodicJob -> {
			periodicJob.setJobstartDate(jobDetails.getJobStartDate());
			periodicJob.setJobCompletionDate(jobDetails.getJobEndDate());
			periodicJob.setUpdatedDate(new Date());
			periodicJobRepo.save(periodicJob);
		});
	}

	private void recordStackTrace(Long jobId, Throwable t) {
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "About to save the error stack trace "
						+ "to the stack trace table.";
				LOGGER.debug(msg);
			}

			// Get the reference to the stack trace repository.
			AsyncExecStackTraceRepository stRepo = StaticContextHolder
					.getBean(AsyncExecStackTraceRepository.class);

			// Use the Gauva Throwables class to get the recursive
			// stack trace.
			String stackTrace = Throwables.getStackTraceAsString(t);
			AsyncExecStackTrace stEntity = new AsyncExecStackTrace();
			stEntity.setJobId(jobId);
			stEntity.setCreatedDate(new Date());
			stEntity.setMessage(stackTrace);

			// Save the stack trace to the DB.
			AsyncExecStackTrace ret = stRepo.save(stEntity);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Saved the error stack trace "
								+ "to the stack trace table with id = %d",
						ret.getId());
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {
			// Log the exception and proceed. Need not throw an exception
			// as this error can always be viewed from the logs.
			String msg = "Error occured while trying to store the "
					+ "Exception Stack Trace to the Stack Trace Table.";
			LOGGER.error(msg, ex);
		}
	}

	private void setThreadContext(Message message) {
		// Generating the UUID and attaching it to Context.
		UUID uuid = UUID.randomUUID();
		UUIDContext.setUniqueID(uuid.toString());
		// Accessing the user name and attaching it to Context.
		User user = null;
		if ("PERIODIC".equalsIgnoreCase(message.getGroupCode())) {
			user = new User();
		} else {
			user = userLoadServiceImpl.loadUser(TenantContext.getTenantId(),
					message.getUserName());
		}
		user.setUserPrincipalName(message.getUserName());
		SecurityContext.setUser(user);
	}

}
