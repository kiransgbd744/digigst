package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.Anx1AProcessReviewSummaryReqHeaderItemDto;

public interface Anx1AProcessAndReviewSummaryRevIntegDocs {

	/**
	 * 
	 * @param dto
	 * @param destinationName
	 * @return
	 */
	/*public Integer pushToErp(Anx1AProcessReviewSummaryReqHeaderItemDto dto,
			String destinationName, AnxErpBatchEntity batch);*/

	public Anx1AProcessReviewSummaryReqHeaderItemDto convertProcReviewSum(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode);
}
