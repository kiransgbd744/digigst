/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface UpdateEWBTransporter {
	public APIResponse updateEWBTransporter(
			UpdateEWBTransporterReqDto req, String gstin);

}
