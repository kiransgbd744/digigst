package com.ey.advisory.core.config;

import java.util.Map;

/**
 * This is the core interface to access configuration data of our application.
 * Every configuration data will have a category name and a unique key 
 * associated with it. This interface abstracts out the actual storage of the
 * configuration elements from the caller of the code. Internally, the 
 * implementation can choose to store the configuration in a database or an
 * in memory cache like REDIS OR a java properties file OR a combination of
 * multiple strategies. For example, a few category configurations can reside
 * in REDIS while the others can reside in the DB or as part of properties 
 * file and the caller of this code is totally shielded from this, thereby
 * preventing the client code to remain unchanged based on the changes in 
 * storage of the actual configuration elements.
 * 
 * We also have a concept of 'Sensitive' vs 'In-Sensitive' configurations.
 * Certain configurations like SMTP mail ids and passwords are considered 
 * as sensitive configurations. Such classifications can also be shielded
 * from the caller of this code. 
 * 
 * Every method in this interface has two overloaded versions - one without a 
 * Group Code and the other, with one. This gives us the flexibility to 
 * override configuration values based on a particular group code. If a group
 * code is not specified, then 'DEFAULT' is assumed. The 'DEFAULT' group code
 * contains the configurations for all groups. Any overriding can be done
 * by defining the same configuration value with a different value for the 
 * required group code.
 * 
 * @author Sai.Pakanati
 *
 */
public interface ConfigManager {

	/**
	 * Fetch a single configuration object using the specified category and key.
	 * 
	 * @param category
	 * @param key
	 * @return
	 */
	public Config getConfig(String category, String key);	
	public Config getConfig(String category, String key, String groupCode);
	
	/**
	 * Fetch a list of group configurations
	 * 
	 * @param category
	 * @param keyStartsWith
	 * @return
	 */
	public Map<String, Config> getConfigs(
				String category, String keyStartsWith);	
	public Map<String, Config> getConfigs(
			String category, String keyStartsWith, String groupCode);	

}