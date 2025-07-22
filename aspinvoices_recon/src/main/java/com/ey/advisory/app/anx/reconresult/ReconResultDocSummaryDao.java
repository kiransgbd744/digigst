/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface ReconResultDocSummaryDao {

	
	
	public List<ReconResultDocSummaryRespDto> findReconResultDocSummaryDetails(
			ReconResultDocSummaryReqDto reqDto, String validQuery);
}
