package com.ey.advisory.app.services.daos.prsummary;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryItemDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;

public interface Anx2PRSummaryRevService {

	public List<Object[]> getSummary(String gstin, List<String> retPeriods,
			String type);

	public Anx2PRSummaryHeaderDto convertObjToHeader(String entityName,
			String entityPan, String companyCode, String state,
			List<Anx2PRSProcessedResponseDto> respDto);

	public void convertObjToDetails(String entityName,
			String entityPan, String companyCode, String state, String gstin,
			String returnPeriod, List<Anx2PRSummaryItemDto> itemDtos);

	/*public Integer pushToErp(final Anx2PRSummaryHeaderDto headerDto,
			final Anx2PRSummaryDetailDto detailsDto, final String destName,
			AnxErpBatchEntity batch);*/

}
