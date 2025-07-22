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
@Service("Gstr1EXPASummaryStructure")
public class Gstr1EXPASummaryStructure {
	
	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1EXPAFinalStructure")
	private Gstr1EXPAFinalStructure gstr1EXPAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1EXPAResp(
			List<Gstr1SummaryScreenRespDto> expEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultExpaEYList = 
				getDefaultEXPAEYStructure();

		List<Gstr1SummaryScreenRespDto> expaRespbody = gstr1EXPAFinalStructure
				.getEXPAEyList(defaultExpaEYList, expEySummary);

		return expaRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultEXPAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultEXPAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto expaEy = new Gstr1SummaryScreenRespDto();
		expaEy.setTaxDocType("EXPORTS-A");
		expaEy = defaultStructureUtil.gstr1DefaultStructure(expaEy);
		
		defaultEXPAEY.add(expaEy);
		return defaultEXPAEY;
	
}

}
