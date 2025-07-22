/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1RSScreenDetailOutwarddownloadDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx1ReviewSummaryScreenDetailedDownloadDao {
	
	List<Anx1RSScreenDetailOutwarddownloadDto> getReviewDetailScreenDownload(
			Annexure1SummaryReqDto request);

}



