package com.ey.advisory.common.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.HostNameEvaluator;
import com.ey.advisory.common.Message;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.config.ConfigManager;

/**
 * This implementation of TaskFetcherAndDistributor uses DB as the message
 * source. It relies on an AsyncExecJob entity that's mapped to a DB table. Each
 * instance of this entity represents a message that needs to be processed by a
 * thread in the Executor thread pool. This Fetcher and Distributor component is
 * responsible for selecting an entity in a 'Submitted' state, from the DB,
 * change the status to 'In Progress' and hand it over to a free thread in the
 * executor thread pool. The message contained within each 'AsyncExecJob' entity
 * is a JSON object with a 'type' key in it. The 'type' of message determines
 * the 'TaskProcessor' instance to be used to process the message.
 * 
 * Since there can be multiple VMs running this application, there can be
 * several thread pools and DBTaskFetcherAndDistributor instances (one Task
 * Fetcher and Distributor instance per VM). So, it's essential that two
 * different distributors do not choose the same message and hand it over to
 * executor threads for processing. If the Message Source is something like
 * Kafka, then the source itself is responsible for ensuring that a message is
 * delivered only once to a consumer. But, since this class uses DB table as the
 * source of messages, we need to have our own mechanism to ensure that one
 * message is consumed only once.
 * 
 * We ensure this by using an Optimistic Locking technique on the JobDetails
 * table. When the 'TaskFetcherAndDistributor' in a VM selects a row from this
 * table, it gets the version number as well. When an update is performed to
 * change the status to 'In Progress', we use a where clause containing this
 * version number in order to ensure that only one thread can update a message
 * row in the JobDetails table.
 * 
 * @author Sai.Pakanati
 *
 */

