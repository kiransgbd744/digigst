package com.ey.advisory.app.data.services.einvoice;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;

/**
 * @author Siva Reddy
 *
 */

public interface GenerateEWBByIrnService {

	public GenerateEWBByIrnResponseDto generateEwayIrnRequest(
			GenerateEWBByIrnNICReqDto req, OutwardTransDocument doc);

}
