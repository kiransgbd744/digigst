/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.ProcessedVsSubmittedResponseDto;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface ProcessedVsSubmittedDao {

	public List<ProcessedVsSubmittedResponseDto> processedVsSubdRecords(
			ProcessedVsSubmittedRequestDto procesVsSubReqDto);
}