@Component("DBTaskFetcherAndDistributor")
public class DBTaskFetcherAndDistributor implements TaskFetcherAndDistributor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DBTaskFetcherAndDistributor.class);

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("EyAsyncMiscIOPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private HostNameEvaluator hostNameEvaluator;

	@Autowired
	private Environment env;

	@Autowired
	private AsyncExecControlParams controlParams;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("JsonBasedJobUrlResolverImpl")
	private JobUrlResolver jobUrlResolver;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	/**
	 * The Task Types to be processed by this server. The task types can be
	 * dynamically switched during runtime using the web service API calls.
	 */
	private CopyOnWriteArrayList<String> taskTypeList = new CopyOnWriteArrayList<>();

	/**
	 * The name of the system property that's passed as a JVM argument OR
	 * configured in application.properties (spring boot config file).
	 */
	private static final String TASK_TYPES_TO_LOAD = "taskTypes";

	@Override
	public CopyOnWriteArrayList<String> getTaskTypeList() {
		return taskTypeList;
	}
	
	private volatile boolean shouldContinue = true; 

	// Method to stop the loop
	public void stopFetchingTasks() {
	    this.shouldContinue = false; // Call this method to stop the loop gracefully.
	}

	/**
	 * This method loads the list of task types to be processed by this
	 * AsyncExec Instance. While loading the jobs from the DB, if specific task
	 * types are mentioned, then it will load only the specified task types and
	 * distribute them against the thread pool. This is required for
	 * distributing similar jobs among a thread pool. Compute intensive jobs can
	 * have separate thread pools, while IO intensive jobs can have its on
	 * separate thread pool, thereby utilizing the pools in an efficient manner.
	 * 
	 * @return the list of TaskTypes to process.
	 */
	private List<String> loadTaskTypesToProcess() {
		String taskTypes = env.getProperty(TASK_TYPES_TO_LOAD);
		if (taskTypes == null || taskTypes.trim().isEmpty()) {
			String msg = "No Task Types are configured for this AsyncExec "
					+ "instance. This instance will load ALL Task Types. It's "
					+ "better to specify the taskTypes values with a comma "
					+ "separated values of taskType names to allocate only "
					+ "those type of tasks to the pool";
			LOGGER.warn(msg); // Print this as a warning message.
			return new ArrayList<>();
		}
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("The taskType env variable configured "
					+ "for this AsyncExec instance: taskType = '%s'. "
					+ "Note that this is case sensitive; and all the values "
					+ "mentioned here should match the case of the actual "
					+ "taskNames.", taskTypes);
			LOGGER.info(msg); // Print the list of task Types.
		}

		// Split the string with comma as the delimiter.
		String[] taskNames = taskTypes.split(",");

		List<String> tasks = Arrays.stream(taskNames)
				.map(taskName -> taskName.trim())
				.collect(Collectors.toCollection(ArrayList::new));

		// Printing the list of tasks.
		IntStream.range(0, taskNames.length).forEach(i -> {
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format(
						"Task Type No: '%d' for this "
								+ "AsyncExec instance = '%s'",
						i + 1, tasks.get(i));
				LOGGER.info(msg); // Print the list of task Types.
			}
		});

		// Return the list of task names to be considered, while loading the
		// tasks from the DB.
		return tasks;
	}

	private List<AsyncExecJob> loadJobDetailsFromDB(int noOfRowsToFetch,
			List<String> taskNames) {
		Pageable pageReq = PageRequest.of(0, noOfRowsToFetch, Direction.DESC,
				"jobPriority");

		List<AsyncExecJob> jobDetails = null;

		if (taskNames.isEmpty()) {
			String msg = "No task names configured in ENV variable. "
					+ "Loading all types of tasks from DB...";
			LOGGER.warn(msg);
			jobDetails = asyncExecJobRepository.findByStatusAndScheduledTime(
					JobStatusConstants.SUBMITTED, new Date(), pageReq);

			List<AsyncExecJob> jobs = jobDetails == null ? new ArrayList<>()
					: jobDetails;
			if (jobs.isEmpty()) {
				String errMsg = String.format("No tasks found in the DB in "
						+ "Submitted state, that are ready for execution.");
				LOGGER.warn(errMsg);
			} else {
				if (LOGGER.isInfoEnabled()) {
					String infoMsg = String.format(
							"%d Tasks loaded from DB for execution!!",
							jobs.size());
					LOGGER.warn(infoMsg);
				}
			}
			return jobs;
		}

		// The following code is executed when the taskTypes environment
		// variable is set.

		// Join the strings.
		final String tasksStr = StringUtils.join(taskNames, ", ");
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format(
					"About to load tasks of " + "type - '%s' from the DB.",
					tasksStr);
			LOGGER.info(msg);
		}

		jobDetails = asyncExecJobRepository
				.findByStatusAndJobCategoryAndScheduledTime(
						JobStatusConstants.SUBMITTED, new Date(), taskNames,
						pageReq);

		List<AsyncExecJob> jobs = jobDetails == null ? new ArrayList<>()
				: jobDetails;

		if (jobs.isEmpty()) {
			String msg = String.format("No tasks of types - "
					+ " '%s' found in the DB in submitted state, "
					+ "that are ready for execution.", tasksStr);
			LOGGER.warn(msg);
		} else {
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format(
						"%d Tasks of Type - "
								+ "'%s' loaded from DB for execution!!",
						jobs.size(), tasksStr);
				LOGGER.warn(msg);
			}
		}

		return jobs;
	}

	private void printSleepMsg(ReasonToSleep reasonToSleep) {

		int activeThreadCount = execPool.getActiveCount();
		int maxPoolSize = execPool.getMaxPoolSize();

		if (reasonToSleep == ReasonToSleep.SUSPENDED) {
			final String shutDownMsg = (activeThreadCount == 0)
					? "It's safe to shutdown the app."
					: "DO NOT SHUT DOWN. Task Processing in Progress.";
			final String msg = String.format(
					"The AsyncExec is in suspended state. "
							+ "Active Threads = %d, Max Threads = %d. %s",
					activeThreadCount, maxPoolSize, shutDownMsg);
			LOGGER.info(msg);
		}

		if (reasonToSleep == ReasonToSleep.NOFREETHREADS) {
			final String msg = String.format(
					"No free threads available in pool. Sleeping... "
							+ "Active Threads = %d, Max Threads = %d. =",
					activeThreadCount, maxPoolSize);
			LOGGER.info(msg);
		}

		if (reasonToSleep == ReasonToSleep.DISTRIBUTIONCOMPLETE) {
			final String msg = String.format(
					"Allocation Round Complete. Sleeping... "
							+ "Active Threads = %d, Max Threads = %d. =",
					activeThreadCount, maxPoolSize);
			LOGGER.info(msg);
		}

	}

	private boolean allocateJobToProcessor(AsyncExecJob job) {
		// Get the Job Id and Message of the Job.
		Long jobId = job.getJobId();
		String jobMsg = (job.getMessage() == null || job.getMessage().isEmpty())
				? "{}" : job.getMessage();
		String userName = job.getUserName();
		String groupCode = job.getGroupCode();
		String messageType = job.getJobCategory();
		String hostName = hostNameEvaluator.getHostName();
		try {
			Message message = new Message(jobId, messageType, groupCode,
					userName, jobMsg);
			updateJobStatus(job, hostName);
			execPool.execute(new RunnableTaskProcessor(message, httpClient,
					jobUrlResolver, configManager));
			return true;
		} catch (ObjectOptimisticLockingFailureException ex) {
			// This exception occurs frequently when multiple VMs
			// access and try to update the same job records. So,
			// we should not log it as an error. Instead treat it
			// as a normal condition and display logger messages
			// only when Debug is enabled.
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Optimistic Locking Failed " + "for Job Id: %d, ",
						jobId);
				LOGGER.debug(msg); // Don't have to print ex.
			}
		} catch (Exception ex) {
			// This exception shouldn't occur in normal
			// circumstances. If this exception occurs, log the
			// Job Id and the message for investigation.
			String msg = String.format("Unknown exception occured for "
					+ "Job Id: %d, Message is: '%s'", jobId, jobMsg);
			LOGGER.debug(msg, ex);
		}

		return false;
	}

	/**
	 * This method is called when there are free threads available and the
	 * AsyncExec app is in an 'Active' state.
	 */
	private void fetchAndAllocateJobs(List<String> taskNames) {
		// Fetch the list of jobs in the 'submitted' status from the
		// DB orodered by job priority in descending.

		// loop and take one by one and try to update it to
		// 'In Progress in DB. If it succeeds.
		int availableThreadCount = execPool.getMaxPoolSize()
				- execPool.getActiveCount();
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Available count....'%d'",
					availableThreadCount);
			LOGGER.info(msg);
		}

		// Fetch a minimum of 10 job messages from the DB. If there are
		// more than 10 free threads, then fetch an extra 10 messages
		// from the DB. This will allow us to keep a buffer for the
		// OptimisticLockException that we handle, so that we don't have
		// to go back to the DB, for distributing tasks to all the
		// free threads.
		int noOfRowsToFetch = (availableThreadCount > 10)
				? availableThreadCount + 10 : 10;

		// Load the list of jobs from the DB. This method will consider
		// the taskTypes environment variable that's set to filter out
		// similar jobs to be distributed among the threads of the
		// thread pool.
		List<AsyncExecJob> jobDetails = loadJobDetailsFromDB(noOfRowsToFetch,
				taskNames);

		int processedCount = 0;
		for (AsyncExecJob job : jobDetails) {
			// If we've already allocated jobs to the
			// pre-calculated number of threads, then break the
			// for loop, and wait for the specified time,
			// before continuing with the next iteration of the
			// while loop. Increment this variable only if the job was
			// successfully submitted to the thread pool. Otherwise,
			// no thread would've been used for processing. This check
			// is required since we fetch extra jobs to compensate for
			// OptimisticLockingException (in case it occurs).
			if (allocateJobToProcessor(job))
				processedCount++;
			if (processedCount == availableThreadCount) {
				break;
			}
		}
	}

	private Optional<ReasonToSleep> checkForAReasonToSleep() {

		// Check if the suspended flag is set. If so, return the
		// 'Suspended' wait reason.
		if (controlParams.isSuspended()) {
			return Optional.of(ReasonToSleep.SUSPENDED);
		}

		// Check if all the threads in the pool are occupied. If so, return
		// a 'NoFreeThreads wait reason.
		if (execPool.getActiveCount() == execPool.getMaxPoolSize()) {
			return Optional.of(ReasonToSleep.NOFREETHREADS);
		}

		// If none of the other wait conditions are satisfied, then
		// return a null wrapped in the optional.
		return Optional.ofNullable(null);
	}

