/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1EXPASummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1EXPASummaryRespHandler")
public class Gstr1EXPASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1EXPASummaryStructure")
	private Gstr1EXPASummaryStructure gstr1EXPAStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleEXPAResp(
			List<Gstr1SummaryScreenRespDto> expaSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1expaResp = gstr1EXPAStructure
				.gstr1EXPAResp(expaSummaryRespList);

		return gstr1expaResp;
	}
	
}
