/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.DistributionTotalDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface DistributionReportsDao {

	
	List<DistributionTotalDto> generateFileStatusCsv(
			Anx1FileStatusReportsReqDto request);

}
