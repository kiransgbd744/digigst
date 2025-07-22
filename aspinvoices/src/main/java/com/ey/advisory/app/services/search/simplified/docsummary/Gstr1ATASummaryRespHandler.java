/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1ATASummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1ATASummaryRespHandler")
public class Gstr1ATASummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1ATASummaryStructure")
	private Gstr1ATASummaryStructure gstr1ATAStructure;

	public List<Gstr1SummaryScreenRespDto> handleATAResp(
			List<Gstr1SummaryScreenRespDto> ataSummaryRespList) {

		List<Gstr1SummaryScreenRespDto> gstr1AtaResp = gstr1ATAStructure
				.gstr1ATAResp(ataSummaryRespList);

		return gstr1AtaResp;
	}
}
