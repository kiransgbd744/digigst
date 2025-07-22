package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1Int6BasicDocSummarySectionDao {

	public abstract List<Ret1LateFeeSummarySectionDto> lateBasicSummarySection(
			Annexure1SummaryReqDto req);
}
