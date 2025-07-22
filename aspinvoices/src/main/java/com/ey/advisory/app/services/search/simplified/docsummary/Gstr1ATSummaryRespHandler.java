/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1ATSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1ATSummaryRespHandler")
public class Gstr1ATSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1ATSummaryStructure")
	private Gstr1ATSummaryStructure gstr1ATStructure;

	public List<Gstr1SummaryScreenRespDto> handleATResp(
			List<Gstr1SummaryScreenRespDto> atSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1AtResp = gstr1ATStructure
				.gstr1ATResp(atSummaryRespList);

		return gstr1AtResp;
	}
}
