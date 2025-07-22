/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.ProcessedVsSubmittedResponseDto;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1AProcessedVsSubmittedDao {

	public List<ProcessedVsSubmittedResponseDto> processedVsSubdRecords(
			ProcessedVsSubmittedRequestDto procesVsSubReqDto);
}
