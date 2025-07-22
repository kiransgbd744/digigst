/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1TXPDASummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1TXPDASummaryRespHandler")
public class Gstr1TXPDASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1TXPDASummaryStructure")
	private Gstr1TXPDASummaryStructure gstr1TXPDAStructure;

	public List<Gstr1SummaryScreenRespDto> handleTXPDAResp(
			List<Gstr1SummaryScreenRespDto> txpdaSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1TxpdaResp = gstr1TXPDAStructure
				.gstr1TXPDAResp(txpdaSummaryRespList);

		return gstr1TxpdaResp;
	}
	
}
