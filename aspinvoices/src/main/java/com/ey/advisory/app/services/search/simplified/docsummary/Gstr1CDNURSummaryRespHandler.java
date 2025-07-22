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
@Service("Gstr1CDNURSummaryRespHandler")
public class Gstr1CDNURSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1CDNURSummaryStructure")
	private Gstr1CDNURSummaryStructure gstr1CDNURStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleCDNURResp(
			List<Gstr1SummaryScreenRespDto> cdnurSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1cdnurResp = gstr1CDNURStructure
				.gstr1CDNURResp(cdnurSummaryRespList);

		return gstr1cdnurResp;
	}
	
}
