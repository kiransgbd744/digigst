/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1B2CLSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1B2CLSummaryRespHandler")
public class Gstr1B2CLSummaryRespHandler {
	
	@Autowired
	@Qualifier("Gstr1B2CLSummaryStructure")
	private Gstr1B2CLSummaryStructure gstr1B2CLStructure;

	public List<Gstr1SummaryScreenRespDto> handleB2CLResp(
			List<Gstr1SummaryScreenRespDto> b2clSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1b2clResp = gstr1B2CLStructure
				.gstr1B2CLResp(b2clSummaryRespList);

		return gstr1b2clResp;
	}
	
	
}
