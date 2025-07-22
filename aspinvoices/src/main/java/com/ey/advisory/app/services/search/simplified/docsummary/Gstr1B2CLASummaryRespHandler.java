/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1B2CLASummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1B2CLASummaryRespHandler")
public class Gstr1B2CLASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1B2CLASummaryStructure")
	private Gstr1B2CLASummaryStructure gstr1B2CLAStructure;
	public List<Gstr1SummaryScreenRespDto> handleB2CLAResp(
			List<Gstr1SummaryScreenRespDto> b2claSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1b2claResp = gstr1B2CLAStructure
				.gstr1B2CLAResp(b2claSummaryRespList);

		return gstr1b2claResp;
	}
	
}
