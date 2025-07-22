/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface ExtendEWB {
	
	 APIResponse extendEwb(
			ExtendEWBReqDto req, String gstin);
}
