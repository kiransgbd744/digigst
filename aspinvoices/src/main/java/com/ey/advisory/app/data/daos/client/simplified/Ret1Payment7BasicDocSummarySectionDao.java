package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Ret1PaymentSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1Payment7BasicDocSummarySectionDao {

	
	
	public abstract List<Ret1PaymentSummarySectionDto> lateBasicSummarySection(
			Annexure1SummaryReqDto req);
}
