/**
 * 
 */
package com.ey.advisory.app.common;

import java.util.List;
import java.util.Map;

/**
 * @author BalaKrishna S
 *
 */
public interface SupplierdDao {
		
	/**
	 * Given a list of supplier PANs, get the corresponding names.
	 * 
	 * @param sPans
	 * @return
	 */
	public Map<String, String> getSupplierNamesForPans(List<String> sPans);
}
