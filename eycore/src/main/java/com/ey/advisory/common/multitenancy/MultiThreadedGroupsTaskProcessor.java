package com.ey.advisory.common.multitenancy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.javatuples.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.async.domain.master.Group;

public class MultiThreadedGroupsTaskProcessor 
					implements DistributedGroupsTaskProcessor {
		
	private static final Logger LOGGER = LoggerFactory.getLogger(
					MultiThreadedGroupsTaskProcessor.class);
	
	private ThreadPoolTaskExecutor execPool;
	
	private DefaultMultiTenantTaskProcessor origProcessor;
	
	/**
	 * The thread count variable is currently not used. It's only used to force
	 * processing using a single threaded task processor by setting it to one.
	 * In this class, the threadCount is not relevant after the Misc IO Pool
	 * is introduced. All activities performed by this processor will use the 
	 * Misc IO Pool. The core and max sizes of the MiscIOPool can be configured
	 * in the properties file. It's better to keep a common thread pool rather
	 * than the 
	 * 
	 * @param origProcessor
	 * @param threadCount
	 */
	public MultiThreadedGroupsTaskProcessor(
			DefaultMultiTenantTaskProcessor origProcessor) {
		this.origProcessor = origProcessor;
		execPool = StaticContextHolder.getBean("EyAsyncMiscIOPool", 
				ThreadPoolTaskExecutor.class);
	}
	
	@Override
	public void processTasksForGroups(List<Group> groups, Message message,
			AppExecContext ctx) {

		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Executing the "
					+ "MultiTenantTaskProcessor logic "
					+ "for TaskType: '%s' using the IO Pool, "
					+ "[ValidGroupCount = %d]", message.getMessageType(), 
					groups.size());
			LOGGER.info(msg);
		}
		
		// Get the original group code in th
		String origGroup = message.getGroupCode();
		
		// Total no. of tasks to be submitted to the thread pool.
		int noOfTasks = groups.size();
		
		// Create a latch with the number of groups to be processed.
		CountDownLatch latch = new CountDownLatch(noOfTasks);
		ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap = 
				createTasksMap(groups);
		
		// Create runnable instances for the tasks for submission.
		List<Runnable> tasks = groups.stream()
				.map(grp -> 
					new ExecTask(message, ctx, tasksMap, grp, 
							origGroup, latch, this.origProcessor))
				.collect(Collectors.toCollection(ArrayList::new));
		
		// Submit the runnable instances to the thread pool. Since the queue
		// size of the Async IO Pool is infinite (by not seetting the value)
		// we can submit as many tasks as we want.
		tasks.forEach(task -> {
			execPool.execute(task);
		});
		
		// Wait for all jobs to get completed.
		try {
			latch.await();
		} catch(InterruptedException ex) {
			String msg = "MultithreadedGroupsTaskProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			throw new AppException(msg, ex);
		}
		
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Executed the "
					+ "MultiTenantTaskProcessor logic "
					+ "for TaskType: '%s' using the Async IO Pool. "
					+ ", [ValidGroupCount = %d]!!", 
					message.getMessageType(), groups.size());
			LOGGER.info(msg);
		}	

		// Check for failed tasks from the pairs stored in the map.
		long failedTasks = tasksMap.entrySet().stream()
				.filter((entry) -> entry.getValue().getValue0() != 
							ProcessingStatus.COMPLETED)
				.count();
		
		Exception firstError = null;
		if(failedTasks > 0) {
			firstError = tasksMap.entrySet().stream()
					.filter(entry -> entry.getValue().getValue1() != null)
					.findFirst()
					.get() // Get can be called directly as we know it exists.
					.getValue().getValue1();
		}
		
		if (failedTasks != 0) {
			String msg = String.format("%d out of %d tasks failed to execute "
					+ "or was never executed in MultiTenantTaskProcessor  "
					+ "for TaskType: '%s'", 
					failedTasks, noOfTasks, message.getMessageType());
			LOGGER.error(msg);
			throw new AppException(msg, firstError,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
		
	}
	
	private ConcurrentHashMap<String, 
					Pair<ProcessingStatus, Exception>> createTasksMap(
						List<Group> groups) {
		ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap = 
					new ConcurrentHashMap<>();
		groups.forEach((group) -> tasksMap.put(
				group.getGroupCode(), 
				new Pair<ProcessingStatus, Exception>(
						ProcessingStatus.NOT_STARTED, null)));
		return tasksMap;
	}
	
	enum ProcessingStatus {
		COMPLETED,
		FAILED,
		NOT_STARTED
	}

}
