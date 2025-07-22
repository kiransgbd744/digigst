/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface AddMultiVehicleDetails {
	public APIResponse addMultiVehicle(
			AddMultiVehicleDetailsReqDto req, String gstin);

}
