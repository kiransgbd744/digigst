/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface InitiateMultiVehicle {
	public APIResponse initiateMultiVehicle(
			InitiateMultiVehicleReqDto req, String gstin);

}
