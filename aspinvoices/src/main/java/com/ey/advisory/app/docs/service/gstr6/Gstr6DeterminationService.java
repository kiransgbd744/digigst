package com.ey.advisory.app.docs.service.gstr6;

import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.DocSeriesSDeleteReqDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DeterminationResponseDto;
import com.ey.advisory.core.search.SearchResult;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public interface Gstr6DeterminationService {

	SearchResult<Gstr6DeterminationResponseDto> getGstr6Determinationvalues(
			Anx1ReportSearchReqDto reqDto);
	
	SearchResult<Gstr6DeterminationResponseDto> getGstr6TurnOvervalues(
			Anx1ReportSearchReqDto reqDto);

	void persistData(Gstr6DeterminationResponseDto dto);

	void deleteData(DocSeriesSDeleteReqDto dtos);
}
