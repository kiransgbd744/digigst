package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1BasicDocSummarySectionDao {

	public abstract List<Ret1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);
	
}
