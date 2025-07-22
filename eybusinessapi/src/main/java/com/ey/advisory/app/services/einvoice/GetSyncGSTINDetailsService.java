package com.ey.advisory.app.services.einvoice;

import com.ey.advisory.einv.dto.GetSyncGSTINDetailsERPRespDto;

/**
 * @author Siva Reddy
 *
 */
public interface GetSyncGSTINDetailsService {

	
	GetSyncGSTINDetailsERPRespDto getSyncDetails(String irnNo,String gstin);

}
