package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.Ret1HeaderItemProcessReviewSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ReviewSummaryRequestItemDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface Ret1ReviewSummaryRequestDocs {

	public Integer pushToErp(Ret1HeaderItemProcessReviewSummaryReqDto reqDto,
			String destName, AnxErpBatchEntity batch);

	public List<Ret1ReviewSummaryRequestItemDto> itemProcessSummary(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> processSummaryData);

	public void getReviewSummary(String gstin, String retPeriod,
			String entityPan, String entityName, String companyCode,
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos);

}
