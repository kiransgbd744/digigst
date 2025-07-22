/**
 * 
 */
package com.ey.advisory.services.gstr3b.itc.reclaim;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BGetITCReclaimService {
	
	public String get3BReclaimAmount(String gstin, String taxPeriod);

}
