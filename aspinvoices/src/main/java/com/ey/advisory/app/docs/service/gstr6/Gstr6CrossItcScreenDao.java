/**
 * 
 */
package com.ey.advisory.app.docs.service.gstr6;

import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.Gstr6CrossItcRequestDto;
import com.ey.advisory.core.search.SearchResult;

/**
 * @author Mahesh.Golla
 *
 * 
 */
public interface Gstr6CrossItcScreenDao {

	SearchResult<Gstr6CrossItcRequestDto> gstr6CrossItcScreenDetails(
			Anx1ReportSearchReqDto reqDto);

}
