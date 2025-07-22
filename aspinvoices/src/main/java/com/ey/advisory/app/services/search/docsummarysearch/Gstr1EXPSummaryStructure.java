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
@Service("Gstr1EXPSummaryStructure")
public class Gstr1EXPSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1EXPFinalStructure")
	private Gstr1EXPFinalStructure gstr1EXPFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1EXPResp(
			List<Gstr1SummaryScreenRespDto> expEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultExpEYList = 
				getDefaultEXPEYStructure();

		List<Gstr1SummaryScreenRespDto> expRespbody = gstr1EXPFinalStructure
				.getEXPEyList(defaultExpEYList, expEySummary);

		return expRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultEXPEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultEXPEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto expEy = new Gstr1SummaryScreenRespDto();
		expEy.setTaxDocType("EXPORTS");
		expEy = defaultStructureUtil.gstr1DefaultStructure(expEy);
		
		defaultEXPEY.add(expEy);
		return defaultEXPEY;
	
}
}
