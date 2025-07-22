package com.ey.advisory.gstnapi.services;

import java.util.List;
import java.util.Map;

/**
 * This interface represents the service used to query GSTN Auth token related
 * information.
 * 
 * @author Arun KA
 *
 */
public interface GSTNAuthTokenService {
	
	/**
	 * For the specified gstins, get a Map of GSTIN-status, where GSTIN is the
	 * input GSTIN and status is "A" if the auth token expiry status is 
	 * available in the DB and is active; and "I" otherwise.
	 */	
	public Map<String, String> getAuthTokenStatusForGstins(List<String> gstins);
	
	/**
	 * Return the AuthToken status for a single GSTIN. If the auth token status
	 * is availabe in the DB and is active, "A" is returned; 
	 * otherwise "I" is returned.
	 * 
	 */	
	public String getAuthTokenStatusForGstin(String gstin);
	
	/**
	 * Get the list of statuses for the specified auth tokens as a List. The
	 * order of the status in the return list will be exactly same as the
	 * GSTINs in the input list. Also, the no. of elements in the return list
	 * is exactly same as that of the input list.
	 */	
	public List<String> getAuthTokenStatuses(List<String> gstins);
	
	
	public Map<String, String> getAuthTokenStatusForGroup();

	
}
