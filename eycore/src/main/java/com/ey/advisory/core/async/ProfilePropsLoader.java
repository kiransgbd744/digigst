package com.ey.advisory.core.async;

import java.util.Map;


/**
 * The class responsible for loading the properties required to
 * initialize the executor thread pool and the Message Fetcher and Distributor
 * component. Each profile will have its own set of properties required to
 * define its behavior. The implementation of this class is responsible to
 * load the properties using the application Configuration Manager.
 * 
 * @author Sai.Pakanati
 *
 */
public interface ProfilePropsLoader {
	
	/**
	 * Returns a map of properties required to configure the 
	 * executor thread pool.
	 * 
	 * @param configManager
	 * 
	 * @return
	 */
	public Map<String, Object> loadProps();

}
