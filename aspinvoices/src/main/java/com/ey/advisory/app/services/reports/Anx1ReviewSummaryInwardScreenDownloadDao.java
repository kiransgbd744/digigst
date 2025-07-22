/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1RSScreenInwarddownloadDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx1ReviewSummaryInwardScreenDownloadDao {

	List<Anx1RSScreenInwarddownloadDto> getReviewSummInvScreenDownload(
			Annexure1SummaryReqDto request);

}
