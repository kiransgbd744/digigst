package com.ey.advisory.common;

/**
 * The implementation of this class can use OS specific techniques to 
 * extract the hostname.
 * 
 * @author Sai.Pakanati
 *
 */
public interface HostNameResolver {
	
	public abstract String getLocalHostName();
}
