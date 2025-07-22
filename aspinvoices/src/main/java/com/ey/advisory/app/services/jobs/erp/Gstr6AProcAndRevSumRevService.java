package com.ey.advisory.app.services.jobs.erp;


import java.util.List;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcessSummaryRespDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AReviewSummaryRespDto;

public interface Gstr6AProcAndRevSumRevService {

	public List<Gstr6AProcessSummaryRespDto> convertProcessSummary(
			String gstin) throws Exception;

	public List<Gstr6AReviewSummaryRespDto> convertReviewSummary(String gstin,
			String retPeriod);

	public Integer pushToErp(Gstr6AProcReviewHeaderSummaryRequestDto reqDto,
			String destName, AnxErpBatchEntity batch);
}
