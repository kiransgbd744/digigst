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
@Service("Gstr1CDNURASummaryRespHandler")
public class Gstr1CDNURASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1CDNURASummaryStructure")
	private Gstr1CDNURASummaryStructure gstr1CDNURAStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleCDNURAResp(
			List<Gstr1SummaryScreenRespDto> cdnuraSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1cdnuraResp = gstr1CDNURAStructure
				.gstr1CDNURAResp(cdnuraSummaryRespList);

		return gstr1cdnuraResp;
	}
	
}
