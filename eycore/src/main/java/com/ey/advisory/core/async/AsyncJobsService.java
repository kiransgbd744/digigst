package com.ey.advisory.core.async;

import java.util.List;

import com.ey.advisory.core.async.domain.master.AsyncExecJob;

/**
 * The basic architecture of AsyncExec app is to have the Fetcher/Distributor
 * thread fetch the jobs from an appropriate job storage and submit it to the
 * free threads in the thread pool. The responsibility of this interface is to
 * shield the nature of the storage that we use to persist/load the jobs. For
 * example, Jobs can be stored to/loaded from a DB or another queuing mechanism
 * like Kafka or RabbitMQ or ActiveMQ. So, when we need to persist a list of
 * jobs, we choose the appropriate implementation of this interface and invoke
 * the createJobs() method to store them. Loading the jobs from the backend
 * storage can be done in a similar fashion.
 * 
 * Apart from this, other strategies of storing and loading the jobs like topics
 * with different priorities based on different attributes of the AsyncExecJob
 * object will also handled by the implementations of this interface. For
 * example, some type of messages can be treated as having a higher priority
 * than other messages. Such messages can be stored in different Kafka topics.
 * So, the implementation of this interface is responsible for fetching the jobs
 * from different topics based on the type of message.
 * 
 * @author Sai.Pakanati
 *
 */
public interface AsyncJobsService {

	/**
	 * Pushes a single job to the appropriate back end storage.
	 * 
	 * @param job
	 *            The job to be stored to the backend.
	 * 
	 * @return
	 */
	public AsyncExecJob createJob(AsyncExecJob job);

	/**
	 * Pushes a List of jobs to the appropriate back end storage.
	 * 
	 * @param job
	 *            The List of jobs to be stored to the backend.
	 * 
	 * @return
	 */
	public List<AsyncExecJob> createJobs(List<AsyncExecJob> jobs);
	
	/**
	 * Pushes a single job to the appropriate back end storage.
	 * 
	 * @param job
	 *            The job details to be stored to the backend.
	 * 
	 * @return
	 */
	public AsyncExecJob createJob(String groupCode, String jobCategory,
			String jsonParam, String userName, Long priority, 
			Long parentJobId, Long scheduleAfterInMins);
	
	public AsyncExecJob createJobAndReturn(String groupCode, String jobCategory,
			String jsonParam, String userName, Long priority, 
			Long parentJobId, Long scheduleAfterInMins);
	

	
}
