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
@Service("Gstr1HsnSummaryRespHandler")
public class Gstr1HsnSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1HSNSummaryStructure")
	private Gstr1HSNSummaryStructure gstr1HSNStructure;

	public List<Gstr1SummaryScreenRespDto> handleHSNResp(
			List<Gstr1SummaryScreenRespDto> hsnSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1HSNResp = gstr1HSNStructure
				.gstr1HSNResp(hsnSummaryRespList);

		return gstr1HSNResp;
	}
}
