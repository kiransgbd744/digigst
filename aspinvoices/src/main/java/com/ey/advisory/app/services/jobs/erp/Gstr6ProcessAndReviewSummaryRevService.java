package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcAndReviewSumFinalDto;

public interface Gstr6ProcessAndReviewSummaryRevService {

	public Gstr6RevIntProcAndReviewSumFinalDto getGstr6RevIntProcAnReview(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode);
}
