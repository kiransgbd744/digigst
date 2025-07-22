package com.ey.advisory.app.data.services.einvoice;

import com.ey.advisory.einv.dto.GetSyncGSTINDetailsResponseDto;

/**
 * @author Siva Reddy
 *
 */
public interface GetSyncGSTINDetailsService {

	
	GetSyncGSTINDetailsResponseDto getSyncDetails(String irnNo,String gstin);

}
