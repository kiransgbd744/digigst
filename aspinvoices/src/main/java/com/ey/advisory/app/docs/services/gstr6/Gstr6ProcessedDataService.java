/**
 * 
 */
package com.ey.advisory.app.docs.services.gstr6;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr6.Gstr6ProcessedSummResponseDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr6ProcessedDataService {

	List<Gstr6ProcessedSummResponseDto> getGstr6ProcessedRec(
			Gstr6SummaryRequestDto dto);
	
	List<Gstr6ProcessedSummResponseDto> getGstr6ProcessedRecForRevIntg(
			Gstr6SummaryRequestDto dto);

}
