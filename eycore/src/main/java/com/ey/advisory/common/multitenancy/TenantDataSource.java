package com.ey.advisory.common.multitenancy;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.async.domain.master.Group;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * This class maintains the mapping of group code to DataSource object (i.e.
 * connection pool) for each group. The class implements InitializingBean, so
 * that the afterPropertiesSet() method is invoked by Spring at the time of
 * context creation (i.e at the time of application startup).
 * 
 * @author Sai.Pakanati
 *
 */
@Slf4j
@Service("tenantDataSource")
public class TenantDataSource{

	private static final String ERR_MSG = "Tenant DB details not available for Group '%s': ";

	@Autowired
	@Qualifier("TomcatDataSourceBuilder")
	private TenantDataSourceBuilder tdsBuilder;
	
	@Autowired
    private TenantDataSourceProperties dataSourceProperties;
	
	private Map<String, Object> groupCodeMap = new ConcurrentHashMap<>();

	private Map<String, Object> mBeanMap = new ConcurrentHashMap<>();

	/**
	 * This map to holds the mapping between the group code and the data source.
	 */
	@Autowired
	private Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

	/**
	 * Group service object.
	 */
	@Autowired
	private GroupService groupService;
	
	/**
	 * This is after property set method. It gets all the groups and based on it
	 * creates dynamic data source. Dynamic data source will be placed into
	 * dataSourceMap map.
	 */
	@PostConstruct
	public void init() {
		
		LOGGER.error("Inside afterPropertiesSet");
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("AfterPropertiesSet method is being invoked by "
					+ "Spring on the TenantDataSource instance");
		}
		
		// Get the map of GroupName/Group from the Master DB.
		Map<String, Group> groupInfoMap = groupService.getGroupMap();
		
		LOGGER.error("Inside afterPropertiesSet {} ",groupInfoMap);

		// Throw exception if none of the groups are configured in the DB.
		if(groupInfoMap == null || groupInfoMap.isEmpty()) {
			throw new TenantNotFoundException(
					"Tenants are not configured in the Master DB");			
		}

		groupInfoMap.forEach((groupCode, group) -> {
			DataSource  dataSource = 
					tdsBuilder.buildDataSource(group, dataSourceProperties);
			if (dataSource != null) {
				dataSourceMap.put(groupCode, dataSource);
			} else {
				LOGGER.error(String.format(ERR_MSG, groupCode));
			}
		});
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("AfterPropertiesSet method invocation by "
					+ "Spring on the TenantDataSource instance finished"
					+ "execution");
		}
	}


	/**
	 * This method is to get the data source for a given tenant identifier
	 * (group).
	 * 
	 * @param groupCode
	 * @return
	 */
	public DataSource getDataSource(String groupCode) {

		if (groupCode == null || groupCode.isEmpty()) {
			String msg = "Group code cannot be null or empty";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		LOGGER.debug("inside get DataSource");
		groupCodeMap.putIfAbsent(groupCode, new Object());
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		Object syncObj = groupCodeMap.get(groupCode);
		synchronized (syncObj) {
			Group group = groupService.getGroupInfo(groupCode);
			org.apache.tomcat.jdbc.pool.DataSource dataSource = null;
			if (group == null) {
				try {
					String msg = String.format(
							"Group is not available for groupcode '%s'. "
									+ "Synchronizing the group from DB",
							groupCode);
					LOGGER.warn(msg);
					group = groupService.syncGroupFromDB(groupCode);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								String.format("Configuring the data source for "
										+ "group: '%s'...", groupCode));
					}

					LOGGER.debug("group {}", group.getGroupCode());
					dataSource = (org.apache.tomcat.jdbc.pool.DataSource) tdsBuilder
							.buildDataSource(group, dataSourceProperties);
					LOGGER.debug("dataSource {}", dataSource);

					if (dataSource == null) {
						String errMsg = String.format(
								"Invalid DB Config parameters "
										+ "detected for the Group: '%s'. "
										+ "Returning null Data Source",
								group.getGroupCode());
						LOGGER.error(errMsg);
						throw new AppException(errMsg);
					}
					dataSourceMap.putIfAbsent(groupCode, dataSource);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								String.format("Configured the data source for "
										+ "group: '%s'!!", groupCode));
					}
				} catch (Exception ex) {
					String errMsg = String.format(
							"Error while synching group '%s' for first time",
							groupCode);
					LOGGER.error(errMsg, ex);
					throw new AppException(errMsg, ex);
				}
			}
			Object mBean = mBeanMap.get(group.getGroupCode());
			if (mBean == null) {
				String objectName = String.format(
						"com.ey.digigst.datasources:type=%s",
						group.getGroupCode());
				try {
					ObjectName mbeanName = new ObjectName(objectName);
					dataSource =
							(org.apache.tomcat.jdbc.pool.DataSource)
							dataSourceMap.get(group.getGroupCode());
					dataSource.createPool();
					server.registerMBean(dataSource.getPool().getJmxPool(),
							mbeanName);
					mBeanMap.put(group.getGroupCode(), mbeanName);
				} catch (InstanceAlreadyExistsException ex) {
					LOGGER.warn("MBean has been already created in Server {}",
							objectName);
				} catch (Exception ex) {
					String errMsg = String.format(
							"Error while registring mBean for Group '%s' for first time",
							groupCode);
					LOGGER.error(errMsg, ex);
					throw new AppException(errMsg, ex);
				}
			}

		}
		final DataSource dataSource = dataSourceMap.get(groupCode);

		if (dataSource == null) {
			LOGGER.error(String.format(ERR_MSG, groupCode));
			throw new TenantNotFoundException(
					String.format(ERR_MSG, groupCode));
		}

		return dataSource;
	}

}
