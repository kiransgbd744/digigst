package com.ey.advisory.app.services.einvoice;

import com.ey.advisory.einv.dto.GenerateEWBByIrnRequest;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;

/**
 * @author Siva Reddy
 *
 */
public interface GenerateEWBByIrnService {

	public GenerateEWBByIrnResponseDto generateEwayIrnRequest(
			GenerateEWBByIrnRequest req);

}
