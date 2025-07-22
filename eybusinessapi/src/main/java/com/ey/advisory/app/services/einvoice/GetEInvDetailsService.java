package com.ey.advisory.app.services.einvoice;

import com.ey.advisory.einv.dto.GenerateIrnResponseDto;

/**
 * @author Siva Reddy
 *
 */
public interface GetEInvDetailsService {

	GenerateIrnResponseDto getEInvDetails(String irnNo, String gstin,
			String source);

	GenerateIrnResponseDto getEInvDtlsByDocDtls(String docType, String docNum,
			String docDate, String gstin);

}
