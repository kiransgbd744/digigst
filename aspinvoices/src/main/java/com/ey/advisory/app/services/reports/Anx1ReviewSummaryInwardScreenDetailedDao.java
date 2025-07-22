/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1RSScreenInwarddetailedDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx1ReviewSummaryInwardScreenDetailedDao {

	List<Anx1RSScreenInwarddetailedDto> getReviewSummInvScreenDetailDownload(
			Annexure1SummaryReqDto request);

}
