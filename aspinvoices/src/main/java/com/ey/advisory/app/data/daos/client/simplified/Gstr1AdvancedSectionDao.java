package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1AdvancedVerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr1AdvancedSectionDao {

	List<Gstr1AdvancedVerticalSummaryRespDto> verticalSummarySection(
			Annexure1SummaryReqDto req);

	List<Ret1AspVerticalSummaryDto> gstinViewSection(
			Annexure1SummaryReqDto req);

	List<Ret1AspVerticalSummaryDto> SummarySectionData(
			Annexure1SummaryReqDto req);

}
