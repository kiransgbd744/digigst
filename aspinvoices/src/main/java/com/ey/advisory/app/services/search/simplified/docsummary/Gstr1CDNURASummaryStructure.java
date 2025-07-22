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
@Service("Gstr1CDNURASummaryStructure")
public class Gstr1CDNURASummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1CDNURAFinalStructure")
	private Gstr1CDNURAFinalStructure gstr1CDNURAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1CDNURAResp(
			List<Gstr1SummaryScreenRespDto> cdnuraEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultCDNURAEYList = 
				getDefaultCDNURAEYStructure();

		List<Gstr1SummaryScreenRespDto> cdnraRespbody = gstr1CDNURAFinalStructure
				.getCDNURAEyList(defaultCDNURAEYList, cdnuraEySummary);

		return cdnraRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultCDNURAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultCDNURAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto cdnuraEy = new Gstr1SummaryScreenRespDto();
		cdnuraEy.setTaxDocType("CDNURA");
		cdnuraEy = defaultStructureUtil.gstr1DefaultStructure(cdnuraEy);
		
		defaultCDNURAEY.add(cdnuraEy);
		return defaultCDNURAEY;
	
}

	
}
