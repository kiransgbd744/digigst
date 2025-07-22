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
@Service("Gstr1SeztSummaryStructure")
public class Gstr1SeztSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1SeztFinalStructure")
	private Gstr1SeztFinalStructure gstr1SeztFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1SeztResp(
			List<Gstr1SummaryScreenRespDto> seztEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultSeztEYList = 
				getDefaultSeztEYStructure();

		List<Gstr1SummaryScreenRespDto> seztRespbody = gstr1SeztFinalStructure
				.getSeztEyList(defaultSeztEYList, seztEySummary);

		return seztRespbody;

	}
	private List<Gstr1SummaryScreenRespDto> getDefaultSeztEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultSeztEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto seztEy = new Gstr1SummaryScreenRespDto();
		seztEy.setTaxDocType("SEZWP");
		seztEy = defaultStructureUtil.gstr1SezDefaultStructure(seztEy);
		
		defaultSeztEY.add(seztEy);
		return defaultSeztEY;
	
}
	
}
	

