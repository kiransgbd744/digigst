/**
 * 
 */
package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateMatchingAPManualService {

	public void executeAPManualRecon(Long configId, List<String> gstins,
			Long entityId);

}
