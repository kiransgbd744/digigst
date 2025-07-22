package com.ey.advisory.app.caches;

/**
 * @author Siva.Nandam
 *
 */
public interface HsnCache {
	public int findhsn(String hsn);
	
	public boolean isValidHSN(String hsnCode);
	
	public String findHsnDescription(String hsnCode);
	
}
