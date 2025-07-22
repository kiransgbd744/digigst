/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1EXPSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1EXPSummaryRespHandler")
public class Gstr1EXPSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1EXPSummaryStructure")
	private Gstr1EXPSummaryStructure gstr1EXPStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleEXPResp(
			List<Gstr1SummaryScreenRespDto> expSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1expResp = gstr1EXPStructure
				.gstr1EXPResp(expSummaryRespList);

		return gstr1expResp;
	}
	
	
}
