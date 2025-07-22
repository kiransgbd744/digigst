/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface GenerateConsolidatedEWB {
	
	public APIResponse consolidateEWB(
			ConsolidateEWBReqDto req, String gstin);

}
