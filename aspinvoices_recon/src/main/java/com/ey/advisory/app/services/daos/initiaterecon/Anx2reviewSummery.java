package com.ey.advisory.app.services.daos.initiaterecon;

import java.util.List;

/**
 * @author Siva.Nandam
 *
 */

public interface Anx2reviewSummery {

	public Anx2PrReviewSummeryHeaderDto getreviewSummery(
			List<Anx2PRReviewSummeryResponseDto> entityResponse,List<String> docType);
}
