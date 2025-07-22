package com.ey.advisory.common.multitenancy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * This TaskProcessor can be used to execute the same task for different
 * tenants.
 * 
 * @author Sai.Pakanati
 *
 */
public abstract class DefaultMultiTenantTaskProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultMultiTenantTaskProcessor.class);

	private static final int NO_OF_PARALLEL_GROUPS_TO_PROCESS = 10;

	private static final int MAX_NO_OF_PARALLEL_GROUPS_TO_PROCESS = 24;

	private static final String NO_OF_PARALLEL_GROUPS_KEY = "async.multitenancy.parallelgroupcount";

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("groupService")
	private GroupService grpService;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Executing DefaultMultiTenantTaskProcessor.execute() "
							+ "method for TaskType: '%s'",
					message.getMessageType());
			LOGGER.debug(logMsg);
		}

		// Load all groups from the system.
		List<Group> allGroups = grpService.getAllGroups();
		allGroups = (allGroups != null) ? allGroups : new ArrayList<>();

		// Get all active groups available in the system that have
		// valid DB configuration.
		List<Group> validGroups = allGroups.stream()
				.filter(grp -> grp.getIsActive() && grp.hasValidDBConfig())
				.collect(Collectors.toCollection(ArrayList::new));

		// Log the condition where the number of valid groups is zero and
		// throw an exception. This will mark the task as failed. it will not
		// allocate further resources to process the tasks.
		if (validGroups.size() == 0) {
			String msg = String.format(
					"No valid groups found in the system. "
							+ "Terminating the MultitenantTaskProcessor for "
							+ "processing TaskType: '%s' ",
					message.getMessageType());
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		// Log the number of valid groups and the total active groups.
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format(
					"%d valid groups out of %d active groups"
							+ "found in the system for processing TaskType: '%s' ",
					validGroups.size(), allGroups.size(),
					message.getMessageType());
			LOGGER.info(msg);
		}

		List<Group> specificGroups = executeForSpecificGroups(validGroups,
				message.getParamsJson());
		// Get the number of threads to use to process the list of groups.
		int threadCount = getNoOfParallelGroups(message);

		// If the valid group count is 1 or if the configured/evaluated thread
		// count for processing all groups is 1, then use the same thread as
		// this TaskProcessor to complete the task. There is no need to follow
		// a multi-threaded approach for this.
		DistributedGroupsTaskProcessor distProcessor = (specificGroups
				.size() == 1 || threadCount <= 1)
						? new SingleThreadedGroupsTaskProcessor(this)
						: new MultiThreadedGroupsTaskProcessor(this);

		distProcessor.processTasksForGroups(specificGroups, message, context);

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Executed DefaultMultiTenantTaskProcessor.execute() "
							+ "method for TaskType: '%s'!!",
					message.getMessageType());
			LOGGER.debug(logMsg);
		}
	}

	/**
	 * This method reads the configuration property to get the number of
	 * parallel groups to process the task for. If there is no property
	 * configured for parallelism, then it uses the default number of threads to
	 * use (which is currently 1). It also checks if the configured value is
	 * more than a pre-determined max value. If so, it uses max value as the
	 * parallelism configuration.
	 * 
	 * @param message
	 *            this is used to print useful logger messages.
	 * 
	 * @return
	 */
	private int getNoOfParallelGroups(Message message) {
		String threadCntStr = env.getProperty(NO_OF_PARALLEL_GROUPS_KEY);
		if (threadCntStr == null || threadCntStr.trim().isEmpty()) {
			String msg = String.format(
					"No Parallelism configured for "
							+ "MultitenantTaskProcessor. Using the default "
							+ "configuration of %d thread(s) to "
							+ "process all groups for TaskType: '%s'",
					NO_OF_PARALLEL_GROUPS_TO_PROCESS, message.getMessageType());
			LOGGER.warn(msg);
			// return the default number of groups.
			return NO_OF_PARALLEL_GROUPS_TO_PROCESS;
		}

		try {
			// If the number of thread count is greater than the number of
			// maximum threads configured for parallel group task executions,
			// then set it as the maximum number.
			int threadCount = Integer.parseInt(threadCntStr);
			if (threadCount > MAX_NO_OF_PARALLEL_GROUPS_TO_PROCESS) {
				String msg = String.format(
						"The parallelism configured for "
								+ "MultitenantTaskProcessor is more than the max value "
								+ "allowed [Configured -> %d, Max -> %d], Using max "
								+ "value to process TaskType: '%s'",
						threadCount, MAX_NO_OF_PARALLEL_GROUPS_TO_PROCESS,
						MAX_NO_OF_PARALLEL_GROUPS_TO_PROCESS,
						message.getMessageType());
				LOGGER.error(msg);
				return MAX_NO_OF_PARALLEL_GROUPS_TO_PROCESS;
			}
			// Return the configured thread count.
			return threadCount;

		} catch (NumberFormatException ex) {
			String msg = String.format(
					"Invalid Number configured for "
							+ "MultitenantTaskProcessor -> '%s'. Using the default "
							+ "configuration of %d threads to process all groups for "
							+ "TaskType: '%s'",
					threadCntStr, NO_OF_PARALLEL_GROUPS_TO_PROCESS,
					message.getMessageType());
			LOGGER.error(msg, ex);
			return NO_OF_PARALLEL_GROUPS_TO_PROCESS;
		}
	}

	/**
	 * Execute the message for a single group. This has to be overridden in the
	 * derived classes.
	 * 
	 * @param groupCode
	 * @param message
	 * @param ctx
	 */
	public abstract void executeForGroup(Group group, Message message,
			AppExecContext ctx);

	private List<Group> executeForSpecificGroups(List<Group> validGrps,
			String refParams) {
		if (StringUtils.isEmpty(refParams))
			return validGrps;
		try {
			JsonObject jsonObj = JsonParser.parseString(refParams)
					.getAsJsonObject();
			JsonArray configuredGrps = jsonObj.get("GroupCodes")
					.getAsJsonArray();
			if (configuredGrps.isEmpty())
				LOGGER.warn("There are no groups configured in REF Params,"
						+ " Hence executing for all the active groups");
			Gson gson = new Gson();
			List<String> list = gson.fromJson(configuredGrps,
					new TypeToken<List<String>>() {
					}.getType());
			List<Group> eligibleGrps = validGrps.stream()
					.filter(grp -> list.contains(grp.getGroupCode()))
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Groups configured for Periodic Execution is {}",
						eligibleGrps);
			return eligibleGrps;
		} catch (Exception ex) {
			LOGGER.error(
					"Invalid Json configured for REF Params - {}, Hence Executing for all the Groups",
					refParams);
			return validGrps;
		}

	}
}
