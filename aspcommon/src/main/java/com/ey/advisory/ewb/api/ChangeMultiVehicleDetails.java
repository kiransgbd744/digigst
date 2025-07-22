/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetaiilsReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface ChangeMultiVehicleDetails {
	
	public APIResponse changeMultiVehicle(ChangeMultiVehicleDetaiilsReqDto req,
			String gstin);

}
