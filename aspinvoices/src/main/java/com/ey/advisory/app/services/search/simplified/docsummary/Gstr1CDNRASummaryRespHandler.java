/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1CDNRASummaryRespHandler")
public class Gstr1CDNRASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1CDNRASummaryStructure")
	private Gstr1CDNRASummaryStructure gstr1CDNRAStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleCDNRAResp(
			List<Gstr1SummaryScreenRespDto> cdnraSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1cdnraResp = gstr1CDNRAStructure
				.gstr1CDNRAResp(cdnraSummaryRespList);

		return gstr1cdnraResp;
	}

	
}
