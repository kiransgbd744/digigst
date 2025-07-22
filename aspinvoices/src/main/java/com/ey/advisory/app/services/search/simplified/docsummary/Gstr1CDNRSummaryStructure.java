package com.ey.advisory.app.services.search.simplified.docsummary;

/**
 * 
 */


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenStructureUtil;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1CDNRSummaryStructure")
public class Gstr1CDNRSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1CDNRFinalStructure")
	private Gstr1CDNRFinalStructure gstr1CDNRFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1CDNRResp(
			List<Gstr1SummaryScreenRespDto> expEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultCDNREYList = 
				getDefaultCDNREYStructure();

		List<Gstr1SummaryScreenRespDto> cdnrRespbody = gstr1CDNRFinalStructure
				.getCDNREyList(defaultCDNREYList, expEySummary);

		return cdnrRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultCDNREYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultCDNREY = new ArrayList<>();

		Gstr1SummaryScreenRespDto cdnrEy = new Gstr1SummaryScreenRespDto();
		cdnrEy.setTaxDocType("CDNR");
		cdnrEy = defaultStructureUtil.gstr1DefaultStructure(cdnrEy);
		
		defaultCDNREY.add(cdnrEy);
		return defaultCDNREY;
	
}
}