/*	private void sleep(ReasonToSleep reasonToSleep) {
		try {
			// print a sleep message first.
			printSleepMsg(reasonToSleep);
			Thread.sleep(reasonToSleep.getTimeToSleep());
		} catch (InterruptedException ex) {
			// log this exception and proceed.
			LOGGER.error(reasonToSleep.getInterruptedMsg(), ex);
		}
	}*/
	
	private void sleep(ReasonToSleep reasonToSleep) throws InterruptedException {
	    // Print a sleep message first.
	    printSleepMsg(reasonToSleep);
	    Thread.sleep(reasonToSleep.getTimeToSleep());
	}

	/**
	 * An asynchronous method that initiates an infinite loop responsible for
	 * periodically polling the DB, fetching messages in the 'Sumbitted' state,
	 * locating the appropriate 'TaskProcessor' instance responsible for
	 * processing the message and handing over the message to it for processing,
	 * in a free thread in the executor pool.
	 */
	@Override
	@Async
	public void fetchAndAllocateTasks() {

		if (LOGGER.isInfoEnabled()) {
			String msg = String.format(
					"DBFetcher Initiated... About to fetch the configured "
							+ "task Types. Active Threads = %d, "
							+ "Max Threads = %d",
					execPool.getActiveCount(), execPool.getMaxPoolSize());
			LOGGER.info(msg);
		}
		// Get the list of task names to be loaded from the DB. If this
		// list is empty, then load all types from the DB.
		final List<String> taskNames = loadTaskTypesToProcess();

		// if taskTypes are mentioned, then copy the contents of the
		// taskTypeList configured in the environment/application.properties
		// to the shared, modifiable CopyOnWriteArrayList.
		if (!taskNames.isEmpty()) {
			taskTypeList.addAll(taskNames);
		}

		if (LOGGER.isInfoEnabled()) {
			String msg = String.format(
					"Task Names Fetched from DB. No of Tasks = '%d'",
					taskTypeList.size());
			LOGGER.info(msg);
		}

		// Start an infinite loop that sleeps for a pre-configured time after
		// fetching and distributing a batch of tasks to the free threads
		// in the executor thread pool.
		while (shouldContinue) {
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format(
						"DBFetcher Status = %s, Active Threads = %d, "
								+ "Max Threads = %d",
						controlParams.getStatus(), execPool.getActiveCount(),
						execPool.getMaxPoolSize());
				LOGGER.info(msg);
			}

			try {

				// Check if there is any reason to sleep. Currently the reason
				// to sleep at this point can be that the AsyncExec process
				// is suspended OR that there are no free threads in the pool.
				Optional<ReasonToSleep> reasonToSleep = checkForAReasonToSleep();

				// If there is no reason to sleep, then fetch relevant jobs
				// from the DB and distribute them among the free threads.
				if (!reasonToSleep.isPresent()) {
					fetchAndAllocateJobs(taskTypeList);
				}

				// If there is no reason to sleep at this point, then
				// consider it as the sleep time after Distribution of tasks
				// is complete.
				sleep(reasonToSleep.isPresent() ? reasonToSleep.get()
						: ReasonToSleep.DISTRIBUTIONCOMPLETE);

			} catch (Exception ex) {
				// This exception can occur while fetching the list of
				// Jobs from the DB. Log the exception and procceed to wait
				// for pre-configured time, before going for the next
				// iteration of the while loop.
				LOGGER.error("Exception while fetching/distributing tasks", ex);
			}

		}

	}

	private AsyncExecJob updateJobStatus(AsyncExecJob job, String hostName) {
		job.setInstanceId(hostName);
		job.setStatus(JobStatusConstants.PICKED);
		job.setJobStartDate(new Date());
		return asyncExecJobRepository.save(job);
	}

}

/**
 * Different reasons because of which the main Fetcher and distributor loop has
 * to wait. This enum encapsulates the time to sleep for each reason, as well as
 * the Interrupted Message to be logged.
 * 
 */
enum ReasonToSleep {
	SUSPENDED {
		@Override
		public long getTimeToSleep() {
			return 30000L;
		}

		@Override
		public String getInterruptedMsg() {
			return "Error while waiting for checking the suspended status";
		}
	},

	NOFREETHREADS {
		@Override
		public long getTimeToSleep() {
			return 10000L;
		}

		@Override
		public String getInterruptedMsg() {
			return "Exception while waiting for threads to be available";
		}
	},

	DISTRIBUTIONCOMPLETE {
		@Override
		public long getTimeToSleep() {
			return 8000L;
		}

		@Override
		public String getInterruptedMsg() {
			return "Exception while waiting for "
					+ "threads to complete processing";
		}
	};

	public abstract long getTimeToSleep();

	public abstract String getInterruptedMsg();
};
