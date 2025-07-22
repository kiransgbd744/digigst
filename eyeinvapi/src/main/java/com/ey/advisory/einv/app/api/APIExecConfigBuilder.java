package com.ey.advisory.einv.app.api;

/**
 * The implementation of this interface is responsible for loading all
 * configurations required to succesfully execute an API.
 * 
 * @author Sai.Pakanati
 *
 */
public interface APIExecConfigBuilder {
	
	/**
	 * Get all the information about the parties involved in the execution
	 * of the API. This infomation will be used later to create the API
	 * request headers & URL params.
	 * 
	 * @param params
	 * @return
	 */
	public APIExecParties buildAPIPartiesInfo(APIParams params);
	
	/**
	 * Get all information related to a particular API, like the different 
	 * supported versions, proxy server configuration etc, that are required
	 * to connect to the API provider and execute the API successfully.
	 * 
	 * @param params
	 * @return
	 */
	public APIConfig buildAPIConfig(APIParams params);
}
