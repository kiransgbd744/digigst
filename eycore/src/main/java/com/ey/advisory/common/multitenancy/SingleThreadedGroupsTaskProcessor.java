package com.ey.advisory.common.multitenancy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.core.async.domain.master.Group;

public class SingleThreadedGroupsTaskProcessor
		implements DistributedGroupsTaskProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(
					SingleThreadedGroupsTaskProcessor.class);
	
	private DefaultMultiTenantTaskProcessor origProcessor;
	
	public SingleThreadedGroupsTaskProcessor(
			DefaultMultiTenantTaskProcessor origProcessor) {
		this.origProcessor = origProcessor;
	}
	
	@Override
	public void processTasksForGroups(List<Group> groups, Message message,
			AppExecContext ctx) {
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Executing the logic within the same "
					+ "thread as the MultiTenantTaskProcessor for processing "
					+ "message for TaskType: '%s', [ValidGroupCount = %d]", 
					message.getMessageType(), groups.size());
			LOGGER.info(msg);
		}
		
		// Get the original group code.
		String origGroup = message.getGroupCode();
		groups.stream()
			.forEach((grp) -> {
				MultiTenancyUtil.switchToNewTenantContext(
						message, origGroup, grp.getGroupCode());
				if (LOGGER.isInfoEnabled()) {
					String msg = String.format("Executing  "
							+ "MultiTenantTaskProcessor for processing "
							+ "message for TaskType: '%s' for group: '%s'", 
							message.getMessageType(), grp.getGroupCode());
					LOGGER.info(msg);
				}				
				origProcessor.executeForGroup(grp, message, ctx);
				if (LOGGER.isInfoEnabled()) {
					String msg = String.format("Executed  "
							+ "MultiTenantTaskProcessor for processing "
							+ "message for TaskType: '%s' for group: '%s'", 
							message.getMessageType(), grp.getGroupCode());
					LOGGER.info(msg);
				}			
				MultiTenancyUtil.switchBackToOldTenantContext(
						message, grp.getGroupCode(), origGroup);
			});		

		if (LOGGER.isInfoEnabled()) {		
			String msg = String.format("Executed the logic within the same "
					+ "thread as the MultiTenantTaskProcessor for processing "
					+ "message for TaskType: '%s', [ValidGroupCount = %d]!!", 
					message.getMessageType(), groups.size());
			LOGGER.info(msg);
		}

	}

}
