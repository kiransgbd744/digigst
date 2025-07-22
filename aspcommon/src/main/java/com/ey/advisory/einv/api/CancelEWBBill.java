package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.CancelEWBBillNICReqDto;

/**
 * @author Siva Reddy
 *
 */
public interface CancelEWBBill {

	public APIResponse cancelEWBservice(CancelEWBBillNICReqDto req);

}
