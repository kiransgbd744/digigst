/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1NilSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1NilSummaryRespHandler")
public class Gstr1NilSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1NilSummaryStructure")
	private Gstr1NilSummaryStructure gstr1NilStructure;

	public List<Gstr1SummaryScreenNilRespDto> handleNilResp(
			List<Gstr1SummaryScreenNilRespDto> nilSummaryRespList) {

		List<Gstr1SummaryScreenNilRespDto> gstr1NilResp = gstr1NilStructure
				.gstr1NilResp(nilSummaryRespList);

		return gstr1NilResp;
	}
	
	
}
