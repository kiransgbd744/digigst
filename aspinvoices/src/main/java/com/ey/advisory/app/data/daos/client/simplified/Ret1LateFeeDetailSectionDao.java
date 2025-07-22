package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1LateFeeDetailSectionDao {

	public abstract List<Ret1LateFeeDetailSummaryDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);
	
	public abstract List<Ret1LateFeeDetailSummaryDto> loadBasicSummarySectionRet1A(
			Annexure1SummaryReqDto req);

}
