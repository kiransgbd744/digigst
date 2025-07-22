package com.ey.advisory.app.reconewbvsitc04;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

public interface EwbVsItc04SummaryInitiateReconDao {
	
	List<EwbVsItc04SummaryInitiateReconLineItemDto> ewb3WayInitiateRecon(EwbVsItc04SummaryInitiateReconDto request);

	List<Gstr2ReconSummaryStatusDto> findEwbVsItc04SummStatus(Long entityId,
			EwbVsItc04SummaryInitiateReconDto criteria);

}
