/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1RSScreenEcomdownloadDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx1ReviewSummaryScreenEcomDownloadDao {

	List<Anx1RSScreenEcomdownloadDto> getReviewSummScreenEcomDownload(
			Annexure1SummaryReqDto request);

}
