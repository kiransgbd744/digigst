/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SezwtSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SezwtSummaryRespHandler")
public class Gstr1SezwtSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1SezwtSummaryStructure")
	private Gstr1SezwtSummaryStructure gstr1SezwtStructure;

	public List<Gstr1SummaryScreenRespDto> handleSezwtResp(
			List<Gstr1SummaryScreenRespDto> sezwtSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1SezwtResp = gstr1SezwtStructure
				.gstr1SezwtResp(sezwtSummaryRespList);

		return gstr1SezwtResp;
	}
	
}
