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
@Service("Gstr1B2CSSummaryRespHandler")
public class Gstr1B2CSSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1B2CSSummaryStructure")
	private Gstr1B2CSSummaryStructure gstr1B2CSStructure;
	
	public List<Gstr1SummaryScreenRespDto> handleB2CSResp(
			List<Gstr1SummaryScreenRespDto> b2csSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1b2csResp = gstr1B2CSStructure
				.gstr1B2CSResp(b2csSummaryRespList);

		return gstr1b2csResp;
	}
	
	
}
