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
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1B2BAFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenStructureUtil;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1B2BASummaryStructure")
public class Gstr1B2BASummaryStructure {
	
	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1B2BAFinalStructure")
	private Gstr1B2BAFinalStructure gstr1B2BAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1B2BAResp(
			List<Gstr1SummaryScreenRespDto> b2baEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultB2baEYList = 
				getDefaultB2BAEYStructure();

		List<Gstr1SummaryScreenRespDto> b2baRespbody = gstr1B2BAFinalStructure
				.getB2BAEyList(defaultB2baEYList, b2baEySummary);

		return b2baRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2BAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultB2baEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2baEy = new Gstr1SummaryScreenRespDto();
		b2baEy.setTaxDocType("B2BA");
		b2baEy = defaultStructureUtil.gstr1DefaultStructure(b2baEy);
		
		defaultB2baEY.add(b2baEy);
		return defaultB2baEY;
	
}
	

}
