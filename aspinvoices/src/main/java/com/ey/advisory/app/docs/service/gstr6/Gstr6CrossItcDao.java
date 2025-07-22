/**
 * 
 */
package com.ey.advisory.app.docs.service.gstr6;

import com.ey.advisory.app.docs.dto.gstr6.Gstr6DeterminationResponseDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;

/**
 * @author Mahesh.Golla
 *
 * 
 */
public interface Gstr6CrossItcDao {

	SearchResult<Gstr6DeterminationResponseDto> gstr6DeterminationDetails(
			SearchCriteria criteria);
	
	SearchResult<Gstr6DeterminationResponseDto> gstr6TurnOverDetails(
			SearchCriteria criteria);

}
