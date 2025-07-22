/**
 * 
 */
package com.ey.advisory.ewb.api;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface GenerateEwayBill {
	public APIResponse generateEwbill(EwayBillRequestDto requestDto,
			String gstin);
}
