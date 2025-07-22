package com.ey.advisory.auth;

/**
 * The implementation of this interface is responsible for
 * fetching the tenant  id from the request, in whichever way 
 * possible.
 * 
 * @author Mohana.Dasari
 *
 */
public interface TenantIdLocator {

	public String getTenantId(Object obj);
}
