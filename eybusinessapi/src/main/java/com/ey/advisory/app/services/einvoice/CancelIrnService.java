/**
 * 
 */
package com.ey.advisory.app.services.einvoice;

import java.util.List;

import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;

/**
 * @author Arun K.A
 *
 */
public interface CancelIrnService {

	public List<CancelIrnERPResponseDto> cancelEinvRequest(CancelIrnReqList req);

}
