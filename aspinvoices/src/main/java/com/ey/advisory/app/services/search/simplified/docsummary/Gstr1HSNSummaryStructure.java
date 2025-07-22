/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1B2BFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1HSNFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenStructureUtil;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1HSNSummaryStructure")
public class Gstr1HSNSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1HSNFinalStructure")
	private Gstr1HSNFinalStructure gstr1HSNFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1HSNResp(
			List<Gstr1SummaryScreenRespDto> hsnEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultHsnEYList = 
				getDefaultHSNEYStructure();

		List<Gstr1SummaryScreenRespDto> hsnRespbody = gstr1HSNFinalStructure
				.getHSNEyList(defaultHsnEYList, hsnEySummary);

		return hsnRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultHSNEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultHSNEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto hsnEy = new Gstr1SummaryScreenRespDto();
		hsnEy.setTaxDocType("HSN");
		hsnEy = defaultStructureUtil.gstr1DefaultStructure(hsnEy);
		
		defaultHSNEY.add(hsnEy);
		return defaultHSNEY;
	
}
}
