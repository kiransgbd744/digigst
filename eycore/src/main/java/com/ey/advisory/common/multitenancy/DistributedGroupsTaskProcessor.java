package com.ey.advisory.common.multitenancy;

import java.util.List;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.core.async.domain.master.Group;

public interface DistributedGroupsTaskProcessor {
	
	public abstract void processTasksForGroups(
				List<Group> groups, Message message, 
				AppExecContext ctx);
}
