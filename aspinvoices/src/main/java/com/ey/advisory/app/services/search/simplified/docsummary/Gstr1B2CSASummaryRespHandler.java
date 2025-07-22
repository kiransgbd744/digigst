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
@Service("Gstr1B2CSASummaryRespHandler")
public class Gstr1B2CSASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1B2CSASummaryStructure")
	private Gstr1B2CSASummaryStructure gstr1B2CSAStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleB2CSAResp(
			List<Gstr1SummaryScreenRespDto> b2csaSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1b2csaResp = gstr1B2CSAStructure
				.gstr1B2CSAResp(b2csaSummaryRespList);

		return gstr1b2csaResp;
	}
	
	
}
