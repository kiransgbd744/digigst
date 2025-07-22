/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface ReconResultDocSummaryService {

	public List<ReconResultDocSummaryRespDto> getReconResultDocSummDetails(
			ReconResultDocSummaryReqDto req);

}
