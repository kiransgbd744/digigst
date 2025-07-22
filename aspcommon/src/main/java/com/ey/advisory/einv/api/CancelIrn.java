/**
 * 
 */
package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.CancelIrnReqDto;

/**
 * @author Arun K.A
 *
 */
public interface CancelIrn {

	public APIResponse cancelIrn(CancelIrnReqDto req);

}
