/**
 * 
 */
package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateMatchingNonAPManualService {
	
	public void executeNonAPManualRecon(Long configId, Boolean apFlag,
			List<String> gstins);

}
