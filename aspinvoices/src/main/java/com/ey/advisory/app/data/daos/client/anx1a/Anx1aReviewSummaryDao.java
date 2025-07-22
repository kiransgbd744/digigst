package com.ey.advisory.app.data.daos.client.anx1a;

import java.util.List;

import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Anx1aReviewSummaryDao {
	public List<Object[]> getOutwardAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto);

	public List<Object[]> getInwardAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto);

	public List<Object[]> getSuppliesAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto);
}
