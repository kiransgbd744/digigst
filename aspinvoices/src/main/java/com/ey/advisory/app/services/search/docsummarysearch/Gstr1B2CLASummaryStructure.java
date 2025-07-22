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
@Service("Gstr1B2CLASummaryStructure")
public class Gstr1B2CLASummaryStructure {

	
	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1B2CLAFinalStructure")
	private Gstr1B2CLAFinalStructure gstr1B2CLAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1B2CLAResp(
			List<Gstr1SummaryScreenRespDto> b2claEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultB2CLAEYList = 
				getDefaultB2CLAEYStructure();

		List<Gstr1SummaryScreenRespDto> b2claRespbody = gstr1B2CLAFinalStructure
				.getB2CLAEyList(defaultB2CLAEYList, b2claEySummary);

		return b2claRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2CLAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultB2CLAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2claEy = new Gstr1SummaryScreenRespDto();
		b2claEy.setTaxDocType("B2CLA");
		b2claEy = defaultStructureUtil.gstr1DefaultStructure(b2claEy);
		
		defaultB2CLAEY.add(b2claEy);
		return defaultB2CLAEY;
	
}
}
