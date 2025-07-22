package com.ey.advisory.app.services.search.simplified.docsummary;

/**
 * 
 */


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;


/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1CDNRSummaryRespHandler")
public class Gstr1CDNRSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1CDNRSummaryStructure")
	private Gstr1CDNRSummaryStructure gstr1CDNRStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleCDNRResp(
			List<Gstr1SummaryScreenRespDto> cdnrSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1cdnrResp = gstr1CDNRStructure
				.gstr1CDNRResp(cdnrSummaryRespList);

		return gstr1cdnrResp;
	}
	
}
