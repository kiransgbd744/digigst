package com.ey.advisory.app.services.einvoice;

import com.ey.advisory.einv.dto.CancelEWBBillRequest;
import com.ey.advisory.einv.dto.CancelEWBBillResponseDto;

/**
 * @author Siva Reddy
 *
 */
public interface CancelEWBBillService {

	public CancelEWBBillResponseDto cancelEwaybillRequest(CancelEWBBillRequest req);

}
