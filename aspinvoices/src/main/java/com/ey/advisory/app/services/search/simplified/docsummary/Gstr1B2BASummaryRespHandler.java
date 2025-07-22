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
@Service("Gstr1B2BASummaryRespHandler")
public class Gstr1B2BASummaryRespHandler {
	
	@Autowired
	@Qualifier("Gstr1B2BASummaryStructure")
	private Gstr1B2BASummaryStructure gstr1B2BStructure;

	public List<Gstr1SummaryScreenRespDto> handleB2BAResp(
			List<Gstr1SummaryScreenRespDto> b2bSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1b2baResp = gstr1B2BStructure
				.gstr1B2BAResp(b2bSummaryRespList);

		return gstr1b2baResp;
	}
	

}
