/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface CancelEWB {
	
	public APIResponse cancelEwb(CancelEwbReqDto req, String gstin);

}
