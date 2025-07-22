/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ProcessedScreenDto;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx1ProcessedScreenDownloadDao {

	List<Anx1ProcessedScreenDto> getProcessedScreenDownload(
			Anx1ProcessedRecordsReqDto request);

}
