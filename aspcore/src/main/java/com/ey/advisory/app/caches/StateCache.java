package com.ey.advisory.app.caches;

/**
 * @author Siva.Nandam
 *
 */
public interface StateCache {
	public int findStateCode(String stateCode) ;
	public String getStateName(String stateCode);
	
}
