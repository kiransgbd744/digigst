package com.ey.advisory.app.data.services.einvoice;

import com.ey.advisory.einv.dto.GenerateIrnResponseDto;

/**
 * @author Siva Reddy
 *
 */
public interface GetEInvDetailsService {

	GenerateIrnResponseDto getEInvDetails(String irnNo, String gstin,
			String source);

}
