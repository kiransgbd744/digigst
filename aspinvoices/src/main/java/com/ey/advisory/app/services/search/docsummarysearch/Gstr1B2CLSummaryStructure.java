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
@Service("Gstr1B2CLSummaryStructure")
public class Gstr1B2CLSummaryStructure {
	
	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1B2CLFinalStructure")
	private Gstr1B2CLFinalStructure gstr1B2CLFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1B2CLResp(
			List<Gstr1SummaryScreenRespDto> b2clEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultB2CLEYList = 
				getDefaultB2CLEYStructure();

		List<Gstr1SummaryScreenRespDto> b2clRespbody = gstr1B2CLFinalStructure
				.getB2CLEyList(defaultB2CLEYList, b2clEySummary);

		return b2clRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2CLEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultB2CLEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2clEy = new Gstr1SummaryScreenRespDto();
		b2clEy.setTaxDocType("B2CL");
		b2clEy = defaultStructureUtil.gstr1DefaultStructure(b2clEy);
		
		defaultB2CLEY.add(b2clEy);
		return defaultB2CLEY;
	
}

}
