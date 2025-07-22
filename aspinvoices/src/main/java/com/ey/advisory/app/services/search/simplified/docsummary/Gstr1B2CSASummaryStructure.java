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
@Service("Gstr1B2CSASummaryStructure")
public class Gstr1B2CSASummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1B2CSAFinalStructure")
	private Gstr1B2CSAFinalStructure gstr1B2csaFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1B2CSAResp(
			List<Gstr1SummaryScreenRespDto> b2csaEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultB2CSAEYList = 
				getDefaultB2CSAEYStructure();

		List<Gstr1SummaryScreenRespDto> cdnraRespbody = gstr1B2csaFinalStructure
				.getB2CSAEyList(defaultB2CSAEYList, b2csaEySummary);

		return cdnraRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2CSAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultB2CSAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2csaEy = new Gstr1SummaryScreenRespDto();
		b2csaEy.setTaxDocType("B2CSA");
		b2csaEy = defaultStructureUtil.gstr1DefaultStructure(b2csaEy);
		
		defaultB2CSAEY.add(b2csaEy);
		return defaultB2CSAEY;
	
}

	
}
