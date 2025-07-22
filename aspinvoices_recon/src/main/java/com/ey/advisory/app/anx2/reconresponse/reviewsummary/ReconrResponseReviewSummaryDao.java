package com.ey.advisory.app.anx2.reconresponse.reviewsummary;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface ReconrResponseReviewSummaryDao {
	
	public List<ReconrResponseReviewSummaryDto> findReconResponseSummary(
			String taxPeriod, String gstin, List<String> docTypeList,
			List<String> tableTypeList, List<String> typeList);

}
