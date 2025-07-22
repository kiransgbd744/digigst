package com.ey.advisory.app.services.einvoice;

import com.ey.advisory.einv.dto.GetEWBDetailsByIrnRespDto;

/**
 * @author Siva Reddy
 *
 */
public interface GetEWBDetailsByIrnService {

	GetEWBDetailsByIrnRespDto getEWBDetailsByIrn(String irnNo, String gstin);

}
