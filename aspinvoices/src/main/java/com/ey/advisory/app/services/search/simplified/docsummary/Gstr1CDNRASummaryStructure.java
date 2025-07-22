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
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenStructureUtil;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1CDNRASummaryStructure")
public class Gstr1CDNRASummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1CDNRAFinalStructure")
	private Gstr1CDNRAFinalStructure gstr1CDNRAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1CDNRAResp(
			List<Gstr1SummaryScreenRespDto> cdnrEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultCDNRAEYList = 
				getDefaultCDNRAEYStructure();

		List<Gstr1SummaryScreenRespDto> cdnrRespbody = gstr1CDNRAFinalStructure
				.getCDNRAEyList(defaultCDNRAEYList, cdnrEySummary);

		return cdnrRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultCDNRAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultCDNRAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto cdnraEy = new Gstr1SummaryScreenRespDto();
		cdnraEy.setTaxDocType("CDNRA");
		cdnraEy = defaultStructureUtil.gstr1DefaultStructure(cdnraEy);
		
		defaultCDNRAEY.add(cdnraEy);
		return defaultCDNRAEY;
	
}
	
}
