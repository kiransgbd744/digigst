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
@Service("Gstr1CDNURSummaryStructure")
public class Gstr1CDNURSummaryStructure {
	
	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1CDNURFinalStructure")
	private Gstr1CDNURFinalStructure gstr1CDNURFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1CDNURResp(
			List<Gstr1SummaryScreenRespDto> cdnurEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultCDNUREYList = 
				getDefaultCDNUREYStructure();

		List<Gstr1SummaryScreenRespDto> cdnrRespbody = gstr1CDNURFinalStructure
				.getCDNUREyList(defaultCDNUREYList, cdnurEySummary);

		return cdnrRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultCDNUREYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultCDNUREY = new ArrayList<>();

		Gstr1SummaryScreenRespDto cdnurEy = new Gstr1SummaryScreenRespDto();
		cdnurEy.setTaxDocType("CDNUR");
		cdnurEy = defaultStructureUtil.gstr1DefaultStructure(cdnurEy);
		
		defaultCDNUREY.add(cdnurEy);
		return defaultCDNUREY;
	
}

}
