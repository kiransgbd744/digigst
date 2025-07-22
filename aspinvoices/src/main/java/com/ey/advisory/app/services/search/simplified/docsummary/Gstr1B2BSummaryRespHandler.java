/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1B2BSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1B2BSummaryRespHandler")
public class Gstr1B2BSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1B2BSummaryStructure")
	private Gstr1B2BSummaryStructure gstr1B2BStructure;

	public List<Gstr1SummaryScreenRespDto> handleB2BResp(
			List<Gstr1SummaryScreenRespDto> b2bSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1b2bResp = gstr1B2BStructure
				.gstr1B2BResp(b2bSummaryRespList);

		return gstr1b2bResp;
	}
	
}

