/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1B2BSummaryStructure")
public class Gstr1B2BSummaryStructure {
	
	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1B2BFinalStructure")
	private Gstr1B2BFinalStructure gstr1B2BFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1B2BResp(
			List<Gstr1SummaryScreenRespDto> b2bEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultB2bEYList = 
				getDefaultB2BEYStructure();

		List<Gstr1SummaryScreenRespDto> b2cRespbody = gstr1B2BFinalStructure
				.getB2BEyList(defaultB2bEYList, b2bEySummary);

		return b2cRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2BEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultB2CEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2bEy = new Gstr1SummaryScreenRespDto();
		b2bEy.setTaxDocType("B2B");
		b2bEy = defaultStructureUtil.gstr1DefaultStructure(b2bEy);
		
		defaultB2CEY.add(b2bEy);
		return defaultB2CEY;
	
}
}
