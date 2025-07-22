package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1Refund8SummarySectionDao {

	public abstract List<Ret1RefundSummarySectionDto> lateBasicSummarySection(
			Annexure1SummaryReqDto req);
	
}
