package com.ey.advisory.common.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ey.advisory.common.Message;

public class MultiTenancyUtil {
	
	private MultiTenancyUtil() {}

	private static final Logger LOGGER = 
				LoggerFactory.getLogger(MultiTenancyUtil.class);
	/**
	 * Switch the multi-tenancy context to use the currently processing group.
	 * 
	 * @param message
	 * @param newGroup
	 */
	public static void switchToNewTenantContext(Message message, 
			String curGroup, String newGroup) {
		if (LOGGER.isInfoEnabled()) {
			String logMsg = String.format("Switching to new mulitenancy "
					+ "context for processing TaskType: '%s' "
					+ "[SwitchFromGroup -> '%s', SwitchToGroup -> '%s']", 
					message.getMessageType(), curGroup, newGroup);
			LOGGER.info(logMsg);
		}			
		// First set the Group code to the tenant context thread local 
		// variable. This is required for JPA/Hibernate to locate the 
		// connection pool for the appropriate tenant database.
		TenantContext.setTenantId(newGroup);
		
		// Now, set the SLF4J MDC variables to appropriate values. This will
		// ensure that the log messages generated will have relevant details
		// for debugging.
		MDC.put("jobId", message.getId() != null ? 
				String.valueOf(message.getId()) : null);
		MDC.put("userName", message.getUserName());
		MDC.put("groupCode", newGroup);
		
		// Print logger message and exit.
		if (LOGGER.isInfoEnabled()) {
			String logMsg = String.format("Switched to new mulitenancy "
					+ "context for processing TaskType: '%s' "
					+ "[SwitchFromGroup -> '%s', SwitchToGroup -> '%s']!!", 
					message.getMessageType(), curGroup, newGroup);
			LOGGER.info(logMsg);
		}	
	}
	
	/**
	 * Switch the multi-tenancy context to use the currently processing group.
	 * 
	 * @param message
	 * @param newGroup
	 */
	public static void switchToNewTenantContext(String curGroup,
			String newGroup) {
			String logMsg = String.format("Switching to new mulitenancy "
					+ "[SwitchFromGroup -> '%s', SwitchToGroup -> '%s']", 
					 curGroup, newGroup);
			LOGGER.info(logMsg);
		// First set the Group code to the tenant context thread local 
		// variable. This is required for JPA/Hibernate to locate the 
		// connection pool for the appropriate tenant database.
		TenantContext.setTenantId(newGroup);
		
		// Now, set the SLF4J MDC variables to appropriate values. This will
		// ensure that the log messages generated will have relevant details
		// for debugging.
		
		MDC.put("groupCode", newGroup);
		
		// Print logger message and exit.
			String logMsg1 = String.format("Switched to new mulitenancy "
					+ "[SwitchFromGroup -> '%s', SwitchToGroup -> '%s']!!", 
					curGroup, newGroup);
			LOGGER.info(logMsg1);
	}

	public static void switchBackToOldTenantContext(Message message, 
					String curGroup, String origGroup) {
		if (LOGGER.isInfoEnabled()) {
			String logMsg = String.format("Switching back mulitenancy context "
					+ "for TaskType: '%s' "
					+ "[SwitchFromGroup -> '%s', SwitchToGroup -> '%s']", 
					message.getMessageType(), curGroup, origGroup);
			LOGGER.info(logMsg);
		}		
		
		// First clear the tenant context variable. This will ensure that 
		// a DB connection pool for a group is not accidentally used by 
		// another code for processing for a different group.
		TenantContext.setTenantId(origGroup);
		
		// Now clear and reset the SLF4J MDC values to original values.
		MDC.clear(); // Clear the SLF4J MDC context.
		MDC.put("jobId", message.getId() != null ? 
				String.valueOf(message.getId()) : null);		
		MDC.put("userName", message.getUserName());
		MDC.put("groupCode", origGroup);
		
		if (LOGGER.isInfoEnabled()) {
			String logMsg = String.format("Switched back mulitenancy context "
					+ "for TaskType: '%s' "
					+ "[SwitchFromGroup -> '%s', SwitchToGroup -> '%s']!!", 
					message.getMessageType(), curGroup, origGroup);
			LOGGER.info(logMsg);
		}
	}	
}
