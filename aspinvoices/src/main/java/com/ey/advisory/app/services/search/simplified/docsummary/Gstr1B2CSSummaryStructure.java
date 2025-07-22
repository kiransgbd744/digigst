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
@Service("Gstr1B2CSSummaryStructure")
public class Gstr1B2CSSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1B2CSFinalStructure")
	private Gstr1B2CSFinalStructure gstr1B2csFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1B2CSResp(
			List<Gstr1SummaryScreenRespDto> b2csEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultB2CSEYList = 
				getDefaultB2CSEYStructure();

		List<Gstr1SummaryScreenRespDto> cdnraRespbody = gstr1B2csFinalStructure
				.getB2CSEyList(defaultB2CSEYList, b2csEySummary);

		return cdnraRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2CSEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultB2CSEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2csEy = new Gstr1SummaryScreenRespDto();
		b2csEy.setTaxDocType("B2CS");
		b2csEy = defaultStructureUtil.gstr1DefaultStructure(b2csEy);
		
		defaultB2CSEY.add(b2csEy);
		return defaultB2CSEY;
	
}

	
}
