package com.ey.advisory.common.multitenancy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.javatuples.Pair;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.MultiThreadedGroupsTaskProcessor.ProcessingStatus;
import com.ey.advisory.core.async.domain.master.Group;

class ExecTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecTask.class);
	
	private Message message;
	private AppExecContext ctx;
	private ConcurrentHashMap<String, 
			Pair<ProcessingStatus, Exception>> tasksMap;
	private Group group;
	private String origGroup;
	private CountDownLatch latch;
	private DefaultMultiTenantTaskProcessor origProcessor;
	
	public ExecTask(Message message, AppExecContext ctx, 
				ConcurrentHashMap<String, 
				Pair<ProcessingStatus, Exception>> tasksMap, 
				Group grp, String origGroup, CountDownLatch latch, 
				DefaultMultiTenantTaskProcessor origProcessor) {
		this.message = message;
		this.ctx = ctx;
		this.tasksMap = tasksMap;
		this.group = grp;
		this.origGroup = origGroup;
		this.origProcessor = origProcessor;
		this.latch = latch;
	}
	
	public void run() {
		try {
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format("Executing  "
						+ "MultiTenantTaskProcessor for processing "
						+ "message for TaskType: '%s' for group: '%s'", 
						message.getMessageType(), group.getGroupCode());
				LOGGER.info(msg);
			}		
			MultiTenancyUtil.switchToNewTenantContext(
					message, origGroup, group.getGroupCode());
			origProcessor.executeForGroup(group, message, ctx);
			MultiTenancyUtil.switchBackToOldTenantContext(
					message, group.getGroupCode(), origGroup);	
			tasksMap.put(group.getGroupCode(), 
					new Pair<ProcessingStatus, Exception>(
							ProcessingStatus.COMPLETED, null));

			if (LOGGER.isInfoEnabled()) {
				String msg = String.format("Executed  "
						+ "MultiTenantTaskProcessor for processing "
						+ "message for TaskType: '%s' for group: '%s'", 
						message.getMessageType(), group.getGroupCode());
				LOGGER.info(msg);
			}			
			
		} catch(Exception ex) {
			// Set the status of the job as failed in the concurrent hash map.
			// Capture the exception for later logging.
			tasksMap.put(group.getGroupCode(), 
					new Pair<ProcessingStatus, Exception>(
							ProcessingStatus.FAILED, ex));
			String msg = String.format("MultiTenantTaskProcessor for "
					+ "processing message for TaskType: '%s' for group: '%s' "
					+ "failed!!", 
					message.getMessageType(), group.getGroupCode());
			LOGGER.error(msg, ex);
			
		} finally {
			latch.countDown();
		}
	}

	public Message getMessage() {
		return message;
	}

	public AppExecContext getCtx() {
		return ctx;
	}

	public ConcurrentHashMap<String, 
				Pair<ProcessingStatus, Exception>> getTasksMap() {
		return tasksMap;
	}

	public Group getGroup() {
		return group;
	}

	public String getOrigGroup() {
		return origGroup;
	}
}

