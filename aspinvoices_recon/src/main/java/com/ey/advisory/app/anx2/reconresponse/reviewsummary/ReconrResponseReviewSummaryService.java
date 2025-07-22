package com.ey.advisory.app.anx2.reconresponse.reviewsummary;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface ReconrResponseReviewSummaryService {

	public List<ReconrResponseReviewSummaryDto> getReconResponseSummary(
			String taxPeriod, String gstin, List<String> docTypeList,
			List<String> tableTypeList, List<String> typeList);

}
