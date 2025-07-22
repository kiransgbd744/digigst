/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1NilSummaryStructure")
public class Gstr1NilSummaryStructure {

	@Autowired
	@Qualifier("Gstr1NilFinalStructure")
	private Gstr1NilFinalStructure gstr1NilFinalStructure;


	public List<Gstr1SummaryScreenNilRespDto> gstr1NilResp(
			List<Gstr1SummaryScreenNilRespDto> nilEySummary) {

		List<Gstr1SummaryScreenNilRespDto> defaultNilEYList = 
				getDefaultNilEYStructure();

		List<Gstr1SummaryScreenNilRespDto> nilRespbody = gstr1NilFinalStructure
				.getNilEyList(defaultNilEYList, nilEySummary);

		return nilRespbody;

	}
	
	private List<Gstr1SummaryScreenNilRespDto> getDefaultNilEYStructure() {

		List<Gstr1SummaryScreenNilRespDto> defaultNilEY = new ArrayList<>();

		Gstr1SummaryScreenNilRespDto nilEy = new Gstr1SummaryScreenNilRespDto();
		nilEy.setTaxDocType("NILEXTNON");
		nilEy.setAspExempted(BigDecimal.ZERO);
		nilEy.setAspNitRated(BigDecimal.ZERO);
		nilEy.setAspNonGst(BigDecimal.ZERO);
		nilEy.setGstnExempted(BigDecimal.ZERO);
		nilEy.setGstnNitRated(BigDecimal.ZERO);
		nilEy.setGstnNonGst(BigDecimal.ZERO);
		nilEy.setDiffExempted(BigDecimal.ZERO);
		nilEy.setDiffNitRated(BigDecimal.ZERO);
		nilEy.setDiffNonGst(BigDecimal.ZERO);
		
		defaultNilEY.add(nilEy);
		return defaultNilEY;
	
}
	
}
