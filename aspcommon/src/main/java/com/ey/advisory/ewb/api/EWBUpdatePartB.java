/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EWBUpdatePartB {
	public APIResponse updateEwbPartB(
			UpdatePartBEwbRequestDto req, String gstin);
}
