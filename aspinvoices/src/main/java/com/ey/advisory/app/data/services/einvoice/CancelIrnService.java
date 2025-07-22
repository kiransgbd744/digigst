/**
 * 
 */
package com.ey.advisory.app.data.services.einvoice;

import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;

/**
 * @author Arun K.A
 *
 */
public interface CancelIrnService {

	public CancelIrnERPResponseDto cancelEinvRequest(CancelIrnReqDto req);
	
	public CancelIrnERPResponseDto CancelEinvEwbRequest(CancelIrnReqDto req);

}
