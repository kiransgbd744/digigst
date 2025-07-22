package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ContextBuilder;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.UUIDContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.services.MessageProcessorFactory;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.AsyncExecStackTrace;
import com.ey.advisory.core.async.domain.master.PeriodicExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.async.repositories.master.AsyncExecStackTraceRepository;
import com.ey.advisory.core.async.repositories.master.PeriodicExecJobRepository;
import com.google.common.base.Throwables;

@RestController
public class JobArrivalController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JobArrivalController.class);
	
	@Autowired
	@Qualifier("JsonConfigMessageProcessorFactoryImpl")
	private MessageProcessorFactory messageProcessorFactory;
	
	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@PostMapping(value = "/executeAsyncJob")
	public String executeAsyncJob(@RequestBody String jsonMsg) {
		Message message = JsonUtil.newGsonInstance(false).fromJson(
				jsonMsg, Message.class);
		//Getting name of the file and path
		//Generating the UUID and attaching it to Context. 
		UUID uuid = UUID.randomUUID();
		UUIDContext.setUniqueID(uuid.toString());
		
		//picked stage
		try {
			AsyncExecJobRepository jobDetailsRepository = 
					StaticContextHolder
						.getBean(AsyncExecJobRepository.class);
			AsyncExecJob jobDetails = jobDetailsRepository
					.findById(message.getId()).get();
			TaskProcessor processor = messageProcessorFactory
					.getMessageTaskProcessor(message);
			TenantContext.setTenantId(message.getGroupCode());

			MDC.put("jobId", message.getId().toString());
			MDC.put("userName", message.getUserName());
			MDC.put("groupCode", message.getGroupCode());
			ContextBuilder contextBuilder = StaticContextHolder
					.getBean("DefaultContextBuilder", ContextBuilder.class);
			AppExecContext context = contextBuilder
					.createAppContext(message.getUserName());
			jobDetails.setStatus(JobStatusConstants.IN_PROGRESS);
			jobDetails.setUpdatedDate(new Date());
			
			
			jobDetailsRepository.save(jobDetails);
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
				jobDetails = jobDetailsRepository
						.findById(message.getId()).get();
				status = jobDetails.getStatus();
				// Mark the job status as
				if (!JobStatusConstants.COMPLETED.equals(status)
						&& !JobStatusConstants.FAILED.equals(status)) {
					jobDetails.setStatus(JobStatusConstants.COMPLETED);
					Date curDate = new Date();
					jobDetails.setJobEndDate(curDate);
					jobDetails.setUpdatedDate(curDate);
					
					
					LocalDateTime.ofInstant(curDate.toInstant(), 
							ZoneId.systemDefault());
					jobDetailsRepository.save(jobDetails);
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
			AppException aex = (ex instanceof AppException) ? 
				(AppException) ex : new AppException(
						"Error occured while executing the Job.", ex,
						ErrMsgEnhancementStrategy
								.APPEND_FIRST_NON_APP_EXCEPTION);

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
				AsyncExecJobRepository jobDetailsRepository = 
						StaticContextHolder
							.getBean(AsyncExecJobRepository.class);
				AsyncExecJob jobDetails = jobDetailsRepository
						.findById(message.getId()).get();
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
					
					jobDetailsRepository.save(jobDetails);
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
			MDC.clear();
			TenantContext.clearTenant();
		}
		return jsonMsg;
	}
	
	private void updatePeriodicJobDetails(AsyncExecJob jobDetails) {
		PeriodicExecJobRepository periodicJobRepo =
				StaticContextHolder.getBean(
						PeriodicExecJobRepository.class);
		PeriodicExecJob  periodicJob =
				periodicJobRepo.findById(
						jobDetails.getPeriodicJobId()).get();
		periodicJob.setJobstartDate(jobDetails.getJobStartDate());
		periodicJob.setJobCompletionDate(jobDetails.getJobEndDate());
		periodicJob.setUpdatedDate(new Date());
		periodicJobRepo.save(periodicJob);
	}
	
	private void recordStackTrace(Long jobId, Throwable t) {
		try {
			
			if(LOGGER.isDebugEnabled()) {
				String msg = "About to save the error stack trace "
						+ "to the stack trace table.";
				LOGGER.debug(msg);
			}
						
			// Get the reference to the stack trace repository.
			AsyncExecStackTraceRepository stRepo = 
					StaticContextHolder
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
			
			if(LOGGER.isDebugEnabled()) {
				String msg = String.format("Saved the error stack trace "
						+ "to the stack trace table with id = %d", ret.getId());
				LOGGER.debug(msg);
			}
			
		} catch(Exception ex) {
			// Log the exception and proceed. Need not throw an exception
			// as this error can always be viewed from the logs.
			String msg = "Error occured while trying to store the "
					+ "Exception Stack Trace to the Stack Trace Table.";
			LOGGER.error(msg, ex);
		}
	}

}
