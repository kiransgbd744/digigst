/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SeztSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SeztSummaryRespHandler")
public class Gstr1SeztSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1SeztSummaryStructure")
	private Gstr1SeztSummaryStructure gstr1SeztStructure;

	public List<Gstr1SummaryScreenRespDto> handleSeztResp(
			List<Gstr1SummaryScreenRespDto> seztSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1AtResp = gstr1SeztStructure
				.gstr1SeztResp(seztSummaryRespList);

		return gstr1AtResp;
	}
}
