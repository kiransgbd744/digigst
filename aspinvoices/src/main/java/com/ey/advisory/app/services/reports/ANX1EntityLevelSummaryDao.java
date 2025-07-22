package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1EntityLevelSummaryDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public interface ANX1EntityLevelSummaryDao {

	List<Anx1EntityLevelSummaryDto> getEntityLevelSummary(
			Gstr1ReviwSummReportsReqDto request);

}
