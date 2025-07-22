/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1RSScreenOutwarddownloadDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1ReviewSummaryScreenDownloadDao {

	List<Anx1RSScreenOutwarddownloadDto> getReviewSummScreenDownload(
			Annexure1SummaryReqDto request);

}
