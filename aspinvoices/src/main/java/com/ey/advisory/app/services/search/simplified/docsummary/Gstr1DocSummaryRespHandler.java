/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1DocSummaryStructure;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1DocSummaryRespHandler")
public class Gstr1DocSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1DocSummaryStructure")
	private Gstr1DocSummaryStructure gstr1DocStructure;

	public List<Gstr1SummaryScreenDocRespDto> handleDocResp(
			List<Gstr1SummaryScreenDocRespDto> docSummaryRespList) {

		List<Gstr1SummaryScreenDocRespDto> gstr1docResp = gstr1DocStructure
				.gstr1DocResp(docSummaryRespList);

		return gstr1docResp;
	}
	
}
