/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1EcomSupStructure;

/**
 * @author Siva.Reddy
 *
 */
@Service("Gstr1tbl14and15SummaryRespHandler")
public class Gstr1tbl14and15SummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1EcomSupStructure")
	private Gstr1EcomSupStructure gstr1Tbl14and15Structure;
	
	public List<Gstr1SummaryScreenRespDto> handletbl14and15Resp(
			List<Gstr1SummaryScreenRespDto> summaryRespList, String taxDocType) {

		List<Gstr1SummaryScreenRespDto> gstr1EcomSupResp = gstr1Tbl14and15Structure
				.gstr1tbl14and15Resp(summaryRespList, taxDocType);

		return gstr1EcomSupResp;
	}
	
	
}
