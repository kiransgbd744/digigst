/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1TXPDSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1TXPDSummaryRespHandler")
public class Gstr1TXPDSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1TXPDSummaryStructure")
	private Gstr1TXPDSummaryStructure gstr1TXPDStructure;

	public List<Gstr1SummaryScreenRespDto> handleTXPDResp(
			List<Gstr1SummaryScreenRespDto> txpdSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1TxpdResp = gstr1TXPDStructure
				.gstr1TXPDResp(txpdSummaryRespList);

		return gstr1TxpdResp;
	}
}
