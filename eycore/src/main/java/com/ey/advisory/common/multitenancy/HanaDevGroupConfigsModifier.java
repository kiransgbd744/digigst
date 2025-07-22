package com.ey.advisory.common.multitenancy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.async.domain.master.GroupConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("DefaultDBConfigsModifier")
@Profile("!hanadev")
public class HanaDevGroupConfigsModifier implements GroupConfigsModifier {

	private static final Logger LOGGER = 
				LoggerFactory.getLogger(HanaDevGroupConfigsModifier.class);
	/**
	 * This map stores the JDBC URL for each group.
	 */
	private Map<String, String> groupJdbcUrlMap = new HashMap<>();
	
	@PostConstruct
	public void initProperties() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("About to load the Group Code to HANA DB "
					+ "JDBC URL Mapping from JSON");
		}
		try (InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream("hanadev-jdbc-urls.json");
				InputStreamReader reader = new InputStreamReader(stream)) {
			JsonParser parser = new JsonParser();
			JsonElement root = parser.parse(reader);
			JsonObject obj = root.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
			entries.forEach(entry -> {
				String key = entry.getKey();
				JsonElement ele = entry.getValue();
				String value = ele.getAsString();
				groupJdbcUrlMap.put(key, value);
			});
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Loaded the Group Code to HANA DB "
						+ "JDBC URL Mapping from JSON!!");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error occured while "
					+ "loading the Group Code to HANA DB mapping from Json";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
	
	@Override
	public Map<String, List<GroupConfig>> alterGroupConfigs(
			Map<String, List<GroupConfig>> configs) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "About to alter the Group Configs for HANA "
					+ "DEV Env tunnelled JDBC URLs";
			LOGGER.debug(msg);
		}
		Map<String, List<GroupConfig>> retConfigs = 
				new HashMap<>();
		
		// Find the Jdbc URL config for each group.
		configs.forEach((grpCode, grpConfigs) -> {
			List<GroupConfig> retGrpConfigs = grpConfigs.stream()
				.map(grpConfig -> replaceWithDevDBConfig(grpConfig))
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(ArrayList::new));
			retConfigs.put(grpCode, retGrpConfigs);
		});
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Altered the Group Configs for HANA "
					+ "DEV Env tunnelled JDBC URLs!!";
			LOGGER.debug(msg);
		}		
		return retConfigs;
	}
	
	private GroupConfig replaceWithDevDBConfig(GroupConfig config) {
		
		if(TenantConstant.DB_URL.equals(config.getConfigCode())) {
			GroupConfig conf = new GroupConfig();
			conf.setGroupConfigId(config.getGroupConfigId());
			conf.setConfigCode(config.getConfigCode());
			conf.setGroupCode(config.getGroupCode());
			conf.setGroupId(config.getGroupId());
			
			// Get the jdbc URL for the group configured in the json, for 
			// HANA DB dev environment. If this is not configured, then 
			// a null config will be returned.
			String jdbcUrl = groupJdbcUrlMap.get(config.getGroupCode());
			if(jdbcUrl == null) {
				String msg = String.format("The HANA Dev JDBC URL is "
						+ "not configured in the hanadev-jdbc-urls.json, "
						+ "for the group code '%s'. Connection pool will "
						+ "not be initialized for this group!!", 
						conf.getGroupCode());
				LOGGER.error(msg);
			}
			conf.setConfigValue(jdbcUrl);
			// If the jdbc url is null (jdbc url is stored in the 
			// config value), then return null. otherwise return the 
			// modified config  object with the url loaded form the json.
			return conf.getConfigValue() != null ? conf : null;
		}
		return config;
	}

	@Override
	public List<GroupConfig> alterGroupConfigs(List<GroupConfig> configs) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "About to alter the Group Configs for HANA "
					+ "DEV Env tunnelled JDBC URLs";
			LOGGER.debug(msg);
		}
		// Find the Jdbc URL config for each group.
			List<GroupConfig> retGrpConfigs = configs.stream()
				.map(grpConfig -> replaceWithDevDBConfig(grpConfig))
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Altered the Group Configs for HANA "
					+ "DEV Env tunnelled JDBC URLs!!";
			LOGGER.debug(msg);
		}		
		return retGrpConfigs;
	}
}
