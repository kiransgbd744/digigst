/**
 * 
 */
package com.ey.advisory.services.gstr3b.itc.reclaim;

import java.util.List;

/**
 * @author vishal.verma
 *
 */

public interface Gstr3BValidateITCReclaimService {

	public List<Gstr3BValidateItcReclaimDto> validate3BReclaimAmount(
			String gstin, String taxPeriod);

}
