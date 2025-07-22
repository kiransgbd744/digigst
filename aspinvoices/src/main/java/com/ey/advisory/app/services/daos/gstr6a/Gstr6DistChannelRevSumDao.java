package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeDistributionEntity;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Gstr6DistChannelRevSumDao {

	public List<Object[]> getSummaryDetails(
			final Annexure1SummaryReqDto reqDto);
	
	public List<Gstr6DigiComputeDistributionEntity> getGstinSummaryDetails(
			final Annexure1SummaryReqDto reqDto);
}
