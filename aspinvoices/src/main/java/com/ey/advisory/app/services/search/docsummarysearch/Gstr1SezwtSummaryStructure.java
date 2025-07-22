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
@Service("Gstr1SezwtSummaryStructure")
public class Gstr1SezwtSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1SezwtFinalStructure")
	private Gstr1SezwtFinalStructure gstr1SezwtFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1SezwtResp(
			List<Gstr1SummaryScreenRespDto> sezwtEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultSezwtEYList = 
				getDefaultSezwtEYStructure();

		List<Gstr1SummaryScreenRespDto> sezwtRespbody = gstr1SezwtFinalStructure
				.getSezwtEyList(defaultSezwtEYList, sezwtEySummary);

		return sezwtRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultSezwtEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultSezwtEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto sezwtEy = new Gstr1SummaryScreenRespDto();
		sezwtEy.setTaxDocType("SEZWOP");
		sezwtEy = defaultStructureUtil.gstr1SezDefaultStructure(sezwtEy);
		
		defaultSezwtEY.add(sezwtEy);
		return defaultSezwtEY;
	
}
}
